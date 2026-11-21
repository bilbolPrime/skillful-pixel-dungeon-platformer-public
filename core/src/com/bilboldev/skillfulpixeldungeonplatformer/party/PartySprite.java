package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AssetHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;
import java.io.IOException;
import java.util.Arrays;

public final class PartySprite {
    public static final class Data {
        public String asset;
        public float width, height, scaleX, scaleY, rotation, alpha;
        public int color, x, y, regionWidth, regionHeight;
        public boolean flipX, flipY;
        public int[] animation;
    }

    static Data capture(GameSprite source) {
        if (source == null) return null;
        Sprite sprite = source.partySprite();
        Data data = new Data();
        data.asset = source.spriteString != null ? source.spriteString : AssetHelper.getInstance().texturePath(sprite.getTexture());
        data.width = source.getWidth(); data.height = source.getHeight();
        data.scaleX = source.getScaleX(); data.scaleY = source.getScaleY(); data.rotation = source.getRotation(); data.alpha = source.getAlpha();
        data.color = Color.rgba8888(sprite.getColor());
        data.x = sprite.getRegionX(); data.y = sprite.getRegionY(); data.regionWidth = sprite.getRegionWidth(); data.regionHeight = sprite.getRegionHeight();
        data.flipX = sprite.isFlipX(); data.flipY = sprite.isFlipY(); data.animation = source.partyAnimation();
        return data;
    }

    static GameSprite restore(Data data) throws IOException {
        if (data == null) return null;
        if (data.asset == null || !data.asset.startsWith("images/") || data.asset.contains("..") || data.asset.length() > 250
                || !finite(data.width, 4096) || !finite(data.height, 4096) || data.width <= 0 || data.height <= 0
                || !finite(data.scaleX, 16) || !finite(data.scaleY, 16) || !finite(data.rotation, 3600)
                || !finite(data.alpha, 1) || data.alpha < 0) throw new IOException("menu_sprite");
        GameSprite result = new GameSprite(data.asset, data.width, data.height, data.alpha);
        Sprite sprite = result.partySprite();
        int width = sprite.getTexture().getWidth(), height = sprite.getTexture().getHeight();

        int x = data.flipX ? data.x - data.regionWidth : data.x, y = data.flipY ? data.y - data.regionHeight : data.y;
        if (x < 0 || y < 0 || data.regionWidth < 1 || data.regionHeight < 1
                || x > width - data.regionWidth || y > height - data.regionHeight) throw new IOException("menu_region");
        result.partyRegion(x, y, data.regionWidth, data.regionHeight, data.flipX, data.flipY);
        if (data.animation != null) {
            int[] a = data.animation;
            if (a.length < 6 || a.length > 69 || a[0] < 1 || a[1] < 1 || a[0] > width || a[1] > height
                    || a[2] < 0 || a[3] < 0 || a[2] > height / a[1] - 1 - a[3] || a[4] < 0 || a[4] > 1) throw new IOException("menu_animation");
            for (int i = 5; i < a.length; i++) if (a[i] < 0 || a[i] >= width / a[0]) throw new IOException("menu_frame");
            result = new Interactable.AnimatedDialoguePortrait(data.asset, data.width, data.height, data.alpha,
                    Arrays.copyOfRange(a, 5, a.length), a[0], a[1], a[2], a[3], a[4] == 1);
        }
        result.setScale(data.scaleX, data.scaleY); result.setRotation(data.rotation);
        Color tint = new Color(); Color.rgba8888ToColor(tint, data.color); result.setColor(tint);
        return result;
    }

    private static boolean finite(float value, float limit) { return !Float.isNaN(value) && !Float.isInfinite(value) && Math.abs(value) <= limit; }
    private PartySprite() { }
}
