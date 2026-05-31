package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class KnockBack extends ActiveSkill {

    {
        manaCost = 5;
    }

    public KnockBack(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){
        owner.getWeapon().modifyKnockback(400f);
        owner.attack(true);
        owner.getWeapon().modifyKnockback(-400f);

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "Back off!", Color.WHITE, 0);
            owner.modifyMana(-manaCost);
        }
    }
}

