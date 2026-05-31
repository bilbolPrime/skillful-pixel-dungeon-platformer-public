package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Wraith extends Mob {
    private static final int[] SPAWN_TILE_OFFSETS = new int[]{1, -1, 2, -2, 3, -3};

    {
        hp = mhp = 1;
        experience = 0;
        canFly = true;
        gf = new GameFilm("images/units/ghost/wraith.png", 112, 15, 1f);
        gf.clipSizeX = 14;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        runFrames = new int[]{0, 1};
        attackFrames = new int[]{0, 2, 3};
        dieFrames = new int[]{0, 4, 5, 6, 7};
        ai = new AgressiveAI(this);
        speedX = 390f;
        attackSpeed = 5.5f;
        weapon = (MeleeAttack) new MeleeAttack().setOwner(this);
        adjustStats(MapHelper.getInstance().getDepth());
    }

    public void adjustStats(int depth) {
        int effectiveDepth = Math.max(1, depth);
        attackSkill = 10 + effectiveDepth;
        defenseSkill = attackSkill * 5;
        damageReduction = 0;
        ((MeleeAttack) weapon).setDamageRange(1f, 3f + effectiveDepth);
    }

    public static int spawnNear(String roomId, float originX, float floorY, int count) {
        Room room = MapHelper.getInstance().getRoom(roomId);
        if (room == null || count < 1) {
            return 0;
        }

        int spawned = 0;
        for (int spawnIndex = 0; spawnIndex < count; spawnIndex++) {
            float spawnX = Float.NaN;
            float spawnY = floorY;

            for (int offset : SPAWN_TILE_OFFSETS) {
                float candidateX = originX + offset * ConstantsHelper.TILE;
                if (!room.spotAvailable(candidateX, floorY)) {
                    continue;
                }

                if (!UnitHelper.getInstance().freeSpace((int) candidateX, (int) floorY, roomId)) {
                    continue;
                }

                spawnX = candidateX;
                break;
            }

            if (Float.isNaN(spawnX)) {
                com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door spawn = room.getRandomSpawn();
                if (spawn == null || !UnitHelper.getInstance().freeSpace((int) spawn.x, (int) spawn.y, roomId)) {
                    continue;
                }

                spawnX = spawn.x;
                spawnY = spawn.y;
            }

            Wraith wraith = new Wraith();
            wraith.adjustStats(MapHelper.getInstance().getDepth());
            wraith.setRoom(roomId);
            wraith.x = spawnX;
            wraith.y = spawnY;
            wraith.floorY = spawnY;
            wraith.facingRight = spawnX < originX;
            UnitHelper.getInstance().addUnit(wraith);
            wraith.changeState(UnitState.IDLE, true);

            Hero hero = UnitHelper.getInstance().getHero();
            if (hero != null) {
                wraith.makeHostile();
                wraith.beginHunting(hero);
            }
            else {
                wraith.wakeToWandering();
            }

            spawned++;
        }

        return spawned;
    }

    @Override
    public String getLibraryDescription() {
        return "A vengeful spirit called forth when a grave or tomb is disturbed. Its ethereal body is notoriously hard to hit with ordinary weapons.";
    }
}