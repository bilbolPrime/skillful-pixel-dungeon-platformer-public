package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.BuildHeroWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.DescriptionWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.SavedGameWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.DifficultySelectWindow;

public class BuildHeroHelper {

    private static BuildHeroHelper m_instance;

    public static BuildHeroHelper getSingleton(){
        if(m_instance == null){
            m_instance = new BuildHeroHelper();
        }

        return m_instance;
    }

    private BuildHeroHelper(){

    }

    public void buildHeroWindow(HeroClass heroClass){
        WindowHelper.getInstance().addWindow(new BuildHeroWindow(heroClass,2000, 1000).build());
    }

    public void showDetail(String icon, String description) {
        WindowHelper.getInstance().addWindow(new DescriptionWindow(icon, description, 1500, 400).build());
    }


    public void play(HeroClass heroClass) {
        if (com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled()
                && !com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
            int slot = SaveHelper.getInstance().firstEmptySlot();
            if (slot < 0) WindowHelper.getInstance().addWindow(new com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow(
                    1100, 240, com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages.get("desktop.runs.full")).build());
            else WindowHelper.getInstance().addWindow(new DifficultySelectWindow(heroClass, slot).build());
            return;
        }
        if (SaveHelper.getInstance().hasSave(heroClass)) {
            WindowHelper.getInstance().addWindow(new SavedGameWindow(heroClass).build());
        } else WindowHelper.getInstance().addWindow(new DifficultySelectWindow(heroClass, false).build());
    }
}

