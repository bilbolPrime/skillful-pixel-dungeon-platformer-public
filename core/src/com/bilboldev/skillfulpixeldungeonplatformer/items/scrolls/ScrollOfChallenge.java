package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;

public class ScrollOfChallenge extends Scroll {
    {
        name = "Scroll of Challenge";
        description = "A taunting invocation that rouses every hostile creature on the floor.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 50;
    }

    @Override
    public void consume() {
        int alerted = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (!(unit instanceof Mob) || unit.isFriendly || unit.showOnly()) {
                continue;
            }

            ((Mob) unit).alert(getHero());
            alerted++;
        }

        EffectsHelper.getInstance().message(getHero(), alerted > 0 ? "Challenge!" : "No answer", Color.ORANGE, 0f);
        super.consume();
    }
}