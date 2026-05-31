package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public class PotionOfStrength extends Potion {
    {
        name = "Potion of Strength";
        description = "This potent elixir permanently increases your Strength by one.";
        gs = new GameSprite("images/misc/extracted items/POTION_GOLDEN.png", 45, 45);
        quantity = 1;
        goldCost = 100;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        UnitHelper.getInstance().getHero().modifyStrength(1);
        EffectsHelper.getInstance().message(UnitHelper.getInstance().getHero(), "+1 STR", Color.GOLD, 0f);
        finishConsume();
    }
}