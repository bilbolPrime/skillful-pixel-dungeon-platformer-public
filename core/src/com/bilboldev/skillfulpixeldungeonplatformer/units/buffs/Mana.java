package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
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

        int manaBase = this.owner.getMmp();
        if (this.owner instanceof Hero && ((Hero) this.owner).getHeroClass() == HeroClass.WIZARD) {
            Hero hero = (Hero) this.owner;

            manaBase = hero.getHeroClass().getMana(hero.getLevel());
        }
        manaBonus = (int) (0.2f * manaBase);
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

