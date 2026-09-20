package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class StorageRoom extends SingleDoorSpecialRoom {

    public StorageRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 20 + 2 * layoutVariant(2);
        getLayout().describe("storage-stacks", width - 6, 5);
        addPlatformSpan(6, 9, 3);
        addPlatformSpan(11, width - 3, 4);
        addPlatformSpan(width - 8, width - 5, 6);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        placeItem(SpecialRoomRewards.randomSupplyReward(), 7, 4);
        placeItem(SpecialRoomRewards.randomSupplyReward(), 9, 4);
        placeItem(SpecialRoomRewards.randomSupplyReward(), width - 6, 5);
        placeItem(SpecialRoomRewards.randomSupplyReward(), width - 7, 7);

    }
}
