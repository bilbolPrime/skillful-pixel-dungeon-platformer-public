package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs;

import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.EnhancementHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.EarthrootArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Rooted;

public class EntanglingGlyph extends ArmorGlyph {
    {
        name = "entanglement glyph";
        description = "> can root attackers while raising earthroot armor around the wearer";
        modifier = 1f;
        levelModifier = 0;
    }

    @Override
    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {
        if (defender == null || attacker == null || attacker == defender || attacker.isDead() || appliedDamage < 1 || !EnhancementHelper.rollProc(defender.getArmor(), 25, 0, 25)) {
            return;
        }

        int upgradeLevel = EnhancementHelper.getUpgradeLevel(defender.getArmor());
        float rootDuration = Math.max(1.5f, 4f - upgradeLevel * 0.5f);

        Buff rooted = attacker.getBuff(Rooted.class);
        if (rooted instanceof Rooted) {
            ((Rooted) rooted).setAnchor(attacker.x, attacker.y, attacker.getRoom());
            rooted.setPermanent(false).setDuration(rootDuration);
        }
        else {
            new Rooted().setAnchor(attacker.x, attacker.y, attacker.getRoom()).setPermanent(false).setDuration(rootDuration).setOwner(attacker);
        }

        Buff existingArmor = defender.getBuff(EarthrootArmor.class);
        if (existingArmor != null) {
            defender.getBuffs().remove(existingArmor);
        }

        new EarthrootArmor()
                .setAnchor(defender.x, defender.y, defender.getRoom())
                .setArmor(5f * (upgradeLevel + 1))
                .setOwner(defender);
    }
}