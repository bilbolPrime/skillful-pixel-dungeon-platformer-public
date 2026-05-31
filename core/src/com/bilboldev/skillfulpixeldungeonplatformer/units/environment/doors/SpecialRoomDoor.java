package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;

public abstract class SpecialRoomDoor extends Door {
    protected void showSpecialRoomMessage(String lockedMessage, String openMessage) {
        if (isLocked && lockedMessage != null && !lockedMessage.isEmpty()) {
            WindowHelper.getInstance().addWindow(100, 100, lockedMessage);
            return;
        }

        if (!isLocked && openMessage != null && !openMessage.isEmpty()) {
            WindowHelper.getInstance().addWindow(100, 100, openMessage);
        }
    }
}