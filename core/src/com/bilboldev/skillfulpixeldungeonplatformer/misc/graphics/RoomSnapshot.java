package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.*;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.Decoration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;


public final class RoomSnapshot {
    public static final int MAX_PLATFORMS = 128, MAX_DOORS = 16, MAX_ENEMIES = 16, MAX_ITEMS = 24;
    public static final int MAX_PROPS = 24, MAX_FOCAL_PARTS = 48;
    public enum DoorState { UNOBSERVED, CLOSED, OPEN, LOCKED, CAGED }
    public enum PropKind { FOCAL, SIGN, DECORATION, PIPE, LAMP, TORCH, CITY_LAMP, HALLS_GREEN, HALLS_RED }


    public enum SourceKind {
        ORDINARY(Room.class, false), ENTRY(EntryRoom.class, false),
        LIBRARY(LibraryRoom.class, true), GARDEN(GardenRoom.class, true), LABORATORY(LaboratoryRoom.class, true),
        MAGIC_WELL(MagicWellRoom.class, true), CRYPT(CryptRoom.class, true), GRAVEYARD(GraveyardRoom.class, true),
        POOL(PoolRoom.class, true), TREASURY(TreasuryRoom.class, true), TREASURE(TreasureRoom.class, true),
        ARMORY(ArmoryRoom.class, true), STORAGE(StorageRoom.class, true), VAULT(VaultRoom.class, true),
        TRAPS(TrapsRoom.class, true), MERCHANT(MerchantRoom.class, true), MERCENARY(MercenaryRoom.class, true),
        UNSUPPORTED(null, false);

        private static final SourceKind[] REGISTERED = values();
        private final Class<? extends Room> type;
        public final boolean special;
        SourceKind(Class<? extends Room> type, boolean special) { this.type = type; this.special = special; }
        public static SourceKind of(Room room) {
            if (room != null) for (SourceKind kind : REGISTERED) if (kind.type == room.getClass()) return kind;
            return UNSUPPORTED;
        }
    }

    public static final class Platform {
        public final int tileX, tileEndX, tileY;
        public final boolean wet;
        private Platform(int tileX, int tileEndX, int tileY, boolean wet) {
            this.tileX = tileX;
            this.tileEndX = tileEndX;
            this.tileY = tileY;
            this.wet = wet;
        }
        public float left() { return tileX * ConstantsHelper.TILE; }
        public float width() { return (tileEndX - tileX + 1) * ConstantsHelper.TILE; }
        public float top() { return (tileY + 1) * ConstantsHelper.TILE + 4f; }
    }

    public static final class DoorShape {
        public final String identifier, destinationIdentifier, pairedDoorIdentifier;
        public final float x, y, width, height;
        public final DoorState state;
        public final SpritePose pose, signPose;
        private DoorShape(Door door, String roomIdentifier) {
            identifier = door.getPersistentId();
            destinationIdentifier = door.getLeadsTo();
            pairedDoorIdentifier = door.otherDoor == null ? null : door.otherDoor.getPersistentId();
            x = door.x;
            y = door.y + 7f;
            width = door.getDisplayWidth();
            height = door.getDisplayHeight();
            pose = door.copyObservedDoor(roomIdentifier);
            signPose = door.copyObservedSign(roomIdentifier);
            state = pose == null ? DoorState.UNOBSERVED : door.isCaged() ? DoorState.CAGED
                    : door.isLocked() ? DoorState.LOCKED : door.isOpen() ? DoorState.OPEN : DoorState.CLOSED;
        }
        public float anchorX() { return x + width / 2f; }
    }

    public static final class Prop {
        public final String identifier;
        public final PropKind kind;
        public final SpritePose pose;
        public final float anchorX, anchorY, waterTargetY;
        Prop(String identifier, PropKind kind, SpritePose pose, float anchorX, float anchorY, float waterTargetY) {
            this.identifier = identifier; this.kind = kind; this.pose = pose;
            this.anchorX = anchorX; this.anchorY = anchorY; this.waterTargetY = waterTargetY;
        }
        public float area() { return pose == null ? 32f * 32f : pose.displayedArea(); }
    }

    public static final class Occupant {
        public final String identifier;
        public final SpritePose pose;
        public final float footX, footY, supportY;
        public final boolean floating, idle;
        private Occupant(Unit unit, SpritePose pose, List<Platform> platforms) {
            identifier = unit.getPersistentId();
            this.pose = pose;

            footX = pose.bottomCenterX();
            footY = pose.bottomCenterY();
            floating = unit.isCanFly() || unit.isLevitating();
            idle = unit instanceof Mob && ((Mob) unit).hasObservedIdlePose(unit.getRoom());
            float support = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE;
            float error = Math.abs(footY - support);
            for (Platform platform : platforms) {
                float difference = Math.abs(footY - platform.top());
                if (footX >= platform.left() && footX <= platform.left() + platform.width() && difference < error) {
                    error = difference;
                    support = platform.top();
                }
            }

            supportY = !floating && error <= 12f ? support : Float.NaN;
        }
    }

    public final String roomIdentifier, destinationIdentifier, departureDoorIdentifier, arrivalDoorIdentifier;
    public final int floor;
    public final SourceKind sourceKind;
    public final RoomLayout.Description layout;
    public final int waterSurfaceThickness;
    public final float widthTiles, heightTiles, departureX, departureY, arrivalX, arrivalY;
    public final RoomDisplayDepth.Direction direction;
    public final List<Platform> platforms;
    public final List<DoorShape> doors;
    public final List<Occupant> items, enemies;
    public final List<Prop> props;
    public final int observedPropArtCount;

    private RoomSnapshot(Room outgoing, Door departure, int floor, RoomDisplayDepth.Direction direction) {
        sourceKind = SourceKind.of(outgoing);
        layout = outgoing.getLayout().copy();
        waterSurfaceThickness = outgoing.getWaterSurfaceThickness();
        roomIdentifier = outgoing.getIdentifier();
        destinationIdentifier = departure.getLeadsTo();
        departureDoorIdentifier = departure.getPersistentId();
        arrivalDoorIdentifier = departure.otherDoor.getPersistentId();
        departureX = departure.x + departure.getDisplayWidth() / 2f;
        departureY = departure.y + 7f;

        arrivalX = departure.otherDoor.x + departure.otherDoor.getDisplayWidth() / 2f;
        arrivalY = departure.otherDoor.y + 7f;
        this.floor = floor;
        this.direction = direction;
        widthTiles = outgoing.getWidth();
        heightTiles = outgoing.getHeight();
        List<Platform> tiles = new ArrayList<>();
        java.util.Set<String> visibleSurfaces = new java.util.HashSet<>(outgoing.getPlatforms());
        visibleSurfaces.addAll(outgoing.getWaterPlatforms());
        for (String key : visibleSurfaces) {
            int separator = key.indexOf('_');
            int x = Integer.parseInt(key.substring(0, separator));
            tiles.add(new Platform(x, x, Integer.parseInt(key.substring(separator + 1)),
                    outgoing.getWaterPlatforms().contains(key)));
        }
        tiles.sort(Comparator.comparingInt((Platform p) -> p.tileY).thenComparingInt(p -> p.tileX));
        List<Platform> spans = new ArrayList<>();
        for (Platform tile : tiles) {
            Platform previous = spans.isEmpty() ? null : spans.get(spans.size() - 1);
            if (previous != null && previous.tileY == tile.tileY && previous.tileEndX + 1 == tile.tileX
                    && previous.wet == tile.wet) {
                spans.set(spans.size() - 1, new Platform(previous.tileX, tile.tileX, tile.tileY, tile.wet));
            } else spans.add(tile);
        }

        spans.sort(Comparator.comparingDouble((Platform p) -> distanceToSpan(p, departureX, departureY))
                .thenComparingInt(p -> p.tileY).thenComparingInt(p -> p.tileX));
        platforms = bounded(spans, MAX_PLATFORMS);

        List<DoorShape> copiedDoors = new ArrayList<>();
        for (Door door : outgoing.getDoors()) copiedDoors.add(new DoorShape(door, roomIdentifier));
        copiedDoors.sort(Comparator.comparingInt((DoorShape d) -> sameId(d.identifier, departureDoorIdentifier) ? 0 : 1)
                .thenComparingDouble(d -> distance(d.anchorX(), d.y, departureX, departureY))
                .thenComparing(d -> String.valueOf(d.identifier))
                .thenComparing(d -> String.valueOf(d.destinationIdentifier)));
        doors = bounded(copiedDoors, MAX_DOORS);

        List<Occupant> copiedItems = new ArrayList<>(), copiedEnemies = new ArrayList<>();
        List<Prop> copiedProps = MapHelper.getInstance().getRoomFixtureObservation().copyFor(roomIdentifier);
        if (outgoing.getSign() != null) {
            SpritePose sign = outgoing.getSign().copyObserved(roomIdentifier, MapHelper.getInstance().getTheme().getSign());
            if (sign != null) copiedProps.add(new Prop(roomIdentifier + ":sign", PropKind.SIGN, sign,
                    sign.bottomCenterX(), sign.bottomCenterY(), Float.NaN));
        }
        Set<Unit> seen = Collections.newSetFromMap(new IdentityHashMap<Unit, Boolean>());
        List<Unit> candidates = UnitHelper.getInstance().getUnitsSnapshot();
        candidates.addAll(outgoing.getStuff());
        for (Unit unit : candidates) {
            if (!seen.add(unit) || !roomIdentifier.equals(unit.getRoom()) || !unit.isVisible() || unit.isInvisible()
                    || unit.getPersistentId() == null || unit.isHero || unit.isDead()) continue;
            if (unit instanceof ItemOnScreen) {
                ItemOnScreen item = (ItemOnScreen) unit;
                SpritePose pose = item.isPlaced() ? item.getLastDisplayedItem(roomIdentifier) : null;
                if (pose != null && pose.alpha > 0f) copiedItems.add(new Occupant(unit, pose, platforms));
            } else if (unit instanceof Mob && !unit.isFriendly && !unit.showOnly() && !unit.isInvisible()) {
                SpritePose pose = ((Mob) unit).getLastObservedBody(roomIdentifier);
                if (pose != null && pose.alpha > 0f) copiedEnemies.add(new Occupant(unit, pose, platforms));
            } else if (unit instanceof Decoration) {
                SpritePose pose = ((Decoration) unit).copyObservedDecoration(roomIdentifier);
                if (pose != null && pose.alpha > 0f) copiedProps.add(new Prop(unit.getPersistentId(), PropKind.DECORATION,
                        pose, pose.bottomCenterX(), pose.bottomCenterY(), Float.NaN));
            } else if (unit instanceof Interactable) {
                SpritePose pose = ((Interactable) unit).copyObservedBody(roomIdentifier);
                if (pose != null && pose.alpha > 0f) copiedProps.add(new Prop(unit.getPersistentId(), PropKind.DECORATION,
                        pose, pose.bottomCenterX(), pose.bottomCenterY(), Float.NaN));
            }
        }
        items = selectOccupants(copiedItems, MAX_ITEMS);
        enemies = selectOccupants(copiedEnemies, MAX_ENEMIES);
        copiedProps.sort(Comparator.comparingDouble((Prop p) -> -p.area()).thenComparing(p -> p.identifier));
        List<Prop> uniqueProps = new ArrayList<>();
        Set<String> propIdentifiers = new HashSet<>();
        for (Prop prop : copiedProps) {
            if (propIdentifiers.add(prop.identifier)) uniqueProps.add(prop);
            if (uniqueProps.size() == MAX_PROPS) break;
        }
        props = Collections.unmodifiableList(uniqueProps);
        int artCount = items.size();
        for (Prop prop : props) if (prop.pose != null) artCount++;
        for (DoorShape door : doors) {
            if (door.pose != null) artCount++;
            if (door.signPose != null) artCount++;
        }
        observedPropArtCount = artCount;
    }

    private static List<Occupant> selectOccupants(List<Occupant> candidates, int maximum) {
        candidates.sort(Comparator.comparingDouble((Occupant o) -> -o.pose.displayedArea())
                .thenComparing(o -> o.identifier));
        Set<String> identifiers = new HashSet<>();
        List<Occupant> selected = new ArrayList<>();
        for (Occupant candidate : candidates) {
            if (identifiers.add(candidate.identifier)) selected.add(candidate);
            if (selected.size() == maximum) break;
        }
        return Collections.unmodifiableList(selected);
    }

    private static <T> List<T> bounded(List<T> records, int maximum) {
        return Collections.unmodifiableList(new ArrayList<>(records.subList(0, Math.min(records.size(), maximum))));
    }

    private static boolean sameId(String first, String second) { return first != null && first.equals(second); }
    private static double distance(float x, float y, float anchorX, float anchorY) {
        double dx = x - anchorX, dy = y - anchorY;
        return dx * dx + dy * dy;
    }
    private static double distanceToSpan(Platform span, float x, float y) {
        return distance(Math.max(span.left(), Math.min(x, span.left() + span.width())), span.top(), x, y);
    }


    public int platformTileCount() {
        int count = 0;
        for (Platform platform : platforms) count += platform.tileEndX - platform.tileX + 1;
        return count;
    }



    public static RoomSnapshot capture(Room outgoing, Door departure, int floor, RoomDisplayDepth depth) {
        if (!hasUsableBounds(outgoing) || outgoing != MapHelper.getInstance().getActiveRoom() || departure == null || depth == null
                || outgoing.getDoors() == null || !outgoing.getDoors().contains(departure) || departure.otherDoor == null
                || !Float.isFinite(departure.x) || !Float.isFinite(departure.y)
                || !Float.isFinite(departure.otherDoor.x) || !Float.isFinite(departure.otherDoor.y)
                || !depth.canShowAppearance(departure.getLeadsTo(), outgoing.getIdentifier())
                || !depth.connected(outgoing.getIdentifier(), departure.getLeadsTo())) return null;
        return new RoomSnapshot(outgoing, departure, floor, depth.direction(outgoing.getIdentifier(), departure.getLeadsTo()));
    }

    public static boolean hasUsableBounds(Room room) {
        return room != null && room.getWidth() > 0f && room.getHeight() > ConstantsHelper.MIN_FLOOR
                && Float.isFinite(room.getWidth() * ConstantsHelper.TILE) && Float.isFinite(room.getHeight() * ConstantsHelper.TILE);
    }


    public boolean isValidFor(Room source, Room current) {
        if (!matchesSource(source) || !hasUsableBounds(current) || current.getClass() != Room.class
                || roomIdentifier == null || destinationIdentifier == null || departureDoorIdentifier == null || arrivalDoorIdentifier == null
                || !roomIdentifier.equals(source.getIdentifier()) || !destinationIdentifier.equals(current.getIdentifier())
                || sourceKind != SourceKind.of(source) || sourceKind == SourceKind.UNSUPPORTED
                || widthTiles != source.getWidth() || heightTiles != source.getHeight()
                || !layout.family.equals(source.getLayout().family())
                || source.getDoors() == null || current.getDoors() == null) return false;
        for (int i = 0; i < source.getDoors().size(); i++) {
            Door door = source.getDoors().get(i);
            if (door == null || !departureDoorIdentifier.equals(door.getPersistentId())) continue;
            Door paired = door.otherDoor;
            return paired != null && paired.otherDoor == door && current.getDoors().contains(paired)
                    && arrivalDoorIdentifier.equals(paired.getPersistentId()) && destinationIdentifier.equals(door.getLeadsTo())
                    && roomIdentifier.equals(paired.getLeadsTo())
                    && departureX == door.x + door.getDisplayWidth() / 2f && departureY == door.y + 7f
                    && arrivalX == paired.x + paired.getDisplayWidth() / 2f && arrivalY == paired.y + 7f
                    && Float.isFinite(door.x) && Float.isFinite(door.y) && Float.isFinite(paired.x) && Float.isFinite(paired.y);
        }
        return false;
    }

    public boolean matchesSource(Room source) {
        if (!hasUsableBounds(source) || !roomIdentifier.equals(source.getIdentifier())
                || sourceKind != SourceKind.of(source) || widthTiles != source.getWidth() || heightTiles != source.getHeight()
                || !layout.family.equals(source.getLayout().family()) || source.getDoors().size() != doors.size()) return false;
        for (DoorShape copied : doors) {
            boolean found = false;
            for (Door live : source.getDoors()) if (sameId(copied.identifier, live.getPersistentId())) {
                found = copied.x == live.x && copied.y == live.y + 7f
                        && java.util.Objects.equals(copied.destinationIdentifier, live.getLeadsTo())
                        && java.util.Objects.equals(copied.pairedDoorIdentifier, live.otherDoor == null ? null : live.otherDoor.getPersistentId());
                break;
            }
            if (!found) return false;
        }
        return true;
    }
}
