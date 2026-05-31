package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.LeatherArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.MailArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.PlateArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.ScaleArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Axe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Glaive;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Hammer;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Mace;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.ShortSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Spear;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Sword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBoltWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.MercenaryBow;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.MercenaryShuriken;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;

import java.util.ArrayList;

public class MercenaryHelper {
    private static final float MERCENARY_ATTACK_SPEED_MULTIPLIER = 0.75f;
    private static final float MERCENARY_DEFENSE_SKILL_MULTIPLIER = 0.5f;

    public enum MercenaryType {
        BRUTE("Warrior"),
        ROGUE("Rogue"),
        WIZARD("Wizard"),
        HUNTRESS("Huntress");

        private final String displayName;

        MercenaryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static MercenaryType fromName(String name) {
            if (name == null || name.isEmpty()) {
                return null;
            }

            for (MercenaryType type : values()) {
                if (type.name().equalsIgnoreCase(name) || type.displayName.equalsIgnoreCase(name)) {
                    return type;
                }
            }

            if ("archer".equalsIgnoreCase(name)) {
                return HUNTRESS;
            }

            if ("thief".equalsIgnoreCase(name)) {
                return ROGUE;
            }

            if ("brute".equalsIgnoreCase(name)) {
                return BRUTE;
            }

            return null;
        }
    }

    private static final Class<? extends MeleeWeapon>[] RANDOM_MELEE_WEAPON_CLASSES = new Class[]{
            Knuckles.class,
            Dagger.class,
            ShortSword.class,
            Mace.class,
            Sword.class,
            Spear.class,
            LongSword.class,
            Axe.class,
            Hammer.class,
            Glaive.class
    };

    private static final Class<? extends Armor>[] ARMOR_CLASSES = new Class[]{
            Cloth.class,
            LeatherArmor.class,
            MailArmor.class,
            ScaleArmor.class,
            PlateArmor.class
    };

    public static MercenaryType rollType(Hero hero) {
        ArrayList<MercenaryType> candidates = getAvailableTypes(hero);
        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
    }

    public static ArrayList<MercenaryType> getAvailableTypes(Hero hero) {
        ArrayList<MercenaryType> candidates = new ArrayList<MercenaryType>();
        ArrayList<MercenaryType> blockedTypes = new ArrayList<MercenaryType>();
        MercenaryType heroType = hero == null ? null : fromHeroClass(hero.getHeroClass());
        if (heroType != null) {
            blockedTypes.add(heroType);
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof MercenaryAlly) || unit.isDead()) {
                continue;
            }

            MercenaryType mercenaryType = ((MercenaryAlly) unit).getMercenaryType();
            if (mercenaryType != null && !blockedTypes.contains(mercenaryType)) {
                blockedTypes.add(mercenaryType);
            }
        }

        for (MercenaryType candidate : MercenaryType.values()) {
            if (!blockedTypes.contains(candidate)) {
                candidates.add(candidate);
            }
        }

        return candidates;
    }

    public static MercenaryType fromHeroClass(HeroClass heroClass) {
        if (heroClass == null) {
            return null;
        }

        switch (heroClass) {
            case WARRIOR:
                return MercenaryType.BRUTE;
            case ROGUE:
                return MercenaryType.ROGUE;
            case WIZARD:
                return MercenaryType.WIZARD;
            case ARCHER:
                return MercenaryType.HUNTRESS;
            default:
                return null;
        }
    }

    public static HeroClass getHeroClass(MercenaryType type) {
        if (type == null) {
            return HeroClass.WARRIOR;
        }

        switch (type) {
            case BRUTE:
                return HeroClass.WARRIOR;
            case ROGUE:
                return HeroClass.ROGUE;
            case WIZARD:
                return HeroClass.WIZARD;
            case HUNTRESS:
                return HeroClass.ARCHER;
            default:
                return HeroClass.WARRIOR;
        }
    }

    public static int rollLevel(int depth, Hero hero) {
        int maxLevel = Math.max(1, depth);
        int minLevel = Math.max(1, (int) Math.floor(depth * 0.75f));
        if (minLevel > maxLevel) {
            minLevel = maxLevel;
        }

        return minLevel + RandomHelper.getInstance().randomInt(Math.max(1, maxLevel - minLevel + 1));
    }

    public static String getName(MercenaryType type) {
        return type == null ? MercenaryType.BRUTE.getDisplayName() : type.getDisplayName();
    }

    public static String getDisplayName(MercenaryType type) {
        return getDisplayName(type, getName(type));
    }

    public static String getDisplayName(MercenaryType type, String name) {
        MercenaryType resolvedType = type;
        MercenaryType parsedType = MercenaryType.fromName(name);
        if (parsedType != null) {
            resolvedType = parsedType;
        }

        if (resolvedType != null) {
            return getHeroClass(resolvedType).getName();
        }

        return Messages.capitalizeForDisplay(Messages.maybeTranslate(name == null ? "" : name));
    }

    public static String normalizeMercenaryName(MercenaryType type, String name) {
        MercenaryType resolvedType = type == null ? MercenaryType.BRUTE : type;
        if (name == null || name.isEmpty()) {
            return getName(resolvedType);
        }

        MercenaryType parsedType = MercenaryType.fromName(name);
        return parsedType == resolvedType ? getName(resolvedType) : name;
    }

    public static int getStrength(MercenaryType type, int level) {
        int normalizedLevel = Math.max(1, level);
        if (type == null) {
            return 13;
        }

        switch (type) {
            case BRUTE:
                return 13 + normalizedLevel / 3;
            case ROGUE:
                return 13 + normalizedLevel / 4;
            case WIZARD:
                return 10 + normalizedLevel / 5;
            case HUNTRESS:
                return 11 + normalizedLevel / 4;
            default:
                return 10;
        }
    }

    public static int getHealth(MercenaryType type, int level) {
        int normalizedLevel = Math.max(1, level);
        if (type == null) {
            return 23;
        }

        switch (type) {
            case BRUTE:
                return 20 + normalizedLevel * 3;
            case ROGUE:
                return 15 + normalizedLevel * 2;
            case WIZARD:
                return 10 + normalizedLevel;
            case HUNTRESS:
                return 15 + normalizedLevel * 2;
            default:
                return 20;
        }
    }

    public static float getMoveSpeed(MercenaryType type) {
        float base = getHeroClass(type).getMoveSpeed();
        if (type == null) {
            return base;
        }

        switch (type) {
            case BRUTE:
                return base * 0.92f;
            case ROGUE:
                return base * 1.02f;
            case WIZARD:
                return base;
            case HUNTRESS:
                return base * 1.02f;
            default:
                return base;
        }
    }

    public static float getAttackSpeed(MercenaryType type) {
        float base = getHeroClass(type).getAttackSpeed();
        if (type != null) {
            switch (type) {
                case BRUTE:
                    base *= 0.95f;
                    break;
                case ROGUE:
                    base *= 1.08f;
                    break;
                case WIZARD:
                    break;
                case HUNTRESS:
                    base *= 1.04f;
                    break;
                default:
                    break;
            }
        }

        return base * MERCENARY_ATTACK_SPEED_MULTIPLIER;
    }

    public static int getAttackSkill(MercenaryType type, int level) {
        return getCombatSkill(type, level);
    }

    public static int getDefenseSkill(MercenaryType type, int level) {
        return Math.max(0, Math.round(getCombatSkill(type, level) * MERCENARY_DEFENSE_SKILL_MULTIPLIER));
    }

    public static int getPrice(MercenaryType type, int level) {
        int normalizedLevel = Math.max(1, level);
        if (type == null) {
            return 125;
        }

        switch (type) {
            case BRUTE:
                return 100 + normalizedLevel * 25;
            case ROGUE:
                return 75 + normalizedLevel * 15;
            case WIZARD:
                return 80 + normalizedLevel * 20;
            case HUNTRESS:
                return 90 + normalizedLevel * 20;
            default:
                return 100 + normalizedLevel * 20;
        }
    }

    public static String getDescription(MercenaryType type) {
        if (type == null) {
            return "A dependable sword for hire.";
        }

        switch (type) {
            case BRUTE:
                return "Brutes are strong mercenaries who rely on physical fitness.";
            case ROGUE:
                return "Thieves rely on stealth, poison, and sudden bursts of violence in combat.";
            case WIZARD:
                return "Wizards choose the path of magic. They are physically weak, so they rely on ranged spells.";
            case HUNTRESS:
                return "Huntresses strike from a distance and prefer to keep enemies at arm's reach.";
            default:
                return "A dependable sword for hire.";
        }
    }

    public static String getSpecialAction(MercenaryType type) {
        if (type == null) {
            return "Follows the hero.";
        }

        switch (type) {
            case BRUTE:
                return "Frontline bruiser who favors a mace and leather armor.";
            case ROGUE:
                return "Throws endless shurikens between melee exchanges.";
            case WIZARD:
                return "Launches firebolts and occasionally slows enemies.";
            case HUNTRESS:
                return "Keeps range and fires an endless stream of arrows.";
            default:
                return "Follows the hero.";
        }
    }

    public static MeleeWeapon createPrimaryWeapon(MercenaryType type, int level) {
        int strength = getStrength(type, level);
        MeleeWeapon weapon;
        switch (type == null ? MercenaryType.BRUTE : type) {
            case WIZARD:
                weapon = new FireBoltWand();
                break;
            case HUNTRESS:
            case ROGUE:
                weapon = createWeightedMeleeWeapon(strength);
                break;
            case BRUTE:
            default:
                weapon = createWeightedMeleeWeapon(strength);
                break;
        }

        if (weapon != null) {
            weapon.identify();
        }

        return weapon;
    }

    public static RangedWeapon createRangedWeapon(MercenaryType type) {
        RangedWeapon rangedWeapon = null;
        if (type == MercenaryType.ROGUE) {
            rangedWeapon = new MercenaryShuriken();
        }
        else if (type == MercenaryType.HUNTRESS) {
            rangedWeapon = new MercenaryBow();
        }

        if (rangedWeapon != null) {
            rangedWeapon.identify();
        }

        return rangedWeapon;
    }

    public static Armor createArmor(MercenaryType type, int level) {
        int strength = getStrength(type, level);
        Armor armor;
        switch (type == null ? MercenaryType.BRUTE : type) {
            case WIZARD:
                armor = new Cloth();
                break;
            case BRUTE:
            case ROGUE:
            case HUNTRESS:
            default:
                armor = createBestArmor(strength);
                break;
        }

        if (armor != null) {
            armor.identify();
        }
        return armor;
    }

    public static MeleeWeapon sanitizePrimaryWeapon(MercenaryType type, int level, MeleeWeapon weapon) {
        if (weapon == null) {
            return null;
        }

        weapon.identify();
        return weapon;
    }

    public static Armor sanitizeArmor(MercenaryType type, int level, Armor armor) {
        if (armor == null) {
            return null;
        }

        armor.identify();
        return armor;
    }

    public static RangedWeapon sanitizeRangedWeapon(MercenaryType type, RangedWeapon rangedWeapon) {
        MercenaryType resolvedType = type == null ? MercenaryType.BRUTE : type;

        if (resolvedType == MercenaryType.ROGUE) {
            if (rangedWeapon instanceof MercenaryShuriken) {
                rangedWeapon.identify();
                return rangedWeapon;
            }
            return createRangedWeapon(resolvedType);
        }

        if (resolvedType == MercenaryType.HUNTRESS) {
            if (isBowWeapon(rangedWeapon)) {
                rangedWeapon.identify();
                return rangedWeapon;
            }
            return createRangedWeapon(resolvedType);
        }

        return null;
    }

    public static boolean isBowWeapon(RangedWeapon rangedWeapon) {
        return rangedWeapon instanceof Bow;
    }

    public static String getLoadoutSummary(MeleeWeapon weapon, Armor armor, RangedWeapon rangedWeapon) {
        ArrayList<String> entries = new ArrayList<String>();
        if (weapon != null) {
            entries.add(weapon.getName());
        }
        if (rangedWeapon != null) {
            entries.add(rangedWeapon.getName());
        }
        if (armor != null) {
            entries.add(armor.getName());
        }

        if (entries.isEmpty()) {
            return "Travel clothes";
        }

        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                summary.append(", ");
            }
            summary.append(entries.get(i));
        }
        return summary.toString();
    }

    private static MeleeWeapon createWeightedMeleeWeapon(int strength) {
        ArrayList<MeleeWeapon> candidates = new ArrayList<MeleeWeapon>();
        int highestTier = 1;

        for (Class<? extends MeleeWeapon> weaponClass : RANDOM_MELEE_WEAPON_CLASSES) {
            MeleeWeapon weapon = instantiate(weaponClass);
            if (weapon == null || weapon.getRequiredStrength() > strength) {
                continue;
            }

            highestTier = Math.max(highestTier, weapon.getTier());
            candidates.add(weapon);
        }

        if (candidates.isEmpty()) {
            return new Knuckles();
        }

        ArrayList<MeleeWeapon> weighted = new ArrayList<MeleeWeapon>();
        for (MeleeWeapon weapon : candidates) {
            if (weapon.getTier() >= highestTier - 1) {
                weighted.add(weapon);
            }
        }

        if (weighted.isEmpty()) {
            weighted = candidates;
        }

        return weighted.get(RandomHelper.getInstance().randomInt(weighted.size()));
    }

    private static Armor createBestArmor(int strength) {
        Armor bestArmor = null;
        for (Class<? extends Armor> armorClass : ARMOR_CLASSES) {
            Armor armor = instantiate(armorClass);
            if (armor == null || armor.getRequiredStrength() > strength) {
                continue;
            }

            if (bestArmor == null || armor.getTier() > bestArmor.getTier()) {
                bestArmor = armor;
            }
        }

        return bestArmor == null ? new Cloth() : bestArmor;
    }

    private static <T extends Item> T instantiate(Class<T> itemClass) {
        try {
            return itemClass.newInstance();
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static int getCombatSkill(MercenaryType type, int level) {
        int normalizedLevel = Math.max(1, level);
        if (type == null) {
            return normalizedLevel;
        }

        switch (type) {
            case BRUTE:
                return normalizedLevel * 2;
            case WIZARD:
                return normalizedLevel;
            case ROGUE:
            case HUNTRESS:
                return normalizedLevel * 3;
            default:
                return normalizedLevel;
        }
    }

    private static boolean matchesExpectedPrimaryWeapon(MercenaryType type, MeleeWeapon weapon) {
        switch (type == null ? MercenaryType.BRUTE : type) {
            case WIZARD:
                return weapon instanceof FireBoltWand;
            case ROGUE:
                return weapon instanceof Dagger;
            case HUNTRESS:
                return weapon == null;
            case BRUTE:
            default:
                return weapon instanceof Mace;
        }
    }

    private static boolean matchesExpectedArmor(MercenaryType type, Armor armor) {
        switch (type == null ? MercenaryType.BRUTE : type) {
            case BRUTE:
                return armor instanceof LeatherArmor;
            case ROGUE:
            case WIZARD:
            case HUNTRESS:
            default:
                return armor instanceof Cloth;
        }
    }
}