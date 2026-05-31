package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLevitation;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

public class TrapsRoom extends SingleDoorSpecialRoom {

    public TrapsRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();

        placeItem(new PotionOfLevitation(), 5, 5);
        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), 12, 7);

        addTrap(7, 5, TrapType.TOXIC, false);
        addTrap(8, 5, TrapType.PARALYTIC, true);
        addTrap(9, 5, TrapType.POISON, false);
        addTrap(10, 5, TrapType.ALARM, true);
        addTrap(11, 7, TrapType.LIGHTNING, false);
        addTrap(12, 7, TrapType.GRIPPING, true);

        return this;
    }
}