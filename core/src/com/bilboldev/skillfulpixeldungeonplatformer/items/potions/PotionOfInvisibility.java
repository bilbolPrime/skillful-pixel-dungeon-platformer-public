package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Invisible;

public class PotionOfInvisibility extends Potion {
    {
        name = "Potion of Invisibility";
        description = "Drinking this potion shrouds you from enemy attention until you reveal yourself.";
        gs = new GameSprite("images/misc/extracted items/POTION_SILVER.png", 45, 45);
        quantity = 1;
        goldCost = 40;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        new Invisible().setPermanent(false).setDuration(12f).setOwner(getHero());
        EffectsHelper.getInstance().message(getHero(), "Invisible", Color.WHITE, 0f);
        finishConsume();
    }
}