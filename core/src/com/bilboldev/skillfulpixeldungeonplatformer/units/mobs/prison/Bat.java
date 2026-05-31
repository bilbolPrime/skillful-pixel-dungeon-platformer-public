package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

public class Bat extends Mob {
    {
        canFly = true;
        hp = mhp = 30;
        experience = 7;
        attackSkill = 16;
        defenseSkill = 15;
        damageReduction = 4;
        gf = new GameFilm("images/units/bat/bat.png", 128, 16, 1f);
        gf.clipSizeX = 15;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{4, 5, 6, 6, 6};
        ai = new AgressiveAI(this);

        jumpSpeed = 180;
        speedX = 480;
        attackSpeed = 6.5f;
        weapon = new MeleeAttack().setDamageRange(6f, 12f);
        weapon.setOwner(this);
    }

    @Override
    public void attack(boolean forced) {
        if (!unitState.canAttack() && !forced) {
            return;
        }

        rangedAttack = false;
        changeState(unitState.ATTACKING, true);
        Unit target = PhysicsHelper.getInstance().queryFirstHit(this, weapon.getHitArea());
        int targetHpBefore = target != null ? target.getHP() : 0;
        if (UnitHelper.getInstance().attackTarget(this, target, weapon, weapon.getDamage(), false)) {
            heal(Math.max(0, targetHpBefore - target.getHP()));
            playSound(Sounds.HIT, 0.4f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }

        if (isCanFly()) {
            fly(false, true);
        }
    }

    @Override
    public String getLibraryDescription() {
        return "A fast flying hunter that siphons health back on every clean hit.";
    }
}