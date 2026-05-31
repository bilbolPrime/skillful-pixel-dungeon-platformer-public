package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public class PotionOfMight extends Potion {
    {
        name = "Potion of Might";
        description = "A heroic draught that permanently increases your Strength and maximum health.";
        gs = new GameSprite("images/misc/extracted items/POTION_MAGENTA.png", 45, 45);
        quantity = 1;
        goldCost = 200;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        getHero().modifyStrength(1);
        getHero().setMaxHP(getHero().getMaxHP() + 5);
        getHero().setHP(getHero().getHP() + 5);
        EffectsHelper.getInstance().heal(getHero());
        EffectsHelper.getInstance().message(getHero(), "+1 STR / +5 HP", Color.GOLD, 0f);
        finishConsume();
    }
}