package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public final class DifficultyHelper {
    private static final String PREFS_NAME = "difficulty-progression";
    private static final String KEY_UNLOCK_PREFIX = "difficulty_unlock_";
    private static final DifficultyHelper INSTANCE = new DifficultyHelper();

    public enum Difficulty {
        NORMAL("Normal", 20, 1f, 1f, 1f, 1f, "Current game state"),
        NIGHTMARE("Nightmare", 40, 1.30f, 1.25f, 1.05f, 1f, "Enemy health +30%, damage +25%, accuracy +5%"),
        HELL("Hell", 60, 1.60f, 1.55f, 1.10f, 1f, "Enemy health +60%, damage +55%, accuracy +10%");

        private final String displayName;
        private final int championChancePercent;
        private final float enemyHealthMultiplier;
        private final float enemyDamageMultiplier;
        private final float enemyAccuracyMultiplier;
        private final float enemyDefenseMultiplier;
        private final String summary;

        Difficulty(String displayName, int championChancePercent, float enemyHealthMultiplier,
                   float enemyDamageMultiplier, float enemyAccuracyMultiplier,
                   float enemyDefenseMultiplier, String summary) {
            this.displayName = displayName;
            this.championChancePercent = championChancePercent;
            this.enemyHealthMultiplier = enemyHealthMultiplier;
            this.enemyDamageMultiplier = enemyDamageMultiplier;
            this.enemyAccuracyMultiplier = enemyAccuracyMultiplier;
            this.enemyDefenseMultiplier = enemyDefenseMultiplier;
            this.summary = summary;
        }

        public String getDisplayName() {
            switch (this) {
                case NIGHTMARE:
                    return Messages.get("custom.difficulty.nightmare");
                case HELL:
                    return Messages.get("custom.difficulty.hell");
                case NORMAL:
                default:
                    return Messages.get("custom.difficulty.normal");
            }
        }

        public int getChampionChancePercent() {
            return championChancePercent;
        }

        public float getEnemyHealthMultiplier() {
            return enemyHealthMultiplier;
        }

        public float getEnemyDamageMultiplier() {
            return enemyDamageMultiplier;
        }

        public float getEnemyAccuracyMultiplier() {
            return enemyAccuracyMultiplier;
        }

        public float getEnemyDefenseMultiplier() {
            return enemyDefenseMultiplier;
        }

        public String getSummary() {
            switch (this) {
                case NIGHTMARE:
                    return Messages.get("custom.difficulty.nightmare.summary");
                case HELL:
                    return Messages.get("custom.difficulty.hell.summary");
                case NORMAL:
                default:
                    return Messages.get("custom.difficulty.normal.summary");
            }
        }

        public Difficulty next() {
            int nextOrdinal = ordinal() + 1;
            Difficulty[] values = values();
            return nextOrdinal >= values.length ? null : values[nextOrdinal];
        }

        public static Difficulty fromName(String name) {
            if (name == null || name.isEmpty()) {
                return NORMAL;
            }

            for (Difficulty difficulty : values()) {
                if (difficulty.name().equalsIgnoreCase(name) || difficulty.displayName.equalsIgnoreCase(name)) {
                    return difficulty;
                }
            }

            return NORMAL;
        }
    }

    private Difficulty currentDifficulty = Difficulty.NORMAL;

    public static DifficultyHelper getInstance() {
        return INSTANCE;
    }

    private DifficultyHelper() {
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(Difficulty difficulty) {
        currentDifficulty = difficulty == null ? Difficulty.NORMAL : difficulty;
    }


    public float getEnemyDamageMultiplier(Unit source) {
        return source instanceof Mob && !source.isFriendly ? currentDifficulty.getEnemyDamageMultiplier() : 1f;
    }


    public float scaleEnemyDamage(Unit source, float damage) {
        return damage * getEnemyDamageMultiplier(source);
    }

    public boolean isUnlocked(HeroClass heroClass, Difficulty difficulty) {
        if (difficulty == null || difficulty == Difficulty.NORMAL) {
            return true;
        }

        HeroClass trackedClass = normalizeTrackedClass(heroClass);
        if (trackedClass == null) {
            return false;
        }

        return prefs().getBoolean(unlockKey(trackedClass, difficulty), false);
    }

    public String getUnlockRequirement(HeroClass heroClass, Difficulty difficulty) {
        if (difficulty == null || difficulty == Difficulty.NORMAL) {
            return "Available";
        }

        Difficulty prerequisite = difficulty == Difficulty.NIGHTMARE ? Difficulty.NORMAL : Difficulty.NIGHTMARE;
        HeroClass trackedClass = normalizeTrackedClass(heroClass);
        String className = trackedClass == null ? "that class" : trackedClass.getName();
        return Messages.get("custom.difficulty.unlock_requirement",
            new Object[]{prerequisite.getDisplayName(), className});
    }

    public void recordVictory(HeroClass heroClass) {
        HeroClass trackedClass = normalizeTrackedClass(heroClass);
        if (trackedClass == null) {
            return;
        }

        Difficulty nextDifficulty = currentDifficulty.next();
        if (nextDifficulty == null) {
            return;
        }

        unlock(trackedClass, nextDifficulty);
    }

    private void unlock(HeroClass heroClass, Difficulty difficulty) {
        if (heroClass == null || difficulty == null || difficulty == Difficulty.NORMAL || isUnlocked(heroClass, difficulty)) {
            return;
        }

        Preferences prefs = prefs();
        prefs.putBoolean(unlockKey(heroClass, difficulty), true);
        prefs.flush();
    }

    private HeroClass normalizeTrackedClass(HeroClass heroClass) {
        if (heroClass == null) {
            return null;
        }

        switch (heroClass) {
            case WARRIOR:
            case WIZARD:
            case ROGUE:
            case ARCHER:
            case NECROMANCER:
            case MERCENARY:
                return heroClass;
            default:
                return null;
        }
    }

    private String unlockKey(HeroClass heroClass, Difficulty difficulty) {
        return KEY_UNLOCK_PREFIX + heroClass.name() + "_" + difficulty.name();
    }

    private Preferences prefs() {
        return Gdx.app.getPreferences(PREFS_NAME + accountSuffix);
    }

    private String accountSuffix = "";
    public void useSteamAccount(String account) { accountSuffix = "-steam-" + account; }
    public java.util.Set<String> unlockedKeys() {
        java.util.Set<String> keys = new java.util.TreeSet<>();
        for (HeroClass hero : HeroClass.values()) for (Difficulty difficulty : Difficulty.values())
            if (difficulty != Difficulty.NORMAL && isUnlocked(hero, difficulty)) keys.add(hero.name() + ":" + difficulty.name());
        return keys;
    }
    public void mergeUnlocks(java.util.Set<String> keys) {
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length == 2) unlock(HeroClass.valueOf(parts[0]), Difficulty.valueOf(parts[1]));
        }
    }
}
