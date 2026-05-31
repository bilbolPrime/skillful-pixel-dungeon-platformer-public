package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Cripple extends Buff {
    public static final float DURATION = 10f;

    private static final float SPEED_PENALTY = 0.5f;
    private static final float ATTACK_SPEED_PENALTY = 0.5f;

    public Cripple() {
        super("Crippled", "-50% move speed and attack speed", "images/buffs/cripple.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner) {
        super.setOwner(owner);
        if (this.owner == null) {
            return this;
        }

        this.owner.modifySpeedModifier(-SPEED_PENALTY);
        this.owner.modifyAttackSpeedModifier(-ATTACK_SPEED_PENALTY);
        return this;
    }

    @Override
    public void debuff() {
        if (owner != null) {
            owner.modifySpeedModifier(SPEED_PENALTY);
            owner.modifyAttackSpeedModifier(ATTACK_SPEED_PENALTY);
        }
    }
}