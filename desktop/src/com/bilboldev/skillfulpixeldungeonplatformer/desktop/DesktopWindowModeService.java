package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.WindowModeService;

import java.util.prefs.Preferences;

public final class DesktopWindowModeService implements WindowModeService {

    private static final String NODE_NAME = "skillful-pixel-dungeon-platformer";
    private static final String KEY_WINDOWED_MODE = "windowedMode";
    private static final String KEY_BORDERLESS_WINDOWED_MODE = "borderlessWindowedMode";
    private static final String KEY_WINDOW_WIDTH = "windowWidth";
    private static final String KEY_WINDOW_HEIGHT = "windowHeight";
    private static final int DEFAULT_WINDOWED_WIDTH = 1280;
    private static final int DEFAULT_WINDOWED_HEIGHT = 720;
    private static final int MIN_WINDOWED_WIDTH = 800;
    private static final int MIN_WINDOWED_HEIGHT = 600;

    private final Preferences preferences = Preferences.userRoot().node(NODE_NAME);

    public void applyStartupConfiguration(Lwjgl3ApplicationConfiguration configuration) {
        Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        if (isBorderlessWindowedModeEnabled()) {
            configuration.setDecorated(false);
            configuration.setWindowedMode(displayMode.width, displayMode.height);
        } else if (isWindowedModeEnabled()) {
            configuration.setDecorated(true);
            configuration.setWindowedMode(
                    clampWidth(windowedWidth(), displayMode.width),
                    clampHeight(windowedHeight(), displayMode.height)
            );
        } else {
            configuration.setDecorated(true);
            configuration.setFullscreenMode(displayMode);
        }
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean supportsBorderlessWindowedMode() {
        return true;
    }

    @Override
    public boolean isWindowedModeEnabled() {
        return preferences.getBoolean(KEY_WINDOWED_MODE, false);
    }

    @Override
    public boolean isBorderlessWindowedModeEnabled() {
        return preferences.getBoolean(KEY_BORDERLESS_WINDOWED_MODE, false);
    }

    @Override
    public void setWindowedModeEnabled(boolean enabled) {
        setWindowModes(enabled, enabled ? false : isBorderlessWindowedModeEnabled());
    }

    @Override
    public void setBorderlessWindowedModeEnabled(boolean enabled) {
        setWindowModes(enabled ? false : isWindowedModeEnabled(), enabled);
    }

    @Override
    public void rememberWindowSize(int width, int height) {
        if (!isWindowedModeEnabled() || width <= 0 || height <= 0) {
            return;
        }

        preferences.putInt(KEY_WINDOW_WIDTH, width);
        preferences.putInt(KEY_WINDOW_HEIGHT, height);
        flushPreferences();
    }

    private void setWindowModes(boolean windowedMode, boolean borderlessWindowedMode) {
        preferences.putBoolean(KEY_WINDOWED_MODE, windowedMode);
        preferences.putBoolean(KEY_BORDERLESS_WINDOWED_MODE, borderlessWindowedMode);
        flushPreferences();
        applyWindowMode(windowedMode, borderlessWindowedMode);
    }

    private void applyWindowMode(boolean windowedMode, boolean borderlessWindowedMode) {
        if (Gdx.graphics == null) {
            return;
        }

        Gdx.graphics.setUndecorated(borderlessWindowedMode);

        if (borderlessWindowedMode) {
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
        } else if (windowedMode) {
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setWindowedMode(
                    clampWidth(windowedWidth(), displayMode.width),
                    clampHeight(windowedHeight(), displayMode.height)
            );
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        bringWindowToFront();
    }

    private int windowedWidth() {
        return preferences.getInt(KEY_WINDOW_WIDTH, DEFAULT_WINDOWED_WIDTH);
    }

    private int windowedHeight() {
        return preferences.getInt(KEY_WINDOW_HEIGHT, DEFAULT_WINDOWED_HEIGHT);
    }

    private int clampWidth(int width, int maxWidth) {
        return Math.max(MIN_WINDOWED_WIDTH, Math.min(width, maxWidth));
    }

    private int clampHeight(int height, int maxHeight) {
        return Math.max(MIN_WINDOWED_HEIGHT, Math.min(height, maxHeight));
    }

    private void bringWindowToFront() {
        if (Gdx.graphics instanceof Lwjgl3Graphics) {
            ((Lwjgl3Graphics) Gdx.graphics).getWindow().restoreWindow();
            ((Lwjgl3Graphics) Gdx.graphics).getWindow().focusWindow();
        }
    }

    private void flushPreferences() {
        try {
            preferences.flush();
        } catch (Exception ignored) {
        }
    }
}