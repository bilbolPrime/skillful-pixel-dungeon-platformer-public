package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Cripple;

public class CripplingEnchantment extends WeaponEnhancement {
    private static final int PROC_CHANCE = 25;
    private static final float CRIPPLE_DURATION = 3f;

    {
        name = "crippling enchantment";
        description = "> hits have a chance to cripple enemies";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1 || !RandomHelper.getInstance().randomChance(PROC_CHANCE)) {
            return;
        }

        new Cripple().setDuration(CRIPPLE_DURATION).setOwner(target);
    }
}