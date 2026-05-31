package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class DarkGold extends Item {
    {
        name = "Dark Gold Ore";
        description = "This metal is called dark not because of its color, but because it melts under daylight, making it useless on the surface.";
        gs = new GameSprite("images/misc/extracted items/ORE.png", 45, 45);
        quantity = 1;
        goldCost = 1;
    }
}