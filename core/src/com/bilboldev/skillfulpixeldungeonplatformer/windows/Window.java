package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;

import java.util.ArrayList;

public class Window {
    protected float x, y;
    protected  float width, height;
    protected ArrayList<GameSprite> gameSprites;
    protected Runnable onHide;

    public Window(float width, float height){
        this.width = width;
        this.height = height;

        gameSprites = new ArrayList<>();
    }

    public boolean contains(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }

    public static ArrayList<GameSprite> createOverlayBackgroundSprites(float x, float y, float width, float height, float borderWidth, float borderHeight) {
        ArrayList<GameSprite> sprites = new ArrayList<>();
        float clampedBorderWidth = Math.max(1f, Math.min(borderWidth, width / 2f));
        float clampedBorderHeight = Math.max(1f, Math.min(borderHeight, height / 2f));
        float innerWidth = Math.max(0f, width - clampedBorderWidth * 2f);
        float innerHeight = Math.max(0f, height - clampedBorderHeight * 2f);

        if (innerWidth > 0f) {
            GameSprite gs = new GameSprite("images/window/body-top.png", innerWidth, clampedBorderHeight);
            gs.setPosition(x + clampedBorderWidth, y + height - clampedBorderHeight);
            sprites.add(gs);

            gs = new GameSprite("images/window/body-bottom.png", innerWidth, clampedBorderHeight);
            gs.setPosition(x + clampedBorderWidth, y);
            sprites.add(gs);
        }

        GameSprite gs = new GameSprite("images/window/top-left.png", clampedBorderWidth, clampedBorderHeight);
        gs.setPosition(x, y + height - clampedBorderHeight);
        sprites.add(gs);

        gs = new GameSprite("images/window/top-right.png", clampedBorderWidth, clampedBorderHeight);
        gs.setPosition(x + width - clampedBorderWidth, y + height - clampedBorderHeight);
        sprites.add(gs);

        if (innerHeight > 0f) {
            gs = new GameSprite("images/window/body-left.png", clampedBorderWidth, innerHeight);
            gs.setPosition(x, y + clampedBorderHeight);
            sprites.add(gs);

            gs = new GameSprite("images/window/body-right.png", clampedBorderWidth, innerHeight);
            gs.setPosition(x + width - clampedBorderWidth, y + clampedBorderHeight);
            sprites.add(gs);
        }

        if (innerWidth > 0f && innerHeight > 0f) {
            gs = new GameSprite("images/window/body.png", innerWidth, innerHeight);
            gs.setPosition(x + clampedBorderWidth, y + clampedBorderHeight);
            sprites.add(gs);
        }

        gs = new GameSprite("images/window/bottom-left.png", clampedBorderWidth, clampedBorderHeight);
        gs.setPosition(x, y);
        sprites.add(gs);

        gs = new GameSprite("images/window/bottom-right.png", clampedBorderWidth, clampedBorderHeight);
        gs.setPosition(x + width - clampedBorderWidth, y);
        sprites.add(gs);

        return sprites;
    }

    public Window build(){
        x = (ConstantsHelper.SCREEN_WIDTH - width) / 2;
        y = (ConstantsHelper.SCREEN_HEIGHT - height) / 2;

        GameSprite gs = new GameSprite("images/window/body-top.png", width - 80, 80);
        gs.setPosition(x + 80, y + height - 80);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/top-left.png", 80, 80);
        gs.setPosition(x, y + height - 80);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/top-right.png", 80, 80);
        gs.setPosition(x + width - 80, y + height - 80);
        gameSprites.add(gs);

        gs = new GameSprite("images/window/body-left.png", 80, height - 160);
        gs.setPosition(x, y + 80);
        gameSprites.add(gs);

        gs = new GameSprite("images/window/body-right.png", 80, height - 160);
        gs.setPosition(x + width - 80, y + 80);
        gameSprites.add(gs);

        gs = new GameSprite("images/window/body.png", width - 160, height - 160);
        gs.setPosition(x + 80, y + 80);
        gameSprites.add(gs);

        gs = new GameSprite("images/window/body-bottom.png", width - 80, 80);
        gs.setPosition(x + 80, y);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/bottom-left.png", 80, 80);
        gs.setPosition(x, y);
        gameSprites.add(gs);
        gs = new GameSprite("images/window/bottom-right.png", 80, 80);
        gs.setPosition(x + width - 80, y);
        gameSprites.add(gs);

        return  this;
    }

    public void draw(Batch batch){
        if (DesktopMenuStyle.active()) {
            DesktopMenuStyle.window(batch, x, y, width, height);
            return;
        }
        if(gameSprites == null || gameSprites.size() == 0){
            return;
        }

        for(GameSprite gs : gameSprites){
            gs.draw(batch);
        }
    }

    public boolean click(float x, float y){
        if(x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height){
            hide();
        }

        return true;
    }

    public boolean pointerDown(float x, float y, int button) {
        return true;
    }

    public boolean tap(float x, float y) {
        return click(x, y);
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    public boolean scroll(float amountY) {
        return false;
    }

    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK ||
                GameSettingsHelper.getInstance().getInteractBinding().matchesKey(keycode)) {
            hide();
            return true;
        }
        return false;
    }


    public void cancelPointerInput() { }

    public Window setOnHide(Runnable onHide) {
        this.onHide = onHide;
        return this;
    }

    public void hide(){
        WindowHelper.getInstance().closeWindow(this);

        if (onHide != null) {
            Runnable callback = onHide;
            onHide = null;
            callback.run();
        }
    }

    public void refresh(){
        gameSprites.clear();
        build();
    }
}


