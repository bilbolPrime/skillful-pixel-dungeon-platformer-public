package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Hammer extends MeleeWeapon {
    {
    gs = new GameSprite("images/misc/extracted items/WAR_HAMMER.png", 45, 45);
        damage = 35;
        name = "Hammer";
        description = "Few creatures can withstand the crushing blow of this towering mass of lead and steel, " +
                "but only the strongest of adventurers can use it effectively.";

        tier = 4;
        speed = 0.8f;
        goldCost = 550;
        baseRequiredStrength = 15;
    }
}

