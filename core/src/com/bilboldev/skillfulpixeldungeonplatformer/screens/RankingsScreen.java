package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryWindow;

import java.util.ArrayList;

public class RankingsScreen extends MenuScreenBase {

    private static final float CARD_WIDTH = 108f;
    private static final float CARD_HEIGHT = 108f;
    private static final float CARD_HORIZONTAL_GAP = 26f;
    private static final float CARD_VERTICAL_GAP = 22f;
    private static final float CARD_PLATFORM_WIDTH = 96f;
    private static final float CARD_PLATFORM_HEIGHT = 40f;
    private static final float CARD_PLATFORM_Y = 6f;
    private static final float CARD_PREVIEW_SCALE = 0.8f;
    private static final float CARD_PREVIEW_STAND_OFFSET = 12f;
    private static final float CARD_PREVIEW_X_OFFSET = -10f;
    private static final float DEPTH_LABEL_RAISE_FACTOR = 0.6f;
    private static final float DEPTH_BADGE_ICON_SIZE = 38f;
    private static final float DEPTH_BADGE_GAP = 8f;
    private static final float DEPTH_BADGE_X_NUDGE = 10f;
    private static final int COLUMNS = 8;
    private static final int ROWS = 3;
    private static final int PAGE_SIZE = COLUMNS * ROWS;

    private final ArrayList<SaveHelper.RankingRunData> rankingRuns = new ArrayList<>();
    private String title = "Rankings";
    private float titleX;
    private float titleY;

    @Override
    protected void createMenuContent() {
        rankingRuns.clear();
        rankingRuns.addAll(SaveHelper.getInstance().loadRankings());
        rebuildPage();
    }

    private void rebuildPage() {
        buttons.clear();
        WindowHelper.getInstance().hideAll();
        addTitleBackButton();

        String localizedTitle = Messages.maybeTranslate(title);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 4), localizedTitle);
        titleX = (ConstantsHelper.SCREEN_WIDTH - glyphLayout.width) / 2f;
        titleY = ConstantsHelper.SCREEN_HEIGHT - 120f;

        float gridWidth = COLUMNS * CARD_WIDTH + (COLUMNS - 1) * CARD_HORIZONTAL_GAP;
        float gridHeight = ROWS * CARD_HEIGHT + (ROWS - 1) * CARD_VERTICAL_GAP;
        float startX = (ConstantsHelper.SCREEN_WIDTH - gridWidth) / 2f;
        float startY = (ConstantsHelper.SCREEN_HEIGHT + gridHeight) / 2f - CARD_HEIGHT;

        for (int cardIndex = 0; cardIndex < PAGE_SIZE; cardIndex++) {
            int column = cardIndex % COLUMNS;
            int row = cardIndex / COLUMNS;
            int rankingIndex = cardIndex;
            RankingCardButton button = new RankingCardButton(
                    rankingIndex < rankingRuns.size() ? rankingRuns.get(rankingIndex) : null,
                    startX + column * (CARD_WIDTH + CARD_HORIZONTAL_GAP),
                    startY - row * (CARD_HEIGHT + CARD_VERTICAL_GAP) + getRowYOffset(row));
            buttons.add(button);
        }
    }

    private float getRowYOffset(int row) {
        float heroHeight = ConstantsHelper.UNIT_DIMENSIONS * CARD_PREVIEW_SCALE;
        if (row == 0) {
            return heroHeight;
        }

        if (row == 1) {
            return heroHeight * 0.5f;
        }

        return 0f;
    }

    @Override
    protected void drawMenu(Batch batch) {
        FontHelper.getSingleton().writeWhite(batch, 4f, titleX, titleY, Messages.maybeTranslate(title));
        drawButtons(batch);
    }

    @Override
    protected boolean handleBackAction() {
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
        return true;
    }

    private class RankingCardButton extends ActionButton {

        private final SaveHelper.RankingRunData rankingRunData;
        private final GameSprite platform;
        private final GameFilm heroPreview;
        private final GameSprite depthIcon;
        private final String depthLabel;
        private final GlyphLayout depthLayout = new GlyphLayout();

        private RankingCardButton(SaveHelper.RankingRunData rankingRunData, float x, float y) {
            super(x, y, CARD_WIDTH, CARD_HEIGHT, "images/misc/black.png", "images/misc/black.png");
            this.rankingRunData = rankingRunData;
            if (rankingRunData != null) {
                enableUiPressFeedback();
            }

            platform = new GameSprite("images/tiles/kingdom/platform.png", CARD_PLATFORM_WIDTH, CARD_PLATFORM_HEIGHT);
            platform.setPosition(x + (CARD_WIDTH - CARD_PLATFORM_WIDTH) / 2f, y + CARD_PLATFORM_Y);

            if (rankingRunData == null) {
                depthLabel = null;
                depthIcon = null;
                heroPreview = null;
                return;
            }

            depthLabel = Integer.toString(rankingRunData.depthReached);
            depthIcon = new GameSprite("images/misc/depth.png", DEPTH_BADGE_ICON_SIZE, DEPTH_BADGE_ICON_SIZE);
            HeroClass heroClass = SaveHelper.getInstance().resolveRankingHeroClass(rankingRunData);
            heroPreview = new GameFilm(heroClass.getFilm(), ConstantsHelper.TILE, ConstantsHelper.TILE, 1f);
            heroPreview.clipSizeX = 12;
            heroPreview.clipSizeY = 15;
            heroPreview.yClipOffset = 1;
            heroPreview.tileX = 0;
                heroPreview.tileY = resolveArmorFilmRow(rankingRunData);
            heroPreview.setScale(CARD_PREVIEW_SCALE, CARD_PREVIEW_SCALE);

            float heroWidth = ConstantsHelper.UNIT_DIMENSIONS * heroPreview.getScaleX();
            heroPreview.setPosition(
                    platform.getX() + (CARD_PLATFORM_WIDTH - heroWidth) / 2f + CARD_PREVIEW_X_OFFSET,
                    platform.getY() + CARD_PLATFORM_HEIGHT - CARD_PREVIEW_STAND_OFFSET);
        }

        @Override
        public void draw(Batch batch) {
            drawSprite(batch, platform, 1f);
            if (heroPreview != null) {
                drawSprite(batch, heroPreview, 1f);

                depthLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, 2), depthLabel);
                float heroWidth = ConstantsHelper.UNIT_DIMENSIONS * heroPreview.getScaleX();
                float heroHeight = ConstantsHelper.UNIT_DIMENSIONS * heroPreview.getScaleY();
                float heroCenterX = heroPreview.getX() + heroWidth / 2f;
                float badgeBaseY = heroPreview.getY() + heroHeight + heroHeight * DEPTH_LABEL_RAISE_FACTOR;
                float badgeHeight = Math.max(depthIcon.getHeight(), depthLayout.height);
                float badgeWidth = depthIcon.getWidth() + DEPTH_BADGE_GAP + depthLayout.width;
                float iconX = heroCenterX - badgeWidth / 2f + DEPTH_BADGE_X_NUDGE;
                float iconY = badgeBaseY + (badgeHeight - depthIcon.getHeight()) / 2f;
                float textX = iconX + depthIcon.getWidth() + DEPTH_BADGE_GAP + DEPTH_BADGE_X_NUDGE;
                float textY = badgeBaseY + (badgeHeight + depthLayout.height) / 2f;

                depthIcon.setPosition(iconX, iconY);
                drawSprite(batch, depthIcon, 1f);
                FontHelper.getSingleton().write(Color.WHITE, batch, 2f, textX, textY, depthLabel);
            }
        }

        @Override
        public boolean canClick() {
            return rankingRunData != null && super.canClick();
        }

        @Override
        public void clicked() {
            WindowHelper.getInstance().hideAll();
            WindowHelper.getInstance().addWindow(new InventoryWindow(rankingRunData, 2000f, 1000f).build());
        }

        private void drawSprite(Batch batch, GameSprite sprite, float alphaMultiplier) {
            float previousAlpha = sprite.getAlpha();
            float alpha = alphaMultiplier * (isShowingPressFeedback() ? 0.75f : 1f);
            sprite.setAlpha(previousAlpha * alpha);
            sprite.draw(batch);
            sprite.setAlpha(previousAlpha);
        }
    }

    private int resolveArmorFilmRow(SaveHelper.RankingRunData rankingRunData) {
        if (rankingRunData == null || rankingRunData.inventorySaveData == null) {
            return 0;
        }

        SaveHelper.InventorySaveData inventorySaveData = rankingRunData.inventorySaveData;
        if (inventorySaveData.items != null) {
            for (SaveHelper.ItemSaveData itemSaveData : inventorySaveData.items) {
                if (!itemSaveData.equippedArmorSlot) {
                    continue;
                }

                Item item = SaveHelper.getInstance().createPreviewItem(itemSaveData);
                if (item instanceof Armor) {
                    return Math.max(0, ((Armor) item).getTier() - 1);
                }
            }
        }

        if (inventorySaveData.equippedArmorClassName != null && !inventorySaveData.equippedArmorClassName.isEmpty()) {
            SaveHelper.ItemSaveData armorSaveData = new SaveHelper.ItemSaveData();
            armorSaveData.className = inventorySaveData.equippedArmorClassName;
            armorSaveData.equippedArmorSlot = true;

            Item item = SaveHelper.getInstance().createPreviewItem(armorSaveData);
            if (item instanceof Armor) {
                return Math.max(0, ((Armor) item).getTier() - 1);
            }
        }

        return 0;
    }
}
