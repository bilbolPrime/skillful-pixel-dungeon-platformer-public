package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Burning;

public class FireEnchantment extends WeaponEnhancement {
    {
        name = "blazing enchantment";
        description = "> hits can add fire damage and set enemies burning";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (attacker == null || target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 33, 14, 60)) {
            return;
        }

        int upgradeLevel = EnhancementHelper.getUpgradeLevel(weapon);
        int bonusDamage = EnhancementHelper.randomInclusive(1, 3 + Math.min(2, upgradeLevel));
        target.takeDamage(attacker, weapon, DifficultyHelper.getInstance().scaleEnemyDamage(attacker, bonusDamage));
        EffectsHelper.getInstance().spark(target);

        if (RandomHelper.getInstance().randomChance(50)) {
            EnhancementHelper.applyOrRefresh(target, new Burning().setDamageMultiplier(DifficultyHelper.getInstance().getEnemyDamageMultiplier(attacker)), 4f + upgradeLevel);
        }
    }
}
