package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableTomb;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.GraveRemains;

public class GraveyardRoom extends SingleDoorSpecialRoom {

    public GraveyardRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 22 + 2 * layoutVariant(2);
        getLayout().describe("graveyard-court", width / 2, 3);
        addPlatformSpan(6, 9, 3);
        addPlatformSpan(width - 9, width - 6, 3);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        GraveRemains leftRemains = new GraveRemains();
        leftRemains.setRewardItem(SpecialRoomRewards.goldStack(2, 4));
        placeInteractable(leftRemains, 7, 4);

        GraveRemains rightRemains = new GraveRemains();
        rightRemains.setRewardItem(SpecialRoomRewards.randomSupplyReward());
        placeInteractable(rightRemains, width - 7, 4);

        DisturbableTomb tomb = new DisturbableTomb();
        tomb.setRewardItem(SpecialRoomRewards.randomArmorReward());
        placeInteractable(tomb, width / 2, 3);

    }
}
