package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class TreasuryRoom extends SingleDoorSpecialRoom {

    public TreasuryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 20 + 2 * layoutVariant(2);
        getLayout().describe("treasury-display", width / 2, 4);
        addPlatformSpan(6, width - 3, 3);
        addPlatformSpan(width / 2 - 1, width / 2 + 2, 5);
        return finishLayout();
    }

    @Override
    protected void placeContents() {
        placeRewardContainer(SpecialRoomRewards.goldStack(6, 10), "images/misc/extracted items/CHEST.png", 96, 96, width / 2, 4);
        placeItem(SpecialRoomRewards.goldStack(3, 6), width / 2 - 3, 4);
        placeItem(SpecialRoomRewards.goldStack(2, 5), width / 2 + 3, 4);
        placeItem(SpecialRoomRewards.goldStack(4, 7), width / 2 + 1, 6);

    }
}
