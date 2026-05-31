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

public class TreasureRoom extends Room {
    {
        canSpawn = false;
        width = 15;
    }
    private Statue statue, statue2;

    public TreasureRoom(String identifier) {
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
        platforms.clear();
        waterPlatforms.clear();
        stuff.clear();
        addPlatformSpan(2, width - 3, 2);
        addPlatformSpan(4, width - 5, 4);

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


        return this;
    }
}

