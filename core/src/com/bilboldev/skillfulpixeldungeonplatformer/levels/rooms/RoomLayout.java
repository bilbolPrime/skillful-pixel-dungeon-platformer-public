package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class RoomLayout {
    public static final int GENERATOR_VERSION = 2;
    public enum Role { PLAYABLE, FOREGROUND, DISTANT }
    public enum Purpose { ORDINARY, ENTRANCE, EXIT, SPECIAL, BOSS }
    public static final class Anchor {
        public final float x, y;
        public Anchor(float x, float y) { this.x = x; this.y = y; }
    }
    public static final class Part {
        public final Role role;
        public final float x, y, width, height;
        public final boolean wet;
        public final boolean pier;
        Part(Role role, float x, float y, float width, float height, boolean wet) {
            this(role, x, y, width, height, wet, false);
        }
        private Part(Role role, float x, float y, float width, float height, boolean wet, boolean pier) {
            this.role = role; this.x = x; this.y = y; this.width = width; this.height = height; this.wet = wet;
            this.pier = pier;
        }
    }

    public static final class Description {
        public final String family;
        public final Purpose purpose;
        public final float width, height;
        public final Anchor feature;
        public final List<Anchor> arrivals, fixtures, standing;
        public final List<Part> parts;
        private Description(RoomLayout plan) {
            family = plan.family; purpose = plan.purpose(); width = plan.room.getWidth(); height = plan.room.getHeight();
            feature = plan.feature(); arrivals = immutable(plan.arrivals()); fixtures = immutable(plan.fixtureAnchors());
            standing = immutable(plan.standing()); parts = immutable(plan.parts());
        }
    }
    private static final float T = ConstantsHelper.TILE;
    private final Room room;
    private String family;
    private Anchor feature;
    private List<Part> piers;
    private List<Anchor> fixtures;
    private boolean piersEnabled;

    public void enablePiers(boolean enabled) { piersEnabled = enabled; piers = null; fixtures = null; }
    private final List<Anchor> plannedArrivals = new ArrayList<>();

    RoomLayout(Room room) { this.room = room; family = room.getClass().getSimpleName(); }
    public String family() { return family; }
    public Purpose purpose() {
        return room.isBossArena() ? Purpose.BOSS : room instanceof EntryRoom ? Purpose.ENTRANCE
                : room instanceof ExitRoom ? Purpose.EXIT : room.getClass() == Room.class ? Purpose.ORDINARY : Purpose.SPECIAL;
    }
    public void describe(String family, int featureX, int featureFloor) {
        if (family == null || family.isEmpty()) throw new IllegalArgumentException("Missing room family");
        this.family = family; feature = new Anchor(featureX * T, featureFloor * T);
        piers = null; fixtures = null;
    }
    public Anchor feature() { return feature == null ? new Anchor(room.getWidth() * T / 2f, ConstantsHelper.MIN_FLOOR * T) : feature; }
    public boolean hasPlannedFeature() { return feature != null; }
    public void reserveArrival(int tileX, int floor) { plannedArrivals.add(new Anchor(tileX * T, floor * T)); }
    public List<Anchor> plannedArrivals() { return Collections.unmodifiableList(plannedArrivals); }
    public List<Anchor> arrivals() {
        List<Anchor> result = new ArrayList<>(plannedArrivals);
        for (Door door : room.getDoors()) result.add(new Anchor(door.x, door.y));
        return result;
    }

    public boolean arrivalReserved(float x, float floorY, float width) {
        for (Anchor anchor : plannedArrivals)
            if (overlapsArrival(anchor.x, anchor.y, x, floorY, width)) return true;
        for (Door door : room.getDoors())
            if (overlapsArrival(door.x, door.y, x, floorY, width)) return true;
        return false;
    }
    private boolean overlapsArrival(float dx, float dy, float x, float y, float width) {
        return Math.abs(dy - y) < T && x + width > dx - T && x < dx + 2f * T;
    }

    public boolean hasMountedFixtures() { return !room.isBossArena() && (piersEnabled || feature != null); }
    public List<Anchor> fixtureAnchors() {
        if (fixtures != null) return fixtures;
        List<Anchor> result = new ArrayList<>();
        if (!piers().isEmpty()) {
            for (Part pier : piers()) result.add(new Anchor(pier.x + pier.width / 2f, pier.y + 1.25f * T));
        } else if (hasMountedFixtures()) {

            for (int side : new int[]{-1, 1}) result.add(new Anchor(
                    Math.max(T, Math.min((room.getWidth() - 1) * T, feature.x + side * 1.75f * T)),
                    Math.min((room.getHeight() - 1) * T, feature.y + 1.25f * T)));
        } else {
            int offset = room.getIdentifier() == null ? 0 : room.getIdentifier().hashCode() & 3;
            for (int tile = 2 + offset; tile < room.getWidth() - 4; tile += 6)
                result.add(new Anchor((tile + 25.5f / 16f) * T, (ConstantsHelper.MIN_FLOOR + 29f / 16f) * T));
        }
        fixtures = Collections.unmodifiableList(result);
        return fixtures;
    }

    private List<Anchor> standing() {
        List<Anchor> result = new ArrayList<>();
        float inset = (T - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        for (int floor = ConstantsHelper.MIN_FLOOR; floor < room.getHeight(); floor++)
            for (int x = 0; x < room.getWidth(); x++)
                if (room.hasSupportedPlacement(x * T + inset, floor * T, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS))
                    result.add(new Anchor((x + .5f) * T, floor * T));
        return result;
    }
    private List<Part> parts() {
        List<Part> result = new ArrayList<>();

        result.add(new Part(Role.PLAYABLE, 0, 0, room.getWidth() * T, ConstantsHelper.MIN_FLOOR * T, false));
        java.util.Set<String> surfaces = new java.util.HashSet<>(room.getPlatforms());
        surfaces.addAll(room.getWaterPlatforms());
        List<String> cells = new ArrayList<>(surfaces);
        Collections.sort(cells);
        for (String cell : cells) {
            String[] xy = cell.split("_");
            result.add(new Part(Role.PLAYABLE, Integer.parseInt(xy[0]) * T, Integer.parseInt(xy[1]) * T,
                    T, T, room.getWaterPlatforms().contains(cell)));
        }
        result.add(new Part(Role.FOREGROUND, -T, 0, T, room.getHeight() * T, false));
        result.add(new Part(Role.FOREGROUND, room.getWidth() * T, 0, T, room.getHeight() * T, false));
        result.add(new Part(Role.FOREGROUND, 0, room.getHeight() * T, room.getWidth() * T, T, false));

        result.add(new Part(Role.DISTANT, 0, ConstantsHelper.MIN_FLOOR * T,
                room.getWidth() * T, (room.getHeight() - ConstantsHelper.MIN_FLOOR) * T, false));
        result.addAll(piers());
        return result;
    }

    public List<Part> piers() {
        if (piers == null) {
            List<Part> result = new ArrayList<>();
            if (piersEnabled && purpose() == Purpose.ORDINARY) {
                int left = family.contains("terrace") ? 9 : family.contains("court") ? 6 : 7;
                int right = (int) room.getWidth() - (family.contains("court") ? 7 : 6);
                for (int column : new int[]{left, right})
                    result.add(new Part(Role.FOREGROUND, (column + .1875f) * T, ConstantsHelper.MIN_FLOOR * T,
                            .625f * T, (room.getHeight() - ConstantsHelper.MIN_FLOOR) * T, false, true));
            }
            piers = immutable(result);
        }
        return piers;
    }
    public Description copy() { return new Description(this); }


    public String signature() {
        List<String> records = new ArrayList<>();
        records.add("family:" + family + ":" + room.getWidth() + ":" + room.getHeight());
        records.add("piers:" + piersEnabled);
        records.add("feature:" + feature().x + ":" + feature().y);
        for (String cell : room.getPlatforms()) records.add("floor:" + cell);
        for (String cell : room.getWaterPlatforms()) records.add("water:" + cell);
        for (Anchor anchor : plannedArrivals) records.add("arrival:" + anchor.x + ":" + anchor.y);
        for (Door door : room.getDoors()) records.add("door:" + door.getPersistentId() + ":" + door.x + ":" + door.y
                + ":" + door.getLeadsTo() + ":" + (door.otherDoor == null ? "terminal" : door.otherDoor.getPersistentId()));
        Collections.sort(records);
        long hash = 0xcbf29ce484222325L;
        for (String record : records) for (int i = 0; i <= record.length(); i++)
            hash = (hash ^ (i == record.length() ? 0 : record.charAt(i))) * 0x100000001b3L;
        return Long.toHexString(hash);
    }
    private static <T> List<T> immutable(List<T> values) { return Collections.unmodifiableList(values); }
}
