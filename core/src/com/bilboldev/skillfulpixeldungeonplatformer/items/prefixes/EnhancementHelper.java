package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.LightningSpread;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.MirrorImage;

import java.util.ArrayList;

public final class EnhancementHelper {
    private EnhancementHelper() {
    }

    public static int getUpgradeLevel(Item item) {
        return item == null ? 0 : Math.max(0, item.getLevel() - 1);
    }

    public static int procChance(Item item, int baseChance, int chancePerUpgrade, int maxChance) {
        return Math.min(maxChance, baseChance + getUpgradeLevel(item) * chancePerUpgrade);
    }

    public static boolean rollProc(Item item, int baseChance, int chancePerUpgrade, int maxChance) {
        return RandomHelper.getInstance().randomChance(procChance(item, baseChance, chancePerUpgrade, maxChance));
    }

    public static int randomInclusive(int minValue, int maxValue) {
        if (maxValue <= minValue) {
            return minValue;
        }

        return minValue + RandomHelper.getInstance().randomInt(maxValue - minValue + 1);
    }

    public static void applyOrRefresh(Unit target, Buff template, float durationSeconds) {
        applyOrRefresh(target, template, durationSeconds, false);
    }

    public static void applyOrRefresh(Unit target, Buff template, float durationSeconds, boolean permanent) {
        if (target == null || template == null) {
            return;
        }

        Buff existing = target.getBuff(template.getClass());
        if (existing != null) {
            existing.setPermanent(permanent).setDuration(Math.max(existing.getRemainingDuration(), durationSeconds));
            return;
        }

        template.setPermanent(permanent).setDuration(durationSeconds).setOwner(target);
    }

    public static void emitLightning(Unit unit) {
        if (unit == null) {
            return;
        }

        EffectsHelper.getInstance().add(new LightningSpread().init(
                unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                0f,
                0f,
                0f,
                0f));
    }

    public static Unit findNearestHostile(Unit owner, Unit center, ArrayList<Unit> excluded, float rangeTiles) {
        if (owner == null || center == null || center.getRoom() == null) {
            return null;
        }

        Unit nearest = null;
        float bestDistanceSq = Float.MAX_VALUE;
        float maxDistance = rangeTiles * ConstantsHelper.TILE;
        float maxDistanceSq = maxDistance * maxDistance;

        for (Unit candidate : UnitHelper.getInstance().getUnits()) {
            if (candidate == null || candidate == center || candidate == owner || candidate.showOnly() || candidate.isDead()) {
                continue;
            }

            if (excluded != null && excluded.contains(candidate)) {
                continue;
            }

            if (candidate.getRoom() == null || !candidate.getRoom().equals(center.getRoom())) {
                continue;
            }

            if (candidate.isFriendly == owner.isFriendly) {
                continue;
            }

            float dx = candidate.x - center.x;
            float dy = candidate.y - center.y;
            float distanceSq = dx * dx + dy * dy;
            if (distanceSq > maxDistanceSq || distanceSq >= bestDistanceSq) {
                continue;
            }

            bestDistanceSq = distanceSq;
            nearest = candidate;
        }

        return nearest;
    }

    public static boolean isAdjacent(Unit first, Unit second) {
        if (first == null || second == null || first.getRoom() == null || !first.getRoom().equals(second.getRoom())) {
            return false;
        }

        return Math.abs(first.x - second.x) <= ConstantsHelper.TILE * 1.25f
                && Math.abs(first.y - second.y) <= ConstantsHelper.TILE * 0.75f;
    }

    public static boolean pushUnitHorizontally(Unit target, float direction) {
        if (target == null || target.getRoom() == null || direction == 0f) {
            return false;
        }

        float candidateX = target.x + Math.signum(direction) * ConstantsHelper.TILE;
        if (!UnitHelper.getInstance().freeSpace(target, Math.round(candidateX), Math.round(target.y), target.getRoom())) {
            return false;
        }

        target.x = candidateX;
        target.floorY = target.y;
        target.speedY = 0f;
        target.momentX = 0f;
        PhysicsHelper.getInstance().syncBodyToUnit(target);
        return true;
    }

    public static boolean teleportUnitToRandomPlatform(Unit target, float minDistanceTiles, float maxDistanceTiles) {
        if (target == null || target.getRoom() == null) {
            return false;
        }

        Room currentRoom = MapHelper.getInstance().getRoom(target.getRoom());
        if (currentRoom == null) {
            return false;
        }

        ArrayList<String> candidates = new ArrayList<String>();
        for (String platform : currentRoom.getPlatforms()) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float candidateX = tileX * ConstantsHelper.TILE;
            float candidateY = (tileY + 1) * ConstantsHelper.TILE;
            float distance = Math.abs(candidateX - target.x);

            if (distance < minDistanceTiles * ConstantsHelper.TILE || distance > maxDistanceTiles * ConstantsHelper.TILE) {
                continue;
            }

            if (!UnitHelper.getInstance().freeSpace(target, Math.round(candidateX), Math.round(candidateY), target.getRoom())) {
                continue;
            }

            candidates.add(platform);
        }

        if (candidates.isEmpty()) {
            return false;
        }

        String platform = candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int tileY = Integer.parseInt(platform.split("_")[1]);
        target.x = tileX * ConstantsHelper.TILE;
        target.y = (tileY + 1) * ConstantsHelper.TILE;
        target.floorY = target.y;
        target.speedY = 0f;
        target.momentX = 0f;
        PhysicsHelper.getInstance().syncBodyToUnit(target);
        return true;
    }

    public static boolean spawnMirrorImage(Unit defender) {
        if (!(defender instanceof Hero)) {
            return false;
        }

        Hero hero = (Hero) defender;
        float[] offsets = new float[]{-ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS};
        for (float offset : offsets) {
            float spawnX = hero.x + offset;
            if (!UnitHelper.getInstance().freeSpace(Math.round(spawnX), Math.round(hero.y), hero.getRoom())) {
                continue;
            }

            MirrorImage image = new MirrorImage().initFromHero(hero);
            image.isSummoned = true;
            image.x = spawnX;
            image.y = hero.y;
            image.floorY = hero.floorY;
            image.facingRight = offset < 0f ? !hero.facingRight : hero.facingRight;
            image.setRoom(hero.getRoom());
            image.makeFriendly();
            UnitHelper.getInstance().addUnit(image);
            return true;
        }

        return false;
    }
}