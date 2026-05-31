package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class ShamanBolt extends FireBolt {
    {
        gs = new GameSprite("images/misc/black-particle.png", 18, 18);
        setDamageRange(2f, 12f);
        lifeSpan = 65f;
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack)) {
            playSound(Sounds.ZAP, 1f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
        markUsed();
        EffectsHelper.getInstance().blackSpark(this);
    }
}