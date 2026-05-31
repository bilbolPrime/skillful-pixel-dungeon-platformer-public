package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Terrorized;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Vertigo;

public class HorrorEnchantment extends WeaponEnhancement {
    {
        name = "eldritch enchantment";
        description = "> hits can leave enemies terrified or heroes reeling with vertigo";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 20, 11, 43)) {
            return;
        }

        float duration = 4f + EnhancementHelper.getUpgradeLevel(weapon);
        if (target instanceof Hero) {
            EnhancementHelper.applyOrRefresh(target, new Vertigo(), duration);
            return;
        }

        EnhancementHelper.applyOrRefresh(target, new Terrorized(), duration, false);
    }
}