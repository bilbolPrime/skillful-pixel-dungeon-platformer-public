package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Monk extends Mob {
    {
        hp = mhp = 70;
        experience = 11;
        attackSkill = 30;
        defenseSkill = 30;
        damageReduction = 2;
        gf = new GameFilm("images/units/monk/monk.png", 256, 32, 1f);
        gf.clipSizeX = 15;
        gf.clipSizeY = 14;
        idleFrames = new int[]{0, 1, 2};
        runFrames = new int[]{11, 12, 13, 14, 15, 16};
        attackFrames = new int[]{3, 4, 5, 6};
        dieFrames = new int[]{7, 8, 9, 10, 10};
        ai = new AgressiveAI(this);

        speedX = 430;
        attackSpeed = 8f;
        weapon = new MeleeAttack().setDamageRange(12f, 16f);
        weapon.setOwner(this);
    }

    @Override
    public void attack(boolean forced) {
        if (!canAttack() && !forced) {
            return;
        }

        rangedAttack = false;
        changeState(UnitState.ATTACKING, true);
        Unit target = PhysicsHelper.getInstance().queryFirstHit(this, weapon.getHitArea());
        if (UnitHelper.getInstance().attackTarget(this, target, weapon, weapon.getDamage(), false)) {
            if (target != null && target.isHero && RandomHelper.getInstance().randomInt(6) == 0) {
                Weapon equippedWeapon = target.getWeapon();
                if (equippedWeapon instanceof MeleeWeapon
                        && !(equippedWeapon instanceof MeleeAttack)
                        && !(equippedWeapon instanceof Knuckles)) {
                    ((MeleeWeapon) equippedWeapon).setEquipped(false);
                    InventoryHelper.getInstance().removeItem(equippedWeapon);
                    equippedWeapon.drop(target.x, target.y, target.getRoom());
                    EffectsHelper.getInstance().message(target, "My weapon!", Color.WHITE, 0f);
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
        return "A disciplined cave martial artist that closes quickly and lands far more hits than its frame suggests.";
    }
}
