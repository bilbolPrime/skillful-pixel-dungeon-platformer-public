package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class DwarfToken extends Item {
    {
        name = "Dwarf Token";
        description = "Many dwarves and some of their larger creations carry these small pieces of metal of unknown purpose.";
        gs = new GameSprite("images/misc/extracted items/TOKEN.png", 45, 45);
        quantity = 1;
        goldCost = 100;
    }
}