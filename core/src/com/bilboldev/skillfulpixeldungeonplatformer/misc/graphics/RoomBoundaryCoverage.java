package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;


public final class RoomBoundaryCoverage {
    private TextureRegion pixel;
    public static boolean isBoundaryTile(int x, int y, int width, int height) {
        return x < 0 || x >= width || y < ConstantsHelper.MIN_FLOOR - 1 || y >= height;
    }
    public void draw(Batch batch, Theme theme, Room room, OrthographicCamera camera) {
        if (camera == null) return;
        float t = ConstantsHelper.TILE, halfW = camera.viewportWidth * camera.zoom / 2f, halfH = camera.viewportHeight * camera.zoom / 2f;
        int left = (int)Math.floor((camera.position.x - halfW) / t), right = (int)Math.ceil((camera.position.x + halfW) / t);
        int bottom = (int)Math.floor((camera.position.y - halfH) / t), top = (int)Math.ceil((camera.position.y + halfH) / t);
        float packed = batch.getPackedColor(), r = batch.getColor().r, g = batch.getColor().g, b = batch.getColor().b, a = batch.getColor().a;
        try {
            for (int y = bottom; y < top; y++) for (int x = left; x < right; x++) {
                if (!isBoundaryTile(x, y, (int)room.getWidth(), (int)room.getHeight())) continue;
                batch.setColor(r * .72f, g * .72f, b * .72f, a);
                theme.getWall().setPosition(x * t, y * t); theme.getWall().draw(batch);
                theme.getFader().setPosition(x * t, y * t); theme.getFader().draw(batch);
            }

            if (pixel == null) pixel = new TextureRegion(TextureHelper.GetSingleton().getSolidPixel());
            batch.setColor(r * .08f, g * .09f, b * .10f, a);
            batch.draw(pixel, -t / 8f, 0, t / 8f, room.getHeight() * t);
            batch.draw(pixel, room.getWidth() * t, 0, t / 8f, room.getHeight() * t);
            batch.draw(pixel, 0, room.getHeight() * t, room.getWidth() * t, t / 8f);
        } finally { batch.setPackedColor(packed); }
    }
}
