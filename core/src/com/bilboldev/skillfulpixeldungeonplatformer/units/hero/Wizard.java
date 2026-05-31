package com.bilboldev.skillfulpixeldungeonplatformer.units.hero;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Starving;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;

import java.util.ArrayList;

public class Wizard extends Hero {

    {
        heroClass = HeroClass.WIZARD;
        hp = mhp = heroClass.getHealth(1);
        mp = mmp = heroClass.getMana(1);
        isFriendly = true;
        gf = new GameFilm(heroClass.getFilm(),256, 128, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 15;
        gf.yClipOffset = 1;
        idleFrames = new int[]{0, 1, 1};
        runFrames = new int[]{2, 3, 4, 5, 6, 7};
        attackFrames = new int[]{13, 14, 15, 15};
        dieFrames = new int[]{8, 9, 10, 11, 12};
        jumpFrames =  new int[]{4};

        attackSpeed = heroClass.getAttackSpeed();
        speedX = heroClass.getMoveSpeed();
        refreshCombatStats();
    }
}

