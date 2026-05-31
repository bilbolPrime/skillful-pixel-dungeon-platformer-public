package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.ToxicGasCloud;

public class StenchGlyph extends ArmorGlyph {
    {
        name = "stench glyph";
        description = "> adjacent hits can erupt into a toxic gas cloud";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || attacker == null || attacker == defender || attacker.isDead() || appliedDamage < 1 || !EnhancementHelper.isAdjacent(defender, attacker)) {
            return;
        }

        if (!EnhancementHelper.rollProc(defender.getArmor(), 20, 11, 45)) {
            return;
        }

        ToxicGasCloud cloud = new ToxicGasCloud().setOwner(defender);
        cloud.x = attacker.x;
        cloud.y = attacker.y;
        cloud.floorY = attacker.floorY;
        cloud.setRoom(attacker.getRoom());
        UnitHelper.getInstance().addUnit(cloud);
    }
}