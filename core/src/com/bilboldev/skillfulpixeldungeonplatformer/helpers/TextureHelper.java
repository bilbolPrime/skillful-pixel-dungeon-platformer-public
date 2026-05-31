package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;


import java.util.HashMap;
import java.util.Map;

public class TextureHelper {
    private HashMap<String, Sprite> otherTextures;
    private HashMap<String, Texture> generatedTextures;

    private static TextureHelper m_instance;

    public static TextureHelper GetSingleton(){
        if(m_instance == null){
            m_instance = new TextureHelper();

        }

        return m_instance;
    }

    public static  void reset(){
        m_instance = null;
    }

    private TextureHelper(){

        otherTextures = new HashMap<String, Sprite>();
        generatedTextures = new HashMap<String, Texture>();


    }

    public Texture debugRectangle(int x, int y, int width, int height){
          Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
          pixmap.setColor(Color.RED);
          pixmap.fillRectangle(x, y, width, height);
          Texture debug = new Texture(pixmap);
          pixmap.dispose();
          generatedTextures.put("debugRectangle_" + x + "_" + y + "_" + width + "_" + height, debug);
          return debug;
    }



    public Texture debugCircle(int radius){
        Pixmap pixmap = new Pixmap(radius * 2, radius * 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.FIREBRICK);
        pixmap.fillCircle(radius, radius, radius );
        Texture debug = new Texture(pixmap);
        pixmap.dispose();
        generatedTextures.put("debugCircle_" + radius, debug);
        return debug;
    }



    public Sprite getOtherTexture(String otherTexture){
        return otherTextures.get(otherTexture);
    }

    public Sprite getSprite(String input){
        Texture tex = getTexture(input);
        Sprite spr = new Sprite(tex);
        spr.setCenter(0, 0);
        spr.setOrigin(spr.getWidth() / 2, spr.getHeight() / 2);
        return  spr;
    }

    public Sprite getGeneratedRadialAuraSprite(String key, int size) {
        Texture texture = generatedTextures.get(key);
        if (texture == null) {
            texture = createRadialAuraTexture(size);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            generatedTextures.put(key, texture);
        }

        Sprite sprite = new Sprite(texture);
        sprite.setCenter(0, 0);
        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
        return sprite;
    }

    public Texture getTexture(String input) {
        return AssetHelper.getInstance().getTexture(input);
    }

    private Texture createRadialAuraTexture(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float center = (size - 1) / 2f;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dx = (x - center) / center;
                float dy = (y - center) / center;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance >= 1f) {
                    continue;
                }

                float angle = MathUtils.atan2(dy, dx);
                float centerGlow = Math.max(0f, 1f - distance);
                float middleHalo = Math.max(0f, 1f - Math.abs(distance - 0.55f) / 0.24f);
                float outerRing = Math.max(0f, 1f - Math.abs(distance - 0.84f) / 0.12f);
                float rays = 0.5f + 0.5f * MathUtils.sin(angle * 6f + distance * 18f);
                float alpha = 0.18f * centerGlow * centerGlow
                        + 0.30f * middleHalo
                        + 0.24f * outerRing * (0.6f + 0.4f * rays);
                alpha *= Math.max(0f, 1f - Math.max(0f, distance - 0.92f) / 0.08f);

                if (alpha <= 0f) {
                    continue;
                }

                pixmap.setColor(1f, 1f, 1f, Math.min(1f, alpha));
                pixmap.drawPixel(x, y);
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    String getKey(String toResize, int width, int height){
        return  toResize + "_" + width + "_" + height;
    }

    String getKey(String toResize, float scale){
        return  toResize + "_" + scale;
    }

    String getKey(String toResize){
        return  toResize;
    }

    public void dispose(){
        if(otherTextures != null){
            for (Map.Entry<String, Sprite> item : otherTextures.entrySet()) {
                item.getValue().getTexture().dispose();
            }

            otherTextures.clear();
        }

        if(generatedTextures != null){
            for (Map.Entry<String, Texture> item : generatedTextures.entrySet()) {
                item.getValue().dispose();
            }

            generatedTextures.clear();
        }

        m_instance = null;
    }
}

