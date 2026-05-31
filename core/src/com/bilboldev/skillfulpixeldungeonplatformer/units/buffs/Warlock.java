package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Warlock extends Buff {
    float regenerationBonus = 0.2f;
    int manaBonus = 50;

    public Warlock() {
        super("Warlock", "Warlock", "images/modifiers/warlock.png");
        permanent = true;
    }



    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null || !(owner instanceof Hero)){
            return this;
        }

        (this.owner).modifyManaRegenerationRate(regenerationBonus);

        (this.owner).setMaxMP((this.owner).getMmp() + manaBonus);
        ( this.owner).modifyMana(manaBonus);

        return this;
    }

    @Override
    public void debuff(){
        (this.owner).modifyManaRegenerationRate(-regenerationBonus);
        (this.owner).setMaxMP((this.owner).getMmp() - manaBonus);
        ( this.owner).modifyMana(0);
    }
}

