package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit;

public class MercenaryRoom extends SingleDoorSpecialRoom {

    public MercenaryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        MercenaryRecruit recruit = MercenaryRecruit.createRandom(MapHelper.getInstance().getDepth());
        if (recruit == null) {
            return null;
        }

        buildWidePlatforms();
        recruit.x = (width - 3) * ConstantsHelper.TILE;
        recruit.y = 3 * ConstantsHelper.TILE;
        recruit.floorY = recruit.y;
        recruit.setRoom(identifier);
        UnitHelper.getInstance().addUnit(recruit);
        return this;
    }
}