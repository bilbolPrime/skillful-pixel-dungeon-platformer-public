package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class DisplacementGlyph extends ArmorGlyph {
    {
        name = "displacement glyph";
        description = "> the wearer can blink to another spot when hurt";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || appliedDamage < 1 || attacker == defender) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 15, 10, 45)) {
            return;
        }

        EnhancementHelper.teleportUnitToRandomPlatform(defender, 1.5f, 50f);
    }
}