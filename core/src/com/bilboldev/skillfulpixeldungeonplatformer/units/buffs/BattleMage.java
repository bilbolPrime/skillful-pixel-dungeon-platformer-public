package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class BattleMage extends Buff {
    public BattleMage() {
        super("Battle Mage", "Battle Mage", "images/skills/battle-mage.png");
        permanent = true;
    }

    int healthBonus = 50;
    int manaBonus = 50;

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null ){
            return this;
        }


        this.owner.setMaxHP(this.owner.getMaxHP() +  healthBonus);
        this.owner.heal(healthBonus);
        (this.owner).setMaxMP((this.owner).getMmp() + manaBonus);
        ( this.owner).modifyMana(manaBonus);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.setMaxHP(this.owner.getMaxHP() -  healthBonus);
        this.owner.heal(0);
        (this.owner).setMaxMP((this.owner).getMmp() - manaBonus);
        ( this.owner).modifyMana(0);
    }
}

