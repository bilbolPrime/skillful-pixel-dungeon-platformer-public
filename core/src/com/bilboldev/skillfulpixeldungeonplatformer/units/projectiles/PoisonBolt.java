package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;

public class PoisonBolt extends FireBolt {
    private float poisonDuration = 4f;

    {
        gs = new GameSprite("images/misc/green.png", 20, 20);
        damage = 3f;
    }

    public PoisonBolt setPoisonDuration(float poisonDuration) {
        this.poisonDuration = poisonDuration;
        return this;
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().attackTarget(owner, target, attackingItem, damage, magicAttack)) {
            new Poisoned().setDuration(poisonDuration).setOwner(target);
            playSound(Sounds.ZAP, 1f);
        } else {
            playSound(Sounds.MISS, 0.4f);
        }

        markUsed();
        EffectsHelper.getInstance().blackSpark(this);
    }
}