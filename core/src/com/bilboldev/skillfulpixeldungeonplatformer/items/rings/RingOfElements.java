package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class RingOfElements extends Ring {
    private static final float DAMAGE_REDUCTION = 0.06f;

    {
        name = "Ring of Elements";
        description = "This ring cushions incoming damage. Each ring level reduces incoming damage by 6% while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_DIAMOND.png", 45, 45);
    }

    @Override
    protected void onEquipped(Hero hero) {
        hero.modifyIncomingDamageModifier(-DAMAGE_REDUCTION * getLevel());
    }

    @Override
    protected void onUnequipped(Hero hero) {
        hero.modifyIncomingDamageModifier(DAMAGE_REDUCTION * getLevel());
    }
}
