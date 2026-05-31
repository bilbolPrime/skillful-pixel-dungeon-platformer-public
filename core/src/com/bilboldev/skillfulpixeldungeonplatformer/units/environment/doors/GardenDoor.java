package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class GardenDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorGardenSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage(null, null);
    }
}