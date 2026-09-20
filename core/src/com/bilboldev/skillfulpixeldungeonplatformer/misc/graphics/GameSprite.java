package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;

public class GameSprite {
   protected Sprite sprite;
   protected float x, y, rotation, originX, originY, scaleX, scaleY, width, height, alpha;

   protected Polygon polygon;
   protected boolean repeatable;
    TextureRegion textureRegion;
    public String spriteString;
    private float[] featherVertices;
    private boolean pulseTintEnabled;
    private float pulseTintRed = 1f;
    private float pulseTintGreen = 1f;
    private float pulseTintBlue = 1f;
    private float pulseTintPeriodSeconds = 0.6f;
    public GameSprite(Sprite sprite, float width, float height){
        this.sprite = sprite;
        this.width = width;
        this.height = height;

        this.originX = width / 2;
        this.originY = width / 2;

        this.scaleX = 1f;
        this.scaleY = 1f;

        this.alpha = 1f;

        this.polygon = new Polygon();
    }

    public GameSprite(String sprite, float width, float height, float alpha){
        this.spriteString = sprite;
        this.sprite = TextureHelper.GetSingleton().getSprite(sprite);
        this.width = width;
        this.height = height;

        this.originX = width / 2;
        this.originY = height / 2;

        this.scaleX = 1f;
        this.scaleY = 1f;

        this.alpha = alpha;

        this.polygon = new Polygon();
    }

    public GameSprite(String sprite, float width, float height){
        this(sprite, width, height, 1f);
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return  x;
    }

    public float getY(){
        return y;
    }

    public void translate(float x, float y){
        this.x += x;
        this.y += y;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public float getWidth(){
        return width;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public float getHeight(){
        return height;
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public void setOrigin(float x, float y){
        sprite.setOrigin(x, y);
    }

    public float getRotation(){
       return rotation;
    }

    public void setAlpha(float alpha){
        if(alpha > 1){
            alpha = 1;
        }
        if(alpha < 0){
            alpha = 0;
        }
        this.alpha = alpha;
    }

    public void setColor(Color color) {
        if (color == null) {
            sprite.setColor(Color.WHITE);
            return;
        }

        sprite.setColor(color);
    }

    public float getAlpha(){
        return  alpha;
    }


    public SpritePose copyPose() {
        return copyPoseAt(x, y, alpha);
    }


    public SpritePose copyPoseAt(float observedX, float observedY, float observedAlpha) {
        if (sprite == null) return null;
        Color color = sprite.getColor();
        return new SpritePose(repeatable ? textureRegion : sprite, observedX, observedY,
                repeatable ? 0f : originX, repeatable ? 0f : originY, width, height,
                repeatable ? 1f : scaleX, repeatable ? 1f : scaleY, repeatable ? 0f : rotation,
                color.r, color.g, color.b, color.a * observedAlpha);
    }

    public void rotate(float rotation){
        this.rotation += rotation;
    }

    public void setScaleX(float scaleX){
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY){
        this.scaleY = scaleY;
    }

    public void setScale(float scaleX, float scaleY){
        setScaleX(scaleX);
        setScaleY(scaleY);
    }
    public void faceRight(boolean faceRight){
        setScaleX(faceRight ? Math.abs(scaleX) : -Math.abs(scaleX));
    }

    public float getScaleX(){
        return  scaleX;
    }

    public float getScaleY(){
        return  scaleY;
    }

    public Rectangle getBoundingRectangle(){
        polygon = new Polygon(new float[]{0,0,width,0,width,height,0,height});
        polygon.setPosition(x, y);
        polygon.setOrigin(originX, originY);
        polygon.setRotation(rotation);
        return polygon.getBoundingRectangle();
    }

    public void setPulseTint(Color color, float periodSeconds) {
        if (color == null) {
            clearPulseTint();
            return;
        }

        pulseTintEnabled = true;
        pulseTintRed = color.r;
        pulseTintGreen = color.g;
        pulseTintBlue = color.b;
        pulseTintPeriodSeconds = Math.max(0.01f, periodSeconds);
    }

    public void clearPulseTint() {
        pulseTintEnabled = false;
        pulseTintRed = 1f;
        pulseTintGreen = 1f;
        pulseTintBlue = 1f;
        pulseTintPeriodSeconds = 0.6f;
    }

    public void draw(Batch batch){
        sprite.setOrigin(originX, originY);
        sprite.setRotation(rotation);
        Color previousColor = new Color(batch.getColor());
        Color spriteColor = sprite.getColor();
        batch.setColor(previousColor.r * spriteColor.r,
                previousColor.g * spriteColor.g,
                previousColor.b * spriteColor.b,
                previousColor.a * spriteColor.a * alpha);


        if(!repeatable)
            batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        else
            batch.draw(textureRegion, x, y, width, height);

        if (pulseTintEnabled) {
            drawPulseOverlay(batch, previousColor, spriteColor, 0f, 0f);
        }

        batch.setColor(previousColor);
    }


    public void drawFeatheredEdges(Batch batch, float edgeWidth) {
        if (rotation != 0f || scaleX != 1f || scaleY != 1f || repeatable || edgeWidth <= 0f) {
            draw(batch);
            return;
        }
        float edge = Math.min(edgeWidth, Math.min(width, height) / 2f);
        if (featherVertices == null) featherVertices = new float[20];
        Color parent = batch.getColor(), tint = sprite.getColor();
        float r = parent.r * tint.r, g = parent.g * tint.g, b = parent.b * tint.b;
        float opacity = parent.a * tint.a * alpha;

        for (int row = 0; row < 3; row++) {
            float bottom = row == 0 ? 0f : row == 1 ? edge : height - edge;
            float top = row == 0 ? edge : row == 1 ? height - edge : height;
            for (int column = 0; column < 3; column++) {
                float left = column == 0 ? 0f : column == 1 ? edge : width - edge;
                float right = column == 0 ? edge : column == 1 ? width - edge : width;
                for (int corner = 0; corner < 4; corner++) {
                    float dx = corner < 2 ? left : right;
                    float dy = corner == 0 || corner == 3 ? bottom : top;
                    float fade = dx == 0f || dx == width || dy == 0f || dy == height ? 0f : 1f;
                    int offset = corner * 5;
                    featherVertices[offset] = x + dx;
                    featherVertices[offset + 1] = y + dy;
                    featherVertices[offset + 2] = Color.toFloatBits(r, g, b, opacity * fade);
                    featherVertices[offset + 3] = sprite.getU() + (sprite.getU2() - sprite.getU()) * dx / width;
                    featherVertices[offset + 4] = sprite.getV2() + (sprite.getV() - sprite.getV2()) * dy / height;
                }
                batch.draw(sprite.getTexture(), featherVertices, 0, 20);
            }
        }
    }

    public void draw(Batch batch, float offsetX, float offsetY){
        sprite.setOrigin(originX, originY);
        sprite.setRotation(rotation);
        Color previousColor = new Color(batch.getColor());
        Color spriteColor = sprite.getColor();
        batch.setColor(previousColor.r * spriteColor.r,
            previousColor.g * spriteColor.g,
            previousColor.b * spriteColor.b,
                previousColor.a * spriteColor.a * alpha);


        batch.draw(sprite, x + offsetX, y + offsetY, originX, originY, width, height, scaleX, scaleY, rotation);

        if (pulseTintEnabled) {
            drawPulseOverlay(batch, previousColor, spriteColor, offsetX, offsetY);
        }

        batch.setColor(previousColor);
    }

    private void drawPulseOverlay(Batch batch, Color previousColor, Color spriteColor, float offsetX, float offsetY) {
        if (!pulseTintEnabled) {
            return;
        }

        float pulseStrength = getPulseStrength();
        drawPulseLayer(batch, previousColor, spriteColor, offsetX, offsetY,
            1f,
            pulseStrength);
    }

    private void drawPulseLayer(Batch batch, Color previousColor, Color spriteColor, float offsetX, float offsetY,
                                float scaleMultiplier, float alphaMultiplier) {
        batch.setColor(previousColor.r * pulseTintRed,
                previousColor.g * pulseTintGreen,
                previousColor.b * pulseTintBlue,
                previousColor.a * spriteColor.a * alpha * alphaMultiplier);

        if(!repeatable) {
            batch.draw(sprite, x + offsetX, y + offsetY, originX, originY, width, height,
                    scaleX * scaleMultiplier, scaleY * scaleMultiplier, rotation);
        }
        else {
            batch.draw(textureRegion, x + offsetX, y + offsetY, width * scaleMultiplier, height * scaleMultiplier);
        }
    }

    private float getPulseStrength() {
        long periodMillis = Math.max(1L, (long) (pulseTintPeriodSeconds * 1000f));
        float phase = (TimeUtils.millis() % periodMillis) / (float) periodMillis;
        float radians = (float) (phase * Math.PI * 2.0 - Math.PI / 2.0);
        return 0.5f + 0.5f * (float) Math.sin(radians);
    }

    public GameSprite clone(){
        GameSprite gs = spriteString != null
                ? new GameSprite(spriteString, width, height, alpha)
                : new GameSprite(sprite, width, height);
        gs.spriteString = spriteString;
        gs.originX = originX;
        gs.originY = originY;
        gs.setRotation(rotation);
        gs.setPosition(x, y);
        gs.setScale(scaleX, scaleY);
        gs.setAlpha(alpha);
        gs.setColor(new Color(sprite.getColor()));
        if (pulseTintEnabled) {
            gs.setPulseTint(new Color(pulseTintRed, pulseTintGreen, pulseTintBlue, 1f), pulseTintPeriodSeconds);
        }
        return gs;
    }

    public void dispose(){
        sprite = null;
        textureRegion = null;
    }

    public void setRepeatable(float width, float height){
        repeatable = true;
        sprite.getTexture().setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        textureRegion = new TextureRegion(sprite.getTexture());
        textureRegion.setRegion(x, y, width, height);
    }
}

