package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Slow extends Buff {
    float bonus = 0.50f;
    public Slow() {
        super("Slow", "-50% speed", "images/buffs/slow.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifySpeedModifier(-bonus);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifySpeedModifier(bonus);
    }
}

