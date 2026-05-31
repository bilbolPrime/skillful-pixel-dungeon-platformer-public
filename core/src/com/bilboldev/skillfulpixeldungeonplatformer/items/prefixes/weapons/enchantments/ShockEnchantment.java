package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class ShockEnchantment extends WeaponEnhancement {
    {
        name = "shocking enchantment";
        description = "> hits can unleash chain lightning into nearby foes";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (attacker == null || target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 25, 12, 50)) {
            return;
        }

        EnhancementHelper.emitLightning(target);
        ArrayList<Unit> struck = new ArrayList<Unit>();
        struck.add(target);

        float chainDamage = Math.max(1f, appliedDamage * 0.5f);
        Unit current = target;
        int remainingJumps = 2 + EnhancementHelper.getUpgradeLevel(weapon);

        while (remainingJumps-- > 0 && chainDamage >= 1f) {
            Unit nextTarget = EnhancementHelper.findNearestHostile(attacker, current, struck, 2.5f);
            if (nextTarget == null) {
                break;
            }

            nextTarget.takeDamage(attacker, weapon, chainDamage);
            EnhancementHelper.emitLightning(nextTarget);
            struck.add(nextTarget);
            current = nextTarget;
            chainDamage *= 0.5f;
        }
    }
}