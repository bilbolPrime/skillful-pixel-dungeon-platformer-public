package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.AchievementInfoWindow;

public class AchievementsScreen extends MenuScreenBase {

    private static final float BADGE_BUTTON_SIZE = 104f;
    private static final float BADGE_HORIZONTAL_GAP = 26f;
    private static final float BADGE_VERTICAL_GAP = 16f;
    private static final float ICON_SIZE = 88f;
    private static final int COLUMNS = 8;
    private float panelX, panelY, panelWidth, panelHeight;

    @Override
    protected void createMenuContent() {
        addTitleBackButton();

        Achievement[] achievements = Achievement.values();
        int columns = hostedContent ? 12 : COLUMNS;
        float buttonSize = hostedContent ? 132f : BADGE_BUTTON_SIZE;
        float iconSize = hostedContent ? 116f : ICON_SIZE;
        float horizontalGap = hostedContent ? 48f : BADGE_HORIZONTAL_GAP;
        float verticalGap = hostedContent ? 28f : BADGE_VERTICAL_GAP;
        int rows = (achievements.length + columns - 1) / columns;
        float gridWidth = columns * buttonSize + (columns - 1) * horizontalGap;
        float gridHeight = rows * buttonSize + (rows - 1) * verticalGap;
        float startX = (ConstantsHelper.SCREEN_WIDTH - gridWidth) / 2f;
        float startY = (ConstantsHelper.SCREEN_HEIGHT + gridHeight) / 2f - buttonSize - 40f;
        panelX = startX - 32f;
        panelY = startY - (rows - 1) * (buttonSize + verticalGap) - 24f;
        panelWidth = gridWidth + 64f;
        panelHeight = gridHeight + 48f;

        for (int index = 0; index < achievements.length; index++) {
            int column = index % columns;
            int row = index / columns;
            float x = startX + column * (buttonSize + horizontalGap);
            float y = startY - row * (buttonSize + verticalGap);
            buttons.add(new AchievementCardButton(achievements[index], x, y, buttonSize, iconSize));
        }
    }

    @Override
    protected void drawMenu(Batch batch) {
        if (hostedContent) {
            DesktopMenuStyle.shade(batch, panelX - 36, panelY - 36, panelWidth + 72, panelHeight + 72, 72, .65f);
            DesktopMenuStyle.corners(batch, panelX, panelY, panelWidth, panelHeight, DesktopMenuStyle.GOLD, .6f);
        } else drawMenuPanel(batch, panelX, panelY, panelWidth, panelHeight);
        drawMenuHeading(batch, "BADGES", 1175f);
        drawButtons(batch);
        Button selected = selectedMenuButton();
        if (selected instanceof AchievementCardButton) {
            String name = ((AchievementCardButton) selected).achievement.getName();
            FontHelper.FittedTextBlock label = FontHelper.getSingleton().fitOverlayText("badge-selection", name, name, 2.2f, 1800f, 36f);
            FontHelper.getSingleton().writeRaw(Color.WHITE, batch, label.size, (ConstantsHelper.SCREEN_WIDTH - label.width) / 2f, 1108f, label.text);
        }
    }

    @Override
    protected boolean handleBackAction() {
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
        return true;
    }

    private final class AchievementCardButton extends ActionButton {
        private final Achievement achievement;
        private final boolean unlocked;
        private final boolean implemented;
        private final GameSprite icon;

        private AchievementCardButton(Achievement achievement, float x, float y, float buttonSize, float iconSize) {
            super(x, y, buttonSize, buttonSize, "images/misc/black.png", "images/misc/black.png");
            enableUiPressFeedback();
            this.achievement = achievement;
            unlocked = AchievementManager.getInstance().isUnlocked(achievement);
            implemented = AchievementManager.getInstance().isImplemented(achievement);
            icon = new GameSprite(unlocked ? achievement.getAchievedIconPath() : achievement.getUnachievedIconPath(), iconSize, iconSize);
            icon.setPosition(x + (buttonSize - iconSize) / 2f, y + (buttonSize - iconSize) / 2f);
        }

        @Override
        public void draw(Batch batch) {
            if (hostedContent) {
                DesktopMenuStyle.fill(batch, x + 4, y + 4, getWidth() - 8, getHeight() - 8,
                        DesktopMenuStyle.EDGE, unlocked ? .20f : .08f);
                if (unlocked || selectedMenuButton() == this)
                    DesktopMenuStyle.corners(batch, x, y, getWidth(), getHeight(),
                            DesktopMenuStyle.GOLD, selectedMenuButton() == this ? 1f : .65f);
                DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
            }
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
