package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

public class Brute extends Mob {
    private boolean enraged;

    {
        hp = mhp = 40;
        experience = 8;
        attackSkill = 20;
        defenseSkill = 15;
        damageReduction = 8;
        gf = new GameFilm("images/units/brute/brute.png", 256, 32, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{4, 5, 6, 7};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{8, 9, 10, 10};
        ai = new AgressiveAI(this);

        speedX = 300;
        attackSpeed = 4f;
        weapon = new MeleeAttack().setDamageRange(8f, 18f);
        weapon.setOwner(this);
    }

    @Override
    public void act(float delta) {
        if (!enraged && hp <= Math.max(1, mhp / 4)) {
            enraged = true;
            ((MeleeAttack) weapon).setDamageRange(10f, 40f);
            EffectsHelper.getInstance().message(this, "Enraged", Color.RED, 0f);
        }

        super.act(delta);
    }

    @Override
    public String getLibraryDescription() {
        return "A heavy prison enforcer that becomes far more dangerous once it is cornered and wounded.";
    }
}