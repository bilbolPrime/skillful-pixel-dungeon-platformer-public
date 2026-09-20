package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.MortarProjectile;

public final class Mortar extends Gun {
    {
        name = "Mortar";
        description = "Fires a slower straight shell. Its first impact hits one enemy and blasts up to eight others within 160 units for half damage. Walls block the blast; allies are safe.";
        gs = new GameSprite(NewClassAssets.ItemArt.MORTAR.key(), 45, 45);
        tier = 5;
        speed = .65f;
        goldCost = 125;
    }

    @Override protected GunProjectile buildProjectile(Hero hero) { return new MortarProjectile(hero, this); }
}
