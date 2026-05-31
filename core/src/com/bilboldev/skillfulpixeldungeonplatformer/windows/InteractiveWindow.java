package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class InteractiveWindow extends Window {
    protected ArrayList<ActionButton> actionButtons;

    public InteractiveWindow(float width, float height) {
        super(width, height);
        actionButtons = new ArrayList<>();
    }

    @Override
    public Window build(){
        super.build();
        this.x += 100;
        for(GameSprite gs : gameSprites){
            gs.translate(100, 0);
        }

        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        for(Button button : actionButtons){
            button.draw(batch);
        }
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

