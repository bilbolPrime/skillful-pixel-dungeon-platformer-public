package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public final class AssetHelper {
    private static AssetHelper instance;

    private final AssetManager assetManager;
    private final TextureLoader.TextureParameter textureParameter;
    private boolean loaded;

    public static AssetHelper getInstance() {
        if (instance == null) {
            instance = new AssetHelper();
        }

        return instance;
    }

    private AssetHelper() {
        assetManager = new AssetManager();
        textureParameter = new TextureLoader.TextureParameter();
        textureParameter.minFilter = Texture.TextureFilter.Nearest;
        textureParameter.magFilter = Texture.TextureFilter.Nearest;
    }

    public void loadAll() {
        if (loaded) {
            return;
        }

        if (!loadTexturesFromManifest(textureParameter)) {
            loadTopLevelTextures(textureParameter);

            FileHandle imagesDirectory = Gdx.files.internal("images");
            if (imagesDirectory.exists()) {
                loadTexturesRecursively(imagesDirectory, textureParameter);
            }
        }

        for (Sounds sound : Sounds.values()) {
            if (sound.isMusic()) {
                assetManager.load(sound.getPath(), Music.class);
            } else {
                assetManager.load(sound.getPath(), Sound.class);
            }
        }

        assetManager.finishLoading();
        loaded = true;
    }

    public Texture getTexture(String path) {
        ensureLoaded();

        String normalizedPath = normalize(path);
        if (!assetManager.isLoaded(normalizedPath, Texture.class)) {
            assetManager.load(normalizedPath, Texture.class, textureParameter);
            assetManager.finishLoadingAsset(normalizedPath);
        }

        return assetManager.get(normalizedPath, Texture.class);
    }

    public Sound getSound(Sounds sound) {
        ensureLoaded();
        return assetManager.get(sound.getPath(), Sound.class);
    }

    public Music getMusic(Sounds sound) {
        ensureLoaded();
        return assetManager.get(sound.getPath(), Music.class);
    }

    public void dispose() {
        assetManager.dispose();
        loaded = false;
        instance = null;
    }

    private boolean loadTexturesFromManifest(TextureLoader.TextureParameter textureParameter) {
        FileHandle manifest = Gdx.files.internal("asset-manifest.txt");
        if (!manifest.exists()) {
            return false;
        }

        String[] lines = manifest.readString("UTF-8").split("\\r?\\n");
        for (String line : lines) {
            String normalizedPath = normalize(line.trim());
            if (normalizedPath.isEmpty() || normalizedPath.startsWith("#") || !isTexture(normalizedPath)) {
                continue;
            }

            if (!assetManager.isLoaded(normalizedPath, Texture.class)) {
                assetManager.load(normalizedPath, Texture.class, textureParameter);
            }
        }

        return true;
    }

    private void loadTopLevelTextures(TextureLoader.TextureParameter textureParameter) {
        for (FileHandle child : Gdx.files.internal("").list()) {
            if (!child.isDirectory() && isTexture(child.path())) {
                String normalizedPath = normalize(child.path());
                if (!assetManager.isLoaded(normalizedPath, Texture.class)) {
                    assetManager.load(normalizedPath, Texture.class, textureParameter);
                }
            }
        }
    }

    private void loadTexturesRecursively(FileHandle handle, TextureLoader.TextureParameter textureParameter) {
        for (FileHandle child : handle.list()) {
            if (child.isDirectory()) {
                loadTexturesRecursively(child, textureParameter);
                continue;
            }

            if (isTexture(child.path())) {
                String normalizedPath = normalize(child.path());
                if (!assetManager.isLoaded(normalizedPath, Texture.class)) {
                    assetManager.load(normalizedPath, Texture.class, textureParameter);
                }
            }
        }
    }

    private boolean isTexture(String path) {
        String normalized = normalize(path).toLowerCase();
        return normalized.endsWith(".png") || normalized.endsWith(".jpg") || normalized.endsWith(".jpeg");
    }

    private String normalize(String path) {
        String normalized = path.replace('\\', '/');

        while (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        return normalized;
    }

    private void ensureLoaded() {
        if (!loaded) {
            throw new IllegalStateException("Assets must be loaded before use.");
        }
    }
}