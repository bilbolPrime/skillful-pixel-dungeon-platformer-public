package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.math.RandomXS128;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.LeatherArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.MailArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.PlateArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.ScaleArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.CorpseDust;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.DarkGold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.DwarfToken;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.DriedRose;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.PhantomFish;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.Pickaxe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.RatSkull;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfAccuracy;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfDetection;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfElements;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfEvasion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHaggler;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHaste;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfHerbalism;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfMending;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfPower;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfSatiety;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfShadows;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.RingOfThorns;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.RotberrySeed;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Axe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Glaive;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Hammer;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Mace;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.ShortSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Spear;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Sword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.FireBoltWand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.Wand;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfAmok;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfAvalanche;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfBlink;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfDisintegration;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfLightning;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfPoison;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfReach;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfRegrowth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.WandOfSlowness;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.CavesLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.CityLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.PrisonLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.SewersLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration.DarkGoldVein;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Blacksmith;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Ghost;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Imp;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Wandmaker;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Monk;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Golem;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Bat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.CursePersonification;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.FetidRat;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.ChoiceDialogWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryItemChoiceWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuestManager {
    private static final float QUEST_DIALOG_BOTTOM_EXTENSION = 100f;
    private static final float QUEST_DIALOG_BUTTON_BOTTOM_PADDING = 110f;
    private static final float BLACKSMITH_DIALOG_BOTTOM_EXTENSION = 45f;
    private static final float BLACKSMITH_DIALOG_BUTTON_BOTTOM_PADDING = 92.5f;
    private static final int BLACKSMITH_DARK_GOLD_REQUIRED = 15;
    private static final long BLACKSMITH_ORE_VEIN_RANDOM_SALT = 0x5C8A41D7B63E2194L;

    public enum GhostQuestType {
        NONE,
        ROSE,
        RAT,
        CURSE
    }

    public enum WandmakerQuestType {
        NONE,
        BERRY,
        DUST,
        FISH
    }

    public enum ImpQuestType {
        NONE,
        GOLEM,
        MONK
    }

    private static final QuestManager ourInstance = new QuestManager();

    private QuestSaveData questSaveData;

    public static QuestManager getInstance() {
        return ourInstance;
    }

    private QuestManager() {
        reset();
    }

    public void reset() {
        questSaveData = new QuestSaveData();
    }

    public void restore(QuestSaveData saveData) {
        questSaveData = saveData != null ? saveData : new QuestSaveData();
        normalize();
    }

    public QuestSaveData snapshot() {
        normalize();
        return questSaveData;
    }

    public GhostQuestState getGhostQuest() {
        normalize();
        return questSaveData.ghostQuest;
    }

    public WandmakerQuestState getWandmakerQuest() {
        normalize();
        return questSaveData.wandmakerQuest;
    }

    public BlacksmithQuestState getBlacksmithQuest() {
        normalize();
        return questSaveData.blacksmithQuest;
    }

    public ImpQuestState getImpQuest() {
        normalize();
        return questSaveData.impQuest;
    }

    private ChoiceDialogWindow createQuestDialog(Interactable npc, String description) {
        ChoiceDialogWindow dialog = new ChoiceDialogWindow(npc.getDialoguePortrait(), description, 1850f, 420f)
            .extendBottom(QUEST_DIALOG_BOTTOM_EXTENSION)
            .setButtonBottomPadding(QUEST_DIALOG_BUTTON_BOTTOM_PADDING);
        if (npc instanceof Blacksmith) {
            dialog.extendBottom(QUEST_DIALOG_BOTTOM_EXTENSION + BLACKSMITH_DIALOG_BOTTOM_EXTENSION)
                .setButtonBottomPadding(QUEST_DIALOG_BUTTON_BOTTOM_PADDING
                    + (BLACKSMITH_DIALOG_BUTTON_BOTTOM_PADDING - 70f));
        }
        return dialog;
    }

    private InventoryItemChoiceWindow createQuestItemChoiceWindow(Interactable npc,
                                                                  String description,
                                                                  ArrayList<Item> items,
                                                                  InventoryItemChoiceWindow.ItemSelectionHandler selectionHandler) {
        return new InventoryItemChoiceWindow(npc.getDialoguePortrait(), description, items, selectionHandler);
    }

    public void onSewersLevelGenerated(SewersLevel level) {
        GhostQuestState ghostQuest = getGhostQuest();
        if (level == null || ghostQuest.spawned || ghostQuest.completed || level.getDepth() <= 1 || level.getDepth() >= 5) {
            return;
        }

        int chanceDivisor = Math.max(1, 5 - level.getDepth());
        if (RandomHelper.getInstance().randomInt(chanceDivisor) != 0) {
            return;
        }

        Room room = chooseQuestSpawnRoom(level);
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null) {
            return;
        }

        ghostQuest.spawned = true;
        ghostQuest.completed = false;
        ghostQuest.given = false;
        ghostQuest.processed = false;
        ghostQuest.depth = level.getDepth();
        ghostQuest.leftToKill = 0;
        ghostQuest.roomId = room.getIdentifier();
        ghostQuest.type = randomGhostQuestType();
        if (ghostQuest.type == GhostQuestType.ROSE) {
            ghostQuest.leftToKill = 8;
        }

        ghostQuest.weaponReward = generateGhostWeaponReward();
        ghostQuest.armorReward = generateGhostArmorReward();
        spawnGhostNpc(room, spawn.x, spawn.y);
    }

    public void onPrisonLevelGenerated(PrisonLevel level) {
        WandmakerQuestState wandmakerQuest = getWandmakerQuest();
        if (level == null || wandmakerQuest.spawned || wandmakerQuest.completed || level.getDepth() <= 6 || level.getDepth() >= 10) {
            return;
        }

        int chanceDivisor = Math.max(1, 10 - level.getDepth());
        if (RandomHelper.getInstance().randomInt(chanceDivisor) != 0) {
            return;
        }

        Room room = chooseQuestSpawnRoom(level);
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null) {
            return;
        }

        wandmakerQuest.spawned = true;
        wandmakerQuest.completed = false;
        wandmakerQuest.given = false;
        wandmakerQuest.depth = level.getDepth();
        wandmakerQuest.roomId = room.getIdentifier();
        wandmakerQuest.type = randomWandmakerQuestType();
        wandmakerQuest.battleWandReward = generateBattleWandReward();
        wandmakerQuest.utilityWandReward = generateUtilityWandReward();
        spawnWandmakerNpc(room, spawn.x, spawn.y);
        spawnWandmakerQuestItem(level, wandmakerQuest.type);
    }

    public void onCityLevelGenerated(CityLevel level) {
        ImpQuestState impQuest = getImpQuest();
        if (level == null || impQuest.spawned || impQuest.completed || level.getDepth() <= 16 || level.getDepth() >= 20) {
            return;
        }

        int chanceDivisor = Math.max(1, 20 - level.getDepth());
        if (RandomHelper.getInstance().randomInt(chanceDivisor) != 0) {
            return;
        }

        Room room = chooseQuestSpawnRoom(level);
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null) {
            return;
        }

        impQuest.spawned = true;
        impQuest.completed = false;
        impQuest.given = false;
        impQuest.depth = level.getDepth();
        impQuest.roomId = room.getIdentifier();
        impQuest.type = randomImpQuestType();
        impQuest.goalCount = impQuest.type == ImpQuestType.GOLEM ? 6 : 8;
        impQuest.ringReward = generateImpRingReward();
        spawnImpNpc(room, spawn.x, spawn.y);
    }

    public void onCavesLevelGenerated(CavesLevel level) {
        BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        if (level == null || blacksmithQuest.spawned || blacksmithQuest.reforged || level.getDepth() <= 11 || level.getDepth() >= 15) {
            return;
        }

        int chanceDivisor = Math.max(1, 15 - level.getDepth());
        if (RandomHelper.getInstance().randomInt(chanceDivisor) != 0) {
            return;
        }

        Room room = chooseQuestSpawnRoom(level);
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null) {
            return;
        }

        blacksmithQuest.spawned = true;
        blacksmithQuest.given = false;
        blacksmithQuest.completed = false;
        blacksmithQuest.reforged = false;
        blacksmithQuest.darkGoldVeinsSpawned = false;
        blacksmithQuest.depth = level.getDepth();
        blacksmithQuest.roomId = room.getIdentifier();
        blacksmithQuest.alternative = RandomHelper.getInstance().randomBoolean();
        spawnBlacksmithNpc(room, spawn.x, spawn.y);
    }

    public void interactWithGhost(final Ghost ghost) {
        GhostQuestState ghostQuest = getGhostQuest();
        if (ghost == null || !ghostQuest.spawned || ghostQuest.completed) {
            return;
        }

        ghostQuest.npcId = ghost.getPersistentId();
        ghostQuest.roomId = ghost.getRoom();

        if (!ghostQuest.given) {
            if (ghostQuest.type == GhostQuestType.CURSE) {
                WindowHelper.getInstance().addWindow(createQuestDialog(
                        ghost,
                        getGhostRequestText(ghostQuest))
                        .addChoice("Yes, I will do it for you", new Runnable() {
                            @Override
                            public void run() {
                                acceptGhostCurseQuest(ghost);
                            }
                        })
                        .addChoice("No, I can't help you", new Runnable() {
                            @Override
                            public void run() {
                                relocateGhost(ghost);
                            }
                        })
                        .build());
                return;
            }

            WindowHelper.getInstance().addWindow(createQuestDialog(
                    ghost,
                    getGhostRequestText(ghostQuest))
                    .addChoice("I will help", new Runnable() {
                        @Override
                        public void run() {
                            acceptGhostStandardQuest(ghost);
                        }
                    })
                    .build());
            return;
        }

        if (canClaimGhostReward(ghostQuest)) {
            showGhostRewardWindow(ghost, ghostQuest);
            return;
        }

            WindowHelper.getInstance().addWindow(createQuestDialog(
                ghost,
                getGhostReminderText(ghostQuest))
                .addChoice("Continue", null)
                .build());
    }

    public void interactWithWandmaker(final Wandmaker wandmaker) {
        final WandmakerQuestState wandmakerQuest = getWandmakerQuest();
        if (wandmaker == null || !wandmakerQuest.spawned || wandmakerQuest.completed) {
            return;
        }

        wandmakerQuest.npcId = wandmaker.getPersistentId();
        wandmakerQuest.roomId = wandmaker.getRoom();

        if (!wandmakerQuest.given) {
            WindowHelper.getInstance().addWindow(createQuestDialog(
                    wandmaker,
                    getWandmakerRequestText(wandmakerQuest))
                    .addChoice("I will help", new Runnable() {
                        @Override
                        public void run() {
                            getWandmakerQuest().given = true;
                        }
                    })
                    .build());
            return;
        }

        if (canClaimWandmakerReward(wandmakerQuest)) {
            showWandmakerRewardWindow(wandmaker, wandmakerQuest);
            return;
        }

            WindowHelper.getInstance().addWindow(createQuestDialog(
                wandmaker,
                getWandmakerReminderText(wandmakerQuest))
                .addChoice("Continue", null)
                .build());
    }

    public void interactWithImp(final Imp imp) {
        final ImpQuestState impQuest = getImpQuest();
        if (imp == null || !impQuest.spawned || impQuest.completed) {
            return;
        }

        impQuest.npcId = imp.getPersistentId();
        impQuest.roomId = imp.getRoom();

        if (!impQuest.given) {
            WindowHelper.getInstance().addWindow(createQuestDialog(
                    imp,
                    getImpRequestText(impQuest))
                    .addChoice("I will handle it", new Runnable() {
                        @Override
                        public void run() {
                            getImpQuest().given = true;
                        }
                    })
                    .build());
            return;
        }

        if (getInventoryCount(DwarfToken.class) >= impQuest.goalCount) {
            showImpRewardWindow(imp, impQuest);
            return;
        }

            WindowHelper.getInstance().addWindow(createQuestDialog(
                imp,
                getImpReminderText(impQuest))
                .addChoice("Continue", null)
                .build());
    }

    public void interactWithBlacksmith(final Blacksmith blacksmith) {
        final BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        if (blacksmith == null || !blacksmithQuest.spawned) {
            return;
        }

        blacksmithQuest.npcId = blacksmith.getPersistentId();
        blacksmithQuest.roomId = blacksmith.getRoom();

        if (blacksmithQuest.reforged) {
            WindowHelper.getInstance().addWindow(createQuestDialog(
                blacksmith,
                getBlacksmithBusyText())
                    .addChoice("Continue", null)
                    .build());
            return;
        }

        if (!blacksmithQuest.given) {
            WindowHelper.getInstance().addWindow(createQuestDialog(
                blacksmith,
                getBlacksmithRequestText(blacksmithQuest))
                    .addChoice("I will do it", new Runnable() {
                        @Override
                        public void run() {
                            giveBlacksmithQuestPickaxe(blacksmith, blacksmithQuest);
                        }
                    })
                    .build());
            return;
        }

        if (!blacksmithQuest.completed) {
            Pickaxe pickaxe = (Pickaxe) findInventoryItem(Pickaxe.class);
            if (pickaxe == null) {
            WindowHelper.getInstance().addWindow(createQuestDialog(
                blacksmith,
                getBlacksmithMissingPickaxeText())
                        .addChoice("Continue", null)
                        .build());
                return;
            }

            if (!canClaimBlacksmithReward(blacksmithQuest, pickaxe)) {
            WindowHelper.getInstance().addWindow(createQuestDialog(
                blacksmith,
                getBlacksmithReminderText(blacksmithQuest))
                        .addChoice("Continue", null)
                        .build());
                return;
            }

            completeBlacksmithQuest(blacksmith, blacksmithQuest, pickaxe);
            return;
        }

        openBlacksmithReforgePrompt(blacksmith, blacksmithQuest);
    }

    public void onMobKilled(Mob mob) {
        processGhostKill(mob);
        processBlacksmithKill(mob);
        processImpKill(mob);
    }

    public void ensureBlacksmithOreVeins() {
        BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        if (!shouldHaveBlacksmithOreVeins(blacksmithQuest)) {
            if (blacksmithQuest != null && blacksmithQuest.depth > 0) {
                clearBlacksmithOreVeins(blacksmithQuest);
                blacksmithQuest.darkGoldVeinsSpawned = false;
            }
            return;
        }

        Level level = findLevelByDepth(blacksmithQuest.depth);
        if (level == null) {
            return;
        }

        if (countBlacksmithOreVeins(level) > 0) {
            blacksmithQuest.darkGoldVeinsSpawned = true;
            return;
        }

        int veinsToSpawn = Math.max(0, BLACKSMITH_DARK_GOLD_REQUIRED - getInventoryCount(DarkGold.class));
        if (veinsToSpawn <= 0) {
            blacksmithQuest.darkGoldVeinsSpawned = true;
            return;
        }

        ArrayList<DarkGoldVeinLocation> candidates = getBlacksmithOreVeinCandidates(level, blacksmithQuest, true);
        if (candidates.isEmpty()) {
            candidates = getBlacksmithOreVeinCandidates(level, blacksmithQuest, false);
        }

        if (candidates.isEmpty()) {
            DarkGoldVeinLocation fallbackLocation = getFallbackBlacksmithOreVeinCandidate(level, blacksmithQuest);
            if (fallbackLocation != null) {
                candidates.add(fallbackLocation);
            }
        }

        if (candidates.isEmpty()) {
            return;
        }

        Collections.sort(candidates, new Comparator<DarkGoldVeinLocation>() {
            @Override
            public int compare(DarkGoldVeinLocation first, DarkGoldVeinLocation second) {
                return first.sortKey().compareTo(second.sortKey());
            }
        });

        RandomXS128 roomRandom = RandomHelper.getInstance().createRoomRandom(
                blacksmithQuest.depth,
                blacksmithQuest.roomId == null ? "blacksmith" : blacksmithQuest.roomId,
                BLACKSMITH_ORE_VEIN_RANDOM_SALT);

        if (blacksmithQuest.roomId != null && veinsToSpawn > 0) {
            ArrayList<DarkGoldVeinLocation> blacksmithRoomCandidates = new ArrayList<DarkGoldVeinLocation>();
            for (DarkGoldVeinLocation candidate : candidates) {
                if (blacksmithQuest.roomId.equals(candidate.roomId)) {
                    blacksmithRoomCandidates.add(candidate);
                }
            }

            if (!blacksmithRoomCandidates.isEmpty()) {
                DarkGoldVeinLocation location = blacksmithRoomCandidates.get(roomRandom.nextInt(blacksmithRoomCandidates.size()));
                candidates.remove(location);
                spawnDarkGoldVein(location);
                veinsToSpawn--;
            }
        }

        for (int spawned = 0; spawned < veinsToSpawn && !candidates.isEmpty(); spawned++) {
            DarkGoldVeinLocation location = candidates.remove(roomRandom.nextInt(candidates.size()));
            spawnDarkGoldVein(location);
        }

        blacksmithQuest.darkGoldVeinsSpawned = true;
    }

    public boolean tryMineWithEquippedPickaxe(Pickaxe pickaxe) {
        BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        if (pickaxe == null || !shouldHaveBlacksmithOreVeins(blacksmithQuest) || MapHelper.getInstance().getDepth() != blacksmithQuest.depth) {
            return false;
        }

        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.getRoom() == null) {
            return false;
        }

        DarkGoldVein vein = findDarkGoldVeinAtHero(hero);
        if (vein == null) {
            return false;
        }

        DarkGold darkGold = new DarkGold();
        if (!InventoryHelper.getInstance().addItem(darkGold)) {
            darkGold.drop(hero.x, hero.y, hero.getRoom());
        }

        UnitHelper.getInstance().removeUnit(vein);
        hero.setHunger(hero.getHunger() - 3f);
        SoundHelper.GetSingleton().play(Sounds.GOLD, 0f, 1f);
        return true;
    }

    public void mineWithPickaxe(Pickaxe pickaxe) {
        if (tryMineWithEquippedPickaxe(pickaxe)) {
            return;
        }

        BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        if (pickaxe == null || !blacksmithQuest.spawned || !blacksmithQuest.given || blacksmithQuest.completed) {
            WindowHelper.getInstance().addWindow(new TextWindow(1100f, 160f, "The pickaxe has no obvious use right now.").build());
            return;
        }

        if (blacksmithQuest.alternative) {
            WindowHelper.getInstance().addWindow(new TextWindow(1200f, 160f, "The blacksmith wanted bat blood, not ore.").build());
            return;
        }

        if (MapHelper.getInstance().getDepth() != blacksmithQuest.depth) {
            WindowHelper.getInstance().addWindow(new TextWindow(1200f, 160f, "There is no dark gold worth mining here.").build());
            return;
        }

        WindowHelper.getInstance().addWindow(new TextWindow(1450f, 160f, "No dark gold vein is within reach from this platform.").build());
    }

    private void processGhostKill(Mob mob) {
        GhostQuestState ghostQuest = getGhostQuest();
        if (mob == null || !ghostQuest.spawned || ghostQuest.completed || ghostQuest.depth != MapHelper.getInstance().getDepth()) {
            return;
        }

        if (ghostQuest.type == GhostQuestType.ROSE) {
            if (!ghostQuest.given || ghostQuest.processed || mob instanceof FetidRat || mob instanceof CursePersonification) {
                return;
            }

            if (RandomHelper.getInstance().randomInt(Math.max(1, ghostQuest.leftToKill)) == 0) {
                new DriedRose().drop(mob.x, mob.y, mob.getRoom());
                ghostQuest.processed = true;
                return;
            }

            ghostQuest.leftToKill = Math.max(1, ghostQuest.leftToKill - 1);
            return;
        }

        if (ghostQuest.type == GhostQuestType.RAT) {
            if (!ghostQuest.given) {
                return;
            }

            if (mob instanceof FetidRat && ghostQuest.uniqueMobId != null && ghostQuest.uniqueMobId.equals(mob.getPersistentId())) {
                ghostQuest.uniqueMobId = null;
                new RatSkull().drop(mob.x, mob.y, mob.getRoom());
                return;
            }

            if (ghostQuest.uniqueMobId == null && !ghostQuest.processed) {
                spawnFetidRat();
                ghostQuest.processed = true;
            }
            return;
        }

        if (ghostQuest.type == GhostQuestType.CURSE
                && ghostQuest.uniqueMobId != null
                && ghostQuest.uniqueMobId.equals(mob.getPersistentId())
                && mob instanceof CursePersonification) {
            ghostQuest.uniqueMobId = null;
            ghostQuest.processed = true;
            Room room = findRoom(mob.getRoom());
            if (room != null) {
                spawnGhostNpc(room, mob.x, mob.floorY);
            }
        }
    }

    private void processImpKill(Mob mob) {
        ImpQuestState impQuest = getImpQuest();
        if (mob == null || !impQuest.spawned || impQuest.completed || !impQuest.given || impQuest.depth != MapHelper.getInstance().getDepth()) {
            return;
        }

        boolean validKill = impQuest.type == ImpQuestType.GOLEM && mob instanceof Golem;
        validKill |= impQuest.type == ImpQuestType.MONK && mob instanceof Monk;
        if (!validKill) {
            return;
        }

        new DwarfToken().drop(mob.x, mob.y, mob.getRoom());
    }

    private void processBlacksmithKill(Mob mob) {
        BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        if (mob == null || !blacksmithQuest.spawned || blacksmithQuest.completed || !blacksmithQuest.given || !blacksmithQuest.alternative) {
            return;
        }

        if (!(mob instanceof Bat) || !(mob.getLastDamagingItem() instanceof Pickaxe)) {
            return;
        }

        ((Pickaxe) mob.getLastDamagingItem()).setBloodStained(true);
    }

    private void acceptGhostCurseQuest(Ghost ghost) {
        GhostQuestState ghostQuest = getGhostQuest();
        ghostQuest.given = true;
        ghostQuest.npcId = null;
        UnitHelper.getInstance().removeUnit(ghost);
        spawnCursePersonification(ghost.getRoom(), ghost.x, ghost.floorY);
        SoundHelper.GetSingleton().play(Sounds.CURSE, 0f, 1f);
        refreshContextButtons();
    }

    private void acceptGhostStandardQuest(Ghost ghost) {
        GhostQuestState ghostQuest = getGhostQuest();
        ghostQuest.given = true;

        if (ghostQuest.type == GhostQuestType.RAT && ghostQuest.uniqueMobId == null && !ghostQuest.processed) {
            spawnFetidRat();
            ghostQuest.processed = ghostQuest.uniqueMobId != null;
        }

        refreshContextButtons();
    }

    private void relocateGhost(Ghost ghost) {
        Room room = chooseQuestSpawnRoom(findLevelByDepth(getGhostQuest().depth));
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null) {
            return;
        }

        ghost.setRoom(room.getIdentifier());
        ghost.x = spawn.x;
        ghost.y = spawn.y;
        ghost.floorY = spawn.y;
        getGhostQuest().roomId = room.getIdentifier();
        refreshContextButtons();
    }

    private void showGhostRewardWindow(final Ghost ghost, final GhostQuestState ghostQuest) {
        final ArrayList<Item> rewards = new ArrayList<Item>();
        Item weaponReward = createRewardItem(ghostQuest.weaponReward);
        if (weaponReward != null) {
            rewards.add(weaponReward);
        }
        Item armorReward = createRewardItem(ghostQuest.armorReward);
        if (armorReward != null) {
            rewards.add(armorReward);
        }

        if (rewards.isEmpty()) {
            return;
        }

        WindowHelper.getInstance().addWindow(createQuestItemChoiceWindow(
            ghost,
                getGhostCompletionText(ghostQuest),
                rewards,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        removeGhostQuestProof(ghostQuest);
                        item.identify();
                        if (!InventoryHelper.getInstance().addItem(item)) {
                            item.spawnNaturally(ghost.x, ghost.y, ghost.floorY, ghost.getRoom());
                        }

                        ghostQuest.completed = true;
                        ghostQuest.spawned = false;
                        ghostQuest.npcId = null;
                        ghostQuest.roomId = null;
                        ghostQuest.uniqueMobId = null;
                        UnitHelper.getInstance().removeUnit(ghost);
                        WindowHelper.getInstance().addWindow(new TextWindow(1100f, 160f, "Farewell, adventurer!").build());
                        refreshContextButtons();
                    }
                }).build());
    }

    private void showWandmakerRewardWindow(final Wandmaker wandmaker, final WandmakerQuestState wandmakerQuest) {
        final ArrayList<Item> rewards = new ArrayList<Item>();
        Item battleWand = createRewardItem(wandmakerQuest.battleWandReward);
        if (battleWand != null) {
            rewards.add(battleWand);
        }
        Item utilityWand = createRewardItem(wandmakerQuest.utilityWandReward);
        if (utilityWand != null) {
            rewards.add(utilityWand);
        }

        if (rewards.isEmpty()) {
            return;
        }

        WindowHelper.getInstance().addWindow(createQuestItemChoiceWindow(
            wandmaker,
                getWandmakerCompletionText(),
                rewards,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        removeWandmakerQuestProof(wandmakerQuest);
                        item.identify();
                        if (!InventoryHelper.getInstance().addItem(item)) {
                            item.spawnNaturally(wandmaker.x, wandmaker.y, wandmaker.floorY, wandmaker.getRoom());
                        }

                        wandmakerQuest.completed = true;
                        wandmakerQuest.spawned = false;
                        wandmakerQuest.npcId = null;
                        wandmakerQuest.roomId = null;
                        UnitHelper.getInstance().removeUnit(wandmaker);
                        WindowHelper.getInstance().addWindow(new TextWindow(1100f, 160f, "Good luck in your quest!").build());
                        refreshContextButtons();
                    }
                }).build());
    }

    private void showImpRewardWindow(final Imp imp, final ImpQuestState impQuest) {
        WindowHelper.getInstance().addWindow(createQuestDialog(
            imp,
            getImpCompletionText())
                .addChoice("Take the ring", new Runnable() {
                    @Override
                    public void run() {
                        Item reward = createRewardItem(impQuest.ringReward);
                        consumeInventoryQuantity(DwarfToken.class, impQuest.goalCount);
                        if (reward != null) {
                            reward.identify();
                            if (!InventoryHelper.getInstance().addItem(reward)) {
                                reward.spawnNaturally(imp.x, imp.y, imp.floorY, imp.getRoom());
                            }
                        }

                        impQuest.completed = true;
                        impQuest.spawned = false;
                        impQuest.npcId = null;
                        impQuest.roomId = null;
                        UnitHelper.getInstance().removeUnit(imp);
                        WindowHelper.getInstance().addWindow(new TextWindow(900f, 160f, "See you!").build());
                        refreshContextButtons();
                    }
                })
                .build());
    }

    private void giveBlacksmithQuestPickaxe(Blacksmith blacksmith, BlacksmithQuestState blacksmithQuest) {
        blacksmithQuest.given = true;
        blacksmithQuest.darkGoldVeinsSpawned = false;
        Pickaxe pickaxe = new Pickaxe();
        if (!InventoryHelper.getInstance().addItem(pickaxe)) {
            pickaxe.drop(blacksmith.x, blacksmith.y, blacksmith.getRoom());
        }

        ensureBlacksmithOreVeins();
    }

    private boolean canClaimBlacksmithReward(BlacksmithQuestState blacksmithQuest, Pickaxe pickaxe) {
        if (pickaxe == null) {
            return false;
        }

        if (blacksmithQuest.alternative) {
            return pickaxe.isBloodStained();
        }

        return getInventoryCount(DarkGold.class) >= BLACKSMITH_DARK_GOLD_REQUIRED;
    }

    private void completeBlacksmithQuest(final Blacksmith blacksmith, final BlacksmithQuestState blacksmithQuest, Pickaxe pickaxe) {
        if (!blacksmithQuest.alternative) {
            consumeInventoryQuantity(DarkGold.class, BLACKSMITH_DARK_GOLD_REQUIRED);
            clearBlacksmithOreVeins(blacksmithQuest);
        }

        unequipIfNeeded(pickaxe);
        InventoryHelper.getInstance().removeItem(pickaxe);
        blacksmithQuest.completed = true;

        WindowHelper.getInstance().addWindow(createQuestDialog(
            blacksmith,
            getBlacksmithCompletionText())
                .addChoice("Continue", new Runnable() {
                    @Override
                    public void run() {
                        openBlacksmithReforgePrompt(blacksmith, blacksmithQuest);
                    }
                })
                .build());
    }

    private void openBlacksmithReforgePrompt(final Blacksmith blacksmith, final BlacksmithQuestState blacksmithQuest) {
        ArrayList<Item> candidates = getBlacksmithPrimaryCandidates();
        if (candidates.size() < 2) {
            WindowHelper.getInstance().addWindow(new TextWindow(1500f, 160f, "You need two identified matching items to reforge.").build());
            return;
        }

        WindowHelper.getInstance().addWindow(createQuestDialog(
            blacksmith,
            getBlacksmithReforgeText())
                .addChoice("Continue", new Runnable() {
                    @Override
                    public void run() {
                        openBlacksmithFirstSelection(blacksmith, blacksmithQuest);
                    }
                })
                .build());
    }

    private void openBlacksmithFirstSelection(final Blacksmith blacksmith, final BlacksmithQuestState blacksmithQuest) {
        final ArrayList<Item> candidates = getBlacksmithPrimaryCandidates();
        if (candidates.isEmpty()) {
            WindowHelper.getInstance().addWindow(new TextWindow(1500f, 160f, "You need two identified matching items to reforge.").build());
            return;
        }

        WindowHelper.getInstance().addWindow(createQuestItemChoiceWindow(
            blacksmith,
                getBlacksmithSelectItemText(),
                candidates,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        openBlacksmithSecondSelection(blacksmith, blacksmithQuest, item);
                    }
                }).build());
    }

    private void openBlacksmithSecondSelection(final Blacksmith blacksmith, final BlacksmithQuestState blacksmithQuest, final Item firstItem) {
        final ArrayList<Item> candidates = getBlacksmithMatchingCandidates(firstItem);
        if (candidates.isEmpty()) {
            WindowHelper.getInstance().addWindow(new TextWindow(1500f, 160f, "That item has no matching partner to reforge.").build());
            return;
        }

        WindowHelper.getInstance().addWindow(createQuestItemChoiceWindow(
            blacksmith,
                getBlacksmithSelectItemText(),
                candidates,
                new InventoryItemChoiceWindow.ItemSelectionHandler() {
                    @Override
                    public void onItemSelected(Item item) {
                        confirmBlacksmithReforge(blacksmith, blacksmithQuest, firstItem, item);
                    }
                }).build());
    }

    private void confirmBlacksmithReforge(final Blacksmith blacksmith, final BlacksmithQuestState blacksmithQuest, final Item firstItem, final Item secondItem) {
        WindowHelper.getInstance().addWindow(createQuestDialog(
                blacksmith,
                getBlacksmithReforgeText())
                .addChoice("Reforge them", new Runnable() {
                    @Override
                    public void run() {
                        applyBlacksmithReforge(blacksmithQuest, firstItem, secondItem);
                    }
                })
                .build());
    }

    private void applyBlacksmithReforge(BlacksmithQuestState blacksmithQuest, Item firstItem, Item secondItem) {
        Item betterItem = firstItem.getLevel() >= secondItem.getLevel() ? firstItem : secondItem;
        Item consumedItem = betterItem == firstItem ? secondItem : firstItem;

        betterItem.identify();
        betterItem.modifyLevel(1);
        AchievementManager.getInstance().onItemUpgraded(betterItem);
        unequipIfNeeded(consumedItem);
        InventoryHelper.getInstance().removeItem(consumedItem);
        blacksmithQuest.reforged = true;

        WindowHelper.getInstance().addWindow(new TextWindow(
                1400f,
                160f,
                String.format("Your %s certainly looks better now.", betterItem.getName().toLowerCase()))
                .build());
    }

    private ArrayList<Item> getBlacksmithPrimaryCandidates() {
        ArrayList<Item> eligibleItems = new ArrayList<Item>();
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (!isBlacksmithReforgeCandidate(item)) {
                continue;
            }

            for (Item otherItem : InventoryHelper.getInstance().getItems()) {
                if (item == otherItem || !isBlacksmithReforgeCandidate(otherItem)) {
                    continue;
                }

                if (item.getClass() == otherItem.getClass()) {
                    eligibleItems.add(item);
                    break;
                }
            }
        }

        return eligibleItems;
    }

    private ArrayList<Item> getBlacksmithMatchingCandidates(Item firstItem) {
        ArrayList<Item> matches = new ArrayList<Item>();
        if (firstItem == null) {
            return matches;
        }

        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (item == firstItem || !isBlacksmithReforgeCandidate(item)) {
                continue;
            }

            if (item.getClass() == firstItem.getClass()) {
                matches.add(item);
            }
        }

        return matches;
    }

    private boolean isBlacksmithReforgeCandidate(Item item) {
        if (item == null || item instanceof Pickaxe || !item.canUpgrade() || !item.isIdentified() || item.getLevel() < 1) {
            return false;
        }

        return !isCursedItem(item);
    }

    private boolean isCursedItem(Item item) {
        if (item instanceof Armor) {
            return ((Armor) item).getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed;
        }

        if (item instanceof Ring) {
            return ((Ring) item).getPrefix() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed;
        }

        return false;
    }

    private void unequipIfNeeded(Item item) {
        if (item instanceof EquipableItem && ((EquipableItem) item).getEquipped()) {
            ((EquipableItem) item).setEquipped(false);
        }
    }

    private void removeGhostQuestProof(GhostQuestState ghostQuest) {
        if (ghostQuest.type == GhostQuestType.ROSE) {
            removeInventoryItem(DriedRose.class);
        }
        else if (ghostQuest.type == GhostQuestType.RAT) {
            removeInventoryItem(RatSkull.class);
        }
    }

    private void removeWandmakerQuestProof(WandmakerQuestState wandmakerQuest) {
        if (wandmakerQuest.type == WandmakerQuestType.BERRY) {
            removeInventoryItem(RotberrySeed.class);
        }
        else if (wandmakerQuest.type == WandmakerQuestType.DUST) {
            removeInventoryItem(CorpseDust.class);
        }
        else if (wandmakerQuest.type == WandmakerQuestType.FISH) {
            removeInventoryItem(PhantomFish.class);
        }
    }

    private boolean canClaimGhostReward(GhostQuestState ghostQuest) {
        if (ghostQuest.type == GhostQuestType.CURSE) {
            return ghostQuest.processed;
        }

        if (ghostQuest.type == GhostQuestType.ROSE) {
            return findInventoryItem(DriedRose.class) != null;
        }

        if (ghostQuest.type == GhostQuestType.RAT) {
            return findInventoryItem(RatSkull.class) != null;
        }

        return false;
    }

    private boolean canClaimWandmakerReward(WandmakerQuestState wandmakerQuest) {
        if (wandmakerQuest.type == WandmakerQuestType.BERRY) {
            return findInventoryItem(RotberrySeed.class) != null;
        }

        if (wandmakerQuest.type == WandmakerQuestType.DUST) {
            return findInventoryItem(CorpseDust.class) != null;
        }

        return findInventoryItem(PhantomFish.class) != null;
    }

    private void spawnGhostNpc(Room room, float x, float y) {
        Ghost ghost = new Ghost();
        ghost.x = x;
        ghost.y = y;
        ghost.floorY = y;
        ghost.setRoom(room.getIdentifier());
        UnitHelper.getInstance().addUnit(ghost);

        GhostQuestState ghostQuest = getGhostQuest();
        ghostQuest.npcId = ghost.getPersistentId();
        ghostQuest.roomId = room.getIdentifier();
    }

    private void spawnWandmakerNpc(Room room, float x, float y) {
        Wandmaker wandmaker = new Wandmaker();
        wandmaker.x = x;
        wandmaker.y = y;
        wandmaker.floorY = y;
        wandmaker.setRoom(room.getIdentifier());
        UnitHelper.getInstance().addUnit(wandmaker);

        WandmakerQuestState wandmakerQuest = getWandmakerQuest();
        wandmakerQuest.npcId = wandmaker.getPersistentId();
        wandmakerQuest.roomId = room.getIdentifier();
    }

    private void spawnImpNpc(Room room, float x, float y) {
        Imp imp = new Imp();
        imp.x = x;
        imp.y = y;
        imp.floorY = y;
        imp.setRoom(room.getIdentifier());
        UnitHelper.getInstance().addUnit(imp);

        ImpQuestState impQuest = getImpQuest();
        impQuest.npcId = imp.getPersistentId();
        impQuest.roomId = room.getIdentifier();
    }

    private void spawnBlacksmithNpc(Room room, float x, float y) {
        Blacksmith blacksmith = new Blacksmith();
        blacksmith.x = x;
        blacksmith.y = y;
        blacksmith.floorY = y;
        blacksmith.setRoom(room.getIdentifier());
        UnitHelper.getInstance().addUnit(blacksmith);

        BlacksmithQuestState blacksmithQuest = getBlacksmithQuest();
        blacksmithQuest.npcId = blacksmith.getPersistentId();
        blacksmithQuest.roomId = room.getIdentifier();
    }

    private void spawnFetidRat() {
        Level level = findLevelByDepth(getGhostQuest().depth);
        Room room = chooseQuestSpawnRoom(level);
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null) {
            return;
        }

        FetidRat fetidRat = new FetidRat();
        fetidRat.x = spawn.x;
        fetidRat.y = spawn.y;
        fetidRat.floorY = spawn.y;
        fetidRat.setRoom(room.getIdentifier());
        UnitHelper.getInstance().addUnit(fetidRat);
        getGhostQuest().uniqueMobId = fetidRat.getPersistentId();
    }

    private void spawnCursePersonification(String roomId, float x, float y) {
        CursePersonification cursePersonification = new CursePersonification();
        cursePersonification.x = x;
        cursePersonification.y = y;
        cursePersonification.floorY = y;
        cursePersonification.setRoom(roomId);
        UnitHelper.getInstance().addUnit(cursePersonification);
        if (UnitHelper.getInstance().getHero() != null) {
            cursePersonification.alert(UnitHelper.getInstance().getHero());
        }
        getGhostQuest().uniqueMobId = cursePersonification.getPersistentId();
    }

    private void spawnWandmakerQuestItem(Level level, WandmakerQuestType questType) {
        if (questType == WandmakerQuestType.BERRY) {
            spawnItemInRoom(level, new RotberrySeed().setQuantity(1));
            return;
        }

        if (questType == WandmakerQuestType.DUST) {
            spawnItemInRoom(level, new CorpseDust());
            return;
        }

        spawnItemInRoom(level, new PhantomFish());
    }

    private void spawnItemInRoom(Level level, Item item) {
        Room room = chooseQuestSpawnRoom(level);
        Door spawn = room != null ? room.getRandomSpawn() : null;
        if (room == null || spawn == null || item == null) {
            return;
        }

        ItemOnScreen itemOnScreen = new ItemOnScreen(item);
        itemOnScreen.x = spawn.x;
        itemOnScreen.y = spawn.y;
        itemOnScreen.floorY = spawn.y;
        itemOnScreen.setRoom(room.getIdentifier());
        UnitHelper.getInstance().addUnit(itemOnScreen);
    }

    private Room chooseQuestSpawnRoom(Level level) {
        if (level == null || level.rooms == null || level.rooms.isEmpty()) {
            return null;
        }

        ArrayList<Room> candidates = new ArrayList<Room>();
        for (Room room : level.rooms) {
            if (room == null || !room.getCanSpawn() || room.getClass() != Room.class
                    || room.getSpawnCandidates().isEmpty()) {
                continue;
            }

            candidates.add(room);
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No supported quest placement on floor " + level.getDepth());
        }

        return candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
    }

    private boolean shouldHaveBlacksmithOreVeins(BlacksmithQuestState blacksmithQuest) {
        return blacksmithQuest != null
                && blacksmithQuest.spawned
                && blacksmithQuest.given
                && !blacksmithQuest.completed
                && !blacksmithQuest.alternative
                && blacksmithQuest.depth > 0;
    }

    private ArrayList<DarkGoldVeinLocation> getBlacksmithOreVeinCandidates(Level level, BlacksmithQuestState blacksmithQuest, boolean strictBlockingCheck) {
        ArrayList<DarkGoldVeinLocation> candidates = new ArrayList<DarkGoldVeinLocation>();
        if (level == null || level.rooms == null) {
            return candidates;
        }

        for (Room room : level.rooms) {
            if (room == null) {
                continue;
            }

            for (String platform : questSupportCells(room)) {
                String[] platformParts = platform.split("_");
                int tileX = Integer.parseInt(platformParts[0]);
                int tileY = Integer.parseInt(platformParts[1]);
                if (isDarkGoldVeinCandidate(room, tileX, tileY, strictBlockingCheck)) {
                    candidates.add(new DarkGoldVeinLocation(room.getIdentifier(), tileX, tileY));
                }
            }
        }

        return candidates;
    }

    private boolean isDarkGoldVeinCandidate(Room room, int tileX, int tileY, boolean strictBlockingCheck) {
        if (room == null) {
            return false;
        }

        float x = tileX * ConstantsHelper.TILE, y = (tileY + 1) * ConstantsHelper.TILE;
        if (!room.hasSupportedPlacement(x, y, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS)
                || room.getLayout().arrivalReserved(x, y, ConstantsHelper.UNIT_DIMENSIONS)) return false;

        if (room.getWaterPlatforms().contains(UtilsHelper.platformKey(tileX, tileY)) || tileY + 1 >= room.getHeight()) {
            return false;
        }

        for (Door door : room.getDoors()) {
            int doorTileX = (int) (door.x / ConstantsHelper.TILE);
            int doorPlatformTileY = Math.max(0, (int) (door.y / ConstantsHelper.TILE) - 1);
            if (doorTileX == tileX && doorPlatformTileY == tileY) {
                return false;
            }
        }

        return !hasDarkGoldVeinAt(room.getIdentifier(), tileX, tileY)
                && !hasTrapAt(room.getIdentifier(), tileX, tileY)
                && (!strictBlockingCheck || !hasBlockingUnitAt(room.getIdentifier(), tileX, tileY));
    }

    private DarkGoldVeinLocation getFallbackBlacksmithOreVeinCandidate(Level level, BlacksmithQuestState blacksmithQuest) {
        if (level == null || level.rooms == null) {
            return null;
        }

        Room preferredRoom = findRoomInLevel(level, blacksmithQuest != null ? blacksmithQuest.roomId : null);
        DarkGoldVeinLocation preferredCandidate = getFallbackBlacksmithOreVeinCandidate(preferredRoom);
        if (preferredCandidate != null) {
            return preferredCandidate;
        }

        for (Room room : level.rooms) {
            DarkGoldVeinLocation candidate = getFallbackBlacksmithOreVeinCandidate(room);
            if (candidate != null) {
                return candidate;
            }
        }

        return null;
    }

    private DarkGoldVeinLocation getFallbackBlacksmithOreVeinCandidate(Room room) {
        if (room == null) {
            return null;
        }

        for (String platform : questSupportCells(room)) {
            String[] platformParts = platform.split("_");
            int tileX = Integer.parseInt(platformParts[0]);
            int tileY = Integer.parseInt(platformParts[1]);
            if (isDarkGoldVeinCandidate(room, tileX, tileY, false)) {
                return new DarkGoldVeinLocation(room.getIdentifier(), tileX, tileY);
            }
        }

        return null;
    }

    private ArrayList<String> questSupportCells(Room room) {
        java.util.TreeSet<String> cells = new java.util.TreeSet<String>(room.getPlatforms());

        for (int x = 1; x < room.getWidth() - 1; x++)
            cells.add(UtilsHelper.platformKey(x, ConstantsHelper.MIN_FLOOR - 1));
        return new ArrayList<String>(cells);
    }

    private Room findRoomInLevel(Level level, String roomId) {
        if (level == null || level.rooms == null || roomId == null) {
            return null;
        }

        for (Room room : level.rooms) {
            if (room != null && roomId.equals(room.getIdentifier())) {
                return room;
            }
        }

        return null;
    }

    private boolean hasBlockingUnitAt(String roomId, int tileX, int tileY) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit == null || unit.getRoom() == null || !unit.getRoom().equals(roomId)) {
                continue;
            }

            if (unit instanceof DarkGoldVein || unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap) {
                continue;
            }

            if (unit.getHP() < 1 || unit.isDead()) {
                continue;
            }

            int unitTileX = (int) ((unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f) / ConstantsHelper.TILE);
            int unitTileY = Math.max(0, (int) (unit.floorY / ConstantsHelper.TILE) - 1);
            if (unitTileX == tileX && unitTileY == tileY) {
                return true;
            }
        }

        return false;
    }

    private boolean hasTrapAt(String roomId, int tileX, int tileY) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap)
                    || unit.getRoom() == null
                    || !unit.getRoom().equals(roomId)) {
                continue;
            }

            int trapTileX = (int) ((unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f) / ConstantsHelper.TILE);
            int trapTileY = Math.max(0, (int) (unit.floorY / ConstantsHelper.TILE) - 1);
            if (trapTileX == tileX && trapTileY == tileY) {
                return true;
            }
        }

        return false;
    }

    private boolean hasDarkGoldVeinAt(String roomId, int tileX, int tileY) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof DarkGoldVein) || unit.getRoom() == null || !unit.getRoom().equals(roomId)) {
                continue;
            }

            int veinTileX = (int) (unit.x / ConstantsHelper.TILE);
            int veinTileY = Math.max(0, (int) (unit.floorY / ConstantsHelper.TILE) - 1);
            if (veinTileX == tileX && veinTileY == tileY) {
                return true;
            }
        }

        return false;
    }

    private void spawnDarkGoldVein(DarkGoldVeinLocation location) {
        DarkGoldVein vein = new DarkGoldVein();
        vein.x = location.tileX * ConstantsHelper.TILE;
        vein.floorY = (location.tileY + 1) * ConstantsHelper.TILE;
        vein.y = vein.floorY;
        vein.setRoom(location.roomId);
        UnitHelper.getInstance().addUnit(vein);
    }

    private DarkGoldVein findDarkGoldVeinAtHero(Hero hero) {
        if (hero == null || hero.getRoom() == null) {
            return null;
        }

        float heroCenterX = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float heroCenterY = hero.floorY - ConstantsHelper.TILE / 2f;
        float searchRadius = ConstantsHelper.TILE * 1.5f;
        DarkGoldVein closestVein = null;
        float closestDistance = Float.MAX_VALUE;

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof DarkGoldVein) || unit.getRoom() == null || !unit.getRoom().equals(hero.getRoom())) {
                continue;
            }

            float veinCenterX = unit.x + ConstantsHelper.TILE / 2f;
            float veinCenterY = unit.floorY - ConstantsHelper.TILE / 2f;
            float horizontalDistance = Math.abs(veinCenterX - heroCenterX);
            float verticalDistance = Math.abs(veinCenterY - heroCenterY);

            if (horizontalDistance > searchRadius || verticalDistance > searchRadius) {
                continue;
            }

            float candidateDistance = horizontalDistance + verticalDistance;
            if (candidateDistance >= closestDistance) {
                continue;
            }

            closestDistance = candidateDistance;
            closestVein = (DarkGoldVein) unit;
        }

        return closestVein;
    }

    private int countBlacksmithOreVeins(Level level) {
        int count = 0;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof DarkGoldVein && isRoomInLevel(level, unit.getRoom())) {
                count++;
            }
        }
        return count;
    }

    private void clearBlacksmithOreVeins(BlacksmithQuestState blacksmithQuest) {
        Level level = findLevelByDepth(blacksmithQuest.depth);
        if (level == null) {
            return;
        }

        ArrayList<Unit> existingUnits = new ArrayList<Unit>(UnitHelper.getInstance().getUnits());
        for (Unit unit : existingUnits) {
            if (unit instanceof DarkGoldVein && isRoomInLevel(level, unit.getRoom())) {
                UnitHelper.getInstance().removeUnit(unit);
            }
        }
    }

    private boolean isRoomInLevel(Level level, String roomId) {
        if (level == null || roomId == null) {
            return false;
        }

        for (Room room : level.rooms) {
            if (room != null && roomId.equals(room.getIdentifier())) {
                return true;
            }
        }

        return false;
    }

    private Level findLevelByDepth(int depth) {
        if (depth < 1 || depth > MapHelper.getInstance().levels.size()) {
            return null;
        }

        return MapHelper.getInstance().levels.get(depth - 1);
    }

    private Room findRoom(String roomId) {
        if (roomId == null) {
            return null;
        }

        for (Level level : MapHelper.getInstance().levels) {
            for (Room room : level.rooms) {
                if (roomId.equals(room.getIdentifier())) {
                    return room;
                }
            }
        }

        return null;
    }

    private Item findInventoryItem(Class<? extends Item> itemClass) {
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (itemClass.isInstance(item)) {
                return item;
            }
        }

        return null;
    }

    private void removeInventoryItem(Class<? extends Item> itemClass) {
        Item item = findInventoryItem(itemClass);
        if (item != null) {
            InventoryHelper.getInstance().removeItem(item);
        }
    }

    private void consumeInventoryQuantity(Class<? extends Item> itemClass, int amount) {
        if (amount <= 0) {
            return;
        }

        ArrayList<Item> items = new ArrayList<Item>(InventoryHelper.getInstance().getItems());
        for (Item item : items) {
            if (amount <= 0 || !itemClass.isInstance(item)) {
                continue;
            }

            int stackAmount = item.getQuantity() > 0 ? item.getQuantity() : 1;
            if (stackAmount <= amount) {
                amount -= stackAmount;
                InventoryHelper.getInstance().removeItem(item);
                continue;
            }

            item.setQuantity(stackAmount - amount);
            amount = 0;
        }

        WindowHelper.getInstance().refresh();
    }

    private int getInventoryCount(Class<? extends Item> itemClass) {
        int total = 0;
        for (Item item : InventoryHelper.getInstance().getItems()) {
            if (!itemClass.isInstance(item)) {
                continue;
            }

            total += item.getQuantity() > 0 ? item.getQuantity() : 1;
        }

        return total;
    }

    private GhostQuestType randomGhostQuestType() {
        int roll = RandomHelper.getInstance().randomInt(3);
        if (roll == 0) {
            return GhostQuestType.ROSE;
        }

        if (roll == 1) {
            return GhostQuestType.RAT;
        }

        return GhostQuestType.CURSE;
    }

    private WandmakerQuestType randomWandmakerQuestType() {
        int roll = RandomHelper.getInstance().randomInt(3);
        if (roll == 0) {
            return WandmakerQuestType.BERRY;
        }

        if (roll == 1) {
            return WandmakerQuestType.DUST;
        }

        return WandmakerQuestType.FISH;
    }

    private ImpQuestType randomImpQuestType() {
        return RandomHelper.getInstance().randomBoolean() ? ImpQuestType.GOLEM : ImpQuestType.MONK;
    }

    private RewardItemData generateGhostWeaponReward() {
        MeleeWeapon bestWeapon = null;
        for (int roll = 0; roll < 4; roll++) {
            MeleeWeapon candidate = createRandomGhostWeapon();
            if (candidate == null) {
                continue;
            }

            if (bestWeapon == null || candidate.getTier() > bestWeapon.getTier()) {
                bestWeapon = candidate;
            }
        }

        if (bestWeapon == null) {
            bestWeapon = new ShortSword();
        }


        return createRewardItemData(InventoryHelper.getInstance().chooseMercenaryGun(bestWeapon, MapHelper.getInstance().getDepth(), null));
    }

    private RewardItemData generateGhostArmorReward() {
        Armor bestArmor = null;
        for (int roll = 0; roll < 4; roll++) {
            Armor candidate = createRandomGhostArmor();
            if (candidate == null) {
                continue;
            }

            if (bestArmor == null || candidate.getTier() > bestArmor.getTier()) {
                bestArmor = candidate;
            }
        }

        if (bestArmor == null) {
            bestArmor = new Cloth();
        }

        return createRewardItemData(bestArmor);
    }

    private RewardItemData generateBattleWandReward() {
        ArrayList<Class<? extends Wand>> wandClasses = new ArrayList<Class<? extends Wand>>();
        wandClasses.add(WandOfAvalanche.class);
        wandClasses.add(WandOfDisintegration.class);
        wandClasses.add(FireBoltWand.class);
        wandClasses.add(WandOfLightning.class);
        wandClasses.add(WandOfPoison.class);
        Wand wand = createItemInstance(wandClasses.get(RandomHelper.getInstance().randomInt(wandClasses.size())));
        if (wand == null) {
            wand = new FireBoltWand();
        }
        wand.setLevel(2);
        return createRewardItemData(wand);
    }

    private RewardItemData generateUtilityWandReward() {
        ArrayList<Class<? extends Wand>> wandClasses = new ArrayList<Class<? extends Wand>>();
        wandClasses.add(WandOfAmok.class);
        wandClasses.add(WandOfBlink.class);
        wandClasses.add(WandOfRegrowth.class);
        wandClasses.add(WandOfSlowness.class);
        wandClasses.add(WandOfReach.class);
        Wand wand = createItemInstance(wandClasses.get(RandomHelper.getInstance().randomInt(wandClasses.size())));
        if (wand == null) {
            wand = new WandOfReach();
        }
        wand.setLevel(2);
        return createRewardItemData(wand);
    }

    private RewardItemData generateImpRingReward() {
        ArrayList<Class<? extends Ring>> ringClasses = new ArrayList<Class<? extends Ring>>();
        ringClasses.add(RingOfAccuracy.class);
        ringClasses.add(RingOfDetection.class);
        ringClasses.add(RingOfElements.class);
        ringClasses.add(RingOfEvasion.class);
        ringClasses.add(RingOfHaggler.class);
        ringClasses.add(RingOfHaste.class);
        ringClasses.add(RingOfHerbalism.class);
        ringClasses.add(RingOfMending.class);
        ringClasses.add(RingOfPower.class);
        ringClasses.add(RingOfSatiety.class);
        ringClasses.add(RingOfShadows.class);
        ringClasses.add(RingOfThorns.class);
        Ring ring = createItemInstance(ringClasses.get(RandomHelper.getInstance().randomInt(ringClasses.size())));
        if (ring == null) {
            ring = new RingOfHaste();
        }
        ring.setLevel(3);
        return createRewardItemData(ring);
    }

    private MeleeWeapon createRandomGhostWeapon() {
        ArrayList<Class<? extends MeleeWeapon>> weaponClasses = new ArrayList<Class<? extends MeleeWeapon>>();
        weaponClasses.add(Axe.class);
        weaponClasses.add(Dagger.class);
        weaponClasses.add(Glaive.class);
        weaponClasses.add(Hammer.class);
        weaponClasses.add(Knuckles.class);
        weaponClasses.add(LongSword.class);
        weaponClasses.add(Mace.class);
        weaponClasses.add(Rod.class);
        weaponClasses.add(ShortSword.class);
        weaponClasses.add(Spear.class);
        weaponClasses.add(Sword.class);
        InventoryHelper.getInstance().applyDepthEquipmentWeights(weaponClasses, MapHelper.getInstance().getDepth());
        return createItemInstance(weaponClasses.get(RandomHelper.getInstance().randomInt(weaponClasses.size())));
    }

    private Armor createRandomGhostArmor() {
        ArrayList<Class<? extends Armor>> armorClasses = new ArrayList<Class<? extends Armor>>();
        armorClasses.add(Cloth.class);
        armorClasses.add(LeatherArmor.class);
        armorClasses.add(MailArmor.class);
        armorClasses.add(PlateArmor.class);
        armorClasses.add(ScaleArmor.class);
        InventoryHelper.getInstance().applyDepthEquipmentWeights(armorClasses, MapHelper.getInstance().getDepth());
        return createItemInstance(armorClasses.get(RandomHelper.getInstance().randomInt(armorClasses.size())));
    }

    private RewardItemData createRewardItemData(Item item) {
        if (item == null) {
            return null;
        }

        RewardItemData rewardItemData = new RewardItemData();
        rewardItemData.className = SaveRegistry.getItemId(item);
        rewardItemData.level = item.getLevel();
        rewardItemData.identified = true;

        if (item instanceof Weapon) {
            Prefix prefix = ((Weapon) item).getPrefix();
            rewardItemData.prefixClassName = SaveRegistry.getPrefixId(prefix);
        }
        else if (item instanceof Armor) {
            Prefix prefix = ((Armor) item).getPrefix();
            rewardItemData.prefixClassName = SaveRegistry.getPrefixId(prefix);
        }

        return rewardItemData;
    }

    private Item createRewardItem(RewardItemData rewardItemData) {
        if (rewardItemData == null || rewardItemData.className == null) {
            return null;
        }

        Item item = SaveRegistry.createItem(rewardItemData.className);
        if (item == null) {
            return null;
        }

        if (item.supportsLevel()) {
            item.setLevel(rewardItemData.level);
        }

        Prefix prefix = createPrefix(rewardItemData.prefixClassName);
        if (item instanceof Weapon) {
            ((Weapon) item).setPrefix(prefix);
        }
        else if (item instanceof Armor) {
            ((Armor) item).setPrefix(prefix);
        }

        if (rewardItemData.identified) {
            item.identify();
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private <T extends Item> T createItemInstance(Class<? extends T> itemClass) {
        try {
            return (T) itemClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private Prefix createPrefix(String prefixClassName) {
        if (prefixClassName == null || prefixClassName.isEmpty()) {
            return null;
        }

        return SaveRegistry.createPrefix(prefixClassName);
    }

    private String getGhostRequestText(GhostQuestState ghostQuest) {
        if (ghostQuest.type == GhostQuestType.ROSE) {
            return "Hello adventurer... Once I was like you - strong and confident... And now I'm dead... But I can't leave this place... Not until I have my dried rose... It's very important to me... Some monster stole it from my body...";
        }

        if (ghostQuest.type == GhostQuestType.RAT) {
            return "Hello adventurer... Once I was like you - strong and confident... And now I'm dead... But I can't leave this place... Not until I have my revenge... Slay the fetid rat that has taken my life...";
        }

        return "Hello adventurer... Once I was like you - strong and confident... And now I'm dead... But I can't leave this place, as I am bound by a horrid curse... Please... Help me... Destroy the curse...";
    }

    private String getGhostReminderText(GhostQuestState ghostQuest) {
        if (ghostQuest.type == GhostQuestType.ROSE) {
            return "Please... Help me... Find the rose...";
        }

        if (ghostQuest.type == GhostQuestType.RAT) {
            return "Please... Help me... Slay the abomination...";
        }

        return "The curse still clings to this place... Please... Destroy it...";
    }

    private String getGhostCompletionText(GhostQuestState ghostQuest) {
        if (ghostQuest.type == GhostQuestType.ROSE) {
            return "Yes! Yes!!! This is it! Please give it to me! And you can take one of these items, maybe they will be useful to you in your journey...";
        }

        if (ghostQuest.type == GhostQuestType.RAT) {
            return "Yes! The ugly creature is slain and I can finally rest... Please take one of these items, maybe they will be useful to you in your journey...";
        }

        return "Thank you! The curse is broken and I can finally rest... Please take one of these items, maybe they will be useful to you in your journey...";
    }

    private String getWandmakerRequestText(WandmakerQuestState wandmakerQuest) {
        if (wandmakerQuest.type == WandmakerQuestType.BERRY) {
            return "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient - a Rotberry seed. Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands.";
        }

        if (wandmakerQuest.type == WandmakerQuestType.DUST) {
            return "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient - corpse dust. It can be gathered from skeletal remains and there is an ample number of them in the dungeon. Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands.";
        }

        return "Oh, what a pleasant surprise to meet a decent person in such place! I came here for a rare ingredient: a phantom fish. You can catch it with your bare hands, but it's very hard to notice in the water. Being a magic user, I'm quite able to defend myself against local monsters, but I'm getting lost in no time, it's very embarrassing. Probably you could help me? I would be happy to pay for your service with one of my best wands.";
    }

    private String getWandmakerReminderText(WandmakerQuestState wandmakerQuest) {
        if (wandmakerQuest.type == WandmakerQuestType.BERRY) {
            return "Any luck with a Rotberry seed? No? Don't worry, I'm not in a hurry.";
        }

        if (wandmakerQuest.type == WandmakerQuestType.DUST) {
            return "Any luck with corpse dust? Bone piles are the most obvious places to look.";
        }

        return "Any luck with a phantom fish? You may want to search the lower prison pools and storerooms.";
    }

    private String getWandmakerCompletionText() {
        return "Oh, I see you have succeeded! I do hope it hasn't troubled you too much. As I promised, you can choose one of my high quality wands.";
    }

    private String getImpRequestText(ImpQuestState impQuest) {
        if (impQuest.type == ImpQuestType.GOLEM) {
            return "Are you an adventurer? I love adventurers! You can always rely on them if something needs to be killed. In my case this is golems who need to be killed. Please, kill 6 of them and a reward is yours.";
        }

        return "Are you an adventurer? I love adventurers! You can always rely on them if something needs to be killed. In my case this is monks who need to be killed. Please, kill 8 of them and bring me their tokens.";
    }

    private String getImpReminderText(ImpQuestState impQuest) {
        if (impQuest.type == ImpQuestType.GOLEM) {
            return "How is your golem safari going?";
        }

        return "Oh, you are still alive! Just don't forget to grab those monks' tokens.";
    }

    private String getImpCompletionText() {
        return "Oh yes! You are my hero! Regarding your reward, I don't have cash with me right now, but I have something better for you. This is my family heirloom ring.";
    }

    private String getBlacksmithRequestText(BlacksmithQuestState blacksmithQuest) {
        if (blacksmithQuest.alternative) {
            return "Hey human! Wanna be useful, eh? Take dis pickaxe and kill a bat wit' it, I need its blood on the head. What do you mean, how am I gonna pay? You greedy... Ok, ok, I don't have money to pay, but I can do some smithin' for you. Consider yourself lucky, I'm the only blacksmith around.";
        }

        return "Hey human! Wanna be useful, eh? Take dis pickaxe and mine me some dark gold ore, 15 pieces should be enough. What do you mean, how am I gonna pay? You greedy... Ok, ok, I don't have money to pay, but I can do some smithin' for you. Consider yourself lucky, I'm the only blacksmith around.";
    }

    private String getBlacksmithMissingPickaxeText() {
        return "Are you kiddin' me? Where is my pickaxe?!";
    }

    private String getBlacksmithReminderText(BlacksmithQuestState blacksmithQuest) {
        if (blacksmithQuest.alternative) {
            return "I said I need bat blood on the pickaxe. Chop chop!";
        }

        return "Dark gold ore. 15 pieces. Seriously, is it dat hard?";
    }

    private String getBlacksmithCompletionText() {
        return "Oh, you have returned... Better late dan never.";
    }

    private String getBlacksmithBusyText() {
        return "I'm busy. Get lost!";
    }

    private String getBlacksmithReforgeText() {
        return "Ok, a deal is a deal, dat's what I can do for you: I can reforge 2 items and turn them into one of a better quality.";
    }

    private String getBlacksmithSelectItemText() {
        return "Select an item to reforge";
    }

    private void refreshContextButtons() {
        if (UnitHelper.getInstance().getHero() == null) {
            return;
        }

        MapHelper.getInstance().refreshHeroEnvironment();
    }

    private void normalize() {
        if (questSaveData == null) {
            questSaveData = new QuestSaveData();
        }

        if (questSaveData.ghostQuest == null) {
            questSaveData.ghostQuest = new GhostQuestState();
        }

        if (questSaveData.wandmakerQuest == null) {
            questSaveData.wandmakerQuest = new WandmakerQuestState();
        }

        if (questSaveData.blacksmithQuest == null) {
            questSaveData.blacksmithQuest = new BlacksmithQuestState();
        }

        if (questSaveData.impQuest == null) {
            questSaveData.impQuest = new ImpQuestState();
        }

        if (questSaveData.ghostQuest.type == null) {
            questSaveData.ghostQuest.type = GhostQuestType.NONE;
        }

        if (questSaveData.wandmakerQuest.type == null) {
            questSaveData.wandmakerQuest.type = WandmakerQuestType.NONE;
        }

        if (questSaveData.impQuest.type == null) {
            questSaveData.impQuest.type = ImpQuestType.NONE;
        }
    }

    public static class QuestSaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        public GhostQuestState ghostQuest = new GhostQuestState();
        public WandmakerQuestState wandmakerQuest = new WandmakerQuestState();
        public BlacksmithQuestState blacksmithQuest = new BlacksmithQuestState();
        public ImpQuestState impQuest = new ImpQuestState();
    }

    public static class RewardItemData implements Serializable {
        private static final long serialVersionUID = 1L;

        public String className;
        public String prefixClassName;
        public int level = 1;
        public boolean identified = true;
    }

    public static class GhostQuestState implements Serializable {
        private static final long serialVersionUID = 1L;

        public GhostQuestType type = GhostQuestType.NONE;
        public boolean spawned;
        public boolean given;
        public boolean processed;
        public boolean completed;
        public int depth;
        public int leftToKill;
        public String npcId;
        public String roomId;
        public String uniqueMobId;
        public RewardItemData weaponReward;
        public RewardItemData armorReward;
    }

    public static class WandmakerQuestState implements Serializable {
        private static final long serialVersionUID = 1L;

        public WandmakerQuestType type = WandmakerQuestType.NONE;
        public boolean spawned;
        public boolean given;
        public boolean completed;
        public int depth;
        public String npcId;
        public String roomId;
        public RewardItemData battleWandReward;
        public RewardItemData utilityWandReward;
    }

    public static class BlacksmithQuestState implements Serializable {
        private static final long serialVersionUID = 1L;

        public boolean spawned;
        public boolean alternative;
        public boolean given;
        public boolean completed;
        public boolean reforged;
        public boolean darkGoldVeinsSpawned;
        public int depth;
        public String npcId;
        public String roomId;
    }

    private static final class DarkGoldVeinLocation {
        private final String roomId;
        private final int tileX;
        private final int tileY;

        private DarkGoldVeinLocation(String roomId, int tileX, int tileY) {
            this.roomId = roomId;
            this.tileX = tileX;
            this.tileY = tileY;
        }

        private String sortKey() {
            return roomId + '_' + tileX + '_' + tileY;
        }
    }

    public static class ImpQuestState implements Serializable {
        private static final long serialVersionUID = 1L;

        public ImpQuestType type = ImpQuestType.NONE;
        public boolean spawned;
        public boolean given;
        public boolean completed;
        public int depth;
        public int goalCount;
        public String npcId;
        public String roomId;
        public RewardItemData ringReward;
    }
}
