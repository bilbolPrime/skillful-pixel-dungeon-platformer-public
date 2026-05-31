package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class ManaRegeneration extends Buff {
    float regenerationBonus = 0.2f;
    public ManaRegeneration() {
        super("Mana Regeneration", "+20% regen", "images/modifiers/mana-regeneration.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null || !(owner instanceof Hero)){
            return this;
        }

        (this.owner).modifyManaRegenerationRate(regenerationBonus);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyManaRegenerationRate(-regenerationBonus);
    }
}

