package com.bilboldev.skillfulpixeldungeonplatformer.units.misc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmbientMusicHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class AmbientSound extends Unit {


    public boolean intro = false;

    float lastCheck = 10f;

    @Override
    public void draw(Batch batch, float alpha){

    }

    @Override
    public void act(float delta){
        lastCheck -= delta * 10f;
        if(lastCheck < 0){
            if(intro)
                AmbientMusicHelper.getSingleton().playIntro(0.4f);
            else
                AmbientMusicHelper.getSingleton().playBackground(0.4f);

            lastCheck = 10f;
        }
    }

    public AmbientSound setIntro(){
        intro = true;
        return this;
    }
}

