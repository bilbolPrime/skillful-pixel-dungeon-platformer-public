package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class HuntressArrowProjectile extends ThrownProjectile {
    {
        setGameSprite(new GameSprite("images/misc/extracted items/Arrow.png", 45, 45));
        setSpeedY(10f);
    }
}
