package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class LuckyEnchantment extends WeaponEnhancement {
    {
        name = "lucky enchantment";
        description = "> rerolls damage and keeps the best result";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public float modifyAttackDamage(Unit attacker, Unit target, Weapon weapon, float rolledDamage) {
        if (attacker == null || target == null || target.isDead() || weapon == null || rolledDamage < 1f) {
            return rolledDamage;
        }

        float bestRoll = rolledDamage;
        int rerolls = 1 + EnhancementHelper.getUpgradeLevel(weapon);
        for (int i = 0; i < rerolls; i++) {
            float candidate = Math.max(1f, weapon.getDamage());
            if (candidate > bestRoll) {
                bestRoll = candidate;
            }
        }

        return bestRoll;
    }
}