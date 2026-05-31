package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.FadeleafPlant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;

public class FadeleafSeed extends Seed {
    {
        name = "Seed of Fadeleaf";
        description = "Touching a Fadeleaf will teleport any creature to a random place on the current level.";
        gs = new GameSprite("images/misc/extracted items/SEED_FADELEAF.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new FadeleafPlant();
    }

    @Override
    protected String getPlantName() {
        return "Fadeleaf";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Mind Vision";
    }
}