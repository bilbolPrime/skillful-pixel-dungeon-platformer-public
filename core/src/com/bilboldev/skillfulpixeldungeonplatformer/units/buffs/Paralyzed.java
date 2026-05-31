package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Paralyzed extends Buff {
    private static final float SPEED_PENALTY = 1f;
    private static final float ATTACK_SPEED_PENALTY = 1f;

    public Paralyzed() {
        super("Paralyzed", "Unable to move or attack.", "images/buffs/paralysis.png");
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
    public boolean preventsAttacks() {
        return true;
    }

    @Override
    public void debuff() {
        if (owner != null) {
            owner.modifySpeedModifier(SPEED_PENALTY);
            owner.modifyAttackSpeedModifier(ATTACK_SPEED_PENALTY);
        }
    }
}