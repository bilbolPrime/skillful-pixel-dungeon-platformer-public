package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.AffectionGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.AntiEntropyGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.AutoRepairGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.BounceGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.DisplacementGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.EntanglingGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.MetabolismGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.MultiplicityGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.PotentialGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.StenchGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.glyphs.ViscosityGlyph;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.enchantments.ChillingEnchantment;
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
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryItemChoiceWindow;

import java.util.ArrayList;

public class ScrollOfEnchantment extends Scroll {
    {
        name = "Scroll of Enchantment";
        description = "A runic script that lays a magical enchantment on a weapon or a glyph on armor, replacing ordinary modifiers with a special property.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 55;
    }

    @Override
    public void consume() {
        ArrayList<Item> candidates = new ArrayList<Item>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if ((item instanceof Weapon && !(item instanceof Wand)) || item instanceof Armor) {
                candidates.add(item);
            }
        }

        if (candidates.isEmpty()) {
            WindowHelper.getInstance().addWindow(1000f, 120f, "Nothing in your pack can be enchanted.");
            return;
        }

        WindowHelper.getInstance().addWindow(new InventoryItemChoiceWindow(
                getGameSprite().spriteString,
                "Choose a weapon or armor to enchant.",
                candidates,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        enchantSelection(item);
                    }
                }).build());
    }

    private void enchantSelection(Item item) {
        Prefix prefix = createEnchantment(item);
        if (prefix == null) {
            return;
        }

        item.identify();
        if (item instanceof Weapon) {
            ((Weapon) item).setPrefix(prefix);
        }
        else if (item instanceof Armor) {
            ((Armor) item).setPrefix(prefix);
        }

        EffectsHelper.getInstance().enchanting(getHero(), item, prefix);
        SoundHelper.GetSingleton().play(Sounds.ZAP, 0.2f, 0.9f);
        EffectsHelper.getInstance().message(getHero(), "Enchanted", Color.CYAN, 0f);
        super.consume();
    }

    private Prefix createEnchantment(Item item) {
        ArrayList<Prefix> prefixes = new ArrayList<Prefix>();
        if (item instanceof Armor) {
            prefixes.add(new AffectionGlyph());
            prefixes.add(new AntiEntropyGlyph());
            prefixes.add(new AutoRepairGlyph());
            prefixes.add(new BounceGlyph());
            prefixes.add(new DisplacementGlyph());
            prefixes.add(new EntanglingGlyph());
            prefixes.add(new MetabolismGlyph());
            prefixes.add(new MultiplicityGlyph());
            prefixes.add(new PotentialGlyph());
            prefixes.add(new StenchGlyph());
            prefixes.add(new ViscosityGlyph());
        }
        else if (item instanceof Weapon && !(item instanceof Wand)) {
            prefixes.add(new DeathEnchantment());
            prefixes.add(new FireEnchantment());
            prefixes.add(new HorrorEnchantment());
            prefixes.add(new InstabilityEnchantment());
            prefixes.add(new VenomousEnchantment());
            prefixes.add(new VampiricEnchantment());
            prefixes.add(new LuckyEnchantment());
            prefixes.add(new ParalysisEnchantment());
            prefixes.add(new ShockEnchantment());
            prefixes.add(new ChillingEnchantment());
            prefixes.add(new TemperingEnchantment());
        }

        if (prefixes.isEmpty()) {
            return null;
        }

        return prefixes.get(RandomHelper.getInstance().randomInt(prefixes.size()));
    }
}