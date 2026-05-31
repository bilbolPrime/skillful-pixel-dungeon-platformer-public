package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;

public class SorrowmossPlant extends Plant {
    {
        initPlant(2, "A Sorrowmoss is a flower with razor-sharp petals, coated with a deadly venom.");
    }

    @Override
    protected void onActivate(Unit target) {
        Poisoned poisoned = new Poisoned();
        poisoned.setPermanent(false);
        poisoned.setDuration(4f + MapHelper.getInstance().getDepth() / 2f);
        poisoned.setOwner(target);
        emitBurst("images/misc/green.png", 6, 7f, 18f);
    }
}