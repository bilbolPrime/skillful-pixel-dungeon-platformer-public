package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public class GraveRemains extends DisturbableGraveProp {

    public GraveRemains() {
        super("images/misc/extracted items/BONES.png", 72f, 72f);
    }

    @Override
    public void interact() {
        if (!canInteract()) {
            return;
        }

        SoundHelper.GetSingleton().play(Sounds.BONES, 0f, 1f);
        disturbAndDropReward();
        if (RandomHelper.getInstance().randomChance(50)) {
            if (spawnWraithsAroundHero(1) == 0) {
                spawnWraiths(1);
            }
        }
    }
}