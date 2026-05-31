package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public class DisturbableTomb extends DisturbableGraveProp {

    public DisturbableTomb() {
        super("images/misc/extracted items/TOMB.png", 96f, 96f);
    }

    @Override
    public void interact() {
        if (!canInteract()) {
            return;
        }

        SoundHelper.GetSingleton().play(Sounds.TOMB, 0f, 1f);
        disturbAndDropReward();
        if (spawnWraithsAroundHero(4) == 0) {
            spawnWraiths(2);
        }
    }
}