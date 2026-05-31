package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;

public class TreasureDoor extends Door {
    {
        sign = MapHelper.getInstance().getTheme().getDoorTreasureSign().clone();
    }

    @Override
    public void showMessage(){
        if(isLocked){
            WindowHelper.getInstance().addWindow(100, 100, "The treasure is hidden behind a locked door.\nMust find a key... ");
        }
    }
}

