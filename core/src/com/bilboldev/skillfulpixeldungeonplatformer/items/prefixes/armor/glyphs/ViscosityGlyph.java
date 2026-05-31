package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.DeferredDamage;

public class ViscosityGlyph extends ArmorGlyph {
    {
        name = "viscosity glyph";
        description = "> can defer incoming damage into a lingering aftershock";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || appliedDamage < 1 || attacker == defender) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 15, 10, 45)) {
            return;
        }

        defender.heal(appliedDamage);
        DeferredDamage existing = (DeferredDamage) defender.getBuff(DeferredDamage.class);
        if (existing != null) {
            existing.addPendingDamage(appliedDamage);
            return;
        }

        new DeferredDamage().addPendingDamage(appliedDamage).setOwner(defender);
    }
}