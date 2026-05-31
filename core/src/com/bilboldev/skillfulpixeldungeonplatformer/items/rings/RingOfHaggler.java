package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class RingOfHaggler extends Ring {
    private static final float BUY_DISCOUNT = 0.75f;
    private static final float SELL_BONUS = 1.25f;

    {
        name = "Ring of Haggler";
        description = "This ring marks you as a trusted trader, lowering buy prices and improving sell offers.";
        gs = new GameSprite("images/misc/extracted items/RING_GARNET.png", 45, 45);
    }

    public static boolean isActive() {
        return countEquipped(RingOfHaggler.class) > 0;
    }

    public static int adjustBuyPrice(int basePrice) {
        return Math.max(1, Math.round(basePrice * (isActive() ? BUY_DISCOUNT : 1f)));
    }

    public static int adjustSellPrice(int basePrice) {
        return Math.max(1, Math.round(basePrice * (isActive() ? SELL_BONUS : 1f)));
    }
}