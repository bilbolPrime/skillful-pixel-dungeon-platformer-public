package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ShortSword extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/SHORT_SWORD.png", 45, 45);
        damage = 7;
        name = "Short sword";
        description = "It is indeed quite short, just a few inches longer, than a dagger.";

        tier = 1;
        speed = 1.1f;
        goldCost = 75;
    }
}

