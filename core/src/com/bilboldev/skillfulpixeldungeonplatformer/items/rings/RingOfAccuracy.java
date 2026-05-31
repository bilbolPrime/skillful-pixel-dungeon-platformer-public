package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfAccuracy extends Ring {
    private static final float ACCURACY_BONUS = 0.4f;

    {
        name = "Ring of Accuracy";
        description = "This ring steadies your strikes, increasing attack accuracy while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_TOPAZ.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifyAccuracyMultiplier(ACCURACY_BONUS * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifyAccuracyMultiplier(-ACCURACY_BONUS * getLevel());
    }
}