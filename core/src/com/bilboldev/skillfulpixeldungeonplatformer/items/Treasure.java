package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Treasure extends Item{
    {
        name = "Treasure";
        description = "A chest holding valuable treasure.\nPicking up the treasure may trigger alarms.";
        gs = new GameSprite("images/misc/extracted items/CHEST.png", 45, 45);
    }
}

