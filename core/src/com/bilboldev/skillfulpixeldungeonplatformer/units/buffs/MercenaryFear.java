package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;


public final class MercenaryFear extends Buff {
    public static final float SECONDS = 1f;
    private final Hero source;
    private final String originRoom;

    @Override public String getName() { return Messages.get("custom.newskills.73.name"); }
    @Override public String getDescription() { return Messages.get("custom.newskills.73.description"); }

    private MercenaryFear(Hero hero) {
        super("No Witnesses", "Nearby mobs run away.", "images/buffs/terror.png");
        source = hero;
        originRoom = hero.getRoom();
        duration = SECONDS;
    }

    public static boolean canAffect(Unit target) {
        Hero hero = UnitHelper.getInstance().getHero();
        return hero != null && hero.getHeroClass() == HeroClass.MERCENARY && hero.hasSkill(Skills.NO_WITNESSES)
                && !hero.isDead() && hero.getHP() > 0 && target instanceof Mob && !((Mob)target).isBoss()
                && !target.isDead() && target.getHP() > 0 && !target.showOnly() && target.isFriendly != hero.isFriendly
                && hero.getRoom() != null && hero.getRoom().equals(target.getRoom())
                && hero.getRoom().equals(MapHelper.getInstance().getActiveRoomIdentifier());
    }

    public static MercenaryFear applyTo(Unit target) {
        if (!canAffect(target)) return null;
        Hero hero = UnitHelper.getInstance().getHero();
        MercenaryFear current = (MercenaryFear)target.getBuff(MercenaryFear.class);
        if (current != null && current.active() && current.source == hero) {
            current.duration = SECONDS;
            return current;
        }
        if (current != null) { current.debuff(); target.getBuffs().remove(current); }
        MercenaryFear fear = new MercenaryFear(hero);
        fear.setOwner(target);
        if (fear.owner == null) return null;
        ((Mob)target).wakeToWandering();
        target.changeState(UnitState.IDLE, true);
        fear.moveAway();
        return fear;
    }

    @Override public boolean active() {
        return owner != null && duration > 0f && source == UnitHelper.getInstance().getHero()
                && originRoom.equals(source.getRoom()) && canAffect(owner);
    }

    @Override public boolean preventsAttacks() { return active(); }


    public void moveAway() {
        if (!active()) { debuff(); return; }
        boolean right = owner.x == source.x ? source.facingRight : owner.x > source.x;
        float nextX = owner.x + (right ? 24f : -24f);
        float limit = MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS;
        boolean clear = nextX > 1f && nextX < limit - 1f
                && UnitHelper.getInstance().freeSpace(owner, (int)nextX, (int)owner.y, originRoom);

        if (clear && !owner.isCanFly() && Math.abs(owner.y - owner.floorY) < 12f)
            clear = MapHelper.getInstance().calculateFloorY(nextX, owner.y - 1f) >= owner.floorY - 12f;
        owner.movingLeft = clear && !right;
        owner.movingRight = clear && right;
        owner.facingRight = right;
    }

    @Override public void act(float delta) {
        if (!active()) { debuff(); return; }
        duration = Math.max(0f, duration - PhysicsHelper.boundGameDelta(delta));
    }

    @Override public void debuff() {
        duration = 0f;
        if (owner != null) { owner.movingLeft = false; owner.movingRight = false; }
        owner = null;
    }

    public static Float savedRemaining(Unit target) {
        if (!canAffect(target)) return null;
        MercenaryFear fear = (MercenaryFear)target.getBuff(MercenaryFear.class);
        return fear != null && fear.active() && Float.isFinite(fear.duration) && fear.duration <= SECONDS
                ? fear.duration : null;
    }

    public static void restoreSaved(Unit target, Float remaining) {
        clear(target);
        if (remaining == null || !Float.isFinite(remaining) || remaining <= 0f || remaining > SECONDS) return;
        MercenaryFear fear = applyTo(target);
        if (fear != null) fear.duration = remaining;
    }

    public static void clear(Unit target) {
        if (target != null) for (Buff buff : target.getBuffs()) if (buff instanceof MercenaryFear) buff.debuff();
    }
    public static void clearOutside(String room) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) if (room == null || !room.equals(unit.getRoom())) clear(unit);
    }
    public static void clearAll() {
        for (Unit unit : UnitHelper.getInstance().getUnits()) clear(unit);
    }
}
