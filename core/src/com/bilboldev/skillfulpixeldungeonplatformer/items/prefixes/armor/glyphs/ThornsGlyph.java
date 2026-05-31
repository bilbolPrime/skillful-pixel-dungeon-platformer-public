package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class ThornsGlyph extends ArmorGlyph {
    {
        name = "thorns glyph";
        description = "> reflects some damage back to attackers";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (attacker == null || attacker.isDead() || damagingItem == null || appliedDamage < 1) {
            return;
        }

        attacker.takeDamage(defender, null, Math.max(1f, appliedDamage * 0.25f));
    }
}