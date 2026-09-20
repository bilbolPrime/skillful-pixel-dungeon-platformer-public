package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PoisonCloud;

public class RottingFist extends YogFist {
    private static final float POISON_INTERVAL = 2f;
    private float regenAt;
    private float poisonAt;

    {
        boss = true;
        hp = mhp = 300;
        experience = 0;
        attackSkill = 36;
        defenseSkill = 25;
        damageReduction = 15;
        dropChance = 0;
        gf = new GameFilm("images/units/rotting-fist/rotting-fist.png", 128, 32, 1f);
        gf.clipSizeX = 24;
        gf.clipSizeY = 17;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1};
        attackFrames= new int[]{0, 1};
        dieFrames = new int[]{ 2, 3, 4};
        ai = new AgressiveAI(this);

        speedX = 235;
        attackSpeed = 3.1f;
        weapon = new MeleeAttack().setDamageRange(24f, 36f);
        weapon.setOwner(this);
    }

    @Override
    public void act(float delta) {
        if (room != null && room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            poisonAt = Math.max(0f, poisonAt - delta);
        }
        if (MapHelper.getInstance().isStandingOnWater(this) && hp < mhp) {
            regenAt -= delta;
            if (regenAt <= 0f) {
                hp = Math.min(mhp, hp + 1);
                regenAt = 0.25f;
            }
        } else {
            regenAt = 0f;
        }

        super.act(delta);
    }

    @Override
    public void attack(boolean forced) {
        if (!forced && !canAttack()) {
            return;
        }

        Unit target = ai == null ? null : ai.getOther();
        super.attack(forced);

        if (target == null || target.isDead() || target.getRoom() == null || !target.getRoom().equals(room)
                || poisonAt > 0f) {
            return;
        }

        PoisonCloud cloud = new PoisonCloud().setOwner(this);
        cloud.x = target.x;
        cloud.y = target.y;
        cloud.floorY = target.floorY;
        cloud.setRoom(room);
        UnitHelper.getInstance().addUnit(cloud);
        poisonAt = POISON_INTERVAL;
    }

    @Override
    protected void emitAmbientParticles() {
        emitAmbientBurst("images/misc/green.png", 12f, 5, 22f, 24f, 46f, 0.02f);
        emitAmbientBurst("images/misc/black-particle.png", 10f, 4, 18f, 16f, 38f, -0.01f);
    }

    @Override
    public String getLibraryDescription() {
        return "One of Yog's guardians, a slower but heavier fist that blankets its target in poisonous gas while regenerating in water.";
    }
}
