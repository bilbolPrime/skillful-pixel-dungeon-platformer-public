package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.LightningSpread;

import java.util.ArrayList;

public class WandOfLightning extends Wand {
    {
        manaCost = 5;
        name = "Wand of Lightning";
        description = "A wand that calls a crackling arc which leaps between nearby enemies.";
        gs = new GameSprite("images/wands/WAND_CHERRY.png", 45, 45);
    }

    @Override
    protected Sounds getCastSound() {
        return Sounds.LIGHTNING;
    }

    @Override
    public void addProjectile(float variance) {
        Unit currentTarget = getFirstHostileInBeam(6f, ConstantsHelper.UNIT_DIMENSIONS);
        if (currentTarget == null) {
            return;
        }

        ArrayList<Unit> struckTargets = new ArrayList<Unit>();
        int maxTargets = getWandPowerMultiplier() >= 1.5f ? 4 : 3;
        for (int jump = 0; jump < maxTargets && currentTarget != null; jump++) {
            struckTargets.add(currentTarget);
            float damage = scalePower(Math.max(2f, 5f - jump + RandomHelper.getInstance().randomFloat(2f)));
            currentTarget.takeDamage(owner, this, damage);
            EffectsHelper.getInstance().add(new LightningSpread().init(
                    currentTarget.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                    currentTarget.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                    0f,
                    0f,
                    0f,
                    0f));

            Unit nextTarget = null;
            float closestDistanceSq = Float.MAX_VALUE;
            for (Unit candidate : getHostilesNear(currentTarget.x, currentTarget.y, 2.5f, 2.5f)) {
                if (struckTargets.contains(candidate)) {
                    continue;
                }

                float dx = candidate.x - currentTarget.x;
                float dy = candidate.y - currentTarget.y;
                float distanceSq = dx * dx + dy * dy;
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    nextTarget = candidate;
                }
            }
            currentTarget = nextTarget;
        }
    }
}