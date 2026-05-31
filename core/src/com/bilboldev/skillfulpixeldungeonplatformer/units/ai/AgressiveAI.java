package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class AgressiveAI extends DefensiveAI{

    {
        los = 800f;
    }

    public AgressiveAI(Unit unit){
        super(unit);
    }

    @Override
    public void wander(float delta){
        Unit candidateTarget = findTarget();

        if (owner instanceof Mob) {
            Mob mob = (Mob) owner;
            Unit bossRoomTarget = mob.findBossRoomTarget();
            if (bossRoomTarget != null) {
                mob.beginHunting(bossRoomTarget);
                return;
            }

            if (mob.isSleeping()) {
                if (mob.consumeSleepNoticeSuppression()) {
                    mob.stopForSleep();
                    return;
                }

                Hero hero = UnitHelper.getInstance().getHero();
                if (hero != null && mob.tryWakeFromSleepingHero(hero)) {
                    mob.beginHunting(hero);
                } else {
                    mob.stopForSleep();
                }
                return;
            }

            boolean alertBoost = mob.consumeAlerted();
            if (candidateTarget != null && mob.canNoticeTarget(candidateTarget, alertBoost, false)) {
                mob.beginHunting(candidateTarget);
                return;
            }
        } else if (candidateTarget != null) {
            setOther(candidateTarget);
            return;
        }

        super.wander(delta);
    }
}

