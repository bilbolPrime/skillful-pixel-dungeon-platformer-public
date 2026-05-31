package com.bilboldev.skillfulpixeldungeonplatformer.items.potions;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Poisoned;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Purity;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.ParalyticGasCloud;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PoisonCloud;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.ToxicGasCloud;

import java.util.ArrayList;

public class PotionOfPurity extends Potion {
    {
        name = "Potion of Purification";
        description = "This cleansing brew neutralizes poison and wards you against harmful gas for a time.";
        gs = new GameSprite("images/misc/extracted items/POTION_JADE.png", 45, 45);
        quantity = 1;
        goldCost = 50;
    }

    @Override
    public void consume() {
        SoundHelper.GetSingleton().play(Sounds.DRINK, 0, 1f);
        getHero().removeBuff(new Poisoned());
        new Purity().setPermanent(false).setDuration(18f).setOwner(getHero());
        EffectsHelper.getInstance().message(getHero(), "Purified", Color.WHITE, 0f);
        finishConsume();
    }

    @Override
    public void shatter(float impactX, float impactY, String roomIdentifier, Unit thrower, Unit directTarget) {
        super.shatter(impactX, impactY, roomIdentifier, thrower, directTarget);

        int cleared = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (unit == null || unit.getRoom() == null || !unit.getRoom().equals(roomIdentifier)) {
                continue;
            }

            if (!(unit instanceof ToxicGasCloud) && !(unit instanceof PoisonCloud) && !(unit instanceof ParalyticGasCloud)) {
                continue;
            }

            if (Math.abs(unit.x - impactX) <= 4f * ConstantsHelper.TILE && Math.abs(unit.y - impactY) <= 3f * ConstantsHelper.TILE) {
                UnitHelper.getInstance().removeUnit(unit);
                cleared++;
            }
        }

        if (thrower != null) {
            EffectsHelper.getInstance().message(thrower, cleared > 0 ? "Gas neutralized" : "No gas", Color.WHITE, 0f);
        }
    }
}