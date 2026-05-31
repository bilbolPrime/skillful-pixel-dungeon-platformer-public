package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Effect {
    protected GameSprite gs;
    protected float x, y, speedX, speedY, rotation, speedRotation;
    protected float lifeSpan = 80f;

    public Effect init(float x, float y, float rotation, float speedX, float speedY, float speedRotation){
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedRotation = speedRotation;

        return this;
    }

    public void act(float delta){
        x += speedX * delta;
        y += speedY * delta;
        rotation += speedRotation * delta;

        gravity(delta);

        lifeSpan = Math.max(0, lifeSpan - 30f * delta);

        if(gs != null){
            gs.setAlpha(lifeSpan / 100f);
            gs.setPosition(x, y);
            gs.setRotation(rotation);
        }
    }

    void gravity(float delta){
        speedY -= ConstantsHelper.GRAVITY  * delta;
    }

    public void draw(Batch batch){
        if(gs != null){
            gs.draw(batch);
        }
    }

    public boolean active(){
        return lifeSpan > 0;
    }
}

