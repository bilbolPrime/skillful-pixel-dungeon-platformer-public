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
        resetLayout();
        width = 20 + 2 * layoutVariant(2);
        getLayout().describe("garden-beds", width / 2, 6);
        addPlatformSpan(6, 9, 3);
        addPlatformSpan(width - 7, width - 3, 3);
        addPlatformSpan(width / 2 - 2, width / 2 + 2, 5);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        placePlant(new SungrassPlant(), 6, 4);
        placePlant(new SungrassPlant(), width - 5, 4);
        if (RandomHelper.getInstance().randomChance(40)) {
            placePlant(new SungrassPlant(), width / 2 - 1, 6);
        }

        SungrassSeed seedReward = new SungrassSeed();
        seedReward.setQuantity(2 + RandomHelper.getInstance().randomInt(2));
        placeItem(seedReward, width / 2 + 1, 6);
        placeItem(SpecialRoomRewards.randomGardenReward(), 8, 4);

    }
}
