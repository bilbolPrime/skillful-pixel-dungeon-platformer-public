package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Weaken extends Buff {
    float bonus = 0.50f;
    public Weaken() {
        super("Weaken", "Weaken", "images/buffs/weakness.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyOutgoingDamageModifier(-0.25f);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyOutgoingDamageModifier(0.25f);
    }
}

