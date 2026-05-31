package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class TreasuryRoom extends SingleDoorSpecialRoom {

    public TreasuryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();
        placeRewardContainer(SpecialRoomRewards.goldStack(6, 10), "images/misc/extracted items/CHEST.png", 96, 96, 9, 5);
        placeItem(SpecialRoomRewards.goldStack(3, 6), 6, 5);
        placeItem(SpecialRoomRewards.goldStack(2, 5), 12, 5);
        placeItem(SpecialRoomRewards.goldStack(4, 7), 10, 7);

        return this;
    }
}