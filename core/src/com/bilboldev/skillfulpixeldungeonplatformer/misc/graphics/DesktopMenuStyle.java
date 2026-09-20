package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;


public final class DesktopMenuStyle {
    public static final Color GOLD = Color.valueOf("C9B986"), VIOLET = Color.valueOf("AE94BA");
    public static final Color INK = Color.valueOf("DFE0CF"), EDGE = Color.valueOf("788174");
    private static final Color SHADE = Color.valueOf("111C1B"), STONE = Color.valueOf("343D38");
    private static final Color LIT_STONE = Color.valueOf("444E42"), SHADOW = Color.valueOf("080E10");
    private static final Color LIGHT = Color.valueOf("C4AB70");

    private static final float[] VERTICES = new float[20];
    private static final Vector3 POINTER = new Vector3();
    private static final Matrix4 POINTER_TRANSFORM = new Matrix4();
    private static float hoverPhase;
    private static boolean topModal;

    private DesktopMenuStyle() { }


    public static boolean active() {
        return (SkillfulPixelDungeonPlatformer.getActiveScreen() instanceof TitleScreen
                || WindowHelper.getInstance().desktopPause() != null)
                && SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled()
                && !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
    }

    public static void resetHover() { hoverPhase = 0; topModal = false; }
    public static void actHover(float delta) {
        if (Float.isFinite(delta) && delta > 0 && !GameSettingsHelper.getInstance().isReducedVisualEffects())
            hoverPhase = (hoverPhase + Math.min(.1f, delta) * .28f) % 1f;
    }
    public static void setTopModal(boolean value) { topModal = value; }


    public static boolean hovered(Batch batch, Button button) {
        if (!active() || !button.canClick() || WindowHelper.getInstance().windowOpen() && !topModal) return false;
        POINTER.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        GameHelper.GetSingleton().getUICamera().unproject(POINTER);
        POINTER_TRANSFORM.set(batch.getTransformMatrix()).inv();
        POINTER.mul(POINTER_TRANSFORM);
        return POINTER.x >= button.x && POINTER.x <= button.x + button.getWidth()
                && POINTER.y >= button.y && POINTER.y <= button.y + button.getHeight();
    }

    public static void hover(Batch batch, Button button, Color accent) {
        hover(batch, button, accent, button.x, button.y, button.getWidth(), button.getHeight());
    }


    public static void hover(Batch batch, Button button, Color accent, float x, float y, float width, float height) {
        if (GameSettingsHelper.getInstance().isReducedVisualEffects() || !hovered(batch, button)) return;
        Color parent = batch.getColor();
        x += 4; y += 4; width -= 8; height -= 8;
        for (int row = 0; row < 3; row++) for (int col = 0; col < 16; col++) {
            for (int corner = 0; corner < 4; corner++) {
                float u = (col + (corner < 2 ? 0 : 1)) / 16f;
                float v = (row + (corner == 0 || corner == 3 ? 0 : 1)) / 3f;
                float wave = Math.max(0, MathUtils.sin((u - hoverPhase + v * .14f) * MathUtils.PI2));
                float edge = Math.min(1, Math.min(u, 1 - u) * 12) * Math.min(1, Math.min(v, 1 - v) * 4);
                int offset = corner * 5;
                VERTICES[offset] = x + u * width; VERTICES[offset + 1] = y + v * height;
                VERTICES[offset + 2] = Color.toFloatBits(parent.r * accent.r, parent.g * accent.g,
                        parent.b * accent.b, parent.a * .25f * wave * wave * edge);
                VERTICES[offset + 3] = corner < 2 ? 0 : 1;
                VERTICES[offset + 4] = corner == 0 || corner == 3 ? 1 : 0;
            }
            batch.draw(TextureHelper.GetSingleton().getSolidPixel(), VERTICES, 0, 20);
        }
    }

    public static void fill(Batch batch, float x, float y, float width, float height, Color color, float alpha) {
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * color.r, parent.g * color.g, parent.b * color.b, parent.a * alpha);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y, width, height);
        batch.setPackedColor(packed);
    }

    public static void shade(Batch batch, float x, float y, float width, float height, float edge, float alpha) {
        wash(batch, x, y, width, height, edge, SHADE, alpha);
    }


    private static void wash(Batch batch, float x, float y, float width, float height, float edge, Color color, float alpha) {
        edge = Math.min(edge, Math.min(width, height) / 2f);
        Color parent = batch.getColor();
        float clear = Color.toFloatBits(parent.r * color.r, parent.g * color.g, parent.b * color.b, 0f);
        float center = Color.toFloatBits(parent.r * color.r, parent.g * color.g, parent.b * color.b, parent.a * alpha);
        for (int row = 0; row < 3; row++) {
            float bottom = row == 0 ? 0 : row == 1 ? edge : height - edge;
            float top = row == 0 ? edge : row == 1 ? height - edge : height;
            for (int col = 0; col < 3; col++) {
                float left = col == 0 ? 0 : col == 1 ? edge : width - edge;
                float right = col == 0 ? edge : col == 1 ? width - edge : width;
                for (int corner = 0; corner < 4; corner++) {
                    float dx = corner < 2 ? left : right, dy = corner == 0 || corner == 3 ? bottom : top;
                    int offset = corner * 5;
                    VERTICES[offset] = x + dx; VERTICES[offset + 1] = y + dy;
                    VERTICES[offset + 2] = dx == 0 || dx == width || dy == 0 || dy == height ? clear : center;
                    VERTICES[offset + 3] = corner < 2 ? 0 : 1;
                    VERTICES[offset + 4] = corner == 0 || corner == 3 ? 1 : 0;
                }
                batch.draw(TextureHelper.GetSingleton().getSolidPixel(), VERTICES, 0, 20);
            }
        }
    }


    public static void card(Batch batch, float x, float y, float width, float height, Color accent, boolean selected) {
        fill(batch, x + 4, y - 4, width - 8, height, SHADOW, .65f);
        Color face = selected ? LIT_STONE : STONE;
        fill(batch, x + 4, y, width - 8, height, face, .94f);
        fill(batch, x, y + 4, 4, height - 8, face, .94f);
        fill(batch, x + width - 4, y + 4, 4, height - 8, face, .94f);
        fill(batch, x + 8, y + height - 4, width - 16, 2, EDGE, selected ? .9f : .45f);
        fill(batch, x + 8, y + 2, width - 16, 2, SHADOW, .7f);
        corners(batch, x, y, width, height, accent, selected ? 1f : .6f);
    }

    public static void corners(Batch batch, float x, float y, float width, float height, Color accent, float alpha) {
        float length = Math.min(20, Math.min(width, height) / 4f);
        for (int i = 0; i < 4; i++) {
            boolean right = i >= 2, top = i % 2 == 1;
            float cornerX = right ? x + width - 4 : x, cornerY = top ? y + height - 4 : y;
            fill(batch, right ? cornerX - length + 4 : cornerX, cornerY, length, 4, accent, alpha);
            fill(batch, cornerX, top ? cornerY - length + 4 : cornerY, 4, length, accent, alpha);
            fill(batch, right ? cornerX - 4 : cornerX + 4, top ? cornerY - 4 : cornerY + 4, 3, 3, EDGE, alpha);
        }
    }

    public static void rule(Batch batch, float x, float y, float width, Color accent) {
        fill(batch, x, y, width, 2, accent, .3f);
        fill(batch, x, y, Math.min(width, 72), 2, accent, .75f);
        fill(batch, x + width - 4, y - 2, 6, 6, accent, .6f);
    }

    public static void window(Batch batch, float x, float y, float width, float height) {
        shade(batch, x - 40, y - 48, width + 80, height + 80, 48, .6f);
        wash(batch, x, y, width, height, 12, STONE, .96f);
        wash(batch, x + 12, y + 12, width - 24, height - 24, 40, SHADE, .32f);
        fill(batch, x + 16, y + height - 6, width - 32, 3, EDGE, .7f);
        fill(batch, x + 16, y + 3, width - 32, 2, EDGE, .45f);
        corners(batch, x, y, width, height, GOLD, .9f);
        rule(batch, x + 40, y + height - 24, 120, GOLD);
    }

    public static void checkbox(Batch batch, float x, float y, boolean checked) {
        card(batch, x, y, 52, 52, checked ? GOLD : EDGE, false);
        fill(batch, x + 10, y + 10, 32, 32, SHADOW, .65f);
        if (checked) {
            fill(batch, x + 16, y + 16, 20, 20, GOLD, 1f);
            fill(batch, x + 20, y + 20, 12, 12, INK, .85f);
        }
    }


    public static void heroLight(Batch batch, float x, float y, float alpha) {
        if (alpha <= 0) return;
        wash(batch, x - 188, y - 24, 376, 268, 124, LIGHT, .12f * alpha);
        wash(batch, x - 164, y - 20, 328, 48, 24, LIGHT, .18f * alpha);
        fill(batch, x - 116, y - 12, 232, 10, SHADOW, .4f * alpha);
        fill(batch, x - 112, y - 6, 224, 6, EDGE, .65f * alpha);
        fill(batch, x - 100, y - 2, 200, 2, GOLD, .55f * alpha);
    }
}
