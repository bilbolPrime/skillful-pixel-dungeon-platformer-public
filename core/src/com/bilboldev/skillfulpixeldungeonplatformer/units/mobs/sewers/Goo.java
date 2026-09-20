package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;


import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.SludgeBomb;

public class Goo extends Mob {
    private static final float WATER_HEAL_INTERVAL = 1f;
    private static final int WATER_HEAL_AMOUNT = 2;
    private static final float SPECIAL_OPENING_SECONDS = 3f;
    private static final float PUSH_INTERVAL_SECONDS = 4f;
    private static final float SPIT_INTERVAL_SECONDS = 6f;
    private static final float SPECIAL_WINDUP_SECONDS = 0.35f;
    private static final float SPECIAL_RECOVERY_SECONDS = 0.4f;
    private enum Special { NONE, PUSH, SPIT }
    protected float lastSludge = 0f;
    private float healOnWaterAt = WATER_HEAL_INTERVAL;
    private Special pendingSpecial = Special.NONE;
    private float specialWindupRemaining;
    private AI committedAI;
    private Unit committedTarget;
    private boolean committedFriendly;
    private boolean committedFacingRight;
    {
        boss = true;
        hp = mhp = 80;
        experience = 10;
        attackSkill = 15;
        defenseSkill = 12;
        damageReduction = 2;
        gf = new GameFilm("images/units/goo/goo.png",256, 16, 1f);
        gf.clipSizeX = 20;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1};
        attackFrames = new int[]{5, 6, 6};
        dieFrames = new int[]{2, 3, 4, 4};
        ai = new AgressiveAI(this){
            @Override
            public void act(float delta){
                lastSludge += delta;
                if (pendingSpecial != Special.NONE) return;
                super.act(delta);
            }

            @Override
            public void attacked(float delta){
                Unit target = getOther();

                if (lastSludge >= SPECIAL_OPENING_SECONDS && target != null && getOwner().canAttack()) {
                    if (UtilsHelper.distance(getOwner(), target) < ConstantsHelper.UNIT_DIMENSIONS) {
                        beginSpecial(Special.PUSH, target);
                        return;
                    }
                    if (Math.abs(target.y - y) <= ConstantsHelper.UNIT_DIMENSIONS
                            && Math.abs(target.x - x) <= ConstantsHelper.TILE * 4f
                            && UnitHelper.getInstance().canSeeTarget(Goo.this, target)) {
                        beginSpecial(Special.SPIT, target);
                        return;
                    }
                }

                super.attacked(delta);
            }
        };

        speedX = 350;
        attackSpeed = 5f;
        weapon = new MeleeAttack().setDamageRange(2f, 12f);
        weapon.setOwner(this);

        dropChance = 100;
    }

    @Override
    public void act(float delta) {
        if (pendingSpecial != Special.NONE && !canContinueSpecial()) cancelSpecial();
        super.act(delta);
        if (pendingSpecial != Special.NONE) {
            if (!canContinueSpecial()) cancelSpecial();
            else {
                facingRight = committedFacingRight;
                movingLeft = movingRight = false;
                specialWindupRemaining = Math.max(0f, specialWindupRemaining - delta);
                if (specialWindupRemaining <= 0f) releaseSpecial();
            }
        }
        MapHelper mapHelper = MapHelper.getInstance();

        if (getRoom() == null || !getRoom().equals(mapHelper.getActiveRoomIdentifier())) {
            resetHealOnWaterTimer();
            return;
        }

        if (!mapHelper.isStandingOnWater(this) || getHP() >= getMaxHP() || isDead()) {
            resetHealOnWaterTimer();
            return;
        }

        healOnWaterAt -= delta;
        if (healOnWaterAt > 0f) {
            return;
        }

        heal(WATER_HEAL_AMOUNT);
        EffectsHelper.getInstance().heal(this);
        resetHealOnWaterTimer();
    }

    private void beginSpecial(Special special, Unit target) {
        pendingSpecial = special;
        specialWindupRemaining = SPECIAL_WINDUP_SECONDS;
        committedAI = ai;
        committedTarget = target;
        committedFriendly = isFriendly;
        committedFacingRight = facingRight = x < target.x;
        movingLeft = movingRight = false;
        lastSludge = SPECIAL_OPENING_SECONDS - (special == Special.PUSH ? PUSH_INTERVAL_SECONDS : SPIT_INTERVAL_SECONDS);
        startAttackAnimation(SPECIAL_WINDUP_SECONDS + SPECIAL_RECOVERY_SECONDS);
        EffectsHelper.getInstance().message(this, special == Special.PUSH ? "..." : "Burp", Color.RED, 0f);
    }

    private boolean canContinueSpecial() {
        if (isDead() || getHP() < 1 || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())
                || ai == null || ai != committedAI || isFriendly != committedFriendly
                || ai.isBlind() || !ai.canTarget(committedTarget)) return false;
        for (Buff buff : buffs) if (buff.preventsAttacks()) return false;
        return true;
    }

    private void cancelSpecial() {
        pendingSpecial = Special.NONE;
        specialWindupRemaining = 0f;
        committedAI = null;
        committedTarget = null;
    }

    private void releaseSpecial() {
        Special special = pendingSpecial;

        pendingSpecial = Special.NONE;
        if (special == Special.PUSH) {
            MeleeAttack attack = (MeleeAttack) getWeapon();
            attack.setDamageRange(5f, 30f);
            attack.modifyKnockback(500f);
            try {
                applyMeleeContact(attack);
            } finally {
                attack.modifyKnockback(-500f);
                attack.setDamageRange(2f, 12f);
            }
        } else if (special == Special.SPIT) {
            for (int i = 0; i < 5; i++) {
                SludgeBomb sludgeBomb = new SludgeBomb();
                sludgeBomb.isFriendly = isFriendly;
                sludgeBomb.facingRight = facingRight;
                sludgeBomb.x = x;
                sludgeBomb.y = y + ConstantsHelper.UNIT_DIMENSIONS / 2;
                sludgeBomb.setOwner(this);
                sludgeBomb.setSpeedX(facingRight ? (400 + i * 125) : -(400 + i * 125));
                sludgeBomb.setSpeedY(50 + i * 75f);
                UnitHelper.getInstance().addUnit(sludgeBomb);
            }
        }
        cancelSpecial();
    }

    private void resetHealOnWaterTimer() {
        healOnWaterAt = WATER_HEAL_INTERVAL;
    }

    @Override
    public void die() {
        cancelSpecial();
        super.die();
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return;
        }

        currentRoom.markBossDefeated();

        for (Door door : currentRoom.getDoors()) {
            door.unlock();
        }
    }

    @Override
    public String getLibraryDescription() {
        return "The sewer boss. Goo lurches into melee up close but can also belch volleys of corrosive sludge when given room.";
    }
}
