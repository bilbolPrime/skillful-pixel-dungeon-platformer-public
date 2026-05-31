package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Starving;

public class Rations extends ConsumableItem {
    {
        name = "Rations";
        description = "Starving heroes do not regenerate health or mana. They also take 1 damage every 10 seconds until they eat.";
        gs = new GameSprite("images/misc/extracted items/RATION.png", 45, 45);
        quantity = 1;
        goldCost = 50;
    }

    @Override
    public void consume(){
        SoundHelper.GetSingleton().play(Sounds.EAT, 0, 1f);

        quantity--;

        UnitHelper.getInstance().getHero().eat();

        if(quantity < 1){
            InventoryHelper.getInstance().removeItem(this);
        }
    }
}

