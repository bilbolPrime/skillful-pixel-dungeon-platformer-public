package com.bilboldev.skillfulpixeldungeonplatformer.items.seeds;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.SorrowmossPlant;

public class SorrowmossSeed extends Seed {
    {
        name = "Seed of Sorrowmoss";
        description = "A Sorrowmoss is a flower with razor-sharp petals, coated with a deadly venom.";
        gs = new GameSprite("images/misc/extracted items/SEED_SORROWMOSS.png", 45, 45);
        quantity = 1;
    }

    @Override
    protected Plant createPlant() {
        return new SorrowmossPlant();
    }

    @Override
    protected String getPlantName() {
        return "Sorrowmoss";
    }

    @Override
    protected String getAlchemyResultName() {
        return "Potion of Toxic Gas";
    }
}