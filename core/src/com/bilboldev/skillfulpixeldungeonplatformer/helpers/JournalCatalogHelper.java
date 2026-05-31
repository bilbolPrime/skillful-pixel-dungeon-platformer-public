package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.ManaPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfExperience;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfFrost;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfInvisibility;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLevitation;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLiquidFlame;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfMight;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfMindVision;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfParalyticGas;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfPurity;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfStrength;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfToxicGas;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Gemstone;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfAccuracy;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfDetection;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfElements;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfEvasion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHaggler;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHaste;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHerbalism;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfMending;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfPower;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfSatiety;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfShadows;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfThorns;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfBloodyRitual;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfChallenge;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfFrost;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfIdentify;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfLullaby;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfMagicMapping;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfMirrorImage;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfPsionicBlast;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfReadiness;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRecharging;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRefuge;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRemoveCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfSacrifice;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfTeleportation;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfTerror;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfUpgrade;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfWipeOut;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBallWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBoltWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.IncinerationWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.MagicMissileWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfAmok;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfAvalanche;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfBlink;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfDisintegration;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfFlock;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfLightning;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfPoison;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfReach;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfRegrowth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfSlowness;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfTeleportation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class JournalCatalogHelper {

    public static ArrayList<Section> buildSections() {
        ArrayList<Section> sections = new ArrayList<Section>();
        sections.add(buildSection("Potions", list(
                HealthPotion.class,
                PotionOfExperience.class,
                PotionOfToxicGas.class,
                PotionOfLiquidFlame.class,
                PotionOfStrength.class,
                PotionOfParalyticGas.class,
                PotionOfLevitation.class,
                PotionOfMindVision.class,
                PotionOfPurity.class,
                PotionOfInvisibility.class,
                PotionOfMight.class,
                PotionOfFrost.class,
                ManaPotion.class
        )));
        sections.add(buildSection("Scrolls", list(
                ScrollOfIdentify.class,
                ScrollOfMagicMapping.class,
                ScrollOfRecharging.class,
                ScrollOfRemoveCurse.class,
                ScrollOfTeleportation.class,
                ScrollOfChallenge.class,
                ScrollOfTerror.class,
                ScrollOfLullaby.class,
                ScrollOfPsionicBlast.class,
                ScrollOfMirrorImage.class,
                ScrollOfUpgrade.class,
                ScrollOfEnchantment.class,
                ScrollOfRefuge.class,
                ScrollOfSacrifice.class,
                ScrollOfBloodyRitual.class,
                ScrollOfSkill.class,
                ScrollOfFrost.class,
                ScrollOfWipeOut.class,
                ScrollOfReadiness.class
        )));
        sections.add(buildSection("Rings", list(
                RingOfMending.class,
                RingOfDetection.class,
                RingOfShadows.class,
                RingOfPower.class,
                RingOfHerbalism.class,
                RingOfAccuracy.class,
                RingOfEvasion.class,
                RingOfSatiety.class,
                RingOfHaste.class,
                RingOfHaggler.class,
                RingOfElements.class,
                RingOfThorns.class,
                Gemstone.class
        )));
        sections.add(buildSection("Wands", list(
                WandOfTeleportation.class,
                WandOfSlowness.class,
                FireBoltWand.class,
                WandOfPoison.class,
                WandOfRegrowth.class,
                WandOfBlink.class,
                WandOfLightning.class,
                WandOfAmok.class,
                WandOfReach.class,
                WandOfFlock.class,
                WandOfDisintegration.class,
                WandOfAvalanche.class,
                MagicMissileWand.class,
                FireBallWand.class,
                IncinerationWand.class
        )));
        return sections;
    }

    private static Section buildSection(String title, ArrayList<Class<? extends Item>> itemClasses) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        int identifiedCount = 0;

        for (Class<? extends Item> itemClass : itemClasses) {
            Item item = instantiate(itemClass);
            if (item == null) {
                continue;
            }

            boolean identified = item.isIdentified();
            if (identified) {
                identifiedCount++;
            }

            String familyName = item.getIdentityFamily().getDisplaySuffix();
            String titleText = identified
                    ? safeText(item.getName())
                    : Messages.get("custom.ui.journal.unknown_family", new Object[]{familyName.toLowerCase(Locale.ROOT)});
            String description = identified
                    ? safeText(item.getDescription())
                    : safeText(ItemIdentityHelper.getInstance().getDisplayDescription(item));
            entries.add(new Entry(titleText, description, item.getTrueSpritePath(), identified));
        }

        return new Section(title, identifiedCount, entries);
    }

    @SafeVarargs
    private static ArrayList<Class<? extends Item>> list(Class<? extends Item>... itemClasses) {
        return new ArrayList<Class<? extends Item>>(Arrays.asList(itemClasses));
    }

    private static Item instantiate(Class<? extends Item> itemClass) {
        try {
            return itemClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    private static String safeText(String text) {
        return text != null ? text : "";
    }

    public static class Section {
        public final String title;
        public final int identifiedCount;
        public final ArrayList<Entry> entries;

        public Section(String title, int identifiedCount, ArrayList<Entry> entries) {
            this.title = title;
            this.identifiedCount = identifiedCount;
            this.entries = entries;
        }

        public int totalCount() {
            return entries.size();
        }
    }

    public static class Entry {
        public final String title;
        public final String description;
        public final String spritePath;
        public final boolean identified;

        public Entry(String title, String description, String spritePath, boolean identified) {
            this.title = title;
            this.description = description;
            this.spritePath = spritePath;
            this.identified = identified;
        }
    }
}