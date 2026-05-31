package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

public class CurareDart extends RangedWeapon {
    {
        gs = new GameSprite("images/misc/extracted items/CURARE_DART.png", 45, 45);
        name = "Curare Dart";
        description = "A nasty toxin-tipped dart that briefly cripples what it hits.";
        tier = 1;
        damage = 2f;
        speed = 1.2f;
        ammo = 6;
        goldCost = 12;
    }

    @Override
    public void createProjectile() {
        if (owner == null) {
            return;
        }

        if (owner.isInvisible()) {
            owner.setInvisible(false);
        }

        ThrownProjectile thrownProjectile = new ThrownProjectile() {
            @Override
            public void onUnitCollision(Unit target) {
                if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, false)) {
                    new Slow().setPermanent(false).setDuration(3f).setOwner(target);
                    playSound(Sounds.HIT, 0.4f);
                } else {
                    playSound(Sounds.MISS, 0.4f);
                }
                markUsed();
            }
        }.setOwner(owner).setAttackingItem(this).setGameSprite(new GameSprite("images/misc/extracted items/CURARE_DART.png", 45, 45));
        thrownProjectile.setDamage(getDamage());
        thrownProjectile.x = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        thrownProjectile.y = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 3f;
        thrownProjectile.speedY = 18f;
        thrownProjectile.facingRight = owner.facingRight;
        thrownProjectile.setSpeedX(owner.facingRight ? 780 : -780);
        thrownProjectile.isFriendly = owner.isFriendly;
        UnitHelper.getInstance().addUnit(thrownProjectile);

        SoundHelper.GetSingleton().play(Sounds.MISS,0.4f);
        useAmmo();
    }
}