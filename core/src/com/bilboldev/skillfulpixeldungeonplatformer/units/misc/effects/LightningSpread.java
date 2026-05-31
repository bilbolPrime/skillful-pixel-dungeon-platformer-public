package com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

import java.util.ArrayList;

public class LightningSpread extends Effect {
    private static final float SPRITE_WIDTH = 42f;
    private static final float SPRITE_HEIGHT = 18f;
    private final ArrayList<Bolt> bolts = new ArrayList<Bolt>();

    {
        gs = new GameSprite("images/misc/lightning.png", SPRITE_WIDTH, SPRITE_HEIGHT, 0.95f);
    }

    @Override
    public Effect init(float x, float y, float rotation, float speedX, float speedY, float speedRotation) {
        super.init(x, y, rotation, speedX, speedY, speedRotation);
        lifeSpan = 38f;
        bolts.clear();

        for (int i = 0; i < 8; i++) {
            float angle = i * 45f + RandomHelper.getInstance().randomFloat(18f) - 9f;
            float radians = (float) Math.toRadians(angle);
            float launchSpeed = 70f + RandomHelper.getInstance().randomFloat(60f);
            bolts.add(new Bolt(
                    RandomHelper.getInstance().randomFloat(10f) - 5f,
                    RandomHelper.getInstance().randomFloat(10f) - 5f,
                    (float) Math.cos(radians) * launchSpeed,
                    (float) Math.sin(radians) * launchSpeed,
                    angle));
        }

        return this;
    }

    @Override
    public void act(float delta) {
        lifeSpan = Math.max(0f, lifeSpan - 45f * delta);

        if (gs != null) {
            gs.setAlpha(lifeSpan / 38f);
        }

        for (Bolt bolt : bolts) {
            bolt.x += bolt.speedX * delta;
            bolt.y += bolt.speedY * delta;
            bolt.speedX -= bolt.speedX * 2.2f * delta;
            bolt.speedY -= bolt.speedY * 2.2f * delta;
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

        for (Bolt bolt : bolts) {
            gs.setRotation(bolt.rotation);
            gs.setPosition(x + bolt.x - gs.getWidth() / 2f, y + bolt.y - gs.getHeight() / 2f);
            gs.draw(batch);
        }
    }

    private static final class Bolt {
        private float x;
        private float y;
        private float speedX;
        private float speedY;
        private final float rotation;

        private Bolt(float x, float y, float speedX, float speedY, float rotation) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.rotation = rotation;
        }
    }
}