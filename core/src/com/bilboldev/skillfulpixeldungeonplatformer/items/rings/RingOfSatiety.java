package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfSatiety extends Ring {
    private static final float HUNGER_REDUCTION = 0.18f;

    {
        name = "Ring of Satiety";
        description = "This ring slows the gnaw of hunger, letting you go longer between meals.";
        gs = new GameSprite("images/misc/extracted items/RING_ONYX.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifyHungerRateModifier(-HUNGER_REDUCTION * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifyHungerRateModifier(HUNGER_REDUCTION * getLevel());
    }
}