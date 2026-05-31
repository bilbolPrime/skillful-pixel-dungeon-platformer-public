package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryItemChoiceWindow;

import java.util.ArrayList;

public class ScrollOfUpgrade extends Scroll {
    {
        name = "Scroll of Upgrade";
        description = "A masterwork rune that improves one upgradeable item and strips curses from gear it uplifts.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 70;
    }

    @Override
    public void consume() {
        ArrayList<Item> candidates = new ArrayList<Item>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (item.canUpgrade()) {
                candidates.add(item);
            }
        }

        if (candidates.isEmpty()) {
            WindowHelper.getInstance().addWindow(1000f, 120f, "Nothing in your pack can be upgraded.");
            return;
        }

        WindowHelper.getInstance().addWindow(new InventoryItemChoiceWindow(
                getGameSprite().spriteString,
            "Choose an upgradeable item.",
                candidates,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        upgradeSelection(item);
                    }
                }).build());
    }

    private void upgradeSelection(Item item) {
        if (!item.canUpgrade()) {
            WindowHelper.getInstance().addWindow(1000f, 120f, "That item cannot be upgraded any further.");
            return;
        }

        item.identify();
        item.modifyLevel(1);
        AchievementManager.getInstance().onItemUpgraded(item);

        if (item instanceof Armor) {
            Armor armor = (Armor) item;
            if (armor.getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed) {
                armor.setPrefix((Prefix) null);
            }
        }
        else if (item instanceof Ring) {
            Ring ring = (Ring) item;
            if (ring.getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed) {
                ring.setPrefix((Prefix) null);
            }
        }

        if (item instanceof Wand && item == getHero().getWeapon()) {
            UIHelper.getInstance().equiped((MeleeWeapon) item);
        }

        EffectsHelper.getInstance().message(getHero(), "Upgraded", Color.GOLD, 0f);
        super.consume();
    }
}