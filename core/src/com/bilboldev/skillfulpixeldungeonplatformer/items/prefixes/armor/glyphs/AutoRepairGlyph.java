package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class AutoRepairGlyph extends ArmorGlyph {
    {
        name = "auto-repair glyph";
        description = "> spends gold to mend the wearer when struck";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (!(defender instanceof Hero) || appliedDamage < 1) {
            return;
        }

        Armor armor = defender.getArmor();
        if (armor == null) {
            return;
        }

        int goldCost = Math.max(1, armor.getTier());
        if (InventoryHelper.getInstance().getGold() < goldCost) {
            return;
        }

        InventoryHelper.getInstance().modifyGold(-goldCost);
        defender.heal(Math.max(1, armor.getTier() / 2));
    }
}