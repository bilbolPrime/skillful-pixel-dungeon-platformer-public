package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class ScrollOfSkill extends Scroll {
    {
        name = "Scroll of Skill";
        description = "Words of hard-earned wisdom that grant the reader an extra skill point.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_SKILLPOINT.png", 45, 45);
        quantity = 1;
        goldCost = 40;
    }

    @Override
    public void consume() {
        Hero hero = UnitHelper.getInstance().getHero();
        hero.setSkillPoints(hero.getSkillPoints() + 1);
        EffectsHelper.getInstance().message(hero, "+1 Skill Point", Color.CYAN, 0f);
        super.consume();
    }
}