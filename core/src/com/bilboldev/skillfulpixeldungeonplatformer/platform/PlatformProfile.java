package com.bilboldev.skillfulpixeldungeonplatformer.platform;

public final class PlatformProfile {
    private static final PlatformProfile ANDROID = new PlatformProfile(true, false, WindowModeService.unsupported(), false);
    private static final PlatformProfile DESKTOP = new PlatformProfile(false, true, WindowModeService.unsupported(), false);
    private static final PlatformProfile DESKTOP_FREE = new PlatformProfile(false, true, WindowModeService.unsupported(), true);

    private final boolean touchControlsEnabled;
    private final boolean keyboardControlsEnabled;
    private final WindowModeService windowModeService;
    private final boolean freeDesktopBuild;

    private PlatformProfile(boolean touchControlsEnabled, boolean keyboardControlsEnabled, WindowModeService windowModeService,
            boolean freeDesktopBuild) {
        this.touchControlsEnabled = touchControlsEnabled;
        this.keyboardControlsEnabled = keyboardControlsEnabled;
        this.windowModeService = windowModeService == null ? WindowModeService.unsupported() : windowModeService;
        this.freeDesktopBuild = freeDesktopBuild;
    }

    public static PlatformProfile android() {
        return ANDROID;
    }

    public static PlatformProfile desktop() {
        return DESKTOP;
    }

    public static PlatformProfile desktop(WindowModeService windowModeService) {
        if (windowModeService == null || !windowModeService.isSupported()) {
            return DESKTOP;
        }

        return new PlatformProfile(false, true, windowModeService, false);
    }

    public static PlatformProfile desktopFree() {
        return DESKTOP_FREE;
    }

    public static PlatformProfile desktopFree(WindowModeService windowModeService) {
        if (windowModeService == null || !windowModeService.isSupported()) {
            return DESKTOP_FREE;
        }

        return new PlatformProfile(false, true, windowModeService, true);
    }

    public boolean touchControlsEnabled() {
        return touchControlsEnabled;
    }

    public boolean keyboardControlsEnabled() {
        return keyboardControlsEnabled;
    }

    public WindowModeService windowModeService() {
        return windowModeService;
    }

    public boolean isFreeDesktopBuild() {
        return freeDesktopBuild;
    }
}
