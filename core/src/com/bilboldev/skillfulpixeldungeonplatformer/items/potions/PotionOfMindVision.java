package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class PotionOfMindVision extends Potion {
    {
        name = "Potion of Mind Vision";
        description = "Your mind sharpens, letting you sense hostile presences beyond your immediate sight.";
        gs = new GameSprite("images/misc/extracted items/POTION_IVORY.png", 45, 45);
        quantity = 1;
        goldCost = 35;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        int sensed = 0;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof Mob) || unit.isFriendly || unit.showOnly() || unit.isDead()) {
                continue;
            }

            sensed++;
            if (unit.getRoom() != null && unit.getRoom().equals(getHero().getRoom())) {
                EffectsHelper.getInstance().message(unit, "Seen", Color.CYAN, 0f);
            }
        }

        EffectsHelper.getInstance().message(getHero(), sensed > 0 ? sensed + " minds sensed" : "No hostile minds", Color.CYAN, 0f);
        finishConsume();
    }
}