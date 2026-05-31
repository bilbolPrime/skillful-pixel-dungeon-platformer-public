package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing;

public class KingRoom extends Room {
    private boolean spawned;

    {
        canSpawn = false;
        width = 30;
        height = 14;
    }

    public KingRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        platforms.clear();
        waterPlatforms.clear();

        addPlatformSpan(2, 27, 2);
        addPlatformSpan(4, 25, 3);
        addPlatformSpan(5, 9, 4);
        addPlatformSpan(20, 24, 4);
        addPlatformSpan(6, 10, 5);
        addPlatformSpan(19, 23, 5);
        addPlatformSpan(13, 16, 4);
        addPlatformSpan(13, 16, 5);

        return this;
    }

    @Override
    public Door getRandomDoor() {
        Door door = new Door();
        if (doors.isEmpty()) {
            door.x = 14 * ConstantsHelper.TILE;
            door.y = 3 * ConstantsHelper.TILE;
        } else {
            door.x = 15 * ConstantsHelper.TILE;
            door.y = 6 * ConstantsHelper.TILE;
        }
        return door;
    }

    @Override
    public void entered() {
        if (bossDefeated) {
            return;
        }

        for (Door door : doors) {
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
        DwarfKing boss = new DwarfKing();
        boss.x = 8 * ConstantsHelper.TILE;
        boss.y = 6 * ConstantsHelper.TILE;
        boss.floorY = boss.y;
        boss.setRoom(identifier);
        UnitHelper.getInstance().addUnit(boss);
    }
}