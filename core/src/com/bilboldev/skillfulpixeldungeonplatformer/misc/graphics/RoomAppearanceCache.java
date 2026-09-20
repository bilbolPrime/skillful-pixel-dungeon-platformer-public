package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;


public final class RoomAppearanceCache {
    public static final int CAPACITY = 4;
    private final RoomSnapshot[] entries = new RoomSnapshot[CAPACITY];
    private final long[] visited = new long[CAPACITY];
    private long runSeed, visitOrder;
    private int floor;
    private boolean scoped;

    public void recordDeparture(long runSeed, int floor, RoomSnapshot incoming,
                                String destination, RoomSnapshot retiring) {
        if (!scoped || this.runSeed != runSeed || this.floor != floor) {
            clear();
            this.runSeed = runSeed;
            this.floor = floor;
            scoped = true;
        }
        long departureOrder = ++visitOrder, arrivalOrder = ++visitOrder;

        int destinationSlot = find(destination);
        if (destinationSlot >= 0) visited[destinationSlot] = arrivalOrder;
        if (incoming != null && incoming.floor == floor) {
            int slot = find(incoming.roomIdentifier);
            if (slot < 0) {
                for (int i = 0; i < CAPACITY; i++) if (entries[i] == null) { slot = i; break; }
            }
            if (slot < 0) slot = oldest(null, retiring);
            entries[slot] = incoming;
            visited[slot] = departureOrder;
        }


        int allowed = CAPACITY - (retiring != null && !contains(retiring) ? 1 : 0);
        while (size() > allowed) {
            int slot = oldest(incoming, retiring);
            entries[slot] = null;
            visited[slot] = 0L;
        }
    }

    private int oldest(RoomSnapshot incoming, RoomSnapshot retiring) {
        int oldest = -1;
        for (int i = 0; i < CAPACITY; i++) {
            if (entries[i] == null || entries[i] == incoming || entries[i] == retiring) continue;
            if (oldest < 0 || visited[i] < visited[oldest]) oldest = i;
        }
        return oldest;
    }

    private int find(String identifier) {
        if (identifier == null) return -1;
        for (int i = 0; i < CAPACITY; i++) {
            if (entries[i] != null && identifier.equals(entries[i].roomIdentifier)) return i;
        }
        return -1;
    }

    public RoomSnapshot get(String identifier) {
        int slot = find(identifier);
        return slot < 0 ? null : entries[slot];
    }

    public long visitOrderOf(String identifier) {
        int slot = find(identifier);
        return slot < 0 ? -1L : visited[slot];
    }

    public RoomSnapshot at(int slot) { return slot >= 0 && slot < CAPACITY ? entries[slot] : null; }

    public boolean contains(RoomSnapshot snapshot) {
        if (snapshot == null) return false;
        for (RoomSnapshot entry : entries) if (entry == snapshot) return true;
        return false;
    }

    public int size() {
        int count = 0;
        for (RoomSnapshot entry : entries) if (entry != null) count++;
        return count;
    }

    public void clear() {
        for (int i = 0; i < CAPACITY; i++) { entries[i] = null; visited[i] = 0L; }
        scoped = false;
        visitOrder = 0L;
    }
}
