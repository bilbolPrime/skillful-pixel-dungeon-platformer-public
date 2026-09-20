package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Knuckles extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/KNUCKLEDUSTER.png", 45, 45);
        damage = 4;
        name = "Knuckles";
        description = "A piece of iron shaped to fit around the knuckles.";

        tier = 1;
        speed = 2f;
        goldCost = 10;
    }
}

