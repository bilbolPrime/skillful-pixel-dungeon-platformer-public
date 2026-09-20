package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.TomeOfMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Shuriken;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.projectiles.ShurikenProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

import java.util.ArrayList;
import java.util.Collections;

public class Tengu extends Mob {
    private static final float TELEPORT_COOLDOWN_SECONDS = 10f;
    private static final float BLOCKED_LANE_REPOSITION_SECONDS = 4f;
    private static final float SHURIKEN_ATTACK_INTERVAL_SECONDS = 2f;
    private static final float SHURIKEN_BURST_WINDUP_SECONDS = 0.2f;
    private static final float SHURIKEN_BURST_SHOT_INTERVAL_SECONDS = 0.18f;
    private static final float TELEPORT_ANIMATION_FRAME_RATE = 10f;
    private static final float SHURIKEN_SPEED_X = 1640f;
    private static final float SHURIKEN_BASE_SPEED_Y = 20f;
    private static final float SHURIKEN_SPEED_Y_STEP = 25f;
    private static final int[] SHURIKEN_BURST_OFFSETS = new int[]{0, -1, 1};

    private final Shuriken tenguShuriken = new Shuriken();
    private int[] teleportInFrames;
    private int[] teleportOutFrames;
    private TeleportAnimationPhase teleportAnimationPhase = TeleportAnimationPhase.NONE;
    private float teleportAnimationFrame;
    private Unit teleportAvoidTarget;
    private int queuedShurikenShots;
    private int nextShurikenBurstIndex;
    private float nextShurikenShotAt;
    private AI committedAI;
    private Unit committedTarget;
    private boolean committedFriendly;
    private String teleportPerch;
    private boolean teleportLanded;

    {
        boss = true;
        hp = mhp = 120;
        experience = 20;
        attackSkill = 20;
        defenseSkill = 20;
        damageReduction = 5;
        gf = new GameFilm("images/units/tengu/tengu.png", 256, 16, 1f);
        gf.clipSizeY = 16;
        gf.clipSizeX = 14;
        idleFrames = new int[]{ 0, 1 };
        runFrames = new int[]{ 2, 3, 4, 5 };
        teleportInFrames = runFrames.clone();
        teleportOutFrames = new int[runFrames.length];
        for (int index = 0; index < runFrames.length; index++) {
            teleportOutFrames[index] = runFrames[runFrames.length - 1 - index];
        }
        attackFrames = new int[]{ 6, 7, 7 };
        dieFrames = new int[]{ 8, 9, 10, 10 };
        tenguShuriken.setOwner(this);
        ai = new AgressiveAI(this) {
            private float throwAt = 1f;
            private float jumpAt = TELEPORT_COOLDOWN_SECONDS;
            private boolean teleportPending;

            @Override
            public void act(float delta) {
                if (teleportPending && !isTeleportAnimating()) {
                    jumpAt = teleportLanded ? TELEPORT_COOLDOWN_SECONDS : 0f;
                    teleportPending = false;
                }
                throwAt -= delta;
                jumpAt -= delta;
                super.act(delta);
            }

            @Override
            public void attacked(float delta) {
                Unit target = getOther();
                if (target == null || target.getHP() < 1 || target.getRoom() == null || getOwner().getRoom() == null || !target.getRoom().equals(getOwner().getRoom())) {
                    super.attacked(delta);
                    return;
                }

                Tengu tengu = (Tengu) getOwner();
                if (tengu.isTeleportAnimating()) {
                    return;
                }

                if (tengu.isFiringShurikenBurst()) {
                    return;
                }

                tengu.movingLeft = false;
                tengu.movingRight = false;
                tengu.facingRight = tengu.x < target.x;

                if (!tengu.canAttack()) {
                    return;
                }

                boolean firingLane = tengu.hasFiringLane(tengu.x, tengu.y, target);
                if (!firingLane) {


                    jumpAt = Math.min(jumpAt, BLOCKED_LANE_REPOSITION_SECONDS);
                }

                if (jumpAt <= 0f) {
                    if (tengu.beginTeleport(target)) {
                        teleportPending = true;
                        return;
                    }
                }

                if (throwAt <= 0f && firingLane) {
                    tengu.beginShurikenBurst();
                    tengu.startAttackAnimation(0.8f);
                    throwAt = SHURIKEN_ATTACK_INTERVAL_SECONDS;
                }
            }
        };

        speedX = 0f;
        attackSpeed = 3.5f;
        weapon = new MeleeAttack().setDamageRange(8f, 15f);
        weapon.setOwner(this);
    }

    @Override
    public void act(float delta) {
        movingLeft = false;
        movingRight = false;
        if ((isTeleportAnimating() || isFiringShurikenBurst()) && !canContinueSpecial()) cancelSpecial();

        super.act(delta);
        if ((isTeleportAnimating() || isFiringShurikenBurst()) && !canContinueSpecial()) cancelSpecial();
        if (isTeleportAnimating()) {
            updateTeleportAnimation(delta);
            return;
        }
        updateShurikenBurst(delta);
    }

    private void rememberSpecial(Unit target) {
        committedAI = ai;
        committedTarget = target;
        committedFriendly = isFriendly;
    }

    private boolean canContinueSpecial() {
        if (isDead() || getHP() < 1 || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())
                || ai == null || ai != committedAI || isFriendly != committedFriendly
                || ai.isBlind() || !ai.canTarget(committedTarget)) return false;

        for (Buff buff : buffs) if (buff.preventsAttacks()) return false;
        return true;
    }

    private void cancelSpecial() {
        queuedShurikenShots = 0;
        nextShurikenBurstIndex = 0;
        nextShurikenShotAt = 0f;
        teleportAnimationPhase = TeleportAnimationPhase.NONE;
        teleportAnimationFrame = 0f;
        teleportAvoidTarget = committedTarget = null;
        teleportPerch = null;
        committedAI = null;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (!isTeleportAnimating()) {
            super.draw(batch, alpha);
            return;
        }

        if (gf == null || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        int[] teleportFrames = teleportAnimationPhase == TeleportAnimationPhase.OUT ? teleportOutFrames : teleportInFrames;
        if (teleportFrames == null || teleportFrames.length == 0) {
            super.draw(batch, alpha);
            return;
        }

        int frameIndex = Math.min(teleportFrames.length - 1, Math.max(0, (int) teleportAnimationFrame));
        gf.tileX = teleportFrames[frameIndex];
        gf.setAlpha(1f);
        gf.setPosition(x, y);
        gf.faceRight(facingRight);
        gf.draw(batch);
        drawHP(batch);
        drawBuffs(batch);
    }

    public void jump(Unit avoidTarget) {
        beginTeleport(avoidTarget);
    }

    public void teleportToPerch() {
        teleportToPerch(UnitHelper.getInstance().getHero());
    }

    public void teleportToPerch(Unit avoidTarget) {
        String platform = choosePerch(avoidTarget);
        if (platform != null) landOnPerch(platform, avoidTarget);
    }

    private String choosePerch(Unit avoidTarget) {
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) return null;

        ArrayList<String> candidates = new ArrayList<String>();
        ArrayList<String> fallback = new ArrayList<String>();
        float nearestHeight = Float.MAX_VALUE;
        for (String platform : currentRoom.getPlatforms()) {
            if (!canLandOnPerch(currentRoom, platform)) continue;
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float px = tileX * ConstantsHelper.TILE, py = (tileY + 1) * ConstantsHelper.TILE;
            if (avoidTarget != null && Math.abs(avoidTarget.x - px) < ConstantsHelper.TILE) continue;
            float heightGap = avoidTarget == null ? 0f : Math.abs(avoidTarget.y - py);
            if (heightGap < nearestHeight) {
                fallback.clear();
                nearestHeight = heightGap;
            }
            if (heightGap == nearestHeight) fallback.add(platform);

            if (avoidTarget == null || Math.abs(avoidTarget.x - px) < ConstantsHelper.TILE * 2f
                    || !hasFiringLane(px, py, avoidTarget)) {
                continue;
            }

            candidates.add(platform);
        }

        if (candidates.isEmpty()) {
            candidates.addAll(fallback);
        }
        if (candidates.isEmpty()) return null;
        Collections.sort(candidates);
        return candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
    }

    private boolean hasFiringLane(float fromX, float fromY, Unit target) {
        if (target == null || Math.abs(target.y - fromY) > ConstantsHelper.UNIT_DIMENSIONS
                || Math.abs(target.x - fromX) > ConstantsHelper.TILE * 4f) return false;
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        return currentRoom != null && MapHelper.getInstance().hasPlatformLineOfSight(currentRoom,
                fromX + ConstantsHelper.UNIT_DIMENSIONS / 2f, fromY + ConstantsHelper.UNIT_DIMENSIONS / 3f,
                target.x + ConstantsHelper.UNIT_DIMENSIONS / 2f, target.y + ConstantsHelper.UNIT_DIMENSIONS / 2f);
    }

    private boolean canLandOnPerch(Room currentRoom, String platform) {
        if (currentRoom == null || platform == null || !currentRoom.getPlatforms().contains(platform)) return false;
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int tileY = Integer.parseInt(platform.split("_")[1]);
        float px = tileX * ConstantsHelper.TILE, py = (tileY + 1) * ConstantsHelper.TILE;
        return px >= 1f && px + ConstantsHelper.UNIT_DIMENSIONS < currentRoom.getWidth() * ConstantsHelper.TILE
                && py + ConstantsHelper.UNIT_DIMENSIONS < currentRoom.getHeight() * ConstantsHelper.TILE
                && !(Math.abs(px - x) < 1f && Math.abs(py - y) < 8f)
                && !currentRoom.getPlatforms().contains(tileX + "_" + (tileY + 1))
                && Math.abs(MapHelper.getInstance().calculateFloorY(px, py) - py) <= 1f
                && UnitHelper.getInstance().freeSpace(this, (int) px, (int) py, room);
    }

    private void landOnPerch(String platform, Unit avoidTarget) {
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int tileY = Integer.parseInt(platform.split("_")[1]);

        x = tileX * ConstantsHelper.TILE;
        y = (tileY + 1) * ConstantsHelper.TILE;
        floorY = y;
        speedY = 0f;
        momentX = 0f;
        facingRight = avoidTarget == null || x < avoidTarget.x;
        PhysicsHelper.getInstance().syncBodyToUnit(this);
    }

    private void armDormantTraps(int maxTraps) {
        ArrayList<PlatformTrap> dormantTraps = new ArrayList<PlatformTrap>();
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof PlatformTrap) || unit.getRoom() == null || !unit.getRoom().equals(room)) {
                continue;
            }

            PlatformTrap trap = (PlatformTrap) unit;
            if (trap.isTriggered() || !trap.isHidden()) {
                continue;
            }

            dormantTraps.add(trap);
        }

        for (int armed = 0; armed < maxTraps && !dormantTraps.isEmpty(); armed++) {
            int index = RandomHelper.getInstance().randomInt(dormantTraps.size());
            dormantTraps.remove(index).setHidden(false);
        }
    }

    private void beginShurikenBurst() {
        rememberSpecial(ai.getOther());
        queuedShurikenShots = SHURIKEN_BURST_OFFSETS.length;
        nextShurikenBurstIndex = 0;
        nextShurikenShotAt = SHURIKEN_BURST_WINDUP_SECONDS;
    }

    private void updateShurikenBurst(float delta) {
        if (!isFiringShurikenBurst()) {
            return;
        }

        nextShurikenShotAt -= delta;
        while (queuedShurikenShots > 0 && nextShurikenShotAt <= 0f) {
            fireShurikenShot(SHURIKEN_BURST_OFFSETS[nextShurikenBurstIndex]);
            queuedShurikenShots--;
            nextShurikenBurstIndex++;
            nextShurikenShotAt += SHURIKEN_BURST_SHOT_INTERVAL_SECONDS;
        }
    }

    private void fireShurikenShot(int offset) {
        ThrownProjectile shuriken = new ShurikenProjectile().toThrownProjectile()
                .setOwner(this)
                .setAttackingItem(tenguShuriken)
                .setDamageRange(8f, 15f);
        shuriken.setRoom(room);
        shuriken.isFriendly = isFriendly;
        shuriken.facingRight = facingRight;
        shuriken.x = x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        shuriken.y = y + ConstantsHelper.UNIT_DIMENSIONS / 3f + offset * 8f;
        shuriken.setSpeedX((facingRight ? SHURIKEN_SPEED_X : -SHURIKEN_SPEED_X) + offset * 70f);
        shuriken.setSpeedY(SHURIKEN_BASE_SPEED_Y + offset * SHURIKEN_SPEED_Y_STEP);
        UnitHelper.getInstance().addUnit(shuriken);
    }

    private boolean isFiringShurikenBurst() {
        return queuedShurikenShots > 0;
    }

    private boolean isTeleportAnimating() {
        return teleportAnimationPhase != TeleportAnimationPhase.NONE;
    }

    private boolean beginTeleport(Unit avoidTarget) {
        if (isTeleportAnimating()) return false;
        teleportPerch = choosePerch(avoidTarget);
        if (teleportPerch == null) return false;
        rememberSpecial(avoidTarget);
        teleportLanded = false;

        if (teleportInFrames == null || teleportInFrames.length == 0) {
            armDormantTraps(4);
            EffectsHelper.getInstance().blackSpark(this);
            landOnPerch(teleportPerch, avoidTarget);
            teleportLanded = true;
            EffectsHelper.getInstance().blackSpark(this);
            return true;
        }

        teleportAvoidTarget = avoidTarget;
        teleportAnimationFrame = 0f;
        teleportAnimationPhase = TeleportAnimationPhase.OUT;
        return true;
    }

    private void updateTeleportAnimation(float delta) {
        int[] teleportFrames = teleportAnimationPhase == TeleportAnimationPhase.OUT ? teleportOutFrames : teleportInFrames;
        if (teleportFrames == null || teleportFrames.length == 0) {
            teleportAnimationPhase = TeleportAnimationPhase.NONE;
            teleportAnimationFrame = 0f;
            return;
        }

        teleportAnimationFrame += delta * TELEPORT_ANIMATION_FRAME_RATE;
        if (teleportAnimationFrame < teleportFrames.length) {
            return;
        }

        if (teleportAnimationPhase == TeleportAnimationPhase.OUT) {
            Room currentRoom = MapHelper.getInstance().getRoom(room);
            if (!canLandOnPerch(currentRoom, teleportPerch)) teleportPerch = choosePerch(teleportAvoidTarget);
            if (teleportPerch == null) {
                cancelSpecial();
                return;
            }
            armDormantTraps(4);
            EffectsHelper.getInstance().blackSpark(this);
            landOnPerch(teleportPerch, teleportAvoidTarget);
            teleportLanded = true;
            EffectsHelper.getInstance().blackSpark(this);
            teleportAnimationPhase = TeleportAnimationPhase.IN;
            teleportAnimationFrame = 0f;
            teleportAvoidTarget = null;
            return;
        }

        teleportAnimationPhase = TeleportAnimationPhase.NONE;
        teleportAnimationFrame = 0f;
    }

    @Override
    public void die() {
        cancelSpecial();
        new TomeOfMastery().drop(x, y, room);
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
        return "A prison assassin boss who keeps leaping between perches, arming fresh poison traps, and filling the arena with shuriken pressure.";
    }

    private enum TeleportAnimationPhase {
        NONE,
        OUT,
        IN
    }
}
