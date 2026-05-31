package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.SungrassPlant;

public class SungrassSeed extends Seed {
    {
        name = "Seed of Sungrass";
        description = "Sungrass is renowned for its sap's healing properties.";
        gs = new GameSprite("images/misc/extracted items/SEED_SUNGRASS.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new SungrassPlant();
    }

    @Override
    protected String getPlantName() {
        return "Sungrass";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Healing";
    }
}