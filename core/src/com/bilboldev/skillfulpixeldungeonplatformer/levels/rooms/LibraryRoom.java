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

public class LibraryRoom extends Room {
    {
        canSpawn = false;
        width = 15;
    }

    public LibraryRoom(String identifier) {
        super(identifier);
    }

    @Override
    public Door getRandomDoor(){
        int tileX = 0;
        int tileY = 0;
        boolean doorExists = false;
        do {
            doorExists = false;
            String platform = "";
            for(String candidatePlatform : platforms){
                platform = candidatePlatform;
                if(RandomHelper.getInstance().randomBoolean()){
                    break;
                }
            }

            tileX = Integer.parseInt(platform.split("_")[0]);
            tileY = Integer.parseInt(platform.split("_")[1]);

            for(Door door : doors){
                if(door.x == tileX * ConstantsHelper.TILE && door.y == (tileY + 1) * ConstantsHelper.TILE){
                    doorExists = true;
                    break;
                }
            }
        }while(doorExists || tileX < 0 || tileX >= width);

        Door door = new Door();
        door.x = tileX * ConstantsHelper.TILE;
        door.y = (tileY + 1)* ConstantsHelper.TILE;

        return door;
    }

    @Override
    public Room build(){
        super.build();

        for(int i = 0; i < width; i++){
            Library library = new Library();
            library.x = i * ConstantsHelper.TILE;
            library.y = 3 * ConstantsHelper.TILE;

            stuff.add(library);
        }

        ArrayList<Item> libraryScrolls = InventoryHelper.getInstance().getLibraryScrolls(
                MapHelper.getInstance().getDepth(),
                identifier,
                2);

        for(int i = 0; i < libraryScrolls.size(); i++){
            ItemOnScreen itemOnScreen = new ItemOnScreen(libraryScrolls.get(i));
            itemOnScreen.x = (width / 2 - 1 + i) * ConstantsHelper.TILE;
            itemOnScreen.y = 3 * ConstantsHelper.TILE;
            itemOnScreen.floorY = 3 * ConstantsHelper.TILE;
            itemOnScreen.setRoom(identifier);
            UnitHelper.getInstance().addUnit(itemOnScreen);
        }

        return this;
    }
}

