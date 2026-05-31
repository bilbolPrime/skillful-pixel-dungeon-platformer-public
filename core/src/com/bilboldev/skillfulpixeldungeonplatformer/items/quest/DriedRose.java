package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class DriedRose extends Item {
    {
        name = "Dried Rose";
        description = "The rose has dried long ago, but it has kept all its petals somehow.";
        gs = new GameSprite("images/misc/extracted items/ROSE.png", 45, 45);
        goldCost = 0;
    }
}