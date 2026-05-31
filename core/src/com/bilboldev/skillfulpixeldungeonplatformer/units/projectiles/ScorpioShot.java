package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Cripple;

public class ScorpioShot extends FireBolt {
    {
        setDamageRange(20f, 32f);
        lifeSpan = 70f;
        magicAttack = false;
    }

    @Override
    public void onUnitCollision(Unit target) {
        boolean hit = owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack);
        markUsed();
        if (hit) {
            playSound(Sounds.BLAST, 10f);
            if (RandomHelper.getInstance().randomChance(50)) {
                Cripple cripple = (Cripple) target.getBuff(Cripple.class);
                if (cripple != null) {
                    cripple.setPermanent(false).setDuration(Cripple.DURATION);
                } else {
                    new Cripple().setPermanent(false).setDuration(Cripple.DURATION).setOwner(target);
                }
            }
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
        EffectsHelper.getInstance().spark(this);
    }
}