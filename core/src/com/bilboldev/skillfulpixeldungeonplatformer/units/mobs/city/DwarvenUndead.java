package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Paralyzed;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class DwarvenUndead extends Mob {
    {
        isSummoned = true;
        hp = mhp = 28;
        experience = 0;
        attackSkill = 16;
        defenseSkill = 15;
        damageReduction = 5;
        dropChance = 0;
        gf = new GameFilm("images/units/undead/undead.png", 256, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1, 2, 3};
        runFrames = new int[]{4, 5, 6, 7, 8, 9};
        attackFrames = new int[]{14, 15, 16, 16};
        dieFrames = new int[]{10, 11, 12, 13};
        ai = new AgressiveAI(this);

        speedX = 300;
        attackSpeed = 3.75f;
        weapon = new MeleeAttack().setDamageRange(12f, 16f);
        weapon.setOwner(this);
    }

    @Override
    public void attack(boolean forced) {
        if (!unitState.canAttack() && !forced) {
            return;
        }

        rangedAttack = false;
        changeState(UnitState.ATTACKING, true);
        Unit target = PhysicsHelper.getInstance().queryFirstHit(this, weapon.getHitArea());
        if (UnitHelper.getInstance().attackTarget(this, target, weapon, weapon.getDamage(), false)) {
            if (target != null && RandomHelper.getInstance().randomInt(5) == 0) {
                Paralyzed paralyzed = (Paralyzed) target.getBuff(Paralyzed.class);
                if (paralyzed != null) {
                    paralyzed.setPermanent(false).setDuration(1f);
                } else {
                    new Paralyzed().setPermanent(false).setDuration(1f).setOwner(target);
                }
            }
            playSound(Sounds.HIT, 0.4f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
    }

    @Override
    public String getLibraryDescription() {
        return "A short-lived royal summon that exists to crowd the city boss arena and keep the pressure on.";
    }
}