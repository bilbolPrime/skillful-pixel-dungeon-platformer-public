package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Burning;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Frost;

public class AntiEntropyGlyph extends ArmorGlyph {
    {
        name = "anti-entropy glyph";
        description = "> adjacent hits can freeze attackers and ignite the wearer";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || attacker == null || attacker == defender || attacker.isDead() || appliedDamage < 1 || !EnhancementHelper.isAdjacent(defender, attacker)) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 20, 7, 45)) {
            return;
        }

        int upgradeLevel = EnhancementHelper.getUpgradeLevel(defender.getArmor());
        EnhancementHelper.applyOrRefresh(attacker, new Frost(), 2.5f + upgradeLevel * 0.5f);
        EnhancementHelper.applyOrRefresh(defender, new Burning(), 3f + upgradeLevel * 0.5f);
    }
}