package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.WarlockBolt;

public class DwarfWarlock extends Mob {
    {
        hp = mhp = 70;
        experience = 11;
        attackSkill = 25;
        defenseSkill = 18;
        damageReduction = 8;
        gf = new GameFilm("images/units/warlock/warlock.png", 256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1, 2, 3};
        attackFrames = new int[]{4, 5, 6, 6};
        dieFrames = new int[]{7, 8, 9, 9, 9};
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
                getOwner().facingRight = getOwner().x < target.x;
                if (horizontalDistance > ConstantsHelper.UNIT_DIMENSIONS * 2f
                        && hasHorizontalProjectileLane(target, 700f * 65f / 75f)) {
                    getOwner().movingLeft = false;
                    getOwner().movingRight = false;
                    getOwner().facingRight = getOwner().x < target.x;

                    if (castAt <= 0f && getOwner().canAttack()) {
                        ((DwarfWarlock) getOwner()).castBolt();
                        getOwner().fakeAttack();
                        castAt = 1.6f;
                    }
                    return;
                }

                super.attacked(delta);
            }
        };

        speedX = 300;
        attackSpeed = 4.25f;
        weapon = new MeleeAttack().setDamageRange(12f, 20f);
        weapon.setOwner(this);
    }

    private void castBolt() {
        WarlockBolt bolt = new WarlockBolt();
        bolt.setRoom(room);
        bolt.isFriendly = isFriendly;
        bolt.facingRight = facingRight;
        bolt.x = x;
        bolt.y = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        bolt.setOwner(this);
        bolt.setDamageRange(12f, 18f);
        bolt.setLifeSpan(65f);
        bolt.setSpeedX(facingRight ? 700f : -700f);
        UnitHelper.getInstance().addUnit(bolt);
    }

    @Override
    public String getLibraryDescription() {
        return "A city caster that prefers a clean lane, then hammers it with shadow bolts before grudgingly switching to melee.";
    }
}
