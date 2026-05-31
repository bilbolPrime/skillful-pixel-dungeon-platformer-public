package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class MultiplicityGlyph extends ArmorGlyph {
    {
        name = "multiplicity glyph";
        description = "> can split off a mirror image when the wearer is struck";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (!(defender instanceof Hero) || appliedDamage < 1) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 20, 5, 40)) {
            return;
        }

        if (EnhancementHelper.spawnMirrorImage(defender)) {
            defender.takeDamage(defender, null, Math.max(1f, defender.getMaxHP() / 6f));
        }
    }
}