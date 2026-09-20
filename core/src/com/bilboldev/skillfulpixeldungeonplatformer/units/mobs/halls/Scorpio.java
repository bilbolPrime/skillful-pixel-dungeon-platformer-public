package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ScorpioShot;

public class Scorpio extends Mob {
    {
        hp = mhp = 95;
        experience = 15;
        attackSkill = 36;
        defenseSkill = 24;
        damageReduction = 16;
        gf = new GameFilm("images/units/scorpio/scorpio.png", 256, 64, 1f);
        gf.clipSizeX = 18;
        gf.clipSizeY = 17;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1 ,2};
        attackFrames = new int[]{3, 4, 5, 6};
        dieFrames = new int[]{7, 8, 9, 10};
        ai = new AgressiveAI(this) {
            private float shotAt = 0.55f;
            private boolean retreating;

            @Override
            public void clearTarget() {
                retreating = false;
                super.clearTarget();
            }

            @Override
            public void act(float delta) {
                shotAt -= delta;
                super.act(delta);
            }

            @Override
            public void attacked(float delta) {
                Unit target = getOther();
                if (target == null || target.getHP() < 1 || target.getRoom() == null || getOwner().getRoom() == null || !target.getRoom().equals(getOwner().getRoom())) {
                    retreating = false;
                    super.attacked(delta);
                    return;
                }

                float horizontalDistance = Math.abs(target.x - getOwner().x);
                getOwner().facingRight = getOwner().x < target.x;
                if (!hasHorizontalProjectileLane(target, Float.MAX_VALUE)) {

                    retreating = false;
                    super.attacked(delta);
                    return;
                }
                if (horizontalDistance <= ConstantsHelper.UNIT_DIMENSIONS * 1.5f) retreating = true;
                else if (horizontalDistance >= ConstantsHelper.UNIT_DIMENSIONS * 2.5f) retreating = false;
                float retreatStep = getOwner().facingRight ? -ConstantsHelper.UNIT_DIMENSIONS : ConstantsHelper.UNIT_DIMENSIONS;
                boolean canRetreat = retreating && canStepOnCurrentFloor(retreatStep);
                if (canRetreat) {
                    getOwner().movingLeft = retreatStep < 0f;
                    getOwner().movingRight = retreatStep > 0f;
                    return;
                }


                if (hasHorizontalProjectileLane(target, 780f * 70f / 75f)) {
                    getOwner().movingLeft = false;
                    getOwner().movingRight = false;
                    if (shotAt <= 0f && getOwner().canAttack()) {
                        ((Scorpio) getOwner()).fireShot();
                        getOwner().fakeAttack();
                        shotAt = 1.25f;
                    }
                    return;
                }

                super.attacked(delta);
            }
        };

        speedX = 350;
        attackSpeed = 4f;
        weapon = new MeleeAttack().setDamageRange(20f, 32f);
        weapon.setOwner(this);
    }

    private void fireShot() {
        ScorpioShot shot = new ScorpioShot();
        shot.setRoom(room);
        shot.isFriendly = isFriendly;
        shot.facingRight = facingRight;
        shot.x = x;
        shot.y = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        shot.setOwner(this);
        shot.setLifeSpan(70f);
        shot.setSpeedX(facingRight ? 780f : -780f);
        UnitHelper.getInstance().addUnit(shot);
    }

    @Override
    public String getLibraryDescription() {
        return "A ranged-only Halls sniper that keeps backing away and punishes open ground with crippling shots.";
    }
}
