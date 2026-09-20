package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Sign;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelEntryDoor;

import java.util.ArrayList;
import java.util.HashSet;

public class EntryRoom extends Room{
    LevelEntryDoor levelEntryDoor;

    {
        width = 15;
        canSpawn = false;
    }

    public EntryRoom(String identifier) {
        super(identifier);
    }

    public EntryRoom buildFoyer(int depth) {
        RoomFoyers.build(this, depth, true);
        Door terminal = new Door();
        terminal.x = 3 * ConstantsHelper.TILE;
        terminal.y = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE;
        levelEntryDoor = terminal.toEntryDoor();
        addDoor(levelEntryDoor);
        setSign(new Sign("The words on this sign cannot be understood.", 6 * ConstantsHelper.TILE, terminal.y));
        return this;
    }

    @Override
    public Room build(){
        super.build();
        levelEntryDoor = getRandomDoor().toEntryDoor();
        addDoor(levelEntryDoor);


        if(levelEntryDoor.y == ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE){

            Sign sign = new Sign("The words on this sign cannot be understood.", levelEntryDoor.x  + ConstantsHelper.TILE, levelEntryDoor.y);
            setSign(sign);
        }
        else {
            for(String platform : platforms){
                int tileX = Integer.parseInt(platform.split("_")[0]);
                int tileY = Integer.parseInt(platform.split("_")[1]);


                if(tileX * ConstantsHelper.TILE == levelEntryDoor.x && (tileY + 1) * ConstantsHelper.TILE == levelEntryDoor.y){

                    String candidatePlatform = UtilsHelper.platformKey(tileX + 1, tileY);
                    if(platforms.contains(candidatePlatform)){
                        Sign sign = new Sign("The words on this sign cannot be understood.", levelEntryDoor.x  + ConstantsHelper.TILE, levelEntryDoor.y);
                        setSign(sign);
                    } else{
                        Sign sign = new Sign("The words on this sign cannot be understood.", levelEntryDoor.x  - ConstantsHelper.TILE, levelEntryDoor.y);
                        setSign(sign);
                    }
                }
            }
        }
        return this;
    }

    public LevelEntryDoor getLevelEntryDoor(){
        return levelEntryDoor;
    }

    public void setSignMessage(String message){
        this.sign.setMessage(message);
    }
}

