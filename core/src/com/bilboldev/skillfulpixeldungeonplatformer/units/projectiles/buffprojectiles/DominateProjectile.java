package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Dominate;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Enrage;

public class DominateProjectile extends BuffProjectile {
    {

        buff = Dominate.class;
        negative = true;
        text = "Dominated...";
    }
}

