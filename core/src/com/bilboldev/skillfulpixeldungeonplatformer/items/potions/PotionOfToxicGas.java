package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.ToxicGasCloud;

public class PotionOfToxicGas extends Potion {
    {
        name = "Potion of Toxic Gas";
        description = "A violent reaction fills the air with a deadly green cloud when this potion is opened.";
        gs = new GameSprite("images/misc/extracted items/POTION_CHARCOAL.png", 45, 45);
        quantity = 1;
        goldCost = 40;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        shatter(getHero().x, getHero().y, getHero().getRoom(), getHero(), getHero());
        EffectsHelper.getInstance().message(getHero(), "Toxic cloud", Color.GREEN, 0f);
        finishConsume();
    }

    @Override
    public void shatter(float impactX, float impactY, String roomIdentifier, Unit thrower, Unit directTarget) {
        super.shatter(impactX, impactY, roomIdentifier, thrower, directTarget);
        ToxicGasCloud cloud = new ToxicGasCloud().setOwner(thrower);
        cloud.x = impactX;
        cloud.y = impactY;
        cloud.setRoom(roomIdentifier);
        UnitHelper.getInstance().addUnit(cloud);
    }
}