package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseTargeting;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.CorpseConsumed;


public abstract class CorpseActiveSkill extends NewClassActiveSkill {
    private final CorpseTargeting.Purpose purpose;

    protected CorpseActiveSkill(int id, int tier, String name, String quickDescription, String description,
            String sprite, int manaCost, float cooldownSeconds, CorpseTargeting.Purpose purpose) {
        super(id, HeroClass.NECROMANCER, tier, name, quickDescription, description, sprite, manaCost, cooldownSeconds);
        if (purpose == null || purpose == CorpseTargeting.Purpose.RECOVERY)
            throw new IllegalArgumentException("Active corpse skill requires a cast purpose");
        this.purpose = purpose;
    }


    protected abstract int occupiedMinionSlots(Hero hero);

    protected abstract CorpseTargeting.PreparedAction stage(Hero hero, CorpseTargeting.Target target);

    @Override protected final boolean canRelease(Hero hero) { return select(hero) != null; }
    private CorpseTargeting.Target select(Hero hero) {
        return CorpseTargeting.select(hero, purpose, occupiedMinionSlots(hero));
    }
    public final CorpseTargeting.Target preview(Hero hero) { return canUse(hero) ? select(hero) : null; }


    public final String unavailableReasonKey(Hero hero) {
        if (purpose.raisesMinion() && occupiedMinionSlots(hero) >= (hero.hasSkill(com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills.LICH) ? 3 : 2))
            return "custom.necromancer.cap_full";
        return select(hero) == null ? "custom.necromancer.no_corpse" : null;
    }

    @Override protected final boolean release(Hero hero, Unit ignored) {
        CorpseTargeting.Target target = select(hero);
        if (target == null) return false;
        CorpseTargeting.PreparedAction prepared = stage(hero, target);
        if (!CorpseTargeting.consume(hero, target, () -> occupiedMinionSlots(hero), prepared)) return false;
        EffectsHelper.getInstance().add(new CorpseConsumed(hero, target.x, target.y));
        return true;
    }


    public static CorpseTargeting.Target markerTarget(Hero hero) {
        if (hero == null) return null;
        int hovered = UIHelper.getInstance().getHoveredQuickSkillSlot();
        if (hovered >= 0) return previewSlot(hero, hovered);
        int requested = hero.getLastRequestedQuickSkillSlot();
        if (hero.getQuickSkill(requested) instanceof CorpseActiveSkill) return previewSlot(hero, requested);
        for (int slot = 0; slot < hero.getQuickSkillSlotCount(); slot++) {
            if (hero.getQuickSkill(slot) instanceof CorpseActiveSkill) return previewSlot(hero, slot);
        }
        return null;
    }
    private static CorpseTargeting.Target previewSlot(Hero hero, int slot) {
        ActiveSkill skill = hero.getQuickSkill(slot);
        return skill instanceof CorpseActiveSkill ? ((CorpseActiveSkill)skill).preview(hero) : null;
    }
}
