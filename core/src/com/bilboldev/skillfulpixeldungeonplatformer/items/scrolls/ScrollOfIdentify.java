package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.IdentifyScrollWindow;

import java.util.ArrayList;

public class ScrollOfIdentify extends Scroll {
    {
        name = "Scroll of Identify";
        description = "A revealing incantation that permanently uncovers one item in your pack.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_GYFU.png", 45, 45);
        quantity = 1;
        goldCost = 30;
    }

    @Override
    public void consume() {
        ArrayList<Item> unidentifiedItems = new ArrayList<Item>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (item == this || item.isIdentified()) {
                continue;
            }

            unidentifiedItems.add(item);
        }

        if (unidentifiedItems.isEmpty()) {
            WindowHelper.getInstance().addWindow(
                    1000f,
                    120f,
                    Messages.get("custom.ui.identify.nothing"));
            return;
        }

        WindowHelper.getInstance().addWindow(new IdentifyScrollWindow(this, unidentifiedItems).build());
    }

    public void identifySelection(Item item) {
        if (item == null) {
            return;
        }

        item.identify();
        EffectsHelper.getInstance().message(UnitHelper.getInstance().getHero(), item.getTrueName(), Color.GOLD, 0f);
        super.consume();
    }
}