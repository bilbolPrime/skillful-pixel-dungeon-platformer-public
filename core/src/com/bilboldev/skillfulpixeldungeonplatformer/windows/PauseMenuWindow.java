package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;

import java.util.ArrayList;

public class PauseMenuWindow extends Window {

    private static final float MENU_WIDTH = 1000f;
    private static final float BUTTON_WIDTH = 900f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float BUTTON_GAP = 16f;
    private static final float TOP_PADDING = 74f;
    private static final float SIDE_PADDING = 90f;
    private static final float BOTTOM_PADDING = 78f;
    private static final float TITLE_SCREEN_EDGE_GAP_RATIO = 0.1f;

    private final ArrayList<ActionButton> buttons;
    private final boolean showSaveExit;
    private ActionButton pressedButton;

    public PauseMenuWindow() {
        this(true);
    }

    public PauseMenuWindow(boolean showSaveExit) {
        super(MENU_WIDTH, 420f);
        this.showSaveExit = showSaveExit;
        buttons = new ArrayList<>();
    }

    @Override
    public Window build() {
        boolean desktopControls = SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled()
                && !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
        int buttonCount = desktopControls ? 2 : 1;
        if (!showSaveExit) {
            buttonCount++;
        }
        if (showSaveExit) {
            buttonCount++;
        }
        float titleScreenEdgeGap = showSaveExit ? 0f : BUTTON_HEIGHT * TITLE_SCREEN_EDGE_GAP_RATIO;
        width = BUTTON_WIDTH + SIDE_PADDING * 2f;
        height = buttonCount * BUTTON_HEIGHT + (buttonCount - 1) * BUTTON_GAP + TOP_PADDING + BOTTOM_PADDING + titleScreenEdgeGap * 2f;

        super.build();
        buttons.clear();

        float buttonX = x + SIDE_PADDING;
        float buttonY = y + height - TOP_PADDING - titleScreenEdgeGap - BUTTON_HEIGHT;

        buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                WindowHelper.getInstance().replaceWindow(new PauseSettingsWindow(showSaveExit).build());
            }
        }.setCenteredText("SETTINGS"));

        if (!showSaveExit) {
            buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
            buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override
                public void clicked() {
                    WindowHelper.getInstance().addWindow(new LanguageSelectWindow().build());
                }
            }.setBindingRow("LANGUAGE", GameSettingsHelper.getInstance().getLanguage().nativeName(), true));
        }

        if (desktopControls) {
            buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
            buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override
                public void clicked() {
                    WindowHelper.getInstance().replaceWindow(new PauseControlsWindow(showSaveExit).build());
                }
            }.setCenteredText(Messages.get("windows.wndsettings$inputtab.key_bindings")));
        }

        if (showSaveExit) {
            buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
            buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override
                public void clicked() {
                    if (!SaveHelper.getInstance().saveCurrentRun()) {
                        WindowHelper.getInstance().addWindow(new TextWindow(800f, 120f, "Could not save the current run.").build());
                        return;
                    }

                    WindowHelper.getInstance().hideAll();
                    SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
                }
            }.setCenteredText("SAVE & EXIT"));
        }

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
            WindowHelper.getInstance().closeWindow(this);
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        WindowHelper.getInstance().closeWindow(this);
        return true;
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }
}
