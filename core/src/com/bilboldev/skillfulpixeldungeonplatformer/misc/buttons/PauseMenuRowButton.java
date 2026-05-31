package com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class PauseMenuRowButton extends ActionButton {

    private final ArrayList<GameSprite> rowSprites;
    private final TextureRegion checkedIcon;
    private final TextureRegion uncheckedIcon;

    private String centeredLabel = "";
    private String leftLabel = "";
    private String rightLabel = "";
    private boolean centered = true;
    private boolean showCheckbox;
    private boolean checked;
    private boolean highlightRightLabel;
    private float checkboxVerticalOffsetRatio;
    private Color centeredLabelColor = Color.WHITE;

    public PauseMenuRowButton(float x, float y, float width, float height) {
        super(x, y, width, height, "images/misc/transparent.png", "images/misc/transparent.png");
        enableUiPressFeedback();

        rowSprites = new ArrayList<>();
        GameSprite gs = new GameSprite("images/buttons/red-button/top.png", width - 20, 20);
        gs.setPosition(x + 20, y + height - 20);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/top-left.png", 20, 20);
        gs.setPosition(x, y + height - 20);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/top-right.png", 20, 20);
        gs.setPosition(x + width - 20, y + height - 20);
        rowSprites.add(gs);

        gs = new GameSprite("images/buttons/red-button/left.png", 20, height - 40);
        gs.setPosition(x, y + 20);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/right.png", 20, height - 40);
        gs.setPosition(x + width - 20, y + 20);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/body.png", width - 40, height - 40);
        gs.setPosition(x + 20, y + 20);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/body.png", width - 20, 20);
        gs.setPosition(x + 20, y);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/bottom-left.png", 20, 20);
        gs.setPosition(x, y);
        rowSprites.add(gs);
        gs = new GameSprite("images/buttons/red-button/bottom-right.png", 20, 20);
        gs.setPosition(x + width - 20, y);
        rowSprites.add(gs);

        Texture iconsTexture = TextureHelper.GetSingleton().getTexture("images/menu/icons.png");
        checkedIcon = new TextureRegion(iconsTexture, 54, 12, 12, 12);
        uncheckedIcon = new TextureRegion(iconsTexture, 66, 12, 12, 12);
    }

    public PauseMenuRowButton setCenteredText(String text) {
        centered = true;
        centeredLabel = text;
        leftLabel = "";
        rightLabel = "";
        showCheckbox = false;
        highlightRightLabel = false;
        centeredLabelColor = Color.WHITE;
        return this;
    }

    public PauseMenuRowButton setCenteredTextColor(Color color) {
        centeredLabelColor = color == null ? Color.WHITE : color;
        return this;
    }

    public PauseMenuRowButton setBindingRow(String leftText, String rightText, boolean highlightRightText) {
        centered = false;
        showCheckbox = false;
        leftLabel = leftText;
        rightLabel = rightText;
        highlightRightLabel = highlightRightText;
        centeredLabelColor = Color.WHITE;
        return this;
    }

    public PauseMenuRowButton setCheckboxRow(String leftText, boolean checked) {
        centered = false;
        leftLabel = leftText;
        rightLabel = "";
        showCheckbox = true;
        this.checked = checked;
        highlightRightLabel = false;
        centeredLabelColor = Color.WHITE;
        return this;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public PauseMenuRowButton setCheckboxVerticalOffsetRatio(float checkboxVerticalOffsetRatio) {
        this.checkboxVerticalOffsetRatio = checkboxVerticalOffsetRatio;
        return this;
    }

    @Override
    public void draw(Batch batch) {
        Color previousColor = new Color(batch.getColor());
        if (isShowingPressFeedback()) {
            batch.setColor(previousColor.r * 1.2f, previousColor.g * 1.2f, previousColor.b * 1.2f, previousColor.a);
        }
        for (GameSprite sprite : rowSprites) {
            sprite.draw(batch);
        }
        batch.setColor(previousColor);

        if (centered) {
            String localizedCenteredLabel = Messages.maybeTranslate(centeredLabel);
            GlyphLayout layout = new GlyphLayout(FontHelper.getSingleton().getFont(centeredLabelColor, 3f), localizedCenteredLabel);
            FontHelper.getSingleton().write(centeredLabelColor, batch, 3f, x + width / 2f - layout.width / 2f, y + 60f, localizedCenteredLabel);
            return;
        }

        String localizedLeftLabel = Messages.maybeTranslate(leftLabel);
        FontHelper.getSingleton().writeWhite(batch, 3f, x + 40f, y + 60f, localizedLeftLabel);

        if (showCheckbox) {
            TextureRegion region = checked ? checkedIcon : uncheckedIcon;
            batch.draw(region, x + width - 100f, y + 24f - height * checkboxVerticalOffsetRatio, 52f, 52f);
            return;
        }

        if (!rightLabel.isEmpty()) {
            Color labelColor = highlightRightLabel ? Color.GOLD : Color.WHITE;
            String localizedRightLabel = Messages.maybeTranslate(rightLabel);
            GlyphLayout layout = new GlyphLayout(FontHelper.getSingleton().getFont(labelColor, 3f), localizedRightLabel);
            FontHelper.getSingleton().write(labelColor, batch, 3f, x + width - 40f - layout.width, y + 60f, localizedRightLabel);
        }
    }
}