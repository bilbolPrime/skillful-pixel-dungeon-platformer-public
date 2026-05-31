package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class ManaShield extends Effect {

    protected float scramble;
    protected float scrambleX;
    protected boolean facingRight;

    {
        gs = new GameSprite("images/effects/mana-shield.png", ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
    }




    public Effect init(float x, float y, boolean facingRight){
        super.init(x, y, rotation, speedX, speedY, speedRotation);

        scramble = 10f;
        this.facingRight = facingRight;
        gs.faceRight(facingRight);
        return this;
    }


    @Override
    public void act(float delta){
        scramble -= 100f * delta;
        if(scramble < 0){
            scrambleX = (facingRight ? 1 : -1) * RandomHelper.getInstance().randomFloat(10f);
            scramble = 10f;
        }

        lifeSpan = Math.max(0, lifeSpan - 55f * delta);

        if(gs != null){
            gs.setAlpha(lifeSpan / 100f);
        }
    }

    @Override
    void gravity(float delta){

    }


    @Override
    public void draw(Batch batch){
        if(gs != null){
            gs.setPosition(x + scrambleX, y);
            gs.draw(batch);
        }
    }
}

