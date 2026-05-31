package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class AchievementInfoWindow extends Window {

    private static final float MIN_WIDTH = 1100f;
    private static final float MIN_HEIGHT = 520f;
    private static final float SCREEN_EDGE_MARGIN = 160f;
    private static final float ICON_SIZE = 140f;
    private static final float ICON_X = 100f;
    private static final float ICON_TOP_PADDING = 90f;
    private static final float TEXT_X = 300f;
    private static final float TEXT_RIGHT_PADDING = 120f;
    private static final float TITLE_BASELINE_OFFSET = 115f;
    private static final float TITLE_DESCRIPTION_GAP = 28f;
    private static final float TOP_CONTENT_PADDING = 90f;
    private static final float BOTTOM_CONTENT_PADDING = 95f;
    private static final float TITLE_SCALE = 3f;
    private static final float DESCRIPTION_SCALE = 2f;

    private final Achievement achievement;
    private final boolean unlocked;
    private final boolean implemented;
    private GameSprite icon;
    private String title;
    private String description;
    private float titleFontSize = TITLE_SCALE;
    private float descriptionFontSize = DESCRIPTION_SCALE;
    private float titleY;
    private float descriptionY;

    public AchievementInfoWindow(Achievement achievement, boolean unlocked, boolean implemented, float width, float height) {
        super(width, height);
        this.achievement = achievement;
        this.unlocked = unlocked;
        this.implemented = implemented;
    }

    @Override
    public Window build() {
        width = Math.max(MIN_WIDTH, Math.min(width, ConstantsHelper.SCREEN_WIDTH - SCREEN_EDGE_MARGIN));

        float textWidth = Math.max(320f, width - TEXT_X - TEXT_RIGHT_PADDING);
        float maxOverlayHeight = ConstantsHelper.SCREEN_HEIGHT * 0.9f;
        float titleScale = TITLE_SCALE;
        float descriptionScale = DESCRIPTION_SCALE;
        FontHelper.FittedTextBlock fittedTitle = FontHelper.getSingleton().fitMultilineToEnglishFootprint(
            achievement.getSourceName(),
            achievement.getName(),
            titleScale,
            textWidth);
        FontHelper.FittedTextBlock fittedDescription = FontHelper.getSingleton().fitMultilineToEnglishFootprint(
            achievement.getSourceUnlockCondition(),
            achievement.getUnlockCondition(),
            descriptionScale,
            textWidth);
        float contentHeight = Math.max(ICON_SIZE, fittedTitle.height + TITLE_DESCRIPTION_GAP + fittedDescription.height);
        while (TOP_CONTENT_PADDING + contentHeight + BOTTOM_CONTENT_PADDING > maxOverlayHeight
            && (titleScale > 1f || descriptionScale > 1f)) {
            titleScale = Math.max(1f, titleScale * 0.95f);
            descriptionScale = Math.max(1f, descriptionScale * 0.95f);
            fittedTitle = FontHelper.getSingleton().fitMultilineToEnglishFootprint(
                achievement.getSourceName(),
                achievement.getName(),
                titleScale,
                textWidth);
            fittedDescription = FontHelper.getSingleton().fitMultilineToEnglishFootprint(
                achievement.getSourceUnlockCondition(),
                achievement.getUnlockCondition(),
                descriptionScale,
                textWidth);
            contentHeight = Math.max(ICON_SIZE, fittedTitle.height + TITLE_DESCRIPTION_GAP + fittedDescription.height);
        }
        title = fittedTitle.text;
        titleFontSize = fittedTitle.size;
        description = fittedDescription.text;
        descriptionFontSize = fittedDescription.size;

        height = Math.min(maxOverlayHeight, Math.max(MIN_HEIGHT, TOP_CONTENT_PADDING + contentHeight + BOTTOM_CONTENT_PADDING));

        super.build();
        icon = new GameSprite(unlocked ? achievement.getAchievedIconPath() : achievement.getUnachievedIconPath(), ICON_SIZE, ICON_SIZE);
        icon.setPosition(x + ICON_X, y + height - ICON_TOP_PADDING - ICON_SIZE);

        titleY = y + height - TITLE_BASELINE_OFFSET;
        descriptionY = titleY - fittedTitle.height - TITLE_DESCRIPTION_GAP;
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (icon != null) {
            icon.draw(batch);
        }

        FontHelper.getSingleton().writeWhiteRaw(batch, titleFontSize, x + TEXT_X, titleY, title);
        FontHelper.getSingleton().writeRaw(Color.LIGHT_GRAY, batch, descriptionFontSize, x + TEXT_X, descriptionY, description);
    }
}