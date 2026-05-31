package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Meat extends Rations {
    {
        name = "Meat";
        description = "A hearty cut of meat that fills the stomach just like standard rations.";
        gs = new GameSprite("images/misc/extracted items/MEAT.png", 45, 45);
        quantity = 1;
        goldCost = 50;
    }
}
