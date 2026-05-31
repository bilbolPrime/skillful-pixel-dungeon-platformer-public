package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class InstabilityEnchantment extends WeaponEnhancement {
    {
        name = "unstable enchantment";
        description = "> each proc becomes a random enchantment effect";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {
        if (target == null || target.isDead() || appliedDamage < 1) {
            return;
        }

        Prefix rolled = createRolledEnchantment();
        if (rolled != null) {
            rolled.onAttack(attacker, target, weapon, appliedDamage);
        }
    }

    private Prefix createRolledEnchantment() {
        ArrayList<Prefix> enchantments = new ArrayList<Prefix>();
        enchantments.add(new DeathEnchantment());
        enchantments.add(new FireEnchantment());
        enchantments.add(new HorrorEnchantment());
        enchantments.add(new VampiricEnchantment());
        enchantments.add(new LuckyEnchantment());
        enchantments.add(new ParalysisEnchantment());
        enchantments.add(new VenomousEnchantment());
        enchantments.add(new ShockEnchantment());
        enchantments.add(new ChillingEnchantment());
        enchantments.add(new TemperingEnchantment());
        return enchantments.get(RandomHelper.getInstance().randomInt(enchantments.size()));
    }
}