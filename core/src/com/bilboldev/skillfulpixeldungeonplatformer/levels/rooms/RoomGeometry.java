package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;


public final class RoomGeometry {
    private static final float TILE = ConstantsHelper.TILE;
    private static final float EPSILON = 0.01f;
    private RoomGeometry() { }

    public static boolean validPlatform(Room room, int x, int y) {
        return x >= 0 && x < room.getWidth() && y >= ConstantsHelper.MIN_FLOOR - 1
                && y < room.getHeight() - 1;
    }

    public static void normalize(Room room) {

        if (room.isBossArena()) return;
        room.getPlatforms().removeIf(key -> !validKey(room, key));
        room.getWaterPlatforms().removeIf(key -> !validKey(room, key)
                || (!key.endsWith("_" + (ConstantsHelper.MIN_FLOOR - 1)) && !room.getPlatforms().contains(key)));
    }

    private static boolean validKey(Room room, String key) {
        if (key == null) return false;
        String[] parts = key.split("_");
        if (parts.length != 2) return false;
        try { return validPlatform(room, Integer.parseInt(parts[0]), Integer.parseInt(parts[1])); }
        catch (NumberFormatException ignored) { return false; }
    }

    public static boolean supports(Room room, float x, float floorY, float width, float height) {
        if (!Float.isFinite(x) || !Float.isFinite(floorY) || !Float.isFinite(width) || !Float.isFinite(height)
                || width <= 0f || height <= 0f || x < 1f
                || x + width > room.getWidth() * TILE - 1f
                || floorY < ConstantsHelper.MIN_FLOOR * TILE
                || floorY + height > room.getHeight() * TILE) return false;
        int floor = Math.round(floorY / TILE);
        if (Math.abs(floorY - floor * TILE) > EPSILON) return false;
        int left = (int) Math.floor(x / TILE);
        int right = (int) Math.floor((x + width - EPSILON) / TILE);
        for (int column = left; column <= right; column++) {
            if (floor != ConstantsHelper.MIN_FLOOR
                    && !room.getPlatforms().contains(UtilsHelper.platformKey(column, floor - 1))) return false;


            for (int upperFloor = floor + 1; upperFloor * TILE < floorY + height - EPSILON; upperFloor++) {
                if (room.getPlatforms().contains(UtilsHelper.platformKey(column, upperFloor - 1))) return false;
            }
        }
        return true;
    }
}
