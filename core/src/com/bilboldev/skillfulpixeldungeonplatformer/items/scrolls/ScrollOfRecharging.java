package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class ScrollOfRecharging extends Scroll {
    {
        name = "Scroll of Recharging";
        description = "In this dungeon's mana-driven magic, the script restores the reader's magical reserves instead of wand charges.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 45;
    }

    @Override
    public void consume() {
        getHero().setMp(getHero().getMmp());
        EffectsHelper.getInstance().message(getHero(), "Mana restored", Color.CYAN, 0f);
        super.consume();
    }
}