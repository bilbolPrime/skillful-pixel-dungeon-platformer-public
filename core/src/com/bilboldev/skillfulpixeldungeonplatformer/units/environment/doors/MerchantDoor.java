package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;

public class MerchantDoor extends Door {
    {
        sign = MapHelper.getInstance().getTheme().getDoorMerchantSign().clone();
    }

    @Override
    public void showMessage(){
        if(isLocked){
            WindowHelper.getInstance().addWindow(100, 100, "The merchant lies behind a locked door.\nMust find a key...");
        }
    }
}

