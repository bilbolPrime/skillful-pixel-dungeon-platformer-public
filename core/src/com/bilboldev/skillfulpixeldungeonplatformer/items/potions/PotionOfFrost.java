package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.FreezingCloud;

public class PotionOfFrost extends Potion {
    {
        name = "Potion of Frost";
        description = "The air around this uncorked vial turns bitterly cold, freezing nearby creatures in place.";
        gs = new GameSprite("images/misc/extracted items/POTION_AZURE.png", 45, 45);
        quantity = 1;
        goldCost = 50;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        shatter(getHero().x, getHero().y, getHero().getRoom(), getHero(), getHero());
        EffectsHelper.getInstance().message(getHero(), "Freezing cloud", Color.CYAN, 0f);
        finishConsume();
    }

    @Override
    public void shatter(float impactX, float impactY, String roomIdentifier, Unit thrower, Unit directTarget) {
        super.shatter(impactX, impactY, roomIdentifier, thrower, directTarget);
        FreezingCloud cloud = new FreezingCloud().setOwner(thrower);
        cloud.x = impactX;
        cloud.y = impactY;
        cloud.setRoom(roomIdentifier);
        UnitHelper.getInstance().addUnit(cloud);
    }
}