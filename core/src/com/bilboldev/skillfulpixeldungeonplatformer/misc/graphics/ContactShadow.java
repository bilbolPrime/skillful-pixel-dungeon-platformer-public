package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;


public final class ContactShadow {
    private ContactShadow() { }

    public static void draw(Batch batch, float centerX, float footY, float width, float alpha, boolean hovering) {
        Room room = MapHelper.getInstance().getActiveRoom();
        if (room == null || !Float.isFinite(centerX) || !Float.isFinite(footY) || alpha <= 0f) return;
        float tile = ConstantsHelper.TILE;
        int column = MathUtils.floor(centerX / tile);
        int row = surfaceRow(room, column, footY);
        if (row < 0) return;

        float surfaceY = (row + 1) * tile + 4f;
        float height = Math.max(0f, footY - surfaceY);
        float strength = Math.max(0f, 1f - height / (1.5f * tile));
        if (strength <= 0f) return;
        float shadowWidth = MathUtils.clamp(width, 18f, 120f) * (0.55f + 0.45f * strength);
        if (hovering) shadowWidth *= 0.8f;
        float left = centerX - shadowWidth / 2f;
        float right = centerX + shadowWidth / 2f;
        float shadowHeight = Math.min(12f, shadowWidth * 0.18f);
        Texture texture = TextureHelper.GetSingleton().getSoftLightTexture();
        float packed = batch.getPackedColor();
        batch.setColor(0f, 0f, 0f, batch.getColor().a * alpha * 0.48f * strength * (hovering ? 0.55f : 1f));

        for (int x = MathUtils.floor(left / tile); x <= MathUtils.floor(right / tile); x++) {
            if (surfaceRow(room, x, footY) != row) continue;
            float from = Math.max(left, x * tile);
            float to = Math.min(right, (x + 1) * tile);
            if (to <= from) continue;
            batch.draw(texture, from, surfaceY - shadowHeight, to - from, shadowHeight,
                    (from - left) / shadowWidth, 1f, (to - left) / shadowWidth, 0f);
        }
        batch.setPackedColor(packed);
    }

    private static int surfaceRow(Room room, int column, float footY) {
        float tile = ConstantsHelper.TILE;
        if (column < 0 || column >= room.getWidth() || footY < ConstantsHelper.MIN_FLOOR * tile) return -1;

        int highest = Math.min((int) room.getHeight() - 1, MathUtils.floor(footY / tile) - 1);
        for (int row = highest; row >= ConstantsHelper.MIN_FLOOR; row--) {
            if (room.getPlatforms().contains(UtilsHelper.platformKey(column, row))) return row;
        }
        return ConstantsHelper.MIN_FLOOR - 1;
    }
}
