package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.FireBall;

public class BurningFist extends YogFist {
    {
        boss = true;
        hp = mhp = 200;
        experience = 0;
        attackSkill = 36;
        defenseSkill = 25;
        damageReduction = 15;
        dropChance = 0;
        gf = new GameFilm("images/units/burning-fist/burning-fist.png", 256, 32, 1f);
        gf.clipSizeX = 24;
        gf.clipSizeY = 17;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1};
        attackFrames = new int[]{ 5, 6, 6, 6};
        dieFrames = new int[]{ 2, 3, 4};
        ai = new AgressiveAI(this) {
            private float flameAt = 0.9f;

            @Override
            public void act(float delta) {
                flameAt -= delta;
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
                if (horizontalDistance > ConstantsHelper.UNIT_DIMENSIONS * 1.75f && flameAt <= 0f) {
                    ((BurningFist) getOwner()).launchFire();
                    getOwner().fakeAttack();
                    getOwner().movingLeft = false;
                    getOwner().movingRight = false;
                    getOwner().facingRight = getOwner().x < target.x;
                    flameAt = 1.75f;
                    return;
                }

                super.attacked(delta);
            }
        };

        speedX = 260;
        attackSpeed = 3.4f;
        weapon = new MeleeAttack().setDamageRange(20f, 32f);
        weapon.setOwner(this);
    }

    private void launchFire() {
        FireBall fireBall = new FireBall();
        fireBall.setRoom(room);
        fireBall.isFriendly = isFriendly;
        fireBall.facingRight = facingRight;
        fireBall.x = x;
        fireBall.y = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        fireBall.setOwner(this);
        fireBall.setDamageRange(20f, 32f);
        fireBall.setSpeedX(facingRight ? 700f : -700f);
        UnitHelper.getInstance().addUnit(fireBall);
    }

    @Override
    protected void emitAmbientParticles() {
        emitAmbientBurst("images/misc/yellow-dot.png", 11f, 4, 20f, 34f, 42f, 0.05f);
        emitAmbientBurst("images/misc/red.png", 12f, 4, 24f, 28f, 46f, 0.03f);
    }

    @Override
    public String getLibraryDescription() {
        return "One of Yog's guardians, mixing heavy melee with repeated bursts of fire across the arena lanes.";
    }
}