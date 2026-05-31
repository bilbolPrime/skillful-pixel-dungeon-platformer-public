package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

public class LibraryDoor extends Door {
    {
        sign = MapHelper.getInstance().getTheme().getDoorLibrarySign().clone();
    }

    @Override
    public void showMessage(){
        if(isLocked){
            WindowHelper.getInstance().addWindow(100, 100,
                    Messages.get("custom.generated.the_library_is_hidden_behind_94af919708"));
        }
    }
}

