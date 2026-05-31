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

    @Override
    public Room build(){
        super.build();
        levelEntryDoor = getRandomDoor().toEntryDoor();
        addDoor(levelEntryDoor);

        // Add the sign in a very non-abstract way
        if(levelEntryDoor.y == ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE){
            // Add it to the right of the door
            Sign sign = new Sign("The words on this sign cannot be understood.", levelEntryDoor.x  + ConstantsHelper.TILE, levelEntryDoor.y);
            setSign(sign);
        }
        else { // Get the platform and add it to left / right of door
            for(String platform : platforms){
                int tileX = Integer.parseInt(platform.split("_")[0]);
                int tileY = Integer.parseInt(platform.split("_")[1]);

                // Find the platform
                if(tileX * ConstantsHelper.TILE == levelEntryDoor.x && (tileY + 1) * ConstantsHelper.TILE == levelEntryDoor.y){
                    // Check the right or left
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

