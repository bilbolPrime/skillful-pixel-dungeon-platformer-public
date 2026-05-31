package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import java.util.Calendar;

public final class NightModeHelper {
    private static final int NIGHT_END_HOUR_EXCLUSIVE = 7;

    private static Boolean surfaceEndingNight;

    private NightModeHelper() {
    }

    public static boolean isNightModeActive() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < NIGHT_END_HOUR_EXCLUSIVE;
    }

    public static float getRespawnCheckMultiplier() {
        return isNightModeActive() ? 2f : 1f;
    }

    public static void captureSurfaceEndingState() {
        surfaceEndingNight = isNightModeActive();
    }

    public static boolean consumeSurfaceEndingNight() {
        boolean night = surfaceEndingNight != null ? surfaceEndingNight.booleanValue() : isNightModeActive();
        surfaceEndingNight = null;
        return night;
    }
}