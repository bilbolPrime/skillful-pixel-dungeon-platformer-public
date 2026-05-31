package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class MagicWellDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorMagicWellSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage(null, null);
    }
}