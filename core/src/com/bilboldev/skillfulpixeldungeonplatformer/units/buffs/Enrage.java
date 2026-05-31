package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Enrage extends Buff {
    public Enrage() {
        super("Enrage", "+50% att -50% def", "images/modifiers/enrage.png");
        permanent = false;
        duration = 100f;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyOutgoingDamageModifier(0.5f);
        this.owner.modifyIncomingDamageModifier(0.5f);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyOutgoingDamageModifier(-0.5f);
        this.owner.modifyIncomingDamageModifier(-0.5f);

    }
}

