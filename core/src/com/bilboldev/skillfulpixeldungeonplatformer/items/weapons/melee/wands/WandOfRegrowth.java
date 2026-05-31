package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class WandOfRegrowth extends Wand {
    {
        manaCost = 4;
        name = "Wand of Regrowth";
        description = "A wand that floods the wielder with renewing life, restoring health, mana, and clearing poison.";
        gs = new GameSprite("images/wands/WAND_BAMBOO.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        if (owner == null) {
            return;
        }

        owner.heal(Math.max(2, Math.round(scalePower(6f))));
        owner.removeBuff(new Poisoned());
        if (owner instanceof Hero) {
            ((Hero) owner).modifyMana(Math.max(1, Math.round(scalePower(2f))));
        }
    }
}