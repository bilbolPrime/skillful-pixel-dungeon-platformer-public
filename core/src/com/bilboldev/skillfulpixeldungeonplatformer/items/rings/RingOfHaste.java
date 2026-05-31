package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfHaste extends Ring {
    private static final float SPEED_BONUS = 0.15f;

    {
        name = "Ring of Haste";
        description = "This ring quickens the wearer's pace, increasing movement speed by 15% while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_SAPPHIRE.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifySpeedModifier(SPEED_BONUS * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifySpeedModifier(-SPEED_BONUS * getLevel());
    }
}