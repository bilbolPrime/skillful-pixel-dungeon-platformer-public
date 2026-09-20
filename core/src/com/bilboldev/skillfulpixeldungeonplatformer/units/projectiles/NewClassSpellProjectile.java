package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpriteTrail;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;


public class NewClassSpellProjectile extends ThrownProjectile {
    public static final int MAX_LIVE = 16;
    public static final float SPEED = 1400f, RANGE = 1024f;
    private final Unit caster;
    private final Hero ownerHero;
    private final int placementVersion, ownerPlacementVersion;
    private final float startX, startY;
    private float elapsed;

    public NewClassSpellProjectile(Hero caster, float damage) {
        this(caster, caster, damage);
    }

    protected NewClassSpellProjectile(NecromancerMinion caster, float damage) {
        this(caster, caster.getOwnerHero(), damage);
    }

    private NewClassSpellProjectile(Unit caster, Hero ownerHero, float damage) {
        this.caster = caster;
        if (ownerHero == null) throw new IllegalArgumentException("Current spell/minion owner required");
        this.ownerHero = ownerHero;
        placementVersion = caster.getPresentationPlacementVersion();
        ownerPlacementVersion = ownerHero.getPresentationPlacementVersion();
        setOwner(caster); setRoom(caster.getRoom());
        isFriendly = caster.isFriendly; facingRight = caster.facingRight;

        x = startX = caster.x + ConstantsHelper.UNIT_DIMENSIONS / 2f - getPhysicsCollisionSize() / 2f;
        y = startY = caster.y + ConstantsHelper.UNIT_DIMENSIONS * 2f / 3f - getPhysicsCollisionSize() / 2f;
        speedX = facingRight ? SPEED : -SPEED; speedY = 0f;
        this.damage = damage; magicAttack = true;
        alignRotationToVelocity = false; rotationOffset = 0f;
        gs = new GameSprite("images/misc/lightning.png", 32, 14);
        gs.setColor(new Color(.82f, .72f, 1f, 1f));
    }


    public static int liveCount() {
        int count = 0;
        for (Unit unit : UnitHelper.getInstance().getUnits()) if (unit instanceof NewClassSpellProjectile) count++;
        return count;
    }

    public static void clearFor(Hero hero) {
        for (int i = UnitHelper.getInstance().getUnits().size() - 1; i >= 0; i--) {
            Unit unit = UnitHelper.getInstance().getUnits().get(i);
            if (unit instanceof NewClassSpellProjectile && ((NewClassSpellProjectile)unit).ownerHero == hero) {
                ((NewClassSpellProjectile)unit).markUsed();
                UnitHelper.getInstance().removeUnit(unit);
            }
        }
    }

    public static void clearFrom(Unit caster) {
        for (int i = UnitHelper.getInstance().getUnits().size() - 1; i >= 0; i--) {
            Unit unit = UnitHelper.getInstance().getUnits().get(i);
            if (unit instanceof NewClassSpellProjectile && ((NewClassSpellProjectile)unit).caster == caster) {
                ((NewClassSpellProjectile)unit).markUsed();
                UnitHelper.getInstance().removeUnit(unit);
            }
        }
    }

    protected final boolean validCaster() {
        return ownerHero == UnitHelper.getInstance().getHero() && !ownerHero.isDead() && !caster.isDead()
                && ownerPlacementVersion == ownerHero.getPresentationPlacementVersion()
                && placementVersion == caster.getPresentationPlacementVersion() && room != null
                && room.equals(caster.getRoom()) && room.equals(ownerHero.getRoom())
                && room.equals(MapHelper.getInstance().getActiveRoomIdentifier())
                && (caster == ownerHero || caster instanceof NecromancerMinion
                    && ((NecromancerMinion)caster).getOwnerHero() == ownerHero
                    && !((NecromancerMinion)caster).isPendingTransfer() && !caster.showOnly());
    }
    protected float maximumLifetime() { return RANGE / SPEED; }
    private boolean inRange() {
        float dx = x - startX, dy = y - startY;
        return Float.isFinite(dx) && Float.isFinite(dy) && dx * dx + dy * dy <= RANGE * RANGE;
    }
    @Override public float getPhysicsCollisionSize() { return 12f; }
    @Override public float getPhysicsGravityScale() { return 0f; }
    @Override public float getPhysicsLinearDamping() { return 0f; }
    @Override public boolean hitsBothSidesOfPlatforms() { return true; }

    @Override public void onUnitCollision(Unit target) {
        if (isUsed() || !validCaster() || target == null || target.isDead() || target.showOnly()
                || target.isFriendly == isFriendly || !room.equals(target.getRoom())) return;
        PhysicsHelper.getInstance().syncProjectileFromPhysics(this);

        boolean validPath = inRange() && PhysicsHelper.getInstance().hasSolidLineOfSight(
                MapHelper.getInstance().getActiveRoom(), startX + 6f, startY + 6f, x + 6f, y + 6f);
        markUsed();
        if (validPath) resolveHit(target);
    }

    protected void resolveHit(Unit target) {
        if (UnitHelper.getInstance().attackTarget(caster, target, null, damage, true)) playSound(Sounds.HIT, .4f);
    }

    @Override public void act(float delta) {
        if (!validCaster()) markUsed();
        super.act(delta);
        if (isUsed()) return;
        elapsed += PhysicsHelper.boundGameDelta(delta);


        if (!inRange() || elapsed >= maximumLifetime() || !PhysicsHelper.getInstance().hasSolidLineOfSight(
                MapHelper.getInstance().getActiveRoom(), startX + 6f, startY + 6f, x + 6f, y + 6f)) {
            markUsed();
            super.act(0f);
        }
    }

    @Override public void draw(Batch batch, float alpha) {
        if (isUsed() || !validCaster()) return;
        gs.setPosition(getRenderX() + 6f - gs.getWidth() / 2f, getRenderY() + 6f - gs.getHeight() / 2f);
        gs.setRotation(facingRight ? 0f : 180f); gs.setAlpha(.95f * alpha);
        SpriteTrail.draw(batch, gs, speedX * .012f, 0f, 18f);
        gs.draw(batch);
    }
}
