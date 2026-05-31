package com.bilboldev.skillfulpixeldungeonplatformer.items.quest;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Invisible;

public class PhantomFish extends ConsumableItem {
    {
        name = "Phantom Fish";
        description = "You can barely see this tiny translucent fish in the air. In the water it becomes effectively invisible.";
        gs = new GameSprite("images/misc/extracted items/PHANTOM.png", 45, 45);
        quantity = 1;
        goldCost = 0;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.EAT, 0f, 1f);
        new Invisible().setPermanent(false).setDuration(12f).setOwner(UnitHelper.getInstance().getHero());
        EffectsHelper.getInstance().message(UnitHelper.getInstance().getHero(), "Invisible", Color.WHITE, 0f);
        quantity--;
        if (quantity < 1) {
            InventoryHelper.getInstance().removeItem(this);
        }
    }
}