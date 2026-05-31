package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

public class Spinner extends Mob {
    {
        hp = mhp = 50;
        experience = 9;
        attackSkill = 20;
        defenseSkill = 14;
        damageReduction = 6;
        gf = new GameFilm("images/units/spinner/spinner.png", 256, 16, 1f);
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3};
        attackFrames = new int[]{4, 5, 5};
        dieFrames = new int[]{6, 7, 8, 9, 9, 9};
        ai = new AgressiveAI(this);

        speedX = 360;
        attackSpeed = 5f;
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
            if (RandomHelper.getInstance().randomChance(50)) {
                new Poisoned().setDuration(4f).setOwner(target);
            }
            playSound(Sounds.HIT, 0.4f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
    }

    @Override
    public String getLibraryDescription() {
        return "A cave predator that rushes in close and leaves venom behind on a clean strike.";
    }
}