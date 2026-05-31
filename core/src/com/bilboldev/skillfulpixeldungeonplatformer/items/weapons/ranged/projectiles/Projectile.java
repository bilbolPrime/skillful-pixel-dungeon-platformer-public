package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

public class Projectile {

    protected GameSprite gs;
    protected float speedY;
    protected boolean alignRotationToVelocity = true;
    protected float rotationOffset = -45f;
    protected float spinSpeed;

    public Projectile setAlignRotationToVelocity(boolean alignRotationToVelocity) {
        this.alignRotationToVelocity = alignRotationToVelocity;
        return this;
    }

    public Projectile setRotationOffset(float rotationOffset) {
        this.rotationOffset = rotationOffset;
        return this;
    }

    public Projectile setSpinSpeed(float spinSpeed) {
        this.spinSpeed = spinSpeed;
        return this;
    }

    public ThrownProjectile toThrownProjectile(){
        return new ThrownProjectile()
                .setSpeedY(this.speedY)
                .setGameSprite(this.gs.clone())
                .setAlignRotationToVelocity(alignRotationToVelocity)
                .setRotationOffset(rotationOffset)
                .setSpinSpeed(spinSpeed);
    }
}

