package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import java.util.ArrayList;


public class GunProjectile extends ThrownProjectile {
    public static final int MAX_LIVE = 24;
    public static final float SPEED = 1640f, RANGE = 2048f, LIFETIME = 2f;
    private final Hero ownerHero;
    private final int placementVersion;
    private final float startX, startY;
    private float lastClearX, lastClearY;
    private float elapsed;
    private ShotDamage shotDamage;
    private int shareIndex, shares = 1;


    private static final class ShotDamage {
        final float rolled;
        float modified;
        boolean resolved;
        final ArrayList<Unit> successfulVictims = new ArrayList<>(9);
        ArrayList<Unit> reportedVictims;
        ShotDamage(float rolled) { this.rolled = rolled; }
    }

    public GunProjectile(Hero hero, Gun gun) {
        ownerHero = hero;
        placementVersion = hero.getPresentationPlacementVersion();
        setOwner(hero); setAttackingItem(gun); setRoom(hero.getRoom());

        accuracyMultiplier = Gun.supports(hero) && hero.hasSkill(Skills.STEADY_AIM) ? 1.2f : 1f;
        isFriendly = hero.isFriendly; facingRight = hero.facingRight;

        x = startX = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f - getPhysicsCollisionSize() / 2f;
        y = startY = hero.y + ConstantsHelper.UNIT_DIMENSIONS / 3f - getPhysicsCollisionSize() / 2f;
        lastClearX = x; lastClearY = y;
        speedX = facingRight ? SPEED : -SPEED; speedY = 0f;
        alignRotationToVelocity = false; rotationOffset = 0f;
        gs = new GameSprite(NewClassAssets.ItemArt.BULLET.key(), 64, 64);
    }

    @Override public void setDamage(float damage) {
        super.setDamage(damage);
        shotDamage = new ShotDamage(damage);
    }

    @Override public float getAccuracyMultiplier() { return accuracyMultiplier; }

    public final void shareDamage(GunProjectile first, int index, int count) {
        shotDamage = first.shotDamage;
        shareIndex = index; shares = count;
        damage = divideDamage(shotDamage.rolled);
    }
    private float divideDamage(float total) {
        float part = total / shares;
        return shareIndex == shares - 1 ? total - part * (shares - 1) : part;
    }

    public final float resolveHitDamage(Unit target, float scale) {
        if (shotDamage == null) shotDamage = new ShotDamage(damage);
        if (!shotDamage.resolved) {
            shotDamage.resolved = true;
            shotDamage.modified = shotDamage.rolled;
            if (attackingItem.getPrefix() != null)
                shotDamage.modified = attackingItem.getPrefix().modifyAttackDamage(ownerHero, target, attackingItem, shotDamage.rolled);
        }
        return divideDamage(shotDamage.modified) * scale;
    }
    public final boolean claimHitEffects(Unit target) {
        if (shotDamage.successfulVictims.contains(target)) return false;
        shotDamage.successfulVictims.add(target);
        return true;
    }
    public final boolean attackTarget(Unit target, float scale, boolean splash) {
        return UnitHelper.getInstance().attackGunTarget(ownerHero, target, attackingItem, this, scale, splash, accuracyMultiplier);
    }


    public static int liveCount() {
        int count = 0;
        for (Unit unit : UnitHelper.getInstance().getUnits()) if (unit instanceof GunProjectile) count++;
        return count;
    }
    public static void clearFor(Hero hero) {
        for (int i = UnitHelper.getInstance().getUnits().size() - 1; i >= 0; i--) {
            Unit unit = UnitHelper.getInstance().getUnits().get(i);
            if (unit instanceof GunProjectile && ((GunProjectile)unit).ownerHero == hero) {
                ((GunProjectile)unit).markUsed();
                UnitHelper.getInstance().removeUnit(unit);
            }
        }
    }
    protected final boolean validOwner() {
        return ownerHero == UnitHelper.getInstance().getHero() && Gun.supports(ownerHero)
                && !ownerHero.isDead() && ownerHero.getHP() > 0
                && placementVersion == ownerHero.getPresentationPlacementVersion() && room != null
                && room.equals(ownerHero.getRoom()) && room.equals(MapHelper.getInstance().getActiveRoomIdentifier());
    }
    protected final boolean inRange() {
        float dx = x - startX, dy = y - startY;
        return Float.isFinite(dx) && Float.isFinite(dy) && dx * dx + dy * dy <= RANGE * RANGE;
    }
    protected final boolean clearPath() {
        float half = getPhysicsCollisionSize() / 2f;
        return PhysicsHelper.getInstance().hasSolidLineOfSight(MapHelper.getInstance().getActiveRoom(),
                startX + half, startY + half, x + half, y + half);
    }
    @Override public float getPhysicsCollisionSize() { return 12f; }
    @Override public float getPhysicsGravityScale() { return 0f; }
    @Override public float getPhysicsLinearDamping() { return 0f; }
    @Override public boolean hitsBothSidesOfPlatforms() { return true; }
    protected float lifetime() { return LIFETIME; }
    protected final boolean withinLifetime() { return elapsed < lifetime(); }
    protected final float impactX() { return (clearPath() ? x : lastClearX) + getPhysicsCollisionSize() / 2f; }
    protected final float impactY() { return (clearPath() ? y : lastClearY) + getPhysicsCollisionSize() / 2f; }

    @Override public void afterPhysicsStep() {

        if (isUsed()) return;
        if (!validOwner() || !inRange() || !withinLifetime()) markUsed();
        else if (!clearPath()) onTerrainCollision();
        else { lastClearX = x; lastClearY = y; }
    }

    @Override public void onUnitCollision(Unit target) {
        if (isUsed() || !validOwner() || target == null || target.isDead() || target.showOnly()
                || target.isFriendly == isFriendly || !room.equals(target.getRoom())) return;
        PhysicsHelper.getInstance().syncProjectileFromPhysics(this);
        if (!inRange() || !withinLifetime()) { markUsed(); return; }
        if (!clearPath()) { onTerrainCollision(); return; }
        onImpact(target);
    }
    protected void onImpact(Unit target) {
        markUsed();
        boolean hit = attackTarget(target, 1f, false);
        boolean audible = true;
        if (shares > 1) {
            if (shotDamage.reportedVictims == null) shotDamage.reportedVictims = new ArrayList<>(shares);
            audible = !shotDamage.reportedVictims.contains(target);
            if (audible) shotDamage.reportedVictims.add(target);
        }
        calculateSoundLevel();
        SoundHelper.GetSingleton().playGunContact(hit ? Sounds.HIT : Sounds.MISS, Math.min(1f, soundLevel * .4f), audible);
    }
    @Override public void act(float delta) {
        if (!validOwner()) markUsed();
        super.act(delta);
        if (isUsed()) return;
        elapsed += PhysicsHelper.boundGameDelta(delta);
        afterPhysicsStep();
        if (isUsed()) super.act(0f);
    }
    @Override public void draw(Batch batch, float alpha) {
        if (isUsed() || !validOwner()) return;
        float half = getPhysicsCollisionSize() / 2f;
        gs.setPosition(getRenderX() + half - gs.getWidth() / 2f, getRenderY() + half - gs.getHeight() / 2f);
        gs.setAlpha(alpha); gs.setRotation(facingRight ? 0f : 180f);
        gs.draw(batch);
    }
}
