package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class Poisoned extends Buff {
    private static final float SCORPION_POISON_DAMAGE_MULTIPLIER = 1.5f;
    private float tickAt = 1f;

    public Poisoned() {
        super("Poisoned", "Taking damage over time", "images/buffs/poison.png");
    }

    @Override
    public Buff setOwner(com.bilboldev.skillfulpixeldungeonplatformer.units.Unit owner) {
        if (owner != null && owner.getBuff(Purity.class) != null) {
            return this;
        }

        return super.setOwner(owner);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tickAt -= delta;
        if (owner == null || tickAt > 0f || owner.getHP() < 1) {
            return;
        }

        float poisonDamage = 1f;
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero != null && !owner.isFriendly && hero.hasSkill(Skills.SCORPION)) {
            poisonDamage *= SCORPION_POISON_DAMAGE_MULTIPLIER;
        }

        owner.takeDamage(owner, null, poisonDamage);
        if (owner instanceof Hero && owner.isDead()) {
            AchievementManager.getInstance().onDeathFromPoison();
        }
        EffectsHelper.getInstance().add(new TrapBurst().init(
                owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/green.png",
                8f,
                4,
                24f,
                30f,
                45f,
                0.03f));
        tickAt = 1f;
    }
}