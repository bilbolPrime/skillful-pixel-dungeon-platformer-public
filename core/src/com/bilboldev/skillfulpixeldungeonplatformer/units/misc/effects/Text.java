package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Text extends Effect {

    {
        lifeSpan = 40f;
    }

    protected String text;
    protected Color color;

    public Text init(float x, float y, float speedX, String text, Color color){
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.text = text;
        this.speedY = 100f;
        this.color = color;
        return this;
    }

    @Override
    public void act(float delta){
        super.act(delta);
        speedX -= speedX * 1.5f * delta;
    }

    @Override
    public void draw(Batch batch){
        if(text != null){
            FontHelper.getSingleton().write(color, batch, 3, (int) x, (int) y,text);
        }
    }

    @Override
    public void gravity(float delta){
        speedY += speedY * 1.5f * delta;
    }
}

