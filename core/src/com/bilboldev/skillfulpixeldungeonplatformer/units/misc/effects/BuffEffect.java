package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class BuffEffect extends Effect {

    protected ArrayList<XY> xyArrayList;
    Unit owner;
    protected float floatSpeedX;


    {
        floatSpeedX = 50;
    }

    public Effect init(float x, float y, float rotation, float speedX, float speedY, float speedRotation, Unit unit){
        super.init(x, y, rotation, speedX, speedY, speedRotation);
        this.owner = unit;
        xyArrayList = new ArrayList<>();

        xyArrayList.add(new XY(0 , ConstantsHelper.UNIT_DIMENSIONS / 3));
        xyArrayList.add(new XY(ConstantsHelper.UNIT_DIMENSIONS / 2, ConstantsHelper.UNIT_DIMENSIONS / 2));
        xyArrayList.add(new XY(ConstantsHelper.UNIT_DIMENSIONS , ConstantsHelper.UNIT_DIMENSIONS));

        return this;
    }


    @Override
    public void act(float delta){

        if(owner == null){
            return;
        }

        x = owner.x;
        y = owner.y;

        for(XY xy : xyArrayList){
            xy.y += floatSpeedX * delta;
            if(floatSpeedX > 0 && xy.y > ConstantsHelper.UNIT_DIMENSIONS){
                xy.y = RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS);
                xy.x = RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS);
            }

            if(floatSpeedX < 0 && xy.y < 0){
                xy.y = RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS);
                xy.x = RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS);
            }
        }

        lifeSpan = Math.max(0, lifeSpan - 5f * delta);

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
            float drawX = owner == null ? x : owner.getRenderX();
            float drawY = owner == null ? y : owner.getRenderY();
            for(XY xy : xyArrayList){
                gs.setPosition(drawX + xy.x, drawY + xy.y);
                gs.draw(batch);
            }
        }
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

