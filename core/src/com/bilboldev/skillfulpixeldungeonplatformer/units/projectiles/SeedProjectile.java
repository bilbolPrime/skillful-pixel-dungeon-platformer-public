package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.Seed;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class SeedProjectile extends ThrownProjectile {

    private Seed seed;

    public SeedProjectile setSeed(Seed seed) {
        this.seed = seed;
        return this;
    }

    @Override
    public SeedProjectile setGameSprite(GameSprite gameSprite) {
        super.setGameSprite(gameSprite);
        this.gs = gameSprite;
        return this;
    }

    @Override
    public void onTerrainCollision() {
        plant(null);
    }

    @Override
    public void onUnitCollision(Unit target) {
        plant(target);
    }

    private void plant(Unit target) {
        if (used) {
            return;
        }

        playSound(Sounds.PLANT, 0.7f);
        EffectsHelper.getInstance().splash(this);

        if (seed != null) {
            seed.land(target != null ? target.x : x, target != null ? target.y : y, room, owner, target);
        }

        markUsed();
    }
}