package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Tengu;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

public class TenguRoom extends Room {
    private boolean spawned;

    {
        canSpawn = false;
        width = 22;
        height = 12;
    }

    public TenguRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        platforms.clear();
        waterPlatforms.clear();

        addPlatformSpan(2, 19, 2);
        addPlatformSpan(3, 6, 3);
        addPlatformSpan(4, 7, 4);
        addPlatformSpan(5, 8, 5);
        addPlatformSpan(15, 18, 3);
        addPlatformSpan(14, 17, 4);
        addPlatformSpan(13, 16, 5);
        addPlatformSpan(8, 13, 6);

        addTrap(6, 3, TrapType.POISON, false);
        addTrap(15, 3, TrapType.PARALYTIC, false);
        addTrap(10, 3, TrapType.POISON, false);

        addTrap(4, 3, TrapType.POISON, true);
        addTrap(8, 4, TrapType.POISON, true);
        addTrap(14, 4, TrapType.POISON, true);
        addTrap(17, 3, TrapType.POISON, true);
        addTrap(6, 5, TrapType.POISON, true);
        addTrap(15, 5, TrapType.POISON, true);
        addTrap(9, 7, TrapType.POISON, true);
        addTrap(12, 7, TrapType.POISON, true);

        return this;
    }

    @Override
    public Door getRandomDoor() {
        Door door = new Door();
        if (doors.isEmpty()) {
            door.x = 3 * ConstantsHelper.TILE;
            door.y = 3 * ConstantsHelper.TILE;
        } else {
            door.x = 18 * ConstantsHelper.TILE;
            door.y = 3 * ConstantsHelper.TILE;
        }
        return door;
    }

    private void addTrap(int tileX, int floorTileY, TrapType trapType, boolean hidden) {
        PlatformTrap trap = new PlatformTrap().setTrapType(trapType).setHidden(hidden);
        trap.x = tileX * ConstantsHelper.TILE + (ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        trap.y = floorTileY * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS / 3f + ConstantsHelper.UNIT_DIMENSIONS * 0.25f;
        trap.floorY = floorTileY * ConstantsHelper.TILE;
        trap.setRoom(identifier);
        UnitHelper.getInstance().addUnit(trap);
    }

    @Override
    public void entered() {
        if (bossDefeated) {
            return;
        }

        for (Door door : doors) {
            if (door.getLeadsTo() != null && door.getLeadsTo().equals(identifier)) {
                continue;
            }

            if (MapHelper.getInstance().getRoom(door.getLeadsTo()) instanceof ExitRoom) {
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
        Tengu tengu = new Tengu();
        tengu.setRoom(identifier);
        tengu.teleportToPerch();
        UnitHelper.getInstance().addUnit(tengu);
    }
}