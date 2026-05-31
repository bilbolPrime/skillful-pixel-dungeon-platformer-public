package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.RotberryPlant;

public class RotberrySeed extends Seed {
    {
        name = "Seed of Rotberry";
        description = "Berries of this shrub taste like sweet, sweet death.";
        gs = new GameSprite("images/misc/extracted items/SEED_ROTBERRY.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new RotberryPlant();
    }

    @Override
    protected String getPlantName() {
        return "Rotberry";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Strength";
    }
}