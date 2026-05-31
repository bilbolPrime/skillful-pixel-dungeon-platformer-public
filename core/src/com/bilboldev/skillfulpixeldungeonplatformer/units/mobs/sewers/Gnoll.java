package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Gnoll extends Mob {
    {
        hp = mhp = 12;
        experience = 2;
        attackSkill = 11;
        defenseSkill = 4;
        damageReduction = 2;
        gf = new GameFilm("images/units/gnoll/gnoll.png",256, 32, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{4, 5, 6, 7};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{8, 9, 10, 10};
        ai = new AgressiveAI(this);

        speedX = 400;
        attackSpeed = 3f;
        weapon = new MeleeAttack().setDamageRange(2f, 5f);
        weapon.setOwner(this);
    }

    @Override
    protected void setLoot() {
        loot = RandomHelper.getInstance().randomChance(40) ? new Gold() : null;
    }

    @Override
    public String getLibraryDescription() {
        return "A brutal marauder that closes distance quickly and hits much harder than the vermin prowling the upper sewers.";
    }
}

