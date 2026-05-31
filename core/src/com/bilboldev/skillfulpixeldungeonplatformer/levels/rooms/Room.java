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
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Room {
    private static final long SCATTERED_ITEM_RANDOM_SALT = 0x53C471A2D91B6EF4L;
    protected String identifier;
    protected int width = 20 + RandomHelper.getInstance().randomInt(10);
    protected int height = 10;

    protected HashSet<String> platforms;
    protected HashSet<String> waterPlatforms;
    protected ArrayList<Door> doors;
    protected ArrayList<Unit> stuff;

    protected boolean canSpawn = true;
    protected boolean trapsPlaced;
    protected boolean bossEncounterStarted;
    protected boolean bossDefeated;

    protected Sign sign;

    public Room(String identifier){
        this.identifier = identifier;
        doors = new ArrayList<>();
        platforms = new HashSet<>();
        waterPlatforms = new HashSet<>();
        stuff = new ArrayList<>();
    }

    public Room build(){
        // Guaranteed platform
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

        seedWater();

        return this;
    }

    protected void seedWater() {
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

    public HashSet<String> getWaterPlatforms() {
        return waterPlatforms;
    }

    public void clearWaterPlatforms() {
        waterPlatforms.clear();
    }

    protected void addPlatformSpan(int startTileX, int endTileX, int tileY) {
        int from = Math.min(startTileX, endTileX);
        int to = Math.max(startTileX, endTileX);
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

    public Room addWaterSpan(int startTileX, int endTileX, int tileY) {
        int from = Math.min(startTileX, endTileX);
        int to = Math.max(startTileX, endTileX);
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

    public boolean connectedTo(String identifier){
        if(doors == null || doors.size() == 0){
            return false;
        }

        for (Door door : doors){
            if(door.getLeadsTo().equals(identifier)){
                return true;
            }
        }

        return false;
    }

    public Door getRandomDoor(){

        int tileX = 0;
        int tileY = 0;
        boolean doorExists = false;
        do {
            doorExists = false;
            tileX = 5 + RandomHelper.getInstance().randomInt(width);
            if (tileX > width - 5) {
                tileX = width - 5;
            }

            tileY = calculateDoorFloorY(tileX);

            for(Door door : doors){
                if(door.x == tileX * ConstantsHelper.TILE){
                    doorExists = true;
                    break;
                }
            }

            if(isSignTile(tileX, tileY)){
                doorExists = true;
            }

            if(isTrapTile(tileX, tileY)){
                doorExists = true;
            }

            for(Unit thing : stuff){
                if(thing.x == tileX * ConstantsHelper.TILE){
                    if((tileY + 1) * ConstantsHelper.TILE == thing.y){
                        doorExists = true;
                        break;
                    }
                }
            }
        }while(doorExists);

        Door door = new Door();
        door.x = tileX * ConstantsHelper.TILE;
        door.y = tileY * ConstantsHelper.TILE;

        return door;
    }

    public Door getRandomSpawn(){
        ArrayList<String> spawnCandidates = new ArrayList<String>();
        for (String platform : platforms) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int floorTileY = Integer.parseInt(platform.split("_")[1]) + 1;
            if (!spawnSpotAvailable(tileX, floorTileY)) {
                continue;
            }

            spawnCandidates.add(platform);
        }

        if (spawnCandidates.isEmpty()) {
            return null;
        }

        Collections.sort(spawnCandidates);
        String platform = spawnCandidates.get(RandomHelper.getInstance().randomInt(spawnCandidates.size()));
        int tileX = Integer.parseInt(platform.split("_")[0]);
        int floorTileY = Integer.parseInt(platform.split("_")[1]) + 1;

        Door spawn = new Door();
        spawn.x = tileX * ConstantsHelper.TILE;
        spawn.y = floorTileY * ConstantsHelper.TILE;
        return spawn;
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

        return calculateDoorFloorY(tileX) == tileY;
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

        return !isTrapTile(tileX, tileY) && calculateDoorFloorY(tileX) == tileY;
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

