package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Decoration extends Unit {
    protected GameSprite gs;

    {
        showOnly = true;
    }

    @Override
    public void act(float delta){

    }

    @Override
    public void draw(Batch batch, float alpha){
        if(gs != null){
            gs.setPosition(x, y);
            gs.draw(batch);
        }
    }
}

