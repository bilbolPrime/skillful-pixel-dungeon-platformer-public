package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class FriendlyAI extends AgressiveAI{

    private static final float DROP_GROUND_TOLERANCE = 2f;

    {
        los = 500f;
    }

    public FriendlyAI(Unit unit){
        super(unit);
    }

    @Override
    public void wander(float delta){
        if(other == null){
            Unit hero = UnitHelper.getInstance().getHero();
            if (hero != null && Math.abs(hero.x - owner.x) > 2 * ConstantsHelper.UNIT_DIMENSIONS) {
                designationX = hero.x + ConstantsHelper.UNIT_DIMENSIONS - RandomHelper.getInstance().randomFloat(2 * ConstantsHelper.UNIT_DIMENSIONS);
            }
            if(Math.abs(designationX - owner.x) < ConstantsHelper.UNIT_DIMENSIONS){
                owner.movingRight = false;
                owner.movingLeft = false;
            }
            else if(designationX > owner.x){
                owner.movingRight = true;
                owner.facingRight = true;
                owner.movingLeft = false;
            } else{
                owner.movingLeft = true;
                owner.facingRight = false;
                owner.movingRight = false;
            }

            if (hero != null) {
                if (owner.movingLeft && !UnitHelper.getInstance().freeSpace(owner, (int) owner.x - 1, (int) owner.y, owner.getRoom())) {
                    owner.jump();
                }

                if (owner.movingRight && !UnitHelper.getInstance().freeSpace(owner, (int) owner.x + 1, (int) owner.y, owner.getRoom())) {
                    owner.jump();
                }

                if (owner.y + 15 < hero.y) {
                    if (owner.isCanFly()) {
                        owner.fly(true, false);
                    }
                    else {
                        owner.jump();
                    }
                }

                if (owner.y > hero.y + 15f) {
                    if (owner.isCanFly()) {
                        owner.fly(false, false);
                    }
                    else {
                        tryDropTowardHero();
                    }
                }
            }
        }
        super.wander(delta);
    }

    private void tryDropTowardHero() {
        if (!isStandingOnFloor()) {
            return;
        }

        float candidateY = MapHelper.getInstance().calculateFloorY(owner.x, owner.y - 1f);
        if (candidateY < owner.y) {
            owner.y--;
            owner.speedY = -10f;
            owner.floorY = candidateY;
        }
    }

    private boolean isStandingOnFloor() {
        if (PhysicsHelper.getInstance().hasBody(owner) && !owner.showOnly()) {
            return PhysicsHelper.getInstance().isGrounded(owner);
        }

        return Math.abs(owner.y - owner.floorY) <= DROP_GROUND_TOLERANCE;
    }
}

