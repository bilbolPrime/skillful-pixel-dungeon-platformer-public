package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;

public class ArmoryRoom extends SingleDoorSpecialRoom {

    public ArmoryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 18 + 2 * layoutVariant(2);
        getLayout().describe("armory-racks", width / 2, 4);
        addPlatformSpan(6, 9, 3);
        addPlatformSpan(width - 6, width - 3, 3);
        addPlatformSpan(width / 2 - 1, width / 2 + 2, 5);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), 7, 4);
        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), width / 2, 6);
        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), width - 5, 4);
        if (RandomHelper.getInstance().randomChance(35)) {
            placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), width / 2 + 1, 6);
        }

    }
}
