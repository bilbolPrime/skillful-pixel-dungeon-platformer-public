package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

public class StoryWindow extends Window {
    private static final float CHROME_SCALE = 8f;
    private static final float MIN_WIDTH = 840f;
    private static final float MIN_HEIGHT = 360f;
    private static final float MAX_TEXT_WIDTH = 960f;
    private static final float MAX_HEIGHT = 760f;
    private static final float CHROME_HEIGHT_SCALE = 1.3f;
    private static final float CHROME_Y_ANCHOR_SCALE = 1.1f;
    private static final float CHROME_RAISE_SCALE = 0.0f;
    private static final float HORIZONTAL_PADDING = 120f;
    private static final float TOP_PADDING = 120f;
    private static final float BOTTOM_PADDING = 110f;
    private static final float FONT_SIZE = 3f;
    private static final Color TEXT_COLOR = Color.WHITE;

    private final NinePatch chromePatch;
    private final String storyText;

    private String wrappedText;
    private float fittedFontSize = FONT_SIZE;
    private float textX;
    private float textY;

    public StoryWindow(String storyText) {
        super(MIN_WIDTH, MIN_HEIGHT);
        this.storyText = storyText == null ? "" : storyText;

        Texture chromeTexture = TextureHelper.GetSingleton().getTexture("images/menu/chrome.png");
        TextureRegion scrollRegion = new TextureRegion(chromeTexture, 32, 32, 32, 32);
        chromePatch = new NinePatch(scrollRegion, 5, 11, 5, 11);
        chromePatch.scale(CHROME_SCALE, CHROME_SCALE);
    }

    @Override
    public Window build() {
        float maxOverlayHeight = Math.min(MAX_HEIGHT, ConstantsHelper.SCREEN_HEIGHT * 0.9f);
        float maxBaseHeight = maxOverlayHeight / CHROME_HEIGHT_SCALE;
        FontHelper.FittedTextBlock fittedText = FontHelper.getSingleton().fitOverlayText(
            getClass().getName() + ":story",
            storyText,
            Messages.maybeTranslate(storyText),
            FONT_SIZE,
            MAX_TEXT_WIDTH,
            Math.max(60f, maxBaseHeight - TOP_PADDING - BOTTOM_PADDING));
        wrappedText = fittedText.text;
        fittedFontSize = fittedText.size;

        float baseHeight = Math.max(MIN_HEIGHT,
            Math.min(maxBaseHeight, fittedText.height + TOP_PADDING + BOTTOM_PADDING));
        float anchoredHeight = Math.min(maxOverlayHeight, baseHeight * CHROME_Y_ANCHOR_SCALE);

        width = Math.max(MIN_WIDTH,
            Math.min(ConstantsHelper.SCREEN_WIDTH - 320f, fittedText.englishWidth + HORIZONTAL_PADDING * 2f));
        height = Math.min(maxOverlayHeight, baseHeight * CHROME_HEIGHT_SCALE);

        x = (ConstantsHelper.SCREEN_WIDTH - width) / 2f;
        y = (ConstantsHelper.SCREEN_HEIGHT - anchoredHeight) / 2f - height * CHROME_RAISE_SCALE;
        textX = x + HORIZONTAL_PADDING;
        textY = (ConstantsHelper.SCREEN_HEIGHT - baseHeight) / 2f + baseHeight - TOP_PADDING;

        return this;
    }

    @Override
    public void draw(Batch batch) {
        chromePatch.draw(batch, x, y, width, height);
        FontHelper.getSingleton().writeRaw(TEXT_COLOR, batch, fittedFontSize, textX, textY, wrappedText);
    }

    @Override
    public boolean click(float x, float y) {
        hide();
        return true;
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        return true;
    }
}