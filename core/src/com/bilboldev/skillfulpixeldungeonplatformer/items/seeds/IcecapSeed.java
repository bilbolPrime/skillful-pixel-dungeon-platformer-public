package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.IcecapPlant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;

public class IcecapSeed extends Seed {
    {
        name = "Seed of Icecap";
        description = "Upon touching an Icecap excretes a pollen, which freezes everything in its vicinity.";
        gs = new GameSprite("images/misc/extracted items/SEED_ICECAP.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new IcecapPlant();
    }

    @Override
    protected String getPlantName() {
        return "Icecap";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Frost";
    }
}