package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Mace extends MeleeWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/MACE.png", 45, 45);
        damage = 15;
        name = "Mace";
        description = "The iron head of this weapon inflicts substantial damage.";

        tier = 2;
        speed = 0.8f;
        goldCost = 125;
        baseRequiredStrength = 3;
    }
}

