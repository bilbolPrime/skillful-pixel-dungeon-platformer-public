package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class MirrorImage extends Mob {
    {
        hp = mhp = 10;
        experience = 0;
        dropChance = 0;
        gf = new GameFilm("images/units/warrior/warrior.png", 256, 128, 1f);
        gf.clipSizeY = 15;
        gf.clipSizeX = 12;
        gf.yClipOffset = 1;
        idleFrames = new int[]{0, 1, 2, 3};
        runFrames = new int[]{0, 1, 2, 3};
        attackFrames = new int[]{4, 5, 6};
        dieFrames = new int[]{7, 8, 9, 10, 11, 12, 13};
        ai = new AgressiveAI(this);

        jumpSpeed = 200;
        speedX = 500;
        attackSpeed = 7f;
        weapon = (MeleeAttack) new MeleeAttack().setDamage(5).setOwner(this);
    }

    public MirrorImage initFromHero(Hero hero) {
        if (hero == null) {
            return this;
        }

        gf = new GameFilm(hero.getHeroClass().getFilm(), 256, 128, 1f);
        gf.clipSizeY = 15;
        gf.clipSizeX = 12;
        gf.yClipOffset = 1;
        idleFrames = new int[]{0, 1, 1};
        runFrames = new int[]{2, 3, 4, 5, 6, 7};
        attackFrames = new int[]{13, 14, 15, 15};
        dieFrames = new int[]{8, 9, 10, 11, 12};
        jumpFrames = new int[]{4};
        gf.tileY = hero.getArmor() != null ? Math.max(0, hero.getArmor().getTier() - 1) : 0;
        canFly = false;
        hp = mhp = Math.max(8, hero.getMaxHP() / 4);
        jumpSpeed = 800f;
        speedX = hero.getSpeedX();
        attackSpeed = hero.getBaseAttackSpeed();
        setBaseAttackSkill(hero.getBaseAttackSkill());
        setBaseDefenseSkill(hero.getBaseDefenseSkill());
        float imageDamage = hero.getWeapon() != null ? hero.getWeapon().min() : 5f;
        setWeapon((MeleeAttack) new MeleeAttack().setDamage(Math.max(4f, imageDamage)));
        return this;
    }

    @Override
    public String getLibraryDescription() {
        return "An illusory copy shaped from the reader's silhouette and armed with a weaker echo of their attacks.";
    }
}