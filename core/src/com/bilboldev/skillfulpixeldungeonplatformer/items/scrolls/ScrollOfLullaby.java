package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class ScrollOfLullaby extends Scroll {
    {
        name = "Scroll of Lullaby";
        description = "A drowsy tune that leaves nearby enemies temporarily unable to act.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 45;
    }

    @Override
    public void consume() {
        int affected = 0;
        for (Mob mob : getActiveRoomMobs(false)) {
            mob.putToSleep();
            affected++;
        }

        EffectsHelper.getInstance().message(getHero(), affected > 0 ? "Sleep" : "Too quiet", Color.WHITE, 0f);
        super.consume();
    }
}