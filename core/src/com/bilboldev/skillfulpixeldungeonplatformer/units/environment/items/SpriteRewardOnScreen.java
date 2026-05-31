package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class SpriteRewardOnScreen extends ItemOnScreen {
    private final GameSprite displaySprite;

    public SpriteRewardOnScreen(Item item, String spritePath, float width, float height) {
        super(item);
        displaySprite = new GameSprite(spritePath, width, height);
    }

    @Override
    public float getInteractionWidth() {
        return displaySprite.getWidth();
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (!isPlaced() || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        displaySprite.setPosition(x, y);
        displaySprite.setAlpha(alpha);
        displaySprite.draw(batch);
    }
}