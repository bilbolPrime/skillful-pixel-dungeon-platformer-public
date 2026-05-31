package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Smash extends ActiveSkill {

    {
        manaCost = 5;
    }

    public Smash(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){
        owner.modifyOutgoingDamageModifier(0.5f);
        owner.attack(true);
        owner.modifyOutgoingDamageModifier(-0.5f);

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "Die!", Color.WHITE, 0);
            owner.modifyMana(-manaCost);
        }
    }
}

