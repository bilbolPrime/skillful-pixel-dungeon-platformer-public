package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfEvasion extends Ring {
    private static final float EVASION_BONUS = 0.2f;

    {
        name = "Ring of Evasion";
        description = "This ring helps you slip out of danger, increasing evasion while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_QUARTZ.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifyEvasionMultiplier(EVASION_BONUS * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifyEvasionMultiplier(-EVASION_BONUS * getLevel());
    }
}