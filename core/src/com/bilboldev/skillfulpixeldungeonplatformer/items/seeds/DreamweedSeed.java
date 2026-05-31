package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.DreamweedPlant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;

public class DreamweedSeed extends Seed {
    {
        name = "Seed of Dreamweed";
        description = "Upon touching a Dreamweed it secretes a glittering cloud of confusing gas.";
        gs = new GameSprite("images/misc/extracted items/SEED_DREAMWEED.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new DreamweedPlant();
    }

    @Override
    protected String getPlantName() {
        return "Dreamweed";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Invisibility";
    }
}