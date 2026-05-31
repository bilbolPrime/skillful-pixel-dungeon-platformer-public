package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.BirthdaySuit;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;

import java.util.ArrayList;

public class MercenaryWindow extends InteractiveWindow {
    private static final float WINDOW_HEIGHT_REDUCTION = 92f;
    private static final float WINDOW_WIDTH = 1240f;
    private static final float WINDOW_HEIGHT = 645f - WINDOW_HEIGHT_REDUCTION;
    private static final float PORTRAIT_SIZE = 165f;
    private static final float HEADER_TEXT_SIZE = 2.45f;
    private static final float BODY_TEXT_SIZE = 2.0f;
    private static final float TEXT_LINE_GAP = 8f;
    private static final float SLOT_WIDTH = 430f;
    private static final float SLOT_HEIGHT = 92f;
    private static final float SLOT_ICON_SIZE = 58f;
    private static final float SLOT_GAP = 36f;
    private static final float SLOT_EDGE_GAP = SLOT_HEIGHT / 2f;
    private static final float PORTRAIT_LEFT_MARGIN = 100f;
    private static final float PORTRAIT_TOP_MARGIN = 110f;
    private static final float EXP_TEXT_EXTRA_DROP = 2f;
    private static final float TEXT_GAP_RATIO = 1f;

    public enum EquipmentSlot {
        WEAPON,
        ARMOR
    }

    private final MercenaryAlly mercenary;
    private GameSprite portrait;

    public MercenaryWindow(MercenaryAlly mercenary) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.mercenary = mercenary;
    }

    @Override
    public Window build() {
        super.build();
        y += WINDOW_HEIGHT_REDUCTION / 2f;
        for (GameSprite gameSprite : gameSprites) {
            gameSprite.translate(0f, WINDOW_HEIGHT_REDUCTION / 2f);
        }
        actionButtons.clear();

        if (mercenary == null) {
            return this;
        }

        portrait = MercenaryHelper.getHeroClass(mercenary.getMercenaryType()).getClassPortrait().clone();
        portrait.setWidth(Math.round(PORTRAIT_SIZE));
        portrait.setHeight(Math.round(PORTRAIT_SIZE));
        portrait.setPosition(x + PORTRAIT_LEFT_MARGIN, y + height - PORTRAIT_SIZE - PORTRAIT_TOP_MARGIN);

        addEquipmentButtons();
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        if (mercenary == null || mercenary.isDead()) {
            FontHelper.getSingleton().write(Color.SALMON, batch, BODY_TEXT_SIZE, x + 120f, y + height - 160f,
                    Messages.get("custom.ui.mercenary.unavailable"));
            return;
        }

        if (portrait != null) {
            portrait.draw(batch);
        }

        float headerX = x + 300f;
        float portraitBottomY = portrait != null ? portrait.getY() : y + height - PORTRAIT_SIZE - PORTRAIT_TOP_MARGIN;
        String displayName = MercenaryHelper.getDisplayName(mercenary.getMercenaryType(), mercenary.getMercenaryName());
        String headerText = Messages.get("custom.ui.mercenary.status.header",
            new Object[]{mercenary.getMercenaryLevel(), displayName, mercenary.getStrength()});
        String expText = Messages.get("custom.ui.mercenary.status.exp",
            new Object[]{mercenary.getMercenaryExperienceText()});
        GlyphLayout expLayout = new GlyphLayout();
        expLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, BODY_TEXT_SIZE), expText);

        String hpText = Messages.get("custom.ui.mercenary.status.hp",
            new Object[]{mercenary.getHP(), mercenary.getMaxHP()});
        GlyphLayout hpLayout = new GlyphLayout();
        hpLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, BODY_TEXT_SIZE), hpText);
        GlyphLayout headerLayout = new GlyphLayout();
        headerLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, HEADER_TEXT_SIZE), headerText);
        float bodyLineHeight = Math.max(expLayout.height, hpLayout.height);
        float textGap = bodyLineHeight * TEXT_GAP_RATIO;
        float expTextY = portraitBottomY + bodyLineHeight;
        float hpTextY = expTextY + hpLayout.height + textGap + bodyLineHeight * 0.5f;
        float headerY = hpTextY + headerLayout.height + textGap + bodyLineHeight * 0.5f;

        FontHelper.getSingleton().write(Color.WHITE,
            batch,
            HEADER_TEXT_SIZE,
            headerX,
            headerY,
            headerText);

        FontHelper.getSingleton().write(Color.WHITE,
            batch,
            BODY_TEXT_SIZE,
            headerX,
            hpTextY,
            hpText);

        FontHelper.getSingleton().write(Color.WHITE,
            batch,
            BODY_TEXT_SIZE,
            headerX,
            expTextY + EXP_TEXT_EXTRA_DROP,
            expText);
    }

    private void addEquipmentButtons() {
        float totalWidth = SLOT_WIDTH * 2f + SLOT_GAP;
        float weaponX = x + (width - totalWidth) / 2f;
        float armorX = weaponX + SLOT_WIDTH + SLOT_GAP;
        float portraitBottomY = y + height - PORTRAIT_SIZE - PORTRAIT_TOP_MARGIN;
        float legacyMinSlotY = y - WINDOW_HEIGHT_REDUCTION + SLOT_EDGE_GAP;
        float legacyMaxSlotY = portraitBottomY - SLOT_EDGE_GAP - SLOT_HEIGHT;
        float slotY = legacyMinSlotY + (legacyMaxSlotY - legacyMinSlotY) / 2f + SLOT_HEIGHT;

        actionButtons.add(new EquipmentSlotButton(weaponX, slotY, EquipmentSlot.WEAPON));
        actionButtons.add(new EquipmentSlotButton(armorX, slotY, EquipmentSlot.ARMOR));
    }

    private void onEquipmentSlotClicked(EquipmentSlot slot) {
        if (mercenary == null || mercenary.isDead()) {
            return;
        }

        Item item = getEquipmentItem(slot);
        if (item != null) {
            WindowHelper.getInstance().addWindow(new MercenaryItemWindow(item, mercenary, slot).addDropAction().build());
            return;
        }

        openEquipmentChoice(slot);
    }

    private void openEquipmentChoice(final EquipmentSlot slot) {
        ArrayList<Item> candidates = getEquipmentCandidates(slot);
        if (candidates.isEmpty()) {
            WindowHelper.getInstance().addWindow(1000f, 120f,
                slot == EquipmentSlot.WEAPON
                    ? Messages.get("custom.ui.mercenary.no_unequipped_weapons")
                    : Messages.get("custom.ui.mercenary.no_unequipped_armor"));
            return;
        }

        String sprite = slot == EquipmentSlot.WEAPON
                ? "images/misc/extracted items/WEAPON.png"
                : "images/misc/extracted items/ARMOR.png";
        String displayName = MercenaryHelper.getDisplayName(mercenary.getMercenaryType(), mercenary.getMercenaryName());
        String description = slot == EquipmentSlot.WEAPON
            ? Messages.get("custom.generated.choose_a_weapon_for_arg_e05b0774bf", new Object[]{displayName})
            : Messages.get("custom.generated.choose_armor_for_arg_96ae44ea74", new Object[]{displayName});

        WindowHelper.getInstance().addWindow(new InventoryItemChoiceWindow(
                sprite,
                description,
                candidates,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        equipSelectedItem(slot, item);
                    }
                }).build());
    }

    private void equipSelectedItem(EquipmentSlot slot, Item item) {
        if (slot == EquipmentSlot.WEAPON && item instanceof MeleeWeapon) {
            InventoryHelper.getInstance().removeItem(item);
            prepareEquippedItem(item);
            mercenary.equipMercenaryWeapon((MeleeWeapon) item);
            return;
        }

        if (slot == EquipmentSlot.WEAPON
                && item instanceof RangedWeapon
                && mercenary.getMercenaryType() == MercenaryHelper.MercenaryType.HUNTRESS
                && MercenaryHelper.isBowWeapon((RangedWeapon) item)) {
            InventoryHelper.getInstance().removeItem(item);
            prepareEquippedItem(item);
            mercenary.equipMercenaryRangedWeapon((RangedWeapon) item);
            return;
        }

        if (slot == EquipmentSlot.ARMOR && item instanceof Armor) {
            InventoryHelper.getInstance().removeItem(item);
            prepareEquippedItem(item);
            mercenary.equipMercenaryArmor((Armor) item);
        }
    }

    private void prepareEquippedItem(Item item) {
        if (!(item instanceof EquipableItem)) {
            return;
        }

        ((EquipableItem) item).setEquippedState(false);
        ((EquipableItem) item).setOwner(null);
    }

    private ArrayList<Item> getEquipmentCandidates(EquipmentSlot slot) {
        ArrayList<Item> candidates = new ArrayList<Item>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (item == null || isHeroEquippedItem(item)) {
                continue;
            }

            if (slot == EquipmentSlot.WEAPON && item instanceof MeleeWeapon && !(item instanceof MeleeAttack)) {
                candidates.add(item);
            }
            else if (slot == EquipmentSlot.WEAPON
                    && mercenary.getMercenaryType() == MercenaryHelper.MercenaryType.HUNTRESS
                    && item instanceof RangedWeapon
                    && MercenaryHelper.isBowWeapon((RangedWeapon) item)) {
                candidates.add(item);
            }
            else if (slot == EquipmentSlot.ARMOR && item instanceof Armor && !(item instanceof BirthdaySuit)) {
                candidates.add(item);
            }
        }

        return candidates;
    }

    private boolean isHeroEquippedItem(Item item) {
        return item instanceof EquipableItem && ((EquipableItem) item).getEquipped();
    }

    private Item getEquipmentItem(EquipmentSlot slot) {
        if (slot == EquipmentSlot.ARMOR) {
            return mercenary.getMercenaryArmor();
        }

        if (mercenary.getMercenaryWeapon() != null) {
            return mercenary.getMercenaryWeapon();
        }

        if (mercenary.getMercenaryType() == MercenaryHelper.MercenaryType.HUNTRESS
                && MercenaryHelper.isBowWeapon(mercenary.getMercenaryRangedWeapon())) {
            return mercenary.getMercenaryRangedWeapon();
        }

        return null;
    }

    private String getEquipmentLabel(EquipmentSlot slot) {
        Item item = getEquipmentItem(slot);
        if (item != null) {
            return item.getName();
        }

        return slot == EquipmentSlot.WEAPON ? "Select weapon" : "Select armor";
    }

    private GameSprite getEquipmentIcon(EquipmentSlot slot) {
        Item item = getEquipmentItem(slot);
        if (item != null && item.getGameSprite() != null) {
            GameSprite icon = item.getGameSprite().clone();
            icon.setRotation(0f);
            return icon;
        }

        return new GameSprite(slot == EquipmentSlot.WEAPON
                ? "images/misc/extracted items/WEAPON.png"
                : "images/misc/extracted items/ARMOR.png", SLOT_ICON_SIZE, SLOT_ICON_SIZE);
    }

    private final class EquipmentSlotButton extends RedButton {
        private final EquipmentSlot slot;

        private EquipmentSlotButton(float x, float y, EquipmentSlot slot) {
            super(x, y, SLOT_WIDTH, SLOT_HEIGHT);
            this.slot = slot;
            setText(getEquipmentLabel(slot));
        }

        @Override
        public void clicked() {
            onEquipmentSlotClicked(slot);
        }

        @Override
        public void draw(Batch batch) {
            setText(getEquipmentLabel(slot));
            super.draw(batch);

            GameSprite icon = getEquipmentIcon(slot);
            icon.setWidth(Math.round(SLOT_ICON_SIZE));
            icon.setHeight(Math.round(SLOT_ICON_SIZE));
            icon.setPosition(x + 22f, y + (SLOT_HEIGHT - SLOT_ICON_SIZE) / 2f);
            icon.draw(batch);
        }
    }
}