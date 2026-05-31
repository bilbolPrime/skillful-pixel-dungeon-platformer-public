package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class Venom extends BuffSkill {

    {
        manaCost = 5;
    }

    public Venom(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner) {
        return super.canUse(owner) && owner instanceof Hero;
    }

    @Override
    public boolean shouldPlayCastAnimation(Unit owner) {
        return false;
    }

    @Override
    public void use(Unit owner, Unit target) {
        Hero hero = (Hero) owner;
        hero.activateVenom();
        hero.attack(true);
        float centerX = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float centerY = hero.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        EffectsHelper.getInstance().add(new TrapBurst().init(
                centerX,
                centerY,
                "images/misc/green.png",
                9f,
                10,
                24f,
                26f,
                46f,
                0.01f));
        EffectsHelper.getInstance().add(new TrapBurst().init(
                centerX,
                centerY,
                "images/misc/black-particle.png",
                8f,
                6,
                20f,
                18f,
                38f,
                -0.01f));
        hero.modifyMana(-manaCost);
            EffectsHelper.getInstance().message(hero, getName(), Color.GREEN, 0f);
    }
}