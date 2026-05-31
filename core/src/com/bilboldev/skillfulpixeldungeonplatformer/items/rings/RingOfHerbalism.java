package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class RingOfHerbalism extends Ring {
    private static final int FOOD_HEAL_PER_LEVEL = 3;
    private static final int FOOD_MANA_PER_LEVEL = 4;

    {
        name = "Ring of Herbalism";
        description = "This ring helps you draw more recovery from simple rations, restoring a little health and mana when you eat.";
        gs = new GameSprite("images/misc/extracted items/RING_TOURMALINE.png", 45, 45);
    }

    public static int getFoodHealingBonus() {
        return getEquippedLevelSum(RingOfHerbalism.class) * FOOD_HEAL_PER_LEVEL;
    }

    public static int getFoodManaBonus() {
        return getEquippedLevelSum(RingOfHerbalism.class) * FOOD_MANA_PER_LEVEL;
    }
}