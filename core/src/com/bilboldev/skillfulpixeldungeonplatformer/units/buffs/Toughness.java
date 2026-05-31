package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Toughness extends Buff {
    float bonus = -0.2f;
    public Toughness() {
        super("Toughness", "+20% defence", "images/modifiers/toughness.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyIncomingDamageModifier(bonus);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyIncomingDamageModifier(-bonus);
    }
}

