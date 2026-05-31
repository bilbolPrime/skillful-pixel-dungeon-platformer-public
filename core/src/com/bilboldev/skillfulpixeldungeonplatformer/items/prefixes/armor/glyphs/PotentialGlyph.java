package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class PotentialGlyph extends ArmorGlyph {
    {
        name = "potential glyph";
        description = "> adjacent hits can unleash shared lightning damage";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || attacker == null || attacker == defender || attacker.isDead() || appliedDamage < 1 || !EnhancementHelper.isAdjacent(defender, attacker)) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 15, 10, 45)) {
            return;
        }

        int shockDamage = EnhancementHelper.randomInclusive(1, appliedDamage);
        attacker.takeDamage(defender, null, shockDamage);
        if (!defender.isDead()) {
            defender.takeDamage(attacker, null, shockDamage);
        }
        EnhancementHelper.emitLightning(attacker);
        EnhancementHelper.emitLightning(defender);
    }
}