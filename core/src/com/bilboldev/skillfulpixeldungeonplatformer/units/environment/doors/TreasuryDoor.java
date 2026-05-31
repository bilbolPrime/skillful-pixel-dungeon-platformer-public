package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class TreasuryDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorTreasurySign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The treasury lies behind a locked door.\nMust find a key...", null);
    }
}