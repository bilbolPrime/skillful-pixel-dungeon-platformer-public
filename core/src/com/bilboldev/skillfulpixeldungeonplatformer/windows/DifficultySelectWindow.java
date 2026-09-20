package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.GameScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.LoadingScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

import java.util.ArrayList;

public class DifficultySelectWindow extends Window {
    private static final float WINDOW_WIDTH = 1020f;
    private static final float WINDOW_HEIGHT = 660f;
    private static final float SIDE_PADDING = 76f;
    private static final float ROW_HEIGHT = 112f;
    private static final float ROW_GAP = 22f;
    private static final float BUTTON_BORDER = 16f;
    private static final float LABEL_SIZE = 3f;

    private final HeroClass heroClass;
    private final boolean replaceExistingSave;
    private int runSlot = -1;
    private final ArrayList<ActionButton> buttons = new ArrayList<>();
    private final WindowChoiceFocus keyboardFocus = new WindowChoiceFocus();
    private ActionButton pressedButton;

    public DifficultySelectWindow(HeroClass heroClass, boolean replaceExistingSave) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.heroClass = heroClass;
        this.replaceExistingSave = replaceExistingSave;
    }

    public DifficultySelectWindow(HeroClass heroClass, int runSlot) {
        this(heroClass, false);
        this.runSlot = runSlot;
    }

    @Override
    public Window build() {
        super.build();
        buttons.clear();
        pressedButton = null;

        float rowWidth = width - SIDE_PADDING * 2f;
        float rowX = x + SIDE_PADDING;
        float totalRowsHeight = DifficultyHelper.Difficulty.values().length * ROW_HEIGHT
                + (DifficultyHelper.Difficulty.values().length - 1) * ROW_GAP;
        float rowY = y + (height + totalRowsHeight) / 2f - ROW_HEIGHT;
        for (DifficultyHelper.Difficulty difficulty : DifficultyHelper.Difficulty.values()) {
            buttons.add(new DifficultyOptionButton(rowX, rowY, rowWidth, ROW_HEIGHT, difficulty));
            rowY -= ROW_HEIGHT + ROW_GAP;
        }
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        for (ActionButton button : buttons) {
            button.draw(batch);
        }
        keyboardFocus.draw(this, batch, buttons);
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        clearPressedButton();
        keyboardFocus.pointerDown(x, y, buttons);
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
        return keyboardFocus.keyDown(this, keycode, buttons);
    }

    @Override
    public void cancelPointerInput() { clearPressedButton(); }

    private void launchGame(DifficultyHelper.Difficulty difficulty) {
        if (runSlot >= 0 && SaveHelper.getInstance().slotOccupied(runSlot)) {
            runSlot = SaveHelper.getInstance().firstEmptySlot();
            if (runSlot < 0) {
                hide();
                WindowHelper.getInstance().addWindow(new TextWindow(1100, 240,
                        com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages.get("desktop.runs.full")).build());
                return;
            }
        }
        if (replaceExistingSave) {
            SaveHelper.getInstance().deleteSave(heroClass);
        }

        DifficultyHelper.getInstance().setCurrentDifficulty(difficulty);
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(
            new LoadingScreen().prepare(runSlot >= 0 ? new GameScreen(heroClass, difficulty, runSlot)
                    : new GameScreen(heroClass, difficulty), SkillfulPixelDungeonPlatformer.getActiveScreen()));
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }

    private final class DifficultyOptionButton extends ActionButton {
        private final ArrayList<GameSprite> rowSprites = new ArrayList<>();
        private final DifficultyHelper.Difficulty difficulty;
        private final boolean unlocked;
        private final String label;
        private final float buttonWidth;
        private final float buttonHeight;

        private DifficultyOptionButton(float x, float y, float width, float height, DifficultyHelper.Difficulty difficulty) {
            super(x, y, width, height, "images/misc/transparent.png", "images/misc/transparent.png");
            enableUiPressFeedback();
            this.difficulty = difficulty;
            this.unlocked = DifficultyHelper.getInstance().isUnlocked(heroClass, difficulty);
            this.label = difficulty.getDisplayName();
            this.buttonWidth = width;
            this.buttonHeight = height;

            GameSprite gs = new GameSprite("images/buttons/red-button/top.png", width - BUTTON_BORDER, BUTTON_BORDER);
            gs.setPosition(x + BUTTON_BORDER, y + height - BUTTON_BORDER);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/top-left.png", BUTTON_BORDER, BUTTON_BORDER);
            gs.setPosition(x, y + height - BUTTON_BORDER);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/top-right.png", BUTTON_BORDER, BUTTON_BORDER);
            gs.setPosition(x + width - BUTTON_BORDER, y + height - BUTTON_BORDER);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/left.png", BUTTON_BORDER, height - BUTTON_BORDER * 2f);
            gs.setPosition(x, y + BUTTON_BORDER);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/right.png", BUTTON_BORDER, height - BUTTON_BORDER * 2f);
            gs.setPosition(x + width - BUTTON_BORDER, y + BUTTON_BORDER);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/body.png", width - BUTTON_BORDER * 2f, height - BUTTON_BORDER * 2f);
            gs.setPosition(x + BUTTON_BORDER, y + BUTTON_BORDER);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/body.png", width - BUTTON_BORDER, BUTTON_BORDER);
            gs.setPosition(x + BUTTON_BORDER, y);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/bottom-left.png", BUTTON_BORDER, BUTTON_BORDER);
            gs.setPosition(x, y);
            rowSprites.add(gs);
            gs = new GameSprite("images/buttons/red-button/bottom-right.png", BUTTON_BORDER, BUTTON_BORDER);
            gs.setPosition(x + width - BUTTON_BORDER, y);
            rowSprites.add(gs);
        }

        @Override
        public void clicked() {
            if (unlocked) {
                launchGame(difficulty);
            }
        }

        @Override
        public void draw(Batch batch) {
            Color previousColor = new Color(batch.getColor());
            float tint = unlocked ? 1f : 0.55f;
            if (isShowingPressFeedback() && unlocked) {
                tint *= 1.15f;
            }
            batch.setColor(previousColor.r * tint, previousColor.g * tint, previousColor.b * tint, previousColor.a);
            for (GameSprite sprite : rowSprites) {
                sprite.draw(batch);
            }
            batch.setColor(previousColor);

            Color labelColor = unlocked ? Color.WHITE : Color.LIGHT_GRAY;
            GlyphLayout layout = new GlyphLayout(FontHelper.getSingleton().getFont(labelColor, LABEL_SIZE), label);
            float labelX = x + (buttonWidth - layout.width) / 2f;
            float labelY = y + (buttonHeight + layout.height) / 2f;
            FontHelper.getSingleton().write(labelColor, batch, LABEL_SIZE, labelX, labelY, label);
        }
    }
}
