package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Weaken;

public class WarlockBolt extends FireBolt {
    private static final float WEAKNESS_DURATION = 40f;

    {
        setDamageRange(12f, 18f);
        lifeSpan = 65f;
    }

    @Override
    public void onUnitCollision(Unit target) {
        boolean hit = owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack);
        markUsed();
        if (hit) {
            if (target != null && target.isHero && RandomHelper.getInstance().randomChance(50)) {
                Weaken weaken = (Weaken) target.getBuff(Weaken.class);
                if (weaken != null) {
                    weaken.setPermanent(false).setDuration(WEAKNESS_DURATION);
                } else {
                    new Weaken().setPermanent(false).setDuration(WEAKNESS_DURATION).setOwner(target);
                }
            }
            playSound(Sounds.BLAST, 10f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
        EffectsHelper.getInstance().spark(this);
    }
}