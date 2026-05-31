package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Larva extends Mob {
    private static final float VISUAL_SCALE = 0.5f;

    {
        isSummoned = true;
        hp = mhp = 25;
        experience = 0;
        attackSkill = 30;
        defenseSkill = 20;
        damageReduction = 8;
        dropChance = 0;
        gf = new GameFilm("images/units/larva/larva.png", 128, 8, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 8;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1, 2, 3};
        attackFrames = new int[]{4, 5, 6, 7};
        dieFrames = new int[]{1, 2};
        ai = new AgressiveAI(this);

        speedX = 390;
        attackSpeed = 5.25f;
        weapon = new MeleeAttack().setDamageRange(15f, 20f);
        weapon.setOwner(this);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (gf != null) {
            gf.setOrigin(ConstantsHelper.UNIT_DIMENSIONS / 2f, 0f);
            gf.setScale(VISUAL_SCALE, VISUAL_SCALE);
        }

        super.draw(batch, alpha);
    }

    @Override
    public String getLibraryDescription() {
        return "A disposable Yog spawn that exists to clutter the floor and close space fast.";
    }
}