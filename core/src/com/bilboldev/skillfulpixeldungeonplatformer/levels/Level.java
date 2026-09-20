package com.bilboldev.skillfulpixeldungeonplatformer.levels;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Key;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.PotionOfStrength;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.ArmoryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CryptRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.EntryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.ExitRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.GardenRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.GraveyardRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.LaboratoryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.LibraryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MagicWellRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MercenaryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MerchantRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.PoolRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomRoutes;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.StorageRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.TrapsRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.TreasuryRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.TreasureRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.VaultRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelEntryDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelExitDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Level {
    private static final long GUARANTEED_STRENGTH_POTION_RANDOM_SALT = 0x3B5C2D719A4E8F61L;
    private static final long LOCKED_ROOM_REWARD_RANDOM_SALT = 0x4C7D11F2A8B5673EL;
    private static final int[] RANDOM_SPECIAL_ROOMS = new int[]{
            ConstantsHelper.ROOM_LIBRARY,
            ConstantsHelper.ROOM_TREASURE,
            ConstantsHelper.ROOM_ARMORY,
            ConstantsHelper.ROOM_GARDEN,
            ConstantsHelper.ROOM_LABORATORY,
            ConstantsHelper.ROOM_MAGIC_WELL,
            ConstantsHelper.ROOM_CRYPT,
            ConstantsHelper.ROOM_POOL,
            ConstantsHelper.ROOM_TREASURY,
            ConstantsHelper.ROOM_TRAPS,
            ConstantsHelper.ROOM_STORAGE,
            ConstantsHelper.ROOM_VAULT,
                ConstantsHelper.ROOM_GRAVEYARD
    };

    public ArrayList<Room> rooms;
    protected Room atRoom, exitRoom;
    protected EntryRoom entryRoom;
    protected Door entryDoor;

    protected HashMap<Integer, ArrayList<Class<? extends Unit>>> units;
    protected int perRoom = 0;
    protected int depth = 1;
    protected boolean spawnStateFrozen;
    protected boolean initialPopulationSpawning;

    protected int getTargetMobsPerRoom() {
        if (perRoom <= 0) {
            return 0;
        }

        return Math.max(1, (int) Math.ceil(perRoom / 2f)) * 2;
    }

    public Level(){
        rooms = new ArrayList<>();
    }

    public Level generateLevel(int depth){
        this.depth = depth;
        int rooms = 5 + RandomHelper.getInstance().randomInt(3);
        String identifier = RandomHelper.getInstance().uniqueId();
        String identifierExit = RandomHelper.getInstance().uniqueId();

        entryRoom = new EntryRoom(identifier).buildFoyer(depth);
        setSignMessage(depth);

        entryDoor = entryRoom.getLevelEntryDoor();
        exitRoom = new ExitRoom(identifierExit).buildFoyer(depth);


        for(int i = 0; i < rooms; i++){
            identifier = RandomHelper.getInstance().uniqueId();
            Room room = new Room(identifier).planForFloor(depth, i).build();
            if (!allowsGeneratedRoomWater()) room.clearWaterPlatforms();
            this.rooms.add(room);
        }



        RoomConnections.connectOrdinaryRooms(this.rooms);


        Room chosenRoom = this.rooms.get(RandomHelper.getInstance().randomInt(this.rooms.size()));
        if (allowsGeneratedRoomWater()) {
            chosenRoom.ensureWater();
        }
        RoomConnections.connect(chosenRoom, entryRoom);



        chosenRoom = this.rooms.get(RandomHelper.getInstance().randomInt(this.rooms.size()));
        RoomConnections.connect(chosenRoom, exitRoom);


        addGeneratedSpecialRoom(chooseSpecialRoom(depth));


        addRoom(entryRoom);
        addRoom(exitRoom);

        for (Room room : this.rooms) RoomRoutes.ensureTraversable(room);
        Door doorExit = exitRoom.getRandomDoor().toExitDoor();
        exitRoom.addDoor(doorExit);

        RoomConnections.validateConnected(this.rooms, entryRoom, exitRoom);

        for (Room room : this.rooms) {
            room.populate();
            room.placeTrapsIfNeeded();
        }

        initialPopulationSpawning = true;
        try {
            for (Room room : this.rooms) if (room.getCanSpawn())
                for (int j = 0; j < getTargetMobsPerRoom(); j++) spawnUnit(room);
        } finally { initialPopulationSpawning = false; }

        if (!MapHelper.getInstance().isRestoringGeneratedLevels()) {
            placeLockedDoorRewards(depth);

            for (Room room : this.rooms) {
                if (isLockedRewardRoom(room)) {
                    continue;
                }
                room.placeScatteredItemIfNeeded(depth);
            }

            placeGuaranteedStrengthPotionIfNeeded(depth);
        }

        RoomConnections.validateConnected(this.rooms, entryRoom, exitRoom);
        for (Room room : this.rooms) RoomRoutes.validateContent(room);

        return this;
    }

    protected boolean allowsGeneratedRoomWater() {
        return false;
    }

    private void placeGuaranteedStrengthPotionIfNeeded(int depth) {
        if (!shouldPlaceGuaranteedStrengthPotion(depth) || rooms.isEmpty()) {
            return;
        }

        RandomXS128 levelRandom = new RandomXS128(
                RandomHelper.getInstance().levelSeed(depth) ^ GUARANTEED_STRENGTH_POTION_RANDOM_SALT,
                RandomHelper.getInstance().levelSeed(depth + 2000) ^ Long.rotateLeft(GUARANTEED_STRENGTH_POTION_RANDOM_SALT, 17)
        );

        int startIndex = levelRandom.nextInt(rooms.size());
        for (int offset = 0; offset < rooms.size(); offset++) {
            Room room = rooms.get((startIndex + offset) % rooms.size());
            if (room.placeSpecificScatteredItem(new PotionOfStrength(), depth, GUARANTEED_STRENGTH_POTION_RANDOM_SALT)) {
                return;
            }
        }
        throw new IllegalStateException("No supported strength-potion slot on floor " + depth);
    }

    private boolean shouldPlaceGuaranteedStrengthPotion(int depth) {
        return depth > 0 && depth % 2 == 0;
    }

    private void placeLockedDoorRewards(int depth) {
        for (Room room : rooms) {
            if (!isLockedRewardCandidate(room)) {
                continue;
            }

            Door lockedDoor = room.getDoors().get(0);
            Item reward = InventoryHelper.getInstance().getRandomTreasure(depth);
            long rewardSalt = LOCKED_ROOM_REWARD_RANDOM_SALT ^ room.getIdentifier().hashCode();
            if (!room.placeSpecificScatteredItem(reward, depth, rewardSalt)) {
                continue;
            }

            if (!spawnKeyOutsideRoom(room)) {
                throw new IllegalStateException("No accessible key slot outside " + room.getIdentifier());
            }

            lockedDoor.lockWithKey();
            if (lockedDoor.otherDoor != null) {
                lockedDoor.otherDoor.lockWithKey();
            }

            return;
        }
    }

    private boolean isLockedRewardCandidate(Room room) {
        return room != null
                && room.getClass() == Room.class
                && room.getDoors().size() == 1;
    }

    private boolean isLockedRewardRoom(Room room) {
        return isLockedRewardCandidate(room) && room.getDoors().get(0).requiresKey();
    }

    private boolean spawnKeyOutsideRoom(Room lockedRoom) {
        ArrayList<Room> candidateRooms = new ArrayList<>();
        Set<Room> reachable = RoomConnections.reachableWithoutLocks(rooms, entryRoom, lockedRoom);

        for (Room room : rooms) {
            if (room == null || room == lockedRoom || !reachable.contains(room)) {
                continue;
            }

            if (room.getIdentifier() != null && room.getIdentifier().equals(lockedRoom.getIdentifier())) {
                continue;
            }

            candidateRooms.add(room);
        }

        while (!candidateRooms.isEmpty()) {
            Room room = candidateRooms.remove(RandomHelper.getInstance().randomInt(candidateRooms.size()));

            if (spawnItemInRoom(room, new Key())) {
                return true;
            }
        }

        return false;
    }

    private boolean spawnItemInRoom(Room room, Item item) {
        if (room == null || item == null) {
            return false;
        }

        Door spawn = room.getRandomSpawn();
        if (spawn == null) {
            return false;
        }

        item.spawnNaturally(spawn.x, spawn.y, spawn.y, room.getIdentifier());
        return true;
    }

    protected void addGeneratedSpecialRoom(int specialRoom) {
        if (rooms == null || rooms.isEmpty()) {
            return;
        }

        Room room = rooms.get(RandomHelper.getInstance().randomInt(rooms.size()));
        String identifier = RandomHelper.getInstance().uniqueId();
        Room specialRoomToAdd = createSpecialRoom(specialRoom, identifier);
        if (specialRoomToAdd == null) {
            return;
        }

        Door specialRoomDoor = createMarkedSpecialDoor(room.getRandomDoor(), specialRoom);
        Door roomDoor = specialRoomToAdd.getRandomDoor();
        RoomConnections.connect(room, specialRoomDoor, specialRoomToAdd, roomDoor);
        addRoom(specialRoomToAdd);
    }

    private Room createSpecialRoom(int specialRoom, String identifier) {
        switch (specialRoom) {
            case ConstantsHelper.ROOM_LIBRARY:
                return new LibraryRoom(identifier).build();
            case ConstantsHelper.ROOM_TREASURE:
                return new TreasureRoom(identifier).build();
            case ConstantsHelper.ROOM_MERCHANT:
                return new MerchantRoom(identifier).build();
            case ConstantsHelper.ROOM_ARMORY:
                return new ArmoryRoom(identifier).build();
            case ConstantsHelper.ROOM_GARDEN:
                return new GardenRoom(identifier).build();
            case ConstantsHelper.ROOM_LABORATORY:
                return new LaboratoryRoom(identifier).build();
            case ConstantsHelper.ROOM_MAGIC_WELL:
                return new MagicWellRoom(identifier).build();
            case ConstantsHelper.ROOM_CRYPT:
                return new CryptRoom(identifier).build();
            case ConstantsHelper.ROOM_POOL:
                return new PoolRoom(identifier).build();
            case ConstantsHelper.ROOM_TREASURY:
                return new TreasuryRoom(identifier).build();
            case ConstantsHelper.ROOM_TRAPS:
                return new TrapsRoom(identifier).build();
            case ConstantsHelper.ROOM_STORAGE:
                return new StorageRoom(identifier).build();
            case ConstantsHelper.ROOM_VAULT:
                return new VaultRoom(identifier).build();
            case ConstantsHelper.ROOM_GRAVEYARD:
                return new GraveyardRoom(identifier).build();
            case ConstantsHelper.ROOM_MERCENARY:
                return new MercenaryRoom(identifier).build();
            default:
                return null;
        }
    }

    private Door createMarkedSpecialDoor(Door baseDoor, int specialRoom) {
        switch (specialRoom) {
            case ConstantsHelper.ROOM_LIBRARY:
                return baseDoor.toLibraryDoor();
            case ConstantsHelper.ROOM_TREASURE:
                return baseDoor.toTreasureDoor();
            case ConstantsHelper.ROOM_MERCHANT:
                return baseDoor.toMerchantDoor();
            case ConstantsHelper.ROOM_ARMORY:
                return baseDoor.toArmoryDoor();
            case ConstantsHelper.ROOM_GARDEN:
                return baseDoor.toGardenDoor();
            case ConstantsHelper.ROOM_LABORATORY:
                return baseDoor.toLaboratoryDoor();
            case ConstantsHelper.ROOM_MAGIC_WELL:
                return baseDoor.toMagicWellDoor();
            case ConstantsHelper.ROOM_CRYPT:
                return baseDoor.toCryptDoor();
            case ConstantsHelper.ROOM_POOL:
                return baseDoor.toPoolDoor();
            case ConstantsHelper.ROOM_TREASURY:
                return baseDoor.toTreasuryDoor();
            case ConstantsHelper.ROOM_TRAPS:
                return baseDoor.toTrapsDoor();
            case ConstantsHelper.ROOM_STORAGE:
                return baseDoor.toStorageDoor();
            case ConstantsHelper.ROOM_VAULT:
                return baseDoor.toVaultDoor();
            case ConstantsHelper.ROOM_GRAVEYARD:
                return baseDoor.toGraveyardDoor();
            case ConstantsHelper.ROOM_MERCENARY:
                return baseDoor.toMercenaryDoor();
            default:
                return baseDoor;
        }
    }

    protected int chooseSpecialRoom(int depth) {
        if (depth % 5 == 1) {
            return ConstantsHelper.ROOM_MERCHANT;
        }

        if (depth % 5 == 2) {
            return ConstantsHelper.ROOM_MERCENARY;
        }

        return RANDOM_SPECIAL_ROOMS[RandomHelper.getInstance().randomInt(RANDOM_SPECIAL_ROOMS.length)];
    }

    protected void setSignMessage(int depth){

    }

    protected void addRoom(Room room){
        if(room instanceof EntryRoom){
            atRoom = room;
        }
        rooms.add(room);
    }

    protected void setAtRoom(Room room){
        this.atRoom = room;
    }

    protected void spawnUnit(Room room){
        if(units == null || units.size() == 0 || !units.containsKey(depth)){
            return;
        }

        try
        {
            Class<? extends Unit> toSpawn = units.get(depth).get(RandomHelper.getInstance().randomInt(units.get(depth).size()));
            spawnUnit(toSpawn, room);
        }
        catch (Exception e){
            if (initialPopulationSpawning) throw new IllegalStateException("Could not populate " + room.getIdentifier(), e);
        }
    }

    protected boolean spawnUnit(Class<? extends Unit> toSpawn, Room room) {
        if (toSpawn == null || room == null) {
            return false;
        }

        try {
            Unit unit = toSpawn.getDeclaredConstructor().newInstance();
            unit.setRoom(room.getIdentifier());
            if (unit instanceof Mob) {
                maybePromoteToChampion((Mob) unit, room);
            }

            ArrayList<String> candidates = room.getSpawnCandidates();
            while (!candidates.isEmpty()) {
                String[] position = candidates.remove(RandomHelper.getInstance().randomInt(candidates.size())).split("_");
                unit.x = Integer.parseInt(position[0]) * ConstantsHelper.TILE;
                unit.y = Integer.parseInt(position[1]) * ConstantsHelper.TILE;
                unit.floorY = unit.y;
                if (room.hasSupportedPlacement(unit.x, unit.floorY,
                        ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS)
                        && isSpawnSpaceFree(unit, room.getIdentifier())) {
                    UnitHelper.getInstance().addUnit(unit);
                    return true;
                }
            }
        }
        catch (Exception failure) {
            if (initialPopulationSpawning) throw new IllegalStateException("Could not place " + toSpawn.getSimpleName()
                    + " in " + room.getIdentifier(), failure);
        }

        if (initialPopulationSpawning) throw new IllegalStateException("No free supported slot for "
                + toSpawn.getSimpleName() + " in " + room.getIdentifier());
        return false;
    }

    private void maybePromoteToChampion(Mob mob, Room room) {
        if (!initialPopulationSpawning || mob == null || room == null || mob.isBoss() || mob.isFriendly) {
            return;
        }

        String roomIdentifier = room.getIdentifier();
        int championChance = DifficultyHelper.getInstance().getCurrentDifficulty().getChampionChancePercent();

        if (depth <= 2) {
            championChance /= 2;
        }
        if (roomIdentifier == null || roomHasChampion(roomIdentifier) || !RandomHelper.getInstance().randomChance(championChance)) {
            return;
        }

        mob.promoteToRandomChampion();
    }

    private boolean roomHasChampion(String roomIdentifier) {
        if (roomIdentifier == null) {
            return false;
        }

        for (Unit existing : UnitHelper.getInstance().getUnits()) {
            if (existing instanceof Mob
                    && roomIdentifier.equals(existing.getRoom())
                    && ((Mob) existing).isChampion()) {
                return true;
            }
        }

        return false;
    }

    private boolean isSpawnSpaceFree(Unit candidate, String roomIdentifier) {
        if (candidate == null || roomIdentifier == null) {
            return false;
        }

        Rectangle candidateHitBox = candidate.getHitBox();
        for (Unit existing : UnitHelper.getInstance().getUnits()) {
            if (existing == null || existing.showOnly()) {
                continue;
            }

            if (existing.getRoom() == null || !existing.getRoom().equals(roomIdentifier)) {
                continue;
            }

            if (candidateHitBox.overlaps(existing.getHitBox())) {
                return false;
            }
        }

        return true;
    }

    public int getDepth(){
        return depth;
    }

    public Room getAtRoom(){
        return atRoom;
    }

    public boolean goToRoom(String identifier) {
        for (Room room : rooms) {
            if (room.getIdentifier().equals(identifier)) {
                setAtRoom(room);
                return true;
            }
        }

        return false;
    }

    public Door getEntryDoor(){
        return entryDoor;
    }

    public Door getEntryPoint(boolean goingDown){
        for(Door door : getAtRoom().getDoors()){
            if(goingDown && door instanceof LevelEntryDoor || !goingDown && door instanceof LevelExitDoor){
                return door;
            }
        }

        return getAtRoom().getDoors().get(getAtRoom().getDoors().size() - 1);
    }

    public boolean enterDoor(Door door){
        if(door.otherDoor == null || door.getLeadsTo() == null){
            return false;
        }

        if (door.isLocked()) {
            if (!door.requiresKey() || !MapHelper.getInstance().consumeKeyForCurrentDepth()) {
                return false;
            }

            door.unlock();
            if (door.otherDoor != null) {
                door.otherDoor.unlock();
            }
        }

        for(Room room : rooms){
            if(room.getIdentifier().equals(door.getLeadsTo())){
                setAtRoom(room);
                SoundHelper.GetSingleton().play(Sounds.OPEN_DOOR);
                door.open();
                if(door.otherDoor != null){
                    door.otherDoor.open();
                }
                onRoomEntered(room);
                return true;
            }
        }

        return false;
    }

    public void goToEntry(){
        setAtRoom(entryRoom);
        onRoomEntered(entryRoom);
    }

    public void goToExit() {
        setAtRoom(exitRoom);
        onRoomEntered(exitRoom);
    }

    public void act(float delta){
        if (spawnStateFrozen) {
            return;
        }
    }

    private void onRoomEntered(Room room) {
        if (room == null) {
            return;
        }

        room.entered();
        replenishRoomOnEntry(room);
    }

    private void replenishRoomOnEntry(Room room) {
        if (spawnStateFrozen || room == null || !room.getCanSpawn()) {
            return;
        }

        int targetMobs = getTargetMobsPerRoom();
        int currentMobs = countSpawnableEnemies(room.getIdentifier());
        int missingMobs = targetMobs - currentMobs;
        if (missingMobs <= 0) {
            return;
        }

        int mobsToSpawn = Math.max(1, (missingMobs + 1) / 2);
        for (int spawned = 0; spawned < mobsToSpawn; spawned++) {
            spawnUnit(room);
        }
    }

    private int countSpawnableEnemies(String roomIdentifier) {
        int foundUnits = 0;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit.spawnSpace() && !unit.isFriendly && roomIdentifier.equals(unit.getRoom())) {
                foundUnits++;
            }
        }

        return foundUnits;
    }

    public Room getEntryRoom(){
        return entryRoom;
    }

    public void freezeSpawns() {
        spawnStateFrozen = true;
    }
}

