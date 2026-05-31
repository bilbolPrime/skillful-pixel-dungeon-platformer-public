package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class PotionOfLiquidFlame extends Potion {
    {
        name = "Potion of Liquid Flame";
        description = "This unstable mixture bursts into searing fire the moment it is uncorked.";
        gs = new GameSprite("images/misc/extracted items/POTION_CRIMSON.png", 45, 45);
        quantity = 1;
        goldCost = 40;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        shatter(getHero().x, getHero().y, getHero().getRoom(), getHero(), getHero());
        EffectsHelper.getInstance().message(getHero(), "Flames erupt", Color.ORANGE, 0f);
        finishConsume();
    }

    @Override
    public void shatter(float impactX, float impactY, String roomIdentifier, Unit thrower, Unit directTarget) {
        super.shatter(impactX, impactY, roomIdentifier, thrower, directTarget);

        Unit source = thrower != null ? thrower : getHero();

        for (Unit unit : getUnitsNearPosition(roomIdentifier, impactX, impactY, 4f, 3f, true)) {
            unit.takeDamage(source, null, unit == directTarget ? 8f : 6f);
            for (int i = 0; i < 3; i++) {
                EffectsHelper.getInstance().spark(unit);
            }
        }
    }
}