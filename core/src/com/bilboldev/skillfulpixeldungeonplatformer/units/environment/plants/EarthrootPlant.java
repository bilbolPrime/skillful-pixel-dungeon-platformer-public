package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.EarthrootArmor;

public class EarthrootPlant extends Plant {
    {
        initPlant(5, "When a creature touches an Earthroot, its roots create a kind of natural armor around it.");
    }

    @Override
    protected void onActivate(Unit target) {
        EarthrootArmor armor = new EarthrootArmor().setAnchor(x, y, room).setArmor(target.getMaxHP());
        armor.setOwner(target);
        emitBurst("images/misc/yellow-dot.png", 6, 7f, 18f);
    }
}