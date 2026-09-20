package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;


public final class RaisedSkeleton extends NecromancerMinion {
    public RaisedSkeleton(Hero owner) {
        this(owner, owner == null ? 0 : owner.getLevel());
    }


    public RaisedSkeleton(Hero owner, int createdAtLevel) {
        super(owner, createdAtLevel);
        hp = mhp = 20 + 5 * (getSummonedLevel() - 1);
        float damage = 3f + .6f * (getSummonedLevel() - 1);

        attackSkill = 12; defenseSkill = 9; damageReduction = 5;
        speedX = 450f; attackSpeed = 4.5f;
        setWeapon(new MeleeAttack().setDamageRange(damage, damage));
        gf = new GameFilm("images/units/skeleton/skeleton.png", 256, 32, 1f);
        gf.clipSizeX = 12; gf.clipSizeY = 15;
        gf.setColor(new Color(.78f, 1f, .88f, 1f));
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{4, 5, 6, 7};
        attackFrames = new int[]{2, 3, 3, 3};
        dieFrames = new int[]{8, 9, 10, 11, 12, 13, 14};
        jumpFrames = new int[]{4};
        changeState(UnitState.IDLE, true);
        applySpiritBinder(owner);
        applyLich(owner);
    }

    @Override public String getLibraryDescription() {
        return "A skeleton raised to fight at the Necromancer's side.";
    }
}
