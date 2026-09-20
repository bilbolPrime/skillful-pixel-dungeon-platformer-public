package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.CameraWhoosh;


public final class RoomTransition {
    private static final float DURATION = 0.24f;
    private static final float ZOOM_OFFSET = 0.045f;
    private float remaining;
    private RoomDisplayDepth.Direction direction = RoomDisplayDepth.Direction.UNAVAILABLE;
    private RoomSnapshot outgoingBackdrop;
    private RoomPlaneSelection previousPlanes = RoomPlaneSelection.EMPTY;
    private float previousPrimaryOpacity = 1f, previousSecondaryOpacity = 1f, previousSecondaryDepth = 1f;
    private float previousSecondaryAnchorX = Float.NaN, previousSecondaryAnchorY = Float.NaN;
    private String destination;
    private int floor, placementVersion;
    private final CameraWhoosh whoosh = new CameraWhoosh();

    public void begin(String destination, int floor, RoomDisplayDepth.Direction direction,
                      RoomSnapshot previousBackdrop, int placementVersion) {
        begin(destination, floor, direction, previousBackdrop, RoomPlaneSelection.EMPTY, placementVersion);
    }

    public void begin(String destination, int floor, RoomDisplayDepth.Direction direction,
                      RoomSnapshot previousBackdrop, RoomPlaneSelection previousSelection, int placementVersion) {
        if (previousSelection == null) previousSelection = RoomPlaneSelection.EMPTY;

        float primaryOpacity = getBackdropBlend();
        float secondaryOpacity = getSecondaryOpacity(previousSelection.secondaryIdentifier);
        float secondaryDepth = getSecondaryDepth(previousSelection.secondaryIdentifier);
        float secondaryAnchorX = getSecondaryAnchorX(previousSelection.secondaryIdentifier, previousSelection.secondaryAnchorX);
        float secondaryAnchorY = getSecondaryAnchorY(previousSelection.secondaryIdentifier, previousSelection.secondaryAnchorY);
        clear();
        if (destination == null || direction == null || direction == RoomDisplayDepth.Direction.UNAVAILABLE
                || GameSettingsHelper.getInstance().isReducedCameraMotion()) return;
        this.destination = destination;
        this.floor = floor;
        this.placementVersion = placementVersion;
        this.direction = direction;
        outgoingBackdrop = previousBackdrop;
        previousPlanes = previousSelection;
        previousPrimaryOpacity = primaryOpacity;
        previousSecondaryOpacity = secondaryOpacity;
        previousSecondaryDepth = secondaryDepth;
        previousSecondaryAnchorX = secondaryAnchorX;
        previousSecondaryAnchorY = secondaryAnchorY;
        remaining = DURATION;
        if (direction == RoomDisplayDepth.Direction.DEEPER || direction == RoomDisplayDepth.Direction.SHALLOWER)
            whoosh.startZoom(direction == RoomDisplayDepth.Direction.DEEPER, DURATION);
    }


    public void validate(String currentRoom, int currentFloor, int placementVersion, boolean dead) {
        if (remaining <= 0f) return;
        if (dead || GameSettingsHelper.getInstance().isReducedCameraMotion()
                || floor != currentFloor || !destination.equals(currentRoom)
                || this.placementVersion != placementVersion) clear();
    }

    public void update(float delta) {
        if (remaining <= 0f) return;
        remaining = Math.max(0f, remaining - Math.max(0f, delta));
        whoosh.update(1f - remaining / DURATION);
        if (remaining == 0f) clear();
    }

    public float getBackdropBlend() {
        float t = remaining / DURATION;
        return 1f - t * t * (3f - 2f * t);
    }

    public float getZoom() {
        float offset = direction == RoomDisplayDepth.Direction.DEEPER ? ZOOM_OFFSET
                : direction == RoomDisplayDepth.Direction.SHALLOWER ? -ZOOM_OFFSET : 0f;
        return 1f + offset * (1f - getBackdropBlend());
    }

    public RoomSnapshot getOutgoingBackdrop() { return outgoingBackdrop; }

    public float getOutgoingOpacity() { return previousPrimaryOpacity * (1f - getBackdropBlend()); }


    public float getSecondaryDepth(String identifier) {
        if (remaining <= 0f || identifier == null) return 1f;
        if (outgoingBackdrop != null && identifier.equals(outgoingBackdrop.roomIdentifier)) return getBackdropBlend();
        if (identifier.equals(previousPlanes.secondaryIdentifier))
            return previousSecondaryDepth + (1f - previousSecondaryDepth) * getBackdropBlend();
        return 1f;
    }

    public float getSecondaryOpacity(String identifier) {
        if (remaining <= 0f || identifier == null) return 1f;
        float from = outgoingBackdrop != null && identifier.equals(outgoingBackdrop.roomIdentifier) ? previousPrimaryOpacity
                : identifier.equals(previousPlanes.secondaryIdentifier) ? previousSecondaryOpacity : 0f;
        return from + (1f - from) * getBackdropBlend();
    }

    public float getSecondaryAnchorX(String identifier, float anchor) {
        if (identifier == null) return anchor;
        float previous = outgoingBackdrop != null && identifier.equals(outgoingBackdrop.roomIdentifier) ? outgoingBackdrop.departureX
                : identifier.equals(previousPlanes.secondaryIdentifier) ? previousSecondaryAnchorX : Float.NaN;
        return remaining <= 0f || !Float.isFinite(previous) ? anchor : previous + (anchor - previous) * getBackdropBlend();
    }

    public float getSecondaryAnchorY(String identifier, float anchor) {
        if (identifier == null) return anchor;
        float previous = outgoingBackdrop != null && identifier.equals(outgoingBackdrop.roomIdentifier) ? outgoingBackdrop.departureY
                : identifier.equals(previousPlanes.secondaryIdentifier) ? previousSecondaryAnchorY : Float.NaN;
        return remaining <= 0f || !Float.isFinite(previous) ? anchor : previous + (anchor - previous) * getBackdropBlend();
    }

    public void clearAppearances() {
        outgoingBackdrop = null;
        previousPlanes = RoomPlaneSelection.EMPTY;
        previousPrimaryOpacity = previousSecondaryOpacity = previousSecondaryDepth = 1f;
        previousSecondaryAnchorX = previousSecondaryAnchorY = Float.NaN;
    }

    public void clear() {
        stopSound();
        remaining = 0f;
        clearAppearances();
        destination = null;
        direction = RoomDisplayDepth.Direction.UNAVAILABLE;
    }

    public void stopSound() { whoosh.stop(); }
}
