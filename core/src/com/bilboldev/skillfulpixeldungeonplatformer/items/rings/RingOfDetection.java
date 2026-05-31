package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;

public class RingOfDetection extends Ring {
    {
        name = "Ring of Detection";
        description = "This ring sharpens your senses, revealing hidden traps around you while worn.";
        gs = new GameSprite("images/misc/extracted items/RING_AGATE.png", 45, 45);
    }

    @Override
    protected void whileEquipped(Hero hero, float deltaSeconds) {
        float radius = (2.5f + 0.75f * Math.max(0, getLevel() - 1)) * ConstantsHelper.TILE;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof PlatformTrap)) {
                continue;
            }

            PlatformTrap trap = (PlatformTrap) unit;
            if (!trap.isHidden() || hero.getRoom() == null || trap.getRoom() == null || !hero.getRoom().equals(trap.getRoom())) {
                continue;
            }

            float dx = (hero.x + ConstantsHelper.TILE / 2f) - (trap.x + ConstantsHelper.TILE / 2f);
            float dy = hero.y - trap.y;
            if (dx * dx + dy * dy <= radius * radius) {
                trap.setHidden(false);
            }
        }
    }
}