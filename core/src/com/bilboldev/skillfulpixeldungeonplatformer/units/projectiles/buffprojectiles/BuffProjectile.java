package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.BuffEffect;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

import java.util.ArrayList;

public class BuffProjectile extends ThrownProjectile {

    protected Unit owner;
    protected GameSprite gs;
    protected float fluctuation;
    protected boolean used;
    protected float lifeSpan;
    protected boolean negative;

    protected Class<?extends Buff> buff;
    protected String text;
    protected float floatSpeed = 100;

    protected boolean removed;

    protected ArrayList<XY> xyArrayList;
    {
        gs = new GameSprite("images/misc/black-particle.png", 10, 10);
        showOnly = true;
        speedY = 0;
        lifeSpan = 75f;
        damage = 0;
        magicAttack = true;
    }

    public BuffProjectile(){
        super();

        xyArrayList = new ArrayList<>();

        for(int i = 0; i < 20; i++){
            xyArrayList.add(new XY(RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS) , RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS)));
        }
    }
    public BuffProjectile setOwner(Unit unit){
        super.setOwner(unit);
        this.owner = unit;
        return this;
    }

    public BuffProjectile setSpeedY(float speedY){
        super.setSpeedY(speedY);
        this.speedY = speedY;
        return this;
    }

    public BuffProjectile setGameSprite(GameSprite gameSprite){
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
        markUsed();
        if (!UnitHelper.getInstance().tryHit(owner, target, attackingItem, magicAttack)) {
            SoundHelper.GetSingleton().play(Sounds.MISS, 0.4f, 1f);
            EffectsHelper.getInstance().blackSpark(this);
            return;
        }

        if(negative){
            SoundHelper.GetSingleton().play(Sounds.DEGRADE, 1f, 0.5f);
        }

        EffectsHelper.getInstance().blackSpark(this);

        try
        {
            buff.newInstance().setOwner(target);
            if(text != null){
                EffectsHelper.getInstance().message(target, text, negative ? Color.RED : Color.WHITE, 0);
            }
        }
        catch (Exception e){

        }
    }

    @Override
    public void act(float delta){

        if(used) {
            if(!removed){
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

           EffectsHelper.getInstance().blackSpark(this);
            return;
        }

        PhysicsHelper.getInstance().syncProjectileFromPhysics(this);

        fluctuation(delta);
    }

    protected void fluctuation(float delta){
        for(XY xy : xyArrayList){
            xy.y += floatSpeed * delta;
            xy.x += floatSpeed * delta;
            if(floatSpeed > 0 && xy.y > ConstantsHelper.UNIT_DIMENSIONS / 5){
                xy.y = ConstantsHelper.UNIT_DIMENSIONS / 5 - RandomHelper.getInstance().randomFloat(2 * ConstantsHelper.UNIT_DIMENSIONS / 5);
                xy.x = RandomHelper.getInstance().randomFloat(3 * ConstantsHelper.UNIT_DIMENSIONS / 5);
            }
        }
    }


    @Override
    public void draw(Batch batch, float alpha){

        if(room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())){
            return;
        }

        if(gs != null){
            for(XY xy : xyArrayList){
                gs.setPosition(x + xy.x, y + xy.y);
                gs.draw(batch);
            }
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


    class XY {
        public float x;
        public float y;

        public XY(float x, float y){
            this.x = x;
            this.y = y;
        }
    }
}

