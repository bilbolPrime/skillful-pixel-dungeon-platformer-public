package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.Library;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelExitDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Goo;

public class GooRoom extends Room {
    private static final int WATER_PLATFORM_START_X = 8;
    private static final int WATER_PLATFORM_END_X = 12;
    private static final int WATER_PLATFORM_TILE_Y = 6;
    private static final int DOOR_SUPPORT_HALF_WIDTH = 1;

    private int waterStartTileX = WATER_PLATFORM_START_X;
    private int waterEndTileX = WATER_PLATFORM_END_X;

    {
        canSpawn = false;
        width = 20;
    }

    public GooRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Door getRandomDoor(){
        int tileX = 0;
        int tileY = 0;
        boolean doorExists = false;
        do {
            doorExists = false;
            String platform = "";
            for(String candidatePlatform : platforms){
                platform = candidatePlatform;
                if(RandomHelper.getInstance().randomBoolean()){
                    break;
                }
            }

            tileX = Integer.parseInt(platform.split("_")[0]);
            tileY = Integer.parseInt(platform.split("_")[1]);

            for(Door door : doors){
                if(door.x == tileX * ConstantsHelper.TILE && door.y == (tileY + 1) * ConstantsHelper.TILE){
                    doorExists = true;
                    break;
                }
            }
        }while(doorExists || tileX < 0 || tileX >= width);

        Door door = new Door();
        door.x = tileX * ConstantsHelper.TILE;
        door.y = (tileY + 1)* ConstantsHelper.TILE;

        return door;
    }

    @Override
    public Room build(){
        super.build();
        for (int tileX = WATER_PLATFORM_START_X; tileX <= WATER_PLATFORM_END_X; tileX++) {
            platforms.add(UtilsHelper.platformKey(tileX, WATER_PLATFORM_TILE_Y));
        }
        int waterStart = WATER_PLATFORM_START_X;
        while (platforms.contains(UtilsHelper.platformKey(waterStart - 1, WATER_PLATFORM_TILE_Y))) {
            waterStart--;
        }

        int waterEnd = WATER_PLATFORM_END_X;
        while (platforms.contains(UtilsHelper.platformKey(waterEnd + 1, WATER_PLATFORM_TILE_Y))) {
            waterEnd++;
        }

        waterStartTileX = waterStart;
        waterEndTileX = waterEnd;

        addWaterSpan(waterStart, waterEnd, WATER_PLATFORM_TILE_Y);

        return this;
    }

    @Override
    public void addDoor(Door door) {
        super.addDoor(door);
        ensureDoorPlatformReachable(door);
    }

    @Override
    public void entered(){
        if (bossDefeated) {
            return;
        }

        for (Door door : doors){
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

        markBossEncounterStarted();

        Door spawn = getRandomDoor();
        Goo unit = new Goo();
        unit.x = spawn.x;
        unit.y = spawn.y;
        unit.setRoom(getIdentifier());
        UnitHelper.getInstance().addUnit(unit);
    }

    private void ensureDoorPlatformReachable(Door door) {
        if (door == null) {
            return;
        }

        int tileX = Math.round(door.x / ConstantsHelper.TILE);
        int floorTileY = Math.round(door.y / ConstantsHelper.TILE);
        int platformTileY = floorTileY - 1;
        if (platformTileY < 0) {
            return;
        }

        addSupportPlatform(tileX, platformTileY);

        for (int currentTileY = platformTileY - 2; currentTileY > WATER_PLATFORM_TILE_Y; currentTileY -= 2) {
            addSupportPlatform(tileX, currentTileY);
        }

        if (tileX < waterStartTileX) {
            addPlatformSpan(tileX, waterStartTileX, WATER_PLATFORM_TILE_Y);
        }
        else if (tileX > waterEndTileX) {
            addPlatformSpan(waterEndTileX, tileX, WATER_PLATFORM_TILE_Y);
        }
        else {
            addSupportPlatform(tileX, WATER_PLATFORM_TILE_Y);
        }
    }

    private void addSupportPlatform(int centerTileX, int tileY) {
        addPlatformSpan(
                Math.max(0, centerTileX - DOOR_SUPPORT_HALF_WIDTH),
                Math.min(width - 1, centerTileX + DOOR_SUPPORT_HALF_WIDTH),
                tileY);
    }
}
