package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;


final class MenuScenePainter {
    private static final Color SHADOW = Color.valueOf("080C10");
    private static final Color IRON = Color.valueOf("232A2C"), IRON_EDGE = Color.valueOf("58605C");
    private final Texture wall, raised, door, pixel, light;
    private final MenuStoneBackdrop base;
    private final int faceY, faceHeight, sideWidth;
    private final boolean caves;
    private float time;

    MenuScenePainter(TitleThemeOption theme) {
        String name = theme == TitleThemeOption.HALL ? "halls" : theme.name().toLowerCase(java.util.Locale.ROOT);
        TextureHelper textures = TextureHelper.GetSingleton();
        wall = textures.getTexture("images/tiles/" + name + "/wall.png");
        raised = theme == TitleThemeOption.SEWERS ? null : textures.getTexture("images/tiles/" + name + "/raised.png");
        door = textures.getTexture("images/tiles/" + name + "/door.png");
        pixel = textures.getSolidPixel(); light = textures.getSoftLightTexture();
        base = new MenuStoneBackdrop(theme);
        caves = theme == TitleThemeOption.CAVES;
        faceY = caves ? 112 : 113;
        faceHeight = caves || theme == TitleThemeOption.HALL ? 6 : 5;
        sideWidth = theme == TitleThemeOption.HALL ? 4 : 3;
    }

    void act(float delta) { time = (time + delta) % 3600f; base.act(delta); }
    void base(Batch batch, OrthographicCamera camera) { base.draw(batch, camera); }

    void wall(Batch batch, float x, float y, float width, float height, float tile, Color tint) {
        batch.setColor(tint);
        float right = x + width, top = y + height;
        for (float row = (float) Math.floor(y / tile) * tile; row < top; row += tile) {
            for (float col = (float) Math.floor(x / tile) * tile; col < right; col += tile) {
                float atX = Math.max(col, x), atY = Math.max(row, y);
                float w = Math.min(right, col + tile) - atX, h = Math.min(top, row + tile) - atY;
                batch.draw(wall, atX, atY, w, h, (atX - col) / tile, 1f - (atY - row) / tile,
                        (atX + w - col) / tile, 1f - (atY + h - row) / tile);
            }
        }
    }

    void ledge(Batch batch, float x, float surface, float width, float tile, Color tint) {
        float nativePixel = tile / 16f;
        rect(batch, x - nativePixel, surface - faceHeight * nativePixel - nativePixel,
                width + 2f * nativePixel, nativePixel * 2f, SHADOW, 0.8f);
        if (raised == null) { wall(batch, x, surface - tile / 4f, width, tile / 4f, tile, tint); return; }
        batch.setColor(tint);
        for (float at = x; at < x + width; at += tile) {
            float w = Math.min(tile, x + width - at);
            batch.draw(raised, at, surface - faceHeight * nativePixel, w, faceHeight * nativePixel,
                    0f, (faceY + faceHeight) / (float) raised.getHeight(),
                    w / nativePixel / raised.getWidth(), faceY / (float) raised.getHeight());
        }
    }

    void pier(Batch batch, float x, float bottom, float width, float height, Color tint, Color edge) {
        rect(batch, x + 12f, bottom - 8f, width + 12f, height, SHADOW, 0.8f);
        wall(batch, x, bottom, width, height, 128f, tint);
        float side = sideWidth * 8f;
        if (raised != null) {
            batch.setColor(tint.r * 0.55f, tint.g * 0.55f, tint.b * 0.55f, 1f);
            for (float y = bottom; y < bottom + height; y += 128f) {
                float h = Math.min(128f, bottom + height - y);
                batch.draw(raised, x + width - side, y, side, h, 0f, (112f + h / 8f) / raised.getHeight(),
                        sideWidth / (float) raised.getWidth(), 112f / raised.getHeight());
            }
        }
        rect(batch, x, bottom, 6f, height, edge, 0.65f);
        ledge(batch, x - 16f, bottom + height + 24f, width + 32f, 128f, tint);
        ledge(batch, x - 8f, bottom + 24f, width + 16f, 128f, tint);
    }

    void doorway(Batch batch, float x, float y, float tile, Color tint) {
        rect(batch, x - 6f, y - 4f, tile + 12f, tile + 12f, SHADOW, 0.9f);
        batch.setColor(tint); batch.draw(door, x, y, tile, tile);
    }

    void bars(Batch batch, float x, float y, float width, float height, float spacing) {
        for (float at = x; at <= x + width; at += spacing) {
            rect(batch, at, y, 8f, height, IRON, 1f);
            rect(batch, at + 2f, y, 2f, height, IRON_EDGE, 0.8f);
        }
        rect(batch, x, y + height * 0.24f, width + 8f, 8f, IRON, 1f);
        rect(batch, x, y + height * 0.73f, width + 8f, 8f, IRON, 1f);
    }

    void lamp(Batch batch, float x, float y, Color glow, float size) {
        boolean reduced = GameSettingsHelper.getInstance().isReducedVisualEffects();
        float flicker = reduced ? 1f : 0.94f + 0.06f * MathUtils.sin(time * 3.1f + x);
        batch.setColor(glow.r, glow.g, glow.b, (reduced ? 0.11f : 0.22f) * flicker);
        batch.draw(light, x - size * 1.5f, y - size * 1.5f, size * 3f, size * 3f);
        rect(batch, x - 16f, y - 28f, 32f, 56f, IRON, 1f);
        rect(batch, x - 8f, y - 16f, 16f, 32f, glow, flicker);
        rect(batch, x - 20f, y + 24f, 40f, 8f, IRON_EDGE, 1f);
        rect(batch, x - 20f, y - 28f, 40f, 8f, IRON_EDGE, 1f);
    }

    void rect(Batch batch, float x, float y, float width, float height, Color tint, float alpha) {
        batch.setColor(tint.r, tint.g, tint.b, alpha);
        batch.draw(pixel, x, y, width, height);
    }


    void heroLanding(Batch batch, Color stone, Color floor, Color edge) {
        rect(batch, 104, 360, 1384, 272, SHADOW, 0.65f);
        wall(batch, 120, 416, 1344, 208, 128, floor);
        ledge(batch, 112, 624, 1360, 128, stone);
        ledge(batch, 112, 448, 1360, 128, stone);
        for (int i = 0; i < 6; i++) {
            float center = MenuHeroRoster.FIRST_X + i * MenuHeroRoster.SPACING;
            rect(batch, center - 56, 580, 112, 4, edge, 0.4f);
            rect(batch, center + MenuHeroRoster.STEP_X - 56, 468, 112, 4, edge, 0.28f);
        }
    }

    void heroInfoFrame(Batch batch, Color stone, Color edge, Color glow) {
        pier(batch, 1504, 288, 48, 592, stone, edge);
        pier(batch, 2352, 288, 48, 592, stone, edge);
        ledge(batch, 1488, 920, 928, 128, stone);
        lamp(batch, 2384, 736, glow, 88);
    }

    boolean clip(Batch batch, OrthographicCamera camera, Rectangle bounds, Rectangle result) {
        batch.flush(); ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), bounds, result);
        return ScissorStack.pushScissors(result);
    }
    void endClip(Batch batch) { batch.flush(); ScissorStack.popScissors(); }
}
