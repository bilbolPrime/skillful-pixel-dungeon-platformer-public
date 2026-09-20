package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.SpriteDecoration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.SpriteRewardOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

public abstract class SingleDoorSpecialRoom extends Room {
    {
        canSpawn = false;
        width = 18;
        height = 10;
    }

    public SingleDoorSpecialRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Door getRandomDoor() {
        requireSupportedPlacement(3, 3, ConstantsHelper.TILE, ConstantsHelper.TILE + 7f);
        Door door = new Door();
        door.x = 3 * ConstantsHelper.TILE;
        door.y = 3 * ConstantsHelper.TILE;

        return door;
    }

    protected void resetLayout() {
        platforms.clear();
        waterPlatforms.clear();
        stuff.clear();
        getLayout().reserveArrival(3, 3);
    }

    protected int layoutVariant(int count) {
        return RandomHelper.getInstance().createRoomRandom(0, identifier, 0x5350454349414cL).nextInt(count);
    }


    protected Room finishLayout() {
        height = Math.max(10, RoomRoutes.minimumRoomHeightForJump(getHighestStandingFloor()));
        return this;
    }

    protected void buildSplitPlatforms() {
        resetLayout();
        addPlatformSpan(3, 8, 4);
        addPlatformSpan(10, 15, 4);
        addPlatformSpan(6, 12, 6);
    }

    protected void buildWidePlatforms() {
        resetLayout();
        addPlatformSpan(3, 15, 4);
        addPlatformSpan(6, 12, 6);
    }

    protected void addSpriteProp(String spritePath, float width, float height, int tileX, int tileY) {
        SpriteDecoration prop = new SpriteDecoration(spritePath, width, height);
        prop.x = tileX * ConstantsHelper.TILE;
        prop.y = tileY * ConstantsHelper.TILE;
        stuff.add(prop);
    }

    protected void placeItem(Item item, int tileX, int floorTileY) {
        if (item == null) {
            return;
        }

        requireContentPlacement(tileX, floorTileY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        float worldX = tileToWorldX(tileX);
        float floorY = floorTileToWorldY(floorTileY);
        item.spawnNaturally(worldX, floorY, floorY, identifier);
    }

    protected void placeRewardContainer(Item item, String spritePath, float width, float height, int tileX, int floorTileY) {
        if (item == null) {
            return;
        }

        requireContentPlacement(tileX, floorTileY, Math.max(width, ConstantsHelper.UNIT_DIMENSIONS),
                Math.max(height, ConstantsHelper.UNIT_DIMENSIONS));
        SpriteRewardOnScreen rewardOnScreen = new SpriteRewardOnScreen(item, spritePath, width, height);
        rewardOnScreen.x = tileToWorldX(tileX);
        rewardOnScreen.y = floorTileToWorldY(floorTileY);
        rewardOnScreen.floorY = rewardOnScreen.y;
        rewardOnScreen.setRoom(identifier);
        UnitHelper.getInstance().addUnit(rewardOnScreen);

        if (item instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun) {
            InventoryHelper.getInstance().spawnNaturalCompanionItems(item, rewardOnScreen.x, rewardOnScreen.y, rewardOnScreen.floorY, identifier);
        }
    }

    protected void placeInteractable(Interactable interactable, int tileX, int floorTileY) {
        if (interactable == null) {
            return;
        }

        requireContentPlacement(tileX, floorTileY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        interactable.x = tileToWorldX(tileX);
        interactable.y = floorTileToWorldY(floorTileY);
        interactable.floorY = interactable.y;
        interactable.setRoom(identifier);
        UnitHelper.getInstance().addUnit(interactable);
    }

    protected void placePlant(Plant plant, int tileX, int floorTileY) {
        if (plant == null) {
            return;
        }

        requireContentPlacement(tileX, floorTileY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        plant.x = tileToWorldX(tileX);
        plant.y = floorTileToWorldY(floorTileY);
        plant.floorY = plant.y;
        plant.setRoom(identifier);
        UnitHelper.getInstance().addUnit(plant);
    }

    protected void addTrap(int tileX, int floorTileY, TrapType trapType, boolean hidden) {
        requireContentPlacement(tileX, floorTileY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        PlatformTrap trap = new PlatformTrap().setTrapType(trapType).setHidden(hidden);
        trap.x = tileToWorldX(tileX) + (ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        trap.y = floorTileY * ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS / 3f + ConstantsHelper.UNIT_DIMENSIONS * 0.25f;
        trap.floorY = floorTileY * ConstantsHelper.TILE;
        trap.setRoom(identifier);
        UnitHelper.getInstance().addUnit(trap);
    }

    protected float tileToWorldX(int tileX) {
        return tileX * ConstantsHelper.TILE;
    }

    protected float floorTileToWorldY(int floorTileY) {
        return floorTileY * ConstantsHelper.TILE;
    }
}
