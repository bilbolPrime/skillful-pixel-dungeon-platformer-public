package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Golem extends Mob {
    {
        hp = mhp = 85;
        experience = 12;
        attackSkill = 28;
        defenseSkill = 18;
        damageReduction = 12;
        gf = new GameFilm("images/units/golem/golem.png", 256, 16, 1f);
        gf.clipSizeX = 16;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4, 5};
        attackFrames = new int[]{6, 7, 8};
        dieFrames = new int[]{9, 10, 11, 12, 13, 13};
        ai = new AgressiveAI(this);

        speedX = 210;
        attackSpeed = 2f;
        weapon = new MeleeAttack().setDamageRange(20f, 40f);
        weapon.setOwner(this);
    }

    @Override
    public String getLibraryDescription() {
        return "A slow city tank that does not care about chip damage and makes every clean hit count.";
    }
}