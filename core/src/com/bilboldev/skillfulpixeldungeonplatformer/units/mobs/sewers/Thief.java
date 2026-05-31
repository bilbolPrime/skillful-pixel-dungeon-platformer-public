package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Thief extends Mob {
    private int stolenGold;

    {
        hp = mhp = 20;
        experience = 5;
        attackSkill = 12;
        defenseSkill = 12;
        damageReduction = 3;
        gf = new GameFilm("images/units/thief/thief.png", 256, 32, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 13;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1, 2, 3, 4};
        attackFrames = new int[]{10, 11, 12, 12};
        dieFrames = new int[]{5, 6, 7, 8, 9, 9, 9};
        ai = new AgressiveAI(this);

        speedX = 450;
        attackSpeed = 7f;
        weapon = new MeleeAttack().setDamageRange(1f, 7f);
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
            playSound(Sounds.HIT, 0.4f);

            if (target instanceof Hero) {
                stealGold((Hero) target);
            }
        }
        else {
            playSound(Sounds.MISS, 0.4f);
        }
    }

    private void stealGold(Hero hero) {
        int availableGold = InventoryHelper.getInstance().getGold();
        if (hero.isDead() || availableGold <= 0 || hero.preventsGoldTheft()) {
            return;
        }

        int stolen = Math.min(availableGold, 4 + RandomHelper.getInstance().randomInt(9));
        InventoryHelper.getInstance().modifyGold(-stolen);
        stolenGold += stolen;
        EffectsHelper.getInstance().message(hero, "-" + stolen + " gold", Color.GOLD, 0f);
        EffectsHelper.getInstance().message(this, "Stole " + stolen, Color.GOLD, 0f);
    }

    @Override
    public void die() {
        int bonusGold = stolenGold;
        super.die();

        if (bonusGold <= 0) {
            return;
        }

        ItemOnScreen itemOnScreen = new ItemOnScreen(new Gold().setQuantity(bonusGold));
        itemOnScreen.x = x;
        itemOnScreen.y = y;
        itemOnScreen.floorY = floorY;
        itemOnScreen.setRoom(room);
        UnitHelper.getInstance().addUnit(itemOnScreen);
    }

    @Override
    public String getLibraryDescription() {
        return "A quick sewer cutpurse that darts into melee, steals gold on contact, and drops its haul when brought down.";
    }
}