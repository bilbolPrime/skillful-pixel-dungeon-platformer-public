package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles.ShurikenProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Shuriken extends RangedWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/SHURIKEN.png", 45, 45);
        projectile = new ShurikenProjectile();
        name = "Shuriken";
        description = "Balanced throwing stars that fly quickly and can be loosed in rapid succession.";
        tier = 1;
        damage = 4f;
        speed = 1.5f;
        ammo = 8;
        goldCost = 15;
    }
}