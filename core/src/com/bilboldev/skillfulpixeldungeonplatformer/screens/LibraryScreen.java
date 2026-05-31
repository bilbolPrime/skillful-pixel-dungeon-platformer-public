package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MobLibraryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.library.LibraryEntry;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.MobInfoWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.Window;

import java.util.ArrayList;

public class LibraryScreen extends MenuScreenBase {
    private static final float ARROW_SCALE = 0.75f;
    private static final float ARROW_WIDTH = 150f * ARROW_SCALE;
    private static final float ARROW_HEIGHT = 130f * ARROW_SCALE;
    private static final float ARROW_VERTICAL_DROP_FACTOR = 0.6f;
    private static final int ARROW_SOURCE_X = 67;
    private static final int ARROW_SOURCE_Y = 58;
    private static final int ARROW_SOURCE_WIDTH = 15;
    private static final int ARROW_SOURCE_HEIGHT = 13;
    private static final float CARD_WIDTH_RATIO = 0.2f;
    private static final float CARD_BASE_HEIGHT = 120f;
    private static final float CARD_HEIGHT_MULTIPLIER = 1.5f;
    private static final float CARD_VERTICAL_GAP = 70f;
    private static final float CARD_VERTICAL_PADDING = 42f;
    private static final float CARD_OVERLAY_BORDER = 40f;
    private static final float CARD_INNER_PADDING = 10f;
    private static final float CARD_TEXT_GAP = 10f;
    private static final int MAX_COLUMNS = 4;
    private static final float GRID_PREVIEW_FRAME_RATE = 7f;

    private final ArrayList<MobCardButton> cardButtons = new ArrayList<>();
    private int themeIndex;
    private String libraryTitle = "Sewers";
    private float headerFontSize = 4f;
    private float headerX;
    private float headerY;

    @Override
    protected void createMenuContent() {
        RatKingSupportHelper.getInstance().applyConfiguredTitleTheme();
        rebuildThemePage();
    }

    @Override
    protected void actMenu(float delta) {
        for (MobCardButton cardButton : cardButtons) {
            cardButton.act(delta);
        }
    }

    private void rebuildThemePage() {
        buttons.clear();
        cardButtons.clear();
        WindowHelper.getInstance().hideAll();
        addTitleBackButton();

        ArrayList<LibraryEntry> entries = new ArrayList<LibraryEntry>(MobLibraryHelper.getInstance().getBestiary(themeIndex));
        libraryTitle = MobLibraryHelper.getInstance().getThemeName(themeIndex);
        GlyphLayout glyphLayout = new GlyphLayout();
        float tallestContent = 0f;
        for (LibraryEntry entry : entries) {
            GameFilm preview = safePreview(entry);
            float previewHeight = getPreviewDrawHeight(preview);
            tallestContent = Math.max(tallestContent, previewHeight);
        }

        float cardWidth = ConstantsHelper.SCREEN_WIDTH * CARD_WIDTH_RATIO;
        float cardHeight = Math.max(CARD_BASE_HEIGHT * CARD_HEIGHT_MULTIPLIER, tallestContent + CARD_VERTICAL_PADDING * 2f);
        float spacingY = cardHeight + CARD_VERTICAL_GAP;
        int columns = Math.min(MAX_COLUMNS, Math.max(1, entries.size()));
        int rows = (entries.size() + columns - 1) / columns;
        float horizontalGap = (ConstantsHelper.SCREEN_WIDTH - columns * cardWidth) / (columns + 1f);
        float spacingX = cardWidth + horizontalGap;
        float gridHeight = cardHeight + (rows - 1) * spacingY;
        float startX = horizontalGap;
        float startY = (ConstantsHelper.SCREEN_HEIGHT + gridHeight) / 2f - cardHeight;

        String localizedHeader = Messages.maybeTranslate(libraryTitle);
        float headerWidthLimit = ConstantsHelper.SCREEN_WIDTH - ARROW_WIDTH * 2f - 160f;
        headerFontSize = FontHelper.getSingleton().fitSize(localizedHeader, 4f, headerWidthLimit, Float.POSITIVE_INFINITY);
        BitmapFont titleFont = FontHelper.getSingleton().getFont(Color.WHITE, headerFontSize, localizedHeader);
        glyphLayout.setText(titleFont, localizedHeader);
        headerX = (ConstantsHelper.SCREEN_WIDTH - glyphLayout.width) / 2f;
        headerY = startY + cardHeight + titleFont.getCapHeight() + 90f;
        float arrowY = headerY + glyphLayout.height / 2f - ARROW_HEIGHT / 2f - ARROW_HEIGHT * ARROW_VERTICAL_DROP_FACTOR;

        buttons.add(new ThemeArrowButton("<", 0f, arrowY, -1));
        buttons.add(new ThemeArrowButton(">", ConstantsHelper.SCREEN_WIDTH - ARROW_WIDTH, arrowY, 1));

        for (int index = 0; index < entries.size(); index++) {
            LibraryEntry entry = entries.get(index);
            int column = index % columns;
            int row = index / columns;
            try {
                MobCardButton button = new MobCardButton(entry, startX + column * spacingX, startY - row * spacingY, cardWidth, cardHeight);
                cardButtons.add(button);
                buttons.add(button);
            } catch (Throwable throwable) {
                logLibraryEntryFailure("grid", entry, throwable);
            }
        }
    }

    @Override
    protected void drawMenu(Batch batch) {
        FontHelper.getSingleton().writeWhiteRaw(batch, headerFontSize, headerX, headerY, Messages.maybeTranslate(libraryTitle));
        drawButtons(batch);
    }

    @Override
    protected boolean handleBackAction() {
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
        return true;
    }

    private float getPreviewDrawWidth(GameFilm preview) {
        return preview == null ? 0f : ConstantsHelper.UNIT_DIMENSIONS * Math.abs(preview.getScaleX());
    }

    private float getPreviewDrawHeight(GameFilm preview) {
        return preview == null ? 0f : ConstantsHelper.UNIT_DIMENSIONS * Math.abs(preview.getScaleY());
    }

    private class MobCardButton extends ActionButton {

        private final LibraryEntry entry;
        private final float cardWidth;
        private final float cardHeight;
        private final GlyphLayout titleLayout = new GlyphLayout();
        private final GameFilm preview;
        private final int[] frames;
        private final ArrayList<GameSprite> backgroundSprites;
        private final String localizedTitle;
        private final float titleFontSize;
        private final float textLeft;
        private final float textWidth;
        private float frameAt;
        private boolean previewFaulted;
        private boolean titleFaulted;

        private MobCardButton(LibraryEntry entry, float x, float y, float width, float height) {
            super(x, y, width, height, "images/misc/transparent.png", "images/misc/transparent.png");
            this.entry = entry;
            this.cardWidth = width;
            this.cardHeight = height;
            this.backgroundSprites = Window.createOverlayBackgroundSprites(x, y, width, height, CARD_OVERLAY_BORDER, CARD_OVERLAY_BORDER);
            this.localizedTitle = safeLocalizedEntryName(entry);
            enableUiPressFeedback();

            preview = safePreview(entry);
            frames = entry instanceof Mob ? ((Mob) entry).getLibraryGridFrames() : null;
            float innerLeft = CARD_OVERLAY_BORDER + CARD_INNER_PADDING;
            float innerRight = cardWidth - CARD_OVERLAY_BORDER - CARD_INNER_PADDING;
            if (preview != null) {
                float previewWidth = getPreviewDrawWidth(preview);
                float previewHeight = getPreviewDrawHeight(preview);
                float previewX = innerLeft;
                float previewY = (cardHeight - previewHeight) / 2f;
                preview.setPosition(previewX, previewY);
                preview.faceRight(true);
                addGameSprite(preview);
            }

            float previewRight = preview == null ? innerLeft : innerLeft + getPreviewDrawWidth(preview);
            textLeft = preview == null ? innerLeft : previewRight + CARD_TEXT_GAP;
            textWidth = Math.max(60f, innerRight - textLeft);
            titleFontSize = FontHelper.getSingleton().fitSize(localizedTitle, 3f, textWidth, cardHeight - CARD_INNER_PADDING * 2f);
        }

        private void act(float delta) {
            if (preview == null || frames == null || frames.length == 0) {
                return;
            }

            frameAt += delta * GRID_PREVIEW_FRAME_RATE;
            preview.tileX = frames[((int) frameAt) % frames.length];
        }

        @Override
        public void draw(Batch batch) {
            Color previousColor = new Color(batch.getColor());
            if (isShowingPressFeedback()) {
                batch.setColor(previousColor.r * 1.2f, previousColor.g * 1.2f, previousColor.b * 1.2f, previousColor.a);
            }
            for (GameSprite backgroundSprite : backgroundSprites) {
                backgroundSprite.draw(batch);
            }
            batch.setColor(previousColor);

            try {
                super.draw(batch);
            } catch (Throwable throwable) {
                if (!previewFaulted) {
                    previewFaulted = true;
                    clearSprites();
                    logLibraryEntryFailure("draw", entry, throwable);
                }
            }

            try {
                BitmapFont titleFont = FontHelper.getSingleton().getFont(Color.WHITE, titleFontSize, localizedTitle);
                titleLayout.setText(titleFont, localizedTitle);
                float textY = y + (cardHeight + titleLayout.height) / 2f;
                FontHelper.getSingleton().writeWhiteRaw(batch, titleFontSize, x + textLeft, textY, localizedTitle);
            } catch (Throwable throwable) {
                if (!titleFaulted) {
                    titleFaulted = true;
                    logLibraryEntryFailure("title-draw", entry, throwable);
                }
            }
        }

        @Override
        public void click() {
            WindowHelper.getInstance().hideAll();
            try {
                WindowHelper.getInstance().addWindow(new MobInfoWindow(entry, 1500, 560).build());
            } catch (Throwable throwable) {
                logLibraryEntryFailure("details", entry, throwable);
                WindowHelper.getInstance().addWindow(1200f, 160f, "This library entry could not be opened on this device.");
            }
        }
    }

    private String localizedEntryName(LibraryEntry entry) {
        if (entry instanceof Mob) {
            return ((Mob) entry).getResolvedLibraryName();
        }

        return Messages.maybeTranslate(entry.getLibraryName());
    }

    private GameFilm safePreview(LibraryEntry entry) {
        if (entry == null) {
            return null;
        }

        try {
            return entry.getLibraryPreview();
        } catch (Throwable throwable) {
            logLibraryEntryFailure("preview", entry, throwable);
            return null;
        }
    }

    private String safeLocalizedEntryName(LibraryEntry entry) {
        if (entry == null) {
            return "";
        }

        try {
            return localizedEntryName(entry);
        } catch (Throwable throwable) {
            logLibraryEntryFailure("title", entry, throwable);
            return entry.getClass().getSimpleName();
        }
    }

    private void logLibraryEntryFailure(String surface, LibraryEntry entry, Throwable throwable) {
        if (Gdx.app == null || entry == null) {
            return;
        }

        Gdx.app.log("LibraryScreen", "Failed to load library " + surface + " for " + entry.getClass().getSimpleName(), throwable);
    }

    private class ThemeArrowButton extends ActionButton {
        private final int direction;
        private final TextureRegion arrowRegion;

        private ThemeArrowButton(String label, float x, float y, int direction) {
            super(x, y, ARROW_WIDTH, ARROW_HEIGHT, "images/misc/transparent.png", "images/misc/transparent.png");
            this.direction = direction;
            enableUiPressFeedback();

            arrowRegion = new TextureRegion(TextureHelper.GetSingleton().getTexture("images/menu/arrow.png"),
                    ARROW_SOURCE_X,
                    ARROW_SOURCE_Y,
                    ARROW_SOURCE_WIDTH,
                    ARROW_SOURCE_HEIGHT);
            if (direction < 0) {
                arrowRegion.flip(true, false);
            }
        }

        @Override
        public boolean canClick() {
            int targetIndex = themeIndex + direction;
            return super.canClick() && targetIndex >= 0 && targetIndex < MobLibraryHelper.getInstance().getThemeCount();
        }

        @Override
        public void draw(Batch batch) {
            super.draw(batch);

            Color previousColor = new Color(batch.getColor());
            if (!canClick()) {
                batch.setColor(previousColor.r, previousColor.g, previousColor.b, previousColor.a * 0.35f);
            } else if (isShowingPressFeedback()) {
                batch.setColor(previousColor.r * 1.2f, previousColor.g * 1.2f, previousColor.b * 1.2f, previousColor.a);
            }

            batch.draw(arrowRegion, x, y, ARROW_WIDTH, ARROW_HEIGHT);
            batch.setColor(previousColor);
        }

        @Override
        public void clicked() {
            themeIndex += direction;
            rebuildThemePage();
        }
    }
}