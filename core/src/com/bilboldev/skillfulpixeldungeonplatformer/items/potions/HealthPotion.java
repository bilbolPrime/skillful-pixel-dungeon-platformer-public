package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;

public class HealthPotion extends Potion {
    {
        name = "Potion of Healing";
        description = "An elixir that will instantly return you to full health and cure poison.";
        gs = new GameSprite("images/items/health-potion.png", 45, 45);
        quantity = 1;
        goldCost = 30;
        setAlwaysIdentified(true);
    }

    @Override
    public void consume(){
        consume(UnitHelper.getInstance().getHero());
    }

    public void consume(Unit target) {
        if (target == null) {
            return;
        }

        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        target.removeBuff(new Poisoned());
        target.heal(1000);
        EffectsHelper.getInstance().heal(target);
        finishConsume();
    }
}

