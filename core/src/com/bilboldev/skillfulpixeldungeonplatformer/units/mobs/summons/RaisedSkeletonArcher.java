package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.SkeletonArcherAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SkeletonArrowProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;

public final class RaisedSkeletonArcher extends NecromancerMinion {
    public static final float SHOT_INTERVAL = 1.2f;
    private float shotCooldown;

    public RaisedSkeletonArcher(Hero owner) { this(owner, owner == null ? 0 : owner.getLevel()); }
    public RaisedSkeletonArcher(Hero owner, int createdAtLevel) {
        super(owner, createdAtLevel);
        hp = mhp = 16 + 4 * (getSummonedLevel() - 1);
        float damage = 4f + .75f * (getSummonedLevel() - 1);
        setWeapon(new MeleeAttack().setDamageRange(damage, damage));
        attackSkill = 12; defenseSkill = 9; damageReduction = 5;
        speedX = 450f; attackSpeed = 4.5f;
        gf = new GameFilm("images/units/skeleton/skeleton.png", 256, 32, 1f);
        gf.clipSizeX = 12; gf.clipSizeY = 15;
        gf.setColor(new Color(.78f, 1f, .88f, 1f));
        idleFrames = new int[]{0, 1}; runFrames = new int[]{4, 5, 6, 7};
        attackFrames = new int[]{2, 3, 3, 3}; dieFrames = new int[]{8, 9, 10, 11, 12, 13, 14}; jumpFrames = new int[]{4};
        changeState(UnitState.IDLE, true);
        applyLich(owner);
    }
    @Override public void makeFriendly() {
        super.makeFriendly();
        ai = new SkeletonArcherAI(this);
    }
    public float getShotCooldown() { return shotCooldown; }
    public void restoreShotCooldown(float remaining) {
        if (!Float.isFinite(remaining) || remaining < 0f || remaining > SHOT_INTERVAL)
            throw new IllegalArgumentException("Invalid remaining archer shot cooldown");
        shotCooldown = remaining;
    }
    public boolean hasShotLine(Unit target) {
        if (!(target instanceof Mob) || target.isDead() || target.getHP() <= 0 || target.showOnly()
                || !target.isVisible() || target.isInvisible() || target.isFriendly == isFriendly
                || room == null || !room.equals(target.getRoom())
                || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) return false;
        float dx = target.x - x, dy = target.y - y;
        return dx * dx + dy * dy <= NewClassSpellProjectile.RANGE * NewClassSpellProjectile.RANGE
                && PhysicsHelper.getInstance().hasSolidLineOfSight(MapHelper.getInstance().getActiveRoom(),
                    x + 48f, y + 64f, target.x + 48f, target.y + 64f);
    }
    public boolean tryShoot(Unit target) {
        Hero hero = getOwnerHero();
        if (hero == null || hero.isDead() || isDead() || isPendingTransfer() || showOnly() || room == null || !room.equals(hero.getRoom())
                || hero.getNewClassActions().isSuspended() || WindowHelper.getInstance().windowOpen()
                || shotCooldown > 0f || !canAttack() || !hasShotLine(target)
                || NewClassSpellProjectile.liveCount() >= NewClassSpellProjectile.MAX_LIVE) return false;
        facingRight = target.x >= x;
        UnitHelper.getInstance().addUnit(new SkeletonArrowProjectile(this, target, weapon.getDamage()));
        shotCooldown = SHOT_INTERVAL;
        startRangedAttackAnimation(.3f);
        playSound(Sounds.MISS, .4f);
        return true;
    }
    @Override public void act(float delta) {
        Hero hero = getOwnerHero();
        boolean active = hero != null && !hero.isDead() && !isDead() && room != null
                && room.equals(hero.getRoom()) && room.equals(MapHelper.getInstance().getActiveRoomIdentifier());
        if (active) {
            shotCooldown = Math.max(0f, shotCooldown - PhysicsHelper.boundGameDelta(delta));
            if (shotCooldown < .000001f) shotCooldown = 0f;
        }
        super.act(delta);
        if (active && ai != null && PhysicsHelper.boundGameDelta(delta) > 0f) tryShoot(ai.getOther());
    }
    @Override public String getLibraryDescription() {
        return "A skeleton archer raised by a Lich. It follows its master and fires arrows at enemies.";
    }
}
