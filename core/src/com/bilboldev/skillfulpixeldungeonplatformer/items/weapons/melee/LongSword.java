package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class LongSword extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/LONG_SWORD.png", 45, 45);
        damage = 14;
        name = "Long sword";
        description = "This towering blade inflicts heavy damage by investing its heft into every cut.";

        tier = 3;
        speed = 1f;
        goldCost = 150;
        baseRequiredStrength = 6;
    }
}

