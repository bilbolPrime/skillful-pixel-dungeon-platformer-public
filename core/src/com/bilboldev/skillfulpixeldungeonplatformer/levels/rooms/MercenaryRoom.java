package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper.MercenaryType;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.MercenaryDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit;

public class MercenaryRoom extends SingleDoorSpecialRoom {
    private MercenaryRecruit recruit;

    public MercenaryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {

        if (SaveHelper.getInstance().isRestoringRoomManifest() && !SaveHelper.getInstance().hasManifestRoom(identifier)) return null;
        int depth = MapHelper.getInstance().getDepth();
        recruit = RandomHelper.getInstance().withRoomGenerationRandom(depth, identifier, 0x52454352554954L,
                () -> MercenaryRecruit.createRandom(depth));
        if (recruit == null) {
            return null;
        }

        buildRecruitLayout();
        return finishLayout();
    }

    protected void buildRecruitLayout() {
        resetLayout();
        width = 18 + 2 * layoutVariant(2);
        getLayout().describe("recruit-rest", width - 4, 4);
        addPlatformSpan(6, 9, 3);
        addPlatformSpan(8, 11, 5);
        addPlatformSpan(width - 6, width - 3, 3);
    }

    @Override
    protected void placeContents() {
        requireContentPlacement(width - 4, 4, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        recruit.x = (width - 4) * ConstantsHelper.TILE;
        recruit.y = 4 * ConstantsHelper.TILE;
        recruit.floorY = recruit.y;
        recruit.setRoom(identifier);
        UnitHelper.getInstance().addUnit(recruit);
        rememberRecruitType(recruit.getMercenaryType());
    }


    public void rememberRecruitType(MercenaryType type) {
        for (Door entrance : getDoors()) {
            if (entrance.otherDoor instanceof MercenaryDoor && identifier.equals(entrance.otherDoor.getLeadsTo())) {
                ((MercenaryDoor) entrance.otherDoor).rememberRecruitType(type);
            }
        }
    }
}
