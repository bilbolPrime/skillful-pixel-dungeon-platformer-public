package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout;
import java.util.List;


public final class RoomPiers {
    private final TextureRegion stone, pixel;
    public RoomPiers(TextureRegion wall) {
        stone = new TextureRegion(wall, 0, 0, 10, 16);
        pixel = new TextureRegion(TextureHelper.GetSingleton().getSolidPixel());
    }
    public void draw(Batch batch, List<RoomLayout.Part> parts) {
        draw(batch, parts, "sewers");
    }
    public void draw(Batch batch, List<RoomLayout.Part> parts, String family) {
        float saved = batch.getPackedColor(), r = batch.getColor().r, g = batch.getColor().g, b = batch.getColor().b, a = batch.getColor().a;
        float t = ConstantsHelper.TILE, edge = t / 16f;
        try {
            for (RoomLayout.Part part : parts) if (part.pier) {
                batch.setColor(r * .09f, g * .12f, b * .13f, a);
                batch.draw(pixel, part.x - edge, part.y, part.width + 3*edge, part.height);
                batch.setColor(r * .33f, g * .40f, b * .40f, a);
                for (float y = part.y; y < part.y + part.height; y += t)
                    batch.draw(stone, part.x, y, part.width, Math.min(t, part.y + part.height - y));
                batch.setColor(r * .27f, g * .34f, b * .33f, a);
                batch.draw(pixel, part.x, part.y, edge, part.height);
                batch.setColor(r * .07f, g * .10f, b * .11f, a);
                batch.draw(pixel, part.x + part.width - edge, part.y, 2*edge, part.height);
                if (family.startsWith("caves")) {
                    batch.setColor(r*.26f, g*.20f, b*.13f, a);
                    batch.draw(pixel, part.x, part.y, 2*edge, part.height);
                    batch.draw(pixel, part.x-edge, part.y+part.height-t, part.width+2*edge, 3*edge);
                } else if (family.startsWith("city") || family.startsWith("halls")) {
                    batch.setColor(r*.35f, g*.34f, b*.30f, a);
                    batch.draw(pixel, part.x-edge, part.y, part.width+2*edge, 2*edge);
                    batch.draw(pixel, part.x-edge, part.y+part.height-2*edge, part.width+2*edge, 2*edge);
                } else if (family.startsWith("prison")) {
                    batch.setColor(r*.20f, g*.23f, b*.24f, a);
                    for (float y = part.y + t; y < part.y+part.height; y += 2*t)
                        batch.draw(pixel, part.x-edge, y, part.width+2*edge, edge);
                }
            }
        } finally { batch.setPackedColor(saved); }
    }
}
