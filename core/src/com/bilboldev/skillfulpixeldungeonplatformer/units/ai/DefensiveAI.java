package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class DefensiveAI extends AI{

    private static final float DROP_EDGE_SCAN_STEP = 8f;
    private static final float DROP_GROUND_TOLERANCE = 2f;

    private int committedDropDirection;
    private float committedDropEdgeX = Float.NaN;
    private float committedDropHeroY = Float.NaN;
    private float committedDropStartY = Float.NaN;

    public DefensiveAI(Unit unit){
        super(unit);
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

        boolean continueCommittedDrop = shouldContinueCommittedHeroDrop(other);
        boolean startCommittedDrop = shouldStartCommittedHeroDrop(other);
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

            // Jump
            if(owner.y + 15 < other.y){
                if(!owner.isCanFly()){
                    owner.jump();
                }
                else {
                    owner.fly(true, false);
                }
            } // Smack them in the face
            else if(Math.abs(owner.y - other.y) < 15){
                attack();
            } // Drop if you can
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

        owner.facingRight = owner.x < other.x;

        if(Math.abs(other.x - owner.x) > 5 * ConstantsHelper.UNIT_DIMENSIONS){
            Unit candidateTarget = findTarget();
            if(candidateTarget != null && Math.abs(other.x - owner.x) > Math.abs(candidateTarget.x - owner.x)) {
                other = candidateTarget;
            }
        }
    }

    private boolean moveTowardDropEdge(Unit target) {
        if (committedDropDirection == 0 && !commitDropEdge(target)) {
            return false;
        }

        float candidateY = MapHelper.getInstance().calculateFloorY(owner.x, owner.y - 1f);
        if (candidateY < owner.y) {
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

    private boolean shouldStartCommittedHeroDrop(Unit target) {
        return target instanceof Hero
                && !owner.isCanFly()
                && owner.y > target.y + 15f
                && isStandingOnFloor();
    }

    private boolean shouldContinueCommittedHeroDrop(Unit target) {
        if (committedDropDirection == 0) {
            return false;
        }

        if (!(target instanceof Hero)) {
            clearDropEdgeCommit();
            return false;
        }

        Hero hero = (Hero) target;
        if (Math.abs(hero.y - committedDropHeroY) > DROP_GROUND_TOLERANCE) {
            clearDropEdgeCommit();
            return false;
        }

        if (hasDroppedPastCommittedStartY()) {
            clearDropEdgeCommit();
            return false;
        }

        if (Float.isNaN(committedDropEdgeX) || !isEdgeOnScreen(committedDropEdgeX)) {
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
        if (!(target instanceof Hero)) {
            return false;
        }

        float targetEdgeX = findClosestDropEdgeX(target);
        if (Float.isNaN(targetEdgeX)) {
            return false;
        }

        committedDropDirection = targetEdgeX >= owner.x ? 1 : -1;
        if (targetEdgeX == owner.x) {
            committedDropDirection = target.x >= owner.x ? 1 : -1;
        }
        committedDropEdgeX = targetEdgeX;
        committedDropHeroY = target.y;
        committedDropStartY = owner.y;
        return true;
    }

    private void clearDropEdgeCommit() {
        committedDropDirection = 0;
        committedDropEdgeX = Float.NaN;
        committedDropHeroY = Float.NaN;
        committedDropStartY = Float.NaN;
    }

    private boolean hasDroppedPastCommittedStartY() {
        return !Float.isNaN(committedDropStartY)
                && owner.y <= committedDropStartY - ConstantsHelper.TILE;
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
            if (candidateY < owner.y && isEdgeOnScreen(probeX)) {
                return probeX;
            }
        }

        return Float.NaN;
    }

    private boolean isEdgeOnScreen(float edgeX) {
        float viewportWidth = GameHelper.GetSingleton().getCamera().viewportWidth > 0f
            ? GameHelper.GetSingleton().getCamera().viewportWidth * Math.max(1f, GameHelper.GetSingleton().getCamera().zoom)
                : ConstantsHelper.SCREEN_WIDTH;
        float halfViewportWidth = viewportWidth / 2f;
        float cameraCenterX = GameHelper.GetSingleton().getCamera().position.x;
        float edgeRightX = edgeX + ConstantsHelper.UNIT_DIMENSIONS;

        return edgeX >= cameraCenterX - halfViewportWidth
            && edgeRightX <= cameraCenterX + halfViewportWidth;
    }

    protected void attack(){
        owner.attack();
    }

    protected Unit findTarget(){
        return UnitHelper.getInstance().findTarget(owner, los);
    }
}

