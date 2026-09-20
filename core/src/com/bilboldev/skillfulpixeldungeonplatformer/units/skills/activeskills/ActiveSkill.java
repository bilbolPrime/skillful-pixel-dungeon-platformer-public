package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;

public class ActiveSkill extends Skill {
    public static final float CAST_ATTACK_DURATION_SECONDS = 0.5f;

    protected int manaCost;
    private long cooldownUntilMillis;

    public ActiveSkill(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    public void use(Unit owner, Unit target){

    }

    public boolean canUse(Unit owner){
        return owner != null
                && owner.canAttack()
                && !isOnCooldown()
                && (!(owner instanceof Hero) || manaCost <= ((Hero)owner).getMp());
    }

    public boolean usesRangedAttackAnimation(Unit owner) {
        return false;
    }

    public boolean shouldPlayCastAnimation(Unit owner) {
        return true;
    }

    public int getManaCost(){
        return manaCost;
    }

    public void startCooldown(float durationSeconds) {
        cooldownUntilMillis = Math.max(
                cooldownUntilMillis,
                TimeUtils.millis() + (long) (Math.max(0f, durationSeconds) * 1000f));
    }

    public boolean isOnCooldown() {
        return TimeUtils.millis() < cooldownUntilMillis;
    }

    public float getCooldownRemainingSeconds() {
        return Math.max(0L, cooldownUntilMillis - TimeUtils.millis()) / 1000f;
    }
}

