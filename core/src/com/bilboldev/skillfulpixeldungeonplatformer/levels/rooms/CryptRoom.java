package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableTomb;

public class CryptRoom extends SingleDoorSpecialRoom {

    public CryptRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildSplitPlatforms();

        DisturbableTomb tomb = new DisturbableTomb();
        tomb.setRewardItem(SpecialRoomRewards.randomArmorReward());
        placeInteractable(tomb, 9, 7);

        return this;
    }
}