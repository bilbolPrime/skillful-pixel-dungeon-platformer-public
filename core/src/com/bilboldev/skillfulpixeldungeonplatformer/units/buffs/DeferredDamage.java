package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

public class DeferredDamage extends Buff {
    private static final float TICK_INTERVAL = 1f;

    private int pendingDamage;
    private float tickAt = TICK_INTERVAL;

    public DeferredDamage() {
        super("Deferred Damage", "Part of the damage will return over the next few moments.", "images/buffs/deferred.png");
        permanent = true;
    }

    public DeferredDamage addPendingDamage(int amount) {
        pendingDamage += Math.max(0, amount);
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tickAt -= delta;

        if (owner == null || owner.getHP() < 1) {
            setPermanent(false);
            setDuration(-1f);
            return;
        }

        while (tickAt <= 0f && pendingDamage > 0 && owner.getHP() > 0) {
            owner.takeDamage(owner, null, 1f);
            pendingDamage--;
            tickAt += TICK_INTERVAL;
        }

        if (pendingDamage <= 0) {
            setPermanent(false);
            setDuration(-1f);
        }
    }
}