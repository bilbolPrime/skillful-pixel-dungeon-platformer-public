package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Wandmaker extends Interactable {
    {
        gs = new GameSprite("images/units/wandmaker/wandmaker.png", 64, 16, 1f);
        hp = mhp = 1000;
        gf = new GameFilm("images/units/wandmaker/wandmaker.png", 64, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 14;
        idleFrames = new int[]{0, 1};
        showOnly = true;
        facingRight = false;
    }

    @Override
    public void interact() {
        QuestManager.getInstance().interactWithWandmaker(this);
    }
}