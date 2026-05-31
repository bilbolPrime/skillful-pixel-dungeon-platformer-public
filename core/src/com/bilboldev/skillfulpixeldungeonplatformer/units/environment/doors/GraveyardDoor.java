package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class GraveyardDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorGraveyardSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage(null, null);
    }
}