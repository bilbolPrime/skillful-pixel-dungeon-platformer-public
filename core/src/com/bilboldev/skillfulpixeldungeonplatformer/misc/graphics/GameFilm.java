package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;

public class GameFilm extends GameSprite{

    public int tileX, tileY;
    public int clipSizeX = 16;
    public int clipSizeY = 16;
    public int yClipOffset = 0;

    protected boolean facingRight = true;

    public GameFilm(Sprite sprite, float width, float height){
        super(sprite, width, height);
    }

    public GameFilm(String sprite, float width, float height, float alpha){
        super(sprite, width, height, alpha);
    }

    public void setFrame(int tileX, int tileY){

    }

    @Override
    public void faceRight(boolean faceRight){
        facingRight = faceRight;
    }

    @Override
    public void draw(Batch batch){

        sprite.setOrigin(originX, originY);
        sprite.setRotation(rotation);
        sprite.setAlpha(alpha);
        Color c = sprite.getColor();
        float preA = c.a;
        c.a *= alpha;
        batch.setColor(c);
        //extureRegion region, float x, float y, float originX, float originY, float width, float height,
        //		float scaleX, float scaleY, float rotation
        if(!repeatable)
            batch.draw(sprite.getTexture(), x, y, originX, originY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS, scaleX, scaleY, rotation, tileX * clipSizeX, (yClipOffset + tileY) * clipSizeY,  clipSizeX, clipSizeY, !facingRight, false);
        else
            batch.draw(textureRegion, x, y, width, height);
        c.a = 1;
        batch.setColor(c);
    }
}

