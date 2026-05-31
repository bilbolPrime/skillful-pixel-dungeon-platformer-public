package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class BerserkerBuff extends Buff {
    private static final int HEALTH_BONUS = 100;
    private static final float DAMAGE_BONUS = 0.2f;

    public BerserkerBuff() {
        super("Berserker", "+100 HP +20% damage", "images/skills/berserker.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.setMaxHP(this.owner.getMaxHP() + HEALTH_BONUS);
        this.owner.heal(HEALTH_BONUS);
        this.owner.modifyOutgoingDamageModifier(DAMAGE_BONUS);
        return this;
    }

    @Override
    public void debuff(){
        if (owner == null) {
            return;
        }

        this.owner.setMaxHP(this.owner.getMaxHP() - HEALTH_BONUS);
        this.owner.setHP(Math.min(this.owner.getHP(), this.owner.getMaxHP()));
        this.owner.modifyOutgoingDamageModifier(-DAMAGE_BONUS);
    }
}
