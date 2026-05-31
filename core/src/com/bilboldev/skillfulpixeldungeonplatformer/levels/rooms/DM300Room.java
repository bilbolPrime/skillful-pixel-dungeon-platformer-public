package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.DM300;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

public class DM300Room extends Room {
    private boolean spawned;

    {
        canSpawn = false;
        width = 28;
        height = 12;
    }

    public DM300Room(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        platforms.clear();
        waterPlatforms.clear();

        addPlatformSpan(1, 26, 2);
        addPlatformSpan(4, 8, 3);
        addPlatformSpan(20, 24, 3);
        addPlatformSpan(7, 11, 4);
        addPlatformSpan(17, 21, 4);
        addPlatformSpan(11, 16, 5);

        addTrap(10, 3, TrapType.TOXIC);
        addTrap(12, 3, TrapType.POISON);
        addTrap(15, 3, TrapType.POISON);
        addTrap(17, 3, TrapType.TOXIC);
        addTrap(9, 5, TrapType.TOXIC);
        addTrap(19, 5, TrapType.TOXIC);
        return this;
    }

    private void addTrap(int tileX, int floorTileY, TrapType trapType) {
        PlatformTrap trap = new PlatformTrap().setTrapType(trapType).setHidden(false);
        trap.x = tileX * ConstantsHelper.TILE + (ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        trap.y = floorTileY * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS / 3f + ConstantsHelper.UNIT_DIMENSIONS * 0.25f;
        trap.floorY = floorTileY * ConstantsHelper.TILE;
        trap.setRoom(identifier);
        UnitHelper.getInstance().addUnit(trap);
    }

    @Override
    public Door getRandomDoor() {
        Door door = new Door();
        if (doors.isEmpty()) {
            door.x = 4 * ConstantsHelper.TILE;
            door.y = 3 * ConstantsHelper.TILE;
        } else {
            door.x = 23 * ConstantsHelper.TILE;
            door.y = 3 * ConstantsHelper.TILE;
        }
        return door;
    }

    @Override
    public void entered() {
        if (bossDefeated) {
            return;
        }

        for (Door door : doors) {
            if(MapHelper.getInstance().getRoom(door.getLeadsTo()) instanceof ExitRoom){
                door.cageUp();
            }
            else {
                door.lock();
            }
        }

        if (bossEncounterStarted) {
            return;
        }

        spawned = true;
        markBossEncounterStarted();
        DM300 boss = new DM300();
        boss.x = 22 * ConstantsHelper.TILE;
        boss.y = 3 * ConstantsHelper.TILE;
        boss.floorY = boss.y;
        boss.setRoom(identifier);
        UnitHelper.getInstance().addUnit(boss);
    }
}