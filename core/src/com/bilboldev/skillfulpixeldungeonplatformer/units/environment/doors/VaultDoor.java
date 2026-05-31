package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

public class VaultDoor extends SpecialRoomDoor {
    {
        sign = MapHelper.getInstance().getTheme().getDoorVaultSign().clone();
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The vault lies behind a locked door.\nMust find a key...", null);
    }
}