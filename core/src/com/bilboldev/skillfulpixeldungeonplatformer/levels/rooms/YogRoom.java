package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.YogDzewa;

public class YogRoom extends Room {
    private boolean spawned;

    {
        canSpawn = false;
        width = 30;
        height = 14;
    }

    public YogRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        platforms.clear();
        waterPlatforms.clear();

        addPlatformSpan(4, 25, 2);
        addPlatformSpan(6, 11, 3);
        addPlatformSpan(18, 23, 3);
        addPlatformSpan(8, 21, 4);
        addPlatformSpan(9, 19, 5);
        addWaterSpan(4, 7, 2);
        addWaterSpan(22, 25, 2);

        return this;
    }

    @Override
    public Door getRandomDoor() {
        Door door = new Door();
        if (doors.isEmpty()) {
            door.x = 14 * ConstantsHelper.TILE;
            door.y = 3 * ConstantsHelper.TILE;
        } else {
            door.x = 18 * ConstantsHelper.TILE;
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
        YogDzewa yog = new YogDzewa();
        yog.x = 14 * ConstantsHelper.TILE;
        yog.y = 6 * ConstantsHelper.TILE;
        yog.floorY = yog.y;
        yog.setRoom(identifier);
        UnitHelper.getInstance().addUnit(yog);
        yog.spawnFists();
    }
}