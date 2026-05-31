package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;

import java.util.ArrayList;

public class TitleExitWindow extends Window {

    private static final float MENU_WIDTH = 1000f;
    private static final float BUTTON_WIDTH = 900f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float BUTTON_GAP = 16f;
    private static final float TOP_PADDING = 74f;
    private static final float SIDE_PADDING = 90f;
    private static final float BOTTOM_PADDING = 78f;
    private static final float TITLE_SCREEN_EDGE_GAP_RATIO = 0.1f;

    private final ArrayList<ActionButton> buttons = new ArrayList<>();
    private ActionButton pressedButton;

    public TitleExitWindow() {
        super(MENU_WIDTH, 0f);
    }

    @Override
    public Window build() {
        float edgeGap = BUTTON_HEIGHT * TITLE_SCREEN_EDGE_GAP_RATIO;
        width = BUTTON_WIDTH + SIDE_PADDING * 2f;
        height = BUTTON_HEIGHT * 2f + BUTTON_GAP + TOP_PADDING + BOTTOM_PADDING + edgeGap * 2f;

        super.build();
        buttons.clear();

        float buttonX = x + SIDE_PADDING;
        float buttonY = y + height - TOP_PADDING - edgeGap - BUTTON_HEIGHT;

        buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                Gdx.app.exit();
            }
        }.setCenteredText("Exit"));

        buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
        buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                hide();
            }
        }.setCenteredText("Cancel"));

        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        for (ActionButton button : buttons) {
            button.draw(batch);
        }
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        clearPressedButton();
        for (ActionButton actionButton : buttons) {
            if (actionButton.isHitProjected(x, y)) {
                pressedButton = actionButton;
                actionButton.pressDown();
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        if (pressedButton != null) {
            ActionButton tappedButton = pressedButton;
            pressedButton = null;
            tappedButton.releasePress();
            if (tappedButton.isHitProjected(x, y)) {
                tappedButton.click();
                return true;
            }

            tappedButton.cancelPress();
            return true;
        }

        if (x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height) {
            hide();
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        hide();
        return true;
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }
}