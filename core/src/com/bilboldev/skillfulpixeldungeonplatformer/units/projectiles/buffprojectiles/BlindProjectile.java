package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Blind;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Weaken;

public class BlindProjectile extends BuffProjectile {
    {

        buff = Blind.class;
        negative = true;
        text = "Blinded...";
    }
}

