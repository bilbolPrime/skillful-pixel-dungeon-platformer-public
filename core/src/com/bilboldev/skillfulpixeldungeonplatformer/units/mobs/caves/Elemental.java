package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBolt;

public class Elemental extends Mob {
    {
        canFly = true;
        hp = mhp = 65;
        experience = 10;
        attackSkill = 25;
        defenseSkill = 20;
        damageReduction = 5;
        gf = new GameFilm("images/units/elemental/elemental.png", 256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1, 2, 3};
        runFrames = new int[]{0, 1, 2, 3};
        attackFrames = new int[]{4, 5, 6};
        dieFrames = new int[]{7, 8, 9, 10, 11, 12, 13};
        ai = new AgressiveAI(this) {
            private float castAt = 0.75f;

            @Override
            public void act(float delta) {
                castAt -= delta;
                super.act(delta);
            }

            @Override
            public void attacked(float delta) {
                Unit target = getOther();
                if (target == null || target.getHP() < 1 || target.getRoom() == null || getOwner().getRoom() == null || !target.getRoom().equals(getOwner().getRoom())) {
                    super.attacked(delta);
                    return;
                }

                float horizontalDistance = Math.abs(target.x - getOwner().x);
                getOwner().facingRight = getOwner().x < target.x;
                if (horizontalDistance > ConstantsHelper.UNIT_DIMENSIONS * 2f
                        && hasHorizontalProjectileLane(target, 650f * 60f / 75f)) {
                    getOwner().movingLeft = false;
                    getOwner().movingRight = false;
                    getOwner().fly(false, true);
                    if (castAt <= 0f && getOwner().canAttack()) {
                        ((Elemental) getOwner()).castFire();
                        getOwner().fakeAttack();
                        castAt = 1.6f;
                    }
                    return;
                }
                if (horizontalDistance > ConstantsHelper.UNIT_DIMENSIONS * 2f && horizontalDistance <= 520f
                        && !hasHorizontalProjectileLane(target, Float.MAX_VALUE)) {
                    alignFlyingShot(target);
                    return;
                }

                super.attacked(delta);
            }
        };

        jumpSpeed = 180;
        speedX = 340;
        attackSpeed = 4.5f;
        weapon = new MeleeAttack().setDamageRange(16f, 20f);
        weapon.setOwner(this);
    }

    private void castFire() {
        FireBolt bolt = new FireBolt();
        bolt.setRoom(room);
        bolt.isFriendly = isFriendly;
        bolt.facingRight = facingRight;
        bolt.x = x;
        bolt.y = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        bolt.setOwner(this);
        bolt.setDamageRange(16f, 20f);
        bolt.setLifeSpan(60f);
        bolt.setSpeedX(facingRight ? 650f : -650f);
        UnitHelper.getInstance().addUnit(bolt);
    }

    @Override
    public String getLibraryDescription() {
        return "A flying fire spirit that peppers the cave with bolts before dropping into claw range.";
    }
}
