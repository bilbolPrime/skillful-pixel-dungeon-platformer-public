package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons;


import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class FireElemental extends Mob {
    {
        //canFly = true;
        hp = mhp = 25;
        experience = 4;
        gf = new GameFilm("images/units/fire-elemental/fire-elemental.png",256, 16, 1f);
        gf.clipSizeY = 15;
        gf.clipSizeX = 12;
        idleFrames = new int[]{0, 1, 2, 3};
        runFrames = new int[]{0, 1, 2, 3};
        attackFrames = new int[]{4, 5, 6};
        dieFrames = new int[]{7, 8, 9, 10, 11, 12 ,13};
        ai = new AgressiveAI(this);

        jumpSpeed = 200;
        speedX = 400;
        attackSpeed = 5f;
        weapon = (MeleeAttack) new MeleeAttack().setDamage(10).setOwner(this);
    }

    @Override
    public String getLibraryDescription() {
        return "A conjured elemental that crackles with heat and swipes with burning claws once it reaches its target.";
    }
}

