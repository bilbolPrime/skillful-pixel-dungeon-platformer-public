package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class TenguKnife extends FireBolt {
    {
        gs = new GameSprite("images/misc/extracted items/SHURIKEN.png", 45, 45);
        setDamageRange(8f, 15f);
        lifeSpan = 80f;
        magicAttack = false;
        alignRotationToVelocity = false;
        rotationOffset = 0f;
        spinSpeed = 1080f;
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack)) {
            playSound(Sounds.HIT, 0.6f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
        markUsed();
        EffectsHelper.getInstance().blackSpark(this);
    }
}