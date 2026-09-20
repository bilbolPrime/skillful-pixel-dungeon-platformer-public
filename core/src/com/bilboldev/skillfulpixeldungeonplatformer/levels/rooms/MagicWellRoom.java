package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

public class MagicWellRoom extends SingleDoorSpecialRoom {

    public MagicWellRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 20 + 2 * layoutVariant(2);
        int center = width / 2;
        getLayout().describe("well-basin", center, 3);
        addGroundWater();
        addPlatformSpan(center - 5, center - 3, 3);
        addPlatformSpan(center + 3, center + 5, 3);
        return finishLayout();
    }

    @Override
    protected void placeContents() {


        placeItem(SpecialRoomRewards.randomWellReward(), width / 2, 3);
        placeItem(SpecialRoomRewards.randomPotionReward(), width / 2 - 4, 4);
        placeItem(SpecialRoomRewards.randomPotionReward(), width / 2 + 4, 4);

    }
}
