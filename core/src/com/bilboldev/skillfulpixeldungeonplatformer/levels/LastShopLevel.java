package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.EntryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.ExitRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MerchantRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;

import java.util.HashMap;

public class LastShopLevel extends Level {
    {
        units = new HashMap<>();
        perRoom = 0;
        spawnStateFrozen = true;
    }

    @Override
    public Level generateLevel(int depth) {
        this.depth = depth;
        String entryIdentifier = RandomHelper.getInstance().uniqueId();
        String hubIdentifier = RandomHelper.getInstance().uniqueId();
        String shopIdentifier = RandomHelper.getInstance().uniqueId();
        String exitIdentifier = RandomHelper.getInstance().uniqueId();

        entryRoom = (EntryRoom) new EntryRoom(entryIdentifier).build();
        setSignMessage(depth);
        entryDoor = entryRoom.getLevelEntryDoor();

        Room hubRoom = new Room(hubIdentifier).build();
        Room shopRoom = new MerchantRoom(shopIdentifier, true).build();
        exitRoom = new ExitRoom(exitIdentifier).build();

        connectRooms(entryRoom, entryRoom.getRandomDoor(), hubRoom, hubRoom.getRandomDoor());
        connectRooms(hubRoom, hubRoom.getRandomDoor().toMerchantDoor(), shopRoom, shopRoom.getRandomDoor());
        connectRooms(hubRoom, hubRoom.getRandomDoor(), exitRoom, exitRoom.getRandomDoor());

        addRoom(entryRoom);
        addRoom(hubRoom);
        addRoom(shopRoom);
        addRoom(exitRoom);
        exitRoom.addDoor(exitRoom.getRandomDoor().toExitDoor());
        return this;
    }

    private void connectRooms(Room firstRoom, Door firstDoor, Room secondRoom, Door secondDoor) {
        firstDoor.otherDoor = secondDoor;
        secondDoor.otherDoor = firstDoor;

        firstDoor.setLeadsTo(secondRoom.getIdentifier());
        secondDoor.setLeadsTo(firstRoom.getIdentifier());

        firstRoom.addDoor(firstDoor);
        secondRoom.addDoor(secondDoor);
    }

    @Override
    protected void setSignMessage(int depth) {
        entryRoom.setSignMessage("An ambitious imp is doing business below.");
    }

    @Override
    protected int chooseSpecialRoom(int depth) {
        return -1;
    }
}