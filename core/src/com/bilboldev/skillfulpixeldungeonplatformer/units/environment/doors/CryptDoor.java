package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class CryptDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorCryptSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The crypt is sealed behind a locked door.\nMust find a key...", null);
    }
}