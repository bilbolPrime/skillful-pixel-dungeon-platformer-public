package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.codedisaster.steamworks.SteamLibraryLoader;
import org.lwjgl.system.Library;
import org.lwjgl.system.Platform;

final class DesktopSteamLibraryLoader implements SteamLibraryLoader {
    @Override
    public void setLibraryPath(String libraryPath) {
        System.setProperty("org.lwjgl.librarypath", libraryPath);
    }

    @Override
    public boolean loadLibrary(String libraryName) {
        Platform operatingSystem = Platform.get();
        Platform.Architecture architecture = Platform.getArchitecture();

        if (operatingSystem == Platform.WINDOWS && architecture == Platform.Architecture.X64) {
            libraryName = libraryName + "64";
        }

        try {
            Library.loadSystem("com.codedisaster.steamworks", libraryName);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}