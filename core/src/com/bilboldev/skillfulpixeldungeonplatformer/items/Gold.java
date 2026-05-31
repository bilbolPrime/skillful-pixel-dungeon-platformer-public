package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Gold extends Item{
    private static final float QUANTITY_MULTIPLIER = 0.8f;

    {
        name = "Gold";
        description = "Gold coins that can be used to buy items.";
        gs = new GameSprite("images/misc/gold.png", 45, 45);
        quantity = scaleQuantity(1 + RandomHelper.getInstance().randomInt(MapHelper.getInstance().getDepth() * 3));
    }

    @Override
    public Gold setQuantity(int quantity) {
        super.setQuantity(scaleQuantity(quantity));
        return this;
    }

    private int scaleQuantity(int quantity) {
        if (quantity <= 0) {
            return 0;
        }

        return Math.max(1, Math.round(quantity * QUANTITY_MULTIPLIER));
    }
}

