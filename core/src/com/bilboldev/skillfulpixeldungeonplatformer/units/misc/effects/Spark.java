package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class Spark extends Effect {

    {
        gs = new GameSprite("images/intro/fireball-front.png", 20, 20);
    }
    protected ArrayList<XY> xyArrayList;


    public Effect init(float x, float y, float rotation, float speedX, float speedY, float speedRotation){
        super.init(x, y, rotation, speedX, speedY, speedRotation);
        xyArrayList = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            xyArrayList.add(new XY(0 , 0, speedX / 10 + (speedX > 0 ? 1 : -1)* (300f - RandomHelper.getInstance().randomFloat(300)),300f - RandomHelper.getInstance().randomFloat(300)));
        }

        return this;
    }


    @Override
    public void act(float delta){
        for(XY xy : xyArrayList){
            xy.y += xy.speedy * delta;
            xy.speedy -= delta * ConstantsHelper.GRAVITY / 5;
            xy.x += xy.speedx * delta;
        }

        lifeSpan = Math.max(0, lifeSpan - 15f * delta);

        if(gs != null){
            gs.setAlpha(lifeSpan / 100f);
            gs.setPosition(x, y);
            gs.setRotation(rotation);
        }
    }

    @Override
    void gravity(float delta){

    }


    @Override
    public void draw(Batch batch){
        if(gs != null){
            for(XY xy : xyArrayList){
                gs.setPosition(x + xy.x, y + xy.y);
                gs.draw(batch);
            }
        }
    }


    class XY {
        public float x;
        public float y;
        public float speedx;
        public float speedy;

        public XY(float x, float y, float speedx, float speedy){
            this.x = x;
            this.y = y;
            this.speedx = speedx;
            this.speedy = speedy;
        }


    }
}

