package com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;

import java.util.ArrayList;

public class RedButton extends ActionButton {
    private static final float MIN_HEIGHT = 90f;
    private static final float TEXT_HORIZONTAL_PADDING = 50f;
    private static final float TEXT_VERTICAL_PADDING = 24f;
    private static final int TEXT_SIZE = 3;

    String text;
    float offsetX;
    private Color textColor = new Color(Color.WHITE);
    private Color backgroundTint = new Color(Color.WHITE);

    ArrayList<GameSprite> gameSprites;
    public RedButton(float x, float y, float width, float height) {
        super(x, y, width, height, "images/misc/transparent.png", "images/misc/transparent.png");
        enableUiPressFeedback();
        gameSprites = createBackgroundSprites(x, y, width, height);
    }

    public static ArrayList<GameSprite> createBackgroundSprites(float x, float y, float width, float height) {
        ArrayList<GameSprite> backgroundSprites = new ArrayList<>();

        GameSprite gs = new GameSprite("images/buttons/red-button/top.png", width - 20, 20);
        gs.setPosition(x + 20, y + height - 20);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/top-left.png", 20, 20);
        gs.setPosition(x, y + height - 20);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/top-right.png", 20, 20);
        gs.setPosition(x + width - 20, y + height - 20);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/left.png", 20, height - 40);
        gs.setPosition(x, y + 20);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/right.png", 20, height - 40);
        gs.setPosition(x + width - 20, y + 20);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/body.png", width - 40, height - 40);
        gs.setPosition(x + 20, y + 20);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/body.png", width - 20, 20);
        gs.setPosition(x + 20, y);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/bottom-left.png", 20, 20);
        gs.setPosition(x, y);
        backgroundSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/bottom-right.png", 20, 20);
        gs.setPosition(x + width - 20, y);
        backgroundSprites.add(gs);

        return backgroundSprites;
    }

    public RedButton setText(String text){
        this.text = text;
        return this;
    }

    public RedButton setTextColor(Color textColor) {
        this.textColor = new Color(textColor);
        return setText(text);
    }

    public RedButton setBackgroundTint(Color backgroundTint) {
        this.backgroundTint = new Color(backgroundTint);
        return this;
    }

    @Override
    public void setPosition(float x, float y) {
        float deltaX = x - this.x, deltaY = y - this.y;
        super.setPosition(x, y);

        for (GameSprite sprite : gameSprites) sprite.translate(deltaX, deltaY);
    }

    public static float getPreferredHeight(String text, float buttonWidth) {
        FittedButtonText fittedText = fitText(text, buttonWidth);
        GlyphLayout fittedLayout = FontHelper.getSingleton().measure(Color.WHITE, fittedText.size, fittedText.text);
        return Math.max(MIN_HEIGHT, fittedLayout.height + TEXT_VERTICAL_PADDING * 2f);
    }

    public static float getTextWrapWidth(float buttonWidth) {
        return Math.max(80f, buttonWidth - TEXT_HORIZONTAL_PADDING * 2f);
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        Color previousColor = new Color(batch.getColor());
        float pressTint = isShowingPressFeedback() ? 1.2f : 1f;
        batch.setColor(previousColor.r * backgroundTint.r * pressTint,
                previousColor.g * backgroundTint.g * pressTint,
                previousColor.b * backgroundTint.b * pressTint,
                previousColor.a * backgroundTint.a);
        if (DesktopMenuStyle.active()) {
            DesktopMenuStyle.card(batch, x, y, width, height, DesktopMenuStyle.GOLD, isShowingPressFeedback());
            DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
        } else for(GameSprite gameSprite : gameSprites){
            gameSprite.draw(batch);
        }
        batch.setColor(previousColor);

        FittedButtonText fittedText = fitText(text, width, Math.max(1f, height - TEXT_VERTICAL_PADDING * 2f));
        GlyphLayout glyphLayout = FontHelper.getSingleton().measure(textColor, fittedText.size, fittedText.text);
        offsetX = width / 2 - glyphLayout.width / 2;
        float textY = y + (height + glyphLayout.height) / 2f;
        FontHelper.getSingleton().writeRaw(textColor, batch, fittedText.size, x + offsetX, textY, fittedText.text);
    }

    private static FittedButtonText fitText(String text, float buttonWidth) {
        return fitText(text, buttonWidth, Float.POSITIVE_INFINITY);
    }

    private static FittedButtonText fitText(String text, float buttonWidth, float maxTextHeight) {
        String localized = Messages.maybeTranslate(text == null ? "" : text);
        float wrapWidth = getTextWrapWidth(buttonWidth);
        float size = TEXT_SIZE;
        String wrapped;

        while (true) {
            wrapped = UtilsHelper.multiLineRaw(localized, size, wrapWidth);
            GlyphLayout layout = FontHelper.getSingleton().measure(Color.WHITE, size, wrapped);
            if (size <= 1f || (layout.width <= wrapWidth + 0.5f && layout.height <= maxTextHeight + 0.5f)) break;
            size -= 0.25f;
        }
        return new FittedButtonText(wrapped, size);
    }

    private static class FittedButtonText {
        private final String text;
        private final float size;

        private FittedButtonText(String text, float size) {
            this.text = text;
            this.size = size;
        }
    }
}

