package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Aggression;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Regeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Toughness;

public class ScrollOfReadiness extends Scroll {
    {
        name = "Scroll of Readiness";
        description = "A preparatory script that hardens the reader for the next fight.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 50;
    }

    @Override
    public void consume() {
        new Aggression().setPermanent(false).setDuration(12f).setOwner(getHero());
        new Toughness().setPermanent(false).setDuration(12f).setOwner(getHero());
        new Regeneration().setPermanent(false).setDuration(12f).setOwner(getHero());
        EffectsHelper.getInstance().message(getHero(), "Ready", Color.GOLD, 0f);
        super.consume();
    }
}