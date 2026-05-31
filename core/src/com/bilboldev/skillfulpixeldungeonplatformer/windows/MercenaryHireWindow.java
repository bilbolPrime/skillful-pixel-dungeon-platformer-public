package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper.MercenaryType;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit;

import java.util.ArrayList;

public class MercenaryHireWindow extends Window {
    private static final float WINDOW_WIDTH = 1520f;
    private static final float WINDOW_HEIGHT = 620f;
    private static final float MIN_WINDOW_HEIGHT = 620f;
    private static final float OUTER_PADDING = 82f;
    private static final float BUTTON_HEIGHT = 92f;
    private static final float BUTTON_WIDTH = 260f;
    private static final float BUTTON_BOTTOM_PADDING = 76f + BUTTON_HEIGHT / 2f;
    private static final float BUTTON_GAP = 28f;
    private static final float TEXT_SIZE = 1.9f;
    private static final float TEXT_TOP_PADDING = 118f;
    private static final float TEXT_BOTTOM_PADDING = BUTTON_BOTTOM_PADDING + BUTTON_HEIGHT + 48f;
    private static final float LINE_HEIGHT = 46f;

    private final MercenaryRecruit recruit;
    private final ArrayList<ActionButton> buttons = new ArrayList<ActionButton>();
    private final ArrayList<String> descriptionLines = new ArrayList<String>();
    private ActionButton pressedButton;

    public MercenaryHireWindow(MercenaryRecruit recruit) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.recruit = recruit;
    }

    @Override
    public Window build() {
        buttons.clear();
        descriptionLines.clear();
        pressedButton = null;

        String wrappedDescription = UtilsHelper.multiLine(buildDescriptionText(), 2, width - OUTER_PADDING * 2f);
        for (String line : wrappedDescription.split("\\n", -1)) {
            descriptionLines.add(line);
        }
        height = Math.max(MIN_WINDOW_HEIGHT, TEXT_TOP_PADDING + TEXT_BOTTOM_PADDING + descriptionLines.size() * LINE_HEIGHT);

        super.build();
        buildButtons();
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        drawDescription(batch);

        for (ActionButton button : buttons) {
            button.draw(batch);
        }
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        pressedButton = null;
        for (ActionButton actionButton : buttons) {
            if (actionButton.isHitProjected(x, y)) {
                pressedButton = actionButton;
                actionButton.pressDown();
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        if (pressedButton != null) {
            ActionButton tappedButton = pressedButton;
            pressedButton = null;
            tappedButton.releasePress();
            if (tappedButton.isHitProjected(x, y)) {
                tappedButton.click();
                return true;
            }

            tappedButton.cancelPress();
            return true;
        }

        if (x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height) {
            WindowHelper.getInstance().closeWindow(this);
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        WindowHelper.getInstance().closeWindow(this);
        return true;
    }

    private void buildButtons() {
        float buttonY = y + BUTTON_BOTTOM_PADDING;
        float noButtonX = x + width - OUTER_PADDING - BUTTON_WIDTH;
        float hireButtonX = noButtonX - BUTTON_GAP - BUTTON_WIDTH;

        PauseMenuRowButton hireButton = new PauseMenuRowButton(hireButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                if (InventoryHelper.getInstance().getGold() < recruit.getMercenaryPrice()) {
                    EffectsHelper.getInstance().message(
                            UnitHelper.getInstance().getHero(),
                            Messages.get("custom.generated.insufficient_gold_725c4e6585"),
                            Color.GOLD,
                            0f);
                    return;
                }

                InventoryHelper.getInstance().modifyGold(-recruit.getMercenaryPrice());
                recruit.hire();
                WindowHelper.getInstance().closeWindow(MercenaryHireWindow.this);
            }
        }.setCenteredText(Messages.get("custom.generated.hire_c498ae69ab"));
        buttons.add(hireButton);

        PauseMenuRowButton leaveButton = new PauseMenuRowButton(noButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                WindowHelper.getInstance().closeWindow(MercenaryHireWindow.this);
            }
        }.setCenteredText("No");
        buttons.add(leaveButton);
    }

    private void drawDescription(Batch batch) {
        float lineY = y + height - TEXT_TOP_PADDING;
        for (String line : descriptionLines) {
            FontHelper.getSingleton().write(Color.WHITE, batch, TEXT_SIZE, x + OUTER_PADDING, lineY, line);
            lineY -= LINE_HEIGHT;
        }
    }

    private String buildDescriptionText() {
        String displayName = MercenaryHelper.getDisplayName(recruit.getMercenaryType(), recruit.getMercenaryName());
        String armorName = resolveItemName(recruit.getMercenaryArmor(), "travel clothes");
        String weaponName = resolveWeaponName();
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(Messages.get("custom.ui.mercenary.hire.offer",
                new Object[]{recruit.getMercenaryLevel(), displayName, recruit.getMercenaryPrice()}));
        lines.add(Messages.get("custom.ui.mercenary.hire.equipment",
                new Object[]{displayName, armorName, weaponName}));

        if (recruit.getMercenaryType() == MercenaryType.ROGUE) {
            lines.add(Messages.get("custom.ui.mercenary.hire.special.rogue", new Object[]{displayName}));
        }

        if (recruit.getMercenaryType() == MercenaryType.WIZARD) {
            lines.add(Messages.get("custom.ui.mercenary.hire.special.wizard", new Object[]{displayName}));
        }

        return String.join("\n", lines);
    }

    private String resolveWeaponName() {
        if (recruit.getMercenaryType() == MercenaryType.HUNTRESS && recruit.getMercenaryRangedWeapon() != null) {
            return resolveItemName(recruit.getMercenaryRangedWeapon(), "bow");
        }

        if (recruit.getMercenaryWeapon() != null) {
            return resolveItemName(recruit.getMercenaryWeapon(), "weapon");
        }

        if (recruit.getMercenaryRangedWeapon() != null) {
            return resolveItemName(recruit.getMercenaryRangedWeapon(), "weapon");
        }

        return Messages.maybeTranslate("weapon");
    }

    private String resolveItemName(Item item, String fallback) {
        if (item == null) {
            return Messages.maybeTranslate(fallback);
        }

        String itemName = item.getName();
        return itemName == null || itemName.isEmpty() ? Messages.maybeTranslate(fallback) : itemName;
    }
}