package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.buffprojectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Vulnerable;

public class VulnerableProjectile extends BuffProjectile {
    {

        buff = Vulnerable.class;
        negative = true;
        text = "Vulnerable...";
    }
}

