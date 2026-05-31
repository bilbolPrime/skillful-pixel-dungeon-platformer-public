package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class StorageDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorStorageSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The storage room lies behind a sealed door.", null);
    }
}