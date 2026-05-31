package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Blind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Enrage;

public class EnrageProjectile extends BuffProjectile {
    {

        buff = Enrage.class;
        negative = true;
        text = "Enrage...";
    }
}

