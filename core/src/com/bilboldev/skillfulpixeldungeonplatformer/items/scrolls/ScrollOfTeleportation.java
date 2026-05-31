package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ScrollOfTeleportation extends Scroll {
    {
        name = "Scroll of Teleportation";
        description = "Chaotic runes that fling the reader to another room on the current floor.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_GOHOME.png", 45, 45);
        goldCost = 40;
    }

    @Override
    public void consume() {
        if (!MapHelper.getInstance().teleportHeroToRandomRoom()) {
            WindowHelper.getInstance().addWindow(1000f, 120f, "The scroll fizzles without finding a destination.");
            return;
        }

        EffectsHelper.getInstance().message(getHero(), "Teleported", Color.CYAN, 0f);
        super.consume();
    }
}