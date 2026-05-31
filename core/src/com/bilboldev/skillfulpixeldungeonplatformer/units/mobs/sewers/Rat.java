package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.ShortSword;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Aggression;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Mastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Regeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Starving;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Toughness;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Rat extends Mob {
    {
        hp = mhp = 8;
        experience = 1;
        attackSkill = 8;
        defenseSkill = 3;
        damageReduction = 1;
        gf = new GameFilm("images/units/rat/rat.png",256, 32, 1f);
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{5, 6, 7, 8, 9, 10};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{10, 11, 12, 13, 14};
        ai = new AgressiveAI(this);

        speedX = 350;
        attackSpeed = 5f;
        weapon = new MeleeAttack().setDamageRange(1f, 5f);
        weapon.setOwner(this);
    }

    @Override
    public String getLibraryDescription() {
        return "A filthy tunnel scavenger that overwhelms careless adventurers with numbers rather than strength.";
    }
}

