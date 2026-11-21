package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.PlatformProfile;

public final class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        DesktopWindowModeService windowModeService = new DesktopWindowModeService();
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Pixel Dungeon Platformer");
        configuration.setWindowIcon("icon.png");
        configuration.setResizable(true);
        configuration.setWindowListener(new DesktopFocusListener());
        windowModeService.applyStartupConfiguration(configuration);
        configuration.useVsync(true);

        new Lwjgl3Application(new SkillfulPixelDungeonPlatformer(
                PlatformProfile.desktopFree(windowModeService).withGamepad(new DesktopGamepadService())), configuration);
    }
}
