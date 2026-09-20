package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfInvisibility;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Piranha;

import java.util.ArrayList;

public class PoolRoom extends SingleDoorSpecialRoom {
    private static final int PIRANHA_COUNT = 3;

    public PoolRoom(String identifier) {
        super(identifier);
    }

    @Override public int getWaterSurfaceThickness() { return 30; }

    @Override
    public Room build() {
        resetLayout();
        width = 22 + 2 * layoutVariant(2);
        getLayout().describe("flooded-basin", width / 2, 3);

        addGroundWater();
        addPlatformSpan(6, 8, 3);
        addPlatformSpan(width / 2, width / 2 + 1, 3);
        addPlatformSpan(width - 7, width - 3, 4);
        return finishLayout();
    }

    @Override
    protected void placeContents() {
        placeRewardContainer(SpecialRoomRewards.randomWeaponOrArmorReward(), "images/misc/extracted items/CHEST.png", 96, 96, width - 4, 5);
        placeItem(new PotionOfInvisibility(), 5, 3);
        spawnPiranhas();

    }

    private void spawnPiranhas() {
        ArrayList<String> candidates = new ArrayList<String>(waterPlatforms);
        java.util.Collections.sort(candidates);

        int spawned = 0;
        while (spawned < PIRANHA_COUNT && !candidates.isEmpty()) {
            String platform = candidates.remove(RandomHelper.getInstance().randomInt(candidates.size()));
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float spawnX = tileToWorldX(tileX);
            float floorY = floorTileToWorldY(tileY + 1);

            if (!hasSupportedPlacement(spawnX, floorY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS)
                    || getLayout().arrivalReserved(spawnX, floorY, ConstantsHelper.UNIT_DIMENSIONS)
                    || !UnitHelper.getInstance().freeSpace((int) spawnX, (int) floorY, identifier)) {
                continue;
            }

            Piranha piranha = new Piranha();
            piranha.x = spawnX;
            piranha.y = floorY;
            piranha.floorY = floorY;
            piranha.facingRight = RandomHelper.getInstance().randomBoolean();
            piranha.setRoom(identifier);
            piranha.wakeToWandering();
            UnitHelper.getInstance().addUnit(piranha);
            spawned++;
        }
        if (spawned != PIRANHA_COUNT) throw new IllegalStateException("Insufficient wet piranha slots in " + identifier);
    }
}
