package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.math.RandomXS128;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class ItemIdentityHelper {

    public enum Family {
        NONE("", "", null, null),
        POTION("Potion", "An unidentified potion. Its contents are unknown until it is used.",
                "custom.identity.display.potion", "custom.identity.unknown.potion"),
        SCROLL("Scroll", "An unidentified scroll. Its magic is unknown until it is read.",
                "custom.identity.display.scroll", "custom.identity.unknown.scroll"),
        WAND("Wand", "An unidentified wand. Its power is unknown until it is used.",
                "custom.identity.display.wand", "custom.identity.unknown.wand"),
        RING("Ring", "An unidentified ring. Its magic is unknown until it is worn.",
                "custom.identity.display.ring", "custom.identity.unknown.ring");

        private final String displaySuffix;
        private final String legacyUnknownDescription;
        private final String displayTemplateKey;
        private final String defaultUnknownDescriptionKey;

        Family(String displaySuffix,
               String legacyUnknownDescription,
               String displayTemplateKey,
               String defaultUnknownDescriptionKey) {
            this.displaySuffix = displaySuffix;
            this.legacyUnknownDescription = legacyUnknownDescription;
            this.displayTemplateKey = displayTemplateKey;
            this.defaultUnknownDescriptionKey = defaultUnknownDescriptionKey;
        }

        public String getDisplaySuffix() {
            return Messages.maybeTranslate(displaySuffix);
        }

        public boolean usesDefaultUnknownDescription(String value) {
            return value == null || value.isEmpty() || legacyUnknownDescription.equals(value);
        }

        public String getDefaultUnknownDescription() {
            return defaultUnknownDescriptionKey == null ? "" : Messages.get(defaultUnknownDescriptionKey);
        }

        public String formatAppearanceName(String appearanceLabel) {
            if (displayTemplateKey == null) {
                return appearanceLabel == null ? "" : appearanceLabel;
            }

            return Messages.get(displayTemplateKey, new Object[]{appearanceLabel == null ? "" : appearanceLabel});
        }
    }

    private static final long POTION_SALT = 0x51C3A2D7L;
    private static final long SCROLL_SALT = 0x2A10FE31L;
    private static final long WAND_SALT = 0x7B118D42L;
    private static final long RING_SALT = 0x34E7AC91L;

    private static final ArrayList<AppearanceOption> POTION_APPEARANCES = new ArrayList<AppearanceOption>(Arrays.asList(
            new AppearanceOption("Amber", "images/misc/extracted items/POTION_AMBER.png"),
            new AppearanceOption("Azure", "images/misc/extracted items/POTION_AZURE.png"),
            new AppearanceOption("Bistre", "images/misc/extracted items/POTION_BISTRE.png"),
            new AppearanceOption("Charcoal", "images/misc/extracted items/POTION_CHARCOAL.png"),
            new AppearanceOption("Crimson", "images/misc/extracted items/POTION_CRIMSON.png"),
            new AppearanceOption("Golden", "images/misc/extracted items/POTION_GOLDEN.png"),
            new AppearanceOption("Indigo", "images/misc/extracted items/POTION_INDIGO.png"),
            new AppearanceOption("Ivory", "images/misc/extracted items/POTION_IVORY.png"),
            new AppearanceOption("Jade", "images/misc/extracted items/POTION_JADE.png"),
            new AppearanceOption("Magenta", "images/misc/extracted items/POTION_MAGENTA.png"),
            new AppearanceOption("Silver", "images/misc/extracted items/POTION_SILVER.png"),
            new AppearanceOption("Turquoise", "images/misc/extracted items/POTION_TURQUOISE.png")
    ));

    private static final ArrayList<AppearanceOption> SCROLL_APPEARANCES = new ArrayList<AppearanceOption>(Arrays.asList(
            new AppearanceOption("Algiz", "images/misc/extracted items/SCROLL_BLOODY.png"),
            new AppearanceOption("Ansuz", "images/misc/extracted items/SCROLL_FROST.png"),
            new AppearanceOption("Berkanan", "images/misc/extracted items/SCROLL_BERKANAN.png"),
            new AppearanceOption("Ehwaz", "images/misc/extracted items/SCROLL_GOHOME.png"),
            new AppearanceOption("Fehu", "images/misc/extracted items/SCROLL_SACRIFICE.png"),
            new AppearanceOption("Gyfu", "images/misc/extracted items/SCROLL_GYFU.png"),
            new AppearanceOption("Hagalaz", "images/misc/extracted items/SCROLL_SKILLPOINT.png"),
            new AppearanceOption("Isaz", "images/misc/extracted items/SCROLL_ISAZ.png"),
            new AppearanceOption("Jera", "images/misc/extracted items/SCROLL_SKILLRESETACTIVE.png"),
            new AppearanceOption("Kaunan", "images/misc/extracted items/SCROLL_KAUNAN.png"),
            new AppearanceOption("Laguz", "images/misc/extracted items/SCROLL_LAGUZ.png"),
            new AppearanceOption("Mannaz", "images/misc/extracted items/SCROLL_MANNAZ.png"),
            new AppearanceOption("Naudiz", "images/misc/extracted items/SCROLL_NAUDIZ.png"),
            new AppearanceOption("Odal", "images/misc/extracted items/SCROLL_ODAL.png"),
            new AppearanceOption("Perthro", "images/misc/extracted items/SCROLL_WIPE_OUT.png"),
            new AppearanceOption("Raido", "images/misc/extracted items/SCROLL_RAIDO.png"),
            new AppearanceOption("Sowilo", "images/misc/extracted items/SCROLL_SOWILO.png"),
            new AppearanceOption("Tiwaz", "images/misc/extracted items/SCROLL_TIWAZ.png"),
            new AppearanceOption("Yngvi", "images/misc/extracted items/SCROLL_YNGVI.png")
    ));

    private static final ArrayList<AppearanceOption> WAND_APPEARANCES = new ArrayList<AppearanceOption>(Arrays.asList(
            new AppearanceOption("Bamboo", "images/wands/WAND_BAMBOO.png"),
            new AppearanceOption("Birch", "images/wands/WAND_BIRCH.png"),
            new AppearanceOption("Cherry", "images/wands/WAND_CHERRY.png"),
            new AppearanceOption("Ebony", "images/wands/WAND_EBONY.png"),
            new AppearanceOption("Holly", "images/wands/WAND_HOLLY.png"),
            new AppearanceOption("Mahogany", "images/wands/WAND_MAHOGANY.png"),
            new AppearanceOption("Oak", "images/wands/WAND_OAK.png"),
            new AppearanceOption("Rowan", "images/wands/WAND_ROWAN.png"),
            new AppearanceOption("Teak", "images/wands/WAND_TEAK.png"),
            new AppearanceOption("Willow", "images/wands/WAND_WILLOW.png"),
            new AppearanceOption("Yew", "images/wands/WAND_YEW.png")
    ));

    private static final ArrayList<AppearanceOption> RING_APPEARANCES = new ArrayList<AppearanceOption>(Arrays.asList(
            new AppearanceOption("Agate", "images/misc/extracted items/RING_AGATE.png"),
            new AppearanceOption("Amethyst", "images/misc/extracted items/RING_AMETHYST.png"),
            new AppearanceOption("Diamond", "images/misc/extracted items/RING_DIAMOND.png"),
            new AppearanceOption("Emerald", "images/misc/extracted items/RING_EMERALD.png"),
            new AppearanceOption("Garnet", "images/misc/extracted items/RING_GARNET.png"),
            new AppearanceOption("Onyx", "images/misc/extracted items/RING_ONYX.png"),
            new AppearanceOption("Opal", "images/misc/extracted items/RING_OPAL.png"),
            new AppearanceOption("Quartz", "images/misc/extracted items/RING_QUARTZ.png"),
            new AppearanceOption("Ruby", "images/misc/extracted items/RING_RUBY.png"),
            new AppearanceOption("Sapphire", "images/misc/extracted items/RING_SAPPHIRE.png"),
            new AppearanceOption("Topaz", "images/misc/extracted items/RING_TOPAZ.png"),
            new AppearanceOption("Tourmaline", "images/misc/extracted items/RING_TOURMALINE.png")
    ));

    private static final ItemIdentityHelper ourInstance = new ItemIdentityHelper();

    private final HashMap<Family, ArrayList<AppearanceOption>> shuffledPools = new HashMap<Family, ArrayList<AppearanceOption>>();
    private ItemIdentitySaveData currentRunData = new ItemIdentitySaveData();

    public static ItemIdentityHelper getInstance() {
        return ourInstance;
    }

    private ItemIdentityHelper() {
        restore(null, RandomHelper.getInstance().getRunSeed());
    }

    public void restore(ItemIdentitySaveData saveData, long runSeed) {
        currentRunData = saveData != null ? saveData.copy() : new ItemIdentitySaveData();
        rebuildPools(runSeed);
        normalizeStoredAppearances();
    }

    public ItemIdentitySaveData snapshot() {
        return currentRunData.copy();
    }

    public boolean isIdentified(Item item) {
        if (item == null || item.isAlwaysIdentified()) {
            return true;
        }

        return currentRunData.identifiedClassNames.contains(getItemKey(item));
    }

    public boolean isIdentified(String itemClassName) {
        if (itemClassName == null || itemClassName.isEmpty()) {
            return false;
        }

        return currentRunData.identifiedClassNames.contains(itemClassName);
    }

    public void identify(Item item) {
        if (item == null || item.isAlwaysIdentified()) {
            return;
        }

        String itemKey = getItemKey(item);
        boolean newlyIdentified = currentRunData.identifiedClassNames.add(itemKey);
        currentRunData.identificationProgressByClassName.remove(itemKey);
        if (newlyIdentified) {
            AchievementManager.getInstance().onItemIdentified(item);
        }
    }

    public void identify(String itemClassName) {
        if (itemClassName == null || itemClassName.isEmpty()) {
            return;
        }

        currentRunData.identifiedClassNames.add(itemClassName);
        currentRunData.identificationProgressByClassName.remove(itemClassName);
    }

    public float getIdentificationProgress(Item item) {
        if (item == null || item.isAlwaysIdentified() || isIdentified(item)) {
            return 0f;
        }

        Float progress = currentRunData.identificationProgressByClassName.get(getItemKey(item));
        return progress != null ? Math.max(0f, progress) : 0f;
    }

    public boolean addIdentificationProgress(Item item, float amount, float requiredAmount) {
        if (item == null || item.isAlwaysIdentified()) {
            return true;
        }

        if (isIdentified(item)) {
            return true;
        }

        if (requiredAmount <= 0f) {
            identify(item);
            return true;
        }

        String className = getItemKey(item);
        float progress = getIdentificationProgress(item) + Math.max(0f, amount);
        if (progress >= requiredAmount) {
            identify(item);
            return true;
        }

        currentRunData.identificationProgressByClassName.put(className, progress);
        return false;
    }

    public String getDisplayName(Item item) {
        if (item == null) {
            return "";
        }

        if (isIdentified(item)) {
            return Messages.capitalizeForDisplay(Messages.maybeTranslate(item.getTrueName()));
        }

        AppearanceOption appearance = ensureAppearance(item);
        if (appearance == null || item.getIdentityFamily() == Family.NONE) {
            return Messages.capitalizeForDisplay(Messages.maybeTranslate(item.getTrueName()));
        }

        return Messages.capitalizeForDisplay(
                item.getIdentityFamily().formatAppearanceName(getLocalizedAppearanceLabel(appearance.label)));
    }

    public String getDisplayDescription(Item item) {
        if (item == null || isIdentified(item)) {
            return item != null ? Messages.maybeTranslate(item.getTrueDescription()) : "";
        }

        if (item.getUnknownDescription() != null
                && !item.getUnknownDescription().isEmpty()
                && !item.getIdentityFamily().usesDefaultUnknownDescription(item.getUnknownDescription())) {
            return Messages.maybeTranslate(item.getUnknownDescription());
        }

        return item.getIdentityFamily().getDefaultUnknownDescription();
    }

    private String getLocalizedAppearanceLabel(String label) {
        if (label == null || label.isEmpty()) {
            return "";
        }

        String key = "custom.identity.appearance." + label.toLowerCase(Locale.ROOT);
        String localized = Messages.getOrNull(key);
        return localized != null && !localized.isEmpty() ? localized : Messages.maybeTranslate(label);
    }

    public String getDisplaySpritePath(Item item) {
        if (item == null) {
            return item != null ? item.getTrueSpritePath() : null;
        }

        if (item.isAlwaysIdentified() || item.getIdentityFamily() == Family.NONE) {
            return item.getTrueSpritePath();
        }

        AppearanceOption appearance = ensureAppearance(item);
        return appearance != null ? appearance.spritePath : item.getTrueSpritePath();
    }

    private AppearanceOption ensureAppearance(Item item) {
        if (item == null || item.getIdentityFamily() == Family.NONE || item.isAlwaysIdentified()) {
            return null;
        }

        String className = getItemKey(item);
        String storedLabel = currentRunData.appearanceLabelByClassName.get(className);
        String storedPath = currentRunData.appearancePathByClassName.get(className);
        if (storedLabel != null && storedPath != null) {
            return new AppearanceOption(storedLabel, storedPath);
        }

        ArrayList<AppearanceOption> pool = shuffledPools.get(item.getIdentityFamily());
        if (pool == null || pool.isEmpty()) {
            return null;
        }

        HashSet<String> usedPaths = new HashSet<String>();
        for (Map.Entry<String, String> entry : currentRunData.familyByClassName.entrySet()) {
            if (!item.getIdentityFamily().name().equals(entry.getValue())) {
                continue;
            }

            String usedPath = currentRunData.appearancePathByClassName.get(entry.getKey());
            if (usedPath != null) {
                usedPaths.add(usedPath);
            }
        }

        AppearanceOption selected = null;
        for (AppearanceOption candidate : pool) {
            if (!usedPaths.contains(candidate.spritePath)) {
                selected = candidate;
                break;
            }
        }

        if (selected == null) {
            selected = pool.get(Math.abs(className.hashCode()) % pool.size());
        }

        currentRunData.familyByClassName.put(className, item.getIdentityFamily().name());
        currentRunData.appearanceLabelByClassName.put(className, selected.label);
        currentRunData.appearancePathByClassName.put(className, selected.spritePath);
        return selected;
    }

    private String getItemKey(Item item) {
        String itemId = SaveRegistry.getItemId(item);
        return itemId != null ? itemId : item.getTrueName() + "|" + item.getTrueSpritePath();
    }

    private void rebuildPools(long runSeed) {
        shuffledPools.clear();
        shuffledPools.put(Family.POTION, shuffle(POTION_APPEARANCES, runSeed ^ POTION_SALT));
        shuffledPools.put(Family.SCROLL, shuffle(SCROLL_APPEARANCES, runSeed ^ SCROLL_SALT));
        shuffledPools.put(Family.WAND, shuffle(WAND_APPEARANCES, runSeed ^ WAND_SALT));
        shuffledPools.put(Family.RING, shuffle(RING_APPEARANCES, runSeed ^ RING_SALT));
    }

    private void normalizeStoredAppearances() {
        for (Family family : Family.values()) {
            if (family == Family.NONE) {
                continue;
            }

            normalizeStoredAppearances(family);
        }
    }

    private void normalizeStoredAppearances(Family family) {
        ArrayList<AppearanceOption> pool = shuffledPools.get(family);
        if (pool == null || pool.isEmpty()) {
            return;
        }

        ArrayList<String> identifiedClassNames = new ArrayList<String>();
        ArrayList<String> unidentifiedClassNames = new ArrayList<String>();

        for (Map.Entry<String, String> entry : currentRunData.familyByClassName.entrySet()) {
            if (!family.name().equals(entry.getValue())) {
                continue;
            }

            String className = entry.getKey();
            if (currentRunData.identifiedClassNames.contains(className)) {
                identifiedClassNames.add(className);
            }
            else {
                unidentifiedClassNames.add(className);
            }
        }

        Collections.sort(identifiedClassNames);
        Collections.sort(unidentifiedClassNames);

        HashSet<String> reservedPaths = new HashSet<String>();
        for (String className : identifiedClassNames) {
            normalizeStoredAppearance(className, pool, reservedPaths);
        }

        for (String className : unidentifiedClassNames) {
            normalizeStoredAppearance(className, pool, reservedPaths);
        }
    }

    private void normalizeStoredAppearance(String className,
                                           ArrayList<AppearanceOption> pool,
                                           HashSet<String> reservedPaths) {
        if (className == null || pool == null || pool.isEmpty()) {
            return;
        }

        String currentPath = currentRunData.appearancePathByClassName.get(className);
        AppearanceOption currentAppearance = findAppearanceByPath(pool, currentPath);
        if (currentAppearance != null && !reservedPaths.contains(currentAppearance.spritePath)) {
            currentRunData.appearanceLabelByClassName.put(className, currentAppearance.label);
            currentRunData.appearancePathByClassName.put(className, currentAppearance.spritePath);
            reservedPaths.add(currentAppearance.spritePath);
            return;
        }

        for (AppearanceOption candidate : pool) {
            if (reservedPaths.contains(candidate.spritePath)) {
                continue;
            }

            currentRunData.appearanceLabelByClassName.put(className, candidate.label);
            currentRunData.appearancePathByClassName.put(className, candidate.spritePath);
            reservedPaths.add(candidate.spritePath);
            return;
        }
    }

    private AppearanceOption findAppearanceByPath(ArrayList<AppearanceOption> pool, String spritePath) {
        if (pool == null || spritePath == null || spritePath.isEmpty()) {
            return null;
        }

        for (AppearanceOption option : pool) {
            if (spritePath.equals(option.spritePath)) {
                return option;
            }
        }

        return null;
    }

    private ArrayList<AppearanceOption> shuffle(ArrayList<AppearanceOption> source, long seed) {
        ArrayList<AppearanceOption> shuffled = new ArrayList<AppearanceOption>(source);
        RandomXS128 random = new RandomXS128(seed, seed ^ 0x9E3779B97F4A7C15L);
        Collections.shuffle(shuffled, random);
        return shuffled;
    }

    private static class AppearanceOption {
        private final String label;
        private final String spritePath;

        private AppearanceOption(String label, String spritePath) {
            this.label = label;
            this.spritePath = spritePath;
        }
    }

    public static class ItemIdentitySaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        public HashMap<String, String> familyByClassName = new HashMap<String, String>();
        public HashMap<String, String> appearanceLabelByClassName = new HashMap<String, String>();
        public HashMap<String, String> appearancePathByClassName = new HashMap<String, String>();
        public HashMap<String, Float> identificationProgressByClassName = new HashMap<String, Float>();
        public HashSet<String> identifiedClassNames = new HashSet<String>();

        public ItemIdentitySaveData copy() {
            ItemIdentitySaveData copy = new ItemIdentitySaveData();
            copy.familyByClassName.putAll(familyByClassName);
            copy.appearanceLabelByClassName.putAll(appearanceLabelByClassName);
            copy.appearancePathByClassName.putAll(appearancePathByClassName);
            copy.identificationProgressByClassName.putAll(identificationProgressByClassName);
            copy.identifiedClassNames.addAll(identifiedClassNames);
            return copy;
        }
    }
}