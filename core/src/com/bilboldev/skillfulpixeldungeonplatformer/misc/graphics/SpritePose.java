package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;


public final class SpritePose {
    private final TextureRegion region;
    private final SpritePose[] layers;
    public final float x, y, originX, originY, width, height, scaleX, scaleY, rotation;
    public final float red, green, blue, alpha;

    SpritePose(TextureRegion source, float x, float y, float originX, float originY,
               float width, float height, float scaleX, float scaleY, float rotation,
               float red, float green, float blue, float alpha) {
        this(source, x, y, originX, originY, width, height, scaleX, scaleY, rotation, red, green, blue, alpha, null);
    }

    private SpritePose(TextureRegion source, float x, float y, float originX, float originY,
                       float width, float height, float scaleX, float scaleY, float rotation,
                       float red, float green, float blue, float alpha, SpritePose[] layers) {
        region = new TextureRegion(source);
        this.layers = layers;
        this.x = x;
        this.y = y;
        this.originX = originX;
        this.originY = originY;
        this.width = width;
        this.height = height;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = rotation;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }


    static SpritePose composite(TextureRegion boundsRegion, float x, float y, float width, float height,
                                SpritePose[] parts, int count) {
        if (count < 1 || count > RoomSnapshot.MAX_FOCAL_PARTS || count > parts.length)
            throw new IllegalArgumentException("Invalid observed focal part count");
        for (int i = 0; i < count; i++) if (parts[i] == null || parts[i].layers != null)
            throw new IllegalArgumentException("Focal parts must be non-nested immutable poses");
        return new SpritePose(boundsRegion, x, y, 0f, 0f, width, height, 1f, 1f, 0f, 1f, 1f, 1f, 1f,
                java.util.Arrays.copyOf(parts, count));
    }

    public int layerCount() { return layers == null ? 0 : layers.length; }
    public SpritePose layerAt(int index) { return layers == null || index < 0 || index >= layers.length ? null : layers[index]; }

    public float bottomCenterX() {
        float localX = (width / 2f - originX) * scaleX, localY = -originY * scaleY;
        return x + originX + localX * MathUtils.cosDeg(rotation) - localY * MathUtils.sinDeg(rotation);
    }

    public float bottomCenterY() {
        float localX = (width / 2f - originX) * scaleX, localY = -originY * scaleY;
        return y + originY + localX * MathUtils.sinDeg(rotation) + localY * MathUtils.cosDeg(rotation);
    }

    public float displayedArea() { return Math.abs(width * height * scaleX * scaleY); }


    public boolean intersects(Rectangle view) {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(width) || !Float.isFinite(height)
                || !Float.isFinite(originX) || !Float.isFinite(originY) || !Float.isFinite(scaleX)
                || !Float.isFinite(scaleY) || !Float.isFinite(rotation) || !Float.isFinite(alpha)
                || width <= 0f || height <= 0f || scaleX == 0f || scaleY == 0f || alpha <= 0f) return false;
        float cos = MathUtils.cosDeg(rotation), sin = MathUtils.sinDeg(rotation);
        float centerX = (width / 2f - originX) * scaleX, centerY = (height / 2f - originY) * scaleY;
        float worldX = x + originX + centerX * cos - centerY * sin;
        float worldY = y + originY + centerX * sin + centerY * cos;
        float halfWidth = (Math.abs(width * scaleX * cos) + Math.abs(height * scaleY * sin)) / 2f;
        float halfHeight = (Math.abs(width * scaleX * sin) + Math.abs(height * scaleY * cos)) / 2f;
        return worldX + halfWidth > view.x && worldX - halfWidth < view.x + view.width
                && worldY + halfHeight > view.y && worldY - halfHeight < view.y + view.height;
    }


    public void drawFeatheredEdges(Batch batch, float edgeWidth, float[] vertices) {
        if (layers != null || rotation != 0f || scaleX != 1f || scaleY != 1f || edgeWidth <= 0f) { draw(batch); return; }
        float edge = Math.min(edgeWidth, Math.min(width, height) / 2f);
        Color tint = batch.getColor();
        float r = tint.r * red, g = tint.g * green, b = tint.b * blue, opacity = tint.a * alpha;
        for (int row = 0; row < 3; row++) {
            float bottom = row == 0 ? 0f : row == 1 ? edge : height - edge;
            float top = row == 0 ? edge : row == 1 ? height - edge : height;
            for (int column = 0; column < 3; column++) {
                float left = column == 0 ? 0f : column == 1 ? edge : width - edge;
                float right = column == 0 ? edge : column == 1 ? width - edge : width;
                if (right <= left || top <= bottom) continue;
                for (int corner = 0; corner < 4; corner++) {
                    float dx = corner < 2 ? left : right, dy = corner == 0 || corner == 3 ? bottom : top;
                    float fade = dx == 0f || dx == width || dy == 0f || dy == height ? 0f : 1f;
                    int at = corner * 5;
                    vertices[at] = x + dx; vertices[at + 1] = y + dy;
                    vertices[at + 2] = Color.toFloatBits(r, g, b, opacity * fade);
                    vertices[at + 3] = region.getU() + (region.getU2() - region.getU()) * dx / width;
                    vertices[at + 4] = region.getV2() + (region.getV() - region.getV2()) * dy / height;
                }
                batch.draw(region.getTexture(), vertices, 0, 20);
            }
        }
    }


    public void draw(Batch batch) {
        if (layers != null) {
            for (int i = 0; i < layers.length; i++) layers[i].draw(batch);
            return;
        }
        drawBody(batch, y, scaleY);
    }


    public void drawAnchored(Batch batch, float heightScale) {
        if (layers != null || rotation != 0f || scaleY <= 0f || !Float.isFinite(heightScale) || heightScale < 0.95f || heightScale > 1f) {
            draw(batch);
            return;
        }
        float drawScaleY = scaleY * heightScale;
        drawBody(batch, y + originY * (drawScaleY - scaleY), drawScaleY);
    }

    private void drawBody(Batch batch, float drawY, float drawScaleY) {
        float packed = batch.getPackedColor();
        Color tint = batch.getColor();
        try {
            batch.setColor(tint.r * red, tint.g * green, tint.b * blue, tint.a * alpha);
            batch.draw(region, x, drawY, originX, originY, width, height, scaleX, drawScaleY, rotation);
        } finally {
            batch.setPackedColor(packed);
        }
    }
}
