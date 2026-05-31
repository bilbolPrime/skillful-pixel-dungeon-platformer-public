package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Mastery extends Buff {
    float bonus = 0f;
    public Mastery() {
        super("Mastery", "+20% att. speed", "images/modifiers/attack-speed.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        bonus = 0.2f;
        UnitHelper.getInstance().getHero().modifyAttackSpeedModifier(bonus);

        return this;
    }

    @Override
    public void debuff(){
        UnitHelper.getInstance().getHero().modifyAttackSpeedModifier(-bonus);
    }
}

