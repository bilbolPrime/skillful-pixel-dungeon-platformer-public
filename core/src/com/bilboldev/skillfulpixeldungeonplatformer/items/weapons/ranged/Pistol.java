package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public final class Pistol extends Gun {
    @Override protected String localizationKey() { return "custom.newitems.pistol"; }

    {
        name = "Pistol";
        description = "A reliable sidearm. Fires one stronger straight bullet from the ranged slot.";
        gs = new GameSprite(NewClassAssets.ItemArt.PISTOL.key(), 45, 45);
        tier = 2;
        speed = 1.10f;
        goldCost = 50;
    }
}
