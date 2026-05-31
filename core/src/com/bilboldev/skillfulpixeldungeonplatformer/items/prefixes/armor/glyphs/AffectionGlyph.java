package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Charm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Dominate;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class AffectionGlyph extends ArmorGlyph {
    {
        name = "affection glyph";
        description = "> adjacent attackers can be charmed into a brief truce";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || attacker == null || attacker == defender || attacker.isDead() || appliedDamage < 1 || !EnhancementHelper.isAdjacent(defender, attacker)) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 20, 5, 45)) {
            return;
        }

        float duration = 3f + EnhancementHelper.randomInclusive(0, 4);
        EnhancementHelper.applyOrRefresh(defender, new Charm(), duration, false);
        if (attacker instanceof Mob) {
            EnhancementHelper.applyOrRefresh(attacker, new Dominate(), duration, false);
        }
        else {
            EnhancementHelper.applyOrRefresh(attacker, new Charm(), duration, false);
        }
    }
}