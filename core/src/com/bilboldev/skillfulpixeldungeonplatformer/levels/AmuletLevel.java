package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.EntryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;

import java.util.HashMap;

public class AmuletLevel extends Level {
    {
        units = new HashMap<>();
        perRoom = 0;
        spawnStateFrozen = true;
    }

    @Override
    public Level generateLevel(int depth) {
        this.depth = depth;
        String identifier = RandomHelper.getInstance().uniqueId();
        entryRoom = (EntryRoom) new EntryRoom(identifier).build();
        entryRoom.setSignMessage("At the heart of the dungeon, the Amulet of Yendor waits in silence.");
        entryDoor = entryRoom.getLevelEntryDoor();

        identifier = RandomHelper.getInstance().uniqueId();
        Room sanctum = new Room(identifier) {
            {
                canSpawn = false;
                width = 20;
                height = 10;
            }

            @Override
            public Room build() {
                for (int i = 4; i < width - 4; i++) {
                    platforms.add(UtilsHelper.platformKey(i, 4));
                }
                return this;
            }

            @Override
            public com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door getRandomDoor() {
                com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door door = new com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door();
                door.x = 9 * ConstantsHelper.TILE;
                door.y = 5 * ConstantsHelper.TILE;
                return door;
            }
        }.build();

        ItemOnScreen amulet = new ItemOnScreen(new AmuletOfYendor());
        amulet.x = 10 * ConstantsHelper.TILE;
        amulet.y = 5 * ConstantsHelper.TILE;
        amulet.floorY = 5 * ConstantsHelper.TILE;
        amulet.setRoom(sanctum.getIdentifier());
        UnitHelper.getInstance().addUnit(amulet);

        com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door door1 = sanctum.getRandomDoor();
        com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door door2 = entryRoom.getRandomDoor();
        door1.otherDoor = door2;
        door2.otherDoor = door1;
        door1.setLeadsTo(entryRoom.getIdentifier());
        door2.setLeadsTo(sanctum.getIdentifier());
        sanctum.addDoor(door1);
        entryRoom.addDoor(door2);

        addRoom(sanctum);
        addRoom(entryRoom);
        return this;
    }

    @Override
    protected int chooseSpecialRoom(int depth) {
        return -1;
    }
}