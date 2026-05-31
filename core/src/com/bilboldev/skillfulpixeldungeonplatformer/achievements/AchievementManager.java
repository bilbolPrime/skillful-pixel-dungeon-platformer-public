package com.bilboldev.skillfulpixeldungeonplatformer.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NightModeHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.DeathEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.AchievementService;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.DM300;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.SeniorMonk;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.AcidicScorpio;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.ShieldedBrute;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Tengu;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.AlbinoRat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Bandit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Goo;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Piranha;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

import java.util.EnumSet;
import java.util.HashMap;

public class AchievementManager {

    private static AchievementManager instance;

    private static final String PREFS_NAME = "achievements";
    private static final String KEY_GAMES_PLAYED = "games_played";
    private static final String KEY_UNLOCKED_PREFIX = "unlocked_";
    private static final String KEY_VICTORY_CLASS_PREFIX = "victory_class_";
    private static final String KEY_BOSS_1_CLASS_PREFIX = "boss_1_class_";
    private static final String KEY_BOSS_3_SUBCLASS_PREFIX = "boss_3_subclass_";
    private static final String KEY_RARE_SLAIN_PREFIX = "rare_slain_";
    private static final String KEY_PIRANHAS_SLAIN = "piranhas_slain";
    private static final String POTIONS_PACKAGE = "com.bilboldev.skillfulpixeldungeonplatformer.items.potions.";
    private static final String SCROLLS_PACKAGE = "com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.";
    private static final String RINGS_PACKAGE = "com.bilboldev.skillfulpixeldungeonplatformer.items.rings.";
    private static final String WANDS_PACKAGE = "com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.";
    private static final HeroClass[] VICTORY_CLASSES = new HeroClass[]{
        HeroClass.WARRIOR,
        HeroClass.WIZARD,
        HeroClass.ROGUE,
        HeroClass.ARCHER
    };
    private static final int[] TRACKED_SUBCLASS_SKILLS = new int[]{
        Skills.GLADIATOR,
        Skills.BERSERKER,
        Skills.BATTLE_MAGE,
        Skills.WARLOCK,
        Skills.FREE_RUNNER,
        Skills.ASSASSIN,
        Skills.WARDEN,
        Skills.SNIPER
    };
    private static final String[] IDENTIFIABLE_POTION_CLASSES = new String[]{
        POTIONS_PACKAGE + "PotionOfLevitation",
        POTIONS_PACKAGE + "PotionOfParalyticGas",
        POTIONS_PACKAGE + "PotionOfToxicGas",
        POTIONS_PACKAGE + "PotionOfInvisibility",
        POTIONS_PACKAGE + "PotionOfMindVision",
        POTIONS_PACKAGE + "PotionOfStrength",
        POTIONS_PACKAGE + "PotionOfFrost",
        POTIONS_PACKAGE + "PotionOfMight",
        POTIONS_PACKAGE + "PotionOfExperience",
        POTIONS_PACKAGE + "PotionOfPurity",
        POTIONS_PACKAGE + "PotionOfLiquidFlame"
    };
    private static final String[] IDENTIFIABLE_SCROLL_CLASSES = new String[]{
        SCROLLS_PACKAGE + "ScrollOfBloodyRitual",
        SCROLLS_PACKAGE + "ScrollOfChallenge",
        SCROLLS_PACKAGE + "ScrollOfFrost",
        SCROLLS_PACKAGE + "ScrollOfEnchantment",
        SCROLLS_PACKAGE + "ScrollOfIdentify",
        SCROLLS_PACKAGE + "ScrollOfLullaby",
        SCROLLS_PACKAGE + "ScrollOfMirrorImage",
        SCROLLS_PACKAGE + "ScrollOfPsionicBlast",
        SCROLLS_PACKAGE + "ScrollOfRecharging",
        SCROLLS_PACKAGE + "ScrollOfMagicMapping",
        SCROLLS_PACKAGE + "ScrollOfReadiness",
        SCROLLS_PACKAGE + "ScrollOfRemoveCurse",
        SCROLLS_PACKAGE + "ScrollOfRefuge",
        SCROLLS_PACKAGE + "ScrollOfSkill",
        SCROLLS_PACKAGE + "ScrollOfSacrifice",
        SCROLLS_PACKAGE + "ScrollOfTeleportation",
        SCROLLS_PACKAGE + "ScrollOfTerror",
        SCROLLS_PACKAGE + "ScrollOfUpgrade",
        SCROLLS_PACKAGE + "ScrollOfWipeOut"
    };
    private static final String[] IDENTIFIABLE_RING_CLASSES = new String[]{
        RINGS_PACKAGE + "RingOfThorns",
        RINGS_PACKAGE + "RingOfShadows",
        RINGS_PACKAGE + "RingOfSatiety",
        RINGS_PACKAGE + "RingOfPower",
        RINGS_PACKAGE + "RingOfMending",
        RINGS_PACKAGE + "RingOfHerbalism",
        RINGS_PACKAGE + "RingOfHaste",
        RINGS_PACKAGE + "RingOfHaggler",
        RINGS_PACKAGE + "RingOfEvasion",
        RINGS_PACKAGE + "RingOfElements",
        RINGS_PACKAGE + "RingOfDetection",
        RINGS_PACKAGE + "RingOfAccuracy",
        RINGS_PACKAGE + "Gemstone"
    };
    private static final String[] IDENTIFIABLE_WAND_CLASSES = new String[]{
        WANDS_PACKAGE + "FireBallWand",
        WANDS_PACKAGE + "IncinerationWand",
        WANDS_PACKAGE + "WandOfAvalanche",
        WANDS_PACKAGE + "FireBoltWand",
        WANDS_PACKAGE + "WandOfAmok",
        WANDS_PACKAGE + "MagicMissileWand",
        WANDS_PACKAGE + "WandOfPoison",
        WANDS_PACKAGE + "WandOfFlock",
        WANDS_PACKAGE + "WandOfReach",
        WANDS_PACKAGE + "WandOfRegrowth",
        WANDS_PACKAGE + "WandOfLightning",
        WANDS_PACKAGE + "WandOfDisintegration",
        WANDS_PACKAGE + "WandOfBlink",
        WANDS_PACKAGE + "WandOfSlowness",
        WANDS_PACKAGE + "WandOfTeleportation"
    };
    private static final String RING_OF_HAGGLER_CLASS = RINGS_PACKAGE + "RingOfHaggler";
    private static final String RING_OF_THORNS_CLASS = RINGS_PACKAGE + "RingOfThorns";

    private static final EnumSet<Achievement> IMPLEMENTED_ACHIEVEMENTS = EnumSet.of(
            Achievement.MONSTERS_SLAIN_1,
            Achievement.MONSTERS_SLAIN_2,
            Achievement.MONSTERS_SLAIN_3,
            Achievement.MONSTERS_SLAIN_4,
            Achievement.GOLD_COLLECTED_1,
            Achievement.GOLD_COLLECTED_2,
            Achievement.GOLD_COLLECTED_3,
            Achievement.GOLD_COLLECTED_4,
            Achievement.LEVEL_REACHED_1,
            Achievement.LEVEL_REACHED_2,
            Achievement.LEVEL_REACHED_3,
            Achievement.LEVEL_REACHED_4,
            Achievement.BOSS_SLAIN_1,
            Achievement.BOSS_SLAIN_2,
            Achievement.BOSS_SLAIN_3,
            Achievement.BOSS_SLAIN_4,
            Achievement.ALL_POTIONS_IDENTIFIED,
            Achievement.ALL_SCROLLS_IDENTIFIED,
            Achievement.ALL_RINGS_IDENTIFIED,
            Achievement.ALL_WANDS_IDENTIFIED,
            Achievement.RING_OF_HAGGLER,
            Achievement.RING_OF_THORNS,
            Achievement.DEATH_FROM_FIRE,
            Achievement.DEATH_FROM_POISON,
            Achievement.DEATH_FROM_GAS,
            Achievement.DEATH_FROM_HUNGER,
            Achievement.NO_MONSTERS_SLAIN,
            Achievement.GRIM_WEAPON,
            Achievement.PIRANHAS,
            Achievement.MASTERY_COMBO,
            Achievement.YASD,
            Achievement.NIGHT_HUNTER,
            Achievement.BOSS_SLAIN_1_ALL_CLASSES,
            Achievement.BOSS_SLAIN_3_ALL_SUBCLASSES,
            Achievement.ALL_ITEMS_IDENTIFIED,
            Achievement.RARE,
            Achievement.STRENGTH_ATTAINED_1,
            Achievement.STRENGTH_ATTAINED_2,
            Achievement.STRENGTH_ATTAINED_3,
            Achievement.STRENGTH_ATTAINED_4,
            Achievement.ITEM_LEVEL_1,
            Achievement.ITEM_LEVEL_2,
            Achievement.ITEM_LEVEL_3,
            Achievement.ITEM_LEVEL_4,
            Achievement.VICTORY,
            Achievement.VICTORY_ALL_CLASSES,
            Achievement.HAPPY_END,
            Achievement.SUPPORTER,
            Achievement.FOOD_EATEN_1,
            Achievement.FOOD_EATEN_2,
            Achievement.FOOD_EATEN_3,
            Achievement.FOOD_EATEN_4,
            Achievement.GAMES_PLAYED_1,
            Achievement.GAMES_PLAYED_2,
            Achievement.GAMES_PLAYED_3,
            Achievement.GAMES_PLAYED_4
    );

    private AchievementService service;
    private boolean trackingRun;
    private int killsThisGame;
    private HashMap<Integer, Integer> killsByDepthThisGame = new HashMap<Integer, Integer>();
    private int goldCollectedThisGame;
    private int foodEatenThisGame;
    private int nightKillStreak;

    private AchievementManager() {
    }

    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    public void setService(AchievementService service) {
        this.service = service;
        if (service != null) {
            service.synchronize(getUnlockedAchievements());
        }
    }

    public void onGameStarted() {
        onGameStarted(true);
    }

    public void onGameStarted(boolean countAsNewGame) {
        trackingRun = true;
        killsThisGame = 0;
        killsByDepthThisGame.clear();
        goldCollectedThisGame = 0;
        foodEatenThisGame = 0;
        nightKillStreak = 0;

        refreshCurrentStateAchievements();

        if (!countAsNewGame) {
            return;
        }

        int gamesPlayed = getGamesPlayed() + 1;
        saveGamesPlayed(gamesPlayed);
        unlockIfReached(gamesPlayed, 10, Achievement.GAMES_PLAYED_1);
        unlockIfReached(gamesPlayed, 100, Achievement.GAMES_PLAYED_2);
        unlockIfReached(gamesPlayed, 500, Achievement.GAMES_PLAYED_3);
        unlockIfReached(gamesPlayed, 2000, Achievement.GAMES_PLAYED_4);
    }

    public void onEnemyKilled() {
        onEnemyKilled(null);
    }

    public void onEnemyKilled(Mob mob) {
        if (!trackingRun || mob == null) {
            return;
        }

        killsThisGame++;
        int currentDepth = MapHelper.getInstance().getDepth();
        Integer killsThisDepth = killsByDepthThisGame.get(currentDepth);
        killsByDepthThisGame.put(currentDepth, killsThisDepth == null ? 1 : killsThisDepth + 1);
        if (NightModeHelper.isNightModeActive()) {
            nightKillStreak++;
        }
        else {
            nightKillStreak = 0;
        }

        unlockIfReached(killsThisGame, 10, Achievement.MONSTERS_SLAIN_1);
        unlockIfReached(killsThisGame, 50, Achievement.MONSTERS_SLAIN_2);
        unlockIfReached(killsThisGame, 150, Achievement.MONSTERS_SLAIN_3);
        unlockIfReached(killsThisGame, 250, Achievement.MONSTERS_SLAIN_4);
        onNightKillStreak(nightKillStreak);

        if (mob instanceof Goo) {
            unlock(Achievement.BOSS_SLAIN_1);
            markFirstBossForCurrentClass();
            if (hasFirstBossKillsForAllClasses()) {
                unlock(Achievement.BOSS_SLAIN_1_ALL_CLASSES);
            }
        }
        else if (mob instanceof Tengu) {
            unlock(Achievement.BOSS_SLAIN_2);
        }
        else if (mob instanceof DM300) {
            unlock(Achievement.BOSS_SLAIN_3);
            markThirdBossForCurrentSubclass();
            if (hasThirdBossKillsForAllSubclasses()) {
                unlock(Achievement.BOSS_SLAIN_3_ALL_SUBCLASSES);
            }
        }
        else if (mob instanceof DwarfKing) {
            unlock(Achievement.BOSS_SLAIN_4);
        }

        if (mob instanceof Piranha) {
            int piranhasSlain = getCounter(KEY_PIRANHAS_SLAIN) + 1;
            saveCounter(KEY_PIRANHAS_SLAIN, piranhasSlain);
            unlockIfReached(piranhasSlain, 6, Achievement.PIRANHAS);
        }

        if (mob instanceof AlbinoRat) {
            markRareKill(AlbinoRat.class);
        }
        else if (mob instanceof Bandit) {
            markRareKill(Bandit.class);
        }
        else if (mob instanceof ShieldedBrute) {
            markRareKill(ShieldedBrute.class);
        }
        else if (mob instanceof SeniorMonk) {
            markRareKill(SeniorMonk.class);
        }
        else if (mob instanceof AcidicScorpio) {
            markRareKill(AcidicScorpio.class);
        }

        if (hasAllRareKills()) {
            unlock(Achievement.RARE);
        }

        Weapon lastDamagingItem = mob.getLastDamagingItem();
        if (lastDamagingItem != null && lastDamagingItem.getPrefix() instanceof DeathEnchantment) {
            unlock(Achievement.GRIM_WEAPON);
        }
    }

    public void onGoldCollected(int amount) {
        if (!trackingRun || amount <= 0) {
            return;
        }

        goldCollectedThisGame += amount;
        unlockIfReached(goldCollectedThisGame, 100, Achievement.GOLD_COLLECTED_1);
        unlockIfReached(goldCollectedThisGame, 500, Achievement.GOLD_COLLECTED_2);
        unlockIfReached(goldCollectedThisGame, 2500, Achievement.GOLD_COLLECTED_3);
        unlockIfReached(goldCollectedThisGame, 7500, Achievement.GOLD_COLLECTED_4);
    }

    public void onLevelReached(int level) {
        if (!trackingRun) {
            return;
        }

        unlockIfReached(level, 6, Achievement.LEVEL_REACHED_1);
        unlockIfReached(level, 12, Achievement.LEVEL_REACHED_2);
        unlockIfReached(level, 18, Achievement.LEVEL_REACHED_3);
        unlockIfReached(level, 24, Achievement.LEVEL_REACHED_4);
        checkStrengthAchievements(UnitHelper.getInstance().getHero() != null
                ? UnitHelper.getInstance().getHero().getStrength()
                : 0);
    }

    public void onFoodEaten() {
        if (!trackingRun) {
            return;
        }

        foodEatenThisGame++;
        unlockIfReached(foodEatenThisGame, 10, Achievement.FOOD_EATEN_1);
        unlockIfReached(foodEatenThisGame, 20, Achievement.FOOD_EATEN_2);
        unlockIfReached(foodEatenThisGame, 30, Achievement.FOOD_EATEN_3);
        unlockIfReached(foodEatenThisGame, 40, Achievement.FOOD_EATEN_4);
    }

    public void onLevelCompleted(int completedDepth) {
        if (!trackingRun) {
            return;
        }

        Integer killsOnCompletedDepth = killsByDepthThisGame.get(completedDepth);
        if (killsOnCompletedDepth == null || killsOnCompletedDepth == 0) {
            unlock(Achievement.NO_MONSTERS_SLAIN);
        }
    }

    public void onDeathFromFire() {
        if (trackingRun) {
            unlock(Achievement.DEATH_FROM_FIRE);
            checkBadgeSetAchievements();
        }
    }

    public void onDeathFromPoison() {
        if (trackingRun) {
            unlock(Achievement.DEATH_FROM_POISON);
            checkBadgeSetAchievements();
        }
    }

    public void onDeathFromGas() {
        if (trackingRun) {
            unlock(Achievement.DEATH_FROM_GAS);
            checkBadgeSetAchievements();
        }
    }

    public void onDeathFromHunger() {
        if (trackingRun) {
            unlock(Achievement.DEATH_FROM_HUNGER);
            checkBadgeSetAchievements();
        }
    }

    public void onItemIdentified(Item item) {
        if (!trackingRun || item == null) {
            return;
        }

        checkIdentificationAchievements();
    }

    public void onItemObtained(Item item) {
        if (!trackingRun || item == null) {
            return;
        }

        checkItemLevelAchievements(item);
    }

    public void onItemUpgraded(Item item) {
        onItemObtained(item);
    }

    public void onStrengthChanged(int strength) {
        if (!trackingRun) {
            return;
        }

        checkStrengthAchievements(strength);
    }

    public boolean isImplemented(Achievement achievement) {
        return IMPLEMENTED_ACHIEVEMENTS.contains(achievement);
    }

    public boolean isUnlocked(Achievement achievement) {
        return Gdx.app.getPreferences(PREFS_NAME)
                .getBoolean(KEY_UNLOCKED_PREFIX + achievement.getId(), false);
    }

    public Iterable<Achievement> getUnlockedAchievements() {
        EnumSet<Achievement> unlocked = EnumSet.noneOf(Achievement.class);
        for (Achievement achievement : Achievement.values()) {
            if (isUnlocked(achievement)) {
                unlocked.add(achievement);
            }
        }

        return unlocked;
    }

    public void onVictory() {
        if (!trackingRun) {
            return;
        }

        markVictoryForCurrentClass();
        unlock(Achievement.VICTORY);

        if (hasVictoriesForAllClasses()) {
            unlock(Achievement.VICTORY_ALL_CLASSES);
        }
    }

    public void onHappyEnd() {
        if (!trackingRun) {
            return;
        }

        unlock(Achievement.HAPPY_END);
    }

    public void onSupporterOwned() {
        applySupporterOwnership(true);
    }

    public void applySupporterOwnership(boolean showFeedback) {
        unlock(Achievement.SUPPORTER, showFeedback);
    }

    public void applyPlatformSync(Achievement achievement) {
        if (achievement == null || isUnlocked(achievement)) {
            return;
        }

        markUnlocked(achievement);
    }

    public void applyPlatformSync(Iterable<Achievement> achievements) {
        if (achievements == null) {
            return;
        }

        for (Achievement achievement : achievements) {
            applyPlatformSync(achievement);
        }
    }

    public void onGladiatorCombo(int comboCount) {
        if (!trackingRun) {
            return;
        }

        if (comboCount >= 7) {
            unlock(Achievement.MASTERY_COMBO);
        }
    }

    public void onNightKillStreak(int streak) {
        if (!trackingRun) {
            return;
        }

        if (streak >= 15) {
            unlock(Achievement.NIGHT_HUNTER);
        }
    }

    private void unlock(Achievement achievement) {
        unlock(achievement, true);
    }

    private void unlock(Achievement achievement, boolean showFeedback) {
        if (isUnlocked(achievement)) {
            return;
        }
        markUnlocked(achievement);
        if (showFeedback) {
            SoundHelper.GetSingleton().play(Sounds.BADGE, 0f, 1f);
            UIHelper.getInstance().showAchievementBanner(achievement);
        }
        if (service != null) {
            service.unlock(achievement);
        }
    }

    private void markUnlocked(Achievement achievement) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(KEY_UNLOCKED_PREFIX + achievement.getId(), true);
        prefs.flush();
    }

    private void markVictoryForCurrentClass() {
        HeroClass heroClass = getTrackedVictoryClass();
        if (heroClass == null) {
            return;
        }

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(KEY_VICTORY_CLASS_PREFIX + heroClass.name(), true);
        prefs.flush();
    }

    private void markFirstBossForCurrentClass() {
        HeroClass heroClass = getTrackedVictoryClass();
        if (heroClass == null) {
            return;
        }

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(KEY_BOSS_1_CLASS_PREFIX + heroClass.name(), true);
        prefs.flush();
    }

    private boolean hasFirstBossKillsForAllClasses() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        for (HeroClass heroClass : VICTORY_CLASSES) {
            if (!prefs.getBoolean(KEY_BOSS_1_CLASS_PREFIX + heroClass.name(), false)) {
                return false;
            }
        }

        return true;
    }

    private void markThirdBossForCurrentSubclass() {
        int subclassSkillId = getTrackedSubclassSkill();
        if (subclassSkillId == -1) {
            return;
        }

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(KEY_BOSS_3_SUBCLASS_PREFIX + subclassSkillId, true);
        prefs.flush();
    }

    private boolean hasThirdBossKillsForAllSubclasses() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        for (int subclassSkillId : TRACKED_SUBCLASS_SKILLS) {
            if (!prefs.getBoolean(KEY_BOSS_3_SUBCLASS_PREFIX + subclassSkillId, false)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasVictoriesForAllClasses() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        for (HeroClass heroClass : VICTORY_CLASSES) {
            if (!prefs.getBoolean(KEY_VICTORY_CLASS_PREFIX + heroClass.name(), false)) {
                return false;
            }
        }

        return true;
    }

    private HeroClass getTrackedVictoryClass() {
        if (UnitHelper.getInstance().getHero() == null) {
            return null;
        }

        HeroClass heroClass = UnitHelper.getInstance().getHero().getHeroClass();
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

    private int getTrackedSubclassSkill() {
        if (UnitHelper.getInstance().getHero() == null) {
            return -1;
        }

        for (int subclassSkillId : TRACKED_SUBCLASS_SKILLS) {
            if (UnitHelper.getInstance().getHero().hasSkill(subclassSkillId)) {
                return subclassSkillId;
            }
        }

        return -1;
    }

    private int getGamesPlayed() {
        return Gdx.app.getPreferences(PREFS_NAME).getInteger(KEY_GAMES_PLAYED, 0);
    }

    private int getCounter(String key) {
        return Gdx.app.getPreferences(PREFS_NAME).getInteger(key, 0);
    }

    private void saveGamesPlayed(int count) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(KEY_GAMES_PLAYED, count);
        prefs.flush();
    }

    private void saveCounter(String key, int count) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(key, count);
        prefs.flush();
    }

    private void markRareKill(Class<? extends Mob> mobClass) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(KEY_RARE_SLAIN_PREFIX + mobClass.getSimpleName(), true);
        prefs.flush();
    }

    private boolean hasAllRareKills() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs.getBoolean(KEY_RARE_SLAIN_PREFIX + AlbinoRat.class.getSimpleName(), false)
                && prefs.getBoolean(KEY_RARE_SLAIN_PREFIX + Bandit.class.getSimpleName(), false)
                && prefs.getBoolean(KEY_RARE_SLAIN_PREFIX + ShieldedBrute.class.getSimpleName(), false)
                && prefs.getBoolean(KEY_RARE_SLAIN_PREFIX + SeniorMonk.class.getSimpleName(), false)
                && prefs.getBoolean(KEY_RARE_SLAIN_PREFIX + AcidicScorpio.class.getSimpleName(), false);
    }

    private void unlockIfReached(int value, int threshold, Achievement achievement) {
        if (value >= threshold) {
            unlock(achievement);
        }
    }

    private void refreshCurrentStateAchievements() {
        checkIdentificationAchievements();
        checkBadgeSetAchievements();

        if (UnitHelper.getInstance().getHero() != null) {
            checkStrengthAchievements(UnitHelper.getInstance().getHero().getStrength());
        }

        for (Item item : InventoryHelper.getInstance().getItems()) {
            checkItemLevelAchievements(item);
        }
    }

    private void checkIdentificationAchievements() {
        if (areAllIdentified(IDENTIFIABLE_POTION_CLASSES)) {
            unlock(Achievement.ALL_POTIONS_IDENTIFIED);
        }

        if (areAllIdentified(IDENTIFIABLE_SCROLL_CLASSES)) {
            unlock(Achievement.ALL_SCROLLS_IDENTIFIED);
        }

        if (areAllIdentified(IDENTIFIABLE_RING_CLASSES)) {
            unlock(Achievement.ALL_RINGS_IDENTIFIED);
        }

        if (areAllIdentified(IDENTIFIABLE_WAND_CLASSES)) {
            unlock(Achievement.ALL_WANDS_IDENTIFIED);
        }

        if (ItemIdentityHelper.getInstance().isIdentified(RING_OF_HAGGLER_CLASS)) {
            unlock(Achievement.RING_OF_HAGGLER);
        }

        if (ItemIdentityHelper.getInstance().isIdentified(RING_OF_THORNS_CLASS)) {
            unlock(Achievement.RING_OF_THORNS);
        }

        if (isUnlocked(Achievement.ALL_POTIONS_IDENTIFIED)
                && isUnlocked(Achievement.ALL_SCROLLS_IDENTIFIED)
                && isUnlocked(Achievement.ALL_RINGS_IDENTIFIED)
                && isUnlocked(Achievement.ALL_WANDS_IDENTIFIED)) {
            unlock(Achievement.ALL_ITEMS_IDENTIFIED);
        }
    }

    private void checkBadgeSetAchievements() {
        if (isUnlocked(Achievement.DEATH_FROM_FIRE)
                && isUnlocked(Achievement.DEATH_FROM_POISON)
                && isUnlocked(Achievement.DEATH_FROM_GAS)
                && isUnlocked(Achievement.DEATH_FROM_HUNGER)) {
            unlock(Achievement.YASD);
        }
    }

    private boolean areAllIdentified(String[] itemClassNames) {
        for (String itemClassName : itemClassNames) {
            if (!ItemIdentityHelper.getInstance().isIdentified(itemClassName)) {
                return false;
            }
        }

        return true;
    }

    private void checkStrengthAchievements(int strength) {
        unlockIfReached(strength, 13, Achievement.STRENGTH_ATTAINED_1);
        unlockIfReached(strength, 15, Achievement.STRENGTH_ATTAINED_2);
        unlockIfReached(strength, 17, Achievement.STRENGTH_ATTAINED_3);
        unlockIfReached(strength, 19, Achievement.STRENGTH_ATTAINED_4);
    }

    private void checkItemLevelAchievements(Item item) {
        if (item == null || !item.supportsLevel()) {
            return;
        }

        int level = item.getLevel();
        unlockIfReached(level, 3, Achievement.ITEM_LEVEL_1);
        unlockIfReached(level, 6, Achievement.ITEM_LEVEL_2);
        unlockIfReached(level, 9, Achievement.ITEM_LEVEL_3);
        unlockIfReached(level, 12, Achievement.ITEM_LEVEL_4);
    }
}
