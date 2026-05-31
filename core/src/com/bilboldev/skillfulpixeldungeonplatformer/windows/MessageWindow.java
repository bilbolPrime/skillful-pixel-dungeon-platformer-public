package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class MessageWindow extends Window{
    protected float x, y;
    protected  float width, height;
    protected ArrayList<GameSprite> gameSprites;

    public MessageWindow(float width, float height){
        super(width, height);
    }

    public MessageWindow build(){
        x = (ConstantsHelper.SCREEN_WIDTH - width) / 2;
        y = (ConstantsHelper.SCREEN_HEIGHT - height) / 2;

        GameSprite gs = new GameSprite("images/window/body-top.png", width - 80, 80);
        gs.setPosition(x + 40, y + height - 80);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/top-left.png", 40, 80);
        gs.setPosition(x, y + height - 80);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/top-right.png", 40, 80);
        gs.setPosition(x + width - 40, y + height - 80);
        gameSprites.add(gs);


        gs = new GameSprite("images/window/body.png", width - 80, height - 160);
        gs.setPosition(x + 40, y + 80);
        gameSprites.add(gs);

        gs = new GameSprite("images/window/body-bottom.png", width - 80, 80);
        gs.setPosition(x + 40, y);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/bottom-left.png", 40, 80);
        gs.setPosition(x, y);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/bottom-right.png", 40, 80);
        gs.setPosition(x + width - 40, y);
        gameSprites.add(gs);

        return  this;
    }

    public void draw(Batch batch){
        if(gameSprites == null || gameSprites.size() == 0){
            return;
        }

        for(GameSprite gs : gameSprites){
            gs.draw(batch);
        }
    }

    public boolean click(float x, float y){
        if(x < this.x || x > this.x + this.width){
            hide();
        }

        if(y < this.y || y > this.y + this.height){
            hide();
        }

        return true;
    }

    public void hide(){
        WindowHelper.getInstance().closeWindow(this);
    }
}


