package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class MagicWellRoom extends SingleDoorSpecialRoom {

    public MagicWellRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();
        addWaterSpan(5, 13, 4);

        placeItem(SpecialRoomRewards.randomWellReward(), 9, 7);
        placeItem(SpecialRoomRewards.randomPotionReward(), 6, 5);
        placeItem(SpecialRoomRewards.randomPotionReward(), 12, 5);

        return this;
    }
}