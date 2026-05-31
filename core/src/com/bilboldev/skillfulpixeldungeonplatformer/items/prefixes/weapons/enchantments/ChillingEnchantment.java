package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;

public class ChillingEnchantment extends WeaponEnhancement {
    {
        name = "chilling enchantment";
        description = "> hits can slow enemies with biting cold";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 25, 12, 50)) {
            return;
        }

        float duration = 2.5f + EnhancementHelper.getUpgradeLevel(weapon) * 0.5f;
        EnhancementHelper.applyOrRefresh(target, new Slow(), duration, false);
    }
}