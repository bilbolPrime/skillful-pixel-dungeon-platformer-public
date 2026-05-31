package com.bilboldev.skillfulpixeldungeonplatformer.items.armor;

import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class LeatherArmor extends Armor {
    {
        gs = new GameSprite("images/misc/extracted items/ARMOR_LEATHER.png", 45, 45);
        name = "Leather armor";
        description = "Armor made from tanned monster hide. Not as light as cloth armor but provides better protection.";
        tier = 2;
        goldCost = 25;
    }
}

