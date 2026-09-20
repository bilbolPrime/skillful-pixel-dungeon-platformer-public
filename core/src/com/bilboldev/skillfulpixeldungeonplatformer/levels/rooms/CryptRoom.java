package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableTomb;

public class CryptRoom extends SingleDoorSpecialRoom {

    public CryptRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 18 + 2 * layoutVariant(2);
        getLayout().describe("crypt-alcove", width / 2, 4);
        addPlatformSpan(width / 2 - 3, width / 2 + 3, 3);
        addPlatformSpan(6, 7, 5);
        addPlatformSpan(width - 5, width - 4, 5);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        DisturbableTomb tomb = new DisturbableTomb();
        tomb.setRewardItem(SpecialRoomRewards.randomArmorReward());
        placeInteractable(tomb, width / 2, 4);

    }
}
