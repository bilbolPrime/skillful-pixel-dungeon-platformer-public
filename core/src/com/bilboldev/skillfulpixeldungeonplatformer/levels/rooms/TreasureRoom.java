package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.ScrollOfRefuge;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.Library;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.TreasureOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Statue;

public class TreasureRoom extends SingleDoorSpecialRoom {
    {
        canSpawn = false;
        width = 15;
    }
    private Statue statue, statue2;

    public TreasureRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build(){
        resetLayout();
        width = 22 + 2 * layoutVariant(2);
        getLayout().describe("statue-treasure-court", width / 2, 3);
        addPlatformSpan(5, 8, 4);
        addPlatformSpan(width - 9, width - 5, 4);
        return this;
    }

    @Override
    protected void placeContents() {
        for (int tile : new int[]{width / 2 - 2, width / 2, width / 2 + 2}) {
            requireContentPlacement(tile, 3, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        }

        statue = new Statue();
        statue.x = (width / 2 )* ConstantsHelper.TILE - 2 * ConstantsHelper.TILE;
        statue.y = 3 * ConstantsHelper.TILE;
        statue.floorY = statue.y;
        statue.setRoom(identifier);

        statue2 = new Statue();
        statue2.x = (width / 2 )* ConstantsHelper.TILE + 2 * ConstantsHelper.TILE;
        statue2.y = 3 * ConstantsHelper.TILE;
        statue2.floorY = statue2.y;
        statue2.facingRight = false;
        statue2.setRoom(identifier);

        UnitHelper.getInstance().addUnit(statue);
        UnitHelper.getInstance().addUnit(statue2);

        TreasureOnScreen treasure = new TreasureOnScreen(){
            @Override
            public void pickedUp(){
                super.pickedUp();
                statue.trigger();
                statue2.trigger();
                SoundHelper.GetSingleton().play(Sounds.ALERT, 0, 1);
            }
        };

        treasure.x = (width / 2 )* ConstantsHelper.TILE;
        treasure.y = 3 * ConstantsHelper.TILE;
        treasure.floorY = treasure.y;
        treasure.setRoom(identifier);
        UnitHelper.getInstance().addUnit(treasure);


    }
}

