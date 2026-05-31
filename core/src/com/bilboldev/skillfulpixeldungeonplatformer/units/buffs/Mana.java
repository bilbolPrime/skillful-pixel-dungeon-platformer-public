package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Mana extends Buff {
    int manaBonus = 0;
    public Mana() {
        super("Mana", "+20% mana", "images/modifiers/mana.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        manaBonus = (int) ( 0.2f * (this.owner).getMmp());
        (this.owner).setMaxMP((this.owner).getMmp() + manaBonus);
        ( this.owner).modifyMana(manaBonus);
        return this;
    }

    @Override
    public void debuff(){
        (this.owner).setMaxMP((this.owner).getMmp() - manaBonus);
        ( this.owner).modifyMana(0);
    }
}

