package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;


public final class RoomPlaneSelection {
    public static final RoomPlaneSelection EMPTY = new RoomPlaneSelection(null, null, null, null, null);
    public final String primaryIdentifier, secondaryIdentifier;
    public final int primaryRank, secondaryRank;
    public final float primaryLinkX, primaryLinkY, secondaryAnchorX, secondaryAnchorY;

    private RoomPlaneSelection(RoomSnapshot primary, RoomSnapshot secondary, RoomSnapshot.DoorShape primaryDoor,
                               RoomSnapshot.DoorShape secondaryDoor, RoomDisplayDepth depth) {
        primaryIdentifier = primary == null ? null : primary.roomIdentifier;
        secondaryIdentifier = secondary == null ? null : secondary.roomIdentifier;
        primaryRank = primary == null ? -1 : depth.rankOf(primaryIdentifier);
        secondaryRank = secondary == null ? -1 : depth.rankOf(secondaryIdentifier);
        primaryLinkX = primaryDoor == null ? Float.NaN : primaryDoor.anchorX();
        primaryLinkY = primaryDoor == null ? Float.NaN : primaryDoor.y;
        secondaryAnchorX = secondaryDoor == null ? Float.NaN : secondaryDoor.anchorX();
        secondaryAnchorY = secondaryDoor == null ? Float.NaN : secondaryDoor.y;
    }


    public static RoomPlaneSelection select(RoomAppearanceCache cache, RoomSnapshot primary, RoomDisplayDepth depth) {
        if (primary == null || !cache.contains(primary)
                || !depth.canShowAppearance(primary.destinationIdentifier, primary.roomIdentifier)
                || !depth.connected(primary.roomIdentifier, primary.destinationIdentifier)) return EMPTY;
        RoomSnapshot selected = null;
        RoomSnapshot.DoorShape selectedNearDoor = null, selectedFarDoor = null;
        for (int slot = 0; slot < RoomAppearanceCache.CAPACITY; slot++) {
            RoomSnapshot candidate = cache.at(slot);
            if (candidate == null || candidate.floor != primary.floor
                    || candidate.roomIdentifier.equals(primary.roomIdentifier)
                    || candidate.roomIdentifier.equals(primary.destinationIdentifier)
                    || !depth.canShowAppearance(primary.destinationIdentifier, candidate.roomIdentifier)
                    || !depth.connected(primary.roomIdentifier, candidate.roomIdentifier)) continue;

            RoomSnapshot.DoorShape nearDoor = null, farDoor = null;
            for (RoomSnapshot.DoorShape near : primary.doors) {
                if (!candidate.roomIdentifier.equals(near.destinationIdentifier) || near.pairedDoorIdentifier == null) continue;
                for (RoomSnapshot.DoorShape far : candidate.doors) {
                    if (primary.roomIdentifier.equals(far.destinationIdentifier)
                            && near.pairedDoorIdentifier.equals(far.identifier)
                            && near.identifier != null && near.identifier.equals(far.pairedDoorIdentifier)) {
                        nearDoor = near; farDoor = far; break;
                    }
                }
                if (nearDoor != null) break;
            }
            if (nearDoor != null && preferred(candidate, selected, primary, depth, cache)) {
                selected = candidate; selectedNearDoor = nearDoor; selectedFarDoor = farDoor;
            }
        }
        return new RoomPlaneSelection(primary, selected, selectedNearDoor, selectedFarDoor, depth);
    }

    private static boolean preferred(RoomSnapshot candidate, RoomSnapshot selected, RoomSnapshot primary,
                                     RoomDisplayDepth depth, RoomAppearanceCache cache) {
        if (selected == null) return true;
        boolean candidateLeadsIn = candidate.destinationIdentifier.equals(primary.roomIdentifier);
        boolean selectedLeadsIn = selected.destinationIdentifier.equals(primary.roomIdentifier);
        if (candidateLeadsIn != selectedLeadsIn) return candidateLeadsIn;
        int travel = Integer.compare(depth.rankOf(primary.roomIdentifier), depth.rankOf(primary.destinationIdentifier));
        int candidateDirection = Integer.compare(depth.rankOf(candidate.roomIdentifier), depth.rankOf(primary.roomIdentifier));
        int selectedDirection = Integer.compare(depth.rankOf(selected.roomIdentifier), depth.rankOf(primary.roomIdentifier));
        if ((candidateDirection == travel) != (selectedDirection == travel)) return candidateDirection == travel;
        long candidateVisit = cache.visitOrderOf(candidate.roomIdentifier), selectedVisit = cache.visitOrderOf(selected.roomIdentifier);
        if (candidateVisit != selectedVisit) return candidateVisit > selectedVisit;
        return candidate.roomIdentifier.compareTo(selected.roomIdentifier) < 0;
    }
}
