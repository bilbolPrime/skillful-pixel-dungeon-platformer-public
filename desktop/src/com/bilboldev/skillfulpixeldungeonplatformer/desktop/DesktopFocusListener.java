package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;

public class DesktopFocusListener extends Lwjgl3WindowAdapter {
    @Override
    public void focusLost() {
        com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance().setFocused(false);
        BaseScreen screen = SkillfulPixelDungeonPlatformer.getActiveScreen();
        if (screen != null) screen.pause();
    }

    @Override
    public void focusGained() {
        com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance().setFocused(true);
        BaseScreen screen = SkillfulPixelDungeonPlatformer.getActiveScreen();
        if (screen != null) screen.resume();
    }
}
