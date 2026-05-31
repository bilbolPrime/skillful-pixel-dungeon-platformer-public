package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Sword extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/SWORD.png", 45, 45);
        damage = 10;
        name = "Sword";
        description = "The razor-sharp length of steel blade shines reassuringly.";

        goldCost = 100;
        tier = 2;
        speed = 1f;
        baseRequiredStrength = 3;
    }
}

