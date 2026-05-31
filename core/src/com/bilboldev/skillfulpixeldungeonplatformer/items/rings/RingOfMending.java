package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfMending extends Ring {
    private static final float REGEN_BONUS = 0.2f;

    {
        name = "Ring of Mending";
        description = "This ring strengthens the body's natural repair, increasing regeneration by 20% while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_EMERALD.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifyRegenerationRate(REGEN_BONUS * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifyRegenerationRate(-REGEN_BONUS * getLevel());
    }
}