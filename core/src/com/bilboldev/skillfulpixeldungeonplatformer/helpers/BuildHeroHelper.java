package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.BuildHeroWindow;

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
}

