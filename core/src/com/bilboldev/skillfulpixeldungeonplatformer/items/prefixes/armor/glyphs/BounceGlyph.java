package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class BounceGlyph extends ArmorGlyph {
    {
        name = "bounce glyph";
        description = "> adjacent attackers can be knocked away";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || attacker == null || attacker == defender || attacker.isDead() || appliedDamage < 1 || !EnhancementHelper.isAdjacent(defender, attacker)) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 20, 8, 45)) {
            return;
        }

        EnhancementHelper.pushUnitHorizontally(attacker, attacker.x < defender.x ? -1f : 1f);
    }
}