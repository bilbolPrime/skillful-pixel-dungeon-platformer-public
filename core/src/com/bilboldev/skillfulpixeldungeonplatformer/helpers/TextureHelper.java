package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
        TextureRegion region = NewClassAssets.region(input);
        Sprite spr = region == null ? new Sprite(getTexture(input)) : new Sprite(region);
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

    public Texture getSolidPixel() {
        Texture texture = generatedTextures.get("visual-solid-pixel");
        if (texture == null) {
            Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixel.setColor(Color.WHITE);
            pixel.fill();
            texture = new Texture(pixel);
            pixel.dispose();
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            generatedTextures.put("visual-solid-pixel", texture);
        }
        return texture;
    }


    public TextureRegion getBackdropTile(int biome, int part) {
        if (biome < 0 || biome > 4 || part < 0 || part > 4 || part == 3 && biome != 2)
            throw new IllegalArgumentException("Unknown backdrop material");
        Texture texture = generatedTextures.get("background-native-materials");
        if (texture == null) {
            Pixmap atlas = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
            atlas.setBlending(Pixmap.Blending.None);
            try {
                String[] biomes = {"sewers", "prison", "caves", "city", "halls"};
                for (int index = 0; index < biomes.length; index++) {
                    int row = index * 24 + 1;
                    Pixmap wall = new Pixmap(Gdx.files.internal("images/tiles/" + biomes[index] + "/wall.png"));
                    try {
                        copyBackdropTile(atlas, wall, 0, 0, 16, 16, 1, row);
                        if (index == 0) {
                            copyBackdropTile(atlas, wall, 0, 0, 3, 16, 21, row);
                            copyBackdropTile(atlas, wall, 0, 0, 16, 3, 41, row);
                        }
                        if (index == 2) {
                            wall.setBlending(Pixmap.Blending.None);
                            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) {
                                int source = wall.getPixel(x, y);
                                wall.drawPixel(x, y, caveWallChannel((source >>> 24) & 255, 145, 58) << 24
                                        | caveWallChannel((source >>> 16) & 255, 160, 66) << 16
                                        | caveWallChannel((source >>> 8) & 255, 165, 68) << 8 | 255);
                            }
                            copyBackdropTile(atlas, wall, 0, 0, 16, 16, 61, row);
                        }
                    } finally { wall.dispose(); }
                    if (index > 0) {
                        Pixmap raised = new Pixmap(Gdx.files.internal("images/tiles/" + biomes[index] + "/raised.png"));
                        try {
                            copyBackdropTile(atlas, raised, index == 2 ? 32 : 0, 112,
                                    index == 2 ? 16 : index == 4 ? 4 : 3, index == 2 ? 6 : 16, 21, row);
                            copyBackdropTile(atlas, raised, 0, 208, 16, 3, 41, row);
                        } finally { raised.dispose(); }
                    }
                }
                atlas.setColor(Color.WHITE);
                atlas.fillRectangle(80, 0, 4, 4);
                texture = new Texture(atlas);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                generatedTextures.put("background-native-materials", texture);
            } finally { atlas.dispose(); }
        }
        if (part == 4) return new TextureRegion(texture, 81, 1, 2, 2);
        int width = part == 1 ? (biome == 2 ? 16 : biome == 4 ? 4 : 3) : 16;
        int height = part == 2 ? 3 : part == 1 && biome == 2 ? 6 : 16;
        return new TextureRegion(texture, 1 + part * 20, 1 + biome * 24, width, height);
    }


    private void copyBackdropTile(Pixmap atlas, Pixmap source, int sourceX, int sourceY, int width, int height, int x, int y) {
        for (int row = -1; row <= height; row++) for (int col = -1; col <= width; col++)
            atlas.drawPixel(x + col, y + row, source.getPixel(sourceX + Math.max(0, Math.min(width - 1, col)),
                    sourceY + Math.max(0, Math.min(height - 1, row))));
    }


    public Texture getSoftLightTexture() {
        Texture texture = generatedTextures.get("visual-soft-light");
        if (texture == null) {
            int size = 64;
            Pixmap gradient = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            gradient.setBlending(Pixmap.Blending.None);
            try {
                float center = (size - 1) / 2f;
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        float dx = (x - center) / center;
                        float dy = (y - center) / center;
                        float falloff = Math.max(0f, 1f - dx * dx - dy * dy);
                        gradient.setColor(1f, 1f, 1f, falloff * falloff);
                        gradient.drawPixel(x, y);
                    }
                }
                texture = new Texture(gradient);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                generatedTextures.put("visual-soft-light", texture);
            } finally {
                gradient.dispose();
            }
        }
        return texture;
    }


    public Texture getCavesReturnTexture() {
        Texture texture = generatedTextures.get("background-caves-wall");
        if (texture == null) {
            Pixmap wall = new Pixmap(Gdx.files.internal("images/tiles/caves/wall.png"));
            try {
                wall.setBlending(Pixmap.Blending.None);
                for (int y = 0; y < wall.getHeight(); y++) for (int x = 0; x < wall.getWidth(); x++) {
                    int source = wall.getPixel(x, y);

                    int red = caveWallChannel((source >>> 24) & 255, 145, 58);
                    int green = caveWallChannel((source >>> 16) & 255, 160, 66);
                    int blue = caveWallChannel((source >>> 8) & 255, 165, 68);
                    wall.drawPixel(x, y, (red << 24) | (green << 16) | (blue << 8) | 255);
                }
                texture = new Texture(wall);
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                generatedTextures.put("background-caves-wall", texture);
            } finally { wall.dispose(); }
        }
        return texture;
    }

    private int caveWallChannel(int source, int tint, int wash) {
        int base = Math.round(source * tint / 255f);
        int washed = Math.round(base * (115f / 255f) + wash * (140f / 255f));
        return Math.round(washed * (90f / 255f));
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

