package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles.TomahawkProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Tomahawk extends RangedWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/TOMAHAWK.png", 45, 45);
        projectile = new TomahawkProjectile();
        name = "Tomahawk";
        description = "A compact throwing axe that trades ammo count for brutal single-hit damage.";
        tier = 3;
        damage = 10f;
        speed = 0.9f;
        ammo = 3;
        goldCost = 20;
    }
}