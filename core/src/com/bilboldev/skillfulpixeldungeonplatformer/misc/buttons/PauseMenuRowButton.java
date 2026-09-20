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
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;

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
    private String fittedBindingKey;
    private FontHelper.FittedTextBlock fittedBindingName, fittedBindingValue;
    private String checkboxLabel, checkboxHelp;
    private float checkboxLabelSize, checkboxHelpSize;

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


    public PauseMenuRowButton setCheckboxHelp(String help) {
        FontHelper fonts = FontHelper.getSingleton();
        checkboxLabel = Messages.maybeTranslate(leftLabel);
        checkboxHelp = Messages.maybeTranslate(help);

        checkboxLabelSize = fonts.fitSize(checkboxLabel, 3f, width - 164f, 32f);
        checkboxHelpSize = fonts.fitSize(checkboxHelp, 2f, width - 164f, 24f);
        return this;
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
        if (DesktopMenuStyle.active()) {
            DesktopMenuStyle.card(batch, x, y, width, height, DesktopMenuStyle.EDGE, isShowingPressFeedback());
            DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
        } else for (GameSprite sprite : rowSprites) {
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
        String localizedRightLabel = Messages.maybeTranslate(rightLabel);
        if (!showCheckbox && !rightLabel.isEmpty()) {
            FontHelper fonts = FontHelper.getSingleton();
            GlyphLayout leftLayout = new GlyphLayout(fonts.getFont(Color.WHITE, 3f), localizedLeftLabel);
            GlyphLayout rightLayout = new GlyphLayout(fonts.getFont(Color.WHITE, 3f), localizedRightLabel);
            if (leftLayout.width + rightLayout.width + 24f > width - 80f) {
                String key = localizedLeftLabel + "\n" + localizedRightLabel;
                if (!key.equals(fittedBindingKey)) {
                    fittedBindingKey = key;
                    fittedBindingName = fonts.fitOverlayText("binding-name", leftLabel, localizedLeftLabel, 2.6f, width - 80f, height / 2f - 12f);
                    fittedBindingValue = fonts.fitOverlayText("binding-value", rightLabel, localizedRightLabel, 2.6f, width - 80f, height / 2f - 12f);
                }
                fonts.writeRaw(Color.WHITE, batch, fittedBindingName.size, x + 40f, y + height - 12f, fittedBindingName.text);
                fonts.writeRaw(highlightRightLabel ? Color.GOLD : Color.WHITE, batch, fittedBindingValue.size,
                        x + 40f, y + height / 2f - 6f, fittedBindingValue.text);
                return;
            }
        }
        if (showCheckbox && checkboxHelp != null) {
            FontHelper fonts = FontHelper.getSingleton();
            fonts.writeRaw(Color.WHITE, batch, checkboxLabelSize, x + 40f, y + height - 20f, checkboxLabel);
            fonts.writeRaw(Color.LIGHT_GRAY, batch, checkboxHelpSize, x + 40f, y + 36f, checkboxHelp);
        } else {
            FontHelper.getSingleton().writeWhite(batch, 3f, x + 40f, y + 60f, localizedLeftLabel);
        }

        if (showCheckbox) {
            if (DesktopMenuStyle.active()) {
                DesktopMenuStyle.checkbox(batch, x + width - 100f, y + 24f - height * checkboxVerticalOffsetRatio, checked);
            } else {
                TextureRegion region = checked ? checkedIcon : uncheckedIcon;
                batch.draw(region, x + width - 100f, y + 24f - height * checkboxVerticalOffsetRatio, 52f, 52f);
            }
            return;
        }

        if (!rightLabel.isEmpty()) {
            Color labelColor = highlightRightLabel ? Color.GOLD : Color.WHITE;
            GlyphLayout layout = new GlyphLayout(FontHelper.getSingleton().getFont(labelColor, 3f), localizedRightLabel);
            FontHelper.getSingleton().write(labelColor, batch, 3f, x + width - 40f - layout.width, y + 60f, localizedRightLabel);
        }
    }
}
