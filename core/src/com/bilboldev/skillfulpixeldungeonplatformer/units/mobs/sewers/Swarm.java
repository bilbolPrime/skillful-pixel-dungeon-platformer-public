package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;

public class Swarm extends Mob {
    {
        canFly = true;
        hp = mhp = 80;
        experience = 4;
        attackSkill = 12;
        defenseSkill = 5;
        damageReduction = 0;
        gf = new GameFilm("images/units/swarm/swarm.png",256, 32, 1f);
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1, 2, 3, 4, 5, 6};
        runFrames = new int[]{0, 1, 2, 3, 4, 5, 6};
        attackFrames = new int[]{7, 8, 9, 10};
        dieFrames = new int[]{11, 12, 13, 14, 14};
        ai = new AgressiveAI(this);

        jumpSpeed = 200;
        speedX = 400;
        attackSpeed = 5f;
        weapon = new MeleeAttack().setDamageRange(1f, 4f);
        weapon.setOwner(this);
    }

    @Override
    public void takeDamage(Unit source, Weapon damagingItem, float damage) {
        boolean physicalDamage = damagingItem != null && !(damagingItem instanceof Wand);
        super.takeDamage(source, damagingItem, damage);

        if (!physicalDamage || isDead() || hp < 2 || room == null) {
            return;
        }

        float preferredOffset = RandomHelper.getInstance().randomBoolean() ? -ConstantsHelper.UNIT_DIMENSIONS : ConstantsHelper.UNIT_DIMENSIONS;
        if (!splitAt(source, preferredOffset)) {
            splitAt(source, -preferredOffset);
        }
    }

    private boolean splitAt(Unit source, float offsetX) {
        int candidateX = Math.round(x + offsetX);
        int candidateY = Math.round(y);
        if (!UnitHelper.getInstance().freeSpace(this, candidateX, candidateY, room)) {
            return false;
        }

        int cloneHp = hp / 2;
        if (cloneHp < 1) {
            return false;
        }

        Swarm clone = new Swarm();
        clone.setRoom(room);
        clone.x = candidateX;
        clone.y = y;
        clone.floorY = floorY;
        clone.setHP(cloneHp);
        clone.changeState(UnitState.IDLE, true);

        Poisoned poison = (Poisoned) getBuff(Poisoned.class);
        if (poison != null) {
            new Poisoned().setDuration(poison.getRemainingDuration()).setOwner(clone);
        }

        UnitHelper.getInstance().addUnit(clone);
        setHP(hp - cloneHp);
        clone.alert(source);
        return true;
    }

    @Override
    public String getLibraryDescription() {
        return "A buzzing cloud of insects that ignores ground hazards, harasses from above, and dies only after the whole mass is dispersed.";
    }
}

