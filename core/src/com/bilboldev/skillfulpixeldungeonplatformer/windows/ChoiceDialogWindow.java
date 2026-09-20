package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class ChoiceDialogWindow extends DescriptionWindow {
    private static final float DEFAULT_BUTTON_BOTTOM_PADDING = 70f;
    private static final float BUTTON_GAP = 15f;

    private final ArrayList<DialogChoice> choices;
    private final ArrayList<ActionButton> actionButtons;
    private float buttonBottomPadding = DEFAULT_BUTTON_BOTTOM_PADDING;
    private float bottomExtension;

    public ChoiceDialogWindow(String sprite, String description, float width, float height) {
        super(sprite, description, width, height);
        choices = new ArrayList<DialogChoice>();
        actionButtons = new ArrayList<ActionButton>();
    }

    public ChoiceDialogWindow(GameSprite sprite, String description, float width, float height) {
        super(sprite, description, width, height);
        choices = new ArrayList<DialogChoice>();
        actionButtons = new ArrayList<ActionButton>();
    }

    public ChoiceDialogWindow addChoice(String label, Runnable action) {
        choices.add(new DialogChoice(label, action, true));
        return this;
    }

    public ChoiceDialogWindow addChoice(String label, Runnable action, boolean enabled) {
        choices.add(new DialogChoice(label, action, enabled));
        return this;
    }

    public ChoiceDialogWindow setButtonBottomPadding(float buttonBottomPadding) {
        this.buttonBottomPadding = buttonBottomPadding;
        return this;
    }

    public ChoiceDialogWindow extendBottom(float bottomExtension) {
        this.bottomExtension = Math.max(0f, bottomExtension);
        return this;
    }

    @Override
    public Window build() {
        int rows = Math.max(1, choices.size());
        float buttonWidth = Math.min(width - 420f, 900f);
        float totalButtonHeight = 0f;
        for (DialogChoice choice : choices) {
            totalButtonHeight += RedButton.getPreferredHeight(choice.label, buttonWidth);
        }
        float buttonStackHeight = totalButtonHeight + Math.max(0, choices.size() - 1) * BUTTON_GAP;
        setReservedBottomHeight(buttonBottomPadding + buttonStackHeight);

        float baseHeight = Math.max(height, 360f + buttonStackHeight + rows * 20f);
        height = baseHeight + bottomExtension;

        super.build();
        actionButtons.clear();

        float buttonX = x + (width - buttonWidth) / 2f;
        float buttonY = y + buttonBottomPadding;

        for (final DialogChoice choice : choices) {
            float buttonHeight = RedButton.getPreferredHeight(choice.label, buttonWidth);
            RedButton button = new RedButton(buttonX, buttonY, buttonWidth, buttonHeight) {
                @Override
                public void click() {
                    if (!choice.enabled) {
                        return;
                    }

                    hide();
                    if (choice.action != null) {
                        choice.action.run();
                    }
                    WindowHelper.getInstance().refresh();
                }
            }.setText(Messages.maybeTranslate(choice.label));

            if (!choice.enabled) {
                button.disable();
                button.setBackgroundTint(new Color(0.55f, 0.55f, 0.55f, 0.9f));
                button.setTextColor(new Color(0.78f, 0.78f, 0.78f, 1f));
            }

            actionButtons.add(button);
            buttonY += buttonHeight + BUTTON_GAP;
        }

        return this;
    }

    @Override
    protected ArrayList<ActionButton> getKeyboardChoices() { return actionButtons; }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        for (ActionButton actionButton : actionButtons) {
            actionButton.draw(batch);
        }
    }

    @Override
    public boolean click(float x, float y) {
        for (ActionButton actionButton : actionButtons) {
            if (actionButton.isHitProjected(x, y)) {
                actionButton.click();
                return true;
            }
        }

        return super.click(x, y);
    }

    private static class DialogChoice {
        private final String label;
        private final Runnable action;
        private final boolean enabled;

        private DialogChoice(String label, Runnable action, boolean enabled) {
            this.label = label;
            this.action = action;
            this.enabled = enabled;
        }
    }
}
