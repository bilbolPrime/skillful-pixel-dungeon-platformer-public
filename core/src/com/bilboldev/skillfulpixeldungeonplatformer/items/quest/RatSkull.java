package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class RatSkull extends Item {
    {
        name = "Giant Rat Skull";
        description = "It could be a nice hunting trophy, but it smells too bad to place it on a wall.";
        gs = new GameSprite("images/misc/extracted items/SKULL.png", 45, 45);
        goldCost = 100;
    }
}