package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;

public class ArmoryRoom extends SingleDoorSpecialRoom {

    public ArmoryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildSplitPlatforms();

        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), 4, 5);
        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), 7, 7);
        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), 12, 5);
        if (RandomHelper.getInstance().randomChance(35)) {
            placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), 10, 7);
        }

        return this;
    }
}