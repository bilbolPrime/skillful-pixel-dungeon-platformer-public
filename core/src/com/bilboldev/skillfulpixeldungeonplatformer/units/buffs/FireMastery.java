package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class FireMastery extends Buff {
    public FireMastery() {
        super("Fire Mastery", "+20% damage", "images/modifiers/fire-mastery.png");
        permanent = false;
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }


        return this;
    }

    @Override
    public void debuff(){

    }
}

