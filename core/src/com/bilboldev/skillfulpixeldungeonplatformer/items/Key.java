package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Key extends Item {
    public Key() {
        name = "Iron key";
        description = "A small iron key that unlocks a locked door on this floor.";
        gs = new GameSprite("images/misc/keys.png", 32, 32);
        quantity = 1;
        goldCost = 0;
    }
}
