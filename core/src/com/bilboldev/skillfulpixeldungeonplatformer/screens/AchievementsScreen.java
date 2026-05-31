package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.AchievementInfoWindow;

public class AchievementsScreen extends MenuScreenBase {

    private static final float BADGE_BUTTON_SIZE = 108f;
    private static final float BADGE_HORIZONTAL_GAP = 26f;
    private static final float BADGE_VERTICAL_GAP = 22f;
    private static final float ICON_SIZE = 96f;
    private static final int COLUMNS = 8;

    @Override
    protected void createMenuContent() {
        addTitleBackButton();

        Achievement[] achievements = Achievement.values();
        int rows = (achievements.length + COLUMNS - 1) / COLUMNS;
        float gridWidth = COLUMNS * BADGE_BUTTON_SIZE + (COLUMNS - 1) * BADGE_HORIZONTAL_GAP;
        float gridHeight = rows * BADGE_BUTTON_SIZE + (rows - 1) * BADGE_VERTICAL_GAP;
        float startX = (ConstantsHelper.SCREEN_WIDTH - gridWidth) / 2f;
        float startY = (ConstantsHelper.SCREEN_HEIGHT + gridHeight) / 2f - BADGE_BUTTON_SIZE;

        for (int index = 0; index < achievements.length; index++) {
            int column = index % COLUMNS;
            int row = index / COLUMNS;
            float x = startX + column * (BADGE_BUTTON_SIZE + BADGE_HORIZONTAL_GAP);
            float y = startY - row * (BADGE_BUTTON_SIZE + BADGE_VERTICAL_GAP);
            buttons.add(new AchievementCardButton(achievements[index], x, y));
        }
    }

    @Override
    protected void drawMenu(Batch batch) {
        drawButtons(batch);
    }

    @Override
    protected boolean handleBackAction() {
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
        return true;
    }

    private static final class AchievementCardButton extends ActionButton {
        private final Achievement achievement;
        private final boolean unlocked;
        private final boolean implemented;
        private final GameSprite icon;

        private AchievementCardButton(Achievement achievement, float x, float y) {
            super(x, y, BADGE_BUTTON_SIZE, BADGE_BUTTON_SIZE, "images/misc/black.png", "images/misc/black.png");
            enableUiPressFeedback();
            this.achievement = achievement;
            unlocked = AchievementManager.getInstance().isUnlocked(achievement);
            implemented = AchievementManager.getInstance().isImplemented(achievement);
            icon = new GameSprite(unlocked ? achievement.getAchievedIconPath() : achievement.getUnachievedIconPath(), ICON_SIZE, ICON_SIZE);
            icon.setPosition(x + (BADGE_BUTTON_SIZE - ICON_SIZE) / 2f, y + (BADGE_BUTTON_SIZE - ICON_SIZE) / 2f);
        }

        @Override
        public void draw(Batch batch) {
            float originalAlpha = icon.getAlpha();
            icon.setAlpha(isShowingPressFeedback() ? 0.75f : originalAlpha);
            icon.draw(batch);
            icon.setAlpha(originalAlpha);
        }

        @Override
        public void click() {
            WindowHelper.getInstance().hideAll();
            float overlayWidth = Math.max(1100f, Math.min(ConstantsHelper.SCREEN_WIDTH - 220f, 1750f));
            WindowHelper.getInstance().addWindow(new AchievementInfoWindow(achievement, unlocked, implemented, overlayWidth, 520f).build());
        }
    }
}
