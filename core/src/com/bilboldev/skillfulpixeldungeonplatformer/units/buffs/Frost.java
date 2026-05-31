package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Frost extends Buff {
    private static final float BASE_DURATION = 3.5f;
    private static final float SPEED_PENALTY = 1f;
    private static final float ATTACK_SPEED_PENALTY = 1f;

    public Frost() {
        super("Frozen", "Unable to move or attack while encased in frost.", "images/buffs/frost.png");
        duration = BASE_DURATION;
    }

    @Override
    public Buff setOwner(Unit owner) {
        if (owner == null) {
            return this;
        }

        Frost existing = (Frost) owner.getBuff(Frost.class);
        if (existing != null) {
            existing.duration = Math.max(existing.duration, duration > 0f ? duration : BASE_DURATION);
            return existing;
        }

        removeBuffImmediately(owner, Burning.class);
        setPermanent(false);
        if (duration <= 0f) {
            duration = BASE_DURATION;
        }

        super.setOwner(owner);
        if (this.owner != null) {
            this.owner.modifySpeedModifier(-SPEED_PENALTY);
            this.owner.modifyAttackSpeedModifier(-ATTACK_SPEED_PENALTY);
        }
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

    private void removeBuffImmediately(Unit owner, Class<? extends Buff> buffClass) {
        Buff other = owner.getBuff(buffClass);
        if (other == null) {
            return;
        }

        other.debuff();
        owner.getBuffs().remove(other);
    }
}