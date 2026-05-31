package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

public class FlameBow extends Bow {
    {
        gs = new GameSprite("images/misc/extracted items/FlameBow.png", 45, 45);
        name = "Flame Bow";
        description = "A magically heated bow whose arrows explode with extra impact on a clean hit.";
        damage = 5f;
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

                if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, false)) {
                    target.takeDamage(owner, attackingItem, 2f);
                    EffectsHelper.getInstance().spark(this);
                    playSound(Sounds.BLAST, 0.6f);
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
