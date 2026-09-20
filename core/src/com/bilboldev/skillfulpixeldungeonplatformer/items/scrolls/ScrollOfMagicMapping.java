package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;

import java.util.ArrayList;

public class ScrollOfMagicMapping extends Scroll {
    {
        name = "Scroll of Magic Mapping";
        description = "Revealing glyphs that uncover hidden secrets on the current floor.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        goldCost = 45;
    }

    @Override
    public void consume() {
        int revealed = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (!(unit instanceof PlatformTrap)) {
                continue;
            }

            PlatformTrap trap = (PlatformTrap) unit;
            if (MapHelper.getInstance().getRoom(trap.getRoom()) == null) {
                continue;
            }
            if (!trap.isHidden()) {
                continue;
            }

            trap.setHidden(false);
            revealed++;
        }

        EffectsHelper.getInstance().message(getHero(), revealed > 0 ? "Secrets revealed" : "Nothing hidden", Color.CYAN, 0f);
        super.consume();
    }
}
