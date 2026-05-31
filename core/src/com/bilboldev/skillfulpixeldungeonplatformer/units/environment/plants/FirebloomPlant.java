package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class FirebloomPlant extends Plant {
    {
        initPlant(0, "When something touches a Firebloom, it bursts into flames.");
    }

    @Override
    protected void onActivate(Unit target) {
        for (Unit unit : getUnitsNearPlant(2.5f, 2f)) {
            unit.takeDamage(this, null, unit == target ? 8f : 6f);
            for (int i = 0; i < 3; i++) {
                EffectsHelper.getInstance().spark(unit);
            }
        }
        emitBurst("images/misc/red.png", 5, 8f, 24f);
    }
}