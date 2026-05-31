package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Burning extends Buff {
    private static final float BASE_DURATION = 5f;
    private static final float TICK_INTERVAL = 1f;

    private float tickAt = TICK_INTERVAL;

    public Burning() {
        super("Burning", "Taking fire damage over time.", "images/buffs/fire.png");
        duration = BASE_DURATION;
    }

    @Override
    public Buff setOwner(Unit owner) {
        if (owner == null) {
            return this;
        }

        Burning existing = (Burning) owner.getBuff(Burning.class);
        if (existing != null) {
            existing.duration = Math.max(existing.duration, duration > 0f ? duration : BASE_DURATION);
            existing.tickAt = Math.min(existing.tickAt, TICK_INTERVAL);
            return existing;
        }

        removeBuffImmediately(owner, Frost.class);
        setPermanent(false);
        if (duration <= 0f) {
            duration = BASE_DURATION;
        }
        return super.setOwner(owner);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tickAt -= delta;

        if (owner == null || owner.getHP() < 1) {
            return;
        }

        while (tickAt <= 0f && owner.getHP() > 0) {
            owner.takeDamage(owner, null, 1f);
            if (owner instanceof Hero && owner.isDead()) {
                AchievementManager.getInstance().onDeathFromFire();
            }
            EffectsHelper.getInstance().spark(owner);
            tickAt += TICK_INTERVAL;
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