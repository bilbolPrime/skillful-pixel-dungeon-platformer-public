package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class PotionOfExperience extends Potion {
    {
        name = "Potion of Experience";
        description = "A draught distilled from hard-won lessons. Drinking it raises your experience by one level.";
        gs = new GameSprite("images/misc/extracted items/POTION_INDIGO.png", 45, 45);
        quantity = 1;
        goldCost = 80;
    }

    @Override
    public void consume() {
        Hero hero = UnitHelper.getInstance().getHero();
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        hero.earnExp(hero.getExperienceToNextLevel());
        EffectsHelper.getInstance().message(hero, "Insight gained", Color.CYAN, 0f);
        finishConsume();
    }
}