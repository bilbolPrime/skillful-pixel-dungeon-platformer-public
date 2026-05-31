package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ImpShopkeeper extends Merchant {
    {
        gs = new GameSprite("images/units/imp/demon.png", 64, 16, 1f);
        hp = mhp = 1000;
        gf = new GameFilm("images/units/imp/demon.png", 64, 16, 1f);
        gf.clipSizeX = 12;
        gf.clipSizeY = 14;
        idleFrames = new int[]{0, 1};
        speedX = 350;
        showOnly = true;
        facingRight = false;
    }

    @Override
    protected String getMerchantWindowText() {
        return "Hello, friend! Show me what you want to sell... maybe it's worth my time...";
    }
}