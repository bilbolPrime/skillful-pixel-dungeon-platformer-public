package com.bilboldev.skillfulpixeldungeonplatformer.items.armor;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Cloth extends Armor {
    {
        gs = new GameSprite("images/misc/extracted items/ARMOR_CLOTH.png", 45, 45);
        name = "Cloth";
        description =  "This lightweight armor offers basic protection.";
        tier = 1;
        goldCost = 1;
    }
}

