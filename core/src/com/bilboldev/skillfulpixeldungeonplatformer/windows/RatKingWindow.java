package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;

import java.util.ArrayList;

public class RatKingWindow extends Window {
    private static final float WINDOW_WIDTH = 1500f;
    private static final float WINDOW_HEIGHT = 980f;
    private static final float OUTER_PADDING = 90f;
    private static final float LEFT_COLUMN_WIDTH = 390f;
    private static final float LEFT_TO_RIGHT_GAP = 90f;
    private static final float PREVIEW_SCALE = 3.225f;
    private static final float PREVIEW_TILE_SIZE = 128f;
    private static final float PREVIEW_PLATFORM_GAP = 64f;
    private static final float PREVIEW_HORIZONTAL_OFFSET_RATIO = 0.25f;
    private static final float PREVIEW_PLATFORM_X_OFFSET_TILES = 0.33f;
    private static final float PREVIEW_EXTRA_RATKING_X_OFFSET_TILES = 0.35f;
    private static final float PREVIEW_VERTICAL_OFFSET_TILES = 0.61f;
    private static final int PREVIEW_PLATFORM_TILE_COUNT = 3;
    private static final float CHECKBOX_VERTICAL_OFFSET_RATIO = 0.15f;
    private static final float PREVIEW_EXTRA_RATKING_PIXEL_OFFSET_X = 0f;
    private static final float ROW_HEIGHT = 92f;
    private static final float ROW_GAP = 18f;
    private static final float THEME_COLUMN_GAP = 18f;
    private static final int[] PREVIEW_FRAMES = new int[]{0, 1};
    private static final float CONTRACT_TEXT_SIZE = 2f;

    private final ArrayList<ActionButton> buttons = new ArrayList<>();
    private final ArrayList<ThemeOptionButton> themeButtons = new ArrayList<>();
    private ActionButton pressedButton;
    private PauseMenuRowButton companionButton;
    private FireworksOptionButton fireworksButton;
    private GameFilm preview;
    private final ArrayList<GameSprite> previewFloors = new ArrayList<>();
    private float previewFrameAt;
    private float portraitPanelX;
    private float portraitPanelY;
    private float portraitPanelWidth;
    private float previewBoxX;
    private float previewBoxY;
    private float previewBoxWidth;
    private float previewBaseX;
    private float previewBaseY;
    private float themePanelX;
    private float themePanelY;
    private float themePanelWidth;
    private float themePanelHeight;
    private float contractTextX;
    private float contractTextY;
    private float contractTextWidth;
    private boolean supporterOfferActive;

    public RatKingWindow() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public Window build() {
        super.build();

        buttons.clear();
        themeButtons.clear();
        companionButton = null;
        fireworksButton = null;
        pressedButton = null;
        previewFrameAt = 0f;
        supporterOfferActive = SkillfulPixelDungeonPlatformer.getPlatformProfile().isFreeVersion();

        layoutPanels();

        if (supporterOfferActive) {
            companionButton = new PauseMenuRowButton(portraitPanelX,
                    y + 110f,
                    width - OUTER_PADDING * 2f,
                    ROW_HEIGHT) {
                @Override
                public void clicked() {
                    Window offer = new FreeVersionAboutWindow().build();
                    if (SkillfulPixelDungeonPlatformer.getActiveScreen() instanceof TitleScreen
                            && ((TitleScreen) SkillfulPixelDungeonPlatformer.getActiveScreen()).usesDesktopMenuScenes())
                        WindowHelper.getInstance().addWindow(offer);
                    else WindowHelper.getInstance().replaceWindow(offer);
                }
            }.setCenteredText(Messages.get("custom.ui.free_version.supporter"));
        }
        else {
            companionButton = new PauseMenuRowButton(portraitPanelX,
                    y + 110f,
                    width - OUTER_PADDING * 2f,
                    ROW_HEIGHT) {
                @Override
                public void clicked() {
                    RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
                    helper.setCompanionEnabled(!helper.isCompanionEnabled());
                    refreshButtonStates();
                }
            }.setCheckboxRow(Messages.get("custom.generated.signed_c5d4d41342"), RatKingSupportHelper.getInstance().isCompanionEnabled())
                .setCheckboxVerticalOffsetRatio(CHECKBOX_VERTICAL_OFFSET_RATIO);
        }
        buttons.add(companionButton);

        float innerThemeWidth = themePanelWidth;
        float narrowButtonWidth = (innerThemeWidth - THEME_COLUMN_GAP) / 2f;
        float buttonY = themePanelY + themePanelHeight - 150f - ROW_HEIGHT;
        float leftButtonX = themePanelX;
        float rightButtonX = leftButtonX + narrowButtonWidth + THEME_COLUMN_GAP;
        TitleThemeOption[] options = TitleThemeOption.values();
        for (int i = 0; i + 1 < options.length - 1; i += 2) {
            ThemeOptionButton leftButton = new ThemeOptionButton(leftButtonX, buttonY, narrowButtonWidth, ROW_HEIGHT, options[i]);
            ThemeOptionButton rightButton = new ThemeOptionButton(rightButtonX, buttonY, narrowButtonWidth, ROW_HEIGHT, options[i + 1]);
            themeButtons.add(leftButton);
            themeButtons.add(rightButton);
            buttons.add(leftButton);
            buttons.add(rightButton);
            buttonY -= ROW_HEIGHT + ROW_GAP;
        }

        ThemeOptionButton hallsButton = new ThemeOptionButton(leftButtonX, buttonY, narrowButtonWidth, ROW_HEIGHT, options[options.length - 1]);
        fireworksButton = new FireworksOptionButton(rightButtonX, buttonY, narrowButtonWidth, ROW_HEIGHT);
        themeButtons.add(hallsButton);
        buttons.add(hallsButton);
        buttons.add(fireworksButton);

        layoutPreviewAndContract();

        preview = new GameFilm("images/units/ratking/ratking.png", 128, 16, 1f);
        preview.clipSizeX = 16;
        preview.clipSizeY = 16;
        preview.setScale(PREVIEW_SCALE, PREVIEW_SCALE);
        preview.faceRight(true);
        preview.setPosition(previewBaseX, previewBaseY);

        refreshButtonStates();
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        previewFrameAt += Gdx.graphics.getDeltaTime() * 1f;
        preview.tileX = PREVIEW_FRAMES[((int) previewFrameAt) % PREVIEW_FRAMES.length];
        preview.setPosition(previewBaseX, previewBaseY);

        for (GameSprite previewFloor : previewFloors) {
            previewFloor.draw(batch);
        }
        preview.draw(batch);

        drawLabels(batch);
        drawContractSection(batch);

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

    private void layoutPanels() {
        portraitPanelX = x + OUTER_PADDING;
        portraitPanelY = y + OUTER_PADDING;
        portraitPanelWidth = LEFT_COLUMN_WIDTH;

        previewBoxX = portraitPanelX;
        previewBoxY = y + 220f;
        previewBoxWidth = portraitPanelWidth;

        themePanelX = portraitPanelX + portraitPanelWidth + LEFT_TO_RIGHT_GAP;
        themePanelY = portraitPanelY;
        themePanelWidth = x + width - OUTER_PADDING - themePanelX;
        themePanelHeight = height - OUTER_PADDING * 2f;
    }

    private void layoutPreviewAndContract() {
        float highestThemeButtonY = Float.NEGATIVE_INFINITY;
        float lowestThemeButtonY = Float.POSITIVE_INFINITY;
        for (ThemeOptionButton themeButton : themeButtons) {
            highestThemeButtonY = Math.max(highestThemeButtonY, themeButton.y);
            lowestThemeButtonY = Math.min(lowestThemeButtonY, themeButton.y);
        }
        if (fireworksButton != null) {
            highestThemeButtonY = Math.max(highestThemeButtonY, fireworksButton.y);
            lowestThemeButtonY = Math.min(lowestThemeButtonY, fireworksButton.y);
        }

        float previewHeight = ConstantsHelper.UNIT_DIMENSIONS * PREVIEW_SCALE;
        float previewWidth = ConstantsHelper.UNIT_DIMENSIONS * PREVIEW_SCALE;
        float themeClusterCenterY = ((highestThemeButtonY + ROW_HEIGHT) + lowestThemeButtonY) / 2f;
        float previewTileOffsetX = PREVIEW_TILE_SIZE * PREVIEW_PLATFORM_X_OFFSET_TILES;
        float extraRatKingOffsetX = PREVIEW_TILE_SIZE * PREVIEW_EXTRA_RATKING_X_OFFSET_TILES;
        float previewOffsetY = PREVIEW_TILE_SIZE * PREVIEW_VERTICAL_OFFSET_TILES;
        previewBaseX = previewBoxX + previewBoxWidth / 2f - previewWidth / 2f + previewWidth * PREVIEW_HORIZONTAL_OFFSET_RATIO + previewTileOffsetX + extraRatKingOffsetX + PREVIEW_EXTRA_RATKING_PIXEL_OFFSET_X;
        previewBaseY = themeClusterCenterY - previewHeight / 2f + previewOffsetY;

        contractTextX = x + OUTER_PADDING;
        contractTextWidth = width - OUTER_PADDING * 2f;
        contractTextY = lowestThemeButtonY - 95f;
    }

    private void refreshButtonStates() {
        RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
        if (companionButton != null && !supporterOfferActive) {
            companionButton.setChecked(helper.isCompanionEnabled());
        }

        refreshPreviewFloor();
        for (ThemeOptionButton themeButton : themeButtons) {
            themeButton.refreshState();
        }
        if (fireworksButton != null) {
            fireworksButton.refreshState();
        }
    }

    private void refreshPreviewFloor() {
        RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
        String floorSprite = helper.getSelectedTitleTheme().createTheme().getFloor().spriteString;
        String tilesSprite = floorSprite.replace("/floor.png", "/tiles.png");
        float floorY = previewBaseY - PREVIEW_PLATFORM_GAP;
        float floorStartX = previewBoxX + previewBoxWidth / 2f - PREVIEW_TILE_SIZE * (PREVIEW_PLATFORM_TILE_COUNT / 2f)
                + PREVIEW_TILE_SIZE * PREVIEW_PLATFORM_X_OFFSET_TILES - (PREVIEW_TILE_SIZE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        TextureRegion tile = new TextureRegion(TextureHelper.GetSingleton().getTexture(tilesSprite), 13 * 16, 2 * 16, 16, 16);

        previewFloors.clear();
        for (int i = 0; i < PREVIEW_PLATFORM_TILE_COUNT; i++) {

            GameSprite previewFloor = new GameSprite(new Sprite(tile), PREVIEW_TILE_SIZE, PREVIEW_TILE_SIZE);
            previewFloor.setPosition(floorStartX + i * PREVIEW_TILE_SIZE, floorY);
            previewFloors.add(previewFloor);
        }
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }

    private void drawLabels(Batch batch) {
        String ratKingTitle = Messages.get("custom.title.rat_king");
        GlyphLayout titleLayout = new GlyphLayout(FontHelper.getSingleton().getFont(Color.WHITE, 4f), ratKingTitle);
        float titleX = previewBoxX + previewBoxWidth / 2f - titleLayout.width / 2f + 20f;
        FontHelper.getSingleton().writeWhite(batch, 4f, titleX, y + height - 140f, ratKingTitle);
        FontHelper.getSingleton().writeWhite(batch, 4f, themePanelX, y + height - 140f, Messages.get("custom.generated.title_screen_theme_fx_412717b278"));
    }

    private void drawContractSection(Batch batch) {
        String wrappedText = UtilsHelper.multiLine(Messages.get("custom.generated.rat_king_contract_5c72c2fef4"), (int) CONTRACT_TEXT_SIZE, contractTextWidth);
        FontHelper.getSingleton().writeWhiteRaw(batch, CONTRACT_TEXT_SIZE, contractTextX, contractTextY, wrappedText);
    }

    private class ThemeOptionButton extends PauseMenuRowButton {
        private final TitleThemeOption option;

        private ThemeOptionButton(float x, float y, float width, float height, TitleThemeOption option) {
            super(x, y, width, height);
            this.option = option;
            setCheckboxVerticalOffsetRatio(CHECKBOX_VERTICAL_OFFSET_RATIO);
            refreshState();
        }

        @Override
        public boolean canClick() {
            return super.canClick() && RatKingSupportHelper.getInstance().isThemeUnlocked(option);
        }

        @Override
        public void clicked() {
            RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
            helper.setSelectedTitleTheme(option);
            helper.applyConfiguredTitleTheme();
            refreshButtonStates();
        }

        private void refreshState() {
            RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
            if (!helper.isThemeUnlocked(option)) {
                setCenteredText(option.getUnlockLabel()).setCenteredTextColor(Color.GRAY);
                return;
            }

            setCheckboxRow(option.getDisplayName(), helper.getSelectedTitleTheme() == option);
        }
    }

    private class FireworksOptionButton extends PauseMenuRowButton {
        private FireworksOptionButton(float x, float y, float width, float height) {
            super(x, y, width, height);
            setCheckboxVerticalOffsetRatio(CHECKBOX_VERTICAL_OFFSET_RATIO);
            refreshState();
        }

        @Override
        public boolean canClick() {
            return super.canClick() && RatKingSupportHelper.getInstance().isFireworksUnlocked();
        }

        @Override
        public void clicked() {
            RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
            helper.setFireworksEnabled(!helper.isFireworksEnabled());
            refreshButtonStates();
        }

        private void refreshState() {
            RatKingSupportHelper helper = RatKingSupportHelper.getInstance();
            if (!helper.isFireworksUnlocked()) {
                setCenteredText(Messages.get("custom.generated.beat_game_c7832852d1")).setCenteredTextColor(Color.GRAY);
                return;
            }

            setCheckboxRow(Messages.get("custom.generated.fireworks_a85f18261c"), helper.isFireworksEnabled());
        }
    }
}
