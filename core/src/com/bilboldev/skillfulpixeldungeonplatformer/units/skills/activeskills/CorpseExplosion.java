package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseTargeting;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.NewClassBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import java.util.ArrayList;
import java.util.List;


public final class CorpseExplosion extends CorpseActiveSkill {
    public static final float RADIUS = 192f;
    public static final int MAX_VICTIMS = 8;

    public CorpseExplosion() {
        super(Skills.CORPSE_EXPLOSION, 4, "Corpse Explosion", "Corpse Explosion",
                "Detonate a nearby corpse to damage surrounding enemies.",
                NewClassAssets.SkillArt.CORPSE_EXPLOSION.key(), 10, 8f, CorpseTargeting.Purpose.CONSUME);
    }

    @Override protected int occupiedMinionSlots(Hero hero) { return 0; }

    @Override protected CorpseTargeting.PreparedAction stage(final Hero hero, final CorpseTargeting.Target corpse) {
        final float originX = corpse.x, originY = corpse.y + 28f;
        final int placement = hero.getPresentationPlacementVersion();
        final List<Mob> victims = new ArrayList<>(MAX_VICTIMS);

        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if (!(unit instanceof Mob)) continue;
            Mob mob = (Mob)unit;
            if (!eligible(hero, mob, corpse.roomId, originX, originY)) continue;
            boolean duplicate = false;
            for (Mob chosen : victims) if (chosen == mob || chosen.getPersistentId().equals(mob.getPersistentId())) duplicate = true;
            if (duplicate) continue;
            int index = 0;
            while (index < victims.size() && compare(victims.get(index), mob, originX, originY) <= 0) index++;
            if (index >= MAX_VICTIMS) continue;
            victims.add(index, mob);
            if (victims.size() > MAX_VICTIMS) victims.remove(MAX_VICTIMS);
        }
        final float damage = 18f + 3f * (hero.getLevel() - 1);
        return new CorpseTargeting.PreparedAction() {
            @Override public boolean ready() {
                return hero == UnitHelper.getInstance().getHero() && !hero.isDead() && hero.getHP() > 0
                        && hero.getPresentationPlacementVersion() == placement && corpse.roomId.equals(hero.getRoom())
                        && corpse.roomId.equals(MapHelper.getInstance().getActiveRoomIdentifier());
            }
            @Override public void commit(CorpseTargeting.Target selected) {
                SoundHelper.GetSingleton().play(Sounds.EXPLOSION, 0f, .65f);
                EffectsHelper.getInstance().add(new NewClassBurst(hero, originX, originY));
                for (Mob victim : victims) {
                    if (!ready()) break;

                    if (UnitHelper.getInstance().getUnits().contains(victim)
                            && eligible(hero, victim, corpse.roomId, originX, originY))
                        UnitHelper.getInstance().attackTarget(hero, victim, null, damage, true);
                }
            }
        };
    }

    private static boolean eligible(Hero hero, Mob mob, String room, float x, float y) {
        return !mob.isDead() && mob.getHP() > 0 && !mob.showOnly() && mob.isVisible() && !mob.isInvisible()
                && mob.isFriendly != hero.isFriendly && room.equals(mob.getRoom())
                && mob.getPersistentId() != null && !mob.getPersistentId().isEmpty()
                && Float.isFinite(mob.x) && Float.isFinite(mob.y) && distanceSquared(mob, x, y) <= RADIUS * RADIUS
                && PhysicsHelper.getInstance().hasSolidLineOfSight(MapHelper.getInstance().getActiveRoom(), x, y, mob.x + 48f, mob.y + 48f);
    }
    private static float distanceSquared(Mob mob, float x, float y) {
        float dx = mob.x + 48f - x, dy = mob.y + 48f - y;
        return dx * dx + dy * dy;
    }
    private static int compare(Mob a, Mob b, float x, float y) {
        int distance = Float.compare(distanceSquared(a, x, y), distanceSquared(b, x, y));
        return distance != 0 ? distance : a.getPersistentId().compareTo(b.getPersistentId());
    }
}
