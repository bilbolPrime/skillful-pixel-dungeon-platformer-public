package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;

import java.util.HashMap;

public final class SaveRegistry {
    public static final String MERCHANT_ROOM_ITEM_FOR_PURCHASE_ID = "merchant-room/item-for-purchase";

    private static final HashMap<String, Class<? extends Item>> ITEM_CLASSES = new HashMap<String, Class<? extends Item>>();
    private static final HashMap<Class<? extends Item>, String> ITEM_IDS = new HashMap<Class<? extends Item>, String>();
    private static final HashMap<String, Class<? extends Prefix>> PREFIX_CLASSES = new HashMap<String, Class<? extends Prefix>>();
    private static final HashMap<Class<? extends Prefix>, String> PREFIX_IDS = new HashMap<Class<? extends Prefix>, String>();
    private static final HashMap<String, Class<? extends Unit>> UNIT_CLASSES = new HashMap<String, Class<? extends Unit>>();
    private static final HashMap<Class<? extends Unit>, String> UNIT_IDS = new HashMap<Class<? extends Unit>, String>();

    static {
        registerItems();
        registerPrefixes();
        registerUnits();
    }

    private SaveRegistry() {
    }

    public static String getItemId(Item item) {
        return item == null ? null : ITEM_IDS.get(item.getClass());
    }

    public static Item createItem(String itemId) {
        return createInstance(ITEM_CLASSES.get(itemId));
    }

    public static boolean matchesItemId(String itemId, Item item) {
        if (itemId == null || item == null) {
            return false;
        }

        return itemId.equals(getItemId(item));
    }

    public static String getPrefixId(Prefix prefix) {
        return prefix == null ? null : PREFIX_IDS.get(prefix.getClass());
    }

    public static Prefix createPrefix(String prefixId) {
        return createInstance(PREFIX_CLASSES.get(prefixId));
    }

    public static String getUnitId(Unit unit) {
        return unit == null ? null : UNIT_IDS.get(unit.getClass());
    }

    public static String getUnitId(Class<? extends Unit> unitClass) {
        return unitClass == null ? null : UNIT_IDS.get(unitClass);
    }

    public static Unit createUnit(String unitId) {
        if (isOwnedMinionId(unitId)) return null;
        return createInstance(UNIT_CLASSES.get(unitId));
    }

    public static boolean isOwnedMinionId(String unitId) {
        Class<? extends Unit> type = UNIT_CLASSES.get(unitId);
        return type != null && NecromancerMinion.class.isAssignableFrom(type);
    }

    public static NecromancerMinion createOwnedMinion(String unitId, Hero owner, int createdAtLevel) {
        if (!isOwnedMinionId(unitId)) return null;
        try {
            return (NecromancerMinion)UNIT_CLASSES.get(unitId).getDeclaredConstructor(Hero.class, int.class)
                    .newInstance(owner, createdAtLevel);
        } catch (Exception ignored) { return null; }
    }

    public static boolean matchesUnitId(String unitId, Class<? extends Unit> unitClass) {
        if (unitId == null || unitClass == null) {
            return false;
        }

        return unitId.equals(getUnitId(unitClass));
    }

    public static boolean isMerchantRoomItemForPurchaseId(String unitId) {
        return MERCHANT_ROOM_ITEM_FOR_PURCHASE_ID.equals(unitId);
    }

    private static <T> T createInstance(Class<? extends T> type) {
        if (type == null) {
            return null;
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static void registerItem(String itemId, Class<? extends Item> itemClass) {
        ITEM_CLASSES.put(itemId, itemClass);
        ITEM_IDS.put(itemClass, itemId);
    }

    private static void registerPrefix(String prefixId, Class<? extends Prefix> prefixClass) {
        PREFIX_CLASSES.put(prefixId, prefixClass);
        PREFIX_IDS.put(prefixClass, prefixId);
    }

    private static void registerUnit(String unitId, Class<? extends Unit> unitClass) {
        UNIT_CLASSES.put(unitId, unitClass);
        UNIT_IDS.put(unitClass, unitId);
    }

    private static void registerItems() {
        registerItem("items/AmuletOfYendor", com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor.class);
        registerItem("items/Gold", com.bilboldev.skillfulpixeldungeonplatformer.items.Gold.class);
        registerItem("items/Key", com.bilboldev.skillfulpixeldungeonplatformer.items.Key.class);
        registerItem("items/TomeOfMastery", com.bilboldev.skillfulpixeldungeonplatformer.items.TomeOfMastery.class);
        registerItem("items/Treasure", com.bilboldev.skillfulpixeldungeonplatformer.items.Treasure.class);

        registerItem("items/armor/Armor", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor.class);
        registerItem("items/armor/BirthdaySuit", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.BirthdaySuit.class);
        registerItem("items/armor/Cloth", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth.class);
        registerItem("items/armor/LeatherArmor", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.LeatherArmor.class);
        registerItem("items/armor/MailArmor", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.MailArmor.class);
        registerItem("items/armor/PlateArmor", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.PlateArmor.class);
        registerItem("items/armor/ScaleArmor", com.bilboldev.skillfulpixeldungeonplatformer.items.armor.ScaleArmor.class);

        registerItem("items/potions/HealthPotion", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion.class);
        registerItem("items/potions/ManaPotion", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.ManaPotion.class);
        registerItem("items/potions/Meat", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Meat.class);
        registerItem("items/potions/Potion", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Potion.class);
        registerItem("items/potions/PotionOfExperience", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfExperience.class);
        registerItem("items/potions/PotionOfFrost", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfFrost.class);
        registerItem("items/potions/PotionOfInvisibility", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfInvisibility.class);
        registerItem("items/potions/PotionOfLevitation", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLevitation.class);
        registerItem("items/potions/PotionOfLiquidFlame", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLiquidFlame.class);
        registerItem("items/potions/PotionOfMight", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfMight.class);
        registerItem("items/potions/PotionOfMindVision", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfMindVision.class);
        registerItem("items/potions/PotionOfParalyticGas", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfParalyticGas.class);
        registerItem("items/potions/PotionOfPurity", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfPurity.class);
        registerItem("items/potions/PotionOfStrength", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfStrength.class);
        registerItem("items/potions/PotionOfToxicGas", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfToxicGas.class);
        registerItem("items/potions/Rations", com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Rations.class);

        registerItem("items/quest/CorpseDust", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.CorpseDust.class);
        registerItem("items/quest/DarkGold", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.DarkGold.class);
        registerItem("items/quest/DriedRose", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.DriedRose.class);
        registerItem("items/quest/DwarfToken", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.DwarfToken.class);
        registerItem("items/quest/PhantomFish", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.PhantomFish.class);
        registerItem("items/quest/Pickaxe", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.Pickaxe.class);
        registerItem("items/quest/RatSkull", com.bilboldev.skillfulpixeldungeonplatformer.items.quest.RatSkull.class);

        registerItem("items/rings/Gemstone", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Gemstone.class);
        registerItem("items/rings/Ring", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring.class);
        registerItem("items/rings/RingOfAccuracy", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfAccuracy.class);
        registerItem("items/rings/RingOfDetection", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfDetection.class);
        registerItem("items/rings/RingOfElements", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfElements.class);
        registerItem("items/rings/RingOfEvasion", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfEvasion.class);
        registerItem("items/rings/RingOfHaggler", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHaggler.class);
        registerItem("items/rings/RingOfHaste", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHaste.class);
        registerItem("items/rings/RingOfHerbalism", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHerbalism.class);
        registerItem("items/rings/RingOfMending", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfMending.class);
        registerItem("items/rings/RingOfPower", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfPower.class);
        registerItem("items/rings/RingOfSatiety", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfSatiety.class);
        registerItem("items/rings/RingOfShadows", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfShadows.class);
        registerItem("items/rings/RingOfThorns", com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfThorns.class);

        registerItem("items/scrolls/Scroll", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.Scroll.class);
        registerItem("items/scrolls/ScrollOfBloodyRitual", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfBloodyRitual.class);
        registerItem("items/scrolls/ScrollOfChallenge", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfChallenge.class);
        registerItem("items/scrolls/ScrollOfEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfEnchantment.class);
        registerItem("items/scrolls/ScrollOfFrost", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfFrost.class);
        registerItem("items/scrolls/ScrollOfIdentify", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfIdentify.class);
        registerItem("items/scrolls/ScrollOfLullaby", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfLullaby.class);
        registerItem("items/scrolls/ScrollOfMagicMapping", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfMagicMapping.class);
        registerItem("items/scrolls/ScrollOfMirrorImage", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfMirrorImage.class);
        registerItem("items/scrolls/ScrollOfPsionicBlast", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfPsionicBlast.class);
        registerItem("items/scrolls/ScrollOfReadiness", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfReadiness.class);
        registerItem("items/scrolls/ScrollOfRecharging", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRecharging.class);
        registerItem("items/scrolls/ScrollOfRefuge", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRefuge.class);
        registerItem("items/scrolls/ScrollOfRemoveCurse", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRemoveCurse.class);
        registerItem("items/scrolls/ScrollOfSacrifice", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfSacrifice.class);
        registerItem("items/scrolls/ScrollOfSkill", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfSkill.class);
        registerItem("items/scrolls/ScrollOfTeleportation", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfTeleportation.class);
        registerItem("items/scrolls/ScrollOfTerror", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfTerror.class);
        registerItem("items/scrolls/ScrollOfUpgrade", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfUpgrade.class);
        registerItem("items/scrolls/ScrollOfWipeOut", com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfWipeOut.class);

        registerItem("items/seeds/Seed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.Seed.class);
        registerItem("items/seeds/DreamweedSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.DreamweedSeed.class);
        registerItem("items/seeds/EarthrootSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.EarthrootSeed.class);
        registerItem("items/seeds/FadeleafSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.FadeleafSeed.class);
        registerItem("items/seeds/FirebloomSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.FirebloomSeed.class);
        registerItem("items/seeds/IcecapSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.IcecapSeed.class);
        registerItem("items/seeds/RotberrySeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.RotberrySeed.class);
        registerItem("items/seeds/SorrowmossSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.SorrowmossSeed.class);
        registerItem("items/seeds/SungrassSeed", com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.SungrassSeed.class);

        registerItem("items/weapons/melee/Axe", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Axe.class);
        registerItem("items/weapons/melee/Dagger", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger.class);
        registerItem("items/weapons/melee/Glaive", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Glaive.class);
        registerItem("items/weapons/melee/HackSword", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.HackSword.class);
        registerItem("items/weapons/melee/Hammer", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Hammer.class);
        registerItem("items/weapons/melee/Knuckles", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles.class);
        registerItem("items/weapons/melee/LongSword", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword.class);
        registerItem("items/weapons/melee/Mace", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Mace.class);
        registerItem("items/weapons/melee/MeleeAttack", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack.class);
        registerItem("items/weapons/melee/Rod", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod.class);
        registerItem("items/weapons/melee/ShortSword", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.ShortSword.class);
        registerItem("items/weapons/melee/Spear", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Spear.class);
        registerItem("items/weapons/melee/Sword", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Sword.class);

        registerItem("items/weapons/melee/wands/FireBallWand", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBallWand.class);
        registerItem("items/weapons/melee/wands/FireBoltWand", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBoltWand.class);
        registerItem("items/weapons/melee/wands/IncinerationWand", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.IncinerationWand.class);
        registerItem("items/weapons/melee/wands/MagicMissileWand", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.MagicMissileWand.class);
        registerItem("items/weapons/melee/wands/Wand", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand.class);
        registerItem("items/weapons/melee/wands/WandOfAmok", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfAmok.class);
        registerItem("items/weapons/melee/wands/WandOfAvalanche", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfAvalanche.class);
        registerItem("items/weapons/melee/wands/WandOfBlink", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfBlink.class);
        registerItem("items/weapons/melee/wands/WandOfDisintegration", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfDisintegration.class);
        registerItem("items/weapons/melee/wands/WandOfFlock", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfFlock.class);
        registerItem("items/weapons/melee/wands/WandOfLightning", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfLightning.class);
        registerItem("items/weapons/melee/wands/WandOfPoison", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfPoison.class);
        registerItem("items/weapons/melee/wands/WandOfReach", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfReach.class);
        registerItem("items/weapons/melee/wands/WandOfRegrowth", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfRegrowth.class);
        registerItem("items/weapons/melee/wands/WandOfSlowness", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfSlowness.class);
        registerItem("items/weapons/melee/wands/WandOfTeleportation", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfTeleportation.class);

        registerItem("items/weapons/ranged/ArrowItem", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.ArrowItem.class);
        registerItem("items/weapons/ranged/BulletItem", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.BulletItem.class);
        registerItem("items/weapons/ranged/Handgun", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Handgun.class);
        registerItem("items/weapons/ranged/Pistol", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Pistol.class);
        registerItem("items/weapons/ranged/Rifle", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Rifle.class);
        registerItem("items/weapons/ranged/Blunderbuss", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Blunderbuss.class);
        registerItem("items/weapons/ranged/Mortar", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Mortar.class);
        registerItem("items/weapons/ranged/Bow", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow.class);
        registerItem("items/weapons/ranged/CurareDart", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.CurareDart.class);
        registerItem("items/weapons/ranged/FlameBow", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.FlameBow.class);
        registerItem("items/weapons/ranged/FrostBow", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.FrostBow.class);
        registerItem("items/weapons/ranged/Javelin", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Javelin.class);
        registerItem("items/weapons/ranged/MercenaryBow", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.MercenaryBow.class);
        registerItem("items/weapons/ranged/MercenaryShuriken", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.MercenaryShuriken.class);
        registerItem("items/weapons/ranged/Shuriken", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Shuriken.class);
        registerItem("items/weapons/ranged/ThrowDart", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.ThrowDart.class);
        registerItem("items/weapons/ranged/Tomahawk", com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Tomahawk.class);
    }

    private static void registerPrefixes() {
        registerPrefix("items/prefixes/armor/Blessed", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Blessed.class);
        registerPrefix("items/prefixes/armor/Cursed", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed.class);
        registerPrefix("items/prefixes/armor/Heavy", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Heavy.class);
        registerPrefix("items/prefixes/armor/Light", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Light.class);
        registerPrefix("items/prefixes/armor/glyphs/AffectionGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.AffectionGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/AntiEntropyGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.AntiEntropyGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/ArmorGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.ArmorGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/AutoRepairGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.AutoRepairGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/BounceGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.BounceGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/DisplacementGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.DisplacementGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/EntanglingGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.EntanglingGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/MetabolismGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.MetabolismGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/MultiplicityGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.MultiplicityGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/PotentialGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.PotentialGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/StenchGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.StenchGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/StoneGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.StoneGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/ThornsGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.ThornsGlyph.class);
        registerPrefix("items/prefixes/armor/glyphs/ViscosityGlyph", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.ViscosityGlyph.class);

        registerPrefix("items/prefixes/rings/Cursed", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed.class);

        registerPrefix("items/prefixes/weapons/Blessed", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.Blessed.class);
        registerPrefix("items/prefixes/weapons/Broken", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.Broken.class);
        registerPrefix("items/prefixes/weapons/Chipped", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.Chipped.class);
        registerPrefix("items/prefixes/weapons/Cursed", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.Cursed.class);
        registerPrefix("items/prefixes/weapons/Heavy", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.Heavy.class);
        registerPrefix("items/prefixes/weapons/Strong", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.Strong.class);
        registerPrefix("items/prefixes/weapons/enchantments/ChillingEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ChillingEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/CripplingEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.CripplingEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/DeathEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.DeathEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/FireEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.FireEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/HorrorEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.HorrorEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/InstabilityEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.InstabilityEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/LuckyEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.LuckyEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/ParalysisEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ParalysisEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/ShockEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ShockEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/TemperingEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.TemperingEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/VampiricEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.VampiricEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/VenomousEnchantment", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.VenomousEnchantment.class);
        registerPrefix("items/prefixes/weapons/enchantments/WeaponEnhancement", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.WeaponEnhancement.class);
        registerPrefix("items/prefixes/weapons/ranged/Guided", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Guided.class);
        registerPrefix("items/prefixes/weapons/ranged/Lots", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Lots.class);
        registerPrefix("items/prefixes/weapons/ranged/Poor", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Poor.class);
        registerPrefix("items/prefixes/weapons/ranged/Some", com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Some.class);
    }

    private static void registerUnits() {
        registerUnit("units/environment/items/ItemOnScreen", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen.class);
        registerUnit("units/environment/items/SpriteRewardOnScreen", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.SpriteRewardOnScreen.class);
        registerUnit("units/environment/items/TreasureOnScreen", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.TreasureOnScreen.class);
        registerUnit("units/environment/decoration/DarkGoldVein", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.DarkGoldVein.class);

        registerUnit("units/environment/plants/Plant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant.class);
        registerUnit("units/environment/plants/DreamweedPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.DreamweedPlant.class);
        registerUnit("units/environment/plants/EarthrootPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.EarthrootPlant.class);
        registerUnit("units/environment/plants/FadeleafPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.FadeleafPlant.class);
        registerUnit("units/environment/plants/FirebloomPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.FirebloomPlant.class);
        registerUnit("units/environment/plants/IcecapPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.IcecapPlant.class);
        registerUnit("units/environment/plants/RotberryPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.RotberryPlant.class);
        registerUnit("units/environment/plants/SorrowmossPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.SorrowmossPlant.class);
        registerUnit("units/environment/plants/SungrassPlant", com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.SungrassPlant.class);

        registerUnit("units/interactable/Blacksmith", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Blacksmith.class);
        registerUnit("units/interactable/DisturbableTomb", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableTomb.class);
        registerUnit("units/interactable/Ghost", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Ghost.class);
        registerUnit("units/interactable/GraveRemains", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.GraveRemains.class);
        registerUnit("units/interactable/Imp", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Imp.class);
        registerUnit("units/interactable/ImpShopkeeper", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.ImpShopkeeper.class);
        registerUnit("units/interactable/Interactable", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable.class);
        registerUnit("units/interactable/MercenaryRecruit", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit.class);
        registerUnit("units/interactable/Merchant", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Merchant.class);
        registerUnit("units/interactable/Wandmaker", com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Wandmaker.class);

        registerUnit("units/mobs/caves/DM300", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.DM300.class);
        registerUnit("units/mobs/caves/Elemental", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Elemental.class);
        registerUnit("units/mobs/caves/Monk", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Monk.class);
        registerUnit("units/mobs/caves/SeniorMonk", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.SeniorMonk.class);
        registerUnit("units/mobs/caves/Spinner", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Spinner.class);

        registerUnit("units/mobs/city/DwarfKing", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing.class);
        registerUnit("units/mobs/city/DwarfWarlock", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfWarlock.class);
        registerUnit("units/mobs/city/DwarvenUndead", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarvenUndead.class);
        registerUnit("units/mobs/city/Golem", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Golem.class);
        registerUnit("units/mobs/city/Succubus", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Succubus.class);

        registerUnit("units/mobs/halls/AcidicScorpio", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.AcidicScorpio.class);
        registerUnit("units/mobs/halls/BurningFist", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.BurningFist.class);
        registerUnit("units/mobs/halls/EvilEye", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.EvilEye.class);
        registerUnit("units/mobs/halls/Larva", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Larva.class);
        registerUnit("units/mobs/halls/RottingFist", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.RottingFist.class);
        registerUnit("units/mobs/halls/Scorpio", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Scorpio.class);
        registerUnit("units/mobs/halls/YogDzewa", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.YogDzewa.class);

        registerUnit("units/mobs/other/Statue", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Statue.class);
        registerUnit("units/mobs/other/Wraith", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Wraith.class);

        registerUnit("units/mobs/prison/Bat", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Bat.class);
        registerUnit("units/mobs/prison/Brute", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Brute.class);
        registerUnit("units/mobs/prison/Shaman", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Shaman.class);
        registerUnit("units/mobs/prison/ShieldedBrute", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.ShieldedBrute.class);
        registerUnit("units/mobs/prison/Tengu", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Tengu.class);

        registerUnit("units/mobs/sewers/AlbinoRat", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.AlbinoRat.class);
        registerUnit("units/mobs/sewers/Bandit", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Bandit.class);
        registerUnit("units/mobs/sewers/Crab", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Crab.class);
        registerUnit("units/mobs/sewers/CursePersonification", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.CursePersonification.class);
        registerUnit("units/mobs/sewers/FetidRat", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.FetidRat.class);
        registerUnit("units/mobs/sewers/Gnoll", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Gnoll.class);
        registerUnit("units/mobs/sewers/Goo", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Goo.class);
        registerUnit("units/mobs/sewers/Piranha", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Piranha.class);
        registerUnit("units/mobs/sewers/Rat", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Rat.class);
        registerUnit("units/mobs/sewers/Skeleton", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Skeleton.class);
        registerUnit("units/mobs/sewers/Swarm", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Swarm.class);
        registerUnit("units/mobs/sewers/Thief", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Thief.class);

        registerUnit("units/mobs/summons/FireElemental", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.FireElemental.class);
        registerUnit("units/mobs/summons/MirrorImage", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.MirrorImage.class);
        registerUnit("units/mobs/summons/RaisedSkeleton", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeleton.class);
        registerUnit("units/mobs/summons/SummonedGhost", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.SummonedGhost.class);
        registerUnit("units/mobs/summons/RaisedSkeletonArcher", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher.class);

        registerUnit("units/mobs/supporter/MercenaryAlly", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly.class);
        registerUnit("units/mobs/supporter/RatKing", com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.RatKing.class);

        registerUnit("units/traps/PlatformTrap", com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap.class);
    }
}
