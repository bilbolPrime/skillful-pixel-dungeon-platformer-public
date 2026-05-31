package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.RotberrySeed;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Rooted;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.ToxicGasCloud;

public class RotberryPlant extends Plant {
    {
        initPlant(7, "Berries of this shrub taste like sweet, sweet death.");
    }

    @Override
    protected void onActivate(Unit target) {
        ToxicGasCloud cloud = new ToxicGasCloud();
        cloud.x = x;
        cloud.y = y;
        cloud.floorY = y;
        cloud.setRoom(room);
        UnitHelper.getInstance().addUnit(cloud);

        Rooted rooted = new Rooted().setAnchor(x, y, room);
        rooted.setPermanent(false);
        rooted.setDuration(3f);
        rooted.setOwner(target);

        dropItem(new RotberrySeed());
        emitBurst("images/misc/green.png", 7, 8f, 22f);
    }
}