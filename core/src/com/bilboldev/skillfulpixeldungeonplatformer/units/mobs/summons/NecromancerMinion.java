package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MinionPlacement;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.FriendlyAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;
import java.util.ArrayList;


public abstract class NecromancerMinion extends Mob {
    private final String ownerId;
    private final int summonedLevel;
    private float deathTime;
    private boolean pendingTransfer;
    private float placementRetry;
    private float platformDropTime;
    private boolean spiritBinderApplied;
    private boolean lichApplied;

    protected NecromancerMinion(Hero owner) {
        this(owner, owner == null ? 0 : owner.getLevel());
    }


    protected NecromancerMinion(Hero owner, int createdAtLevel) {
        if (owner == null || owner.getHeroClass() != HeroClass.NECROMANCER || owner.isDead()
                || owner.getPersistentId() == null || createdAtLevel < 1) throw new IllegalArgumentException("Living Necromancer and creation level required");
        ownerId = owner.getPersistentId();
        summonedLevel = createdAtLevel;
        isSummoned = true;
        experience = 0; dropChance = 0;
        makeFriendly();
    }

    public final String getOwnerId() { return ownerId; }
    public final int getSummonedLevel() { return summonedLevel; }
    public final boolean isPendingTransfer() { return pendingTransfer; }
    public final boolean hasSpiritBinderBonus() { return spiritBinderApplied; }
    public final boolean hasLichBonus() { return lichApplied; }

    public final void applyLich(Hero hero) {
        if (lichApplied || spiritBinderApplied || !belongsTo(hero) || !hero.hasSkill(Skills.LICH)
                || isDead() || hp <= 0 || !(weapon instanceof MeleeAttack)) return;
        ((MeleeAttack)weapon).setDamageRange(weapon.min() * 1.15f, weapon.max() * 1.15f);
        lichApplied = true;
    }


    public final void restoreLich(Hero hero, boolean savedApplied) {
        lichApplied = savedApplied;
        applyLich(hero);
    }


    public final void applySpiritBinder(Hero hero) {
        if (spiritBinderApplied || lichApplied || !belongsTo(hero) || !hero.hasSkill(Skills.SPIRIT_BINDER) || isDead() || hp <= 0 || mhp <= 0) return;
        int oldMax = mhp;
        mhp = (int)Math.min(Integer.MAX_VALUE, Math.round(oldMax * 1.2d));
        hp = Math.min(mhp, Math.max(1, (int)Math.round((double)hp * mhp / oldMax)));
        spiritBinderApplied = true;
    }


    public final void restoreSpiritBinder(Hero hero, boolean savedApplied) {
        spiritBinderApplied = savedApplied;
        applySpiritBinder(hero);
    }
    @Override public final boolean isDroppingThroughPlatform() { return platformDropTime > 0f; }
    public final boolean belongsTo(Hero hero) {
        return hero != null && hero.getHeroClass() == HeroClass.NECROMANCER && ownerId.equals(hero.getPersistentId());
    }
    public final Hero getOwnerHero() {
        if (!UnitHelper.getInstance().getUnits().contains(this)) return null;
        Hero hero = UnitHelper.getInstance().getHero();
        return belongsTo(hero) ? hero : null;
    }

    @Override public void appear(float x, float y) {
        super.appear(x, y);
        if (ai instanceof FriendlyAI) ((FriendlyAI)ai).startFollowingAtCurrentPosition();
    }


    public static int occupiedSlots(Hero hero) {
        int count = 0;
        for (Unit unit : UnitHelper.getInstance().getUnits())
            if (unit instanceof NecromancerMinion && ((NecromancerMinion)unit).belongsTo(hero) && !unit.isDead()) count++;
        return count;
    }

    public static void clearFor(Hero hero) {
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if (unit instanceof NecromancerMinion && ((NecromancerMinion)unit).belongsTo(hero))
                UnitHelper.getInstance().removeUnit(unit);
        }
    }


    public static void clearAll() {
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot())
            if (unit instanceof NecromancerMinion) UnitHelper.getInstance().removeUnit(unit);
    }


    public final void restorePlacement(Hero hero, boolean savedPending) {
        deathTime = platformDropTime = 0f;
        movingLeft = movingRight = false; momentX = speedY = airMomentumX = 0f;
        changeState(UnitState.IDLE, true); wakeToWandering();
        pendingTransfer = savedPending || !(canFly ? MinionPlacement.canRestoreFlyingAt(hero, x, y)
                : MinionPlacement.canRestoreAt(hero, x, y, getCollisionWidth()));
        showOnly = pendingTransfer; setVisible(!pendingTransfer); placementRetry = .25f;
        if (!pendingTransfer) {
            appear(x, y);
            PhysicsHelper.getInstance().register(this);
            PhysicsHelper.getInstance().syncBodyToUnit(this);
        }
    }


    public static void transferFor(Hero hero) {
        if (hero == null || hero.isDead() || hero.getHeroClass() != HeroClass.NECROMANCER
                || !MapHelper.getInstance().getActiveRoomIdentifier().equals(hero.getRoom())) return;
        ArrayList<NecromancerMinion> arriving = new ArrayList<>();
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if (!(unit instanceof NecromancerMinion) || !((NecromancerMinion)unit).belongsTo(hero)) continue;
            NecromancerMinion minion = (NecromancerMinion)unit;
            if (minion.isDead()) { UnitHelper.getInstance().removeUnit(minion); continue; }

            minion.pendingTransfer = true; minion.placementRetry = minion.platformDropTime = 0f;
            minion.showOnly = true; minion.setVisible(false);
            PhysicsHelper.getInstance().unregister(minion);
            minion.setRoom(hero.getRoom());
            minion.movingLeft = minion.movingRight = false;
            minion.momentX = minion.speedY = 0f;
            minion.changeState(UnitState.IDLE, true);
            minion.wakeToWandering();
            arriving.add(minion);
        }

        for (NecromancerMinion minion : arriving) minion.tryPlacement(hero);
    }

    private void tryPlacement(Hero hero) {
        placementRetry = .25f;
        Vector2 position = canFly ? MinionPlacement.findFlying(hero, getCollisionWidth()) : MinionPlacement.find(hero, getCollisionWidth());
        if (position == null) return;
        pendingTransfer = false; showOnly = false; setVisible(true);
        floorY = position.y; facingRight = hero.facingRight;
        appear(position.x, position.y);
        PhysicsHelper.getInstance().register(this);
        PhysicsHelper.getInstance().syncBodyToUnit(this);
    }

    @Override public void act(float delta) {
        Hero hero = getOwnerHero();
        boolean here = room != null && room.equals(MapHelper.getInstance().getActiveRoomIdentifier());
        if (hero == null || hero.isDead() || isDead() && !here) {
            UnitHelper.getInstance().removeUnit(this);
            return;
        }

        if (!here || !room.equals(hero.getRoom())) return;
        if (pendingTransfer && !isDead()) {
            float activeDelta = PhysicsHelper.boundGameDelta(delta);
            if (activeDelta > 0f) {
                placementRetry -= activeDelta;
                if (placementRetry <= 0f) tryPlacement(hero);
            }
            return;
        }
        if (showOnly && !isDead()) return;
        platformDropTime = Math.max(0f, platformDropTime - PhysicsHelper.boundGameDelta(delta));
        super.act(delta);
        if (!isDead() && !canFly && ai != null && ai.getOther() == null && platformDropTime == 0f
                && hero.y + 15f < y && y > ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE + 16f
                && PhysicsHelper.getInstance().isGrounded(this)) {


            platformDropTime = .25f;
            PhysicsHelper.getInstance().setVerticalSpeed(this, -10f);
        }
        if (isDead()) {
            deathTime += PhysicsHelper.boundGameDelta(delta);

            if (deathTime >= 2f) UnitHelper.getInstance().removeUnit(this);
        }
    }

    @Override protected void drawBodyFilm(Batch batch) {
        if (isDead()) gf.setAlpha(gf.getAlpha() * Math.max(0f, Math.min(1f, 2f - deathTime)));
        super.drawBodyFilm(batch);
    }


    @Override public final void makeHostile() { makeFriendly(); }
    @Override public final void confused() {
        movingLeft = movingRight = false;
        if (ai != null) ai.clearTarget();
    }

    @Override protected final void onDeathCommitted() {                                          }

    @Override public final void die() {
        NewClassSpellProjectile.clearFrom(this);
        ai = null; movingLeft = movingRight = false; momentX = speedY = 0f; floorY = y;
        showOnly = true;
        PhysicsHelper.getInstance().unregister(this);
    }

    @Override public final void unSummon() {

        UnitHelper.getInstance().removeUnit(this);
    }
}
