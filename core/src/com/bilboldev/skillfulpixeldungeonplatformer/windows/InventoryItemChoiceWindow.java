package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class InventoryItemChoiceWindow extends DescriptionWindow {
    public interface ItemSelectionHandler {
        void onItemSelected(Item item);
    }

    private final ArrayList<ActionButton> actionButtons;
    private final ArrayList<Item> items;
    private final ItemSelectionHandler selectionHandler;

    public InventoryItemChoiceWindow(String sprite, String description, ArrayList<Item> items, ItemSelectionHandler selectionHandler) {
        super(sprite, description, 1850f, 420f);
        this.items = new ArrayList<Item>(items);
        this.selectionHandler = selectionHandler;
        this.actionButtons = new ArrayList<ActionButton>();

        int columns = Math.max(1, Math.min(3, this.items.size()));
        int rows = (this.items.size() + columns - 1) / columns;
        height = Math.max(420f, 360f + rows * 110f);
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        x = ConstantsHelper.SCREEN_WIDTH / 2 - width / 2;
    }

    public InventoryItemChoiceWindow(GameSprite sprite, String description, ArrayList<Item> items, ItemSelectionHandler selectionHandler) {
        super(sprite, description, 1850f, 420f);
        this.items = new ArrayList<Item>(items);
        this.selectionHandler = selectionHandler;
        this.actionButtons = new ArrayList<ActionButton>();

        int columns = Math.max(1, Math.min(3, this.items.size()));
        int rows = (this.items.size() + columns - 1) / columns;
        height = Math.max(420f, 360f + rows * 110f);
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        x = ConstantsHelper.SCREEN_WIDTH / 2 - width / 2;
    }

    @Override
    public Window build() {
        super.build();
        actionButtons.clear();

        final float buttonWidth = 500f;
        final float buttonHeight = 90f;
        final float gapX = 50f;
        final float gapY = 20f;
        final int columns = Math.max(1, Math.min(3, items.size()));
        final float totalButtonWidth = columns * buttonWidth + (columns - 1) * gapX;
        final float startX = x + (width - totalButtonWidth) / 2f;
        final float startY = y + height - 370f;

        for (int i = 0; i < items.size(); i++) {
            final Item item = items.get(i);
            int column = i % columns;
            int row = i / columns;
            float buttonX = startX + column * (buttonWidth + gapX);
            float buttonY = startY - row * (buttonHeight + gapY);

            actionButtons.add(new RedButton(buttonX, buttonY, buttonWidth, buttonHeight) {
                @Override
                public void click() {
                    if (selectionHandler != null) {
                        selectionHandler.onItemSelected(item);
                    }

                    hide();
                    WindowHelper.getInstance().refresh();
                }
            }.setText(item.getNameWithQuantity()));
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
}
