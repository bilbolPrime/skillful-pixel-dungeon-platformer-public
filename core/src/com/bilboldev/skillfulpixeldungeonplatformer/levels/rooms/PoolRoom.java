package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfInvisibility;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Piranha;

import java.util.ArrayList;

public class PoolRoom extends SingleDoorSpecialRoom {
    private static final int PIRANHA_COUNT = 3;
    private static final String CHEST_TILE = UtilsHelper.platformKey(9, 6);
    private static final String POTION_TILE = UtilsHelper.platformKey(14, 4);

    public PoolRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build() {
        buildWidePlatforms();
        waterPlatforms.addAll(platforms);
        placeRewardContainer(SpecialRoomRewards.randomWeaponOrArmorReward(), "images/misc/extracted items/CHEST.png", 96, 96, 9, 7);
        placeItem(new PotionOfInvisibility(), 14, 5);
        spawnPiranhas();

        return this;
    }

    @Override
    public boolean hasWaterAt(float worldX, float floorY) {
        int tileY = Math.max(0, (int) (floorY / ConstantsHelper.TILE) - 1);
        if (tileY == MapHelper.getInstance().MIN_FLOOR - 1) {
            return true;
        }

        return super.hasWaterAt(worldX, floorY);
    }

    private void spawnPiranhas() {
        ArrayList<String> candidates = new ArrayList<String>(waterPlatforms);
        candidates.remove(CHEST_TILE);
        candidates.remove(POTION_TILE);

        for (int spawned = 0; spawned < PIRANHA_COUNT && !candidates.isEmpty(); ) {
            String platform = candidates.remove(RandomHelper.getInstance().randomInt(candidates.size()));
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float spawnX = tileToWorldX(tileX);
            float floorY = floorTileToWorldY(tileY + 1);

            if (!UnitHelper.getInstance().freeSpace((int) spawnX, (int) floorY, identifier)) {
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
    }
}