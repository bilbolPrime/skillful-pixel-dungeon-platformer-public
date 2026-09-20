package com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class Button {
    static float hitMargin = 20;
    public float x;
    public float y;
    float width;
    float height;
    Rectangle rec, recExtended;
    Texture notPressed;
    Texture pressed;
    boolean isHit;
    float topOffset;
    boolean enabled;
    ArrayList<GameSprite> gameSprites;

    public Button(float x, float y, float width, float height, Texture notPressed, Texture pressed){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.notPressed = notPressed;
        this.pressed = pressed;
        this.rec = new Rectangle(x, y, width, height);
        this.recExtended = new Rectangle(x - hitMargin, y - hitMargin, width + 2 * hitMargin, height + 2 * hitMargin);
        this.topOffset = Gdx.graphics.getHeight();
        enabled = true;
    }

    public Button(float x, float y, float width, float height, String notPressed, String pressed){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;


        this.rec = new Rectangle(x, y, width, height);
        this.recExtended = new Rectangle(x - hitMargin, y - hitMargin, width + 2 * hitMargin, height + 2 * hitMargin);
        this.topOffset = Gdx.graphics.getHeight();
        enabled = true;
    }

    public boolean isHit(float x, float y){
        if(!canClick())
            return false;

        isHit = this.recExtended.contains(x, this.topOffset - y);
        return  isHit;
    }

    public boolean isHitProjected(float x, float y){
        if(!canClick())
            return false;

        isHit = this.recExtended.contains(x,  y);
        return  isHit;
    }

    public void draw(Batch batch){

        if(!enabled){
            return;
        }

        boolean isTapped = false;








        batch.draw(isTapped ? pressed : notPressed, x, y);
    }

    public void disable(){

        enabled = false;
    }

    public void setEnabled(){
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    public void setPosition(float x, float y) {
        float deltaX = x - this.x;
        float deltaY = y - this.y;

        this.x = x;
        this.y = y;
        this.rec.setPosition(x, y);
        this.recExtended.setPosition(x - hitMargin, y - hitMargin);

        if (gameSprites != null) {
            for (GameSprite gameSprite : gameSprites) {
                gameSprite.translate(deltaX, deltaY);
            }
        }
    }

    public void click(){

    }

    public void longClick(){

    }

    public boolean canClick(){
        return  canClick(0);
    }

    public boolean canClick(int charges){
        return  enabled;
    }

    public void clicked(){

    }

    public void addGameSprite(GameSprite gameSprite){
        if(gameSprites == null){
            gameSprites = new ArrayList<GameSprite>();
        }

        gameSprite.translate(x, y);
        gameSprites.add(gameSprite);
    }

    public void addGameSprite(ArrayList<GameSprite> gameSprites){
        for(GameSprite gameSprite: gameSprites){
            addGameSprite(gameSprite);
        }
    }

    public void clearSprites(){
        gameSprites = new ArrayList<GameSprite>();
    }
}


