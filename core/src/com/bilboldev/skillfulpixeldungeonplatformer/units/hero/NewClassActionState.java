package com.bilboldev.skillfulpixeldungeonplatformer.units.hero;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassSkillTree;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;


public final class NewClassActionState {
    private static final int COUNT = Skills.EXECUTE - Skills.MIND_SHOT + 1;
    private final float[] cooldowns = new float[COUNT];
    private final float[] durations = new float[COUNT];
    private boolean suspended, releasing;

    public float cooldown(int id) { return NewClassSkillTree.isReserved(id) ? cooldowns[id - Skills.MIND_SHOT] : 0f; }
    public float duration(int id) { return NewClassSkillTree.isReserved(id) ? durations[id - Skills.MIND_SHOT] : 0f; }
    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean value) { suspended = value; }
    public boolean isReleasing() { return releasing; }
    public boolean beginRelease() {
        if (releasing || suspended) return false;
        releasing = true;
        return true;
    }
    public void endRelease() { releasing = false; }

    public void startCooldown(int id, float seconds) {
        if (NewClassSkillTree.isReserved(id)) cooldowns[id - Skills.MIND_SHOT] = Math.max(cooldown(id), seconds(seconds));
    }
    public void setDuration(int id, float seconds) {
        if (NewClassSkillTree.isReserved(id)) durations[id - Skills.MIND_SHOT] = seconds(seconds);
    }
    public void tick(float delta) {
        if (suspended || !Float.isFinite(delta)) return;
        float elapsed = PhysicsHelper.boundGameDelta(delta);
        for (int index = 0; index < COUNT; index++) {
            cooldowns[index] = Math.max(0f, cooldowns[index] - elapsed);
            durations[index] = Math.max(0f, durations[index] - elapsed);
        }
    }
    public float[] copyCooldowns() { return cooldowns.clone(); }
    public float[] copyDurations() { return durations.clone(); }


    public void restore(Hero hero, float[] savedCooldowns, float[] savedDurations) {
        for (int index = 0; index < COUNT; index++) {
            int id = Skills.MIND_SHOT + index;
            boolean allowed = hero.hasSkill(id) && NewClassSkillTree.node(hero.getHeroClass(), id) != null;
            cooldowns[index] = allowed ? savedSeconds(savedCooldowns, index) : 0f;
            durations[index] = allowed ? savedSeconds(savedDurations, index) : 0f;
        }
        releasing = false;
    }
    private static float savedSeconds(float[] values, int index) {
        return values != null && index < values.length ? seconds(values[index]) : 0f;
    }
    private static float seconds(float value) { return Float.isFinite(value) ? Math.max(0f, value) : 0f; }
}
