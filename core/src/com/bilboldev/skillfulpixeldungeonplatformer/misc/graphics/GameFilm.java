package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;

public class GameFilm extends GameSprite{

    public int tileX, tileY;
    public int clipSizeX = 16;
    public int clipSizeY = 16;
    public int yClipOffset = 0;

    protected boolean facingRight = true;
    private float visualLeft, visualBottom;
    private float visualWidth = ConstantsHelper.UNIT_DIMENSIONS;
    private float visualHeight = ConstantsHelper.UNIT_DIMENSIONS;
    private float impactFlashTime;
    private float drawnFrameWidth, drawnFrameHeight, drawnScaleX, drawnScaleY, drawnAlpha;
    private int drawnFrameX;

    public void showImpactFlash() { impactFlashTime = 0.10f; }
    public void clearImpactFlash() { impactFlashTime = 0f; }
    public void updateImpactFlash(float delta) { impactFlashTime = Math.max(0f, impactFlashTime - delta); }

    public GameFilm(Sprite sprite, float width, float height){
        super(sprite, width, height);
    }

    public GameFilm(String sprite, float width, float height, float alpha){
        super(sprite, width, height, alpha);
    }

    public void setFrame(int tileX, int tileY){

    }

    @Override
    public void faceRight(boolean faceRight){
        facingRight = faceRight;
    }

    @Override
    public void draw(Batch batch){
        drawFrame(batch, tileX);
    }


    public void drawFrame(Batch batch, int frameX){
        drawFrame(batch, frameX, 1f, 1f);
    }


    public void drawFrame(Batch batch, int frameX, float accentScaleX, float accentScaleY){

        sprite.setOrigin(originX, originY);
        sprite.setRotation(rotation);
        sprite.setAlpha(alpha);
        Color c = sprite.getColor();
        float preA = c.a;
        c.a *= alpha;
        batch.setColor(c);


        if(!repeatable) {

            float artPixel = ConstantsHelper.UNIT_DIMENSIONS / clipSizeY;
            float pixelsPerWorldUnit = Math.abs(batch.getProjectionMatrix().val[Matrix4.M11])
                    * Gdx.graphics.getBackBufferHeight() / 2f;
            float projectedPixel = artPixel * pixelsPerWorldUnit * Math.abs(scaleY);
            float wholePixel = Math.round(projectedPixel);

            if (wholePixel >= 1f && Math.abs(wholePixel - projectedPixel) <= projectedPixel * 0.05f) {
                artPixel *= wholePixel / projectedPixel;
            }
            float frameWidth = clipSizeX * artPixel;
            float frameHeight = clipSizeY * artPixel;
            float footCenterX = x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
            float drawScaleX = scaleX * accentScaleX;
            float drawScaleY = scaleY * accentScaleY;
            drawnFrameWidth = frameWidth;
            drawnFrameHeight = frameHeight;
            drawnScaleX = drawScaleX;
            drawnScaleY = drawScaleY;
            drawnAlpha = c.a;
            drawnFrameX = frameX;
            visualWidth = frameWidth * Math.abs(drawScaleX);
            visualHeight = frameHeight * Math.abs(drawScaleY);
            visualLeft = footCenterX - visualWidth / 2f;
            visualBottom = y;

            batch.draw(sprite.getTexture(), footCenterX - frameWidth / 2f, y, frameWidth / 2f, 0f,
                    frameWidth, frameHeight, drawScaleX, drawScaleY, rotation,
                    frameX * clipSizeX, (yClipOffset + tileY) * clipSizeY,
                    clipSizeX, clipSizeY, !facingRight, false);
            if (impactFlashTime > 0f) {
                float intensity = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.35f : 1f;
                int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
                int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
                float packed = batch.getPackedColor();
                try {

                    batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ZERO, GL20.GL_ONE);
                    batch.setColor(1f, 1f, 1f, c.a * 0.75f * intensity * impactFlashTime / 0.10f);
                    batch.draw(sprite.getTexture(), footCenterX - frameWidth / 2f, y, frameWidth / 2f, 0f,
                            frameWidth, frameHeight, drawScaleX, drawScaleY, rotation,
                            frameX * clipSizeX, (yClipOffset + tileY) * clipSizeY,
                            clipSizeX, clipSizeY, !facingRight, false);
                } finally {
                    batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
                    batch.setPackedColor(packed);
                }
            }
        } else
            batch.draw(textureRegion, x, y, width, height);
        c.a = 1;
        batch.setColor(c);
    }

    public float getVisualLeft() { return visualLeft; }
    public float getVisualBottom() { return visualBottom; }
    public float getVisualWidth() { return visualWidth; }
    public float getVisualHeight() { return visualHeight; }


    public SpritePose copyDrawnFrame() {
        if (sprite == null || repeatable || drawnFrameWidth <= 0f || drawnFrameHeight <= 0f) return null;
        TextureRegion frame = new TextureRegion(sprite.getTexture(), drawnFrameX * clipSizeX,
                (yClipOffset + tileY) * clipSizeY, clipSizeX, clipSizeY);
        frame.flip(!facingRight, false);
        Color color = sprite.getColor();
        return new SpritePose(frame, x + ConstantsHelper.UNIT_DIMENSIONS / 2f - drawnFrameWidth / 2f,
                y, drawnFrameWidth / 2f, 0f, drawnFrameWidth, drawnFrameHeight,
                drawnScaleX, drawnScaleY, rotation, color.r, color.g, color.b, drawnAlpha);
    }
}

