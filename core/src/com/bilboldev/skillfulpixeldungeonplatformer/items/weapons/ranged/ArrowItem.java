package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ArrowItem extends Item {
    {
        name = "Arrow";
        description = "Standard bow ammunition. Keep arrows in your pack; equipped bows consume one arrow per shot.";
        gs = new GameSprite("images/misc/extracted items/Arrow.png", 45, 45);
        quantity = 1;
        goldCost = 5;
    }

    @Override
    public String getBigDescription() {
        return getDescription() + "\n\n" + Messages.get(
            "custom.generated.this_is_a_stack_of_878540b56a",
            new Object[]{Math.max(1, quantity)});
    }
}