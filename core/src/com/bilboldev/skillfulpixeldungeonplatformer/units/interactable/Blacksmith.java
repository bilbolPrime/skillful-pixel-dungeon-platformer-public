package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Blacksmith extends Interactable {
    {
        gs = new GameSprite("images/units/blacksmith/blacksmith.png", 64, 16, 1f);
        hp = mhp = 1000;
        gf = new GameFilm("images/units/blacksmith/blacksmith.png", 64, 16, 1f);
        gf.clipSizeX = 13;
        gf.clipSizeY = 16;
        idleFrames = new int[]{0, 1};
        showOnly = true;
        facingRight = false;
    }

    @Override
    public void interact() {
        QuestManager.getInstance().interactWithBlacksmith(this);
    }
}