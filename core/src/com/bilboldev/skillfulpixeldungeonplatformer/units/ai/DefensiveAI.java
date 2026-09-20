package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class DefensiveAI extends AI{

    private static final float DROP_EDGE_SCAN_STEP = 8f;
    private static final float DROP_GROUND_TOLERANCE = 2f;

    private static final float DROP_FLOOR_TOLERANCE = 8f;

    private int committedDropDirection;
    private float committedDropEdgeX = Float.NaN;
    private Unit committedDropTarget;
    private float committedDropStartY = Float.NaN;

    public DefensiveAI(Unit unit){
        super(unit);
    }

    @Override
    public void clearTarget() {
        clearDropEdgeCommit();
        super.clearTarget();
    }


    @Override
    public void wander(float delta){
        super.wander(delta);
    }

    @Override
    public void attacked(float delta){
        boolean ignoreInvisibility = owner instanceof Mob && ((Mob) owner).shouldForceBossRoomAggro();
        if (ignoreInvisibility) {
            Unit forcedTarget = ((Mob) owner).findBossRoomTarget();
            if (forcedTarget != null) {
                other = forcedTarget;
            }
        }

        if(other == null || other.getHP() < 1 || (!ignoreInvisibility && other.isInvisible()) || other.getRoom() == null || owner.getRoom() == null || !other.getRoom().equals(owner.getRoom()) || (!ignoreInvisibility && !UnitHelper.getInstance().canSeeTarget(owner, other))){
            clearDropEdgeCommit();
            other = null;
            state = States.IDLE;
            if (owner instanceof Mob) {
                ((Mob) owner).onTargetLost();
            }
            owner.movingRight = false;
            owner.movingLeft = false;
            if(owner.isCanFly()){
                owner.fly(false, true);
            }
            return;
        }

        owner.facingRight = owner.x < other.x;
        boolean continueCommittedDrop = shouldContinueCommittedDrop(other);
        boolean startCommittedDrop = shouldStartCommittedDrop(other);
        if (continueCommittedDrop || startCommittedDrop) {
            if (moveTowardDropEdge(other)) {
                owner.facingRight = committedDropDirection > 0;
                return;
            }
        } else if (committedDropDirection != 0) {
            clearDropEdgeCommit();
        }

        if(Math.abs(other.x - owner.x) > ConstantsHelper.UNIT_DIMENSIONS){
            if(other.x > owner.x){
                owner.movingRight = true;
                owner.movingLeft = false;
            }

            if(other.x < owner.x){
                owner.movingLeft = true;
                owner.movingRight = false;
            }

            if(!owner.isCanFly()){
                if(owner.movingLeft && !UnitHelper.getInstance().freeSpace(owner, (int)owner.x - 1, (int)owner.y, owner.getRoom())){
                    owner.jump();
                }

                if(owner.movingRight && !UnitHelper.getInstance().freeSpace(owner,( int)owner.x + 1, (int)owner.y, owner.getRoom())){
                    owner.jump();
                }
            }

            if(owner.isCanFly() && Math.abs(other.y - owner.y) > ConstantsHelper.UNIT_DIMENSIONS ){
                owner.fly(other.y > owner.y, false);
            }
        }
        else {
            owner.movingLeft = false;
            owner.movingRight = false;


            if(owner.y + 15 < other.y){
                if(!owner.isCanFly()){
                    owner.jump();
                }
                else {
                    owner.fly(true, false);
                }
            }
            else if(Math.abs(owner.y - other.y) < 15){
                attack();
            }
            else if(!owner.isCanFly() && owner.y > other.y + 15 && isStandingOnFloor()){
                float candidateY = MapHelper.getInstance().calculateFloorY(owner.x, owner.y - 1);
                if(candidateY < owner.y){

                        owner.y--;
                        owner.speedY = -10;
                        owner.floorY = candidateY;
                    }
            } else if(owner.isCanFly() && owner.y > other.y + 15){
                owner.fly(false, false);
            }
        }

        if(Math.abs(other.x - owner.x) > 5 * ConstantsHelper.UNIT_DIMENSIONS){
            Unit candidateTarget = findTarget();
            if(isBetterTarget(candidateTarget)) {
                other = candidateTarget;
            }
        }
    }

    private boolean moveTowardDropEdge(Unit target) {
        if (committedDropDirection == 0 && !commitDropEdge(target)) {
            return false;
        }

        float candidateY = MapHelper.getInstance().calculateFloorY(owner.x, owner.y - 1f);
        if (candidateY < owner.y - DROP_FLOOR_TOLERANCE) {
            if (!PhysicsHelper.getInstance().hasBody(owner) || owner.showOnly()) {
                if (isStandingOnFloor()) {
                    dropFromEdge(candidateY);
                }
            }

            moveInCommittedDropDirection();
            return true;
        }

        moveInCommittedDropDirection();
        return true;
    }

    private boolean shouldStartCommittedDrop(Unit target) {
        return !owner.isCanFly()
                && owner.y > target.y + 15f
                && isStandingOnFloor();
    }

    private boolean shouldContinueCommittedDrop(Unit target) {
        if (committedDropDirection == 0) {
            return false;
        }

        if (target != committedDropTarget || owner.isCanFly()) {
            clearDropEdgeCommit();
            return false;
        }


        if (target.y >= committedDropStartY - 15f) {
            clearDropEdgeCommit();
            return false;
        }

        if (hasDroppedPastCommittedStartY()) {
            clearDropEdgeCommit();
            return false;
        }

        if (Float.isNaN(committedDropEdgeX)) {
            clearDropEdgeCommit();
            return false;
        }

        return true;
    }

    private void moveInCommittedDropDirection() {
        owner.movingLeft = committedDropDirection < 0;
        owner.movingRight = committedDropDirection > 0;
    }

    private boolean commitDropEdge(Unit target) {
        float targetEdgeX = findClosestDropEdgeX(target);
        if (Float.isNaN(targetEdgeX)) {
            return false;
        }

        committedDropDirection = targetEdgeX >= owner.x ? 1 : -1;
        if (targetEdgeX == owner.x) {
            committedDropDirection = target.x >= owner.x ? 1 : -1;
        }
        committedDropEdgeX = targetEdgeX;
        committedDropTarget = target;
        committedDropStartY = owner.y;
        return true;
    }

    private void clearDropEdgeCommit() {
        committedDropDirection = 0;
        committedDropEdgeX = Float.NaN;
        committedDropTarget = null;
        committedDropStartY = Float.NaN;
    }

    private boolean hasDroppedPastCommittedStartY() {
        return !Float.isNaN(committedDropStartY)

                && owner.y <= committedDropStartY - ConstantsHelper.TILE + DROP_GROUND_TOLERANCE;
    }

    private boolean isStandingOnFloor() {
        if (PhysicsHelper.getInstance().hasBody(owner) && !owner.showOnly()) {
            return PhysicsHelper.getInstance().isGrounded(owner);
        }

        return Math.abs(owner.y - owner.floorY) <= DROP_GROUND_TOLERANCE;
    }

    private void dropFromEdge(float candidateY) {
        owner.y--;
        owner.speedY = -10;
        owner.floorY = candidateY;
    }

    private float findClosestDropEdgeX(Unit target) {
        float leftEdgeX = findDropEdgeX(-1f);
        float rightEdgeX = findDropEdgeX(1f);

        if (Float.isNaN(leftEdgeX)) {
            return rightEdgeX;
        }

        if (Float.isNaN(rightEdgeX)) {
            return leftEdgeX;
        }

        float targetCenterX = target.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return Math.abs(targetCenterX - leftEdgeX) <= Math.abs(targetCenterX - rightEdgeX) ? leftEdgeX : rightEdgeX;
    }

    private float findDropEdgeX(float direction) {
        float maxX = MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - 1f - ConstantsHelper.UNIT_DIMENSIONS;
        float probeX = owner.x;

        while (probeX > 1f && probeX < maxX) {
            probeX += direction * DROP_EDGE_SCAN_STEP;
            float candidateY = MapHelper.getInstance().calculateFloorY(probeX, owner.y - 1f);
            if (probeX > 1f && probeX < maxX && candidateY < owner.y - DROP_FLOOR_TOLERANCE) {
                return probeX;
            }
        }

        return Float.NaN;
    }

    protected void attack(){
        owner.attack();
    }


    protected boolean hasHorizontalProjectileLane(Unit target, float travelRange) {
        float size = ConstantsHelper.UNIT_DIMENSIONS / 4f;
        float targetSize = target.getCollisionWidth();
        float targetLeft = target.x + (ConstantsHelper.UNIT_DIMENSIONS - targetSize) / 2f;
        float gap = owner.facingRight ? targetLeft - owner.x - size : owner.x - targetLeft - targetSize;
        float shotBottom = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return gap <= travelRange && shotBottom < target.y + targetSize && shotBottom + size > target.y;
    }


    protected void alignFlyingShot(Unit target) {
        boolean leavePlatform = owner.y > target.y
                && MapHelper.getInstance().calculateFloorY(owner.x, owner.y) > target.y + DROP_FLOOR_TOLERANCE;
        owner.movingLeft = leavePlatform && target.x < owner.x;
        owner.movingRight = leavePlatform && target.x > owner.x;
        owner.fly(target.y > owner.y, false);
    }


    protected boolean canStepOnCurrentFloor(float offset) {
        if (!isStandingOnFloor()) return false;
        float nextX = owner.x + offset;
        float limit = MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS;
        return nextX > 1f && nextX < limit - 1f
                && MapHelper.getInstance().calculateFloorY(nextX, owner.y - 1f) >= owner.y - DROP_FLOOR_TOLERANCE
                && UnitHelper.getInstance().freeSpace(owner, (int) nextX, (int) owner.y, owner.getRoom());
    }

    protected Unit findTarget(){
        return UnitHelper.getInstance().findTarget(owner, los);
    }
}

