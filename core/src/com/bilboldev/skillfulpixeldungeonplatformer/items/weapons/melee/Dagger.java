package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Dagger extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/DAGGER.png", 45, 45);
        damage = 5;
        name = "Dagger";
        description = "A simple iron dagger with a well worn wooden handle.";
        tier = 1;
        speed = 1.5f;
        goldCost = 10;
    }
}

