package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class PoolDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorPoolSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage(null, null);
    }
}