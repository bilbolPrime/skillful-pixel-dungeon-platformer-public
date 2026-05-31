package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class CorpseDust extends Item {
    {
        name = "Corpse Dust";
        description = "The ball of corpse dust doesn't differ outwardly from a regular dust ball, but it feels wrong in your hands.";
        gs = new GameSprite("images/misc/extracted items/DUST.png", 45, 45);
        goldCost = 0;
    }
}