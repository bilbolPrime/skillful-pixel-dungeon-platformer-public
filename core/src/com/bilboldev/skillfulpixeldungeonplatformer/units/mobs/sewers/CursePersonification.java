package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class CursePersonification extends Mob {
    {
        hp = mhp = 28;
        experience = 4;
        attackSkill = 14;
        defenseSkill = 7;
        damageReduction = 1;
        canFly = true;
        gf = new GameFilm("images/units/ghost/curse-personification.png", 128, 16, 1f);
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4, 5};
        attackFrames = new int[]{5, 6, 7};
        dieFrames = new int[]{7};
        ai = new AgressiveAI(this);
        speedX = 380;
        attackSpeed = 5.5f;
        weapon = (MeleeAttack) new MeleeAttack().setOwner(this);
    }

    @Override
    public String getLibraryDescription() {
        return "A hateful knot of cursed magic given shape by the ghost's lingering torment.";
    }
}