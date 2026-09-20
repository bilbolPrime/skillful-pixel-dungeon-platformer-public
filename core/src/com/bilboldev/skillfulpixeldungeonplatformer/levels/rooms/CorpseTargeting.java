package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Blind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.TemporaryBlind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.*;
import java.util.function.IntSupplier;


public final class CorpseTargeting {
    public static final float RANGE = 512f;

    public static final float FEET_OFFSET = 4.48f;

    public enum Purpose {
        CONSUME(0f), SKELETON(86.4f), SKELETON_ARCHER(86.4f), RECOVERY(0f);
        final float width;
        Purpose(float width) { this.width = width; }
        public boolean raisesMinion() { return width > 0f; }
    }


    public static final class Target {
        public final String victimId, roomId;
        public final float x, y, actionX, actionY;
        public final Purpose purpose;
        private final long order;
        private final long restoreVersion;
        private final int placementVersion;
        private final Hero owner;
        private final Room room;
        private Target(Hero owner, Room room, CorpseRecord corpse, Purpose purpose, float actionX, float actionY) {
            this.owner = owner; this.room = room; this.purpose = purpose;
            restoreVersion = room.getCorpseRestoreVersion();
            placementVersion = owner.getPresentationPlacementVersion();
            victimId = corpse.victimId; roomId = corpse.roomId; order = corpse.order;
            x = corpse.x; y = corpse.y; this.actionX = actionX; this.actionY = actionY;
        }
        private boolean same(Target other) {
            return other != null && owner == other.owner && room == other.room && purpose == other.purpose
                    && placementVersion == other.placementVersion
                    && restoreVersion == other.restoreVersion
                    && victimId.equals(other.victimId) && order == other.order && x == other.x && y == other.y
                    && actionX == other.actionX && actionY == other.actionY;
        }
    }


    public interface PreparedAction {
        boolean ready();

        void commit(Target target);
    }

    private CorpseTargeting() { }


    public static Target select(Hero hero, Purpose purpose, int occupiedSlots) {
        if (hero == null || purpose == null || hero != UnitHelper.getInstance().getHero()
                || hero.getHeroClass() != HeroClass.NECROMANCER || hero.isDead() || hero.showOnly()
                || (purpose != Purpose.RECOVERY && hero.getHP() <= 0) || hero.getRoom() == null
                || hero.getBuff(Blind.class) != null || hero.getBuff(TemporaryBlind.class) != null
                || !Float.isFinite(hero.x) || !Float.isFinite(hero.y)) return null;
        Room room = MapHelper.getInstance().getActiveRoom();
        if (room == null || !hero.getRoom().equals(room.getIdentifier())) return null;
        if (purpose.raisesMinion() && (occupiedSlots < 0 || occupiedSlots >= (hero.hasSkill(Skills.LICH) ? 3 : 2))) return null;
        if (purpose == Purpose.RECOVERY && !safeRecovery(hero, room)) return null;
        float centerX = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        Target best = null;
        float bestDistance = Float.POSITIVE_INFINITY;
        boolean bestFacing = false;
        for (CorpseRecord corpse : room.getCorpses()) {
            if (!room.getIdentifier().equals(corpse.roomId)) continue;
            float dx = corpse.x - centerX, dy = corpse.y + FEET_OFFSET - hero.y;
            float distance = dx * dx + dy * dy;
            if (!Float.isFinite(distance) || distance > RANGE * RANGE) continue;
            if (room.corpseSupportBelow(corpse.x, corpse.y) != corpse.y
                    || !PhysicsHelper.getInstance().hasCorpseLineOfSight(room, centerX,
                        hero.y + ConstantsHelper.UNIT_DIMENSIONS * .75f, corpse.x, corpse.y + 28f)) continue;
            float actionX = purpose == Purpose.RECOVERY ? hero.x : corpse.x - ConstantsHelper.UNIT_DIMENSIONS / 2f;
            float actionY = purpose == Purpose.RECOVERY ? hero.y : corpse.y + FEET_OFFSET;
            if (purpose.raisesMinion() && (room.corpseSupportBelow(corpse.x, corpse.y, purpose.width / 2f) != corpse.y
                    || !freeFootprint(hero, room, corpse.x, actionY, purpose.width, false))) continue;
            boolean facing = dx == 0f || (dx > 0f) == hero.facingRight;
            int compare = Float.compare(distance, bestDistance);
            if (best == null || compare < 0 || compare == 0 && (facing && !bestFacing
                    || facing == bestFacing && corpse.victimId.compareTo(best.victimId) < 0)) {
                best = new Target(hero, room, corpse, purpose, actionX, actionY);
                bestDistance = distance; bestFacing = facing;
            }
        }
        return best;
    }

    private static boolean safeRecovery(Hero hero, Room room) {
        float center = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float support = room.corpseSupportBelow(center, hero.y, hero.getCollisionWidth() / 2f);
        return Float.isFinite(support) && Math.abs(hero.y - support - FEET_OFFSET) <= 1f
                && Math.abs(hero.speedY) <= 50f
                && freeFootprint(hero, room, center, hero.y, hero.getCollisionWidth(), true);
    }


    static boolean freeFootprint(Hero hero, Room room, float centerX, float feetY, float width, boolean recovery) {
        Rectangle area = new Rectangle(centerX - width / 2f, feetY, width, ConstantsHelper.UNIT_DIMENSIONS);
        if (area.y + area.height > room.getHeight() * ConstantsHelper.TILE
                || !PhysicsHelper.getInstance().isCorpseActionSpaceClear(room, area)) return false;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit.isDead() || !room.getIdentifier().equals(unit.getRoom())) continue;
            if ((unit instanceof Hero || unit instanceof Mob) && !unit.showOnly()
                    && !(recovery && unit == hero) && area.overlaps(unit.getHitBox())) return false;

            Rectangle danger = null;
            if (unit instanceof PlatformTrap && !((PlatformTrap)unit).isTriggered()) danger = ((PlatformTrap)unit).getTriggerArea();
            else if (unit instanceof ToxicGasCloud) danger = ((ToxicGasCloud)unit).getCloudArea();
            else if (unit instanceof PoisonCloud) danger = ((PoisonCloud)unit).getCloudArea();
            else if (unit instanceof ParalyticGasCloud) danger = ((ParalyticGasCloud)unit).getCloudArea();
            else if (unit instanceof FreezingCloud) danger = ((FreezingCloud)unit).getCloudArea();
            else if (unit instanceof SpikeTrap && !((SpikeTrap)unit).isSpent()) danger = unit.getHitBox();
            if (danger != null && area.overlaps(danger)) return false;
        }
        return true;
    }


    public static boolean consume(Hero hero, Target expected, IntSupplier occupiedSlots, PreparedAction action) {
        if (expected == null || expected.owner != hero || occupiedSlots == null || action == null
                || !expected.same(select(hero, expected.purpose, occupiedSlots.getAsInt())) || !action.ready()
                || !expected.same(select(hero, expected.purpose, occupiedSlots.getAsInt()))) return false;
        if (!expected.room.claimCorpse(expected.victimId, expected.order)) return false;
        action.commit(expected);
        return true;
    }
}
