package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;

public class DwarfKing extends Mob {
    private static final int MAX_ARMY_SIZE = 5;
    private static final float PEDESTAL_Y = 6f;
    private static final int[] PEDESTAL_X_TILES = new int[]{8, 21};

    private static final float SUMMON_INTERVAL = 4f;
    private static final float WAVE_TWO_HEALTH_THRESHOLD = 2f / 3f;
    private static final float WAVE_THREE_HEALTH_THRESHOLD = 1f / 3f;
    private static final String WAVE_1_MESSAGE = "Enough! Arise my slaves!";
    private static final String WAVE_2_MESSAGE = "More! Bleed for your king!";
    private static final String WAVE_3_MESSAGE = "Useless! KILL THEM NOW!";

    private float summonAt = 0.75f;
    private int pedestalIndex;
    private int announcedWave;

    {
        boss = true;
        hp = mhp = 300;

        regenerationRate = 0.25f;
        experience = 40;
        attackSkill = 32;
        defenseSkill = 25;
        damageReduction = 14;
        gf = new GameFilm("images/units/king/king.png", 256, 16, 1f);
        gf.clipSizeX = 16;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{2, 3, 4, 5, 6, 7, 8};
        attackFrames = new int[]{9, 10, 11};
        dieFrames = new int[]{12, 13, 14, 15, 15, 15};
        ai = new AgressiveAI(this);

        speedX = 260;
        attackSpeed = 3.5f;
        weapon = new MeleeAttack().setDamageRange(20f, 38f);
        weapon.setOwner(this);
    }

    @Override
    public void act(float delta) {
        summonAt -= delta;

        if (isDead()) {
            super.act(delta);
            return;
        }

        if (room != null && room.equals(MapHelper.getInstance().getActiveRoomIdentifier())
                && canAttack() && ai != null && !ai.isBlind() && ai.canTarget(ai.getOther())) {
            if (countArmy() >= getArmyCap()) {


                summonAt = SUMMON_INTERVAL;
            } else if (summonAt <= 0f) {
                if (moveToPedestal()) {
                    if (summonUndead()) {
                        fakeAttack();
                        pedestalIndex = 1 - pedestalIndex;
                    }
                    summonAt = SUMMON_INTERVAL;
                    movingLeft = false;
                    movingRight = false;
                    return;
                }
            }
        }

        super.act(delta);
    }

    private int getArmyCap() {
        return 1 + (MAX_ARMY_SIZE * (mhp - hp) / Math.max(1, mhp));
    }

    private int countArmy() {
        int count = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (!(unit instanceof DwarvenUndead) || unit.isDead() || unit.getRoom() == null || !unit.getRoom().equals(room)) {
                continue;
            }

            count++;
        }
        return count;
    }

    private boolean moveToPedestal() {
        float targetY = PEDESTAL_Y * ConstantsHelper.TILE;
        for (int attempt = 0; attempt < PEDESTAL_X_TILES.length; attempt++) {
            int candidateIndex = (pedestalIndex + attempt) % PEDESTAL_X_TILES.length;
            float targetX = PEDESTAL_X_TILES[candidateIndex] * ConstantsHelper.TILE;
            if (Math.abs(MapHelper.getInstance().calculateFloorY(targetX, targetY) - targetY) > 1f
                    || !UnitHelper.getInstance().freeSpace(this, Math.round(targetX), Math.round(targetY), room)
                    || !hasPlacementSpace(this, targetX, targetY)) {
                continue;
            }


            pedestalIndex = candidateIndex;
            x = targetX;
            y = targetY;
            floorY = y;
            speedY = 0f;
            momentX = 0f;
            if (ai != null && ai.getOther() != null) facingRight = x < ai.getOther().x;
            PhysicsHelper.getInstance().syncBodyToUnit(this);
            if (ai != null) {


                Unit target = ai.getOther();
                ai.clearTarget();
                if (ai.canTarget(target)) ai.setOther(target);
            }
            return true;
        }
        return false;
    }

    private boolean summonUndead() {
        int summonIndex = 1 - pedestalIndex;
        int spawnTileX = PEDESTAL_X_TILES[summonIndex];
        float spawnY = PEDESTAL_Y * ConstantsHelper.TILE;
        int[] offsets = new int[]{0, -1, 1, -2, 2};
        DwarvenUndead undead = new DwarvenUndead();
        for (int offset : offsets) {
            float spawnX = (spawnTileX + offset) * ConstantsHelper.TILE;
            if (Math.abs(MapHelper.getInstance().calculateFloorY(spawnX, spawnY) - spawnY) > 1f
                    || !UnitHelper.getInstance().freeSpace((int) spawnX, (int) spawnY, room)
                    || !hasPlacementSpace(undead, spawnX, spawnY)) {
                continue;
            }

            undead.x = spawnX;
            undead.y = spawnY;
            undead.floorY = spawnY;
            undead.setRoom(room);
            undead.facingRight = spawnX < x;
            UnitHelper.getInstance().addUnit(undead);
            announceWaveIfNeeded();
            return true;
        }

        return false;
    }

    private boolean hasPlacementSpace(Unit actor, float targetX, float targetY) {
        Rectangle placement = actor.getHitBoxAt(targetX, targetY);
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == actor || unit.isDead() || unit.showOnly() || !room.equals(unit.getRoom())
                    || !(unit.isHero || unit instanceof Mob)) {
                continue;
            }
            if (placement.overlaps(unit.getHitBox())) {
                return false;
            }
        }
        return true;
    }

    private void announceWaveIfNeeded() {
        int wave = getCurrentWave();
        if (wave <= announcedWave) {
            return;
        }

        announcedWave = wave;
        EffectsHelper.getInstance().message(this, getWaveMessage(wave), Color.RED, 0f);
        SoundHelper.GetSingleton().play(Sounds.CHALLENGE, 0f, 1f);
    }

    private int getCurrentWave() {
        if (hp <= Math.round(mhp * WAVE_THREE_HEALTH_THRESHOLD)) {
            return 3;
        }

        if (hp <= Math.round(mhp * WAVE_TWO_HEALTH_THRESHOLD)) {
            return 2;
        }

        return 1;
    }

    private String getWaveMessage(int wave) {
        switch (wave) {
            case 2:
                return WAVE_2_MESSAGE;
            case 3:
                return WAVE_3_MESSAGE;
            case 1:
            default:
                return WAVE_1_MESSAGE;
        }
    }

    public int getNextPedestalIndex() {
        return pedestalIndex;
    }

    public int getAnnouncedWave() {
        return announcedWave;
    }

    public float getSummonDelay() {
        return Math.max(0f, summonAt);
    }

    public void restoreSummonState(Integer nextPedestal, Integer wave, Float delay) {
        pedestalIndex = nextPedestal != null && nextPedestal >= 0
                && nextPedestal < PEDESTAL_X_TILES.length ? nextPedestal : 0;


        announcedWave = wave == null ? (hp < mhp ? getCurrentWave() : 0)
                : Math.max(0, Math.min(3, wave));
        summonAt = delay != null && Float.isFinite(delay)
                ? Math.max(0f, Math.min(SUMMON_INTERVAL, delay)) : 0.75f;
    }

    @Override
    public void die() {
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
        return "A summoner king who alternates between his hall pedestals and buries the fight under fresh undead reinforcements.";
    }
}
