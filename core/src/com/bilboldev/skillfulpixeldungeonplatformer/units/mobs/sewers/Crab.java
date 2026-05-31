package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Crab extends Mob {
    {
        hp = mhp = 15;
        experience = 3;
        attackSkill = 12;
        defenseSkill = 5;
        damageReduction = 4;
        gf = new GameFilm("images/units/crab/crab.png",256, 16, 1f);
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4, 5, 6};
        attackFrames = new int[]{7, 8, 9, 9};
        dieFrames = new int[]{10, 11, 12, 13, 14};
        ai = new AgressiveAI(this);

        speedX = 550;
        attackSpeed = 8f;

        weapon = new MeleeAttack().setDamageRange(3f, 6f);
        weapon.setOwner(this);
    }

    @Override
    public String getLibraryDescription() {
        return "An armored sewer crab that rushes forward with surprising speed and punishes sloppy melee timing.";
    }
}

