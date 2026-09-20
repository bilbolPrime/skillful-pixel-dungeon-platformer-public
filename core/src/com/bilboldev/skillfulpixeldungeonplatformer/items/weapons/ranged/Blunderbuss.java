package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile;

public final class Blunderbuss extends Gun {
    private static final float DAMAGE_MULTIPLIER = 1.5f;

    {
        name = "Blunderbuss";
        description = "Fires three straight pellets in a narrow spread. They share one damage roll and cost one bullet for the entire volley.";
        gs = new GameSprite(NewClassAssets.ItemArt.BLUNDERBUSS.key(), 45, 45);
        tier = 3;
        speed = .80f;
        goldCost = 75;
    }

    @Override protected int projectilesPerShot() { return 3; }


    @Override public float min() { return super.min() * DAMAGE_MULTIPLIER; }
    @Override public float max() { return super.max() * DAMAGE_MULTIPLIER; }
    @Override public float getDamage() { return super.getDamage() * DAMAGE_MULTIPLIER; }

    @Override protected GunProjectile buildProjectile(Hero hero, int index) {
        GunProjectile shot = super.buildProjectile(hero);
        float angle = (index - 1) * 6f;
        shot.speedX = (hero.facingRight ? 1f : -1f) * GunProjectile.SPEED * MathUtils.cosDeg(angle);
        shot.speedY = GunProjectile.SPEED * MathUtils.sinDeg(angle);
        return shot;
    }
}
