package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Aggression extends Buff {
    float bonus = 0.2f;
    public Aggression() {
        super("Aggression", "+20% damage", "images/modifiers/aggression.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyOutgoingDamageModifier(bonus);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyOutgoingDamageModifier(-bonus);
    }
}

