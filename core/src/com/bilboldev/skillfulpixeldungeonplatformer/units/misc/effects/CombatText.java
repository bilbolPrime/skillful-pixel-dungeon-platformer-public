package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;


public final class CombatText extends Effect {
    private String text;
    private final Color color = new Color();
    private GlyphLayout layout;

    public CombatText init(float centerX, float topY, String text, Color color) {
        x = centerX;
        y = topY;
        this.text = text;
        this.color.set(color);
        lifeSpan = 0.8f;
        return this;
    }

    @Override
    public void act(float delta) {
        y += 40f * delta;
        lifeSpan = Math.max(0f, lifeSpan - delta);
    }

    @Override
    public void draw(Batch batch) {
        FontHelper fonts = FontHelper.getSingleton();
        if (layout == null) layout = fonts.measure(color, 3f, text);
        float fade = Math.min(1f, lifeSpan / 0.2f);
        float left = x - layout.width / 2f;
        float packed = batch.getPackedColor();
        try {
            color.a = fade;
            fonts.writeRaw(color, batch, 3f, left, y, text);
        } finally {
            batch.setPackedColor(packed);
        }
    }
}
