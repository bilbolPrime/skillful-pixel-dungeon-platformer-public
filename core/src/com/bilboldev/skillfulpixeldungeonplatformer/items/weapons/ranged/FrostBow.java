package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

public class FrostBow extends Bow {
    {
        gs = new GameSprite("images/misc/extracted items/ForstBow.png", 45, 45);
        name = "Frost Bow";
        description = "A magically chilled bow whose arrows sap speed from anything they strike.";
        damage = 6f;
        goldCost = 55;
    }

    @Override
    protected ThrownProjectile buildArrowProjectile() {
        return new ThrownProjectile() {
            {
                setGameSprite(new GameSprite("images/misc/extracted items/Arrow.png", 45, 45));
            }

            @Override
            public void onUnitCollision(Unit target) {
                if (hasHitTarget(target)) {
                    return;
                }

                if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, false, accuracyMultiplier)) {
                    new Slow().setPermanent(false).setDuration(2.5f).setOwner(target);
                    playSound(Sounds.HIT, 0.4f);
                    recordHitTarget(target);
                    if (owner instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero
                            && ((com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero) owner).handleBowProjectileImpact(target, attackingItem, damage, true, this)) {
                        return;
                    }
                } else {
                    playSound(Sounds.MISS, 0.4f);
                }
                markUsed();
            }
        };
    }
}
