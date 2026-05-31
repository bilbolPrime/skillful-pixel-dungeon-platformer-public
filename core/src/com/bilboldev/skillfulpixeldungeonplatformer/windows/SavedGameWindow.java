package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.GameScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.LoadingScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

import java.util.ArrayList;

public class SavedGameWindow extends Window {

    private static final float MIN_MENU_WIDTH = 1100f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float BUTTON_GAP = 18f;
    private static final float SIDE_PADDING = 100f;
    private static final float TOP_PADDING = 90f;
    private static final float TEXT_TO_BUTTON_GAP = 70f;
    private static final float BOTTOM_PADDING = 90f;

    private final HeroClass heroClass;
    private final ArrayList<ActionButton> buttons = new ArrayList<>();
    private ActionButton pressedButton;
    private String message;
    private float buttonWidth;

    public SavedGameWindow(HeroClass heroClass) {
        super(MIN_MENU_WIDTH, 460f);
        this.heroClass = heroClass;
    }

    @Override
    public Window build() {
        String continueLabel = Messages.get("windows.wndgameinprogress.continue");
        String startOverLabel = Messages.get("custom.generated.start_over_91a5868691");
        message = UtilsHelper.multiLine(
            Messages.get("custom.ui.saved_run_prompt", new Object[]{heroClass.getName(), continueLabel, startOverLabel}),
            3,
            ConstantsHelper.SCREEN_WIDTH - 2f * SIDE_PADDING - 200f);

        GlyphLayout glyphLayout = new GlyphLayout();
        float maxTextWidth = 0f;
        float lineHeight = 0f;
        String[] lines = message.split("\\n");
        for (String line : lines) {
            glyphLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 3f), line);
            maxTextWidth = Math.max(maxTextWidth, glyphLayout.width);
            lineHeight = Math.max(lineHeight, glyphLayout.height);
        }

        buttonWidth = Math.max(900f, maxTextWidth + 120f);
        width = Math.max(MIN_MENU_WIDTH, buttonWidth + SIDE_PADDING * 2f);
        height = TOP_PADDING + lines.length * Math.max(55f, lineHeight + 8f) + TEXT_TO_BUTTON_GAP + BUTTON_HEIGHT * 2f + BUTTON_GAP + BOTTOM_PADDING;

        super.build();
        buttons.clear();

        float buttonX = x + SIDE_PADDING;
        float buttonY = y + BOTTOM_PADDING + BUTTON_HEIGHT + BUTTON_GAP;

        buttons.add(new PauseMenuRowButton(buttonX, buttonY, buttonWidth, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                launchGame(true);
            }
        }.setCenteredText(continueLabel));

        buttonY -= BUTTON_HEIGHT + BUTTON_GAP;
        buttons.add(new PauseMenuRowButton(buttonX, buttonY, buttonWidth, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                WindowHelper.getInstance().addWindow(new DifficultySelectWindow(heroClass, true).build());
            }
        }.setCenteredText(startOverLabel));

        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, 3f, x + SIDE_PADDING, y + height - TOP_PADDING, message);

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

    private void launchGame(boolean continueSavedRun) {
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new LoadingScreen().prepare(new GameScreen(heroClass, continueSavedRun), SkillfulPixelDungeonPlatformer.getActiveScreen()));
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }
}