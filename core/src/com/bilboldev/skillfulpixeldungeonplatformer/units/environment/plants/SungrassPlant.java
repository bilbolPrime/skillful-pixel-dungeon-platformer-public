package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.SungrassHealth;

public class SungrassPlant extends Plant {
    {
        initPlant(4, "Sungrass is renowned for its sap's healing properties.");
    }

    @Override
    protected void onActivate(Unit target) {
        SungrassHealth health = new SungrassHealth().setAnchor(x, y, room);
        health.setOwner(target);
        target.heal(Math.max(1, target.getMaxHP() / 10));
        EffectsHelper.getInstance().heal(target);
        emitBurst("images/misc/yellow-dot.png", 6, 7f, 16f);
    }
}