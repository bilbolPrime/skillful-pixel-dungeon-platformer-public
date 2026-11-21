package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpriteTrail;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

import java.util.ArrayList;

public class ThrownProjectile extends Unit {

    protected Unit owner;
    protected Weapon attackingItem;
    protected GameSprite gs;
    protected float rotation;
    protected boolean used;
    protected boolean removed;
    protected float damage = 1;
    protected boolean magicAttack;
    protected boolean alignRotationToVelocity = true;
    protected float rotationOffset = -45f;
    protected float spinSpeed;
    protected float accuracyMultiplier = 1f;
    protected boolean kneeShot;
    protected boolean bombvoyageSplash;
    protected boolean piercesTargets;
    protected ArrayList<Unit> hitTargets = new ArrayList<>();

    {
        gs = new GameSprite("images/weapons/projectile.png", 45, 45);
        showOnly = true;
        speedY = 20;
    }

    public ThrownProjectile setOwner(Unit unit){
        this.owner = unit;
        return this;
    }

    public ThrownProjectile setSpeedY(float speedY){
        this.speedY = speedY;
        return this;
    }

    public ThrownProjectile setGameSprite(GameSprite gameSprite){
        this.gs = gameSprite;
        return this;
    }

    public ThrownProjectile setAlignRotationToVelocity(boolean alignRotationToVelocity) {
        this.alignRotationToVelocity = alignRotationToVelocity;
        return this;
    }

    public ThrownProjectile setRotationOffset(float rotationOffset) {
        this.rotationOffset = rotationOffset;
        return this;
    }

    public ThrownProjectile setSpinSpeed(float spinSpeed) {
        this.spinSpeed = spinSpeed;
        return this;
    }

    public ThrownProjectile setAccuracyMultiplier(float accuracyMultiplier) {
        this.accuracyMultiplier = Math.max(0.1f, accuracyMultiplier);
        return this;
    }

    public ThrownProjectile setKneeShot(boolean kneeShot) {
        this.kneeShot = kneeShot;
        return this;
    }

    public ThrownProjectile setBombvoyageSplash(boolean bombvoyageSplash) {
        this.bombvoyageSplash = bombvoyageSplash;
        return this;
    }

    public ThrownProjectile setPiercesTargets(boolean piercesTargets) {
        this.piercesTargets = piercesTargets;
        return this;
    }

    public ThrownProjectile setAttackingItem(Weapon attackingItem) {
        this.attackingItem = attackingItem;
        return this;
    }

    private transient float partyPhysicsRemainder;
    public float partyDamage() { return damage; }
    public Weapon partyWeapon() { return attackingItem; }
    public com.bilboldev.skillfulpixeldungeonplatformer.items.Item partyPayload() { return null; }
    public void partyPayload(com.bilboldev.skillfulpixeldungeonplatformer.items.Item item) { }
    public String[] partyHitTargets() {
        String[] ids = new String[hitTargets.size()];
        for (int i = 0; i < ids.length; i++) ids[i] = hitTargets.get(i).getPersistentId();
        return ids;
    }
    public void partyHitTargets(String[] ids, java.util.Map<String, Unit> units) {
        if (ids == null || ids.length > 512) throw new IllegalArgumentException("hit targets");
        hitTargets.clear();
        for (String id : ids) if (units.containsKey(id)) hitTargets.add(units.get(id));
    }
    public void partySeek(float x, float y, float vx, float vy, float elapsed, float gravity, float damping) {
        this.x = x; this.y = y; speedX = vx; speedY = vy; partyPhysicsRemainder = 0;
        float remaining = Math.min(60, elapsed);
        while (remaining > 0) { float step = Math.min(1f / 60f, remaining); partyVisualStep(step, gravity, damping); remaining -= step; }
    }

    public void partyVisualStep(float delta, float gravity, float damping) {
        partyPhysicsRemainder += PhysicsHelper.boundGameDelta(delta);
        final float step = 1f / 60f;
        while (partyPhysicsRemainder >= step) {
            speedY -= ConstantsHelper.GRAVITY * gravity * step;
            float drag = Math.max(0, 1 - step * damping);
            speedX *= drag; speedY *= drag;
            x += speedX * step; y += speedY * step;
            partyPhysicsRemainder -= step;
        }
        if (alignRotationToVelocity && (speedX != 0 || speedY != 0)) rotation = calculateAngleFromSpeed(speedX, speedY);
        else if (spinSpeed != 0) rotation += (speedX >= 0 ? -spinSpeed : spinSpeed) * delta;
    }

    public Unit getOwner() {
        return owner;
    }

    public boolean isUsed() {
        return used;
    }

    public void markUsed() {
        used = true;
    }

    public boolean causesKneeShot() {
        return kneeShot;
    }

    public boolean causesBombvoyageSplash() {
        return bombvoyageSplash;
    }

    public boolean piercesTargets() {
        return piercesTargets;
    }

    public float getPhysicsCollisionSize() {
        return ConstantsHelper.UNIT_DIMENSIONS / 4f;
    }

    public float getPhysicsGravityScale() {
        return 0.25f;
    }

    public float getPhysicsLinearDamping() {
        return 0.8f;
    }

    public boolean collidesWithTerrain() {
        return true;
    }

    public boolean hitsBothSidesOfPlatforms() { return false; }

    public void onTerrainCollision() {
        markUsed();
    }

    public void afterPhysicsStep() { }

    public void onUnitCollision(Unit target) {
        if (hasHitTarget(target)) {
            return;
        }

        boolean hit = owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack, accuracyMultiplier);
        if (hit) {
            playSound(Sounds.HIT, 0.4f);
            recordHitTarget(target);
            if (owner instanceof Hero && ((Hero) owner).handleBowProjectileImpact(target, attackingItem, damage, true, this)) {
                return;
            }
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
        markUsed();
    }

    @Override
    public void act(float delta){

        if(used) {
            if (!removed) {
                removed = true;
                UnitHelper.getInstance().removeUnit(this);
            }
            return;
        }

        if(room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            markUsed();
            return;
        }

        PhysicsHelper.getInstance().syncProjectileFromPhysics(this);

        if (alignRotationToVelocity) {
            rotation = calculateAngleFromSpeed(speedX, speedY);

            if(rotation < 0){
                if(Math.abs(rotation) < 45)
                {
                    rotation = rotation + (speedX > 0 ? - 75f*delta : 75f * delta);
                }
            }
            else if(Math.abs(rotation) < 45)
            {
                rotation = rotation + (speedX > 0 ? - 25f*delta : 25f * delta);
            }
        } else if (spinSpeed != 0f) {
            rotation += (speedX >= 0 ? -spinSpeed : spinSpeed) * delta;
        }
    }

    @Override
    public void draw(Batch batch, float alpha){
        if(gs != null && !used){
            gs.setRotation(rotation + rotationOffset + (facingRight ? 0f : 180f));
            gs.setPosition(getRenderX(), getRenderY());
            SpriteTrail.draw(batch, gs, speedX * 0.025f, speedY * 0.025f, 24f);
            gs.draw(batch);
        }
    }

    float calculateAngleFromSpeed(float speedX, float speedY){
        float angle = (float)  Math.toDegrees(Math.asin(speedY / (Math.sqrt(Math.pow(speedX, 2) + Math.pow(speedY, 2)))));
        if(speedX < 0){
            return - angle;
        }

        return angle;
    }

    void checkGround(float oldX, float oldY){
        float floorY = MapHelper.getInstance().calculateFloorY(oldX, oldY);
        if(this.y < floorY && speedY < 0)
        {
            used = true;
        }
    }

    public Rectangle getHitArea(){
        Polygon polygon = new Polygon(new float[]{0,0,ConstantsHelper.UNIT_DIMENSIONS / 4,0,ConstantsHelper.UNIT_DIMENSIONS / 4,ConstantsHelper.UNIT_DIMENSIONS / 4,0,ConstantsHelper.UNIT_DIMENSIONS / 4});
        polygon.setPosition(x, y);
        polygon.setOrigin(ConstantsHelper.UNIT_DIMENSIONS / 2, ConstantsHelper.UNIT_DIMENSIONS / 2);
        polygon.setRotation(0);
        return polygon.getBoundingRectangle();
    }

    public float getDamage(){
        return damage;
    }

    public void setDamage(float damage){
        this.damage = damage;
    }

    protected boolean hasHitTarget(Unit target) {
        return target != null && hitTargets.contains(target);
    }

    protected void recordHitTarget(Unit target) {
        if (target != null && !hitTargets.contains(target)) {
            hitTargets.add(target);
        }
    }

    public ThrownProjectile setDamageRange(float minDamage, float maxDamage) {
        float low = Math.min(minDamage, maxDamage);
        float high = Math.max(minDamage, maxDamage);
        damage = low + (high > low ? RandomHelper.getInstance().randomFloat(high - low) : 0f);
        return this;
    }
}
