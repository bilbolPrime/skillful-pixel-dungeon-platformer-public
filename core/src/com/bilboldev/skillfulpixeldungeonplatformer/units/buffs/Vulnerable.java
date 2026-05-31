package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Vulnerable extends Buff {
    float bonus = 0.50f;
    public Vulnerable() {
        super("Vulnerable", "Vulnerable", "images/modifiers/vulnerability.png");
        duration = 25f;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyIncomingDamageModifier(0.25f);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyIncomingDamageModifier(-0.25f);
    }
}

