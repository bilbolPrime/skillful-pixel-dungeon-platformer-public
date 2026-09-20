package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassSkillTree;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.NewClassActionState;


public abstract class NewClassActiveSkill extends ActiveSkill {
    private final float cooldownSeconds;

    protected NewClassActiveSkill(int id, HeroClass heroClass, int tier, String name, String quickDescription,
            String description, String sprite, int manaCost, float cooldownSeconds) {
        super(id, heroClass, tier, name, quickDescription, description, sprite);
        if (NewClassSkillTree.node(heroClass, id) == null || !NewClassSkillTree.isReserved(id)
                || manaCost < 0 || !Float.isFinite(cooldownSeconds) || cooldownSeconds < 0f)
            throw new IllegalArgumentException("Invalid new-class action definition");
        this.manaCost = manaCost;
        this.cooldownSeconds = cooldownSeconds;
    }


    protected abstract boolean canRelease(Hero hero);


    protected abstract boolean release(Hero hero, Unit target);

    @Override public final boolean canUse(Unit owner) {
        if (!(owner instanceof Hero)) return false;
        Hero hero = (Hero)owner;
        if (hero != UnitHelper.getInstance().getHero() || hero.isDead() || hero.getHP() <= 0 || hero.showOnly()
                || !SkillsHelper.getInstance().isSupported(hero, this) || !hero.hasSkill(id)
                || hero.getRoom() == null || !hero.getRoom().equals(MapHelper.getInstance().getActiveRoomIdentifier())
                || WindowHelper.getInstance().windowOpen() || !hero.canAttack() || hero.getMp() < manaCost) return false;
        NewClassActionState state = hero.getNewClassActions();
        return !state.isSuspended() && !state.isReleasing() && state.cooldown(id) <= 0f && canRelease(hero);
    }


    public final boolean tryUse(Hero hero, Unit target) {
        if (!canUse(hero)) return false;
        NewClassActionState state = hero.getNewClassActions();
        if (!state.beginRelease()) return false;
        try {
            if (!release(hero, target)) return false;
            hero.modifyMana(-manaCost);
            state.startCooldown(id, cooldownSeconds);
            hero.recordActiveSkillUse(this);
            if (shouldPlayCastAnimation(hero)) {
                playCastAnimation(hero);
            }
            return true;
        } finally {
            state.endRelease();
        }
    }

    protected void playCastAnimation(Hero hero) {
        if (usesRangedAttackAnimation(hero)) hero.startRangedAttackAnimation(CAST_ATTACK_DURATION_SECONDS);
        else hero.startAttackAnimation(CAST_ATTACK_DURATION_SECONDS);
    }


    @Override public final void use(Unit owner, Unit target) {
        if (owner instanceof Hero) tryUse((Hero)owner, target);
    }
    @Override public final void startCooldown(float seconds) {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero != null && hero.hasSkill(id) && SkillsHelper.getInstance().isSupported(hero, this))
            hero.getNewClassActions().startCooldown(id, seconds);
    }
    @Override public final boolean isOnCooldown() { return getCooldownRemainingSeconds() > 0f; }
    @Override public final float getCooldownRemainingSeconds() {
        Hero hero = UnitHelper.getInstance().getHero();
        return hero != null && hero.hasSkill(id) && SkillsHelper.getInstance().isSupported(hero, this)
                ? hero.getNewClassActions().cooldown(id) : 0f;
    }
}
