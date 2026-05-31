package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfPower extends Ring {
    private static final float WAND_POWER_BONUS = 0.2f;

    {
        name = "Ring of Power";
        description = "This ring amplifies wand energy, making magical attacks and effects stronger while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_RUBY.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifyWandPowerModifier(WAND_POWER_BONUS * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifyWandPowerModifier(-WAND_POWER_BONUS * getLevel());
    }
}