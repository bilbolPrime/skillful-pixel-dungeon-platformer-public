package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelEntryDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelExitDoor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class RoomConnections {
    private RoomConnections() { }


    public static void connectOrdinaryRooms(List<Room> rooms) {
        for (int index = 1; index < rooms.size(); index++) {
            connect(rooms.get(index), rooms.get(RandomHelper.getInstance().randomInt(index)));
        }
        if (RandomHelper.getInstance().randomBoolean()) {
            ArrayList<Room[]> extras = new ArrayList<>();
            for (int first = 0; first < rooms.size(); first++) {
                for (int second = first + 1; second < rooms.size(); second++) {
                    Room a = rooms.get(first), b = rooms.get(second);
                    if (!a.connectedTo(b.getIdentifier())) extras.add(new Room[]{a, b});
                }
            }
            if (!extras.isEmpty()) {
                Room[] pair = extras.get(RandomHelper.getInstance().randomInt(extras.size()));
                connect(pair[0], pair[1]);
            }
        }
    }

    public static void connect(Room first, Room second) {
        connect(first, first.getRandomDoor(), second, second.getRandomDoor());
    }

    public static void connect(Room first, Door outward, Room second, Door inward) {
        if (first == null || second == null || first == second || first.getIdentifier() == null
                || second.getIdentifier() == null
                || first.getIdentifier().equals(second.getIdentifier())
                || outward == null || inward == null || outward == inward
                || first.connectedTo(second.getIdentifier()) || second.connectedTo(first.getIdentifier())) {
            throw new IllegalArgumentException("Invalid or duplicate room connection");
        }
        outward.otherDoor = inward;
        inward.otherDoor = outward;
        outward.setLeadsTo(second.getIdentifier());
        inward.setLeadsTo(first.getIdentifier());
        first.addDoor(outward);
        second.addDoor(inward);
    }

    public static void validateConnected(List<Room> rooms, Room entrance, Room exit) {
        Map<String, Room> byId = index(rooms);
        if (entrance == null || exit == null || byId.get(entrance.getIdentifier()) != entrance
                || byId.get(exit.getIdentifier()) != exit) {
            throw new IllegalStateException("Missing floor entrance or exit");
        }
        Map<Door, Room> owners = new IdentityHashMap<>();
        for (Room room : rooms) {
            for (Door door : room.getDoors()) {
                if (door == null || owners.put(door, room) != null) fail(room, "duplicate/null door");
            }
        }
        for (Room room : rooms) {
            Set<String> destinations = new LinkedHashSet<>();
            for (Door door : room.getDoors()) {
                if (door.otherDoor == null && door.getLeadsTo() == null
                        && ((room == entrance && door instanceof LevelEntryDoor)
                        || (room == exit && door instanceof LevelExitDoor))) continue;
                Room destination = byId.get(door.getLeadsTo());
                if (destination == null || destination == room || door.otherDoor == null
                        || owners.get(door.otherDoor) != destination || door.otherDoor.otherDoor != door
                        || !room.getIdentifier().equals(door.otherDoor.getLeadsTo())
                        || !destinations.add(destination.getIdentifier())) fail(room, "non-reciprocal door");
                if (door.isLocked() != door.otherDoor.isLocked()
                        || door.requiresKey() != door.otherDoor.requiresKey()) fail(room, "mismatched lock");
            }
        }
        Set<Room> connected = reachable(byId, entrance, null, true);
        for (Room room : rooms) if (!connected.contains(room)) fail(room, "disconnected from entrance");
    }


    public static Set<Room> reachableWithoutLocks(List<Room> rooms, Room entrance, Room excluded) {
        return reachable(index(rooms), entrance, excluded, false);
    }

    private static Set<Room> reachable(Map<String, Room> byId, Room entrance, Room excluded, boolean passLocks) {
        Set<Room> visited = new LinkedHashSet<>();
        ArrayDeque<Room> pending = new ArrayDeque<>();
        if (entrance != null && entrance != excluded && byId.get(entrance.getIdentifier()) == entrance) {
            visited.add(entrance);
            pending.add(entrance);
        }
        while (!pending.isEmpty()) {
            Room current = pending.remove();
            for (Door door : current.getDoors()) {
                if (door == null || door.otherDoor == null) continue;
                Room destination = byId.get(door.getLeadsTo());
                if (destination == null || destination == excluded || visited.contains(destination)
                        || door.otherDoor.otherDoor != door || !destination.getDoors().contains(door.otherDoor)
                        || !current.getIdentifier().equals(door.otherDoor.getLeadsTo())
                        || (!passLocks && (door.isLocked() || door.otherDoor.isLocked()))) continue;
                visited.add(destination);
                pending.add(destination);
            }
        }
        return visited;
    }

    private static Map<String, Room> index(List<Room> rooms) {
        Map<String, Room> result = new LinkedHashMap<>();
        if (rooms == null) throw new IllegalStateException("Missing floor rooms");
        for (Room room : rooms) {
            if (room == null || room.getIdentifier() == null || room.getDoors() == null
                    || result.put(room.getIdentifier(), room) != null) {
                throw new IllegalStateException("Missing or duplicate room identity");
            }
        }
        return result;
    }

    private static void fail(Room room, String reason) {
        throw new IllegalStateException("Room " + room.getIdentifier() + ": " + reason);
    }
}
