package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.EarthrootPlant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;

public class EarthrootSeed extends Seed {
    {
        name = "Seed of Earthroot";
        description = "When a creature touches an Earthroot, its roots create a kind of natural armor around it.";
        gs = new GameSprite("images/misc/extracted items/SEED_EARTHROOT.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new EarthrootPlant();
    }

    @Override
    protected String getPlantName() {
        return "Earthroot";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Paralytic Gas";
    }
}