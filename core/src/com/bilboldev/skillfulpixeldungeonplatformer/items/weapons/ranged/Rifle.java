package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile;

public final class Rifle extends Gun {
    {
        name = "Rifle";
        description = "A powerful, precise firearm. Fires one straight bullet with 50% greater accuracy, at a slower rate.";
        gs = new GameSprite(NewClassAssets.ItemArt.RIFLE.key(), 45, 45);
        tier = 4;
        speed = .85f;
        goldCost = 100;
    }

    @Override protected GunProjectile buildProjectile(Hero hero) {
        GunProjectile shot = super.buildProjectile(hero);

        shot.setAccuracyMultiplier(shot.getAccuracyMultiplier() * 1.5f);
        return shot;
    }
}
