package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public final class RoomDisplayDepth {
    public enum Direction { DEEPER, SHALLOWER, SAME, UNAVAILABLE }

    private final String entrance;
    private final Map<String, Integer> ranks;
    private final Set<String> ordinaryRooms;
    private final Set<String> appearanceSources;
    private final Map<String, Set<String>> connections;

    private RoomDisplayDepth(String entrance, Map<String, Integer> ranks, Set<String> ordinaryRooms, Set<String> appearanceSources,
                             Map<String, Set<String>> connections) {
        this.entrance = entrance;
        this.ranks = Collections.unmodifiableMap(ranks);
        this.ordinaryRooms = Collections.unmodifiableSet(ordinaryRooms);
        this.appearanceSources = Collections.unmodifiableSet(appearanceSources);
        this.connections = Collections.unmodifiableMap(connections);
    }


    public static RoomDisplayDepth from(Level level) {
        Map<String, Room> rooms = new HashMap<>();
        Set<String> ambiguous = new HashSet<>();
        Map<String, Integer> ranks = new HashMap<>();
        Set<String> ordinary = new HashSet<>();
        Set<String> appearanceSources = new HashSet<>();
        Map<String, Set<String>> connections = new HashMap<>();
        String entrance = level == null || level.getEntryRoom() == null ? null : level.getEntryRoom().getIdentifier();
        if (level != null && level.rooms != null) {
            for (Room room : level.rooms) {
                if (room == null || room.getIdentifier() == null) continue;
                if (rooms.put(room.getIdentifier(), room) != null) ambiguous.add(room.getIdentifier());

                if (RoomSnapshot.hasUsableBounds(room)) {
                    if (room.getClass() == Room.class) ordinary.add(room.getIdentifier());
                    if (RoomSnapshot.SourceKind.of(room) != RoomSnapshot.SourceKind.UNSUPPORTED) appearanceSources.add(room.getIdentifier());
                }
            }
        }
        for (String id : ambiguous) { rooms.remove(id); ordinary.remove(id); appearanceSources.remove(id); }
        for (Room room : rooms.values()) {
            Set<String> connected = new HashSet<>();
            if (room.getDoors() != null) for (Door door : room.getDoors()) {
                Room destination = door == null ? null : rooms.get(door.getLeadsTo());
                if (destination != null && door.otherDoor != null && door.otherDoor.otherDoor == door
                        && destination.getDoors() != null && destination.getDoors().contains(door.otherDoor)
                        && room.getIdentifier().equals(door.otherDoor.getLeadsTo())) connected.add(door.getLeadsTo());
            }
            connections.put(room.getIdentifier(), Collections.unmodifiableSet(connected));
        }
        if (entrance != null && rooms.containsKey(entrance)) {
            ArrayDeque<String> pending = new ArrayDeque<>();
            ranks.put(entrance, 0);
            pending.add(entrance);
            while (!pending.isEmpty()) {
                String current = pending.remove();
                for (String next : connections.get(current)) {


                    if (ranks.containsKey(next)) continue;
                    ranks.put(next, ranks.get(current) + 1);
                    pending.add(next);
                }
            }
        }
        return new RoomDisplayDepth(entrance, ranks, ordinary, appearanceSources, connections);
    }


    public int rankOf(String identifier) {
        Integer rank = ranks.get(identifier);
        return rank == null ? -1 : rank;
    }

    public boolean connected(String first, String second) {
        Set<String> from = connections.get(first), to = connections.get(second);
        return from != null && to != null && from.contains(second) && to.contains(first);
    }


    public static boolean reciprocallyConnected(Room first, Room second) {
        if (first == null || second == null || first == second) return false;
        for (Door door : first.getDoors()) if (second.getIdentifier().equals(door.getLeadsTo())
                && door.otherDoor != null && door.otherDoor.otherDoor == door
                && second.getDoors().contains(door.otherDoor) && first.getIdentifier().equals(door.otherDoor.getLeadsTo())) return true;
        return false;
    }

    public Direction direction(String previous, String current) {
        int from = rankOf(previous), to = rankOf(current);
        if (from < 0 || to < 0) return Direction.UNAVAILABLE;
        return to > from ? Direction.DEEPER : to < from ? Direction.SHALLOWER : Direction.SAME;
    }


    public boolean canShowPrevious(String current, String previous) {
        return rankOf(current) >= 0 && rankOf(previous) >= 0 && !current.equals(previous)
                && ordinaryRooms.contains(current)
                && (ordinaryRooms.contains(previous) || previous.equals(entrance));
    }


    public boolean canShowAppearance(String current, String previous) {
        return rankOf(current) >= 0 && rankOf(previous) >= 0 && !current.equals(previous)
                && ordinaryRooms.contains(current) && appearanceSources.contains(previous);
    }
}
