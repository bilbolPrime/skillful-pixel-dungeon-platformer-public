package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Levitation;

public class PotionOfLevitation extends Potion {
    {
        name = "Potion of Levitation";
        description = "This peculiar liquid makes you light enough to drift over pressure traps for a while.";
        gs = new GameSprite("images/misc/extracted items/POTION_TURQUOISE.png", 45, 45);
        quantity = 1;
        goldCost = 35;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        new Levitation().setPermanent(false).setDuration(16f).setOwner(getHero());
        EffectsHelper.getInstance().message(getHero(), "Levitating", Color.CYAN, 0f);
        finishConsume();
    }
}