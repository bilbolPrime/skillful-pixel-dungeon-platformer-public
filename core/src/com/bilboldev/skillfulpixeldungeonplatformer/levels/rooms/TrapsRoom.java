package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLevitation;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

public class TrapsRoom extends SingleDoorSpecialRoom {

    public TrapsRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        resetLayout();
        width = 22 + 2 * layoutVariant(2);
        getLayout().describe("hazard-crossing", 10, 3);

        addPlatformSpan(width - 5, width - 2, 3);
        return finishLayout();
    }

    @Override
    protected void placeContents() {

        placeItem(new PotionOfLevitation(), 5, 3);
        placeItem(SpecialRoomRewards.randomWeaponOrArmorReward(), width - 3, 4);

        addTrap(7, 3, TrapType.TOXIC, false);
        addTrap(8, 3, TrapType.PARALYTIC, true);
        addTrap(9, 3, TrapType.POISON, false);
        addTrap(11, 3, TrapType.ALARM, true);
        addTrap(12, 3, TrapType.LIGHTNING, false);
        addTrap(13, 3, TrapType.GRIPPING, true);

    }
}
