package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.Library;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;

import java.util.ArrayList;

public class LibraryRoom extends SingleDoorSpecialRoom {
    {
        canSpawn = false;
        width = 15;
    }

    public LibraryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Room build(){
        resetLayout();
        width = 20 + 2 * layoutVariant(2);
        getLayout().describe("library-gallery", width / 2, 3);
        addPlatformSpan(5, 6, 3);
        addPlatformSpan(7, width - 4, 4);
        addPlatformSpan(8, width - 5, 6);
        return finishLayout();
    }

    @Override
    protected void placeContents() {
        ArrayList<Item> libraryScrolls = InventoryHelper.getInstance().getLibraryScrolls(
                MapHelper.getInstance().getDepth(),
                identifier,
                2);

        for(int i = 0; i < libraryScrolls.size(); i++){
            requireContentPlacement(width / 2 - 1 + i, 5,
                    ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
            ItemOnScreen itemOnScreen = new ItemOnScreen(libraryScrolls.get(i));
            itemOnScreen.x = (width / 2 - 1 + i) * ConstantsHelper.TILE;
            itemOnScreen.y = 5 * ConstantsHelper.TILE;
            itemOnScreen.floorY = 5 * ConstantsHelper.TILE;
            itemOnScreen.setRoom(identifier);
            UnitHelper.getInstance().addUnit(itemOnScreen);
        }

    }
}

