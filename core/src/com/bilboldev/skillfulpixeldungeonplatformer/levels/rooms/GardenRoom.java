package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.SungrassSeed;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.SungrassPlant;

public class GardenRoom extends SingleDoorSpecialRoom {

    public GardenRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();

        placePlant(new SungrassPlant(), 6, 5);
        placePlant(new SungrassPlant(), 9, 7);
        if (RandomHelper.getInstance().randomChance(40)) {
            placePlant(new SungrassPlant(), 12, 5);
        }

        SungrassSeed seedReward = new SungrassSeed();
        seedReward.setQuantity(2 + RandomHelper.getInstance().randomInt(2));
        placeItem(seedReward, 11, 7);
        placeItem(SpecialRoomRewards.randomGardenReward(), 5, 5);

        return this;
    }
}