package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Rod extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/QUARTERSTAFF.png", 45, 45);
        damage = 8;
        name = "Rod";
        description = "A staff of hardwood, its ends are shod with iron.";

        tier = 1;
        speed = 1f;
        goldCost = 100;
        baseRequiredStrength = 4;
    }
}

