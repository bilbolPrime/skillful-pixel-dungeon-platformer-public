package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;

public class FetidRat extends Rat {
    {
        hp = mhp = 24;
        experience = 4;
        attackSkill = 12;
        defenseSkill = 5;
        damageReduction = 2;
        gf = new GameFilm("images/units/rat/fetid-rat.png", 256, 32, 1f);
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{5, 6, 7, 8, 9, 10};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{10, 11, 12, 13, 14};
        ai = new AgressiveAI(this);
        weapon = (MeleeAttack) new MeleeAttack().setOwner(this);
    }

    @Override
    public String getLibraryDescription() {
        return "A diseased sewer alpha that reeks of death and carries the rage of the ghost's unfinished grudge.";
    }
}