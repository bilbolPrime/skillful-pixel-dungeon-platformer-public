package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Statue;

public final class GuardedTreasureOnScreen extends TreasureOnScreen {
    private Statue firstGuardian, secondGuardian;

    public GuardedTreasureOnScreen() { }
    public GuardedTreasureOnScreen(Statue first, Statue second) { firstGuardian = first; secondGuardian = second; }

    @Override public void pickedUp(boolean refreshEnvironment) {
        super.pickedUp(refreshEnvironment);
        if (firstGuardian != null) firstGuardian.trigger();
        if (secondGuardian != null) secondGuardian.trigger();
        SoundHelper.GetSingleton().play(Sounds.ALERT, 0, 1);
    }
}
