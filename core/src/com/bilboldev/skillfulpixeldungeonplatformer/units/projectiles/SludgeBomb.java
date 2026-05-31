package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class SludgeBomb extends ThrownProjectile {

    protected Unit owner;
    protected GameSprite gs;
    protected float rotation;
    protected boolean used;
    protected float damage = 1;

    {
        gs = new GameSprite("images/skills/sludge-bomb.png", 45, 45);
        showOnly = true;
        speedY = 20;
        damage = 2f + RandomHelper.getInstance().randomFloat(2f);
    }

    public SludgeBomb setOwner(Unit unit){
        super.setOwner(unit);
        this.owner = unit;
        return this;
    }

    public SludgeBomb setSpeedY(float speedY){
        super.setSpeedY(speedY);
        this.speedY = speedY;
        return this;
    }

    public SludgeBomb setGameSprite(GameSprite gameSprite){
        super.setGameSprite(gameSprite);
        this.gs = gameSprite;
        return this;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public void markUsed() {
        used = true;
    }

    @Override
    public float getPhysicsGravityScale() {
        return 1f;
    }

    @Override
    public float getPhysicsLinearDamping() {
        return 0.9f;
    }

    @Override
    public void onTerrainCollision() {
        EffectsHelper.getInstance().splash(this);
        markUsed();
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack)) {
            playSound(Sounds.HIT,0.4f);
        }
        else {
            playSound(Sounds.MISS,0.4f);
        }
        markUsed();
        EffectsHelper.getInstance().splash(this);
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
        rotation += delta * 100f * Math.max(Math.abs(speedX), Math.abs(speedY));
    }

    @Override
    public void draw(Batch batch, float alpha){
        if(gs != null && !used){
            gs.setRotation(rotation + (facingRight  ? - 45 : 135));
            gs.setPosition(x, y);
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
            EffectsHelper.getInstance().splash(this);
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
}

