package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

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

    public static Integer savedAmount(Unit unit) {
        DeferredDamage debt = (DeferredDamage) unit.getBuff(DeferredDamage.class);
        return debt == null || debt.pendingDamage <= 0 ? null : debt.pendingDamage;
    }

    public static Float savedTick(Unit unit) {
        DeferredDamage debt = (DeferredDamage) unit.getBuff(DeferredDamage.class);
        return debt == null || debt.pendingDamage <= 0 ? null : debt.tickAt;
    }

    public static void restoreSaved(Unit unit, Integer amount, Float remainingTick) {

        if (amount == null || amount <= 0 || unit.isDead() || unit.getHP() <= 0) return;
        DeferredDamage debt = (DeferredDamage) unit.getBuff(DeferredDamage.class);
        if (debt == null) {
            debt = new DeferredDamage();
            debt.setOwner(unit);
        }
        debt.pendingDamage = amount;
        debt.tickAt = remainingTick == null || Float.isNaN(remainingTick) || Float.isInfinite(remainingTick)
                ? TICK_INTERVAL : Math.max(0f, Math.min(TICK_INTERVAL, remainingTick));
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

            tickAt += TICK_INTERVAL;
            pendingDamage--;

            if (owner.repayDeferredDamage(1) == 0) pendingDamage++;
        }

        if (pendingDamage <= 0) {
            setPermanent(false);
            setDuration(-1f);
        }
    }
}
