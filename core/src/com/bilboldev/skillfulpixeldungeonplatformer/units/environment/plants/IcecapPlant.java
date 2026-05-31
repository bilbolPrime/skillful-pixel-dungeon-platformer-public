package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.FreezingCloud;

public class IcecapPlant extends Plant {
    {
        initPlant(1, "Upon touching an Icecap excretes a pollen, which freezes everything in its vicinity.");
    }

    @Override
    protected void onActivate(Unit target) {
        FreezingCloud cloud = new FreezingCloud();
        cloud.x = x;
        cloud.y = y;
        cloud.floorY = y;
        cloud.setRoom(room);
        UnitHelper.getInstance().addUnit(cloud);
        emitBurst("images/misc/grey.png", 6, 8f, 20f);
    }
}