package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Bleeding;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;

public class AlbinoRat extends Rat {
    {
        gf.yClipOffset = 1;
        hp = mhp = 15;
        experience = 3;
        weapon = new MeleeAttack().setDamageRange(1f, 5f);
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
        float damage = weapon.getDamage();
        if (UnitHelper.getInstance().attackTarget(this, target, weapon, damage, false)) {
            if (target != null && RandomHelper.getInstance().randomChance(50)) {
                float bleedDuration = Math.max(0.75f, damage * 0.75f);
                Bleeding bleeding = (Bleeding) target.getBuff(Bleeding.class);
                if (bleeding != null) {
                    bleeding.setDuration(bleeding.getRemainingDuration() + bleedDuration);
                } else {
                    new Bleeding().setDuration(bleedDuration).setOwner(target);
                }
            }
            playSound(Sounds.HIT, 0.4f);
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
    }

    @Override
    public String getLibraryName() {
        return "Albino";
    }

    @Override
    public String getLibraryDescription() {
        return "A tougher, meaner sewer rat with pale fur and a nastier bite than the common pack vermin.";
    }
}

