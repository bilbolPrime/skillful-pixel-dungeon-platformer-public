package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryItemChoiceWindow;

import java.util.ArrayList;

public class ScrollOfRemoveCurse extends Scroll {
    {
        name = "Scroll of Remove Curse";
        description = "A cleansing prayer that strips a cursed armor piece or ring of its affliction.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 40;
    }

    @Override
    public void consume() {
        ArrayList<Item> candidates = new ArrayList<Item>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (isCursedItem(item)) {
                candidates.add(item);
            }
        }

        if (candidates.isEmpty()) {
            WindowHelper.getInstance().addWindow(1000f, 120f, "Nothing in your pack is cursed.");
            return;
        }

        WindowHelper.getInstance().addWindow(new InventoryItemChoiceWindow(
                getGameSprite().spriteString,
                "Choose a cursed item to cleanse.",
                candidates,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        removeCurseSelection(item);
                    }
                }).build());
    }

    private void removeCurseSelection(Item item) {
        item.identify();
        if (item instanceof Armor) {
            ((Armor) item).setPrefix((Prefix) null);
        }
        else if (item instanceof Ring) {
            ((Ring) item).setPrefix((Prefix) null);
        }

        EffectsHelper.getInstance().message(getHero(), "Purified", Color.GOLD, 0f);
        super.consume();
    }

    private boolean isCursedItem(Item item) {
        if (item instanceof Armor) {
            return ((Armor) item).getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed;
        }

        if (item instanceof Ring) {
            return ((Ring) item).getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed;
        }

        return false;
    }
}