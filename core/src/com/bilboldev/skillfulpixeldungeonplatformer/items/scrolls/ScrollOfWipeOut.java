package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;

public class ScrollOfWipeOut extends Scroll {
    {
        name = "Scroll of Wipe Out";
        description = "An annihilating incantation that destroys non-boss hostiles on the current floor.";
        gs = new GameSprite("images/misc/extracted items/SCROLL_WIPE_OUT.png", 45, 45);
        goldCost = 120;
    }

    @Override
    public void consume() {
        int slain = 0;
        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {

            if (!(unit instanceof Mob) || unit.isFriendly || unit.showOnly || unit.isDead() || unit.getHP() < 1
                    || MapHelper.getInstance().getRoom(unit.getRoom()) == null) {
                continue;
            }

            Mob mob = (Mob) unit;
            if (mob.isBoss()) {
                continue;
            }

            mob.takeDamage(getHero(), null, 99999f);
            slain++;
        }

        EffectsHelper.getInstance().message(getHero(), slain > 0 ? "Wipe out" : "No targets", Color.RED, 0f);
        super.consume();
    }
}
