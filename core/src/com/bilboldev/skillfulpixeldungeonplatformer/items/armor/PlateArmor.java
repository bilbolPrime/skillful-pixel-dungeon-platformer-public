package com.bilboldev.skillfulpixeldungeonplatformer.items.armor;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class PlateArmor extends Armor {
    {
    gs = new GameSprite("images/misc/extracted items/ARMOR_PLATE.png", 45, 45);
        name = "Plate armor";
        description =  "Enormous plates of metal are joined together into a suit that provides " +
                "unmatched protection to any adventurer strong enough to bear its staggering weight.";
        tier = 5;
        goldCost = 100;
        baseRequiredStrength = 15;
    }
}

