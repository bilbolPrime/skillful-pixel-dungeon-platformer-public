package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ChillingEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.CripplingEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.DeathEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.FireEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.HorrorEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.InstabilityEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.LuckyEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ParalysisEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ShockEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.TemperingEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.VampiricEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.VenomousEnchantment;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public final class EnhancementVisualHelper {
    private static final float ENHANCEMENT_PULSE_PERIOD_SECONDS = 0.8f;

    private static final Color DEATH = rgb(0x000000);
    private static final Color FIRE = rgb(0xFF4400);
    private static final Color HORROR = rgb(0x222222);
    private static final Color INSTABILITY = rgb(0xFFFFFF);
    private static final Color VENOMOUS = rgb(0x4400AA);
    private static final Color VAMPIRIC = rgb(0x660022);
    private static final Color LUCKY = rgb(0x00FF00);
    private static final Color PARALYSIS = rgb(0xCCAA44);
    private static final Color SHOCK = rgb(0x66CCEE);
    private static final Color CHILLING = rgb(0x0044FF);
    private static final Color TEMPERING = rgb(0xCC8888);
    private static final Color CRIPPLING = rgb(0x884400);

    private EnhancementVisualHelper() {
    }

    public static void applyItemEnhancementPulse(GameSprite sprite, Item item) {
        if (item instanceof Weapon) {
            applyWeaponEnhancementPulse(sprite, (Weapon) item);
            return;
        }

        clearPulse(sprite);
    }

    public static void applyWeaponEnhancementPulse(GameSprite sprite, Weapon weapon) {
        applyPrefixEnhancementPulse(sprite, weapon == null ? null : weapon.getPrefix());
    }

    public static void applyPrefixEnhancementPulse(GameSprite sprite, Prefix prefix) {
        if (sprite == null) {
            return;
        }

        Color glowColor = getEnhancementGlowColor(prefix);
        if (glowColor == null) {
            sprite.clearPulseTint();
            return;
        }

        sprite.setPulseTint(glowColor, ENHANCEMENT_PULSE_PERIOD_SECONDS);
    }

    private static void clearPulse(GameSprite sprite) {
        if (sprite != null) {
            sprite.clearPulseTint();
        }
    }

    private static Color getEnhancementGlowColor(Prefix prefix) {
        if (prefix == null || !prefix.isEnhancement()) {
            return null;
        }

        if (prefix instanceof DeathEnchantment) {
            return DEATH;
        }
        if (prefix instanceof FireEnchantment) {
            return FIRE;
        }
        if (prefix instanceof HorrorEnchantment) {
            return HORROR;
        }
        if (prefix instanceof InstabilityEnchantment) {
            return INSTABILITY;
        }
        if (prefix instanceof VenomousEnchantment) {
            return VENOMOUS;
        }
        if (prefix instanceof VampiricEnchantment) {
            return VAMPIRIC;
        }
        if (prefix instanceof LuckyEnchantment) {
            return LUCKY;
        }
        if (prefix instanceof ParalysisEnchantment) {
            return PARALYSIS;
        }
        if (prefix instanceof ShockEnchantment) {
            return SHOCK;
        }
        if (prefix instanceof ChillingEnchantment) {
            return CHILLING;
        }
        if (prefix instanceof TemperingEnchantment) {
            return TEMPERING;
        }
        if (prefix instanceof CripplingEnchantment) {
            return CRIPPLING;
        }

        return INSTABILITY;
    }

    private static Color rgb(int color) {
        return new Color(
                ((color >> 16) & 0xFF) / 255f,
                ((color >> 8) & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                1f);
    }
}