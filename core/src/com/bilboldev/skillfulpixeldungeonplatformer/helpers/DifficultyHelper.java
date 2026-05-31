package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public final class DifficultyHelper {
    private static final String PREFS_NAME = "difficulty-progression";
    private static final String KEY_UNLOCK_PREFIX = "difficulty_unlock_";
    private static final DifficultyHelper INSTANCE = new DifficultyHelper();

    public enum Difficulty {
        NORMAL("Normal", 20, 1f, 1f, "Current game state"),
        NIGHTMARE("Nightmare", 40, 1.2f, 1f, "+20% enemy health and attack"),
        HELL("Hell", 60, 1.4f, 1f, "+40% enemy health and attack");

        private final String displayName;
        private final int championChancePercent;
        private final float enemyStatMultiplier;
        private final float enemyDefenseMultiplier;
        private final String summary;

        Difficulty(String displayName, int championChancePercent, float enemyStatMultiplier,
                   float enemyDefenseMultiplier, String summary) {
            this.displayName = displayName;
            this.championChancePercent = championChancePercent;
            this.enemyStatMultiplier = enemyStatMultiplier;
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

        public float getEnemyStatMultiplier() {
            return enemyStatMultiplier;
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
                return heroClass;
            default:
                return null;
        }
    }

    private String unlockKey(HeroClass heroClass, Difficulty difficulty) {
        return KEY_UNLOCK_PREFIX + heroClass.name() + "_" + difficulty.name();
    }

    private Preferences prefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }
}