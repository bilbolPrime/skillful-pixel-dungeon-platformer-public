package com.bilboldev.skillfulpixeldungeonplatformer.achievements;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

import java.util.Locale;

public enum Achievement {
    MONSTERS_SLAIN_1("MONSTERS_SLAIN_1", "10 enemies slain", "10 enemies slain", "Slay at least 10 enemies in a run."),
    MONSTERS_SLAIN_2("MONSTERS_SLAIN_2", "50 enemies slain", "50 enemies slain", "Slay at least 50 enemies in a run."),
    MONSTERS_SLAIN_3("MONSTERS_SLAIN_3", "150 enemies slain", "150 enemies slain", "Slay at least 150 enemies in a run."),
    MONSTERS_SLAIN_4("MONSTERS_SLAIN_4", "250 enemies slain", "250 enemies slain", "Slay at least 250 enemies in a run."),
    GOLD_COLLECTED_1("GOLD_COLLECTED_1", "100 gold collected", "100 gold collected", "Collect at least 100 gold in a run."),
    GOLD_COLLECTED_2("GOLD_COLLECTED_2", "500 gold collected", "500 gold collected", "Collect at least 500 gold in a run."),
    GOLD_COLLECTED_3("GOLD_COLLECTED_3", "2500 gold collected", "2500 gold collected", "Collect at least 2500 gold in a run."),
    GOLD_COLLECTED_4("GOLD_COLLECTED_4", "7500 gold collected", "7500 gold collected", "Collect at least 7500 gold in a run."),
    LEVEL_REACHED_1("LEVEL_REACHED_1", "Level 6 reached", "Level 6 reached", "Reach hero level 6 in a run."),
    LEVEL_REACHED_2("LEVEL_REACHED_2", "Level 12 reached", "Level 12 reached", "Reach hero level 12 in a run."),
    LEVEL_REACHED_3("LEVEL_REACHED_3", "Level 18 reached", "Level 18 reached", "Reach hero level 18 in a run."),
    LEVEL_REACHED_4("LEVEL_REACHED_4", "Level 24 reached", "Level 24 reached", "Reach hero level 24 in a run."),
    BOSS_SLAIN_1("BOSS_SLAIN_1", "1st boss slain", "1st boss slain", "Defeat the 1st boss."),
    BOSS_SLAIN_2("BOSS_SLAIN_2", "2nd boss slain", "2nd boss slain", "Defeat the 2nd boss."),
    BOSS_SLAIN_3("BOSS_SLAIN_3", "3rd boss slain", "3rd boss slain", "Defeat the 3rd boss."),
    BOSS_SLAIN_4("BOSS_SLAIN_4", "4th boss slain", "4th boss slain", "Defeat the 4th boss."),
    ALL_POTIONS_IDENTIFIED("ALL_POTIONS_IDENTIFIED", "All potions identified", "All potions identified", "Make every potion type known/identified."),
    ALL_SCROLLS_IDENTIFIED("ALL_SCROLLS_IDENTIFIED", "All scrolls identified", "All scrolls identified", "Make every scroll type known/identified."),
    ALL_RINGS_IDENTIFIED("ALL_RINGS_IDENTIFIED", "All rings identified", "All rings identified", "Make every ring type known/identified."),
    ALL_WANDS_IDENTIFIED("ALL_WANDS_IDENTIFIED", "All wands identified", "All wands identified", "Make every wand type known/identified."),
    RING_OF_HAGGLER("RING_OF_HAGGLER", "Ring of Haggler obtained", "Ring of Haggler obtained", "Make the Ring of Haggler known/identified."),
    RING_OF_THORNS("RING_OF_THORNS", "Ring of Thorns obtained", "Ring of Thorns obtained", "Make the Ring of Thorns known/identified."),
    VICTORY("VICTORY", "Amulet of Yendor obtained", "Amulet of Yendor obtained", "Pick up the Amulet of Yendor."),
    ALL_BAGS_BOUGHT("ALL_BAGS_BOUGHT", "All bags bought", "All bags bought", "Buy the Seed Pouch, Scroll Holder, and Wand Holster."),
    DEATH_FROM_FIRE("DEATH_FROM_FIRE", "Death from fire", "Death from fire", "Die from fire damage."),
    DEATH_FROM_POISON("DEATH_FROM_POISON", "Death from poison", "Death from poison", "Die from poison damage."),
    DEATH_FROM_GAS("DEATH_FROM_GAS", "Death from toxic gas", "Death from toxic gas", "Die from toxic gas damage."),
    DEATH_FROM_HUNGER("DEATH_FROM_HUNGER", "Death from hunger", "Death from hunger", "Die from hunger."),
    NO_MONSTERS_SLAIN("NO_MONSTERS_SLAIN", "Level completed without killing any monsters", "Level completed without killing any monsters", "Complete a dungeon level without killing any monsters."),
    GRIM_WEAPON("GRIM_WEAPON", "Monster killed by a Grim weapon", "Monster killed by a Grim weapon", "Kill any monster with a Grim weapon effect."),
    PIRANHAS("PIRANHAS", "6 piranhas killed", "6 piranhas killed", "Kill 6 piranhas."),
    SUPPORTER("SUPPORTER", "Thanks for your support!", "Thanks for your support!", "Apply supporter ownership/unlocks through donation, billing, or store entitlements."),
    BOSS_SLAIN_1_ALL_CLASSES("BOSS_SLAIN_1_ALL_CLASSES", "1st boss slain by Warrior, Mage, Rogue & Huntress", "1st boss slain by Warrior, Mage, Rogue & Huntress", "Defeat the 1st boss once with each base class: Warrior, Mage, Rogue, and Huntress."),
    BOSS_SLAIN_3_ALL_SUBCLASSES("BOSS_SLAIN_3_ALL_SUBCLASSES", "3rd boss slain by Gladiator, Berserker, Warlock, Battlemage, Freerunner, Assassin, Sniper & Warden", "3rd boss slain by Gladiator, Berserker, Warlock, Battlemage, Freerunner, Assassin, Sniper & Warden", "Defeat the 3rd boss once with each subclass: Gladiator, Berserker, Warlock, Battlemage, Freerunner, Assassin, Sniper, and Warden."),
    YASD("YASD", "Death from fire, poison, toxic gas & hunger", "Death from fire, poison, toxic gas & hunger", "Unlock the fire, poison, toxic gas, and hunger death badges across runs."),
    ALL_ITEMS_IDENTIFIED("ALL_ITEMS_IDENTIFIED", "All potions, scrolls, rings & wands identified", "All potions, scrolls, rings & wands identified", "Unlock all four identification badges: potions, scrolls, rings, and wands."),
    VICTORY_ALL_CLASSES("VICTORY_ALL_CLASSES", "Amulet of Yendor obtained by Warrior, Mage, Rogue & Huntress", "Amulet of Yendor obtained by Warrior, Mage, Rogue & Huntress", "Pick up the Amulet of Yendor once with each base class: Warrior, Mage, Rogue, and Huntress."),
    RARE("RARE", "All rare monsters slain", "All rare monsters slain", "Kill each rare monster variant at least once: Albino, Bandit, Shielded, Senior, and Acidic."),
    HAPPY_END("HAPPY_END", "Happy end", "Happy end", "Reach the surface ending scene after a winning run."),
    CHAMPION("CHAMPION", "Challenge won", "Challenge won", "Win a run while at least one challenge is enabled."),
    STRENGTH_ATTAINED_1("STRENGTH_ATTAINED_1", "13 points of Strength attained", "13 points of Strength attained", "Reach 13 Strength in a run."),
    STRENGTH_ATTAINED_2("STRENGTH_ATTAINED_2", "15 points of Strength attained", "15 points of Strength attained", "Reach 15 Strength in a run."),
    STRENGTH_ATTAINED_3("STRENGTH_ATTAINED_3", "17 points of Strength attained", "17 points of Strength attained", "Reach 17 Strength in a run."),
    STRENGTH_ATTAINED_4("STRENGTH_ATTAINED_4", "19 points of Strength attained", "19 points of Strength attained", "Reach 19 Strength in a run."),
    FOOD_EATEN_1("FOOD_EATEN_1", "10 pieces of food eaten", "10 pieces of food eaten", "Eat at least 10 pieces of food in a run."),
    FOOD_EATEN_2("FOOD_EATEN_2", "20 pieces of food eaten", "20 pieces of food eaten", "Eat at least 20 pieces of food in a run."),
    FOOD_EATEN_3("FOOD_EATEN_3", "30 pieces of food eaten", "30 pieces of food eaten", "Eat at least 30 pieces of food in a run."),
    FOOD_EATEN_4("FOOD_EATEN_4", "40 pieces of food eaten", "40 pieces of food eaten", "Eat at least 40 pieces of food in a run."),
    ITEM_LEVEL_1("ITEM_LEVEL_1", "Item of level 3 acquired", "Item of level 3 acquired", "Obtain or upgrade any item to at least level 3."),
    ITEM_LEVEL_2("ITEM_LEVEL_2", "Item of level 6 acquired", "Item of level 6 acquired", "Obtain or upgrade any item to at least level 6."),
    ITEM_LEVEL_3("ITEM_LEVEL_3", "Item of level 9 acquired", "Item of level 9 acquired", "Obtain or upgrade any item to at least level 9."),
    ITEM_LEVEL_4("ITEM_LEVEL_4", "Item of level 12 acquired", "Item of level 12 acquired", "Obtain or upgrade any item to at least level 12."),
    POTIONS_COOKED_1("POTIONS_COOKED_1", "3 potions cooked", "3 potions cooked", "Cook at least 3 potions in a run."),
    POTIONS_COOKED_2("POTIONS_COOKED_2", "6 potions cooked", "6 potions cooked", "Cook at least 6 potions in a run."),
    POTIONS_COOKED_3("POTIONS_COOKED_3", "9 potions cooked", "9 potions cooked", "Cook at least 9 potions in a run."),
    POTIONS_COOKED_4("POTIONS_COOKED_4", "12 potions cooked", "12 potions cooked", "Cook at least 12 potions in a run."),
    MASTERY_COMBO("MASTERY_COMBO", "7-hit combo", "7-hit combo", "Reach a 7-hit combo."),
    DEATH_FROM_GLYPH("DEATH_FROM_GLYPH", "Death from an enchantment", "Death from an enchantment", "Die from an enchantment or glyph effect."),
    NIGHT_HUNTER("NIGHT_HUNTER", "15 monsters killed at nighttime", "15 monsters killed at nighttime", "Kill 15 monsters at nighttime."),
    DEATH_FROM_FALLING("DEATH_FROM_FALLING", "Death from falling down", "Death from falling down", "Die from falling."),
    GAMES_PLAYED_1("GAMES_PLAYED_1", "10 games played", "10 games played", "Record at least 10 completed runs in Rankings."),
    GAMES_PLAYED_2("GAMES_PLAYED_2", "100 games played", "100 games played", "Record at least 100 completed runs in Rankings."),
    GAMES_PLAYED_3("GAMES_PLAYED_3", "500 games played", "500 games played", "Record at least 500 completed runs in Rankings."),
    GAMES_PLAYED_4("GAMES_PLAYED_4", "2000 games played", "2000 games played", "Record at least 2000 completed runs in Rankings.");

    private static final String ICON_BASE_PATH = "images/achievements/";

    private final String id;
    private final String name;
    private final String description;
    private final String unlockCondition;

    Achievement(String id, String name, String description, String unlockCondition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlockCondition = unlockCondition;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return localizedBadgeText(".title", name);
    }

    public String getSourceName() {
        return name;
    }

    public String getDescription() {
        return localizedBadgeText(".title", description);
    }

    public String getUnlockCondition() {
        return localizedBadgeText(".desc", unlockCondition);
    }

    public String getSourceUnlockCondition() {
        return unlockCondition;
    }

    private String localizedBadgeText(String suffix, String fallback) {
        if (Messages.lang() == Languages.ENGLISH) {
            return fallback;
        }

        String localized = Messages.getOrNull(badgeMessageKey(suffix));
        return localized != null ? localized : fallback;
    }

    private String badgeMessageKey(String suffix) {
        return "badges$badge." + id.toLowerCase(Locale.ROOT) + suffix;
    }

    public String getAchievedIconPath() {
        return ICON_BASE_PATH + id + ".png";
    }

    public String getUnachievedIconPath() {
        return ICON_BASE_PATH + id + "_unachieved.png";
    }
}
