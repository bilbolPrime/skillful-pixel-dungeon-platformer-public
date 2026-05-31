package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.FirebloomPlant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;

public class FirebloomSeed extends Seed {
    {
        name = "Seed of Firebloom";
        description = "When something touches a Firebloom, it bursts into flames.";
        gs = new GameSprite("images/misc/extracted items/SEED_FIREBLOOM.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new FirebloomPlant();
    }

    @Override
    protected String getPlantName() {
        return "Firebloom";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Liquid Flame";
    }
}