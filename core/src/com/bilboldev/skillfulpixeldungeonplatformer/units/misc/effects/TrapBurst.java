package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class TrapBurst extends Effect {
    private final ArrayList<Particle> particles = new ArrayList<Particle>();
    private float gravityScale;

    public TrapBurst init(float x, float y, String spritePath, float particleSize, int count, float spread, float rise, float lifeSpan, float gravityScale) {
        this.x = x;
        this.y = y;
        this.lifeSpan = lifeSpan;
        this.gravityScale = gravityScale;
        this.gs = new GameSprite(spritePath, particleSize, particleSize, 0.85f);
        particles.clear();

        for (int i = 0; i < count; i++) {
            particles.add(new Particle(
                    RandomHelper.getInstance().randomFloat(32f) - 16f,
                    RandomHelper.getInstance().randomFloat(20f) - 10f,
                    RandomHelper.getInstance().randomFloat(spread * 2f) - spread,
                    rise + RandomHelper.getInstance().randomFloat(spread) - spread / 2f));
        }

        return this;
    }

    @Override
    public void act(float delta) {
        lifeSpan = Math.max(0f, lifeSpan - 30f * delta);

        if (gs != null) {
            gs.setAlpha(lifeSpan / 100f);
        }

        for (Particle particle : particles) {
            particle.x += particle.speedX * delta;
            particle.y += particle.speedY * delta;
            particle.speedX -= particle.speedX * 1.8f * delta;
            particle.speedY -= ConstantsHelper.GRAVITY * gravityScale * delta;
        }
    }

    @Override
    void gravity(float delta) {
    }

    @Override
    public void draw(Batch batch) {
        if (gs == null) {
            return;
        }

        for (Particle particle : particles) {
            gs.setPosition(x + particle.x, y + particle.y);
            gs.draw(batch);
        }
    }

    private static final class Particle {
        private float x;
        private float y;
        private float speedX;
        private float speedY;

        private Particle(float x, float y, float speedX, float speedY) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
        }
    }
}