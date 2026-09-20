package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;


public final class SpriteTrail {
    private SpriteTrail() { }

    public static void draw(Batch batch, GameSprite sprite, float dx, float dy, float maxLength) {
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 1f) return;
        float limit = Math.min(length, maxLength) / length;
        dx *= limit;
        dy *= limit;
        float x = sprite.getX(), y = sprite.getY(), alpha = sprite.getAlpha();
        float intensity = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.35f : 1f;
        try {
            sprite.setPosition(x - dx, y - dy);
            sprite.setAlpha(alpha * 0.10f * intensity);
            sprite.draw(batch);
            sprite.setPosition(x - dx * 0.45f, y - dy * 0.45f);
            sprite.setAlpha(alpha * 0.22f * intensity);
            sprite.draw(batch);
        } finally {
            sprite.setPosition(x, y);
            sprite.setAlpha(alpha);
        }
    }
}
