package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;

public class VenomousEnchantment extends WeaponEnhancement {
    {
        name = "venomous enchantment";
        description = "> hits can poison enemies over time";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 33, 13, 60)) {
            return;
        }

        float duration = 4f + EnhancementHelper.getUpgradeLevel(weapon) * 1.5f;
        EnhancementHelper.applyOrRefresh(target, new Poisoned().setDamageMultiplier(DifficultyHelper.getInstance().getEnemyDamageMultiplier(attacker)), duration);
    }
}
