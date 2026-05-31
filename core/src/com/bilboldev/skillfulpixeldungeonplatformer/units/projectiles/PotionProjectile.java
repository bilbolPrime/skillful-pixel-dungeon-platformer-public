package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.ManaPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Potion;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class PotionProjectile extends ThrownProjectile {

    private Potion potion;

    public PotionProjectile setPotion(Potion potion) {
        this.potion = potion;
        return this;
    }

    @Override
    public PotionProjectile setGameSprite(GameSprite gameSprite) {
        super.setGameSprite(gameSprite);
        this.gs = gameSprite;
        return this;
    }

    @Override
    public void onTerrainCollision() {
        shatter(null);
    }

    @Override
    public void onUnitCollision(Unit target) {
        shatter(target);
    }

    private void shatter(Unit target) {
        if (used) {
            return;
        }

        playSound(Sounds.SHATTER, 0.6f);
        EffectsHelper.getInstance().splash(this, getSplashColor());

        if (potion != null) {
            potion.shatter(target != null ? target.x : x, target != null ? target.y : y, room, owner, target);
        }

        markUsed();
    }

    private Color getSplashColor() {
        if (potion instanceof HealthPotion) {
            return new Color(0.86f, 0.18f, 0.22f, 1f);
        }
        if (potion instanceof ManaPotion) {
            return new Color(0.16f, 0.24f, 0.68f, 1f);
        }

        String spritePath = gs != null ? gs.spriteString : null;
        if (spritePath == null) {
            return Color.WHITE;
        }

        if (spritePath.contains("POTION_AMBER")) {
            return new Color(0.95f, 0.62f, 0.18f, 1f);
        }
        if (spritePath.contains("POTION_AZURE")) {
            return new Color(0.29f, 0.65f, 0.94f, 1f);
        }
        if (spritePath.contains("POTION_BISTRE")) {
            return new Color(0.45f, 0.31f, 0.19f, 1f);
        }
        if (spritePath.contains("POTION_CHARCOAL")) {
            return new Color(0.22f, 0.24f, 0.28f, 1f);
        }
        if (spritePath.contains("POTION_CRIMSON")) {
            return new Color(0.82f, 0.15f, 0.27f, 1f);
        }
        if (spritePath.contains("POTION_GOLDEN")) {
            return new Color(0.96f, 0.78f, 0.2f, 1f);
        }
        if (spritePath.contains("POTION_INDIGO")) {
            return new Color(0.39f, 0.31f, 0.86f, 1f);
        }
        if (spritePath.contains("POTION_IVORY")) {
            return new Color(0.95f, 0.93f, 0.82f, 1f);
        }
        if (spritePath.contains("POTION_JADE")) {
            return new Color(0.22f, 0.78f, 0.48f, 1f);
        }
        if (spritePath.contains("POTION_MAGENTA")) {
            return new Color(0.86f, 0.26f, 0.69f, 1f);
        }
        if (spritePath.contains("POTION_SILVER")) {
            return new Color(0.8f, 0.84f, 0.89f, 1f);
        }
        if (spritePath.contains("POTION_TURQUOISE")) {
            return new Color(0.27f, 0.86f, 0.82f, 1f);
        }

        return Color.WHITE;
    }
}