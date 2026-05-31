package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class FireBolt extends ThrownProjectile {

    protected Unit owner;
    protected GameSprite gs;
    protected float fluctuation;
    protected boolean used;
    protected float lifeSpan;

    {
        gs = new GameSprite("images/wands/fire-ball.png", 25, 25);
        showOnly = true;
        speedY = 0;
        damage = 4f + RandomHelper.getInstance().randomFloat(4f);
        lifeSpan = 75f;
        magicAttack = true;
    }

    public FireBolt setOwner(Unit unit){
        super.setOwner(unit);
        this.owner = unit;
        return this;
    }

    public FireBolt setSpeedY(float speedY){
        super.setSpeedY(speedY);
        this.speedY = speedY;
        return this;
    }

    public FireBolt setGameSprite(GameSprite gameSprite){
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
        return 0f;
    }

    @Override
    public float getPhysicsLinearDamping() {
        return 0f;
    }

    @Override
    public boolean collidesWithTerrain() {
        return false;
    }

    @Override
    public void onTerrainCollision() {
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack)) {
            playSound(Sounds.BLAST,10f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
        markUsed();
        EffectsHelper.getInstance().spark(this);
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
            used = true;
            return;
        }

        lifeSpan -= 75f * delta;

        if(lifeSpan < 0 && !used){
            markUsed();
            playSound(Sounds.BLAST,10f);
            EffectsHelper.getInstance().spark(this);
            return;
        }

        PhysicsHelper.getInstance().syncProjectileFromPhysics(this);

        fluctuation(delta);
    }

    protected void fluctuation(float delta){
        fluctuation -= delta * 100f;
        if(fluctuation < 0){
            gs.setHeight(10 + RandomHelper.getInstance().randomInt(15));
            fluctuation = 10f;
        }
    }

    @Override
    public void draw(Batch batch, float alpha){

        if(room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            return;
        }

        if(gs != null && !used){
            gs.setPosition(x, y + (facingRight ? 1 : -1) * (25 - gs.getHeight()) / 2);
            gs.setRotation(facingRight  ? 0 : 180);
            gs.draw(batch);
        }
    }

    public void modifyLifeSpan(float modification){
        lifeSpan += modification;
    }

    public float getLifeSpan(){
        return lifeSpan;
    }

    public void setLifeSpan(float lifeSpan){
        this.lifeSpan = lifeSpan;
    }

    public float getDamage(){
        return damage;
    }

    public void setDamage(float damage){
        this.damage = damage;
    }
}

