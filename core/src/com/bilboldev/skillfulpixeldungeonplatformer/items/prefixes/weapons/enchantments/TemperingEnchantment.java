package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class TemperingEnchantment extends WeaponEnhancement {
    {
        name = "tempered enchantment";
        description = "> stabilizes weak damage rolls into steadier hits";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (attacker == null || target == null || target.isDead() || weapon == null || appliedDamage < 1) {
            return;
        }

        float minDamage = weapon.min();
        float maxDamage = weapon.max();
        int upgradeLevel = EnhancementHelper.getUpgradeLevel(weapon);
        int temperedFloor = Math.max(1, Math.round(minDamage + (maxDamage - minDamage) * (0.25f + 0.1f * Math.min(2, upgradeLevel))));
        int bonusDamage = temperedFloor - appliedDamage;
        if (bonusDamage > 0) {
            target.takeDamage(attacker, weapon, bonusDamage);
        }
    }
}