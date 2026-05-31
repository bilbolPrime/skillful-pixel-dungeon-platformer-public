package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmuletHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;

public class LevelEntryDoor extends Door {
    {
        sign = MapHelper.getInstance().getTheme().getDoorLevelEntrySign().clone();
    }


    @Override
    public void showMessage(){
        if(MapHelper.getInstance().getDepth() == 1){
            if (AmuletHelper.hasAmulet()) {
                open();
                AmuletHelper.reachSurface();
            }
            else {
                WindowHelper.getInstance().addWindow(new TextWindow(
                        1200f,
                        220f,
                        "I did not come to this dungeon to cower at the first step,\nMy people need me...")
                        .build());
            }
        }
        else {
            open();
            MapHelper.getInstance().goUp();
        }
    }
}

