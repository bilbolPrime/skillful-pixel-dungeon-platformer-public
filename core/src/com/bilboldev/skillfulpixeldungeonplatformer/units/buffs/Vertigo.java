package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Vertigo extends Buff {
    private static final float BASE_DURATION = 6f;
    private static final float SPEED_PENALTY = 0.2f;
    private static final float ACCURACY_PENALTY = 0.35f;
    private static final float EVASION_PENALTY = 0.35f;

    public Vertigo() {
        super("Vertigo", "Disoriented and less effective in combat.", "images/buffs/vertigo.png");
        duration = BASE_DURATION;
    }

    @Override
    public Buff setOwner(Unit owner) {
        if (owner == null) {
            return this;
        }

        Vertigo existing = (Vertigo) owner.getBuff(Vertigo.class);
        if (existing != null) {
            existing.duration = Math.max(existing.duration, duration > 0f ? duration : BASE_DURATION);
            return existing;
        }

        setPermanent(false);
        if (duration <= 0f) {
            duration = BASE_DURATION;
        }

        super.setOwner(owner);
        if (this.owner != null) {
            this.owner.modifySpeedModifier(-SPEED_PENALTY);
            this.owner.modifyAccuracyMultiplier(-ACCURACY_PENALTY);
            this.owner.modifyEvasionMultiplier(-EVASION_PENALTY);
        }
        return this;
    }

    @Override
    public void debuff() {
        if (owner != null) {
            owner.modifySpeedModifier(SPEED_PENALTY);
            owner.modifyAccuracyMultiplier(ACCURACY_PENALTY);
            owner.modifyEvasionMultiplier(EVASION_PENALTY);
        }
    }
}