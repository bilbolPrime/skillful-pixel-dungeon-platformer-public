package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;

public class VaultRoom extends SingleDoorSpecialRoom {

    public VaultRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();
        placeRewardContainer(SpecialRoomRewards.randomWandOrRingReward(), "images/misc/extracted items/CRYSTAL_CHEST.png", 96, 96, 8, 5);
        placeRewardContainer(SpecialRoomRewards.randomWandOrRingReward(), "images/misc/extracted items/LOCKED_CHEST.png", 96, 96, 11, 7);
        if (RandomHelper.getInstance().randomChance(35)) {
            placeRewardContainer(SpecialRoomRewards.randomWandOrRingReward(), "images/misc/extracted items/CRYSTAL_CHEST.png", 96, 96, 13, 5);
        }

        return this;
    }
}