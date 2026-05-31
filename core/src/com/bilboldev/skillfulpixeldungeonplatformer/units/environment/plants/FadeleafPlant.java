package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class FadeleafPlant extends Plant {
    {
        initPlant(6, "Touching a Fadeleaf will teleport any creature to a random place on the current level.");
    }

    @Override
    protected void onActivate(Unit target) {
        if (teleportUnitToRandomPlatform(target)) {
            EffectsHelper.getInstance().splash(target);
            emitBurst("images/misc/yellow-dot.png", 6, 7f, 18f);
        }
    }
}