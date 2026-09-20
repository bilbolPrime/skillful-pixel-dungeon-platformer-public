package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;


final class RoomFoyers {
    private RoomFoyers() { }

    static void build(Room room, int depth, boolean entrance) {
        room.width = 18 + 2 * RandomHelper.getInstance().createRoomRandom(depth,
                room.getIdentifier(), 0x464f594552L).nextInt(2);
        room.height = 10;
        room.platforms.clear(); room.waterPlatforms.clear(); room.stuff.clear();
        RoomLayout plan = room.getLayout();
        plan.describe(entrance ? "arrival-gallery" : "departure-landing", room.width / 2, ConstantsHelper.MIN_FLOOR);
        plan.reserveArrival(3, ConstantsHelper.MIN_FLOOR);
        plan.reserveArrival(room.width - 4, ConstantsHelper.MIN_FLOOR);

        room.addPlatformSpan(7, room.width - 8, entrance ? 4 : 3);
        RoomRoutes.ensureTraversable(room);
    }
}
