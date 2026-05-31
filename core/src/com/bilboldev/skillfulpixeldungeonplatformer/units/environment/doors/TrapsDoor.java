package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class TrapsDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorTrapsSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage(null, null);
    }
}