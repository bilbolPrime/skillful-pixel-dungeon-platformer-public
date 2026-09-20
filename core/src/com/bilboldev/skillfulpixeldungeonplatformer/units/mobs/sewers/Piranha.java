package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Meat;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;

public class Piranha extends Mob {
    private static final float UNDERWATER_SCALE = 0.5f;

    {
        int depth = Math.max(1, MapHelper.getInstance().getDepth());

        hp = mhp = 10 + depth * 5;
        experience = 0;
        attackSkill = 20 + depth * 2;
        defenseSkill = 10 + depth * 2;
        damageReduction = depth;

        gf = new GameFilm("images/units/piranha/piranha.png", 256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1, 2, 1};
        runFrames = new int[]{0, 1, 2, 1};
        attackFrames = new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11};
        dieFrames = new int[]{12, 13, 14};

        ai = new WaterBoundAgressiveAI(this);

        speedX = 650f;
        attackSpeed = 8f;

        weapon = new MeleeAttack().setDamageRange(depth, 4f + depth * 2f);
        weapon.setOwner(this);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        boolean underwater = MapHelper.getInstance().isStandingOnWater(this);
        float originalX = x;
        float originalY = y;
        float originalScaleX = gf == null ? 1f : gf.getScaleX();
        float originalScaleY = gf == null ? 1f : gf.getScaleY();

        if (underwater && gf != null) {
            x = originalX + ConstantsHelper.UNIT_DIMENSIONS * (1f - UNDERWATER_SCALE) / 2f;
            y = floorY - ConstantsHelper.UNIT_DIMENSIONS * UNDERWATER_SCALE;
            gf.setScale(UNDERWATER_SCALE, UNDERWATER_SCALE);
        }

        try {
            super.draw(batch, alpha);
        } finally {
            x = originalX;
            y = originalY;
            if (gf != null) {
                gf.setScale(originalScaleX, originalScaleY);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!isDead() && hitDryGround()) {
            dieOutOfWater();
        }
    }

    private boolean hitDryGround() {
        return !MapHelper.getInstance().isStandingOnWater(this)
                && Math.abs(y - floorY) < 0.01f
                && Math.abs(speedY) < 0.01f;
    }

    private void dieOutOfWater() {
        movingLeft = false;
        movingRight = false;
        setHP(0);
        changeState(UnitState.DEAD, true);
        notifyDeathCommitted();
        die();
    }

    @Override
    protected void setLoot() {
        loot = new Meat();
    }

    @Override
    public String getLibraryDescription() {
        return "A giant carnivorous fish bred to guard flooded treasure rooms. It moves quickly through water, hits far harder than its size suggests, and dies if dragged onto dry ground.";
    }

    private static final class WaterBoundAgressiveAI extends AgressiveAI {

        private WaterBoundAgressiveAI(Unit unit) {
            super(unit);
        }

        @Override
        public void wander(float delta) {
            super.wander(delta);
            clampMovementToWater(delta);
        }

        @Override
        public void attacked(float delta) {
            Unit owner = getOwner();
            Unit target = getOther();

            if (owner == null) {
                return;
            }

            if (target == null
                    || target.getHP() < 1
                    || target.isInvisible()
                    || target.getRoom() == null
                    || owner.getRoom() == null
                    || !target.getRoom().equals(owner.getRoom())) {
                clearTarget();
                if (owner instanceof Mob) {
                    ((Mob) owner).onTargetLost();
                }
                stopMoving();
                return;
            }

            owner.facingRight = owner.x < target.x;
            if (Math.abs(target.x - owner.x) > ConstantsHelper.UNIT_DIMENSIONS) {
                owner.movingRight = target.x > owner.x;
                owner.movingLeft = target.x < owner.x;
                clampMovementToWater(delta);
            } else {
                stopMoving();
                if (Math.abs(owner.y - target.y) < 15f) {
                    attack();
                }
            }

            if (Math.abs(target.x - owner.x) > 5 * ConstantsHelper.UNIT_DIMENSIONS) {
                Unit candidateTarget = findTarget();
                if (candidateTarget != null && Math.abs(target.x - owner.x) > Math.abs(candidateTarget.x - owner.x)) {
                    setOther(candidateTarget);
                }
            }
        }

        private void clampMovementToWater(float delta) {
            Unit owner = getOwner();
            if (owner == null || owner.getRoom() == null || owner.movingLeft == owner.movingRight) {
                return;
            }

            Room room = MapHelper.getInstance().getRoom(owner.getRoom());
            if (room == null) {
                return;
            }

            float direction = owner.movingRight ? 1f : -1f;
            float candidateX = owner.x + direction * delta * owner.getSpeedX();
            if (!room.hasWaterAt(candidateX + ConstantsHelper.UNIT_DIMENSIONS / 2f, owner.floorY)) {
                stopMoving();
            }
        }

        private void stopMoving() {
            Unit owner = getOwner();
            if (owner == null) {
                return;
            }

            owner.movingLeft = false;
            owner.movingRight = false;
        }
    }
}
