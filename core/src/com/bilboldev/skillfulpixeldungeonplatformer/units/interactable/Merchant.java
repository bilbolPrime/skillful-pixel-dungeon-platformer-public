package com.bilboldev.skillfulpixeldungeonplatformer.units.interactable;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.MerchantWindow;

public class Merchant extends Interactable {
    private static final String DEFAULT_TRADE_TEXT = "Welcome to my shop. Show me what you want to sell.. maybe it's worth my time..";

    {
        gs = new GameSprite("images/units/merchant/single.png",16, 16, 1f);
        hp = mhp = 1000;
        gf = new GameFilm("images/units/merchant/merchant.png",32, 16, 1f);
        gf.clipSizeX = 14;
        gf.clipSizeY = 15;
        idleFrames = new int[]{0, 1};
        speedX = 350;
        showOnly = true;
        facingRight = false;
    }

    @Override
    public void interact(){
        WindowHelper.getInstance().addWindow(new MerchantWindow(
                UnitHelper.getInstance().getHero().getHeroClass(),
                2000,
                1000,
                getMerchantWindowPortrait(),
                getMerchantWindowText())
                .build());
    }

    protected GameSprite getMerchantWindowPortrait() {
        return getInteractGS() == null ? null : getInteractGS().clone();
    }

    protected String getMerchantWindowText() {
        return DEFAULT_TRADE_TEXT;
    }
}

