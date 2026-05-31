package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class LevelExitDoor extends Door {
    {
        sign = MapHelper.getInstance().getTheme().getDoorLevelExitSign().clone();
    }

    @Override
    protected GameSprite getOpenedDoorSprite() {
        return MapHelper.getInstance().getTheme().getUncagedDoor().clone();
    }

    @Override
    public void showMessage(){
        open();
        MapHelper.getInstance().goDown();
    }
}

