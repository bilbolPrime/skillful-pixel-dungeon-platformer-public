package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class ArmoryDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorArmorySign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The armory lies behind a locked door.\nMust find a key...", null);
    }
}