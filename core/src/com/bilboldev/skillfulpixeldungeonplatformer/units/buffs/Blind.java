package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Blind extends Buff {
    float bonus = 0.50f;
    public Blind() {
        super("Blind", "Blind", "images/buffs/blindness.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        if(this.owner instanceof Mob){
            ((Mob)this.owner).blinded();
        }


        return this;
    }

    @Override
    public void debuff(){
        if (owner instanceof Mob) ((Mob) owner).clearBlindness();
    }
}

