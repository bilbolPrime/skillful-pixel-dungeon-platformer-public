package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Terrorized;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class ScrollOfTerror extends Scroll {
    {
        name = "Scroll of Terror";
        description = "A panic-inducing curse that sends nearby enemies into frightened disarray for a short time.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 50;
    }

    @Override
    public void consume() {
        int affected = 0;
        for (Mob mob : getActiveRoomMobs(false)) {
            new Terrorized().setPermanent(false).setDuration(6f).setOwner(mob);
            affected++;
        }

        EffectsHelper.getInstance().message(getHero(), affected > 0 ? "Terror" : "No fear", Color.RED, 0f);
        super.consume();
    }
}