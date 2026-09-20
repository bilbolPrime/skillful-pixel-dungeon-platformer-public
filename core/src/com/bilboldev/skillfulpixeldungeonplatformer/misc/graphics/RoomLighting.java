package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout;


public final class RoomLighting {
    public static final float MOUNTED_SOURCE_OFFSET_Y = -ConstantsHelper.TILE / 8f;
    private TextureRegion glow;
    private Room source;
    private java.util.List<RoomLayout.Part> parts;
    public void draw(Batch batch, Room room, float x, float y, float radius) {
        draw(batch, room, x, y, radius, .90f, .65f, .29f);
    }
    public void draw(Batch batch, Room room, float x, float y, float radius, float red, float green, float blue) {
        com.badlogic.gdx.graphics.OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        if (camera != null && !camera.frustum.boundsInFrustum(x, y, 0f, radius, radius, 0f)) return;
        if (glow == null) glow = new TextureRegion(TextureHelper.GetSingleton().getSoftLightTexture());

        if (source != room) { source = room; parts = room.getLayout().copy().parts; }
        float saved = batch.getPackedColor(), r = batch.getColor().r, g = batch.getColor().g, b = batch.getColor().b, a = batch.getColor().a;
        float intensity = GameSettingsHelper.getInstance().getVisualEffectIntensity();

        int steps = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 4 : 8;
        float size = radius * 2f / steps;
        try {
            batch.setColor(r * red, g * green, b * blue, a * .24f * intensity);
            for (int row = 0; row < steps; row++) for (int column = 0; column < steps; column++) {
                float px = x - radius + column * size, py = y - radius + row * size;
                if (!patchVisible(room, x, y, px, py, size)) continue;
                float u = (float)column / steps, v = 1f - (float)row / steps;
                batch.draw(glow.getTexture(), px, py, size, size, u, v, u + 1f/steps, v - 1f/steps);
            }
        } finally { batch.setPackedColor(saved); }
    }
    private boolean patchVisible(Room room, float x, float y, float px, float py, float size) {
        for (int corner = 0; corner < 4; corner++) {
            float tx = px + ((corner & 1) == 0 ? 0 : size), ty = py + ((corner & 2) == 0 ? 0 : size);
            if (!sampleVisible(room, parts, x, y, tx, ty)) return false;
        }
        return true;
    }
    public static boolean sampleVisible(Room room, float x, float y, float tx, float ty) {
        return sampleVisible(room, room.getLayout().copy().parts, x, y, tx, ty);
    }
    private static boolean sampleVisible(Room room, java.util.List<RoomLayout.Part> parts, float x, float y, float tx, float ty) {
        if (tx < 0 || tx > room.getWidth()*ConstantsHelper.TILE || ty < ConstantsHelper.MIN_FLOOR*ConstantsHelper.TILE
                || ty > room.getHeight()*ConstantsHelper.TILE) return false;
        for (RoomLayout.Part part : parts) if (part.role == RoomLayout.Role.PLAYABLE
                && crossesSurface(part.x, part.x + part.width, part.y + part.height, x, y, tx, ty)) return false;
        return clearPiers(parts, x, y, tx, ty);
    }
    private static boolean crossesSurface(float left, float right, float top, float x, float y, float tx, float ty) {
        if (Math.abs(ty - y) <= .01f) return false;
        float crossing = (top - y) / (ty - y), atX = x + crossing * (tx - x);
        return crossing > 0f && crossing < 1f && atX >= left && atX <= right;
    }
    private static boolean clearPiers(java.util.List<RoomLayout.Part> parts, float x, float y, float tx, float ty) {
        for (RoomLayout.Part pier : parts) {
            if (!pier.pier) continue;

            if (x >= pier.x && x <= pier.x + pier.width) continue;
            if (Math.max(x, tx) < pier.x || Math.min(x, tx) > pier.x + pier.width) continue;
            float face = x < pier.x ? pier.x : pier.x + pier.width;
            float progress = (face - x) / (tx - x);
            float atY = y + (ty - y) * progress;
            if (progress > 0 && progress < 1 && atY >= pier.y && atY <= pier.y + pier.height) return false;
        }
        return true;
    }
    private static boolean detachedSampleVisible(RoomSnapshot snapshot, float x, float y, float tx, float ty) {
        if (tx < 0 || tx > snapshot.widthTiles * ConstantsHelper.TILE
                || ty < ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE || ty > snapshot.heightTiles * ConstantsHelper.TILE) return false;
        for (RoomSnapshot.Platform span : snapshot.platforms)
            if (crossesSurface(span.left(), span.left() + span.width(), (span.tileY + 1) * ConstantsHelper.TILE,
                    x, y, tx, ty)) return false;
        return clearPiers(snapshot.layout.parts, x, y, tx, ty);
    }

    public static void drawDetached(Batch batch, RoomSnapshot snapshot, TextureRegion glow, float x, float y, float radius) {
        float size = radius / 2f;
        for (int row = 0; row < 4; row++) for (int column = 0; column < 4; column++) {
            float px = x - radius + column * size, py = y - radius + row * size;
            boolean visible = true;
            for (int corner = 0; corner < 4; corner++)
                visible &= detachedSampleVisible(snapshot, x, y, px + ((corner & 1) == 0 ? 0 : size),
                        py + ((corner & 2) == 0 ? 0 : size));
            if (visible) batch.draw(glow.getTexture(), px, py, size, size, column / 4f, 1f - row / 4f,
                    (column + 1) / 4f, 1f - (row + 1) / 4f);
        }
    }
}
