package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ShamanBolt;

public class Shaman extends Mob {
    {
        hp = mhp = 18;
        experience = 6;
        attackSkill = 11;
        defenseSkill = 8;
        damageReduction = 4;
        gf = new GameFilm("images/units/shaman/shaman.png", 256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{4, 5, 6, 7};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{8, 9, 9, 9};
        ai = new AgressiveAI(this) {
            private float castAt = 0.5f;

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
                float verticalDistance = Math.abs(target.y - getOwner().y);
                if (horizontalDistance > ConstantsHelper.UNIT_DIMENSIONS * 2.5f && verticalDistance < ConstantsHelper.TILE * 1.25f) {
                    getOwner().movingLeft = false;
                    getOwner().movingRight = false;
                    getOwner().facingRight = getOwner().x < target.x;

                    if (castAt <= 0f) {
                        ((Shaman) getOwner()).castBolt();
                        getOwner().fakeAttack();
                        castAt = 1.8f;
                    }
                    return;
                }

                super.attacked(delta);
            }
        };

        speedX = 320;
        attackSpeed = 4.5f;
        weapon = new MeleeAttack().setDamageRange(2f, 6f);
        weapon.setOwner(this);
    }

    private void castBolt() {
        ShamanBolt bolt = new ShamanBolt();
        bolt.setRoom(room);
        bolt.isFriendly = isFriendly;
        bolt.facingRight = facingRight;
        bolt.x = x;
        bolt.y = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        bolt.setOwner(this);
        bolt.setSpeedX(facingRight ? 625f : -625f);
        UnitHelper.getInstance().addUnit(bolt);
    }

    @Override
    public String getLibraryDescription() {
        return "A prison caster that keeps its distance and hurls crackling bolts down the corridor before committing to melee.";
    }
}