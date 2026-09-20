package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;


public final class SewersTitleScene implements MenuScene {
    private static final Color WALL = new Color(0.31f, 0.40f, 0.40f, 1f);
    private static final Color INNER_WALL = new Color(0.22f, 0.30f, 0.29f, 1f);
    private static final Color FAR_WALL = new Color(0.16f, 0.23f, 0.22f, 1f);
    private static final Color BLACK = Color.valueOf("0A1012");
    private static final Color EDGE = Color.valueOf("465653");
    private static final Color PILLAR_SIDE = Color.valueOf("192627");
    private static final Color IRON = Color.valueOf("283B39");
    private static final Color IRON_LIGHT = Color.valueOf("465A50");
    private static final Color MOSS = Color.valueOf("354334");
    private static final Color WATER = Color.valueOf("112A29");
    private static final Color WATER_LIGHT = Color.valueOf("3D6257");
    private static final Color WATER_DROP = Color.valueOf("719F9D");
    private static final Color WATER_GLINT = Color.valueOf("AAC5BB");
    private static final Color LAMP = Color.valueOf("D6AD61");
    private static final Color GNOLL_TINT = new Color(0.48f, 0.59f, 0.51f, 1f);
    private static final Rectangle BREACH = new Rectangle(1120f, 288f, 1152f, 752f);
    private static final Rectangle PASSAGE = new Rectangle(1544f, 688f, 600f, 272f);

    private static final Rectangle WALL_DRAIN = new Rectangle(2304f, 512f, 128f, 128f);
    private static final Rectangle ROOM_DRAIN = new Rectangle(1248f, 768f, 96f, 96f);


    private final Texture wall = texture("images/tiles/sewers/wall.png");
    private final Texture platform = texture("images/tiles/sewers/platform.png");
    private final Texture drain = texture("images/tiles/sewers/decoration.png");
    private final Texture door = texture("images/tiles/sewers/door.png");
    private final Texture fire = texture("images/intro/fireball-front.png");
    private final Texture pixel = TextureHelper.GetSingleton().getSolidPixel();
    private final Texture light = TextureHelper.GetSingleton().getSoftLightTexture();
    private final Rectangle scissors = new Rectangle();
    private final Rectangle passageScissors = new Rectangle();
    private final SewersTitleActors actors = new SewersTitleActors();
    private double elapsed;
    private float time;
    private boolean reducedEffects;

    public void act(float delta) {

        if (Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0f) return;
        float step = Math.min(delta, 0.1f);
        elapsed += step;
        actors.act(step);
        time = (float) (elapsed % 3600.0);
    }

    public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        reducedEffects = GameSettingsHelper.getInstance().isReducedVisualEffects();
        float left = camera.position.x - camera.viewportWidth / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float right = left + camera.viewportWidth;
        float top = bottom + camera.viewportHeight;
        try {
            masonry(batch, left, bottom, camera.viewportWidth, camera.viewportHeight, 128f, WALL);

            if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) {
                drawBreach(batch, camera);
                drawBrokenReturns(batch);
            }

            drawPipe(batch, 1040f, 656f, top);
            drawDrain(batch, WALL_DRAIN, WALL);
            lamp(batch, 1016f, 776f, 1f);
            lamp(batch, 2336f, 984f, 1f);


            masonry(batch, left, 208f, camera.viewportWidth, 80f, 128f, WALL);
            rect(batch, left, 280f, camera.viewportWidth, 8f, EDGE, 1f);
            rect(batch, left, 200f, camera.viewportWidth, 16f, BLACK, 1f);

            water(batch, left, 128f, camera.viewportWidth, 72f, 1f, 30f);
            rect(batch, left, 120f, camera.viewportWidth, 8f, EDGE, 0.75f);
            rect(batch, left, 112f, camera.viewportWidth, 8f, BLACK, 1f);
            moss(batch, 1120f, 280f, 216f);
            moss(batch, 2024f, 280f, 280f);
            moss(batch, 1408f, 120f, 224f);
            moss(batch, 2200f, 120f, 216f);
            drip(batch, 1112f, 656f, 192f, 0f);
            drainDrops(batch, WALL_DRAIN, 192f, 0.37f);
            if (!reducedEffects) {
                for (int i = 0; i < 12; i++) {
                    float x = 1152f + (i * 137f + time * (3f + i % 3)) % 1224f;
                    float y = 336f + (i * 83f + time * 5f) % 680f;
                    rect(batch, x, y, 3f, 3f, WATER_LIGHT, 0.25f);
                }
            }

            for (int i = 0; i < 16; i++) {
                float alpha = (16 - i) / 50f;
                rect(batch, left, top - (i + 1) * 16f, right - left, 16f, BLACK, alpha);
                rect(batch, left, bottom + i * 16f, right - left, 16f, BLACK, alpha);
            }
        } finally {
            batch.setPackedColor(packed);
        }
    }

    private void drawBreach(Batch batch, OrthographicCamera camera) {
        if (!clip(batch, camera, BREACH, scissors)) return;
        try {
            masonry(batch, BREACH.x, BREACH.y, BREACH.width, BREACH.height, 96f, INNER_WALL);

            rect(batch, 1528f, 672f, 632f, 304f, BLACK, 1f);
            if (clip(batch, camera, PASSAGE, passageScissors)) {
                try {
                    masonry(batch, PASSAGE.x, PASSAGE.y, PASSAGE.width, PASSAGE.height, 64f, FAR_WALL);
                    sprite(batch, door, 2028f, 692f, 112f, 168f, GNOLL_TINT);
                    lamp(batch, 1936f, 856f, 0.55f);
                    actors.draw(batch, true);
                } finally {
                    endClip(batch);
                }
            }

            masonry(batch, 1512f, 672f, 56f, 304f, 96f, INNER_WALL);
            masonry(batch, 2120f, 672f, 56f, 304f, 96f, INNER_WALL);
            masonry(batch, 1568f, 920f, 64f, 56f, 96f, INNER_WALL);
            masonry(batch, 2056f, 928f, 64f, 48f, 96f, INNER_WALL);
            ledge(batch, 1480f, 688f, 712f, 0.65f);
            ledge(batch, 1272f, 600f, 192f, 0.64f);

            drawDrain(batch, ROOM_DRAIN, INNER_WALL);
            water(batch, BREACH.x, BREACH.y, BREACH.width, 96f, 0.7f, 12f);
            ledge(batch, 1088f, 456f, 448f, 0.9f);
            ledge(batch, 1728f, 456f, 608f, 0.9f);

            drainDrops(batch, ROOM_DRAIN, 600f, 0.18f);
            drip(batch, 1452f, 568f, 456f, 0.56f);
            drip(batch, 1496f, 424f, 376f, 0.81f);
            actors.draw(batch, false);

            rect(batch, 2224f, 288f, 12f, 312f, BLACK, 0.5f);
            masonry(batch, 2152f, 288f, 72f, 320f, 96f, INNER_WALL);

            rect(batch, 2152f, 288f, 6f, 312f, EDGE, 0.6f);
            rect(batch, 2212f, 288f, 12f, 312f, PILLAR_SIDE, 1f);
            rect(batch, 2152f, 600f, 72f, 8f, EDGE, 1f);
            moss(batch, 1768f, 448f, 208f);
        } finally {
            endClip(batch);
        }
    }

    private void drawBrokenReturns(Batch batch) {

        rect(batch, 1120f, 288f, 16f, 752f, BLACK, 1f);
        rect(batch, 1120f, 1024f, 1152f, 16f, BLACK, 1f);
        rect(batch, 2256f, 288f, 16f, 752f, BLACK, 1f);
        stoneReturn(batch, 1120f, 968f, 224f, 72f);
        stoneReturn(batch, 1120f, 904f, 120f, 64f);
        stoneReturn(batch, 1120f, 808f, 48f, 96f);
        stoneReturn(batch, 2104f, 976f, 168f, 64f);
        stoneReturn(batch, 2208f, 896f, 64f, 80f);
        stoneReturn(batch, 1120f, 288f, 152f, 64f);
        stoneReturn(batch, 2200f, 288f, 72f, 80f);
        moss(batch, 1328f, 1032f, 224f);
        moss(batch, 2184f, 896f, 88f);

        rect(batch, 1336f, 1040f, 8f, 56f, BLACK, 0.8f);
        rect(batch, 1344f, 1088f, 40f, 8f, BLACK, 0.8f);
        rect(batch, 1384f, 1088f, 8f, 40f, BLACK, 0.8f);
        rect(batch, 2256f, 856f, 56f, 8f, BLACK, 0.8f);
        rect(batch, 2312f, 824f, 8f, 40f, BLACK, 0.8f);
    }

    private void stoneReturn(Batch batch, float x, float y, float width, float height) {
        rect(batch, x - 8f, y - 16f, width + 24f, height + 16f, BLACK, 1f);
        masonry(batch, x, y, width, height, 128f, WALL);
        rect(batch, x, y, width, 8f, EDGE, 0.55f);
    }

    private void ledge(Batch batch, float x, float surfaceY, float width, float brightness) {
        batch.setColor(brightness * 0.65f, brightness * 0.75f, brightness * 0.7f, 1f);
        for (float at = x; at < x + width; at += 96f) {
            float size = Math.min(96f, x + width - at);
            batch.draw(platform, at, surfaceY - 96f, size, 96f, 0f, 1f, size / 96f, 0f);
        }
        rect(batch, x, surfaceY - 8f, width, 8f, EDGE, 1f);
        masonry(batch, x, surfaceY - 24f, width, 16f, 96f, INNER_WALL);
        rect(batch, x, surfaceY - 32f, width, 8f, BLACK, 1f);
    }

    private void drawPipe(Batch batch, float x, float outletY, float top) {
        rect(batch, x - 8f, outletY - 8f, 64f, top - outletY + 8f, BLACK, 1f);
        rect(batch, x, outletY, 40f, top - outletY, IRON, 1f);
        rect(batch, x + 8f, outletY + 24f, 8f, top - outletY - 24f, IRON_LIGHT, 1f);
        for (float y = outletY + 112f; y < top; y += 192f) {
            rect(batch, x - 8f, y, 56f, 16f, BLACK, 1f);
            rect(batch, x - 8f, y + 8f, 56f, 8f, IRON_LIGHT, 1f);
        }
        rect(batch, x, outletY, 88f, 40f, IRON, 1f);
        rect(batch, x + 8f, outletY + 32f, 80f, 8f, IRON_LIGHT, 1f);
        rect(batch, x + 56f, outletY - 8f, 40f, 16f, BLACK, 1f);
    }

    private void drawDrain(Batch batch, Rectangle tile, Color tint) {
        sprite(batch, drain, tile.x, tile.y, tile.width, tile.height, tint);
    }

    private void drainDrops(Batch batch, Rectangle tile, float targetY, float offset) {

        drip(batch, tile.x + tile.width * 8f / 16f, tile.y + tile.height * 7f / 16f, targetY, offset);
    }

    private void lamp(Batch batch, float x, float y, float scale) {
        float flicker = reducedEffects ? 1f : 0.93f + 0.04f * MathUtils.sin(time * 2.3f + x)
                + 0.03f * MathUtils.sin(time * 5.1f + y);
        batch.setColor(0.72f, 0.55f, 0.27f, (reducedEffects ? 0.13f : 0.28f) * flicker);
        batch.draw(light, x - 184f * scale, y - 192f * scale, 400f * scale, 416f * scale);
        rect(batch, x - 8f * scale, y - 32f * scale, 48f * scale, 64f * scale, BLACK, 1f);
        rect(batch, x, y - 24f * scale, 32f * scale, 48f * scale, IRON_LIGHT, 1f);
        rect(batch, x + 8f * scale, y - 16f * scale, 16f * scale, 32f * scale, LAMP, flicker);
        batch.setColor(1f, 0.85f, 0.56f, 0.65f * flicker);
        batch.draw(fire, x - 8f * scale, y - 24f * scale, 48f * scale, 48f * scale);
        rect(batch, x - 8f * scale, y + 24f * scale, 48f * scale, 8f * scale, IRON, 1f);
        rect(batch, x - 8f * scale, y - 32f * scale, 48f * scale, 8f * scale, IRON, 1f);
    }

    private void moss(Batch batch, float x, float y, float width) {
        for (int i = 0; i < 9; i++) {
            float at = x + i * width / 9f;
            rect(batch, snap(at), y - (i % 3) * 8f, width / 12f, 8f + (i % 4) * 8f, MOSS, 0.85f);
        }
    }

    private void water(Batch batch, float x, float y, float width, float height, float alpha, float flowSpeed) {
        rect(batch, x, y, width, height, WATER, alpha);
        rect(batch, x, y + height - 8f, width, 8f, WATER_LIGHT, 0.28f);
        double flow = elapsed * flowSpeed * (reducedEffects ? 0.3f : 1f);
        for (int i = 0; i < 30; i++) {
            float length = 24f + (i % 5) * 16f;

            float at = (float) ((i * 173f + flow * (1f + i % 3 * 0.1f)) % (width + length)) - length;
            float start = Math.max(0f, at), end = Math.min(width, at + length);
            float down = 16f + (i * 31f) % Math.max(1f, height - 24f);
            if (end > start) rect(batch, x + start, y + height - down, end - start, 4f,
                    WATER_LIGHT, 0.2f + (i % 3) * 0.05f);
        }
    }

    private void drip(Batch batch, float x, float sourceY, float waterY, float offset) {
        int drops = reducedEffects ? 2 : 4;
        float fallDistance = sourceY - waterY;
        float cycle = 0.55f + (float) Math.sqrt(fallDistance) * 0.035f;
        float intensity = reducedEffects ? 0.55f : 1f;
        for (int i = 0; i < drops; i++) {
            float phase = ((time + offset) / cycle + i / (float) drops) % 1f;
            float atX = x + (i % 2 == 0 ? -2f : 2f);
            if (phase < 0.8f) {
                float fall = phase / 0.8f;
                float head = sourceY - fallDistance * (0.18f * fall + 0.82f * fall * fall);
                float length = Math.min(sourceY - head, 8f + 16f * fall);
                rect(batch, atX - 3f, snap(head), 6f, length, WATER_DROP, 0.8f * intensity);
                rect(batch, atX - 3f, snap(head), 6f, Math.min(4f, length), WATER_GLINT, 0.75f * intensity);
            } else {
                float spread = (phase - 0.8f) / 0.2f;
                float radius = 4f + spread * 24f;
                float alpha = (1f - spread) * intensity;
                rect(batch, x - radius, waterY + 4f * MathUtils.sin(spread * MathUtils.PI), 6f, 4f, WATER_DROP, alpha);
                rect(batch, x + radius, waterY + 4f * MathUtils.sin(spread * MathUtils.PI), 6f, 4f, WATER_DROP, alpha);
                rect(batch, x - radius, waterY, radius * 2f, 3f, WATER_GLINT, alpha * 0.45f);
            }
        }
    }

    private boolean clip(Batch batch, OrthographicCamera camera, Rectangle bounds, Rectangle result) {
        batch.flush();
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), bounds, result);
        return ScissorStack.pushScissors(result);
    }

    private void endClip(Batch batch) {
        batch.flush();
        ScissorStack.popScissors();
    }

    private void masonry(Batch batch, float x, float y, float width, float height, float tile, Color tint) {
        batch.setColor(tint);
        float right = x + width, top = y + height;
        for (float row = (float) Math.floor(y / tile) * tile; row < top; row += tile) {
            for (float col = (float) Math.floor(x / tile) * tile; col < right; col += tile) {
                float left = Math.max(x, col), bottom = Math.max(y, row);
                float w = Math.min(right, col + tile) - left, h = Math.min(top, row + tile) - bottom;
                batch.draw(wall, left, bottom, w, h, (left - col) / tile, 1f - (bottom - row) / tile,
                        (left + w - col) / tile, 1f - (bottom + h - row) / tile);
            }
        }
    }

    private void sprite(Batch batch, Texture texture, float x, float y, float width, float height, Color tint) {
        batch.setColor(tint);
        batch.draw(texture, x, y, width, height);
    }

    private void rect(Batch batch, float x, float y, float width, float height, Color tint, float alpha) {
        batch.setColor(tint.r, tint.g, tint.b, alpha);
        batch.draw(pixel, x, y, width, height);
    }

    private static Texture texture(String path) { return TextureHelper.GetSingleton().getTexture(path); }
    private static float snap(float value) { return Math.round(value / 2f) * 2f; }
}
