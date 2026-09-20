package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.math.RandomXS128;

import java.util.UUID;

public class RandomHelper {

    private static final long SEED_MIXER = 0x9E3779B97F4A7C15L;

    private final RandomXS128 runtimeRandom;
    private RandomXS128 generationRandom;
    private RandomXS128 previewRandom;
    private long runSeed;

    private static final RandomHelper ourInstance = new RandomHelper();

    public static RandomHelper getInstance() {
        return ourInstance;
    }

    private RandomHelper()
    {
        runtimeRandom = new RandomXS128();
        runSeed = runtimeRandom.nextLong();
    }

    private RandomXS128 activeRandom() {
        if (previewRandom != null) return previewRandom;
        return generationRandom != null ? generationRandom : runtimeRandom;
    }


    public <T> T withPreviewRandom(java.util.function.Supplier<T> factory) {
        RandomXS128 previous = previewRandom;
        previewRandom = new RandomXS128(0x4D454E55L);
        try { return factory.get(); }
        finally { previewRandom = previous; }
    }

    public long newRunSeed() {
        return runtimeRandom.nextLong();
    }

    public void setRunSeed(long runSeed) {
        this.runSeed = runSeed;
    }

    public long getRunSeed() {
        return runSeed;
    }

    public void beginLevelGeneration(int depth) {
        generationRandom = new RandomXS128(levelSeed(depth), levelSeed(depth + 1000));
    }

    public void endLevelGeneration() {
        generationRandom = null;
    }

    public boolean isGeneratingLevel() {
        return generationRandom != null;
    }

    public long levelSeed(int depth) {
        return mix(runSeed + SEED_MIXER * depth);
    }

    public RandomXS128 createRoomRandom(int depth, String roomIdentifier, long salt) {
        long roomHash = hashString(roomIdentifier);
        long primarySeed = mix(levelSeed(depth) ^ roomHash ^ salt);
        long secondarySeed = mix(levelSeed(depth + 1000) ^ Long.rotateLeft(roomHash, 19) ^ (salt + SEED_MIXER));
        return new RandomXS128(primarySeed, secondarySeed);
    }


    public <T> T withRoomGenerationRandom(int depth, String identifier, long salt, java.util.function.Supplier<T> action) {
        RandomXS128 previous = generationRandom;
        generationRandom = createRoomRandom(depth, identifier, salt);
        try { return action.get(); }
        finally { generationRandom = previous; }
    }


    public int randomInt(int maxExcluded)
    {
        if(maxExcluded == 0){
            return 0;
        }

        return activeRandom().nextInt(maxExcluded);
    }

    public float randomFloat(float maxExcluded)
    {
        if(maxExcluded == 0){
            return 0;
        }

        return activeRandom().nextFloat() * maxExcluded;
    }

    public boolean randomChance(int percentageToOccur){
        return randomInt(100) < percentageToOccur;
    }

    public boolean randomBoolean(){ return activeRandom().nextBoolean(); }

    public String uniqueId(){
        if (previewRandom != null) {
            return Long.toHexString(previewRandom.nextLong()) + Long.toHexString(previewRandom.nextLong());
        }
        if (generationRandom != null) {
            return Long.toHexString(generationRandom.nextLong()) + Long.toHexString(generationRandom.nextLong());
        }

        return UUID.randomUUID().toString();
    }

    private long mix(long value) {
        long mixed = value;
        mixed ^= mixed >>> 33;
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= mixed >>> 33;
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= mixed >>> 33;
        return mixed;
    }

    private long hashString(String value) {
        long hash = 1125899906842597L;
        if (value == null) {
            return hash;
        }

        for (int index = 0; index < value.length(); index++) {
            hash = 31 * hash + value.charAt(index);
        }

        return hash;
    }
}

