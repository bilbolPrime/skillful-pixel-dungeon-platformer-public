package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.PlatformProfile;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamException;

import javax.swing.JOptionPane;
import java.io.File;

public final class DesktopLauncher {
    public static final String LOCAL_DEVELOPMENT_PROPERTY = "spd.localDevelopmentLaunch";
    public static final String FREE_DESKTOP_BUILD_PROPERTY = "spd.desktopFreeBuild";
    private static final String STEAM_APP_ID_PROPERTY = "spd.steamAppId";
    private static final int STEAM_APP_ID = Integer.getInteger(STEAM_APP_ID_PROPERTY, 0);
    private static final String SELF_TEST_ARGUMENT = "--steam-self-test";

    private static boolean steamSelfTestMode;

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        steamSelfTestMode = hasArgument(args, SELF_TEST_ARGUMENT);
        boolean freeDesktopBuild = isFreeDesktopBuild();
        boolean localDevelopmentLaunch = !freeDesktopBuild && isLocalDevelopmentLaunch() && !steamSelfTestMode;

        if (localDevelopmentLaunch) {
            System.setProperty(LOCAL_DEVELOPMENT_PROPERTY, "true");
            System.out.println("[SteamLaunch] Local development launch detected; skipping Steam ownership check.");
        }
        else if (!freeDesktopBuild && !ensureSteamOwnership()) {
            if (steamSelfTestMode) {
                System.exit(1);
            }
            return;
        }

        if (steamSelfTestMode) {
            System.out.println(freeDesktopBuild
                    ? "[SteamLaunch] Self-test skipped for free desktop build."
                    : "[SteamLaunch] Self-test passed.");
            return;
        }

        DesktopWindowModeService windowModeService = new DesktopWindowModeService();
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Pixel Dungeon Platformer");
        configuration.setWindowIcon("icon.png");
        configuration.setResizable(true);
        configuration.setWindowListener(new DesktopFocusListener());
        windowModeService.applyStartupConfiguration(configuration);
        configuration.useVsync(true);

        DesktopAchievementService steamService = null;
        if (!localDevelopmentLaunch && !freeDesktopBuild) {
            DesktopAchievementService initializedSteamService = new DesktopAchievementService();
            if (initializedSteamService.init()) {
                steamService = initializedSteamService;
            }
        }

        new Lwjgl3Application(
        new SkillfulPixelDungeonPlatformer(
                (freeDesktopBuild ? PlatformProfile.desktopFree(windowModeService) : PlatformProfile.desktop(windowModeService))
                        .withGamepad(new DesktopGamepadService()),
                steamService, steamService == null ? null : new DesktopCloudStorage()),
                configuration);
    }

    private static boolean isFreeDesktopBuild() {
        return Boolean.parseBoolean(System.getProperty(FREE_DESKTOP_BUILD_PROPERTY, "false"));
    }

    private static boolean ensureSteamOwnership() {
        try {
            if (STEAM_APP_ID <= 0) {
                showLaunchError("Steam app ID is not configured. Set -D" + STEAM_APP_ID_PROPERTY + "=<your app id> for Steam builds.");
                return false;
            }

            if (!SteamAPI.loadLibraries(new DesktopSteamLibraryLoader())) {
                showLaunchError("Steam native libraries could not be loaded for this platform.");
                return false;
            }

            if (SteamAPI.restartAppIfNecessary(STEAM_APP_ID)) {
                return false;
            }

            if (!SteamAPI.init()) {
                showLaunchError("Steam initialization failed. Please launch Pixel Dungeon Platformer through Steam.");
                return false;
            }

            SteamApps steamApps = new SteamApps();
            try {
                if (!steamApps.isSubscribedApp(STEAM_APP_ID)) {
                    showLaunchError("Steam did not confirm ownership of Pixel Dungeon Platformer for this account.");
                    return false;
                }
            } finally {
                steamApps.dispose();
                SteamAPI.shutdown();
            }

            return true;
        } catch (SteamException exception) {
            showLaunchError("Steam ownership check failed: " + exception.getMessage());
            return false;
        }
    }

    private static boolean hasArgument(String[] args, String expectedArgument) {
        if (args == null) {
            return false;
        }

        for (String argument : args) {
            if (expectedArgument.equals(argument)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isLocalDevelopmentLaunch() {
        try {
            File codeSource = new File(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return codeSource.isDirectory();
        } catch (Exception exception) {
            return false;
        }
    }

    private static void showLaunchError(String message) {
        System.err.println("[SteamLaunch] " + message);
        if (!steamSelfTestMode) {
            JOptionPane.showMessageDialog(null, message, "Pixel Dungeon Platformer", JOptionPane.ERROR_MESSAGE);
        }
    }
}
