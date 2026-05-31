package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class ReachBolt extends FireBolt {
    {
        gs = new GameSprite("images/misc/black-particle.png", 20, 20);
        damage = 0f;
    }

    @Override
    public void onUnitCollision(Unit target) {
        if (owner != null && UnitHelper.getInstance().tryHit(owner, target, attackingItem, true)) {
            float ownerX = owner.x;
            float ownerY = owner.y;
            float ownerFloorY = owner.floorY;

            owner.x = target.x;
            owner.y = target.y;
            owner.floorY = target.floorY;
            target.x = ownerX;
            target.y = ownerY;
            target.floorY = ownerFloorY;

            PhysicsHelper.getInstance().syncBodyToUnit(owner);
            PhysicsHelper.getInstance().syncBodyToUnit(target);
            playSound(Sounds.ZAP, 1f);
        } else {
            playSound(Sounds.MISS, 0.4f);
        }

        markUsed();
        EffectsHelper.getInstance().blackSpark(this);
    }
}