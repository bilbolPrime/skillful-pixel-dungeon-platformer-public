package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;

public class MercenaryItemWindow extends ItemWindow {
    private final MercenaryAlly mercenary;
    private final MercenaryWindow.EquipmentSlot slot;

    public MercenaryItemWindow(Item item, MercenaryAlly mercenary, MercenaryWindow.EquipmentSlot slot) {
        super(item);
        this.mercenary = mercenary;
        this.slot = slot;
    }

    public Window addDropAction() {
        height += actionButtons.size() == 0 ? 200f : 0f;
        y = ConstantsHelper.SCREEN_HEIGHT / 2f - height / 2f;

        actionButtons.add(new RedButton(x + width - 500f, y + 100f, 400f, 100f) {
            @Override
            public void click() {
                dropEquipment();
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("DROP"));

        return this;
    }

    private void dropEquipment() {
        if (mercenary == null || mercenary.isDead()) {
            return;
        }

        Item equipment;
        if (slot == MercenaryWindow.EquipmentSlot.WEAPON) {
            equipment = item instanceof RangedWeapon
                    ? mercenary.clearMercenaryRangedWeapon()
                    : mercenary.clearMercenaryWeapon();
        }
        else {
            equipment = mercenary.clearMercenaryArmor();
        }
        if (equipment == null) {
            return;
        }

        clearOwnership(equipment);
        equipment.drop(mercenary.x, mercenary.y, mercenary.getRoom());
    }

    private void clearOwnership(Item equipment) {
        if (!(equipment instanceof EquipableItem)) {
            return;
        }

        ((EquipableItem) equipment).setOwner(null);
        ((EquipableItem) equipment).setEquippedState(false);
    }
}