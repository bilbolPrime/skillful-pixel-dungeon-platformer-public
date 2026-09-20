package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class FreeVersionAboutWindow extends Window {
    private static final float WINDOW_WIDTH = 1500f;
    private static final float WINDOW_HEIGHT = 900f;
    private static final float ABOUT_ICON_SIZE = 144f;
    private static final float PADDING = 112f;
    private static final float HEADER_TEXT_OFFSET = 292f;
    private static final float BODY_TOP_OFFSET = 308f;
    private static final float BUTTON_HEIGHT = 92f;
    private static final float BUTTON_GAP = 48f;
    private static final String LOCALIZATION_ASSET_PATH = "free/about.properties";
    private static final String ANDROID_STORE_URL = "https://store.steampowered.com/app/4725620/Pixel_Dungeon_Platformer/?utm_source=android&utm_medium=free_port&utm_campaign=steam_conversion";
    private static final String DESKTOP_STORE_URL = "https://store.steampowered.com/app/4725620/Pixel_Dungeon_Platformer/?utm_source=desktop&utm_medium=free_port&utm_campaign=steam_conversion";
    private static final String ENGLISH_TITLE = "A realtime platform adaption of Skillful Pixel Dungeon by BilbolDev";
    private static final String ENGLISH_BODY = "Thank you for playing the free version of Pixel Dungeon Platformer. You can play the entire game without any limitations. Please consider purchasing through Steam to support the dev. Supporter bonuses in the Steam version include access to Steam achievements, Steam Cloud saves, a supporter badge, and the option for Rat King to accompany you on your adventure as a harmless entertaining companion.";
    private static Properties localizationProperties;

    private final GameSprite aboutSprite;
    private FontHelper.FittedTextBlock heading, body;
    private PauseMenuRowButton storeButton;
    private boolean storePressed;

    public FreeVersionAboutWindow() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        aboutSprite = new GameSprite("images/intro/about.png", ABOUT_ICON_SIZE, ABOUT_ICON_SIZE);
    }

    @Override
    public Window build() {
        super.build();
        aboutSprite.setPosition(x + PADDING, y + height - PADDING - ABOUT_ICON_SIZE);
        heading = FontHelper.getSingleton().fitLabelToBounds(resolveLocalizedValue("title", ENGLISH_TITLE),
                2.8f, width - HEADER_TEXT_OFFSET - PADDING, ABOUT_ICON_SIZE);
        float bodyHeight = height - BODY_TOP_OFFSET - PADDING - BUTTON_HEIGHT - BUTTON_GAP;
        body = FontHelper.getSingleton().fitLabelToBounds(normalizeBodyText(resolveLocalizedValue("body", ENGLISH_BODY)),
                2.6f, width - PADDING * 2, bodyHeight);
        storeButton = new PauseMenuRowButton(x + (width - 680f) / 2f, y + PADDING, 680f, BUTTON_HEIGHT) {
            @Override public void clicked() {
                Gdx.net.openURI(SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
                        ? ANDROID_STORE_URL : DESKTOP_STORE_URL);
            }
        }.setCenteredText(Messages.get("custom.ui.free_version.steam"));
        storePressed = false;
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        aboutSprite.draw(batch);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, heading.size,
                x + HEADER_TEXT_OFFSET, y + height - PADDING - (ABOUT_ICON_SIZE - heading.height) / 2, heading.text);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, body.size,
                x + PADDING, y + height - BODY_TOP_OFFSET, body.text);
        storeButton.draw(batch);
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        cancelPointerInput();
        storePressed = storeButton.isHitProjected(x, y);
        if (storePressed) storeButton.pressDown();
        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        if (storePressed) {
            storePressed = false;
            storeButton.releasePress();
            if (storeButton.isHitProjected(x, y)) storeButton.click();
            else storeButton.cancelPress();
            return true;
        }

        return super.tap(x, y);
    }

    @Override
    public void cancelPointerInput() {
        storePressed = false;
        if (storeButton != null) storeButton.cancelPress();
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

}
