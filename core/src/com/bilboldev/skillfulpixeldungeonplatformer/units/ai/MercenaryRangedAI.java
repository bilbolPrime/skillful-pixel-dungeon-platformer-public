package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;

public class MercenaryRangedAI extends FriendlyAI {
    private final MercenaryAlly mercenary;

    {
        los = 1100f;
    }

    public MercenaryRangedAI(MercenaryAlly mercenary) {
        super(mercenary);
        this.mercenary = mercenary;
    }

    @Override
    public void attacked(float delta) {
        if (!canTarget(other)) {
            clearTarget();
            if (owner instanceof Mob) {
                ((Mob) owner).onTargetLost();
            }
            return;
        }

        float horizontalDistance = Math.abs(other.x - owner.x);
        float verticalDistance = Math.abs(other.y - owner.y);
        float minRange = mercenary.getPreferredMinDistance();
        float maxRange = mercenary.getPreferredMaxDistance();
        boolean attackWindow = verticalDistance <= ConstantsHelper.UNIT_DIMENSIONS * 1.5f;

        owner.facingRight = owner.x < other.x;

        if (horizontalDistance < minRange) {
            moveAwayFromTarget();
            if (attackWindow && horizontalDistance <= ConstantsHelper.UNIT_DIMENSIONS * 1.5f) {
                mercenary.performMercenaryAttack();
            }
        }
        else if (horizontalDistance > maxRange) {
            moveTowardTarget();
        }
        else {
            owner.movingLeft = false;
            owner.movingRight = false;

            if (owner.y + 15 < other.y) {
                owner.jump();
            }
            else if (attackWindow) {
                mercenary.performMercenaryAttack();
            }
            else if (owner.y > other.y + 15 && owner.y == owner.floorY) {
                float candidateY = MapHelper.getInstance().calculateFloorY(owner.x, owner.y - 1);
                if (candidateY < owner.y) {
                    owner.y--;
                    owner.speedY = -10;
                    owner.floorY = candidateY;
                }
            }
        }

        if (other != null && Math.abs(other.x - owner.x) > 5 * ConstantsHelper.UNIT_DIMENSIONS) {
            Unit candidateTarget = findTarget();
            if (isBetterTarget(candidateTarget)) {
                other = candidateTarget;
            }
        }
    }

    private void moveTowardTarget() {
        if (other.x > owner.x) {
            owner.movingRight = true;
            owner.movingLeft = false;
        }
        else {
            owner.movingLeft = true;
            owner.movingRight = false;
        }

        if (owner.movingLeft && !UnitHelper.getInstance().freeSpace(owner, (int) owner.x - 1, (int) owner.y, owner.getRoom())) {
            owner.jump();
        }

        if (owner.movingRight && !UnitHelper.getInstance().freeSpace(owner, (int) owner.x + 1, (int) owner.y, owner.getRoom())) {
            owner.jump();
        }
    }

    private void moveAwayFromTarget() {
        if (other.x > owner.x) {
            owner.movingLeft = true;
            owner.movingRight = false;
        }
        else {
            owner.movingRight = true;
            owner.movingLeft = false;
        }

        if (owner.movingLeft && !UnitHelper.getInstance().freeSpace(owner, (int) owner.x - 1, (int) owner.y, owner.getRoom())) {
            owner.jump();
            owner.movingLeft = false;
        }

        if (owner.movingRight && !UnitHelper.getInstance().freeSpace(owner, (int) owner.x + 1, (int) owner.y, owner.getRoom())) {
            owner.jump();
            owner.movingRight = false;
        }
    }
}
