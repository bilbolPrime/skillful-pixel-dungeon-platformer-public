package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Confused;

public class DreamweedPlant extends Plant {
    {
        initPlant(3, "Upon touching a Dreamweed it secretes a glittering cloud of confusing gas.");
    }

    @Override
    protected void onActivate(Unit target) {
        for (Unit unit : getUnitsNearPlant(1.75f, 1.25f)) {
            Confused confused = new Confused();
            confused.setPermanent(false);
            confused.setDuration(6f);
            confused.setOwner(unit);
        }
        emitBurst("images/misc/grey.png", 6, 7f, 20f);
    }
}