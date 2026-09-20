package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;


public final class RoomFraming {
    public static final float ORIGINAL_VIEW_HEIGHT = 1024f;
    public static final float DESKTOP_TILES_ACROSS = 17.5f;
    private RoomFraming() { }

    public static float baseZoom(boolean desktop, boolean protectedScene, int screenWidth, int screenHeight) {

        return desktop && !protectedScene && screenWidth >= 1100 && screenHeight >= 600
                ? DESKTOP_TILES_ACROSS * ConstantsHelper.TILE * 9f / 16f / ORIGINAL_VIEW_HEIGHT : 1f;
    }

    public static float centerY(float visibleHeight, float roomHeight, float uiHeight, float reservedUiHeight, boolean widerDesktop) {
        float reserve = visibleHeight * reservedUiHeight / Math.max(1f, uiHeight);
        float bottom = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE - reserve;
        if (!widerDesktop) {
            if (roomHeight < visibleHeight) return roomHeight / 2f;
            bottom = Math.max(0f, Math.min(bottom, roomHeight - visibleHeight));
        }

        return bottom + visibleHeight / 2f;
    }


    public static float fitJump(float preferredZoom, float jumpTop, float uiHeight, float reservedUiHeight) {
        float usableFraction = Math.max(.1f, 1f - reservedUiHeight / Math.max(1f, uiHeight));
        float neededHeight = (jumpTop - ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE) / usableFraction;
        return Math.max(preferredZoom, neededHeight / ORIGINAL_VIEW_HEIGHT);
    }
}
