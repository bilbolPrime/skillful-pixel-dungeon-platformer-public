package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class LaboratoryRoom extends SingleDoorSpecialRoom {

    public LaboratoryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 18 + 2 * layoutVariant(2);
        getLayout().describe("laboratory-benches", width / 2, 4);
        addPlatformSpan(6, 10, 3);
        addPlatformSpan(12, width - 3, 3);
        addPlatformSpan(9, 13, 5);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        placeItem(SpecialRoomRewards.randomPotionReward(), 8, 4);
        placeItem(SpecialRoomRewards.randomPotionReward(), 12, 6);
        placeItem(SpecialRoomRewards.randomPotionReward(), width - 5, 4);

    }
}
