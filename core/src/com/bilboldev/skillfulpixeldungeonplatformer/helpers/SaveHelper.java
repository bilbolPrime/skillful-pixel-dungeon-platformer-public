package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.BirthdaySuit;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.Pickaxe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Gemstone;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MerchantRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseRecord;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.MercenaryDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableGraveProp;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.other.Statue;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Swarm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.SummonedGhost;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.DeferredDamage;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.ActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.TrapType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Locale;

public class SaveHelper {
    private Map<String, RoomSaveData> restoringRoomManifest;
    public static final int RUN_SLOT_COUNT = 9;
    private Hero saveOwner;
    private int activeSlot = -1;
    private String activeRunId;
    private RunSaveData activeMetadata;
    private double playedSeconds;
    private String steamAccount;
    private long slotRevision;

    public void useSteamAccount(String account) {
        if (account == null || !account.matches("[0-9]{1,20}")) throw new IllegalArgumentException("Invalid Steam account");
        steamAccount = account;
    }
    public FileHandle desktopSaveRoot() { return Gdx.files.local(steamAccount == null ? "saves" : "saves/steam_" + steamAccount); }
    public String activeRunId() { return activeRunId; }
    public int activeSlot() { return activeSlot; }
    public RunSaveData activeMetadata() { return activeMetadata; }
    public double uncheckpointedSeconds() { return playedSeconds - (activeMetadata == null ? 0 : activeMetadata.playedSeconds); }
    public long slotRevision() { return slotRevision; }
    public void advancePlayTime(float delta) {
        if (saveOwner != null && !saveOwner.isDead() && Float.isFinite(delta) && delta > 0) playedSeconds += Math.min(.1f, delta);
    }


    public void detachRun() { saveOwner = null; activeSlot = -1; activeRunId = null; activeMetadata = null; playedSeconds = 0; }

    public void attachRun(Hero hero, int slot, RunSaveData saved) {
        if (slot < -1 || slot >= RUN_SLOT_COUNT) throw new IllegalArgumentException("Invalid run slot");
        saveOwner = hero;
        activeSlot = slot;
        activeRunId = saved == null ? java.util.UUID.randomUUID().toString() : saved.runId;
        activeMetadata = saved;
        playedSeconds = saved == null ? 0 : saved.playedSeconds;
    }

    public boolean slotOccupied(int slot) { return exists(slotFile(slot)); }

    public int firstEmptySlot() {
        for (int slot = 0; slot < RUN_SLOT_COUNT; slot++) if (!slotOccupied(slot)) return slot;
        return -1;
    }

    public RunSaveData loadSlot(int slot) { return readRun(slotFile(slot)); }
    public RunSaveData loadSlotBackup(int slot) { return readRunFile(backup(slotFile(slot))); }


    public boolean deleteSlot(int slot, String expectedRunId) {
        RunSaveData saved = loadSlot(slot);
        if (saved != null && !saved.runId.equals(expectedRunId)) return false;
        if (saved != null && !com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.retire(saved.runId, "DELETED")) return false;
        boolean removed = deleteRunFiles(slotFile(slot));
        if (removed) { slotRevision++; com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.fillSlots(); }
        return removed;
    }

    public void deleteCurrentRun() {
        Hero owner = saveOwner;
        int slot = activeSlot;
        String identity = activeRunId;
        if (owner != null) com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.retire(identity, owner.isDead() || owner.getHP() <= 0 ? "DEAD" : "WON");
        detachRun();
        if (owner == null) return;
        if (slot >= 0) { deleteRunFiles(slotFile(slot)); slotRevision++; com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.fillSlots(); }
        else deleteSave(owner.getHeroClass());
    }


    public void importClassRunsIntoSlots() {
        if (steamAccount != null) return;
        for (HeroClass heroClass : HeroClass.values()) {
            if (heroClass == HeroClass.NEUTRAL || !hasSave(heroClass)) continue;
            RunSaveData saved = load(heroClass);
            if (saved == null) continue;
            boolean alreadyImported = false;
            for (int slot = 0; slot < RUN_SLOT_COUNT; slot++) {
                RunSaveData existing = loadSlot(slot);
                if (existing != null && existing.runId.equals(saved.runId)) { alreadyImported = true; break; }
            }
            if (alreadyImported) { deleteSave(heroClass); continue; }
            int empty = firstEmptySlot();
            if (empty < 0) return;
            if (writeRun(slotFile(empty), saved)) deleteSave(heroClass);
        }
    }

    private FileHandle slotFile(int slot) {
        if (slot < 0 || slot >= RUN_SLOT_COUNT) throw new IllegalArgumentException("Invalid run slot");
        return desktopSaveRoot().child(String.format(Locale.ROOT, "slot_%02d_v%d.sav", slot + 1,
                com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout.GENERATOR_VERSION));
    }

    public boolean installCloudSlot(int slot, RunSaveData data) {
        if (slot == activeSlot && saveOwner != null) return false;
        boolean saved = writeRun(slotFile(slot), data);
        if (saved) slotRevision++;
        return saved;
    }

    public boolean replaceActiveCloudSlot(int slot, RunSaveData data) {
        if (slot != activeSlot || saveOwner == null) return installCloudSlot(slot, data);
        boolean saved = writeRun(slotFile(slot), data);
        if (saved) { detachRun(); slotRevision++; }
        return saved;
    }


    public void removeRetiredSlots(java.util.Set<String> retired) {
        for (int slot = 0; slot < RUN_SLOT_COUNT; slot++) {
            RunSaveData data = loadSlot(slot);
            if (data != null && retired.contains(data.runId) && slot != activeSlot && deleteRunFiles(slotFile(slot))) slotRevision++;
        }
    }

    public ArrayList<RunSaveData> unscopedDesktopRuns() {
        ArrayList<RunSaveData> runs = new ArrayList<>();
        for (int slot = 0; slot < RUN_SLOT_COUNT; slot++) {
            RunSaveData data = readRun(Gdx.files.local(String.format(Locale.ROOT, "saves/slot_%02d_v2.sav", slot + 1)));
            if (data != null) runs.add(data);
        }
        for (HeroClass type : HeroClass.values()) if (type != HeroClass.NEUTRAL) {
            RunSaveData data = load(type); if (data != null) runs.add(data);
        }
        return runs;
    }

    private static FileHandle backup(FileHandle file) { return file.sibling(file.name() + ".bak"); }
    private static boolean exists(FileHandle file) { return file.exists() || backup(file).exists(); }

    private static boolean deleteRunFiles(FileHandle file) {
        boolean removed = !file.exists() || file.delete();
        FileHandle previous = backup(file);
        removed &= !previous.exists() || previous.delete();
        return removed;
    }


    private boolean writeRun(FileHandle file, RunSaveData data) {
        FileHandle temp = file.sibling(file.name() + ".tmp"), previous = backup(file);
        try {
            file.parent().mkdirs();
            try (java.io.OutputStream stream = temp.write(false);
                 ObjectOutputStream out = new ObjectOutputStream(stream)) { out.writeObject(data); }
            if (file.exists()) {
                if (previous.exists() && !previous.delete()) return false;
                if (!file.file().renameTo(previous.file())) return false;
            }
            if (!temp.file().renameTo(file.file())) {
                if (previous.exists()) previous.file().renameTo(file.file());
                return false;
            }
            return true;
        } catch (IOException | RuntimeException e) {
            Gdx.app.error("SaveHelper", "Could not save run", e);
            return false;
        } finally { if (temp.exists()) temp.delete(); }
    }

    private RunSaveData readRun(FileHandle file) {
        RunSaveData data = readRunFile(file);
        return data != null ? data : readRunFile(backup(file));
    }

    private RunSaveData readRunFile(FileHandle file) {
        if (!file.exists()) return null;
        try (java.io.InputStream stream = file.read(); ObjectInputStream in = new ObjectInputStream(stream)) {
            Object loaded = in.readObject();
            if (!(loaded instanceof RunSaveData)) return null;
            RunSaveData data = (RunSaveData) loaded;
            if (!isValidRun(data)) return null;
            if (data.runId == null) data.runId = java.util.UUID.nameUUIDFromBytes(
                    (data.heroClassName + ":" + data.runSeed).getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
            return data;
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            Gdx.app.error("SaveHelper", "Could not read run: " + file.name(), e);
            return null;
        }
    }

    public static boolean isValidRun(RunSaveData data) {
        if (data == null || data.generatorVersion != com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout.GENERATOR_VERSION
                || data.rooms == null || data.rooms.isEmpty() || data.rooms.size() > 3000 || data.hero == null || data.inventory == null
                || data.currentRoomId == null || data.heroClassName == null || data.currentDepth < 1 || data.currentDepth > 30
                || data.generatedDepthCount < data.currentDepth || data.generatedDepthCount > 30) return false;
        try { if (HeroClass.valueOf(data.heroClassName) == HeroClass.NEUTRAL) return false; }
        catch (IllegalArgumentException e) { return false; }
        return true;
    }

    public void prepareRoomRestore(RunSaveData data) {
        restoringRoomManifest = null;
        if (data == null) return;
        restoringRoomManifest = new HashMap<>();
        for (RoomSaveData room : data.rooms) if (room != null && room.roomId != null)
            restoringRoomManifest.put(room.roomId, room);
    }

    public boolean isRestoringRoomManifest() { return restoringRoomManifest != null; }
    public boolean hasManifestRoom(String identifier) {
        return restoringRoomManifest != null && restoringRoomManifest.containsKey(identifier);
    }

    private void validateRoomManifest() {
        if (restoringRoomManifest == null) throw new IllegalStateException("Missing generated room manifest");
        int count = 0;
        for (Level level : MapHelper.getInstance().levels) for (Room room : level.rooms) {
            RoomSaveData saved = restoringRoomManifest.get(room.getIdentifier());
            if (!matchesLayout(room, saved)) throw new IllegalStateException("Generated room layout mismatch: " + room.getIdentifier());
            count++;
        }
        if (count != restoringRoomManifest.size()) throw new IllegalStateException("Generated room count mismatch");
    }

    public static boolean matchesLayout(Room room, RoomSaveData saved) {
        return room != null && saved != null && room.getIdentifier().equals(saved.roomId)
                && room.getLayout().family().equals(saved.family) && room.getWidth() == saved.width
                && room.getHeight() == saved.height && room.getLayout().signature().equals(saved.layoutSignature);
    }

    private static final int MAX_RANKING_ENTRIES = 24;

    private static final SaveHelper ourInstance = new SaveHelper();

    public static SaveHelper getInstance() {
        return ourInstance;
    }

    private SaveHelper() {
    }

    public boolean hasSave(HeroClass heroClass) {
        return heroClass != null && exists(getSaveFile(heroClass));
    }

    public void deleteSave(HeroClass heroClass) {
        if (heroClass == null) {
            return;
        }

        deleteRunFiles(getSaveFile(heroClass));
    }

    public boolean saveCurrentRun() {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero != saveOwner || hero.isDead()) {
            return false;
        }

        RunSaveData saveData = snapshotCurrentRun(hero);
        saveData.runId = activeRunId;
        saveData.playedSeconds = playedSeconds;
        if (activeMetadata != null) {
            saveData.cloudRevision = activeMetadata.cloudRevision;
            saveData.cloudParent = activeMetadata.cloudParent;
            saveData.cloudClock = activeMetadata.cloudClock;
        }
        if (!com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.recordLocalSave(saveData)) return false;
        boolean saved = writeRun(activeSlot >= 0 ? slotFile(activeSlot) : getSaveFile(hero.getHeroClass()), saveData);
        if (saved || saveData.cloudRevision != null) activeMetadata = saveData;
        if (saved) slotRevision++;
        return saved;
    }

    public RunSaveData load(HeroClass heroClass) {
        if (heroClass == null) {
            return null;
        }

        return readRun(getSaveFile(heroClass));
    }

    @SuppressWarnings("unchecked")
    public ArrayList<RankingRunData> loadRankings() {
        FileHandle rankingsFile = getRankingsFile();
        if (!rankingsFile.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(rankingsFile.read())) {
            Object loaded = ois.readObject();
            if (loaded instanceof ArrayList) {
                return (ArrayList<RankingRunData>) loaded;
            }
        }
        catch (ClassNotFoundException e) {
            Gdx.app.log("SaveHelper", "Ranking file class not found (version mismatch?)", e);
        }
        catch (IOException e) {
            Gdx.app.log("SaveHelper", "Failed to read ranking file", e);
        }

        return new ArrayList<>();
    }

    public boolean recordDefeatedRun() {
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null) {
            return false;
        }

        ArrayList<RankingRunData> rankings = loadRankings();
        rankings.add(0, snapshotRankingRun(hero));
        while (rankings.size() > MAX_RANKING_ENTRIES) {
            rankings.remove(rankings.size() - 1);
        }

        return saveRankings(rankings);
    }

    public void restore(RunSaveData saveData, Hero hero) {
        if (saveData == null || hero == null || saveData.hero == null || saveData.inventory == null) {
            return;
        }
        prepareRoomRestore(saveData);

        hero.clearControlIntent();
        NecromancerMinion.clearAll();
        NecromancerCurse.clearAll();
        MercenaryFear.clearAll();
        MapHelper mapHelper = MapHelper.getInstance();
        mapHelper.clearRoomPresentation();
        mapHelper.setRestoringGeneratedLevels(true);
        try {
            mapHelper.restoreShownChapterIntroDepths(resolveShownChapterIntroDepths(saveData));
            mapHelper.restoreKeyCountsByDepth(saveData.keyCountsByDepth);

            int generatedDepthCount = Math.max(1, Math.max(saveData.currentDepth, saveData.generatedDepthCount));

            for (int depth = 2; depth <= generatedDepthCount; depth++) {
                mapHelper.enterDepth(depth);
            }

            mapHelper.enterDepth(Math.max(1, saveData.currentDepth));
            if (saveData.currentRoomId != null) {
                mapHelper.goToRoom(saveData.currentRoomId);
            }

            validateRoomManifest();
            restoreHero(hero, saveData.hero, saveData.currentRoomId);
            restoreInventory(saveData.inventory, hero);
            restorePersistentUnits(saveData.units);
            restoreRoomStates(saveData.rooms);
            restoreDoorStates(saveData.doors);

            for (Level generatedLevel : MapHelper.getInstance().levels) {
                generatedLevel.freezeSpawns();
            }

            hero.x = saveData.hero.x;
            hero.y = saveData.hero.y;
            hero.floorY = saveData.hero.floorY;
            hero.facingRight = saveData.hero.facingRight;
            PhysicsHelper.getInstance().syncBodyToUnit(hero);
            hero.getFriendlies();
            restoreOwnedMinions(saveData.units, hero);

            UIHelper.getInstance().setExpString(hero.expString());
            mapHelper.refreshHeroEnvironment();
        }
        finally {
            mapHelper.setRestoringGeneratedLevels(false);
            restoringRoomManifest = null;
        }
    }

    private RunSaveData snapshotCurrentRun(Hero hero) {
        RunSaveData saveData = new RunSaveData();
        saveData.heroClassName = hero.getHeroClass().name();
        saveData.difficultyName = DifficultyHelper.getInstance().getCurrentDifficulty().name();
        saveData.runSeed = RandomHelper.getInstance().getRunSeed();
        saveData.generatedDepthCount = MapHelper.getInstance().getGeneratedDepthCount();
        saveData.currentDepth = MapHelper.getInstance().getDepth();
        saveData.currentRoomId = hero.getRoom();
        saveData.itemIdentity = ItemIdentityHelper.getInstance().snapshot();
        saveData.quests = QuestManager.getInstance().snapshot();
        saveData.shownChapterIntroDepths = new ArrayList<>(MapHelper.getInstance().getShownChapterIntroDepthsSnapshot());
        saveData.keyCountsByDepth = MapHelper.getInstance().getKeyCountsByDepthSnapshot();
        saveData.hero = snapshotHero(hero);
        saveData.inventory = snapshotInventory(hero);
        saveData.units = snapshotUnits();
        saveData.rooms = snapshotRooms(hero);
        saveData.doors = snapshotDoors();
        return saveData;
    }

    private RankingRunData snapshotRankingRun(Hero hero) {
        RankingRunData saveData = new RankingRunData();
        saveData.heroClassName = hero.getHeroClass().name();
        saveData.heroClassDisplayName = hero.getHeroClass().getName();
        saveData.depthReached = Math.max(MapHelper.getInstance().getDepth(), MapHelper.getInstance().getGeneratedDepthCount());
        saveData.endedAtMillis = System.currentTimeMillis();
        saveData.heroSaveData = snapshotHero(hero);
        saveData.inventorySaveData = snapshotInventory(hero);
        saveData.inventorySnapshot = snapshotInventoryLines(hero);
        saveData.usedSkills = snapshotUsedSkills(hero);
        return saveData;
    }

    private HeroSaveData snapshotHero(Hero hero) {
        HeroSaveData saveData = new HeroSaveData();
        if (hero.getHeroClass() == HeroClass.NECROMANCER) saveData.necromancerOwnerId = hero.getPersistentId();
        saveData.x = hero.x;
        saveData.y = hero.y;
        saveData.floorY = hero.floorY;
        saveData.facingRight = hero.facingRight;
        saveData.level = hero.getLevel();
        saveData.bonusStrength = hero.getBonusStrength();
        saveData.experience = hero.getExperience();
        saveData.hp = hero.getHP();
        saveData.maxHp = hero.getMaxHP();
        saveData.mp = hero.getMp();
        saveData.maxMp = hero.getMmp();
        saveData.hunger = hero.getHunger();
        saveData.deferredDamage = DeferredDamage.savedAmount(hero);
        saveData.deferredDamageTick = DeferredDamage.savedTick(hero);
        saveData.fletchingTimer = hero.getFletchingTimer();
        saveData.huntingTimer = hero.getHuntingTimer();
        HashMap<Integer, Integer> fletchedArrows = hero.getFletchedArrowsPerDepth();
        if (!fletchedArrows.isEmpty()) saveData.fletchedArrowsPerDepth = fletchedArrows;
        saveData.skillPoints = hero.getSkillPoints();
        saveData.unlockedSkillIds = hero.getUnlockedSkills();
        if (NewClassSkillTree.isNewClass(hero.getHeroClass())) {
            saveData.newClassCooldowns = hero.getNewClassActions().copyCooldowns();
            saveData.newClassDurations = hero.getNewClassActions().copyDurations();
        }
        if (hero.getHeroClass() == HeroClass.NECROMANCER && hero.hasSkill(Skills.MASTER_OF_DEATH))
            saveData.masterOfDeathUsed = hero.hasUsedMasterOfDeath();
        saveData.activeSkillId = hero.getActiveSkill() != null ? hero.getActiveSkill().getId() : null;
        saveData.activeSkill2Id = hero.getActiveSkill2() != null ? hero.getActiveSkill2().getId() : null;
        saveData.activeSkill3Id = hero.getActiveSkill3() != null ? hero.getActiveSkill3().getId() : null;
        saveData.activeSkill4Id = hero.getActiveSkill4() != null ? hero.getActiveSkill4().getId() : null;
        saveData.activeSkill5Id = hero.getActiveSkill5() != null ? hero.getActiveSkill5().getId() : null;
        saveData.activeSkill6Id = hero.getActiveSkill6() != null ? hero.getActiveSkill6().getId() : null;
        saveData.activeSkill7Id = hero.getActiveSkill7() != null ? hero.getActiveSkill7().getId() : null;
        saveData.activeSkillUsageCounts = hero.getActiveSkillUsageCounts();
        return saveData;
    }

    private ArrayList<String> snapshotInventoryLines(Hero hero) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Gold: " + InventoryHelper.getInstance().getGold());

        ArrayList<Item> snapshottedItems = new ArrayList<>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            addInventorySnapshotLine(lines, snapshottedItems, item, hero);
        }

        addInventorySnapshotLine(lines, snapshottedItems, hero.getWeapon(), hero);
        addInventorySnapshotLine(lines, snapshottedItems, hero.getArmor(), hero);
        addInventorySnapshotLine(lines, snapshottedItems, hero.getRangedWeapon(), hero);

        return lines;
    }

    private void addInventorySnapshotLine(ArrayList<String> lines, ArrayList<Item> snapshottedItems, Item item, Hero hero) {
        if (item == null || snapshottedItems.contains(item) || item instanceof MeleeAttack || item instanceof BirthdaySuit) {
            return;
        }

        String itemLine = item.getNameWithQuantity();
        if (item == hero.getWeapon()) {
            itemLine += " [Melee]";
        }
        else if (item == hero.getArmor()) {
            itemLine += " [Armor]";
        }
        else if (item == hero.getRangedWeapon()) {
            itemLine += " [Ranged]";
        }
        else if (item instanceof EquipableItem && ((EquipableItem) item).getEquipped()) {
            itemLine += " [Equipped]";
        }

        lines.add(itemLine);
        snapshottedItems.add(item);
    }

    private ArrayList<String> snapshotUsedSkills(Hero hero) {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<Map.Entry<Integer, Integer>> usedSkills = new ArrayList<>(hero.getActiveSkillUsageCounts().entrySet());
        usedSkills.sort((first, second) -> {
            int countComparison = Integer.compare(second.getValue(), first.getValue());
            if (countComparison != 0) {
                return countComparison;
            }

            Skill firstSkill = SkillsHelper.getInstance().getSkill(first.getKey());
            Skill secondSkill = SkillsHelper.getInstance().getSkill(second.getKey());
            String firstName = firstSkill != null ? firstSkill.getName() : String.valueOf(first.getKey());
            String secondName = secondSkill != null ? secondSkill.getName() : String.valueOf(second.getKey());
            return firstName.compareTo(secondName);
        });

        for (Map.Entry<Integer, Integer> skillUsage : usedSkills) {
            Skill skill = SkillsHelper.getInstance().getSkill(skillUsage.getKey());
            String skillName = skill != null ? skill.getName() : "Skill #" + skillUsage.getKey();
            lines.add(skillName + " x" + skillUsage.getValue());
        }

        if (lines.isEmpty()) {
            lines.add("No active skills used.");
        }

        return lines;
    }

    private InventorySaveData snapshotInventory(Hero hero) {
        InventorySaveData saveData = new InventorySaveData();
        ArrayList<Item> snapshottedItems = new ArrayList<>();
        saveData.gold = InventoryHelper.getInstance().getGold();
        if (hero.getWeapon() != null && !(hero.getWeapon() instanceof MeleeAttack)) {
            saveData.equippedMeleeClassName = SaveRegistry.getItemId(hero.getWeapon());
        }
        if (hero.getArmor() != null && !(hero.getArmor() instanceof BirthdaySuit)) {
            saveData.equippedArmorClassName = SaveRegistry.getItemId(hero.getArmor());
        }
        if (hero.getRangedWeapon() != null) {
            saveData.equippedRangedClassName = SaveRegistry.getItemId(hero.getRangedWeapon());
        }

        for (Item item : InventoryHelper.getInstance().getItems()) {
            ItemSaveData itemSaveData = snapshotItem(item, hero);
            if (itemSaveData != null) {
                saveData.items.add(itemSaveData);
                snapshottedItems.add(item);
            }
        }

        snapshotEquippedItemIfMissing(saveData, snapshottedItems, hero.getWeapon(), hero);
        snapshotEquippedItemIfMissing(saveData, snapshottedItems, hero.getArmor(), hero);
        snapshotEquippedItemIfMissing(saveData, snapshottedItems, hero.getRangedWeapon(), hero);

        return saveData;
    }

    private ArrayList<UnitSaveData> snapshotUnits() {
        ArrayList<UnitSaveData> units = new ArrayList<>();
        HashSet<String> ownedIds = new HashSet<>();
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!isPersistentUnit(unit)) {
                continue;
            }

            UnitSaveData saveData = new UnitSaveData();
            saveData.id = unit.getPersistentId();
                saveData.className = unit instanceof MerchantRoom.ItemForPurchase
                    ? SaveRegistry.MERCHANT_ROOM_ITEM_FOR_PURCHASE_ID
                    : SaveRegistry.getUnitId(unit);
            saveData.roomId = unit.getRoom();
            saveData.x = unit.x;
            saveData.y = unit.y;
            saveData.floorY = unit.floorY;
            saveData.hp = unit.getHP();
            saveData.facingRight = unit.facingRight;
            saveData.friendly = unit instanceof Mob ? ((Mob) unit).isFriendlyWithoutControl() : unit.isFriendly;
            saveData.showOnly = unit.showOnly;
            saveData.summoned = unit.isSummoned;
            if (unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing) {
                com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing king =
                        (com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing) unit;
                saveData.kingNextPedestal = king.getNextPedestalIndex();
                saveData.kingAnnouncedWave = king.getAnnouncedWave();
                saveData.kingSummonDelay = king.getSummonDelay();
            }
            if (unit instanceof Swarm) saveData.swarmSplitOffshoot = ((Swarm) unit).isSplitOffshoot();
            saveData.necromancerCurseSeconds = NecromancerCurse.savedRemaining(unit);
            saveData.mercenaryFearSeconds = MercenaryFear.savedRemaining(unit);
            saveData.deferredDamage = DeferredDamage.savedAmount(unit);
            saveData.deferredDamageTick = DeferredDamage.savedTick(unit);

            if (unit instanceof NecromancerMinion) {
                if (!ownedIds.add(unit.getPersistentId())) continue;
                NecromancerMinion minion = (NecromancerMinion)unit;
                saveData.minion = new MinionSaveData();
                saveData.minion.ownerId = minion.getOwnerId();
                saveData.minion.createdAtLevel = minion.getSummonedLevel();
                saveData.minion.maxHp = minion.getMaxHP();
                saveData.minion.minDamage = minion.getWeapon().min();
                saveData.minion.maxDamage = minion.getWeapon().max();
                saveData.minion.pending = minion.isPendingTransfer();
                saveData.minion.spiritBinderApplied = minion.hasSpiritBinderBonus();
                saveData.minion.lichApplied = minion.hasLichBonus();
                if (minion instanceof SummonedGhost) saveData.minion.remainingLifetime = ((SummonedGhost)minion).getRemainingLifetime();
                if (minion instanceof RaisedSkeletonArcher) saveData.minion.shotCooldown = ((RaisedSkeletonArcher)minion).getShotCooldown();
            }

            if (unit instanceof ItemOnScreen) {
                saveData.item = snapshotItem(((ItemOnScreen) unit).getItem(), UnitHelper.getInstance().getHero());
                if (unit instanceof MerchantRoom.ItemForPurchase) {
                    saveData.purchaseCost = ((MerchantRoom.ItemForPurchase) unit).getBaseGoldCost();
                }
            }
            else if (unit instanceof DisturbableGraveProp) {
                DisturbableGraveProp graveProp = (DisturbableGraveProp) unit;
                saveData.rewardItem = snapshotItem(graveProp.getRewardItem(), UnitHelper.getInstance().getHero());
                saveData.triggered = graveProp.isDisturbed();
            }

            if (unit instanceof Statue) {
                saveData.triggered = ((Statue) unit).isTriggered();
            }

            if (unit instanceof PlatformTrap) {
                saveData.trapType = ((PlatformTrap) unit).getTrapType().ordinal();
                saveData.hidden = ((PlatformTrap) unit).isHidden();
                saveData.triggered = ((PlatformTrap) unit).isTriggered();
            }

            if (unit instanceof Mob) {
                Mob mob = (Mob) unit;
                saveData.sleeping = mob.isSleeping();
                saveData.championType = mob.getChampionType() != null ? mob.getChampionType().name() : null;
            }

            if (unit instanceof MercenaryRecruit) {
                MercenaryRecruit recruit = (MercenaryRecruit) unit;
                saveData.mercenaryType = recruit.getMercenaryType().name();
                saveData.mercenaryLevel = recruit.getMercenaryLevel();
                saveData.mercenaryPrice = recruit.getMercenaryPrice();
                saveData.mercenaryName = recruit.getMercenaryName();
                saveData.mercenaryWeapon = snapshotItem(recruit.getMercenaryWeapon(), UnitHelper.getInstance().getHero());
                saveData.mercenaryArmor = snapshotItem(recruit.getMercenaryArmor(), UnitHelper.getInstance().getHero());
                saveData.mercenaryRangedWeapon = snapshotItem(recruit.getMercenaryRangedWeapon(), UnitHelper.getInstance().getHero());
            }
            else if (unit instanceof MercenaryAlly) {
                MercenaryAlly mercenary = (MercenaryAlly) unit;
                saveData.mercenaryType = mercenary.getMercenaryType().name();
                saveData.mercenaryLevel = mercenary.getMercenaryLevel();
                saveData.mercenaryExperience = mercenary.getMercenaryExperience();
                saveData.mercenaryPrice = mercenary.getMercenaryPrice();
                saveData.mercenaryName = mercenary.getMercenaryName();
                saveData.mercenaryWeapon = snapshotItem(mercenary.getMercenaryWeapon(), UnitHelper.getInstance().getHero());
                saveData.mercenaryArmor = snapshotItem(mercenary.getMercenaryArmor(), UnitHelper.getInstance().getHero());
                saveData.mercenaryRangedWeapon = snapshotItem(mercenary.getMercenaryRangedWeapon(), UnitHelper.getInstance().getHero());
            }

            units.add(saveData);
        }

        return units;
    }

    private ArrayList<DoorSaveData> snapshotDoors() {
        ArrayList<DoorSaveData> doors = new ArrayList<>();
        for (Level level : MapHelper.getInstance().levels) {
            for (Room room : level.rooms) {
                for (Door door : room.getDoors()) {
                    DoorSaveData saveData = new DoorSaveData();
                    saveData.roomId = room.getIdentifier();
                    saveData.x = door.x;
                    saveData.y = door.y;
                    saveData.locked = door.isLocked();
                    saveData.requiresKey = door.requiresKey();
                    saveData.caged = door.isCaged();
                    saveData.opened = door.isOpen();
                    if (door instanceof MercenaryDoor) {
                        MercenaryHelper.MercenaryType type = ((MercenaryDoor) door).getRecruitType();
                        saveData.mercenaryType = type == null ? null : type.name();
                    }
                    doors.add(saveData);
                }
            }
        }

        return doors;
    }

    private ArrayList<RoomSaveData> snapshotRooms(Hero hero) {
        ArrayList<RoomSaveData> rooms = new ArrayList<>();
        for (Level level : MapHelper.getInstance().levels) {
            for (Room room : level.rooms) {
                RoomSaveData saveData = new RoomSaveData();
                saveData.roomId = room.getIdentifier();
                saveData.family = room.getLayout().family();
                saveData.width = (int) room.getWidth();
                saveData.height = (int) room.getHeight();
                saveData.layoutSignature = room.getLayout().signature();
                saveData.bossEncounterStarted = room.hasBossEncounterStarted();
                saveData.bossDefeated = room.isBossDefeated();
                if (hero.getHeroClass() == HeroClass.NECROMANCER) {
                    if (room.getNextCorpseOrder() > 0L) saveData.nextCorpseOrder = room.getNextCorpseOrder();
                    for (CorpseRecord corpse : room.getCorpses()) {
                        if (saveData.corpses == null) saveData.corpses = new ArrayList<>();
                        CorpseSaveData data = new CorpseSaveData();
                        data.victimId = corpse.victimId; data.roomId = corpse.roomId;
                        data.deathX = corpse.deathX; data.deathY = corpse.deathY;
                        data.x = corpse.x; data.y = corpse.y; data.order = corpse.order;
                        saveData.corpses.add(data);
                    }
                }
                rooms.add(saveData);
            }
        }

        return rooms;
    }

    private ItemSaveData snapshotItem(Item item, Hero hero) {
        if (item == null) {
            return null;
        }

        ItemSaveData saveData = new ItemSaveData();
        saveData.className = SaveRegistry.getItemId(item);
        saveData.quantity = item.getQuantity();
        saveData.equipped = item instanceof EquipableItem && ((EquipableItem) item).getEquipped();
        saveData.equippedMeleeSlot = item == hero.getWeapon();
        saveData.equippedArmorSlot = item == hero.getArmor();
        saveData.equippedRangedSlot = item == hero.getRangedWeapon();

        if (item.supportsLevel()) {
            saveData.itemLevel = item.getLevel();
        }

        if (item instanceof Weapon) {
            Prefix prefix = ((Weapon) item).getPrefix();
            saveData.prefixClassName = SaveRegistry.getPrefixId(prefix);
        }
        else if (item instanceof Armor) {
            Prefix prefix = ((Armor) item).getPrefix();
            saveData.prefixClassName = SaveRegistry.getPrefixId(prefix);
        }
        else if (item instanceof Ring) {
            Prefix prefix = ((Ring) item).getPrefix();
            saveData.prefixClassName = SaveRegistry.getPrefixId(prefix);
        }

        if (item instanceof RangedWeapon) {
            saveData.ammo = ((RangedWeapon) item).getAmmo();
        }

        if (item instanceof Pickaxe) {
            saveData.pickaxeBloodStained = ((Pickaxe) item).isBloodStained();
        }

        if (item instanceof Gemstone) {
            saveData.gemstoneCharge = ((Gemstone) item).getCharge();
        }

        return saveData;
    }

    private void snapshotEquippedItemIfMissing(InventorySaveData saveData, ArrayList<Item> snapshottedItems, Item item, Hero hero) {
        if (item == null || snapshottedItems.contains(item)) {
            return;
        }

        if (item instanceof MeleeAttack || item instanceof BirthdaySuit) {
            return;
        }

        ItemSaveData itemSaveData = snapshotItem(item, hero);
        if (itemSaveData != null) {
            saveData.items.add(itemSaveData);
            snapshottedItems.add(item);
        }
    }

    private void restoreHero(Hero hero, HeroSaveData saveData, String roomId) {
        if (hero.getHeroClass() == HeroClass.NECROMANCER && saveData.necromancerOwnerId != null
                && !saveData.necromancerOwnerId.isEmpty()) hero.setPersistentId(saveData.necromancerOwnerId);
        hero.setLevelDirect(saveData.level);
        hero.setBonusStrength(saveData.bonusStrength);
        hero.setMaxHP(hero.getHeroClass().getHealth(hero.getLevel()));
        hero.setMaxMP(hero.getHeroClass().getMana(hero.getLevel()));

        if (saveData.unlockedSkillIds != null) {
            for (Integer skillId : saveData.unlockedSkillIds) {
                Skill skill = SkillsHelper.getInstance().getSkill(skillId);
                if (skill != null && !hero.hasSkill(skillId)) {
                    hero.restoreSkill(skill);
                }
            }
        }

        hero.setMaxHP(saveData.maxHp);
        hero.setMaxMP(saveData.maxMp);
        hero.setHP(saveData.hp);
        hero.setMp(saveData.mp);
        hero.setExperience(saveData.experience);
        hero.setSkillPoints(saveData.skillPoints);
        hero.setHunger(saveData.hunger);
        hero.restoreHuntressPassives(saveData.fletchingTimer, saveData.huntingTimer, saveData.fletchedArrowsPerDepth);
        hero.setRoom(roomId);
        hero.setActiveSkillUsageCounts(saveData.activeSkillUsageCounts);
        if (NewClassSkillTree.isNewClass(hero.getHeroClass()))
            hero.getNewClassActions().restore(hero, saveData.newClassCooldowns, saveData.newClassDurations);
        hero.restoreMasterOfDeath(saveData.masterOfDeathUsed);
        DeferredDamage.restoreSaved(hero, saveData.deferredDamage, saveData.deferredDamageTick);
        hero.setActiveSkill(resolveActiveSkill(saveData.activeSkillId));
        hero.setActiveSkill2(resolveActiveSkill(saveData.activeSkill2Id));
        hero.setActiveSkill3(resolveActiveSkill(saveData.activeSkill3Id));
        hero.setActiveSkill4(resolveActiveSkill(saveData.activeSkill4Id));
        hero.setActiveSkill5(resolveActiveSkill(saveData.activeSkill5Id));
        hero.setActiveSkill6(resolveActiveSkill(saveData.activeSkill6Id));
        hero.setActiveSkill7(resolveActiveSkill(saveData.activeSkill7Id));
    }

    private void restoreInventory(InventorySaveData saveData, Hero hero) {
        InventoryHelper inventoryHelper = InventoryHelper.getInstance();
        ArrayList<String> legacyEquippedItems = new ArrayList<>();
        ArrayList<Item> equippedMiscItems = new ArrayList<>();
        Item equippedMeleeItem = null;
        Item equippedArmorItem = null;
        Item equippedRangedItem = null;

        inventoryHelper.getItems().clear();
        hero.setWeapon((MeleeAttack) new MeleeAttack().setOwner(hero));
        hero.setArmor((Armor) new BirthdaySuit().setOwner(hero));
        hero.setRangedWeapon(null);
        UIHelper.getInstance().setInventoryString("0 / 16");

        int currentGold = inventoryHelper.getGold();
        if (currentGold != 0) {
            inventoryHelper.modifyGold(-currentGold);
        }
        inventoryHelper.modifyGold(saveData.gold);

        if (saveData.items != null) {
            for (ItemSaveData itemSaveData : saveData.items) {
                Item item = createItem(itemSaveData);
                if (item == null) {
                    continue;
                }

                inventoryHelper.addRestoredItem(item);
                if (itemSaveData.equipped) {
                    legacyEquippedItems.add(itemSaveData.className);
                    if (!(item instanceof MeleeWeapon) && !(item instanceof Armor) && !(item instanceof RangedWeapon)) {
                        equippedMiscItems.add(item);
                    }
                }
                if (itemSaveData.equippedMeleeSlot) {
                    equippedMeleeItem = item;
                }
                if (itemSaveData.equippedArmorSlot) {
                    equippedArmorItem = item;
                }
                if (itemSaveData.equippedRangedSlot) {
                    equippedRangedItem = item;
                }
            }
        }

        equipItem(equippedMeleeItem, MeleeWeapon.class);
        equipItem(equippedArmorItem, Armor.class);
        equipItem(equippedRangedItem, RangedWeapon.class);

        if (equippedMeleeItem == null) {
            equipInventoryItem(saveData.equippedMeleeClassName, MeleeWeapon.class);
        }
        if (equippedArmorItem == null) {
            equipInventoryItem(saveData.equippedArmorClassName, Armor.class);
        }
        if (equippedRangedItem == null) {
            equipInventoryItem(saveData.equippedRangedClassName, RangedWeapon.class);
        }

        if (hero.getWeapon() instanceof MeleeAttack) {
            equipLegacyInventoryItem(legacyEquippedItems, MeleeWeapon.class);
        }

        if (hero.getArmor() instanceof BirthdaySuit) {
            equipLegacyInventoryItem(legacyEquippedItems, Armor.class);
        }

        if (hero.getRangedWeapon() == null) {
            equipLegacyInventoryItem(legacyEquippedItems, RangedWeapon.class);
        }

        for (Item equippedMiscItem : equippedMiscItems) {
            equipItem(equippedMiscItem, null);
        }
    }

    private void restorePersistentUnits(ArrayList<UnitSaveData> unitSaveData) {
        ArrayList<Unit> existingUnits = new ArrayList<>(UnitHelper.getInstance().getUnits());
        Map<String, Unit> existingUnitsById = new HashMap<>();
        HashSet<String> savedIds = new HashSet<>();
        boolean trapStateSaved = false;

        if (unitSaveData != null) {
            for (UnitSaveData saveData : unitSaveData) {
                if (isValidSavedUnit(saveData)) {
                    savedIds.add(saveData.id);
                }

                if (saveData != null && SaveRegistry.getUnitId(PlatformTrap.class).equals(saveData.className)) {
                    trapStateSaved = true;
                }
            }
        }

        for (Unit unit : existingUnits) {
            if (!isPersistentUnit(unit)) {
                continue;
            }

            existingUnitsById.put(unit.getPersistentId(), unit);
            if (!savedIds.contains(unit.getPersistentId())) {
                if (unit instanceof PlatformTrap && !trapStateSaved) {
                    continue;
                }

                UnitHelper.getInstance().removeUnit(unit);
            }
        }

        if (unitSaveData == null) {
            return;
        }

        for (UnitSaveData saveData : unitSaveData) {
            if (!isValidSavedUnit(saveData)) {
                continue;
            }

            Unit unit = existingUnitsById.get(saveData.id);
            if (unit != null && !matchesSavedUnitType(unit, saveData)) {
                UnitHelper.getInstance().removeUnit(unit);
                existingUnitsById.remove(saveData.id);
                unit = null;
            }

            if (unit == null) {
                unit = createUnit(saveData);
                if (unit == null) {
                    continue;
                }

                unit.setPersistentId(saveData.id);
                unit.setRoom(saveData.roomId);
                unit.x = saveData.x;
                unit.y = saveData.y;
                unit.floorY = saveData.floorY;
                unit.facingRight = saveData.facingRight;
                UnitHelper.getInstance().addUnit(unit);
            }

            applyUnitState(unit, saveData);
        }
    }


    private void restoreOwnedMinions(ArrayList<UnitSaveData> rows, Hero hero) {
        if (rows == null || hero.getHeroClass() != HeroClass.NECROMANCER || hero.isDead() || hero.getHP() <= 0) return;
        HashSet<String> seen = new HashSet<>();
        for (Unit unit : UnitHelper.getInstance().getUnits()) seen.add(unit.getPersistentId());
        int count = 0, cap = hero.hasSkill(Skills.LICH) ? 3 : 2;
        for (UnitSaveData row : rows) {
            if (row == null || row.minion == null || !SaveRegistry.isOwnedMinionId(row.className)
                    || row.id == null || row.id.isEmpty() || seen.contains(row.id) || count >= cap) continue;
            MinionSaveData data = row.minion;
            boolean ghost = SaveRegistry.getUnitId(SummonedGhost.class).equals(row.className);
            boolean archer = SaveRegistry.getUnitId(RaisedSkeletonArcher.class).equals(row.className);
            if (!hero.getPersistentId().equals(data.ownerId) || data.createdAtLevel < 1 || data.createdAtLevel > hero.getLevel()
                    || data.maxHp < 1 || row.hp < 1 || row.hp > data.maxHp
                    || !Float.isFinite(data.minDamage) || !Float.isFinite(data.maxDamage)
                    || data.minDamage < 0f || data.maxDamage < data.minDamage
                    || !Float.isFinite(row.x) || !Float.isFinite(row.y) || !Float.isFinite(row.floorY)
                    || row.item != null || row.rewardItem != null
                    || !data.pending && findRoom(row.roomId) == null
                    || data.spiritBinderApplied && (!hero.hasSkill(Skills.SPIRIT_BINDER) || data.lichApplied)
                    || data.lichApplied && !hero.hasSkill(Skills.LICH)) continue;
            if (ghost ? !hero.hasSkill(Skills.SPIRIT_BINDER) || data.remainingLifetime == null
                    || !Float.isFinite(data.remainingLifetime) || data.remainingLifetime <= 0f || data.remainingLifetime > SummonedGhost.SECONDS
                    : data.remainingLifetime != null) continue;
            if (archer ? !hero.hasSkill(Skills.LICH) || data.shotCooldown == null
                    || !Float.isFinite(data.shotCooldown) || data.shotCooldown < 0f || data.shotCooldown > RaisedSkeletonArcher.SHOT_INTERVAL
                    : data.shotCooldown != null) continue;
            NecromancerMinion minion = SaveRegistry.createOwnedMinion(row.className, hero, data.createdAtLevel);
            if (minion == null) continue;
            minion.setPersistentId(row.id);
            minion.setMaxHP(data.maxHp); minion.setHP(row.hp);
            ((MeleeAttack)minion.getWeapon()).setDamageRange(data.minDamage, data.maxDamage);
            minion.restoreSpiritBinder(hero, data.spiritBinderApplied);
            minion.restoreLich(hero, data.lichApplied);
            if (ghost) ((SummonedGhost)minion).restoreLifetime(data.remainingLifetime);
            if (archer) ((RaisedSkeletonArcher)minion).restoreShotCooldown(data.shotCooldown);

            minion.setRoom(hero.getRoom()); minion.x = row.x; minion.y = row.y; minion.floorY = row.floorY;
            minion.facingRight = row.facingRight;
            minion.showOnly = true; minion.setVisible(false);
            UnitHelper.getInstance().addUnit(minion);
            minion.restorePlacement(hero, data.pending || !hero.getRoom().equals(row.roomId));
            seen.add(row.id); count++;
        }
    }

    private void restoreDoorStates(ArrayList<DoorSaveData> doorSaveData) {
        if (doorSaveData == null) {
            return;
        }

        for (DoorSaveData saveData : doorSaveData) {
            Door door = findDoor(saveData.roomId, saveData.x, saveData.y);
            if (door == null) {
                continue;
            }

            if (door instanceof MercenaryDoor) {
                ((MercenaryDoor) door).rememberRecruitType(MercenaryHelper.MercenaryType.fromName(saveData.mercenaryType));
            }

            if (saveData.caged) {
                door.cageUp();
            }
            else if (saveData.locked) {
                if (saveData.requiresKey) {
                    door.lockWithKey();
                }
                else {
                    door.lock();
                }
            }
            else if (saveData.opened) {
                door.open();
            }
            else {
                door.close();
            }
        }
    }

    private void restoreRoomStates(ArrayList<RoomSaveData> roomSaveData) {
        MapHelper.getInstance().clearCorpses();
        if (roomSaveData == null) {
            return;
        }

        for (RoomSaveData saveData : roomSaveData) {
            if (saveData == null || saveData.roomId == null) continue;
            Room room = findRoom(saveData.roomId);
            if (room == null) {
                continue;
            }

            room.setBossEncounterStarted(saveData.bossEncounterStarted);
            room.setBossDefeated(saveData.bossDefeated);
            Hero hero = UnitHelper.getInstance().getHero();
            if (hero != null && hero.getHeroClass() == HeroClass.NECROMANCER) {
                ArrayList<CorpseRecord> records = null;
                if (saveData.corpses != null && !saveData.corpses.isEmpty()) {
                    records = new ArrayList<>();
                    for (CorpseSaveData corpse : saveData.corpses) if (corpse != null) {
                        records.add(new CorpseRecord(corpse.victimId, corpse.roomId, corpse.deathX, corpse.deathY,
                                corpse.x, corpse.y, corpse.order));
                    }
                }
                room.restoreCorpses(records, saveData.nextCorpseOrder);
            }
        }
    }

    private void applyUnitState(Unit unit, UnitSaveData saveData) {
        unit.setPersistentId(saveData.id);
        unit.setRoom(saveData.roomId);
        unit.x = saveData.x;
        unit.y = saveData.y;
        unit.floorY = saveData.floorY;
        unit.facingRight = saveData.facingRight;
        unit.showOnly = saveData.showOnly;
        unit.isSummoned = saveData.summoned;
        if (unit instanceof Swarm) ((Swarm) unit).restoreSplitOffshoot(Boolean.TRUE.equals(saveData.swarmSplitOffshoot));

        if (unit instanceof Mob) {
            restoreChampion((Mob) unit, saveData);
        }

        if (saveData.hp <= 0) {
            UnitHelper.getInstance().removeUnit(unit);
            return;
        }

        unit.setHP(saveData.hp);

        if (unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing) {
            ((com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing) unit)
                    .restoreSummonState(saveData.kingNextPedestal, saveData.kingAnnouncedWave, saveData.kingSummonDelay);
        }

        if (unit instanceof Mob && saveData.friendly) {
            ((Mob) unit).makeFriendly();
        }
        else {
            unit.isFriendly = saveData.friendly;
        }

        if (unit instanceof Mob) {
            if (Boolean.TRUE.equals(saveData.sleeping)) {
                ((Mob) unit).putToSleep();
            } else {
                ((Mob) unit).wakeToWandering();
            }
            unit.changeState(com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState.IDLE, true);
        }

        if (unit instanceof MerchantRoom.ItemForPurchase && saveData.purchaseCost != null) {
            ((MerchantRoom.ItemForPurchase) unit).setGoldCost(saveData.purchaseCost);
        }

        if (unit instanceof DisturbableGraveProp) {
            ((DisturbableGraveProp) unit)
                    .setRewardItem(createItem(saveData.rewardItem))
                    .setDisturbed(Boolean.TRUE.equals(saveData.triggered));
        }

        if (unit instanceof Statue) {
            ((Statue) unit).setTriggered(Boolean.TRUE.equals(saveData.triggered));
        }

        if (unit instanceof PlatformTrap) {
            ((PlatformTrap) unit)
                    .setTrapType(TrapType.fromIndex(saveData.trapType))
                    .setHidden(Boolean.TRUE.equals(saveData.hidden))
                    .setTriggered(Boolean.TRUE.equals(saveData.triggered));
        }

        if (unit instanceof MercenaryRecruit) {
            restoreMercenaryRecruit((MercenaryRecruit) unit, saveData);
        }
        else if (unit instanceof MercenaryAlly) {
            restoreMercenaryAlly((MercenaryAlly) unit, saveData);
            unit.setHP(saveData.hp);
        }

        NecromancerCurse.restoreSaved(unit, saveData.necromancerCurseSeconds);
        MercenaryFear.restoreSaved(unit, saveData.mercenaryFearSeconds);
        DeferredDamage.restoreSaved(unit, saveData.deferredDamage, saveData.deferredDamageTick);
        PhysicsHelper.getInstance().syncBodyToUnit(unit);
    }

    private Unit createUnit(UnitSaveData saveData) {
        try {
            if (saveData.item != null) {
                Item item = createItem(saveData.item);
                if (item == null) {
                    return null;
                }

                if (SaveRegistry.MERCHANT_ROOM_ITEM_FOR_PURCHASE_ID.equals(saveData.className)) {
                    Room room = findRoom(saveData.roomId);
                    if (room instanceof MerchantRoom) {
                        MerchantRoom.ItemForPurchase itemForPurchase = ((MerchantRoom) room).new ItemForPurchase(item);
                        if (saveData.purchaseCost != null) {
                            itemForPurchase.setGoldCost(saveData.purchaseCost);
                        }
                        return itemForPurchase;
                    }
                }

                return new ItemOnScreen(item);
            }

            return SaveRegistry.createUnit(saveData.className);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private void restoreMercenaryRecruit(MercenaryRecruit recruit, UnitSaveData saveData) {
        MercenaryHelper.MercenaryType type = resolveMercenaryType(saveData.mercenaryType);
        int level = saveData.mercenaryLevel == null ? 1 : saveData.mercenaryLevel;
        int price = saveData.mercenaryPrice == null ? MercenaryHelper.getPrice(type, level) : saveData.mercenaryPrice;
        String name = saveData.mercenaryName == null ? MercenaryHelper.getName(type) : saveData.mercenaryName;

        recruit.applyMercenaryProfile(type,
                name,
                level,
                price,
                createMeleeWeapon(saveData.mercenaryWeapon),
                createArmor(saveData.mercenaryArmor),
                createRangedWeapon(saveData.mercenaryRangedWeapon));
    }

    private void restoreMercenaryAlly(MercenaryAlly mercenary, UnitSaveData saveData) {
        MercenaryHelper.MercenaryType type = resolveMercenaryType(saveData.mercenaryType);
        int level = saveData.mercenaryLevel == null ? 1 : saveData.mercenaryLevel;
        int price = saveData.mercenaryPrice == null ? MercenaryHelper.getPrice(type, level) : saveData.mercenaryPrice;
        String name = saveData.mercenaryName == null ? MercenaryHelper.getName(type) : saveData.mercenaryName;

        mercenary.applyMercenaryProfile(type,
                name,
                level,
                price,
                createMeleeWeapon(saveData.mercenaryWeapon),
                createArmor(saveData.mercenaryArmor),
                createRangedWeapon(saveData.mercenaryRangedWeapon));
        mercenary.setMercenaryExperience(saveData.mercenaryExperience == null ? 0 : saveData.mercenaryExperience);
        mercenary.wakeToWandering();
    }

    private void restoreChampion(Mob mob, UnitSaveData saveData) {
        if (mob == null || saveData == null || saveData.championType == null || saveData.championType.isEmpty()) {
            return;
        }

        Mob.ChampionType type = Mob.ChampionType.fromName(saveData.championType);
        if (type != null) {
            mob.promoteToChampion(type);
        }
    }

    private MercenaryHelper.MercenaryType resolveMercenaryType(String typeName) {
        MercenaryHelper.MercenaryType type = MercenaryHelper.MercenaryType.fromName(typeName);
        return type == null ? MercenaryHelper.MercenaryType.BRUTE : type;
    }

    private MeleeWeapon createMeleeWeapon(ItemSaveData saveData) {
        Item item = createItem(saveData);
        return item instanceof MeleeWeapon ? (MeleeWeapon) item : null;
    }

    private Armor createArmor(ItemSaveData saveData) {
        Item item = createItem(saveData);
        return item instanceof Armor ? (Armor) item : null;
    }

    private RangedWeapon createRangedWeapon(ItemSaveData saveData) {
        Item item = createItem(saveData);
        return item instanceof RangedWeapon ? (RangedWeapon) item : null;
    }

    private Item createItem(ItemSaveData saveData) {
        try {
            Item item = SaveRegistry.createItem(saveData.className);
            if (item == null) {
                return null;
            }
            if (item instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.Gold) {
                ((com.bilboldev.skillfulpixeldungeonplatformer.items.Gold) item).restoreQuantity(saveData.quantity);
            }
            else {
                item.setQuantity(saveData.quantity);
            }
            if (item instanceof Gun) ((Gun) item).suppressNaturalCompanion();

            Prefix prefix = createPrefix(saveData.prefixClassName);
            if (item instanceof Weapon) {
                ((Weapon) item).setPrefix(prefix);
            }
            else if (item instanceof Armor) {
                ((Armor) item).setPrefix(prefix);
            }
            else if (item instanceof Ring) {
                ((Ring) item).setPrefix(prefix);
            }

            if (saveData.itemLevel != null && item.supportsLevel()) {
                item.setLevel(saveData.itemLevel);
            }

            if (item instanceof RangedWeapon && saveData.ammo != null) {
                ((RangedWeapon) item).setAmmo(saveData.ammo);
            }

            if (item instanceof Pickaxe && saveData.pickaxeBloodStained != null) {
                ((Pickaxe) item).setBloodStained(saveData.pickaxeBloodStained);
            }

            if (item instanceof Gemstone && saveData.gemstoneCharge != null) {
                ((Gemstone) item).restoreCharge(saveData.gemstoneCharge);
            }

            if (item instanceof EquipableItem) {
                ((EquipableItem) item).setEquippedState(false);
            }

            return item;
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public HeroClass resolveRankingHeroClass(RankingRunData rankingRunData) {
        if (rankingRunData == null) {
            return HeroClass.WARRIOR;
        }

        if (rankingRunData.heroClassName != null) {
            try {
                return HeroClass.valueOf(rankingRunData.heroClassName);
            }
            catch (IllegalArgumentException ignored) {

            }
        }

        if (rankingRunData.heroClassDisplayName == null) {
            return HeroClass.WARRIOR;
        }

        String normalizedName = rankingRunData.heroClassDisplayName.trim().toLowerCase(Locale.ROOT);
        if ("huntress".equals(normalizedName) || "archer".equals(normalizedName)) {
            return HeroClass.ARCHER;
        }
        if ("wizard".equals(normalizedName)) {
            return HeroClass.WIZARD;
        }
        if ("rogue".equals(normalizedName)) {
            return HeroClass.ROGUE;
        }
        if ("necromancer".equals(normalizedName)) {
            return HeroClass.NECROMANCER;
        }
        if ("mercenary".equals(normalizedName)) {
            return HeroClass.MERCENARY;
        }
        return HeroClass.WARRIOR;
    }

    public Item createPreviewItem(ItemSaveData saveData) {
        Item item = createItem(saveData);
        if (item instanceof EquipableItem) {
            boolean equipped = saveData != null
                    && (saveData.equipped || saveData.equippedMeleeSlot || saveData.equippedArmorSlot || saveData.equippedRangedSlot);
            ((EquipableItem) item).setEquippedState(equipped);
        }
        return item;
    }

    private Prefix createPrefix(String prefixClassName) {
        if (prefixClassName == null || prefixClassName.isEmpty()) {
            return null;
        }

        return SaveRegistry.createPrefix(prefixClassName);
    }

    private boolean equipInventoryItem(String itemClassName, Class<?> expectedType) {
        if (itemClassName == null || itemClassName.isEmpty()) {
            return false;
        }

        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (!itemClassName.equals(SaveRegistry.getItemId(item))) {
                continue;
            }

            if (expectedType != null && !expectedType.isInstance(item)) {
                continue;
            }

            equipItem(item);
            return true;
        }

        return false;
    }

    private boolean equipLegacyInventoryItem(ArrayList<String> legacyEquippedItems, Class<?> expectedType) {
        for (String legacyEquippedItem : legacyEquippedItems) {
            if (equipInventoryItem(legacyEquippedItem, expectedType)) {
                return true;
            }
        }

        return false;
    }

    private boolean equipItem(Item item, Class<?> expectedType) {
        if (item == null || (expectedType != null && !expectedType.isInstance(item))) {
            return false;
        }

        equipItem(item);
        return true;
    }

    private void equipItem(Item item) {
        if (item instanceof MeleeWeapon) {
            ((MeleeWeapon) item).setEquipped(true);
        }
        else if (item instanceof Armor) {
            ((Armor) item).setEquipped(true);
        }
        else if (item instanceof RangedWeapon) {
            ((RangedWeapon) item).setEquipped(true);
        }
        else if (item instanceof EquipableItem) {
            ((EquipableItem) item).setEquipped(true);
        }
    }

    private boolean isValidSavedUnit(UnitSaveData saveData) {
        return saveData != null && saveData.id != null && saveData.hp > 0 && saveData.minion == null
                && !SaveRegistry.isOwnedMinionId(saveData.className);
    }

    private boolean matchesSavedUnitType(Unit unit, UnitSaveData saveData) {
        if (unit == null || saveData == null) {
            return false;
        }

        if (saveData.item != null) {
            return SaveRegistry.isMerchantRoomItemForPurchaseId(saveData.className)
                    ? unit instanceof MerchantRoom.ItemForPurchase
                    : unit instanceof ItemOnScreen;
        }

        return saveData.className != null && saveData.className.equals(SaveRegistry.getUnitId(unit.getClass()));
    }

    private ActiveSkill resolveActiveSkill(Integer skillId) {
        if (skillId == null) {
            return null;
        }

        Skill skill = SkillsHelper.getInstance().getSkill(skillId);
        return skill instanceof ActiveSkill ? (ActiveSkill) skill : null;
    }

    private Room findRoom(String roomId) {
        if (roomId == null) {
            return null;
        }

        for (Level level : MapHelper.getInstance().levels) {
            for (Room room : level.rooms) {
                if (room.getIdentifier().equals(roomId)) {
                    return room;
                }
            }
        }

        return null;
    }

    private Door findDoor(String roomId, float x, float y) {
        Room room = findRoom(roomId);
        if (room == null) {
            return null;
        }

        for (Door door : room.getDoors()) {
            if ((int) door.x == (int) x && (int) door.y == (int) y) {
                return door;
            }
        }

        return null;
    }

    private boolean isPersistentUnit(Unit unit) {
        if (unit instanceof NecromancerMinion) {
            NecromancerMinion minion = (NecromancerMinion)unit;
            return !unit.isDead() && unit.getHP() > 0 && minion.belongsTo(UnitHelper.getInstance().getHero())
                    && SaveRegistry.isOwnedMinionId(SaveRegistry.getUnitId(unit))
                    && (minion.isPendingTransfer() || unit.getRoom() != null);
        }
        if (unit == null || unit.isHero || unit.getRoom() == null || unit.getHP() < 1 || unit.isDead()) {
            return false;
        }

        if (unit instanceof Interactable) {
            return true;
        }

        if (unit instanceof ItemOnScreen) {
            return true;
        }

        if (unit instanceof PlatformTrap) {
            return true;
        }

        if (unit instanceof Plant) {
            return true;
        }



        return unit instanceof Mob && (!unit.isSummoned
                || unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarvenUndead
                || unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Larva);
    }

    private FileHandle getSaveFile(HeroClass heroClass) {


        return Gdx.files.local("saves/" + heroClass.name().toLowerCase(java.util.Locale.ROOT) + "_v"
                + com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout.GENERATOR_VERSION + ".sav");
    }

    private ArrayList<Integer> resolveShownChapterIntroDepths(RunSaveData saveData) {
        if (saveData == null) {
            return new ArrayList<>();
        }

        if (saveData.shownChapterIntroDepths != null && !saveData.shownChapterIntroDepths.isEmpty()) {
            return saveData.shownChapterIntroDepths;
        }

        ArrayList<Integer> chapterDepths = new ArrayList<>();
        int furthestReachedDepth = Math.max(saveData.currentDepth, saveData.generatedDepthCount);
        int[] introDepths = new int[] {1, 6, 11, 16, 22};
        for (int introDepth : introDepths) {
            if (furthestReachedDepth >= introDepth) {
                chapterDepths.add(introDepth);
            }
        }

        return chapterDepths;
    }

    private FileHandle getRankingsFile() {
        return Gdx.files.local("rankings/runs.sav");
    }

    private boolean saveRankings(ArrayList<RankingRunData> rankings) {
        FileHandle rankingsFile = getRankingsFile();
        rankingsFile.parent().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(rankingsFile.write(false))) {
            oos.writeObject(rankings);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public static class RunSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String runId;
        public double playedSeconds;
        public String cloudRevision, cloudParent, cloudDevice;
        public HashMap<String, Long> cloudClock;
        public int generatorVersion = com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout.GENERATOR_VERSION;
        public String heroClassName;
        public String difficultyName;
        public long runSeed;
        public int generatedDepthCount;
        public int currentDepth;
        public String currentRoomId;
        public ItemIdentityHelper.ItemIdentitySaveData itemIdentity;
        public QuestManager.QuestSaveData quests;
        public ArrayList<Integer> shownChapterIntroDepths = new ArrayList<>();
        public HashMap<Integer, Integer> keyCountsByDepth = new HashMap<>();
        public HeroSaveData hero;
        public InventorySaveData inventory;
        public ArrayList<UnitSaveData> units = new ArrayList<>();
        public ArrayList<RoomSaveData> rooms = new ArrayList<>();
        public ArrayList<DoorSaveData> doors = new ArrayList<>();
    }

    public static class HeroSaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        public String necromancerOwnerId;
        public float x;
        public float y;
        public float floorY;
        public boolean facingRight;
        public int level;
        public int bonusStrength;
        public int experience;
        public int hp;
        public int maxHp;
        public int mp;
        public int maxMp;
        public float hunger;

        public float fletchingTimer;
        public float huntingTimer;
        public HashMap<Integer, Integer> fletchedArrowsPerDepth;
        public int skillPoints;
        public ArrayList<Integer> unlockedSkillIds = new ArrayList<>();
        public Integer activeSkillId;
        public Integer activeSkill2Id;
        public Integer activeSkill3Id;
        public Integer activeSkill4Id;
        public Integer activeSkill5Id;
        public Integer activeSkill6Id;
        public Integer activeSkill7Id;
        public HashMap<Integer, Integer> activeSkillUsageCounts = new HashMap<>();

        public float[] newClassCooldowns;
        public float[] newClassDurations;

        public Boolean masterOfDeathUsed;

        public Integer deferredDamage;
        public Float deferredDamageTick;
    }

    public static class InventorySaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public int gold;
        public String equippedMeleeClassName;
        public String equippedArmorClassName;
        public String equippedRangedClassName;
        public ArrayList<ItemSaveData> items = new ArrayList<>();
    }

    public static class ItemSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String className;
        public int quantity;
        public String prefixClassName;
        public Integer itemLevel;
        public boolean equipped;
        public boolean equippedMeleeSlot;
        public boolean equippedArmorSlot;
        public boolean equippedRangedSlot;
        public Integer ammo;
        public Boolean pickaxeBloodStained;
        public Float gemstoneCharge;
    }

    public static class UnitSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id;
        public String className;
        public String roomId;
        public float x;
        public float y;
        public float floorY;
        public int hp;
        public boolean facingRight;
        public boolean friendly;
        public boolean showOnly;
        public boolean summoned;

        public Integer kingNextPedestal;
        public Integer kingAnnouncedWave;
        public Float kingSummonDelay;

        public Boolean swarmSplitOffshoot;
        public ItemSaveData item;
        public ItemSaveData rewardItem;
        public Integer purchaseCost;
        public Boolean triggered;
        public Integer trapType;
        public Boolean hidden;
        public Boolean sleeping;
        public String championType;
        public String mercenaryType;
        public Integer mercenaryLevel;
        public Integer mercenaryExperience;
        public Integer mercenaryPrice;
        public String mercenaryName;
        public ItemSaveData mercenaryWeapon;
        public ItemSaveData mercenaryArmor;
        public ItemSaveData mercenaryRangedWeapon;

        public MinionSaveData minion;

        public Float necromancerCurseSeconds;

        public Float mercenaryFearSeconds;

        public Integer deferredDamage;
        public Float deferredDamageTick;
    }

    public static class MinionSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String ownerId;
        public int createdAtLevel;
        public int maxHp;
        public float minDamage, maxDamage;
        public boolean pending;
        public boolean spiritBinderApplied;
        public boolean lichApplied;

        public Float remainingLifetime;

        public Float shotCooldown;
    }

    public static class DoorSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String roomId;
        public float x;
        public float y;
        public boolean locked;
        public boolean requiresKey;
        public boolean caged;
        public boolean opened;
        public String mercenaryType;
    }

    public static class RoomSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String roomId;
        public String family, layoutSignature;
        public int width, height;
        public boolean bossEncounterStarted;
        public boolean bossDefeated;

        public ArrayList<CorpseSaveData> corpses;
        public Long nextCorpseOrder;
    }

    public static class CorpseSaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String victimId;
        public String roomId;
        public float deathX, deathY, x, y;
        public long order;
    }

    public static class RankingRunData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String heroClassName;
        public String heroClassDisplayName;
        public int depthReached;
        public long endedAtMillis;
        public HeroSaveData heroSaveData;
        public InventorySaveData inventorySaveData;
        public ArrayList<String> inventorySnapshot = new ArrayList<>();
        public ArrayList<String> usedSkills = new ArrayList<>();
    }
}
