package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.NewClassBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import java.util.ArrayList;


public final class MortarProjectile extends GunProjectile {
    public static final float SPEED = 1100f, LIFETIME = 3f, RADIUS = 160f;
    public static final int MAX_SPLASH_VICTIMS = 8;
    private boolean pendingImpact;
    private Unit directVictim;
    private float blastX, blastY;

    public MortarProjectile(Hero hero, Gun gun) {
        super(hero, gun);
        speedX = facingRight ? SPEED : -SPEED;
        gs = new GameSprite(NewClassAssets.ItemArt.BULLET.key(), 96, 96);
    }
    @Override protected float lifetime() { return LIFETIME; }
    @Override protected void onImpact(Unit target) { queueImpact(target); }
    @Override public void onTerrainCollision() {
        if (isUsed()) return;
        PhysicsHelper.getInstance().syncProjectileFromPhysics(this);
        if (validOwner() && inRange() && withinLifetime()) queueImpact(null);
        else markUsed();
    }
    private void queueImpact(Unit target) {
        if (isUsed()) return;
        markUsed();
        blastX = impactX(); blastY = impactY();
        directVictim = target;
        pendingImpact = true;
    }
    @Override public void act(float delta) {
        drainImpact();
        super.act(delta);

        drainImpact();
    }
    private void drainImpact() {
        if (pendingImpact) {
            pendingImpact = false;
            if (validOwner()) detonate();
            directVictim = null;
        }
    }
    private void detonate() {
        ArrayList<Mob> victims = new ArrayList<>(MAX_SPLASH_VICTIMS);

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof Mob) || unit == directVictim || !eligible(unit)) continue;
            Mob mob = (Mob)unit;
            if (mob.getPersistentId() == null || mob.getPersistentId().isEmpty()
                    || directVictim instanceof Mob && mob.getPersistentId().equals(((Mob)directVictim).getPersistentId())) continue;
            boolean duplicate = false;
            for (Mob chosen : victims) if (chosen == mob || chosen.getPersistentId().equals(mob.getPersistentId())) duplicate = true;
            if (duplicate) continue;
            int index = 0;
            while (index < victims.size() && compare(victims.get(index), mob) <= 0) index++;
            if (index >= MAX_SPLASH_VICTIMS) continue;
            victims.add(index, mob);
            if (victims.size() > MAX_SPLASH_VICTIMS) victims.remove(MAX_SPLASH_VICTIMS);
        }
        playSound(Sounds.EXPLOSION, .55f);
        EffectsHelper.getInstance().add(NewClassBurst.gunImpact((Hero)owner, blastX, blastY));
        if (validTarget(directVictim) && UnitHelper.getInstance().getUnits().contains(directVictim)
                && PhysicsHelper.getInstance().hasSolidLineOfSight(MapHelper.getInstance().getActiveRoom(), blastX, blastY,
                directVictim.x + ConstantsHelper.UNIT_DIMENSIONS / 2f, directVictim.y + ConstantsHelper.UNIT_DIMENSIONS / 2f)) {
            boolean hit = attackTarget(directVictim, 1f, false);
            playSound(hit ? Sounds.HIT : Sounds.MISS, .4f);
        }
        for (Mob victim : victims) {
            if (!validOwner()) break;
            if (UnitHelper.getInstance().getUnits().contains(victim) && eligible(victim)) attackTarget(victim, .5f, true);
        }
    }
    private boolean validTarget(Unit unit) {
        return unit != null && !unit.isDead() && unit.getHP() > 0 && !unit.showOnly()
                && unit.isFriendly != isFriendly && room.equals(unit.getRoom());
    }
    private boolean eligible(Unit unit) {
        return validTarget(unit) && Float.isFinite(unit.x) && Float.isFinite(unit.y)
                && distance(unit) <= RADIUS * RADIUS
                && PhysicsHelper.getInstance().hasSolidLineOfSight(MapHelper.getInstance().getActiveRoom(),
                blastX, blastY, unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f, unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f);
    }
    private float distance(Unit unit) {
        float dx = unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f - blastX;
        float dy = unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f - blastY;
        return dx * dx + dy * dy;
    }
    private int compare(Mob a, Mob b) {
        int distance = Float.compare(distance(a), distance(b));
        return distance != 0 ? distance : a.getPersistentId().compareTo(b.getPersistentId());
    }
}
