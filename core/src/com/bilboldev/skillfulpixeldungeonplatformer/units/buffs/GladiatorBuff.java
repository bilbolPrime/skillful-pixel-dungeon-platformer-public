package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class GladiatorBuff extends Buff {
    private static final int HEALTH_BONUS = 50;

    private float attackSpeedBonus = 0f;

    public GladiatorBuff() {
        super("Gladiator", "+50 HP +20% att. speed", "images/skills/gladiator.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        attackSpeedBonus = 0.2f;
        this.owner.setMaxHP(this.owner.getMaxHP() + HEALTH_BONUS);
        this.owner.heal(HEALTH_BONUS);
        this.owner.modifyAttackSpeedModifier(attackSpeedBonus);
        return this;
    }

    @Override
    public void debuff(){
        if (owner == null) {
            return;
        }

        this.owner.setMaxHP(this.owner.getMaxHP() - HEALTH_BONUS);
        this.owner.heal(0);
        this.owner.modifyAttackSpeedModifier(-attackSpeedBonus);
    }
}