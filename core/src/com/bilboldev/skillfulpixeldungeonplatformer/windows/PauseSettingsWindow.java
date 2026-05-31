package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.WindowModeService;

import java.util.ArrayList;

public class PauseSettingsWindow extends Window {

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

    public PauseSettingsWindow() {
        this(true);
    }

    public PauseSettingsWindow(boolean showSaveExit) {
        super(MENU_WIDTH, BUTTON_HEIGHT * 2f + BUTTON_GAP + TOP_PADDING + BOTTOM_PADDING);
        this.showSaveExit = showSaveExit;
        buttons = new ArrayList<>();
    }

    @Override
    public Window build() {
        float titleScreenEdgeGap = showSaveExit ? 0f : BUTTON_HEIGHT * TITLE_SCREEN_EDGE_GAP_RATIO;
        WindowModeService windowModeService = SkillfulPixelDungeonPlatformer.getPlatformProfile().windowModeService();
        boolean showWindowedModeButton = !showSaveExit && windowModeService.isSupported();
        boolean showBorderlessWindowedModeButton = showWindowedModeButton && windowModeService.supportsBorderlessWindowedMode();
        int rowCount = 2 + (showWindowedModeButton ? 1 : 0) + (showBorderlessWindowedModeButton ? 1 : 0);
        width = BUTTON_WIDTH + SIDE_PADDING * 2f;
        height = BUTTON_HEIGHT * rowCount + BUTTON_GAP * (rowCount - 1) + TOP_PADDING + BOTTOM_PADDING + titleScreenEdgeGap * 2f;
        super.build();
        buttons.clear();

        float buttonX = x + SIDE_PADDING;
        float buttonY = y + height - TOP_PADDING - titleScreenEdgeGap - BUTTON_HEIGHT;

        buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                GameSettingsHelper settings = GameSettingsHelper.getInstance();
                settings.setMusicEnabled(!settings.isMusicEnabled());
                setChecked(settings.isMusicEnabled());
            }
        }.setCheckboxRow("Music", GameSettingsHelper.getInstance().isMusicEnabled()));

        buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
        buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                GameSettingsHelper settings = GameSettingsHelper.getInstance();
                boolean soundWasEnabled = settings.isSoundFxEnabled();
                settings.setSoundFxEnabled(!soundWasEnabled);
                setChecked(settings.isSoundFxEnabled());
                if (!soundWasEnabled && settings.isSoundFxEnabled()) {
                    SoundHelper.GetSingleton().playUiClick();
                }
            }
        }.setCheckboxRow("Sound FX", GameSettingsHelper.getInstance().isSoundFxEnabled()));

        if (showWindowedModeButton) {
            buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
            buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override
                public void clicked() {
                    windowModeService.setWindowedModeEnabled(!windowModeService.isWindowedModeEnabled());
                    WindowHelper.getInstance().replaceWindow(new PauseSettingsWindow(showSaveExit).build());
                }
            }.setCheckboxRow("Windowed Mode", windowModeService.isWindowedModeEnabled()));
        }

        if (showBorderlessWindowedModeButton) {
            buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
            buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override
                public void clicked() {
                    windowModeService.setBorderlessWindowedModeEnabled(!windowModeService.isBorderlessWindowedModeEnabled());
                    WindowHelper.getInstance().replaceWindow(new PauseSettingsWindow(showSaveExit).build());
                }
            }.setCheckboxRow("Borderless Windowed Mode", windowModeService.isBorderlessWindowedModeEnabled()));
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
            WindowHelper.getInstance().replaceWindow(new PauseMenuWindow(showSaveExit).build());
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        WindowHelper.getInstance().replaceWindow(new PauseMenuWindow(showSaveExit).build());
        return true;
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }
}