package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class LaboratoryRoom extends SingleDoorSpecialRoom {

    public LaboratoryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildSplitPlatforms();

        placeItem(SpecialRoomRewards.randomPotionReward(), 4, 5);
        placeItem(SpecialRoomRewards.randomPotionReward(), 8, 7);
        placeItem(SpecialRoomRewards.randomPotionReward(), 12, 5);

        return this;
    }
}