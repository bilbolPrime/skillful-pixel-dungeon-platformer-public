package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Vulnerable;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Weaken;

public class WeakenProjectile extends BuffProjectile {
    {

        buff = Weaken.class;
        negative = true;
        text = "Weakened...";
    }
}

