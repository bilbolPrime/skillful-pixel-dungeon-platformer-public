package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

public class DM300 extends Mob {
    private static final float CHARGE_INTERVAL = 2.75f;
    private static final float WINDUP_DURATION = 0.75f;
    private static final float CHARGE_DURATION = 1.2f;
    private static final float RECOVERY_DURATION = 3f;
    private static final float CHARGE_SPEED_MULTIPLIER = 3f;
    private static final float IMPACT_DAMAGE_MULTIPLIER = 3f;
    private static final float HERO_THROW_FORCE = 950f;
    private static final float SMOKE_INTERVAL = 0.22f;
    private static final float WINDUP_FLICKER_INTERVAL = 0.08f;

    private ChargeState chargeState = ChargeState.IDLE;
    private float chargeStateTimer;
    private float chargeAt = 1.25f;
    private float smokeAt;
    private float flickerAt;
    private boolean drawVisible = true;
    private int chargeDirection = 1;
    private AI chargeAI;
    private Unit chargeTarget;
    private boolean chargeFriendly;
    private Unit ascentTarget;
    private float ascentX = Float.NaN;
    private float ascentY;

    {
        boss = true;
        hp = mhp = 200;
        experience = 30;
        attackSkill = 28;
        defenseSkill = 18;
        damageReduction = 10;
        gf = new GameFilm("images/units/dm300/dm300.png", 256, 32, 1f);
        gf.clipSizeY = 20;
        gf.clipSizeX = 22;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4};
        attackFrames = new int[]{5, 6, 7, 7};
        dieFrames = new int[]{8, 8, 9, 9, 9};
        ai = new AgressiveAI(this) {
            @Override
            public void act(float delta) {
                if (((DM300) getOwner()).chargeState != ChargeState.IDLE) {
                    return;
                }

                super.act(delta);
            }

            @Override
            public void attacked(float delta) {
                DM300 dm300 = (DM300) getOwner();
                if (dm300.chargeState != ChargeState.IDLE) {
                    return;
                }

                Unit target = getOther();
                if (target == null || target.isDead() || target.getRoom() == null || !target.getRoom().equals(dm300.room)) {
                    super.attacked(delta);
                    return;
                }

                dm300.facingRight = dm300.x < target.x;
                if (dm300.canAttack() && dm300.chargeAt <= 0f && dm300.canStartChargeAt(target)) {
                    dm300.startCharge(target);
                    dm300.fakeAttack();
                    dm300.chargeAt = CHARGE_INTERVAL + RandomHelper.getInstance().randomFloat(0.75f);
                    return;
                }

                if (dm300.canAttack() && dm300.pursueRaisedTarget(target)) {
                    return;
                }
                super.attacked(delta);
            }
        };

        speedX = 250;
        attackSpeed = 3.25f;
        weapon = new MeleeAttack().setDamageRange(18f, 24f);
        weapon.setOwner(this);
    }

    @Override
    public void act(float delta) {
        if (chargeState != ChargeState.IDLE && !canContinueCharge()) cancelCharge();
        if (chargeState == ChargeState.IDLE) {
            chargeAt -= delta;
        } else {
            advanceChargeState(delta);
        }

        super.act(delta);
    }

    private boolean canStartChargeAt(Unit target) {
        return PhysicsHelper.getInstance().isGrounded(this)
                && Math.abs(target.floorY - floorY) <= ConstantsHelper.TILE * 0.5f
                && hasChargeSupportAt(x + (target.x >= x ? 1f : -1f) * ConstantsHelper.TILE * 0.25f);
    }


    private boolean pursueRaisedTarget(Unit target) {
        boolean grounded = PhysicsHelper.getInstance().isGrounded(this);
        if (target != ascentTarget || grounded && target.y <= y + ConstantsHelper.TILE * 0.5f) {
            ascentX = Float.NaN;
            ascentTarget = target;
        }
        if (grounded && target.y <= y + ConstantsHelper.TILE * 0.5f) return false;

        if (grounded && !Float.isNaN(ascentX) && y >= ascentY - 8f) ascentX = Float.NaN;
        if (Float.isNaN(ascentX)) {
            if (!grounded) return false;
            Room currentRoom = MapHelper.getInstance().getRoom(room);
            if (currentRoom == null) return false;
            float best = Float.MAX_VALUE;
            for (String platform : currentRoom.getPlatforms()) {
                String[] tile = platform.split("_");
                float px = Integer.parseInt(tile[0]) * ConstantsHelper.TILE;
                float py = (Integer.parseInt(tile[1]) + 1) * ConstantsHelper.TILE;
                if (py <= y + ConstantsHelper.TILE * 0.5f || py > y + ConstantsHelper.TILE + 8f
                        || py > target.floorY + 8f || px < 1f
                        || px + ConstantsHelper.UNIT_DIMENSIONS >= currentRoom.getWidth() * ConstantsHelper.TILE
                        || Math.abs(MapHelper.getInstance().calculateFloorY(px, py) - py) > 1f
                        || !UnitHelper.getInstance().freeSpace(this, (int) px, (int) py, room)) continue;
                float score = Math.abs(px - x) + Math.abs(px - target.x) * 0.25f;
                if (score < best || score == best && px < ascentX) {
                    best = score;
                    ascentX = px;
                    ascentY = py;
                }
            }
            if (Float.isNaN(ascentX)) return false;
        }
        movingLeft = x > ascentX + 12f;
        movingRight = x < ascentX - 12f;
        if (movingLeft || movingRight) facingRight = movingRight;
        if (grounded && Math.abs(x - ascentX) < ConstantsHelper.UNIT_DIMENSIONS) jump();
        return true;
    }

    private boolean hasChargeSupportAt(float nextX) {
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        return currentRoom != null && nextX >= 0f
                && nextX <= currentRoom.getWidth() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS
                && Math.abs(MapHelper.getInstance().calculateFloorY(nextX, y) - y) <= 8f;
    }

    private void startCharge(Unit target) {
        ascentTarget = null;
        ascentX = Float.NaN;
        chargeAI = ai;
        chargeTarget = target;
        chargeFriendly = isFriendly;
        chargeDirection = target.x >= x ? 1 : -1;
        chargeState = ChargeState.WINDUP;
        chargeStateTimer = WINDUP_DURATION;
        flickerAt = WINDUP_FLICKER_INTERVAL;
        drawVisible = true;
        movingLeft = false;
        movingRight = false;
        momentX = 0f;
    }

    private boolean canContinueCharge() {
        if (isDead() || getHP() < 1 || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())
                || ai == null || ai != chargeAI || isFriendly != chargeFriendly
                || ai.isBlind() || !ai.canTarget(chargeTarget)) return false;
        for (Buff buff : buffs) if (buff.preventsAttacks()) return false;
        return true;
    }

    private void cancelCharge() {
        chargeState = ChargeState.IDLE;
        chargeStateTimer = 0f;
        chargeAI = null;
        chargeTarget = null;
        movingLeft = movingRight = false;
        drawVisible = true;
    }

    private void advanceChargeState(float delta) {
        switch (chargeState) {
            case WINDUP:
                advanceWindup(delta);
                break;
            case CHARGING:
                advanceCharge(delta);
                break;
            case RECOVERING:
                advanceRecovery(delta);
                break;
            default:
                break;
        }
    }

    private void advanceWindup(float delta) {
        movingLeft = false;
        movingRight = false;
        momentX = 0f;
        chargeStateTimer -= delta;
        flickerAt -= delta;
        if (flickerAt <= 0f) {
            flickerAt = WINDUP_FLICKER_INTERVAL;
            drawVisible = !drawVisible;
        }

        if (chargeStateTimer <= 0f) {
            chargeState = ChargeState.CHARGING;
            chargeStateTimer = CHARGE_DURATION;
            drawVisible = true;
            movingLeft = chargeDirection < 0;
            movingRight = chargeDirection > 0;
        }
    }

    private void advanceCharge(float delta) {
        float nextX = x + chargeDirection * speedX * CHARGE_SPEED_MULTIPLIER * delta;




        float supportedEndX = nextX + chargeDirection
                * (getSpeedX() * delta + ConstantsHelper.TILE * 0.25f);
        if (!hasChargeSupportAt(nextX) || !hasChargeSupportAt(supportedEndX)) {
            beginRecovery();
            return;
        }
        Hero hero = getHeroInLane(nextX);
        if (hero != null) {
            x = nextX;
            PhysicsHelper.getInstance().syncBodyToUnit(this);
            ramHero(hero);
            beginRecovery();
            return;
        }

        if (!canChargeTo(nextX)) {
            beginRecovery();
            return;
        }

        x = nextX;
        PhysicsHelper.getInstance().syncBodyToUnit(this);
        chargeStateTimer -= delta;
        if (chargeStateTimer <= 0f) {
            beginRecovery();
        }
    }

    private void beginRecovery() {
        chargeState = ChargeState.RECOVERING;
        chargeStateTimer = RECOVERY_DURATION;
        smokeAt = 0f;
        movingLeft = false;
        movingRight = false;
        momentX = 0f;
        emitSmoke();
        seedRecoveryHazards();
    }

    private void advanceRecovery(float delta) {
        movingLeft = false;
        movingRight = false;
        momentX = 0f;
        chargeStateTimer -= delta;
        smokeAt -= delta;
        if (smokeAt <= 0f) {
            smokeAt = SMOKE_INTERVAL;
            emitSmoke();
        }

        if (chargeStateTimer <= 0f) {
            chargeState = ChargeState.IDLE;
            drawVisible = true;
        }
    }

    private boolean canChargeTo(float nextX) {
        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return false;
        }

        float minX = 0f;
        float maxX = currentRoom.getWidth() * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS;
        return nextX >= minX
                && nextX <= maxX
                && UnitHelper.getInstance().freeSpace(this, Math.round(nextX), Math.round(y), room);
    }

    private Hero getHeroInLane(float nextX) {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.isDead() || hero.getRoom() == null || !hero.getRoom().equals(room)) {
            return null;
        }

        Rectangle sweptHitBox = new Rectangle(getHitBox());
        sweptHitBox.merge(getHitBoxAt(nextX, y));
        return sweptHitBox.overlaps(hero.getHitBox()) ? hero : null;
    }

    private void ramHero(Hero hero) {
        hero.takeDamage(this, weapon, DifficultyHelper.getInstance().scaleEnemyDamage(this, weapon.getDamage() * IMPACT_DAMAGE_MULTIPLIER));
        hero.momentX += chargeDirection * HERO_THROW_FORCE;
        EffectsHelper.getInstance().spark(this);
        EffectsHelper.getInstance().spark(hero);
    }

    private void emitSmoke() {
        EffectsHelper.getInstance().add(new TrapBurst().init(
                x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/grey.png",
                12f,
                6,
                28f,
                34f,
                42f,
                -0.012f));
        EffectsHelper.getInstance().add(new TrapBurst().init(
                x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/black-particle.png",
                9f,
                4,
                18f,
                26f,
                36f,
                -0.01f));
    }

    private void seedRecoveryHazards() {
        seedRecoveryHazard(0, TrapType.TOXIC);
        seedRecoveryHazard(-chargeDirection, TrapType.POISON);
    }

    private void seedRecoveryHazard(int tileOffset, TrapType trapType) {
        if (room == null) {
            return;
        }

        Room currentRoom = MapHelper.getInstance().getRoom(room);
        if (currentRoom == null) {
            return;
        }

        int tileX = Math.round((x + tileOffset * ConstantsHelper.TILE) / ConstantsHelper.TILE);
        int floorTileY = Math.round(floorY / ConstantsHelper.TILE);
        if (tileX < 0 || !currentRoom.getPlatforms().contains(UtilsHelper.platformKey(tileX, floorTileY - 1)) || hasTrapAt(tileX, floorTileY)) {
            return;
        }

        PlatformTrap trap = new PlatformTrap().setTrapType(trapType).setHidden(false);
        trap.x = tileX * ConstantsHelper.TILE + (ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        trap.y = floorY - ConstantsHelper.UNIT_DIMENSIONS / 3f + ConstantsHelper.UNIT_DIMENSIONS * 0.25f;
        trap.floorY = floorY;
        trap.setRoom(room);
        UnitHelper.getInstance().addUnit(trap);
    }

    private boolean hasTrapAt(int tileX, int floorTileY) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof PlatformTrap) || unit.getRoom() == null || !unit.getRoom().equals(room)) {
                continue;
            }

            if ((int) (unit.x / ConstantsHelper.TILE) == tileX && (int) (unit.floorY / ConstantsHelper.TILE) == floorTileY) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (chargeState == ChargeState.WINDUP && !drawVisible) {
            return;
        }

        super.draw(batch, alpha);
    }

    @Override
    public void die() {
        cancelCharge();
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
        return "A brutal mining automaton that locks on, flickers in place, then rockets straight through its target before venting smoke to cool down.";
    }

    private enum ChargeState {
        IDLE,
        WINDUP,
        CHARGING,
        RECOVERING
    }
}
