package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public class ManaPotion extends Potion {
    {
        name = "Potion of Mana";
        description = "An elixir that instantly replenishes spiritual energy.";
        gs = new GameSprite("images/misc/extracted items/POTION_MANA.png", 45, 45);
        quantity = 1;
        goldCost = 30;
        setAlwaysIdentified(true);
    }

    @Override
    public void consume(){
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        UnitHelper.getInstance().getHero().modifyMana(1000);
        finishConsume();
    }
}

