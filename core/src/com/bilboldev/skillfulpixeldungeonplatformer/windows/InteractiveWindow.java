package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;
import java.util.List;

public class InteractiveWindow extends Window {
    protected ArrayList<ActionButton> actionButtons;
    protected final WindowChoiceFocus keyboardFocus = new WindowChoiceFocus();

    public InteractiveWindow(float width, float height) {
        super(width, height);
        actionButtons = new ArrayList<>();
    }

    @Override
    public Window build(){
        super.build();
        float offset = horizontalOffset();
        this.x += offset;
        for(GameSprite gs : gameSprites){
            gs.translate(offset, 0);
        }

        return this;
    }

    protected float horizontalOffset() { return 100f; }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        for(Button button : actionButtons){
            button.draw(batch);
        }
        keyboardFocus.draw(this, batch, getKeyboardChoices());
    }

    protected List<? extends Button> getKeyboardChoices() { return actionButtons; }

    @Override
    public boolean keyDown(int keycode) { return keyboardFocus.keyDown(this, keycode, getKeyboardChoices()); }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        keyboardFocus.pointerDown(x, y, getKeyboardChoices());
        return super.pointerDown(x, y, button);
    }

    @Override
    public boolean click(float x, float y){
        for(ActionButton actionButton : actionButtons){
            if(actionButton.isHitProjected(x, y)){
                actionButton.click();
                return true;
            }
        }

        if(x < this.x || x > this.x + this.width){
            hide();
        }

        if(y < this.y || y > this.y + this.height){
            hide();
        }

        return true;
    }

    @Override
    public void refresh(){
        actionButtons.clear();
        super.refresh();
    }
}

