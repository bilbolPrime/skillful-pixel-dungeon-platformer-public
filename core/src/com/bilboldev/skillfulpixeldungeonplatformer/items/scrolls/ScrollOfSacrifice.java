package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class ScrollOfSacrifice extends Scroll {
    {
        name = "Scroll of Sacrifice";
        description = "A forbidden rite that trades flesh for power. Reading it grants 1 Strength at the cost of 20% of your current vitality.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_SACRIFICE.png", 45, 45);
        quantity = 1;
        goldCost = 65;
    }

    @Override
    public void consume() {
        Hero hero = UnitHelper.getInstance().getHero();
        int hpCost = Math.max(5, hero.getMaxHP() / 5);
        hero.setHP(Math.max(1, hero.getHP() - hpCost));
        hero.modifyStrength(1);
        EffectsHelper.getInstance().message(hero, "+1 STR / -" + hpCost + " HP", Color.GOLD, 0f);
        super.consume();
    }
}