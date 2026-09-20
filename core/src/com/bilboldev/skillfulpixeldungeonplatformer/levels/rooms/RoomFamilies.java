package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.badlogic.gdx.math.RandomXS128;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;


public final class RoomFamilies {
    private static final long SALT = 0x524f4f4d504c414eL;
    private static final String[][] NAMES = {
            {"drain-gallery", "cistern-terraces", "spillway-court"},
            {"prison-cell-gallery", "prison-guard-landings", "prison-service-passage"},
            {"caves-rock-terraces", "caves-braced-passage", "caves-irregular-chamber"},
            {"city-arcade", "city-broad-gallery", "city-columned-court"},
            {"halls-ritual-gallery", "halls-shrine-approach", "halls-monumental-chamber"}
    };
    private RoomFamilies() { }

    public static void build(Room room, int depth, int ordinal) {
        RandomXS128 floorRandom = RandomHelper.getInstance().createRoomRandom(depth, "families", SALT);
        RandomXS128 random = RandomHelper.getInstance().createRoomRandom(depth, room.getIdentifier(), SALT);
        int theme = Math.max(0, Math.min(4, (depth - 1) / 5));
        int family = (floorRandom.nextInt(3) + ordinal) % 3;
        room.width = 24 + random.nextInt(7) + (theme >= 3 ? 2 : 0);
        room.height = 10;
        room.platforms.clear(); room.waterPlatforms.clear();
        RoomLayout plan = room.getLayout();
        int center = room.width / 2;
        boolean sewers = theme == 0;
        plan.describe(NAMES[theme][family], center, ConstantsHelper.MIN_FLOOR);
        plan.enablePiers(true);


        buildTheme(new Shelves(room, random), theme, family);
        room.height = RoomRoutes.minimumRoomHeightForJump(room.getHighestStandingFloor());
        RoomRoutes.ensureTraversable(room);
        if (sewers) {

            room.addGroundWater();


            int wetSide = random.nextBoolean() ? 1 : -1;
            wetShelfNear(room, center + wetSide * room.width / 4, 3);
            wetShelfNear(room, center - wetSide * room.width / 4, 5);
            wetShelfNear(room, center + wetSide * room.width / 4, 7);
        }
    }

    private static void wetShelfNear(Room room, int preferredX, int row) {
        int closest = -1, distance = Integer.MAX_VALUE;
        for (int x = 1; x < room.width - 1; x++) {
            if (room.platforms.contains(x + "_" + row) && Math.abs(x - preferredX) < distance) {
                closest = x; distance = Math.abs(x - preferredX);
            }
        }
        if (closest >= 0) room.addWaterPlatform(closest, row);
    }


    private static void buildTheme(Shelves s, int theme, int family) {
        int w = s.room.width, c = w / 2;
        if (theme == 0) {
            if (family == 0) {
                s.at(2,c-3,4); s.at(c+2,w-3,4);
                s.at(3,7,6); s.at(c-3,c+3,6); s.at(w-8,w-3,6);
                s.at(6,c-1,8); s.at(c+3,w-5,8);
            } else if (family == 1) {
                s.at(2,7,4); s.at(c-2,c+2,4); s.at(w-7,w-3,4);
                s.at(6,c+1,6); s.at(c+5,w-3,6);
                s.at(3,8,8); s.at(c+1,w-6,8);
            } else {
                s.at(2,8,4); s.at(c-1,c+2,4); s.at(w-9,w-3,4);
                s.at(2,5,6); s.at(c-5,c+5,6); s.at(w-6,w-3,6);
                s.at(4,9,8); s.at(w-10,w-5,8);
            }
        } else if (theme == 1) {
            if (family == 0) {
                s.at(2,6,4); s.at(c-3,c+2,4); s.at(w-7,w-3,4);
                s.at(3,c-1,6); s.at(c+3,w-3,6);
                s.at(4,8,8); s.at(c-2,c+2,8); s.at(w-7,w-3,8);
            } else if (family == 1) {
                s.at(3,c-1,4); s.at(c+2,w-3,4);
                s.at(2,6,6); s.at(c-4,c+4,6); s.at(w-7,w-3,6);
                s.at(3,9,8); s.at(c+1,w-4,8);
            } else {
                s.at(2,8,4); s.at(c,c+4,4); s.at(w-6,w-3,4);
                s.at(5,c+1,6); s.at(w-9,w-3,6);
                s.at(2,7,8); s.at(c-2,c+3,8); s.at(w-7,w-3,8);
            }
        } else if (theme == 2) {
            if (family == 0) {
                s.at(2,7,4); s.at(c-3,c+2,4); s.at(w-7,w-3,4);
                s.at(4,10,6); s.at(c+1,w-4,6);
                s.at(2,6,8); s.at(c-4,c+1,8); s.at(w-7,w-3,8);
            } else if (family == 1) {
                s.at(3,c-2,4); s.at(c+2,w-3,4);
                s.at(2,7,6); s.at(c-3,c+3,6); s.at(w-7,w-3,6);
                s.at(5,c+1,8); s.at(c+5,w-3,8);
            } else {
                s.at(2,6,4); s.at(c-4,c+1,4); s.at(w-8,w-3,4);
                s.at(3,9,6); s.at(c+2,w-3,6);
                s.at(5,c,8); s.at(c+4,w-4,8);
            }
        } else if (theme == 3) {
            if (family == 0) {
                s.at(3,8,4); s.at(c-2,c+3,4); s.at(w-7,w-3,4);
                s.at(5,c,6); s.at(c+4,w-3,6);
                s.at(3,8,8); s.at(c-2,c+3,8); s.at(w-7,w-3,8);
            } else if (family == 1) {
                s.at(2,c,4); s.at(c+4,w-3,4);
                s.at(4,9,6); s.at(c+1,w-4,6);
                s.at(3,c-1,8); s.at(c+3,w-4,8);
            } else {
                s.at(2,8,4); s.at(c-2,c+2,4); s.at(w-9,w-3,4);
                s.at(3,7,6); s.at(c-4,c+4,6); s.at(w-8,w-3,6);
                s.at(4,c-1,8); s.at(c+3,w-5,8);
            }
        } else {
            if (family == 0) {
                s.at(3,c-2,4); s.at(c+2,w-4,4);
                s.at(2,7,6); s.at(c-3,c+3,6); s.at(w-8,w-3,6);
                s.at(4,9,8); s.at(c-1,c+4,8); s.at(w-6,w-3,8);
            } else if (family == 1) {
                s.at(2,8,4); s.at(c,c+4,4); s.at(w-6,w-3,4);
                s.at(5,c+2,6); s.at(w-8,w-3,6);
                s.at(3,8,8); s.at(c+1,w-4,8);
            } else {
                s.at(2,c-1,4); s.at(c+3,w-3,4);
                s.at(3,8,6); s.at(c-2,c+2,6); s.at(w-8,w-3,6);
                s.at(5,c+1,8); s.at(c+5,w-3,8);
            }
        }
    }

    private static final class Shelves {
        final Room room;
        final RandomXS128 random;
        final boolean mirrored;
        Shelves(Room room, RandomXS128 random) {
            this.room = room; this.random = random; mirrored = random.nextBoolean();
        }
        void at(int left, int right, int standingFloor) {

            left = Math.max(2, Math.min(room.width - 6, left + random.nextInt(3) - 1));
            right = Math.max(left + 3, Math.min(room.width - 3, right + random.nextInt(3) - 1));
            if (mirrored) { int oldLeft = left; left = room.width - 1 - right; right = room.width - 1 - oldLeft; }
            room.addPlatformSpan(left, right, standingFloor - 1);
        }
    }
}
