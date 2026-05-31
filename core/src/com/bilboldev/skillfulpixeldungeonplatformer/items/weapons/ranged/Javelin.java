package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles.SpearProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Javelin extends RangedWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/JAVELIN.png", 45, 45);
        projectile = new SpearProjectile();
        name = "Javelin";
        description = "A heavy throwing spear that hits hard but is thrown more deliberately.";
        tier = 2;
        damage = 9f;
        speed = 0.8f;
        ammo = 4;
        goldCost = 15;
    }
}