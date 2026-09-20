package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;


public final class NecromancerCurse extends Weaken {
    public static final float SECONDS = 6f;
    private boolean applied;
    private String originRoom;

    private NecromancerCurse() {
        name = "Curse";
        description = "The curse weakens attacks for a short time.";
        permanent = false;
        duration = SECONDS;
    }

    @Override public String getName() { return Messages.get("custom.newskills.59.name"); }
    @Override public String getDescription() { return Messages.get("custom.necromancer.curse_description"); }


    public static NecromancerCurse applyTo(Unit target) {
        if (target == null || target.isDead() || target.getHP() <= 0 || target.showOnly() || target.getRoom() == null) return null;
        NecromancerCurse current = (NecromancerCurse)target.getBuff(NecromancerCurse.class);
        if (current != null && current.applied && current.owner == target) {
            current.duration = SECONDS;
            return current;
        }
        if (current != null) target.getBuffs().remove(current);
        NecromancerCurse curse = new NecromancerCurse();
        curse.setOwner(target);
        return curse.applied ? curse : null;
    }

    @Override public Buff setOwner(Unit target) {
        if (applied || target == null || owner != null) return this;
        super.setOwner(target);
        applied = owner != null;
        if (applied) originRoom = owner.getRoom();
        return this;
    }

    @Override public void act(float delta) {
        if (!applied || owner == null || owner.isDead() || !originRoom.equals(owner.getRoom())) { debuff(); return; }
        if (originRoom.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            super.act(PhysicsHelper.boundGameDelta(delta));
            duration = Math.max(0f, duration);
        }
    }

    @Override public boolean active() { return applied && owner != null && super.active(); }

    private static boolean saveEligible(Unit target) {
        Hero hero = UnitHelper.getInstance().getHero();
        return hero != null && hero.getHeroClass() == HeroClass.NECROMANCER && hero.hasSkill(Skills.CURSE)
                && !hero.isDead() && hero.getHP() > 0 && target instanceof Mob && !target.isDead() && target.getHP() > 0
                && !target.showOnly() && target.isFriendly != hero.isFriendly && hero.getRoom() != null
                && hero.getRoom().equals(target.getRoom()) && hero.getRoom().equals(MapHelper.getInstance().getActiveRoomIdentifier());
    }


    public static Float savedRemaining(Unit target) {
        if (!saveEligible(target)) return null;
        NecromancerCurse curse = (NecromancerCurse)target.getBuff(NecromancerCurse.class);
        return curse != null && curse.active() && Float.isFinite(curse.duration) && curse.duration > 0f
                && curse.duration <= SECONDS ? curse.duration : null;
    }


    public static void restoreSaved(Unit target, Float remaining) {
        clear(target);
        if (!saveEligible(target) || remaining == null || !Float.isFinite(remaining) || remaining <= 0f || remaining > SECONDS) return;
        NecromancerCurse curse = applyTo(target);
        if (curse != null) curse.duration = remaining;
    }

    @Override public void debuff() {
        permanent = false; duration = 0f;
        if (applied && owner != null) super.debuff();
        applied = false; owner = null; originRoom = null;
    }


    public static void clear(Unit target) {
        if (target == null) return;
        for (Buff buff : target.getBuffs()) if (buff instanceof NecromancerCurse) buff.debuff();
    }

    public static void clearOutside(String room) {
        for (Unit unit : UnitHelper.getInstance().getUnits())
            if (room == null || !room.equals(unit.getRoom())) clear(unit);
    }

    public static void clearAll() {
        for (Unit unit : UnitHelper.getInstance().getUnits()) clear(unit);
    }
}
