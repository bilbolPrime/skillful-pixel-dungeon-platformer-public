package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableTomb;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.GraveRemains;

public class GraveyardRoom extends SingleDoorSpecialRoom {

    public GraveyardRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();

        GraveRemains leftRemains = new GraveRemains();
        leftRemains.setRewardItem(SpecialRoomRewards.goldStack(2, 4));
        placeInteractable(leftRemains, 6, 5);

        GraveRemains rightRemains = new GraveRemains();
        rightRemains.setRewardItem(SpecialRoomRewards.randomSupplyReward());
        placeInteractable(rightRemains, 12, 5);

        DisturbableTomb tomb = new DisturbableTomb();
        tomb.setRewardItem(SpecialRoomRewards.randomArmorReward());
        placeInteractable(tomb, 9, 7);

        return this;
    }
}