package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class BeamEffect extends Effect {
    public BeamEffect init(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.lifeSpan = 20f;
        this.gs = new GameSprite("images/misc/beam.png", width, height, 0.95f);
        this.gs.setPosition(x, y);
        return this;
    }

    @Override
    public void act(float delta) {
        lifeSpan = Math.max(0f, lifeSpan - 120f * delta);

        if (gs != null) {
            gs.setAlpha(lifeSpan / 20f);
            gs.setPosition(x, y);
        }
    }

    @Override
    void gravity(float delta) {
    }
}