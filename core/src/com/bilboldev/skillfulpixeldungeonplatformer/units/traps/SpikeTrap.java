package com.bilboldev.skillfulpixeldungeonplatformer.units.traps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;

public class SpikeTrap extends Unit {
    protected boolean used;

    public boolean isSpent() { return used; }
    protected float lastCheck, animationAt;

    {
        showOnly = true;
        gf = new GameFilm("images/units/traps/spike-trap/spike-trap.png", 60, 19,1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 19;
        weapon = (MeleeAttack) new MeleeAttack(){
            @Override
            public Rectangle getHitArea(){
                Polygon polygon = new Polygon(new float[]{0,0, ConstantsHelper.UNIT_DIMENSIONS,0,ConstantsHelper.UNIT_DIMENSIONS,ConstantsHelper.UNIT_DIMENSIONS,0,ConstantsHelper.UNIT_DIMENSIONS});
                polygon.setPosition(owner.x, owner.y);
                polygon.setOrigin(ConstantsHelper.UNIT_DIMENSIONS / 2, ConstantsHelper.UNIT_DIMENSIONS / 2);
                polygon.setRotation(0);
                return polygon.getBoundingRectangle();
            }
        }.setDamage(5f).setOwner(this);
    }

    @Override
    public void act(float delta){
        if(!MapHelper.getInstance().getActiveRoomIdentifier().equals(room)){
            return;
        }

        if(used){
            animationAt += 25f * delta;
            gf.tileX = Math.min(4, (int)animationAt);
            if(animationAt > 100f){
                removeUnit();
            }
            return;
        }

        if(lastCheck < 0){
            lastCheck = 1f;
            if(UnitHelper.getInstance().attack(this, weapon)){
                SoundHelper.GetSingleton().play(Sounds.TRAP, 1f, 1f);
                used = true;
            }
        }

        lastCheck -= 10f * delta;
    }


    @Override
    public void draw(Batch batch, float alpha){
        gf.setAlpha(animationAt == 0 ? 0.5f : (animationAt < 50f ? 1f : (100f - animationAt) / 50f));
        gf.setPosition(x, y);
        gf.faceRight(facingRight);
        gf.draw(batch);
    }
}

