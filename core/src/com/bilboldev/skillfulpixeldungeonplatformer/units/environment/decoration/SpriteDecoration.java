package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class SpriteDecoration extends Decoration {

    public SpriteDecoration(String spritePath, float width, float height) {
        gs = new GameSprite(spritePath, width, height);
    }
}