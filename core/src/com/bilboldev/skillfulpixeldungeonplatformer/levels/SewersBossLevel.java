package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.EntryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.ExitRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.GooRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.LibraryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers.Sewers;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.AlbinoRat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Crab;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Rat;

import java.util.ArrayList;
import java.util.HashMap;

public class SewersBossLevel extends SewersLevel {
    {
        units = new HashMap<>();
        perRoom = 0;
    }

    @Override
    public Level generateLevel(int depth){
        this.depth = depth;
        String identifier = RandomHelper.getInstance().uniqueId();
        String identifierExit = RandomHelper.getInstance().uniqueId();

        entryRoom = (EntryRoom) new EntryRoom(identifier).build();
        setSignMessage(depth);

        entryDoor = entryRoom.getLevelEntryDoor();
        exitRoom = new ExitRoom(identifierExit).build();

        identifier = RandomHelper.getInstance().uniqueId();
        Room room = new GooRoom(identifier).build();
        addRoom(room);

        // Entry
        Room chosenRoom = room;
        Door door1 = chosenRoom.getRandomDoor();
        Door door2 = entryRoom.getRandomDoor();

        door1.otherDoor = door2;
        door2.otherDoor = door1;

        door1.setLeadsTo(entryRoom.getIdentifier());
        door2.setLeadsTo(chosenRoom.getIdentifier());

        chosenRoom.addDoor(door1);
        entryRoom.addDoor(door2);


        // Exit
        door1 = chosenRoom.getRandomDoor();
        door2 = exitRoom.getRandomDoor();

        door1.otherDoor = door2;
        door2.otherDoor = door1;

        door1.setLeadsTo(exitRoom.getIdentifier());
        door2.setLeadsTo(chosenRoom.getIdentifier());

        chosenRoom.addDoor(door1);
        exitRoom.addDoor(door2);
        addRoom(entryRoom);
        addRoom(exitRoom);

        Door doorExit = exitRoom.getRandomDoor().toExitDoor();
        exitRoom.addDoor(doorExit);
        return this;
    }

    @Override
    protected void setSignMessage(int depth){
        entryRoom.setSignMessage(new Sewers().getSignMessages()[depth - 1]);
    }
}

