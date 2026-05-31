package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Frenzy extends BuffSkill {

    {
        manaCost = 10;
    }

    public Frenzy(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public boolean shouldPlayCastAnimation(Unit owner) {
        return false;
    }

    @Override
    public void use(Unit owner, Unit target){
        new com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Frenzy().setOwner(owner);

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "I run out of patience", Color.WHITE, 0);
            owner.modifyMana(-manaCost);
        }
    }
}

