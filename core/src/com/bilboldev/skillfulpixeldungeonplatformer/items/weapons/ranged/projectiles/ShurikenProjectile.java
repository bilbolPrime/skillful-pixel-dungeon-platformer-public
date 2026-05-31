package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ShurikenProjectile extends Projectile {
    {
        gs = new GameSprite("images/misc/extracted items/SHURIKEN.png", 45, 45);
        speedY = 20;
        alignRotationToVelocity = false;
        rotationOffset = 0f;
        spinSpeed = 1080f;
    }
}