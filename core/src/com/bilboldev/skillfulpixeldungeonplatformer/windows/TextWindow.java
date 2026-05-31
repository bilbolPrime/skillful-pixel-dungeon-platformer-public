package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class TextWindow extends Window{
    protected String text;
    private final String sourceText;
    private float textFontSize = ConstantsHelper.WINDOW_FONT_SIZE;
    protected float xOffset, yOffset;

    public TextWindow(float width, float height, String text) {
        super(width, height);
        this.sourceText = text == null ? "" : text;
        this.text = this.sourceText;
    }

    @Override
    public Window build(){
        float textWidth = Math.max(100, Math.min(width - 200, ConstantsHelper.SCREEN_WIDTH - 400));
        float maxOverlayHeight = ConstantsHelper.SCREEN_HEIGHT * 0.9f;
        String localizedText = Messages.maybeTranslate(sourceText);
        FontHelper.FittedTextBlock fittedText = FontHelper.getSingleton().fitOverlayText(
            getClass().getName() + ":text",
            sourceText,
            localizedText,
            ConstantsHelper.WINDOW_FONT_SIZE,
            textWidth,
            Math.max(60f, maxOverlayHeight - 260f));
        text = fittedText.text;
        textFontSize = fittedText.size;

        String englishText = UtilsHelper.multiLineEnglish(sourceText, ConstantsHelper.WINDOW_FONT_SIZE, textWidth);

        float maxWidth = -1;
        for(String line : englishText.split("\n", -1)){
            GlyphLayout lineLayout = FontHelper.getSingleton().measureEnglish(Color.BLACK, ConstantsHelper.WINDOW_FONT_SIZE, line);
            if(maxWidth < lineLayout.width){
                maxWidth = lineLayout.width;
            }
        }

        float preferredWidth = maxWidth + 200;
        if(preferredWidth > width){
            this.width = Math.min(preferredWidth, ConstantsHelper.SCREEN_WIDTH - 200);
        }

        float minHeight = fittedText.height + 260;
        if (height < minHeight) {
            height = minHeight;
        }
        height = Math.min(height, maxOverlayHeight);

        xOffset = 100;
        yOffset = height - 100;

        super.build();

        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);
        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, textFontSize, (int)(x + xOffset), (int)(y + yOffset), text);
    }
}


