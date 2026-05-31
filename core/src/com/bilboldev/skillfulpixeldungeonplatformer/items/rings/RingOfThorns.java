package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class RingOfThorns extends Ring {
    {
        name = "Ring of Thorns";
        description = "This ring lashes back at attackers, reflecting a portion of the damage they inflict.";
        gs = new GameSprite("images/misc/extracted items/RING_AMETHYST.png", 45, 45);
    }

    public static int getReflectedDamage(int incomingDamage) {
        int equipped = countEquipped(RingOfThorns.class);
        if (equipped < 1 || incomingDamage < 1) {
            return 0;
        }

        float reflectRatio = 0.2f + 0.05f * Math.max(0, equipped - 1);
        return Math.max(1, Math.round(incomingDamage * reflectRatio));
    }
}