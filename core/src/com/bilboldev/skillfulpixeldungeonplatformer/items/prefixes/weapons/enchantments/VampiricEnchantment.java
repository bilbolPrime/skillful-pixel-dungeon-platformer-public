package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class VampiricEnchantment extends WeaponEnhancement {
    {
        name = "vampiric enchantment";
        description = "> successful hits leech health back to the attacker";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (attacker == null || attacker.isDead() || appliedDamage < 1 || attacker.getHP() >= attacker.getMaxHP()) {
            return;
        }

        float healRatio = 0.33f + 0.08f * Math.min(2, EnhancementHelper.getUpgradeLevel(weapon));
        attacker.heal(Math.max(1, Math.round(appliedDamage * healRatio)));
    }
}