package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Health extends Buff {
    int healthBonus = 0;
    public Health() {
        super("Health", "+20% health", "images/modifiers/health.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        float healthMultiplier = 0.2f;
        if (this.owner instanceof Hero && ((Hero) this.owner).getHeroClass() == HeroClass.ROGUE) {
            healthMultiplier = 0.15f;
        }

        int healthBase = this.owner.getMaxHP();
        if (this.owner instanceof Hero && (((Hero) this.owner).getHeroClass() == HeroClass.WARRIOR
                || ((Hero) this.owner).getHeroClass() == HeroClass.ROGUE)) {
            Hero hero = (Hero) this.owner;

            healthBase = hero.getHeroClass().getHealth(hero.getLevel());
        }
        healthBonus = Math.round(healthMultiplier * healthBase);
        this.owner.setMaxHP(this.owner.getMaxHP() +  healthBonus);
        this.owner.heal(healthBonus);
        return this;
    }

    @Override
    public void debuff(){
        this.owner.setMaxHP(this.owner.getMaxHP() -  healthBonus);
        this.owner.heal(0);
    }
}

