package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Starving;

public class MetabolismGlyph extends ArmorGlyph {
    {
        name = "metabolism glyph";
        description = "> can convert stored energy into a burst of healing";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || appliedDamage < 1 || defender.getBuff(Starving.class) != null) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 20, 5, 40)) {
            return;
        }

        defender.heal(Math.max(1, defender.getMaxHP() / 10 + EnhancementHelper.getUpgradeLevel(defender.getArmor())));
    }
}