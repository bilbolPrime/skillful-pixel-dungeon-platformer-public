package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.LeatherArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.MailArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.PlateArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.ScaleArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.ManaPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfFrost;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfInvisibility;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLevitation;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfLiquidFlame;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfMindVision;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfParalyticGas;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfPurity;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfStrength;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfToxicGas;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Rations;
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
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfIdentify;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRefuge;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRemoveCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfTeleportation;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.SungrassSeed;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Axe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Glaive;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Hammer;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Mace;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Spear;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Sword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBallWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBoltWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.MagicMissileWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfBlink;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfDisintegration;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfLightning;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfRegrowth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfSlowness;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Javelin;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Shuriken;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.ThrowDart;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Tomahawk;

public final class SpecialRoomRewards {

    private SpecialRoomRewards() {
    }

    public static Item randomWeaponOrArmorReward() {
        return instantiate(
                LeatherArmor.class,
                MailArmor.class,
                ScaleArmor.class,
                PlateArmor.class,
                Axe.class,
                Glaive.class,
                Hammer.class,
                LongSword.class,
                Mace.class,
                Spear.class,
                Sword.class,
                Bow.class,
                Javelin.class,
                Tomahawk.class);
    }

    public static Item randomArmorReward() {
        return instantiate(
                LeatherArmor.class,
                MailArmor.class,
                ScaleArmor.class,
                PlateArmor.class);
    }

    public static Item randomPotionReward() {
        return instantiate(
                HealthPotion.class,
                ManaPotion.class,
                PotionOfInvisibility.class,
                PotionOfLevitation.class,
                PotionOfLiquidFlame.class,
                PotionOfMindVision.class,
                PotionOfPurity.class,
                PotionOfFrost.class,
                PotionOfStrength.class,
                PotionOfToxicGas.class,
                PotionOfParalyticGas.class);
    }

    public static Item randomWellReward() {
        return instantiate(
                HealthPotion.class,
                ManaPotion.class,
                PotionOfMindVision.class,
                Gemstone.class,
                ScrollOfIdentify.class);
    }

    public static Item randomWandOrRingReward() {
        return instantiate(
                FireBoltWand.class,
                FireBallWand.class,
                MagicMissileWand.class,
                WandOfBlink.class,
                WandOfLightning.class,
                WandOfDisintegration.class,
                WandOfSlowness.class,
                WandOfRegrowth.class,
                RingOfDetection.class,
                RingOfHaste.class,
                RingOfMending.class,
                RingOfAccuracy.class,
                RingOfEvasion.class,
                RingOfPower.class,
                RingOfElements.class,
                RingOfShadows.class,
                RingOfHaggler.class,
                RingOfHerbalism.class,
                RingOfSatiety.class,
                RingOfThorns.class,
                Gemstone.class);
    }

    public static Item randomSupplyReward() {
        return instantiate(
                Rations.class,
                HealthPotion.class,
                ManaPotion.class,
                PotionOfInvisibility.class,
                PotionOfLevitation.class,
                PotionOfLiquidFlame.class,
                ScrollOfIdentify.class,
                ScrollOfRefuge.class,
                ScrollOfTeleportation.class,
                ScrollOfRemoveCurse.class,
                ScrollOfEnchantment.class,
                ThrowDart.class,
                Shuriken.class);
    }

    public static Item randomGardenReward() {
        return instantiate(
                SungrassSeed.class,
                HealthPotion.class,
                Rations.class);
    }

    public static Gold goldStack(int minimum, int maximum) {
        Gold gold = new Gold();
        gold.setQuantity(minimum + RandomHelper.getInstance().randomInt(Math.max(1, maximum - minimum + 1)));
        return gold;
    }

    @SafeVarargs
    private static Item instantiate(Class<? extends Item>... candidates) {
        if (candidates == null || candidates.length == 0) {
            return new Gold();
        }

        try {
            return candidates[RandomHelper.getInstance().randomInt(candidates.length)].newInstance();
        }
        catch (Exception ignored) {
            return new Gold();
        }
    }
}