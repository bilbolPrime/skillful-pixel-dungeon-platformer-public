package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Paralyzed;

public class ParalysisEnchantment extends WeaponEnhancement {
    {
        name = "stunning enchantment";
        description = "> hits can leave enemies paralyzed";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 13, 9, 30)) {
            return;
        }

        float duration = 1.5f + EnhancementHelper.getUpgradeLevel(weapon) * 0.5f;
        EnhancementHelper.applyOrRefresh(target, new Paralyzed(), duration, false);
    }
}