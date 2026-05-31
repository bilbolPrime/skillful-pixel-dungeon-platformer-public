package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Confused extends Buff {
    float bonus = 0.50f;
    public Confused() {
        super("Confuse", "Confuse", "images/buffs/vertigo.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        if(this.owner instanceof Mob){
            ((Mob)this.owner).confused();
        }
        //this.owner.modifySpeedModifier(-bonus);

        return this;
    }

    @Override
    public void debuff(){

    }
}

