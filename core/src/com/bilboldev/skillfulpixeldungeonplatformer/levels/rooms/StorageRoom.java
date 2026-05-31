package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class StorageRoom extends SingleDoorSpecialRoom {

    public StorageRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildSplitPlatforms();

        placeItem(SpecialRoomRewards.randomSupplyReward(), 4, 5);
        placeItem(SpecialRoomRewards.randomSupplyReward(), 7, 7);
        placeItem(SpecialRoomRewards.randomSupplyReward(), 11, 5);
        placeItem(SpecialRoomRewards.randomSupplyReward(), 13, 7);

        return this;
    }
}