package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class LaboratoryDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorLaboratorySign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The laboratory lies behind a locked door.\nMust find a key...", null);
    }
}