package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;

public class VaultRoom extends SingleDoorSpecialRoom {

    public VaultRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 18 + 2 * layoutVariant(2);
        getLayout().describe("vault-alcove", width - 6, 4);
        addPlatformSpan(7, 11, 3);
        addPlatformSpan(width - 5, width - 3, 3);
        addPlatformSpan(width - 7, width - 3, 5);
        return finishLayout();
    }

    @Override
    protected void placeContents() {
        placeRewardContainer(SpecialRoomRewards.randomWandOrRingReward(), "images/misc/extracted items/CHEST.png", 96, 96, 9, 4);
        placeRewardContainer(SpecialRoomRewards.randomWandOrRingReward(), "images/misc/extracted items/CHEST.png", 96, 96, width - 5, 6);
        if (RandomHelper.getInstance().randomChance(35)) {
            placeRewardContainer(SpecialRoomRewards.randomWandOrRingReward(), "images/misc/extracted items/CHEST.png", 96, 96, width - 3, 4);
        }

    }
}
