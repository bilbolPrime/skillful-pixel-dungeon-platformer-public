package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Regeneration extends Buff {
    float regenerationBonus = 0.2f;
    public Regeneration() {
        super("Regeneration", "+20% regen", "images/modifiers/regeneration.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyRegenerationRate(regenerationBonus);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyRegenerationRate(-regenerationBonus);
    }
}

