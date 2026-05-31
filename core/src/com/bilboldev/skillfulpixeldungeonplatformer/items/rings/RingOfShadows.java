package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class RingOfShadows extends Ring {
    {
        name = "Ring of Shadows";
        description = "This ring helps you slip through the dark, causing distant enemies to lose track of you more easily.";
        gs = new GameSprite("images/misc/extracted items/RING_OPAL.png", 45, 45);
    }

    @Override
    protected void whileEquipped(Hero hero, float deltaSeconds) {
        float keepAggroRadius = (2.5f + 0.75f * getLevel()) * ConstantsHelper.TILE;
        float keepAggroRadiusSq = keepAggroRadius * keepAggroRadius;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof Mob) || unit.isFriendly || unit.isDead()) {
                continue;
            }

            if (hero.getRoom() == null || unit.getRoom() == null || !hero.getRoom().equals(unit.getRoom())) {
                continue;
            }

            float dx = (hero.x + ConstantsHelper.TILE / 2f) - (unit.x + ConstantsHelper.TILE / 2f);
            float dy = hero.y - unit.y;
            if (dx * dx + dy * dy > keepAggroRadiusSq) {
                ((Mob) unit).forgetTarget(hero);
            }
        }
    }
}