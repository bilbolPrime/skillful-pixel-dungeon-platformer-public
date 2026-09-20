package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.badlogic.gdx.math.RandomXS128;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Sign;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Room {
    public static final int MAX_CORPSES = 12;
    private static final long SCATTERED_ITEM_RANDOM_SALT = 0x53C471A2D91B6EF4L;
    protected String identifier;
    protected int width = 20 + RandomHelper.getInstance().randomInt(10);
    protected int height = 10;

    protected HashSet<String> platforms;
    protected HashSet<String> waterPlatforms;
    protected ArrayList<Door> doors;
    protected ArrayList<Unit> stuff;


    private ArrayList<CorpseRecord> corpses;
    private List<CorpseRecord> corpseView;
    private long nextCorpseOrder;
    private long corpseRestoreVersion;

    protected boolean canSpawn = true;
    protected boolean trapsPlaced;
    protected boolean bossEncounterStarted;
    protected boolean bossDefeated;

    protected Sign sign;
    private RoomLayout layout;
    private int generationDepth = 1, generationOrdinal;
    private boolean contentsPlaced;


    public final void populate() {
        if (contentsPlaced) return;
        placeContents();
        contentsPlaced = true;
    }

    protected void placeContents() { }

    public Room planForFloor(int depth, int ordinal) {
        generationDepth = depth; generationOrdinal = ordinal;
        return this;
    }

    public RoomLayout getLayout() {
        if (layout == null) layout = new RoomLayout(this);
        return layout;
    }

    public Room(String identifier){
        this.identifier = identifier;
        doors = new ArrayList<>();
        platforms = new HashSet<>();
        waterPlatforms = new HashSet<>();
        stuff = new ArrayList<>();
    }

    public Room build(){
        if (getClass() == Room.class) {
            RoomFamilies.build(this, generationDepth, generationOrdinal);
            seedWater();
            return this;
        }


        int tileX = RandomHelper.getInstance().randomInt(width);
        int platformWidth = 3 + RandomHelper.getInstance().randomInt(width / 5);

        for(int i = tileX; i < tileX + platformWidth; i++){
            platforms.add(UtilsHelper.platformKey(i, 4));
        }

        int bonusForPlat = 0;
        for(int i = 4; i < height - 2; i += 2){
            for(int j = 0; j < width; j ++){
                if(i > 5 && platforms.contains(UtilsHelper.platformKey(j, i - 2)) && RandomHelper.getInstance().randomChance( 75)){
                    for(int k = j; k < j +  3 + RandomHelper.getInstance().randomInt(width / 5); k++){
                        platforms.add(UtilsHelper.platformKey(k, i));
                    }

                    j += 10;
                }

                if(i == 4 && RandomHelper.getInstance().randomChance( 35 + bonusForPlat)){
                    for(int k = j; k < j +  3 + RandomHelper.getInstance().randomInt(width / 5); k++){
                        platforms.add(UtilsHelper.platformKey(k, i));
                    }
                    j += 10;
                    bonusForPlat = 0;
                }
                else {
                    bonusForPlat += 20;
                }

                if(i == 4){
                    j += 3;
                }
            }
        }

        RoomGeometry.normalize(this);
        seedWater();

        return this;
    }

    protected void seedWater() {
        if (generationDepth >= 1 && generationDepth <= 4) return;
        if (getClass() != Room.class || platforms.isEmpty() || !RandomHelper.getInstance().randomChance(70)) {
            return;
        }

        addRandomWaterPlatforms(2 + RandomHelper.getInstance().randomInt(2));
    }

    public void ensureWater() {
        if (getClass() != Room.class || platforms.isEmpty() || !waterPlatforms.isEmpty()) {
            return;
        }

        addRandomWaterPlatforms(1 + RandomHelper.getInstance().randomInt(2));
    }

    private void addRandomWaterPlatforms(int platformCount) {
        ArrayList<String> waterCandidates = new ArrayList<String>(platforms);
        Collections.sort(waterCandidates);

        for (int puddle = 0; puddle < platformCount && !waterCandidates.isEmpty(); puddle++) {
            String candidate = waterCandidates.remove(RandomHelper.getInstance().randomInt(waterCandidates.size()));
            String[] candidateParts = candidate.split("_");
            int tileX = Integer.parseInt(candidateParts[0]);
            int tileY = Integer.parseInt(candidateParts[1]);

            while (platforms.contains(UtilsHelper.platformKey(tileX - 1, tileY))) {
                tileX--;
            }

            for (int currentTileX = tileX; ; currentTileX++) {
                String platformKey = UtilsHelper.platformKey(currentTileX, tileY);
                if (!platforms.contains(platformKey)) {
                    break;
                }

                waterPlatforms.add(platformKey);
                waterCandidates.remove(platformKey);
            }
        }
    }

    public void placeTrapsIfNeeded() {
        if (trapsPlaced || !shouldPlaceTraps()) {
            return;
        }

        placeTraps();
        trapsPlaced = true;
    }

    protected void placeTraps() {
        ArrayList<String> trapCandidates = new ArrayList<String>();
        for (String platform : platforms) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int floorTileY = Integer.parseInt(platform.split("_")[1]) + 1;
            if (!trapSpotAvailable(tileX, floorTileY)) {
                continue;
            }

            trapCandidates.add(platform);
        }

        int trapCount = Math.min(6, trapCandidates.size());
        Collections.sort(trapCandidates);
        int hiddenTrapIndex = trapCount > 0 ? RandomHelper.getInstance().randomInt(trapCount) : -1;
        for (int i = 0; i < trapCount; i++) {
            String platform = trapCandidates.remove(RandomHelper.getInstance().randomInt(trapCandidates.size()));
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);
            float floorY = (tileY + 1) * ConstantsHelper.TILE;

            PlatformTrap trap = new PlatformTrap()
                    .setTrapType(TrapType.randomType())
                    .setHidden(i == hiddenTrapIndex);
            trap.x = tileX * ConstantsHelper.TILE + (ConstantsHelper.TILE - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
            trap.y = floorY - ConstantsHelper.UNIT_DIMENSIONS / 3f + ConstantsHelper.UNIT_DIMENSIONS * 0.25f;
            trap.floorY = floorY;
            trap.setRoom(identifier);
            UnitHelper.getInstance().addUnit(trap);
        }
    }

    protected boolean shouldPlaceTraps() {
        return canSpawn && getClass() == Room.class;
    }

    public void placeScatteredItemIfNeeded(int depth) {
        if (!shouldPlaceScatteredItem()) {
            return;
        }

        RandomXS128 roomRandom = RandomHelper.getInstance().createRoomRandom(depth, identifier, SCATTERED_ITEM_RANDOM_SALT);
        for (int roll = 0; roll < 2; roll++) {
            if (roomRandom.nextInt(100) >= 25) {
                continue;
            }

            placeSpecificScatteredItem(InventoryHelper.getInstance().getRandomRoomScatterItem(depth, roomRandom), depth, roomRandom);
        }
    }

    public boolean placeSpecificScatteredItem(Item item, int depth, long salt) {
        RandomXS128 roomRandom = RandomHelper.getInstance().createRoomRandom(depth, identifier, salt);
        return placeSpecificScatteredItem(item, depth, roomRandom);
    }

    private boolean placeSpecificScatteredItem(Item item, int depth, RandomXS128 roomRandom) {
        if (!shouldPlaceScatteredItem() || item == null) {
            return false;
        }

        ArrayList<String> itemCandidates = getScatterItemCandidates();

        if (itemCandidates.isEmpty()) {
            return false;
        }

        Collections.sort(itemCandidates);
        String platform = itemCandidates.get(roomRandom.nextInt(itemCandidates.size()));
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int floorTileY = Integer.parseInt(platform.split("_")[1]) + 1;
        float floorY = floorTileY * ConstantsHelper.TILE;

        if (item instanceof Gold) {
            item.setQuantity(Math.max(1, item.getQuantity()) * Math.max(1, depth));
        }

        item.spawnNaturally(tileX * ConstantsHelper.TILE, floorY, floorY, identifier);
        return true;
    }

    private ArrayList<String> getScatterItemCandidates() {
        ArrayList<String> itemCandidates = new ArrayList<String>();
        for (String platform : platforms) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int floorTileY = Integer.parseInt(platform.split("_")[1]) + 1;
            if (!scatterItemSpotAvailable(tileX, floorTileY)) {
                continue;
            }

            itemCandidates.add(platform);
        }

        return itemCandidates;
    }

    protected boolean shouldPlaceScatteredItem() {
        return canSpawn && getClass() == Room.class;
    }

    private boolean trapSpotAvailable(int tileX, int floorTileY) {
        if (getLayout().arrivalReserved(tileX * ConstantsHelper.TILE, floorTileY * ConstantsHelper.TILE,
                ConstantsHelper.UNIT_DIMENSIONS)) return false;
        if (!hasSupportedPlacement(tileX * ConstantsHelper.TILE, floorTileY * ConstantsHelper.TILE,
                ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS)) return false;
        for (Door door : doors) {
            if ((int) (door.x / ConstantsHelper.TILE) == tileX && (int) (door.y / ConstantsHelper.TILE) == floorTileY) {
                return false;
            }
        }

        if (isSignTile(tileX, floorTileY) || isTrapTile(tileX, floorTileY)) {
            return false;
        }

        for (Unit thing : stuff) {
            if ((int) (thing.x / ConstantsHelper.TILE) == tileX && (int) (thing.y / ConstantsHelper.TILE) == floorTileY) {
                return false;
            }
        }

        return true;
    }

    private boolean scatterItemSpotAvailable(int tileX, int floorTileY) {
        return !getLayout().arrivalReserved(tileX * ConstantsHelper.TILE, floorTileY * ConstantsHelper.TILE,
                ConstantsHelper.UNIT_DIMENSIONS) && placementSpotAvailable(tileX, floorTileY);
    }

    private boolean placementSpotAvailable(int tileX, int floorTileY) {
        if (!hasSupportedPlacement(tileX * ConstantsHelper.TILE, floorTileY * ConstantsHelper.TILE,
                ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS)) return false;
        if (isSignTile(tileX, floorTileY) || isTrapTile(tileX, floorTileY)) {
            return false;
        }

        for (Door door : doors) {
            if ((int) (door.x / ConstantsHelper.TILE) == tileX && (int) (door.y / ConstantsHelper.TILE) == floorTileY) {
                return false;
            }
        }

        for (Unit thing : stuff) {
            if ((int) (thing.x / ConstantsHelper.TILE) == tileX && (int) (thing.y / ConstantsHelper.TILE) == floorTileY) {
                return false;
            }
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit.getRoom() == null || !unit.getRoom().equals(identifier)) {
                continue;
            }

            if ((int) (unit.x / ConstantsHelper.TILE) == tileX && (int) (unit.floorY / ConstantsHelper.TILE) == floorTileY) {
                return false;
            }
        }

        return true;
    }

    public HashSet<String> getPlatforms(){
        return platforms;
    }

    public int getHighestStandingFloor() {
        int highest = ConstantsHelper.MIN_FLOOR;
        for (String cell : platforms) highest = Math.max(highest, Integer.parseInt(cell.substring(cell.indexOf('_') + 1)) + 1);
        return highest;
    }

    public HashSet<String> getWaterPlatforms() {
        return waterPlatforms;
    }

    public int getWaterSurfaceThickness() {
        return getClass() == Room.class && generationDepth >= 1 && generationDepth <= 4 ? 18 : 10;
    }

    public void clearWaterPlatforms() {
        waterPlatforms.clear();
    }

    protected void addPlatformSpan(int startTileX, int endTileX, int tileY) {
        int from = Math.min(startTileX, endTileX);
        int to = Math.max(startTileX, endTileX);
        if (!isBossArena()) {
            from = Math.max(0, from);
            to = Math.min(width - 1, to);
            if (tileY < ConstantsHelper.MIN_FLOOR - 1 || tileY >= height - 1 || from > to) return;
        }
        for (int tileX = from; tileX <= to; tileX++) {
            platforms.add(UtilsHelper.platformKey(tileX, tileY));
        }

        if (touchesWaterSpan(from, to, tileY)) {
            addWaterSpan(from, to, tileY);
        }
    }

    private boolean touchesWaterSpan(int fromTileX, int toTileX, int tileY) {
        for (int tileX = fromTileX - 1; tileX <= toTileX + 1; tileX++) {
            if (waterPlatforms.contains(UtilsHelper.platformKey(tileX, tileY))) {
                return true;
            }
        }

        return false;
    }

    public Room addWaterPlatform(int tileX, int tileY) {
        if (!isBossArena() && !RoomGeometry.validPlatform(this, tileX, tileY)) return this;
        String platformKey = UtilsHelper.platformKey(tileX, tileY);
        if (!platforms.contains(platformKey)) {
            return this;
        }

        int startTileX = tileX;
        while (platforms.contains(UtilsHelper.platformKey(startTileX - 1, tileY))) {
            startTileX--;
        }

        for (int currentTileX = startTileX; ; currentTileX++) {
            String currentPlatformKey = UtilsHelper.platformKey(currentTileX, tileY);
            if (!platforms.contains(currentPlatformKey)) {
                break;
            }

            waterPlatforms.add(currentPlatformKey);
        }

        return this;
    }


    public Room addGroundWater() {
        for (int x = 0; x < width; x++)
            waterPlatforms.add(UtilsHelper.platformKey(x, ConstantsHelper.MIN_FLOOR - 1));
        return this;
    }

    public Room addWaterSpan(int startTileX, int endTileX, int tileY) {
        int from = Math.min(startTileX, endTileX);
        int to = Math.max(startTileX, endTileX);
        if (!isBossArena()) {
            from = Math.max(0, from);
            to = Math.min(width - 1, to);
            if (tileY < ConstantsHelper.MIN_FLOOR - 1 || tileY >= height - 1 || from > to) return this;
        }
        for (int tileX = from; tileX <= to; tileX++) {
            addWaterPlatform(tileX, tileY);
        }

        return this;
    }

    public boolean hasWaterAt(float worldX, float floorY) {
        int tileX = (int) (worldX / ConstantsHelper.TILE);
        int tileY = Math.max(0, (int) (floorY / ConstantsHelper.TILE) - 1);
        return waterPlatforms.contains(UtilsHelper.platformKey(tileX, tileY));
    }

    public void addDoor(Door door){
        this.doors.add(door);
        applyDoorPrecedence(door);
    }

    public ArrayList<Door> getDoors(){
        return doors;
    }

    public boolean hasBossEncounterStarted() {
        return bossEncounterStarted;
    }

    public void setBossEncounterStarted(boolean bossEncounterStarted) {
        this.bossEncounterStarted = bossEncounterStarted;
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }

    public void setBossDefeated(boolean bossDefeated) {
        this.bossDefeated = bossDefeated;
    }

    public void markBossEncounterStarted() {
        bossEncounterStarted = true;
    }

    public void markBossDefeated() {
        bossEncounterStarted = true;
        bossDefeated = true;
    }

    public String getIdentifier(){
        return identifier;
    }

    public List<CorpseRecord> getCorpses() {
        return corpseView == null ? Collections.<CorpseRecord>emptyList() : corpseView;
    }

    public long getNextCorpseOrder() { return nextCorpseOrder; }
    long getCorpseRestoreVersion() { return corpseRestoreVersion; }


    public void clearCorpses() {
        if (corpses != null) corpses.clear();
        corpses = null;
        corpseView = null;
        nextCorpseOrder = 0L;
        corpseRestoreVersion++;
    }


    public void restoreCorpses(List<CorpseRecord> saved, Long savedNextOrder) {
        clearCorpses();
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.getHeroClass() != HeroClass.NECROMANCER || hero.isDead()) return;
        if (saved != null && !saved.isEmpty()) {
            HashSet<String> seen = new HashSet<>();
            for (CorpseRecord record : saved) {
                if (record == null || record.victimId == null || record.victimId.isEmpty()
                        || !identifier.equals(record.roomId) || record.order < 0L || record.order >= Long.MAX_VALUE - 1
                        || !Float.isFinite(record.deathX) || !Float.isFinite(record.deathY)
                        || !Float.isFinite(record.x) || !Float.isFinite(record.y) || record.y > record.deathY
                        || record.x != record.deathX + ConstantsHelper.UNIT_DIMENSIONS / 2f
                        || corpseSupportBelow(record.x, record.y) != record.y || !seen.add(record.victimId)) continue;
                if (corpses == null) {
                    corpses = new ArrayList<>();
                    corpseView = Collections.unmodifiableList(corpses);
                }
                corpses.add(record);
                while (corpses.size() > MAX_CORPSES) corpses.remove(oldestCorpseIndex());
                nextCorpseOrder = Math.max(nextCorpseOrder, record.order + 1L);
            }
        }
        if (savedNextOrder != null && savedNextOrder >= 0L && savedNextOrder < Long.MAX_VALUE)
            nextCorpseOrder = Math.max(nextCorpseOrder, savedNextOrder);
    }

    private int oldestCorpseIndex() {
        int oldest = 0;
        for (int index = 1; index < corpses.size(); index++) {
            CorpseRecord candidate = corpses.get(index), current = corpses.get(oldest);
            if (candidate.order < current.order || (candidate.order == current.order
                    && candidate.victimId.compareTo(current.victimId) < 0)) oldest = index;
        }
        return oldest;
    }


    boolean claimCorpse(String victimId, long order) {
        if (corpses == null) return false;
        for (int i = 0; i < corpses.size(); i++) {
            CorpseRecord record = corpses.get(i);
            if (record.order == order && record.victimId.equals(victimId)) {
                corpses.remove(i);


                for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
                    if (unit.isDead() && identifier.equals(unit.getRoom()) && victimId.equals(unit.getPersistentId()))
                        UnitHelper.getInstance().removeUnit(unit);
                }
                return true;
            }
        }
        return false;
    }


    public boolean recordCorpseDeath(String victimId, float deathX, float deathY) {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.getHeroClass() != HeroClass.NECROMANCER || hero.isDead()
                || victimId == null || victimId.isEmpty() || identifier == null
                || !Float.isFinite(deathX) || !Float.isFinite(deathY)) {
            return false;
        }
        float centerX = deathX + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float supportY = corpseSupportBelow(centerX, deathY);
        if (Float.isNaN(supportY)) return false;
        if (corpses != null) {
            for (CorpseRecord corpse : corpses) {
                if (corpse.victimId.equals(victimId)) return false;
            }
        } else {
            corpses = new ArrayList<>();
            corpseView = Collections.unmodifiableList(corpses);
        }
        while (corpses.size() >= MAX_CORPSES) {
            corpses.remove(oldestCorpseIndex());
        }
        corpses.add(new CorpseRecord(victimId, identifier, deathX, deathY, centerX, supportY, nextCorpseOrder++));
        return true;
    }


    public float corpseSupportBelow(float centerX, float deathY) {
        return corpseSupportBelow(centerX, deathY, CorpseRecord.HALF_WIDTH);
    }


    float corpseSupportBelow(float centerX, float deathY, float halfWidth) {
        if (!Float.isFinite(centerX) || !Float.isFinite(deathY)
                || !Float.isFinite(halfWidth) || halfWidth <= 0f
                || centerX - halfWidth < 4f
                || centerX + halfWidth > width * ConstantsHelper.TILE - 4f) return Float.NaN;

        int leftTile = (int)Math.floor((centerX - halfWidth) / ConstantsHelper.TILE);
        int rightTile = (int)Math.floor((centerX + halfWidth - 0.01f) / ConstantsHelper.TILE);
        int highest = Math.min(height, (int)Math.floor(deathY / ConstantsHelper.TILE));
        for (int topTile = highest; topTile >= ConstantsHelper.MIN_FLOOR; topTile--) {
            if (topTile == ConstantsHelper.MIN_FLOOR) return topTile * ConstantsHelper.TILE;
            boolean supported = true;
            for (int tile = leftTile; tile <= rightTile; tile++) {
                if (!platforms.contains(UtilsHelper.platformKey(tile, topTile - 1))) { supported = false; break; }
            }
            if (supported) return topTile * ConstantsHelper.TILE;
        }
        return Float.NaN;
    }

    public boolean connectedTo(String identifier){
        if(doors == null || doors.size() == 0){
            return false;
        }

        for (Door door : doors){
            if(door != null && identifier != null && identifier.equals(door.getLeadsTo())){
                return true;
            }
        }

        return false;
    }

    public Door getRandomDoor(){
        ArrayList<String> candidates = getDoorCandidates();
        if (candidates.isEmpty()) throw new IllegalStateException("No supported door slot in room " + identifier);
        if (getClass() == Room.class) candidates = chooseDoorHeight(candidates);
        String[] position = candidates.get(RandomHelper.getInstance().randomInt(candidates.size())).split("_");
        Door door = new Door();
        door.x = Integer.parseInt(position[0]) * ConstantsHelper.TILE;
        door.y = Integer.parseInt(position[1]) * ConstantsHelper.TILE;
        return door;
    }


    protected ArrayList<String> getDoorCandidates() {
        if (getClass() == Room.class) return getLayeredDoorCandidates();
        ArrayList<String> planned = new ArrayList<>();
        for (RoomLayout.Anchor anchor : getLayout().plannedArrivals()) {
            boolean occupied = false;
            for (Door door : doors) if (Math.abs(door.x - anchor.x) < 2 * ConstantsHelper.TILE) occupied = true;
            int tile = (int)(anchor.x / ConstantsHelper.TILE), floor = (int)(anchor.y / ConstantsHelper.TILE);
            if (!occupied && hasSupportedPlacement(anchor.x, anchor.y, ConstantsHelper.TILE, ConstantsHelper.TILE + 7f)
                    && placementSpotAvailable(tile, floor)) planned.add(UtilsHelper.platformKey(tile, floor));
        }
        if (!planned.isEmpty()) return planned;
        ArrayList<String> interior = new ArrayList<>(), edges = new ArrayList<>();
        for (int tileX = 1; tileX < width - 1; tileX++) {
            boolean occupiedColumn = false;
            for (Door door : doors) if ((int) (door.x / ConstantsHelper.TILE) == tileX) occupiedColumn = true;
            if (occupiedColumn) continue;
            for (int floor = ConstantsHelper.MIN_FLOOR; floor < height; floor++) {
                if (!hasSupportedPlacement(tileX * ConstantsHelper.TILE, floor * ConstantsHelper.TILE,
                        ConstantsHelper.TILE, ConstantsHelper.TILE + 7f)
                        || !placementSpotAvailable(tileX, floor)) continue;
                (tileX >= 5 && tileX <= width - 5 ? interior : edges).add(UtilsHelper.platformKey(tileX, floor));
            }
        }
        return interior.isEmpty() ? edges : interior;
    }

    private ArrayList<String> getLayeredDoorCandidates() {
        ArrayList<String> candidates = new ArrayList<>();
        float tile = ConstantsHelper.TILE;
        for (int floor = ConstantsHelper.MIN_FLOOR; floor < height; floor++) {
            for (int column = 2; column < width - 2; column++) {
                float x = column * tile, y = floor * tile;

                if (!hasSupportedPlacement(x - tile, y, 3 * tile, tile + 7f)
                        || !placementSpotAvailable(column, floor)) continue;
                boolean close = false;
                for (Door door : doors) if (Math.abs(door.x - x) < 3 * tile && Math.abs(door.y - y) < 2 * tile) {
                    close = true; break;
                }
                if (!close) candidates.add(UtilsHelper.platformKey(column, floor));
            }
        }
        return candidates;
    }

    private ArrayList<String> chooseDoorHeight(ArrayList<String> candidates) {
        java.util.TreeMap<Integer, ArrayList<String>> tiers = new java.util.TreeMap<>();
        for (String cell : candidates) {
            int floor = Integer.parseInt(cell.split("_")[1]);
            if (!tiers.containsKey(floor)) tiers.put(floor, new ArrayList<String>());
            tiers.get(floor).add(cell);
        }
        int leastUsed = Integer.MAX_VALUE;
        ArrayList<Integer> preferred = new ArrayList<>();
        for (int floor : tiers.keySet()) {
            int count = 0;
            for (Door door : doors) if (Math.round(door.y / ConstantsHelper.TILE) == floor) count++;
            if (count < leastUsed) { leastUsed = count; preferred.clear(); }
            if (count == leastUsed) preferred.add(floor);
        }

        return tiers.get(preferred.get(RandomHelper.getInstance().randomInt(preferred.size())));
    }

    public boolean isBossArena() {
        return this instanceof GooRoom || this instanceof TenguRoom || this instanceof DM300Room
                || this instanceof KingRoom || this instanceof YogRoom;
    }

    public boolean hasSupportedPlacement(float x, float floorY, float bodyWidth, float bodyHeight) {
        return RoomGeometry.supports(this, x, floorY, bodyWidth, bodyHeight);
    }

    protected void requireSupportedPlacement(int tileX, int floorTileY, float bodyWidth, float bodyHeight) {
        if (!hasSupportedPlacement(tileX * ConstantsHelper.TILE, floorTileY * ConstantsHelper.TILE,
                bodyWidth, bodyHeight)) {
            throw new IllegalStateException("Unsupported placement in " + identifier + " at " + tileX + "," + floorTileY);
        }
    }

    protected void requireContentPlacement(int tileX, int floorTileY, float bodyWidth, float bodyHeight) {
        requireSupportedPlacement(tileX, floorTileY, bodyWidth, bodyHeight);
        if (getLayout().arrivalReserved(tileX * ConstantsHelper.TILE, floorTileY * ConstantsHelper.TILE, bodyWidth))
            throw new IllegalStateException("Content overlaps arrival in " + identifier + " at " + tileX + "," + floorTileY);
    }

    public Door getRandomSpawn(){
        ArrayList<String> spawnCandidates = getSpawnCandidates();
        if (spawnCandidates.isEmpty()) return null;
        String[] position = spawnCandidates.get(RandomHelper.getInstance().randomInt(spawnCandidates.size())).split("_");
        Door spawn = new Door();
        spawn.x = Integer.parseInt(position[0]) * ConstantsHelper.TILE;
        spawn.y = Integer.parseInt(position[1]) * ConstantsHelper.TILE;
        return spawn;
    }


    public ArrayList<String> getSpawnCandidates() {
        ArrayList<String> spawnCandidates = new ArrayList<String>();
        for (String platform : platforms) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int floorTileY = Integer.parseInt(platform.split("_")[1]) + 1;
            if (!spawnSpotAvailable(tileX, floorTileY)) {
                continue;
            }

            spawnCandidates.add(UtilsHelper.platformKey(tileX, floorTileY));
        }

        if (spawnCandidates.isEmpty()) for (int column = 1; column < width - 1; column++)
            if (spawnSpotAvailable(column, ConstantsHelper.MIN_FLOOR))
                spawnCandidates.add(UtilsHelper.platformKey(column, ConstantsHelper.MIN_FLOOR));
        Collections.sort(spawnCandidates);
        return spawnCandidates;
    }

    private boolean spawnSpotAvailable(int tileX, int floorTileY) {
        return scatterItemSpotAvailable(tileX, floorTileY);
    }

    public boolean spotAvailable(float x, float y){

        int tileX = 0;
        int tileY = 0;

        boolean somethingExists = false;

        tileX =  (int) (x / ConstantsHelper.TILE);
        tileY =  (int) (y / ConstantsHelper.TILE);

        for(Door door : doors){
            if(door.x == tileX * ConstantsHelper.TILE && door.y == tileY * ConstantsHelper.TILE){
                somethingExists = true;
                break;
            }
        }

        for(Unit thing : stuff){
            if(thing.x == tileX * ConstantsHelper.TILE && thing.y == tileY * ConstantsHelper.TILE){
                somethingExists = true;
                break;
            }
        }

        if(isSignTile(tileX, tileY)){
            somethingExists = true;
        }

        if(isTrapTile(tileX, tileY)){
            somethingExists = true;
        }

        if(somethingExists){
            return false;
        }

        return hasSupportedPlacement(x, y, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
    }

    public int calculateDoorFloorY(int doorX){
        int calculatedTile = doorX;


        int candidateFloor = 3;

        for(String platform : platforms){
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);

            if(tileX == calculatedTile){
                candidateFloor = Math.max(candidateFloor, tileY + 1);
                if(RandomHelper.getInstance().randomBoolean()){
                    break;
                }
            }
        }

        return candidateFloor;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

    public boolean getCanSpawn(){
        return canSpawn;
    }

    public void setCanSpawn(Boolean canSpawn){
        this.canSpawn = canSpawn;
    }

    protected boolean isSignTile(int tileX, int tileY){
        return sign != null
                && (int)(sign.getX() / ConstantsHelper.TILE) == tileX
                && (int)(sign.getY() / ConstantsHelper.TILE) == tileY;
    }

    protected boolean isTrapTile(int tileX, int tileY) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof PlatformTrap) || unit.getRoom() == null || !unit.getRoom().equals(identifier)) {
                continue;
            }

            if ((int) (unit.x / ConstantsHelper.TILE) == tileX && (int) (unit.floorY / ConstantsHelper.TILE) == tileY) {
                return true;
            }
        }

        return false;
    }

    private void applyDoorPrecedence(Door door) {
        int tileX = (int) (door.x / ConstantsHelper.TILE);
        int tileY = (int) (door.y / ConstantsHelper.TILE);

        removeTrapAt(tileX, tileY);

        if (isSignTile(tileX, tileY)) {
            relocateSign(tileX, tileY);
        }
    }

    private void removeTrapAt(int tileX, int tileY) {
        ArrayList<Unit> units = new ArrayList<Unit>(UnitHelper.getInstance().getUnits());
        for (Unit unit : units) {
            if (!(unit instanceof PlatformTrap) || unit.getRoom() == null || !unit.getRoom().equals(identifier)) {
                continue;
            }

            if ((int) (unit.x / ConstantsHelper.TILE) == tileX && (int) (unit.floorY / ConstantsHelper.TILE) == tileY) {
                UnitHelper.getInstance().removeUnit(unit);
            }
        }
    }

    private void relocateSign(int blockedTileX, int tileY) {
        if (sign == null) {
            return;
        }

        int[] offsets = new int[]{1, -1, 2, -2, 3, -3};
        for (int offset : offsets) {
            int candidateX = blockedTileX + offset;
            if (candidateX < 0 || candidateX >= width) {
                continue;
            }

            if (canPlaceSignAt(candidateX, tileY)) {
                sign = new Sign(sign.getMessage(), candidateX * ConstantsHelper.TILE, tileY * ConstantsHelper.TILE);
                return;
            }
        }

        sign = null;
    }

    private boolean canPlaceSignAt(int tileX, int tileY) {
        for (Door door : doors) {
            if ((int) (door.x / ConstantsHelper.TILE) == tileX && (int) (door.y / ConstantsHelper.TILE) == tileY) {
                return false;
            }
        }

        for (Unit thing : stuff) {
            if ((int) (thing.x / ConstantsHelper.TILE) == tileX && (int) (thing.y / ConstantsHelper.TILE) == tileY) {
                return false;
            }
        }

        return !isTrapTile(tileX, tileY) && hasSupportedPlacement(tileX * ConstantsHelper.TILE,
                tileY * ConstantsHelper.TILE, ConstantsHelper.TILE, ConstantsHelper.TILE);
    }

    public Sign getSign(){
        return sign;
    }

    public ArrayList<Unit> getStuff(){
        return stuff;
    }

    public void setSign(Sign sign){
        this.sign = sign;
    }

    public void entered(){

    }
}

