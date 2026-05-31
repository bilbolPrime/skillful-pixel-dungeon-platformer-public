package com.bilboldev.skillfulpixeldungeonplatformer.items.armor;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ScaleArmor extends Armor {
    {
        gs = new GameSprite("images/misc/extracted items/ARMOR_SCALE.png", 45, 45);
        name = "Scale armor";
        description =  "The metal scales sewn onto a leather vest create a flexible, yet protective armor.";
        tier = 4;
        goldCost = 60;
        baseRequiredStrength = 10;
    }
}

