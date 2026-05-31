package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class DarkGoldVein extends Decoration {
    private static final float DRAW_SIZE = ConstantsHelper.TILE / 2f;
    private static final float DRAW_OFFSET_X = (ConstantsHelper.TILE - DRAW_SIZE) / 2f;
    private static final float DRAW_OFFSET_Y = 0f;

    {
        gs = new GameSprite("images/misc/extracted items/ORE.png", DRAW_SIZE, DRAW_SIZE);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (gs != null && room != null && room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            gs.setPosition(x + DRAW_OFFSET_X, y + DRAW_OFFSET_Y);
            gs.draw(batch);
        }
    }
}