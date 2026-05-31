package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class FreeVersionAboutWindow extends Window {
    private static final float WINDOW_WIDTH = 1500f;
    private static final float WINDOW_HEIGHT = 785f;
    private static final float ABOUT_ICON_SIZE = 200f;
    private static final float ABOUT_ICON_RAISE_RATIO = 0.15f;
    private static final float DESCRIPTION_TEXT_X = 340f;
    private static final float DESCRIPTION_TEXT_RIGHT_PADDING = 120f;
    private static final float DESCRIPTION_TEXT_Y_OFFSET = 125f;
    private static final float ABOUT_TEXT_MAX_SIZE = 2.4f;
    private static final float ABOUT_TEXT_MIN_SIZE = 1.8f;
    private static final float ABOUT_TEXT_SIZE_STEP = 0.2f;
    private static final float LINK_TEXT_MAX_SIZE = 2.5f;
    private static final float LINK_TEXT_MIN_SIZE = 1.6f;
    private static final float LINK_TEXT_SIZE_STEP = 0.1f;
    private static final float LINK_MIN_BOTTOM_MARGIN = 60f;
    private static final float LINK_SIDE_MARGIN = 120f;
    private static final float LINK_SPACING = 48f;
    private static final float LINK_HIT_PADDING = 12f;
    private static final int OUTLINE_OFFSET = 1;
    private static final String LOCALIZATION_ASSET_PATH = "free/about.properties";
    private static final String STORE_URL = System.getProperty("spd.steamStoreUrl", "https://store.steampowered.com/");
    private static final String ENGLISH_TITLE = "A realtime platform adaption of Skillful Pixel Dungeon by BilbolDev";
    private static final String ENGLISH_BODY = "Thank you for playing the free version of Pixel Dungeon Platformer. You can play the entire game without any limitations. Please consider purchasing through Steam to support the dev. Supporter bonuses include access to Steam achievements, a supporter badge, and the option for Rat King to accompany you on your adventure as a harmless entertaining companion.";
    private static Properties localizationProperties;

    private final GameSprite aboutSprite;
    private GlyphLayout storeUrlLayout;
    private String descriptionText;
    private float descriptionFontSize = ABOUT_TEXT_MAX_SIZE;
    private float storeUrlFontSize = LINK_TEXT_MAX_SIZE;
    private float storeUrlX;
    private float storeUrlY;

    public FreeVersionAboutWindow() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        aboutSprite = new GameSprite("images/intro/about.png", ABOUT_ICON_SIZE, ABOUT_ICON_SIZE);
    }

    @Override
    public Window build() {
        super.build();
        aboutSprite.setPosition(x + 100f, y + height - 300f + ABOUT_ICON_SIZE * ABOUT_ICON_RAISE_RATIO);
        layoutStoreUrl();
        layoutDescriptionText(localizedDescription());
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        aboutSprite.draw(batch);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, descriptionFontSize, x + DESCRIPTION_TEXT_X, y + height - DESCRIPTION_TEXT_Y_OFFSET, descriptionText);
        drawEnglishText(Color.YELLOW, batch, storeUrlFontSize, storeUrlX, storeUrlY, STORE_URL);
    }

    @Override
    public boolean tap(float x, float y) {
        if (isStoreUrlHit(x, y)) {
            SoundHelper.GetSingleton().playUiClick();
            Gdx.net.openURI(STORE_URL);
            return true;
        }

        return super.tap(x, y);
    }

    private void layoutStoreUrl() {
        storeUrlFontSize = fitStoreUrlFontSize();
        storeUrlLayout = measure(Color.YELLOW, storeUrlFontSize, STORE_URL);
        storeUrlX = x + width / 2f - storeUrlLayout.width / 2f;
        storeUrlY = y + LINK_MIN_BOTTOM_MARGIN;
    }

    private void layoutDescriptionText(String sourceText) {
        String safeText = sourceText == null || sourceText.isEmpty()
                ? ENGLISH_TITLE + "\n\n" + ENGLISH_BODY
                : sourceText;
        float textTop = y + height - DESCRIPTION_TEXT_Y_OFFSET;
        float maxTextHeight = Math.max(60f, textTop - (storeUrlY + storeUrlLayout.height + LINK_SPACING));

        for (float size = ABOUT_TEXT_MAX_SIZE; size >= ABOUT_TEXT_MIN_SIZE - 0.001f; size -= ABOUT_TEXT_SIZE_STEP) {
            String wrappedText = UtilsHelper.multiLineRaw(safeText, size, getDescriptionWrapWidth());
            GlyphLayout layout = FontHelper.getSingleton().measure(Color.WHITE, size, wrappedText);
            descriptionText = wrappedText;
            descriptionFontSize = size;
            if (layout.height <= maxTextHeight + 0.5f) {
                return;
            }
        }
    }

    private float fitStoreUrlFontSize() {
        float maxWidth = Math.max(100f, width - LINK_SIDE_MARGIN * 2f);
        for (float size = LINK_TEXT_MAX_SIZE; size >= LINK_TEXT_MIN_SIZE - 0.001f; size -= LINK_TEXT_SIZE_STEP) {
            if (measure(Color.YELLOW, size, STORE_URL).width <= maxWidth + 0.5f) {
                return size;
            }
        }

        return LINK_TEXT_MIN_SIZE;
    }

    private GlyphLayout measure(Color color, float size, String text) {
        GlyphLayout layout = new GlyphLayout();
        String safeText = text == null ? "" : text;
        layout.setText(FontHelper.getSingleton().getEnglishFont(color, size, safeText), safeText);
        return layout;
    }

    private static String localizedDescription() {
        return resolveLocalizedValue("title", ENGLISH_TITLE)
                + "\n\n"
                + normalizeBodyText(resolveLocalizedValue("body", ENGLISH_BODY));
    }

    private static String resolveLocalizedValue(String field, String fallback) {
        String languageCode = Messages.lang().code();
        Properties properties = getLocalizationProperties();
        String localizedValue = properties.getProperty(languageCode + "." + field);
        if (localizedValue != null && !localizedValue.trim().isEmpty()) {
            return localizedValue;
        }

        String englishValue = properties.getProperty("en." + field);
        if (englishValue != null && !englishValue.trim().isEmpty()) {
            return englishValue;
        }

        return fallback;
    }

    private static Properties getLocalizationProperties() {
        if (localizationProperties != null) {
            return localizationProperties;
        }

        Properties loaded = new Properties();
        FileHandle file = Gdx.files.internal(LOCALIZATION_ASSET_PATH);
        if (file.exists()) {
            try {
                loaded.load(new StringReader(file.readString("UTF-8")));
            } catch (IOException ignored) {
            }
        }

        localizationProperties = loaded;
        return localizationProperties;
    }

    private static String normalizeBodyText(String text) {
        if (text == null || text.isEmpty()) {
            return ENGLISH_BODY;
        }

        String[] lines = UtilsHelper.normalizeLineBreaks(text).split("\n");
        StringBuilder builder = new StringBuilder();
        for (String bodyLine : lines) {
            String line = stripBulletPrefix(bodyLine == null ? "" : bodyLine.trim()).trim();
            if (line.isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(line);
        }

        return builder.length() == 0 ? ENGLISH_BODY : builder.toString();
    }

    private static String stripBulletPrefix(String line) {
        if (line == null) {
            return "";
        }

        String trimmed = line.trim();
        while (!trimmed.isEmpty()) {
            char first = trimmed.charAt(0);
            if (first != '-' && first != '—' && first != '•') {
                break;
            }
            trimmed = trimmed.substring(1).trim();
        }
        return trimmed;
    }

    private void drawEnglishText(Color color, Batch batch, float size, float textX, float textY, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float snappedX = Math.round(textX);
        float snappedY = Math.round(textY);
        Color outlineColor = new Color(0f, 0f, 0f, color.a);
        BitmapFont outlineFont = FontHelper.getSingleton().getEnglishFont(outlineColor, size, text);
        for (int offsetX = -OUTLINE_OFFSET; offsetX <= OUTLINE_OFFSET; offsetX++) {
            for (int offsetY = -OUTLINE_OFFSET; offsetY <= OUTLINE_OFFSET; offsetY++) {
                if (offsetX == 0 && offsetY == 0) {
                    continue;
                }

                outlineFont.draw(batch, text, snappedX + offsetX, snappedY + offsetY);
            }
        }

        FontHelper.getSingleton().getEnglishFont(color, size, text).draw(batch, text, snappedX, snappedY);
    }

    private float getDescriptionWrapWidth() {
        return Math.max(100f, width - DESCRIPTION_TEXT_X - DESCRIPTION_TEXT_RIGHT_PADDING);
    }

    private boolean isStoreUrlHit(float x, float y) {
        if (storeUrlLayout == null) {
            return false;
        }

        return x >= storeUrlX - LINK_HIT_PADDING
                && x <= storeUrlX + storeUrlLayout.width + LINK_HIT_PADDING
                && y >= storeUrlY - storeUrlLayout.height - LINK_HIT_PADDING
                && y <= storeUrlY + LINK_HIT_PADDING;
    }
}