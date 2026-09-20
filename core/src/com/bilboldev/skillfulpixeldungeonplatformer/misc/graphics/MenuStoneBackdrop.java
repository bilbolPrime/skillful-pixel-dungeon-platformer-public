package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;


final class MenuStoneBackdrop implements MenuScene {
    private final Texture wall, pixel, raised;
    private final Color tint, floorTint;
    private final boolean sewers;
    private final int faceY, faceHeight;
    private double elapsed;

    MenuStoneBackdrop(TitleThemeOption theme) {
        sewers = theme == TitleThemeOption.SEWERS;
        String name = theme == TitleThemeOption.HALL ? "halls" : theme.name().toLowerCase(java.util.Locale.ROOT);
        wall = TextureHelper.GetSingleton().getTexture("images/tiles/" + name + "/wall.png");
        raised = sewers ? null : TextureHelper.GetSingleton().getTexture("images/tiles/" + name + "/raised.png");
        pixel = TextureHelper.GetSingleton().getSolidPixel();
        faceY = theme == TitleThemeOption.CAVES ? 112 : 113;
        faceHeight = theme == TitleThemeOption.CAVES || theme == TitleThemeOption.HALL ? 6 : 5;
        switch (theme) {
            case PRISON: tint = new Color(0.39f, 0.43f, 0.46f, 1f); floorTint = new Color(0.48f, 0.51f, 0.53f, 1f); break;
            case CAVES: tint = new Color(0.34f, 0.40f, 0.39f, 1f); floorTint = new Color(0.48f, 0.53f, 0.51f, 1f); break;
            case CITY: tint = new Color(0.46f, 0.44f, 0.40f, 1f); floorTint = new Color(0.57f, 0.54f, 0.48f, 1f); break;
            case HALL: tint = new Color(0.36f, 0.33f, 0.40f, 1f); floorTint = new Color(0.48f, 0.45f, 0.52f, 1f); break;
            default: tint = new Color(0.31f, 0.40f, 0.40f, 1f); floorTint = tint;
        }
    }

    @Override public void act(float delta) { elapsed += delta; }

    @Override public void draw(Batch batch, OrthographicCamera camera) {
        float packed = batch.getPackedColor();
        float left = camera.position.x - camera.viewportWidth / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float right = left + camera.viewportWidth, top = bottom + camera.viewportHeight;
        batch.setColor(tint);
        for (float y = (float) Math.floor(bottom / 128f) * 128f; y < top; y += 128f)
            for (float x = (float) Math.floor(left / 128f) * 128f; x < right; x += 128f)
                batch.draw(wall, x, y, 128f, 128f);
        batch.setColor(0.05f, 0.08f, 0.09f, 0.5f);
        batch.draw(pixel, left, 200f, camera.viewportWidth, 16f);
        batch.setColor(0.27f, 0.34f, 0.33f, 1f);
        batch.draw(pixel, left, 280f, camera.viewportWidth, 8f);
        if (sewers) {

            fill(batch, left, 280f, camera.viewportWidth, 8f, 0x465653, 1f);
            fill(batch, left, 200f, camera.viewportWidth, 16f, 0x0A1012, 1f);
            fill(batch, left, 128f, camera.viewportWidth, 72f, 0x112A29, 1f);
            fill(batch, left, 192f, camera.viewportWidth, 8f, 0x3D6257, 0.28f);
            double flow = elapsed * 30f * (GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.3f : 1f);
            for (int i = 0; i < 30; i++) {
                float length = 24f + i % 5 * 16f;
                float at = (float) ((i * 173f + flow * (1f + i % 3 * 0.1f)) % (camera.viewportWidth + length)) - length;
                float start = Math.max(0f, at), end = Math.min(camera.viewportWidth, at + length);
                float down = 16f + i * 31f % 48f;
                if (end > start) fill(batch, left + start, 200f - down, end - start, 4f, 0x3D6257, 0.45f);
            }
            fill(batch, left, 120f, camera.viewportWidth, 8f, 0x465653, 0.75f);
            fill(batch, left, 112f, camera.viewportWidth, 8f, 0x0A1012, 1f);
            for (int i = 0; i < 16; i++) {
                float alpha = (16 - i) / 50f;
                fill(batch, left, top - (i + 1) * 16f, camera.viewportWidth, 16f, 0x0A1012, alpha);
                fill(batch, left, bottom + i * 16f, camera.viewportWidth, 16f, 0x0A1012, alpha);
            }
        } else {

            float height = faceHeight * 8f;
            fill(batch, left, 288f - height - 8f, camera.viewportWidth, 16f, 0x080C10, 0.8f);
            batch.setColor(floorTint);
            for (float x = (float) Math.floor(left / 128f) * 128f; x < right; x += 128f)
                batch.draw(raised, x, 288f - height, 128f, height, 0, faceY, 16, faceHeight, false, false);
        }
        batch.setPackedColor(packed);
    }

    private void fill(Batch batch, float x, float y, float w, float h, int rgb, float alpha) {
        batch.setColor((rgb >> 16 & 255) / 255f, (rgb >> 8 & 255) / 255f, (rgb & 255) / 255f, alpha);
        batch.draw(pixel, x, y, w, h);
    }
}
