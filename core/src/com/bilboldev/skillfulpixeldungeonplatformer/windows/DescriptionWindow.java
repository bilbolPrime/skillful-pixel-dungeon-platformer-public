package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DescriptionWindow extends Window{

    protected static final float DESCRIPTION_TEXT_X = 340f;
    protected static final float DESCRIPTION_TEXT_RIGHT_PADDING = 120f;
    private static final float DESCRIPTION_TEXT_SIZE = 3f;

    private static final float DESCRIPTION_VERTICAL_PADDING = 225f;

    private GameSprite descriptionSprite;
    private final String descriptionSource;
    private String description;
    private float descriptionFontSize = DESCRIPTION_TEXT_SIZE;
    private float descriptionSpriteYOffset;
    private float reservedBottomHeight;
    protected final WindowChoiceFocus keyboardFocus = new WindowChoiceFocus();

    public DescriptionWindow(String sprite, String description, float width, float height) {
        this(new GameSprite(sprite, 200, 200), description, width, height);
    }

    public DescriptionWindow(GameSprite sprite, String description, float width, float height) {
        super(width, height);
        descriptionSprite = sprite;
        this.descriptionSource = description;
        this.description = description;
    }

    public DescriptionWindow setDescriptionSpriteYOffset(float descriptionSpriteYOffset) {
        this.descriptionSpriteYOffset = descriptionSpriteYOffset;
        return this;
    }

    public DescriptionWindow setDescriptionSpriteYOffsetByRatio(float ratio) {
        return setDescriptionSpriteYOffset(descriptionSprite.getHeight() * ratio);
    }

    protected DescriptionWindow setReservedBottomHeight(float reservedBottomHeight) {
        this.reservedBottomHeight = Math.max(0f, reservedBottomHeight);
        return this;
    }

    @Override
    public Window build(){
        float wrapWidth = getDescriptionWrapWidth(width);
        float maxOverlayHeight = ConstantsHelper.SCREEN_HEIGHT * 0.9f;
        float maxTextHeight = Math.max(60f, maxOverlayHeight - DESCRIPTION_VERTICAL_PADDING - reservedBottomHeight);
        String localizedDescription = Messages.maybeTranslate(descriptionSource == null ? "" : descriptionSource);
        FontHelper.FittedTextBlock fittedDescription = FontHelper.getSingleton().fitOverlayText(
                getClass().getName() + ":description",
                descriptionSource,
                localizedDescription,
                DESCRIPTION_TEXT_SIZE,
                wrapWidth,
                maxTextHeight);
        description = fittedDescription.text;
        descriptionFontSize = fittedDescription.size;

        height = Math.min(maxOverlayHeight, Math.max(height, fittedDescription.height + DESCRIPTION_VERTICAL_PADDING + reservedBottomHeight));
        super.build();

        descriptionSprite.setPosition(x + 100, y + height - 300 + descriptionSpriteYOffset);
        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        descriptionSprite.draw(batch);

        FontHelper.getSingleton().writeRaw(Color.WHITE, batch, descriptionFontSize, x + DESCRIPTION_TEXT_X, y + height - 125, description);
        keyboardFocus.draw(this, batch, getKeyboardChoices());

    }

    protected static float getDescriptionWrapWidth(float windowWidth) {
        return Math.max(100f, windowWidth - DESCRIPTION_TEXT_X - DESCRIPTION_TEXT_RIGHT_PADDING);
    }

    protected List<? extends Button> getKeyboardChoices() { return Collections.emptyList(); }

    @Override
    public boolean keyDown(int keycode) { return keyboardFocus.keyDown(this, keycode, getKeyboardChoices()); }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        keyboardFocus.pointerDown(x, y, getKeyboardChoices());
        return super.pointerDown(x, y, button);
    }

    protected static float getDescriptionLineHeight(String description) {
        return Math.max(
                55f,
                FontHelper.getSingleton().getFont(Color.WHITE, DESCRIPTION_TEXT_SIZE, description).getLineHeight() * 1.15f);
    }


}


