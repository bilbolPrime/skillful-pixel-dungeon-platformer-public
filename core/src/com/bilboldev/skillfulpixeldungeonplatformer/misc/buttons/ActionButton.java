package com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.TimeUtils;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class ActionButton extends Button {

    private static final long UI_PRESS_FLASH_MILLIS = 120L;
    private static final float DEFAULT_AVAILABLE_ALPHA = 1f;
    private static final float DEFAULT_PRESSED_ALPHA = 0.9f;
    private static final float DEFAULT_UNAVAILABLE_ALPHA = 1f;


    private int item = -1;
    private int itemCost = 1;
    private  int itemCount = 0;
    private int manaCost = 0;
    private boolean haveMana = false;
    private boolean actionAvailable = true;
    private boolean useItemCountForAvailability = false;
    private float availableAlpha = DEFAULT_AVAILABLE_ALPHA;
    private float pressedAlpha = DEFAULT_PRESSED_ALPHA;
    private float unavailableAlpha = DEFAULT_UNAVAILABLE_ALPHA;
    private  boolean showAlert = false;


    GameSprite sprNotPressed;
    GameSprite sprPressed;

    GameSprite countPlaceHolder;
    BitmapFont font;
    private boolean uiPressFeedbackEnabled;
    private boolean uiPressFeedbackConsumed;
    private boolean uiPressing;
    private long uiPressFlashUntilMillis;


    public ActionButton(float x, float y, float width, float height, Texture notPressed, Texture pressed) {
        super(x, y, width, height, notPressed, pressed);
    }

    public ActionButton(float x, float y, float width, float height, String notPressed, String pressed) {
        super(x, y, width, height, notPressed, pressed);

        sprNotPressed = new GameSprite(notPressed, width, height);
        sprPressed = new GameSprite(pressed, width, height);

        sprNotPressed.translate(x, y);
        sprPressed.translate(x, y);

    // sprNotPressed.setAlpha(0.8f);
     sprPressed.setAlpha(1f);
        enabled = true;

        //font = FontHelper.getSingleton().getDimboOutlineFont(50);

        countPlaceHolder = new GameSprite("images/buttons/count-placeholder.png", 75, 75);
        countPlaceHolder.setPosition(x + width - 57, y + height - 80);
    }


    @Override
    public void draw(Batch batch){

        if(!enabled ){
            return;
        }

        boolean clickable = canClick();
        boolean showPressed = clickable && isShowingPressFeedback();
        float visualAlpha = !clickable ? unavailableAlpha : (showPressed ? pressedAlpha : availableAlpha);
        Color previousColor = new Color(batch.getColor());
        if (showPressed) {
            batch.setColor(previousColor.r * 1.2f, previousColor.g * 1.2f, previousColor.b * 1.2f, previousColor.a * visualAlpha);
        } else {
            batch.setColor(previousColor.r, previousColor.g, previousColor.b, previousColor.a * visualAlpha);
        }

        if (sprNotPressed != null) {
            if (showPressed && sprPressed != null) {
                sprPressed.draw(batch);
            } else {
                sprNotPressed.draw(batch);
            }
        } else if (notPressed != null) {
            Texture texture = showPressed && pressed != null ? pressed : notPressed;
            batch.draw(texture, x, y, width, height);
        }

        batch.setColor(previousColor);

        if(gameSprites != null){
            for(GameSprite sprite : gameSprites){
                float alphaSprite = sprite.getAlpha();
                sprite.setAlpha(alphaSprite * visualAlpha);
                sprite.draw(batch);
                sprite.setAlpha(alphaSprite);
            }
        }


        if(itemCount > 0){
            float placeholderAlpha = countPlaceHolder.getAlpha();
            countPlaceHolder.setAlpha(placeholderAlpha * visualAlpha);
            countPlaceHolder.draw(batch);
            countPlaceHolder.setAlpha(placeholderAlpha);
            FontHelper.getSingleton().write(new Color(1f, 1f, 1f, visualAlpha), batch, 3f, x + width - 40 + (itemCount > 9 ? 0 : 10), y + height - 25, "" + itemCount);
        }

        if(manaCost > 0){
            float placeholderAlpha = countPlaceHolder.getAlpha();
            countPlaceHolder.setAlpha(placeholderAlpha * visualAlpha);
            countPlaceHolder.draw(batch);
            countPlaceHolder.setAlpha(placeholderAlpha);
            if(haveMana){
                FontHelper.getSingleton().write(new Color(Color.ROYAL.r, Color.ROYAL.g, Color.ROYAL.b, visualAlpha), batch, 3f, x + width - 40 + (manaCost > 9 ? 0 : 10), y + height - 25, "" + manaCost);
            }
            else {
                FontHelper.getSingleton().write(new Color(Color.FIREBRICK.r, Color.FIREBRICK.g, Color.FIREBRICK.b, visualAlpha), batch, 3f, x + width - 40 + (manaCost > 9 ? 0 : 10), y + height - 25, "" + manaCost);
            }
        }
    }

    public void setItemCount(int itemCount){
        this.itemCount = Math.max(0, itemCount);
    }

    public void setUseItemCountForAvailability(boolean useItemCountForAvailability) {
        this.useItemCountForAvailability = useItemCountForAvailability;
    }

    public void setVisualAlpha(float availableAlpha, float pressedAlpha, float unavailableAlpha) {
        this.availableAlpha = clampAlpha(availableAlpha);
        this.pressedAlpha = clampAlpha(pressedAlpha);
        this.unavailableAlpha = clampAlpha(unavailableAlpha);
    }

    public void setManaCost(int manaCost){
        this.manaCost = manaCost;
    }

    public void setActionAvailable(boolean actionAvailable) {
        this.actionAvailable = actionAvailable;
    }

    @Override
    public void setPosition(float x, float y) {
        float deltaX = x - this.x;
        float deltaY = y - this.y;

        super.setPosition(x, y);

        if (sprNotPressed != null) {
            sprNotPressed.translate(deltaX, deltaY);
        }

        if (sprPressed != null) {
            sprPressed.translate(deltaX, deltaY);
        }

        if (countPlaceHolder != null) {
            countPlaceHolder.translate(deltaX, deltaY);
        }
    }

    private float clampAlpha(float alpha) {
        return Math.max(0f, Math.min(1f, alpha));
    }

    @Override
    public void click(){
        if(canClick()){
            if (uiPressFeedbackEnabled && !uiPressFeedbackConsumed) {
                triggerUiPressFeedback();
            }
            clicked();
            uiPressing = false;
            uiPressFeedbackConsumed = false;
        }
    }

    public void pressDown() {
        if (!canClick()) {
            return;
        }

        triggerUiPressFeedback();
    }

    public void releasePress() {
        uiPressing = false;
    }

    public void cancelPress() {
        uiPressing = false;
        uiPressFeedbackConsumed = false;
    }

    @Override
    public void longClick(){

    }

    @Override
    public boolean canClick(){
        return  canClick(0);
    }

    @Override
    public boolean canClick(int charges)
    {
        if (!enabled || !actionAvailable) {
            return false;
        }

        if (manaCost > 0 && !haveMana) {
            return false;
        }

        if (useItemCountForAvailability && itemCount < itemCost) {
            return false;
        }

        return true;
    }

    @Override
    public void clicked(){

    }

    protected ActionButton enableUiPressFeedback() {
        uiPressFeedbackEnabled = true;
        return this;
    }

    protected boolean isShowingPressFeedback() {
        return uiPressFeedbackEnabled && (uiPressing || TimeUtils.millis() < uiPressFlashUntilMillis);
    }

    private void triggerUiPressFeedback() {
        if (!uiPressFeedbackEnabled) {
            return;
        }

        uiPressing = true;
        uiPressFlashUntilMillis = TimeUtils.millis() + UI_PRESS_FLASH_MILLIS;
        uiPressFeedbackConsumed = true;
        SoundHelper.GetSingleton().playUiClick();
    }

    public void updateItemCount(){

    }


    public void addGameSprites(ArrayList<GameSprite> gameSprites){
        if( this.gameSprites == null){
            this.gameSprites = new ArrayList<GameSprite>();
        }

        this.gameSprites.addAll(gameSprites);
    }


    public void setHaveMana(boolean haveMana){
        this.haveMana = haveMana;
    }
}

