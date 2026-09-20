package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public final class Handgun extends Gun {
    {
        name = "Handgun";
        description = "A compact, reliable firearm for the Mercenary. Fires one straight bullet from the ranged slot.";
        gs = new GameSprite(NewClassAssets.ItemArt.HANDGUN.key(), 45, 45);
        tier = 1;
        speed = 1.20f;
        goldCost = 25;
    }
}
