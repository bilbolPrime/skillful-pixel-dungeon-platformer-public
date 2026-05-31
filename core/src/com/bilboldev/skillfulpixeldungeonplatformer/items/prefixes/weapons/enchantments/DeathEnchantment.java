package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class DeathEnchantment extends WeaponEnhancement {
    {
        name = "grim enchantment";
        description = "> rare hits instantly kill non-boss enemies";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1 || (target instanceof Mob && ((Mob) target).isBoss())) {
            return;
        }

        if (!EnhancementHelper.rollProc(weapon, 8, 1, 10)) {
            return;
        }

        target.takeDamage(attacker, weapon, target.getHP());
    }
}