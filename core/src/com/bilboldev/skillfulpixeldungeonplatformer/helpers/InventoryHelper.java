package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.math.RandomXS128;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Key;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Treasure;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Cloth;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.LeatherArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.MailArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.PlateArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.ScaleArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.Prefix;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Blessed;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Axe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Dagger;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Glaive;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Hammer;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Knuckles;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.LongSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Mace;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Rod;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.ShortSword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Spear;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.Sword;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.RangedWeapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Guided;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Lots;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Poor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.weapons.ranged.Some;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;

import java.util.ArrayList;

public class InventoryHelper {

    private static final int MERCHANT_STOCK_SIZE = 14;
    private static final int MERCHANT_FIXED_STOCK_SIZE = 6;
    private static final int MERCHANT_ROLLED_STOCK_SIZE = MERCHANT_STOCK_SIZE - MERCHANT_FIXED_STOCK_SIZE;
    private static final int GOLD_DROP_WEIGHT = 7;
    private static final int GOLD_DROP_RATE_REDUCTION_PERCENT = 20;
    private static final int WEAPON_DROP_RATE_REDUCTION_PERCENT = 20;

    private final int INVENTORY_SIZE = 16;
    private int gold;
    private ArrayList<Item> items;
    private static final InventoryHelper ourInstance = new InventoryHelper();

    public static InventoryHelper getInstance() {
        return ourInstance;
    }

    private InventoryHelper() {
        init();
    }

    private void init(){
        items = new ArrayList<>();

        boolean startingMeleeWeaponFound = false;

        for(Item item : UnitHelper.getInstance().getHero().getHeroClass().getItems()){
            if(item instanceof MeleeWeapon){
                startingMeleeWeaponFound = true;
                ((MeleeWeapon)item).setEquipped(true, false);
            }

            if(item instanceof Armor){
                ((Armor)item).setEquipped(true, false);
            }

            if(item instanceof RangedWeapon && !(item instanceof Potion) && !(item instanceof Seed)){
                ((RangedWeapon)item).setEquipped(true, false);
            }
            addItem(item);
        }

        if (!startingMeleeWeaponFound) {
            UnitHelper.getInstance().getHero().setWeapon((MeleeAttack) new MeleeAttack().setOwner(UnitHelper.getInstance().getHero()));
        }

        refreshInventoryUi();
        gold = 0;
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public void reset() {
        if (UnitHelper.getInstance().getHero() == null) {
            items = new ArrayList<>();
            gold = 0;
            return;
        }

        init();
    }

    public boolean addItem(Item item){
        if(item instanceof Treasure && !(item instanceof AmuletOfYendor)){
            return true;
        }

        if (mergeStackableRangedWeapon(item)) {
            return true;
        }

        if(item.getQuantity() > 0){
            for(Item item1 : items){
                if(item1.getClass() == item.getClass()){
                    item1.setQuantity(item1.getQuantity() + item.getQuantity());
                    AchievementManager.getInstance().onItemObtained(item1);
                    refreshInventoryUi();
                    return true;
                }
            }
        }

        if(this.items.size() < INVENTORY_SIZE){
            this.items.add(item);
            AchievementManager.getInstance().onItemObtained(item);
            refreshInventoryUi();
        }
        else {
            WindowHelper.getInstance().addWindow(new TextWindow(1000,100,"Backpack is full.").build());
            return false;
        }

        return true;
    }

    public boolean canAddItem(Item item) {
        if(item instanceof Treasure && !(item instanceof AmuletOfYendor)){
            return true;
        }

        if (canMergeStackableRangedWeapon(item)) {
            return true;
        }

        if(item.getQuantity() > 0){
            for(Item item1 : items){
                if(item1.getClass() == item.getClass()){
                    return true;
                }
            }
        }

        return this.items.size() < INVENTORY_SIZE;
    }

    private boolean mergeStackableRangedWeapon(Item item) {
        if (!(item instanceof RangedWeapon)) {
            return false;
        }

        RangedWeapon incoming = (RangedWeapon) item;
        if (!incoming.canStackAmmoInInventory()) {
            return false;
        }

        for (Item existingItem : items) {
            if (!canStackRangedWeapon(existingItem, incoming)) {
                continue;
            }

            RangedWeapon existingWeapon = (RangedWeapon) existingItem;
            existingWeapon.setAmmo(existingWeapon.getAmmo() + incoming.getAmmo());
            AchievementManager.getInstance().onItemObtained(existingWeapon);
            refreshInventoryUi();
            return true;
        }

        return false;
    }

    private boolean canMergeStackableRangedWeapon(Item item) {
        if (!(item instanceof RangedWeapon)) {
            return false;
        }

        RangedWeapon incoming = (RangedWeapon) item;
        if (!incoming.canStackAmmoInInventory()) {
            return false;
        }

        for (Item existingItem : items) {
            if (canStackRangedWeapon(existingItem, incoming)) {
                return true;
            }
        }

        return false;
    }

    private boolean canStackRangedWeapon(Item existingItem, RangedWeapon incomingWeapon) {
        if (!(existingItem instanceof RangedWeapon) || existingItem.getClass() != incomingWeapon.getClass()) {
            return false;
        }

        RangedWeapon existingWeapon = (RangedWeapon) existingItem;
        return existingWeapon.canStackAmmoInInventory()
                && existingWeapon.getLevel() == incomingWeapon.getLevel()
                && samePrefix(existingWeapon.getPrefix(), incomingWeapon.getPrefix());
    }

    private boolean samePrefix(Prefix firstPrefix, Prefix secondPrefix) {
        if (firstPrefix == null || secondPrefix == null) {
            return firstPrefix == secondPrefix;
        }

        return firstPrefix.getClass().equals(secondPrefix.getClass());
    }

    public boolean hasItem(Class<? extends Item> itemClass) {
        for (Item item : items) {
            if (itemClass.isInstance(item)) {
                return true;
            }
        }

        return false;
    }

    public boolean dropItem(Item item){
        if (item instanceof RangedWeapon && UnitHelper.getInstance().getHero().getRangedWeapon() == item) {
            ((RangedWeapon) item).setEquipped(false, false);
        }

        item.drop(UnitHelper.getInstance().getHeroX(), UnitHelper.getInstance().getHero().y);
        items.remove(item);
        refreshInventoryUi();
        return true;
    }

    public void removeItem(Item item){
        items.remove(item);
        refreshInventoryUi();
    }

    public boolean consumeHealthPotion() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof HealthPotion) {
                ((HealthPotion) item).consume();
                return true;
            }
        }

        return false;
    }

    public boolean consumeManaPotion() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof ManaPotion) {
                ((ManaPotion) item).consume();
                return true;
            }
        }

        return false;
    }

    public boolean consumeRations() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof Rations) {
                ((Rations) item).consume();
                return true;
            }
        }

        return false;
    }

    public int getHealthPotionCount() {
        return getItemQuantity(HealthPotion.class);
    }

    public int getManaPotionCount() {
        return getItemQuantity(ManaPotion.class);
    }

    public int getRationsCount() {
        return getItemQuantity(Rations.class);
    }

    public int getArrowCount() {
        return getItemQuantity(ArrowItem.class);
    }

    public int getKeyCount() {
        return getItemQuantity(Key.class);
    }

    public boolean consumeKey() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!(item instanceof Key)) {
                continue;
            }

            item.setQuantity(item.getQuantity() - 1);
            if (item.getQuantity() < 1) {
                items.remove(i);
            }
            refreshInventoryUi();
            return true;
        }

        refreshInventoryUi();
        return false;
    }

    public boolean consumeArrows(int amount) {
        if (amount <= 0) {
            return true;
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!(item instanceof ArrowItem)) {
                continue;
            }

            item.setQuantity(item.getQuantity() - amount);
            if (item.getQuantity() < 1) {
                items.remove(i);
            }
            refreshInventoryUi();
            return true;
        }

        refreshInventoryUi();
        return false;
    }

    public int getGold(){
        return gold;
    }

    public int getMerchantBuyPrice(int basePrice) {
        return RingOfHaggler.adjustBuyPrice(basePrice);
    }

    public int getSellPrice(Item item) {
        if (item == null) {
            return 0;
        }

        return RingOfHaggler.adjustSellPrice(item.getGoldCost() / 3);
    }

    public void modifyGold(int modification){
        gold += modification;
        AchievementManager.getInstance().onGoldCollected(modification);
    }

    private int getItemQuantity(Class<? extends Item> itemClass) {
        for (Item item : items) {
            if (itemClass.isInstance(item)) {
                return Math.max(0, item.getQuantity());
            }
        }

        return 0;
    }

    private void refreshInventoryUi() {
        UIHelper.getInstance().setInventoryString(items.size() + " / " + INVENTORY_SIZE);
        UIHelper.getInstance().updateRangedButton();
        UIHelper.getInstance().updateTouchConsumableButtons(
                getHealthPotionCount(),
                getManaPotionCount(),
                getRationsCount());
    }

    public void spawnNaturalCompanionItems(Item item, float x, float y, float floorY, String roomIdentifier) {
        Item companionItem = buildNaturalCompanionItem(item);
        if (companionItem == null) {
            return;
        }

        ItemOnScreen companionItemOnScreen = new ItemOnScreen(companionItem);
        companionItemOnScreen.x = x;
        companionItemOnScreen.y = y;
        companionItemOnScreen.floorY = floorY;
        companionItemOnScreen.setRoom(roomIdentifier);
        UnitHelper.getInstance().addUnit(companionItemOnScreen);
    }

    private Item buildNaturalCompanionItem(Item item) {
        if (item instanceof Bow) {
            return new ArrowItem().setQuantity(10);
        }

        return null;
    }

    public Item getRandomDrop(int depth){
        return getRandomDrop(depth, null, true);
    }

    public Item getRandomDrop(int depth, RandomXS128 random, boolean includeGold){
       try {
           Class<? extends Item> selectedItemClass = getRandomDropClass(depth, random, includeGold, true);
           selectedItemClass = applyDropRateReductions(selectedItemClass, depth, random, includeGold);
           Item item = selectedItemClass.newInstance();
           applyDropStylePrefix(item, depth, random);
           return item;
       }
       catch (Exception e){
           return includeGold ? new Gold() : new HealthPotion();
       }
    }

    private Class<? extends Item> applyDropRateReductions(Class<? extends Item> itemClass, int depth, RandomXS128 random, boolean includeGold) {
        Class<? extends Item> adjustedItemClass = itemClass;

        while (true) {
            if (includeGold && adjustedItemClass == Gold.class && randomChance(random, GOLD_DROP_RATE_REDUCTION_PERCENT)) {
                adjustedItemClass = getRandomDropClass(depth, random, false, true);
                continue;
            }

            if (isWeaponDropClass(adjustedItemClass) && randomChance(random, WEAPON_DROP_RATE_REDUCTION_PERCENT)) {
                adjustedItemClass = getRandomDropClass(depth, random, includeGold, false);
                continue;
            }

            return adjustedItemClass;
        }
    }

    private boolean isWeaponDropClass(Class<? extends Item> itemClass) {
        return itemClass != null && Weapon.class.isAssignableFrom(itemClass);
    }

    private Class<? extends Item> getRandomDropClass(int depth, RandomXS128 random, boolean includeGold, boolean includeWeapons) {
        ArrayList<Class<? extends Item>> candidateItems = buildRandomDropCandidates(depth, includeGold, includeWeapons);
        return candidateItems.get(nextInt(random, candidateItems.size()));
    }

    private ArrayList<Class<? extends Item>> buildRandomDropCandidates(int depth, boolean includeGold, boolean includeWeapons) {
        ArrayList<Class<? extends Item>> candidateItems = new ArrayList<>();

        if (includeGold) {
            addWeightedCandidate(candidateItems, Gold.class, GOLD_DROP_WEIGHT);
        }

        // consumables
        addWeightedCandidate(candidateItems, HealthPotion.class, 6);
        addWeightedCandidate(candidateItems, ManaPotion.class, 4);
        if (depth >= 2) {
            addWeightedCandidate(candidateItems, PotionOfExperience.class, 1);
            addWeightedCandidate(candidateItems, PotionOfMindVision.class, 2);
            addWeightedCandidate(candidateItems, PotionOfLevitation.class, 2);
            addWeightedCandidate(candidateItems, PotionOfInvisibility.class, 2);
            addWeightedCandidate(candidateItems, PotionOfLiquidFlame.class, 3);
            addWeightedCandidate(candidateItems, PotionOfToxicGas.class, 3);
            addWeightedCandidate(candidateItems, PotionOfPurity.class, 2);
        }
        if (depth >= 3) {
            addWeightedCandidate(candidateItems, PotionOfStrength.class, 1);
            addWeightedCandidate(candidateItems, PotionOfFrost.class, 2);
            addWeightedCandidate(candidateItems, PotionOfParalyticGas.class, 2);
        }
        if (depth >= 4) {
            addWeightedCandidate(candidateItems, PotionOfMight.class, 1);
        }
        candidateItems.add(Rations.class);

        if (depth >= 2) {
            candidateItems.add(ScrollOfIdentify.class);
            candidateItems.add(ScrollOfTeleportation.class);
            candidateItems.add(ScrollOfRemoveCurse.class);
            candidateItems.add(ScrollOfMagicMapping.class);
            candidateItems.add(ScrollOfRecharging.class);
            candidateItems.add(ScrollOfRefuge.class);
        }
        if (depth >= 2) {
            candidateItems.add(ScrollOfSkill.class);
            candidateItems.add(ScrollOfReadiness.class);
            candidateItems.add(ScrollOfEnchantment.class);
        }
        if (depth >= 3) {
            candidateItems.add(ScrollOfChallenge.class);
            candidateItems.add(ScrollOfLullaby.class);
            candidateItems.add(ScrollOfMirrorImage.class);
            addWeightedCandidate(candidateItems, ScrollOfUpgrade.class, 6);
        }
        if (depth >= 4) {
            candidateItems.add(ScrollOfSacrifice.class);
            candidateItems.add(ScrollOfFrost.class);
            candidateItems.add(ScrollOfTerror.class);
            candidateItems.add(ScrollOfPsionicBlast.class);
        }
        if (depth >= 5) {
            candidateItems.add(ScrollOfBloodyRitual.class);
        }
        if (depth >= 6) {
            candidateItems.add(ScrollOfWipeOut.class);
        }

        // armor
        candidateItems.add(Cloth.class);
        candidateItems.add(LeatherArmor.class);
        candidateItems.add(MailArmor.class);
        candidateItems.add(PlateArmor.class);
        candidateItems.add(ScaleArmor.class);

        if (includeWeapons) {
            // ranged
            candidateItems.add(ArrowItem.class);
            candidateItems.add(ThrowDart.class);
            candidateItems.add(Shuriken.class);
            if (depth >= 3) {
                candidateItems.add(CurareDart.class);
                candidateItems.add(Bow.class);
            }
            if (depth >= 4) {
                candidateItems.add(Javelin.class);
            }
            if (depth >= 5) {
                candidateItems.add(Tomahawk.class);
                candidateItems.add(FrostBow.class);
            }
            if (depth >= 7) {
                candidateItems.add(FlameBow.class);
            }

            // melee
            candidateItems.add(Axe.class);
            candidateItems.add(Dagger.class);
            candidateItems.add(Glaive.class);
            candidateItems.add(Hammer.class);
            candidateItems.add(Knuckles.class);
            candidateItems.add(LongSword.class);
            candidateItems.add(Mace.class);
            candidateItems.add(Rod.class);
            candidateItems.add(ShortSword.class);
            candidateItems.add(Spear.class);
            candidateItems.add(Sword.class);

            // wands
            addWeightedCandidate(candidateItems, FireBoltWand.class, 8);
            if (depth >= 3) {
                addWeightedCandidate(candidateItems, WandOfAmok.class, 3);
                addWeightedCandidate(candidateItems, WandOfBlink.class, 3);
                addWeightedCandidate(candidateItems, MagicMissileWand.class, 4);
                addWeightedCandidate(candidateItems, WandOfReach.class, 3);
            }
            if (depth >= 4) {
                addWeightedCandidate(candidateItems, WandOfPoison.class, 3);
                addWeightedCandidate(candidateItems, WandOfRegrowth.class, 3);
            }
            if (depth >= 5) {
                addWeightedCandidate(candidateItems, FireBallWand.class, 6);
                addWeightedCandidate(candidateItems, WandOfFlock.class, 2);
                addWeightedCandidate(candidateItems, WandOfSlowness.class, 3);
                addWeightedCandidate(candidateItems, WandOfTeleportation.class, 2);
            }
            if (depth >= 6) {
                addWeightedCandidate(candidateItems, WandOfLightning.class, 4);
                addWeightedCandidate(candidateItems, WandOfAvalanche.class, 2);
            }
            if (depth >= 7) {
                addWeightedCandidate(candidateItems, WandOfDisintegration.class, 3);
                addWeightedCandidate(candidateItems, IncinerationWand.class, 2);
            }
        }

        if (depth >= 3) {
            candidateItems.add(RingOfDetection.class);
            candidateItems.add(RingOfHerbalism.class);
            candidateItems.add(RingOfHaste.class);
            candidateItems.add(RingOfMending.class);
        }

        if (depth >= 4) {
            candidateItems.add(RingOfPower.class);
            candidateItems.add(RingOfSatiety.class);
        }

        if (depth >= 5) {
            candidateItems.add(RingOfAccuracy.class);
            candidateItems.add(RingOfEvasion.class);
            candidateItems.add(RingOfElements.class);
            candidateItems.add(RingOfShadows.class);
        }

        if (depth >= 7) {
            candidateItems.add(RingOfHaggler.class);
            candidateItems.add(RingOfThorns.class);
            candidateItems.add(Gemstone.class);
        }

        return candidateItems;
    }

    public Item getRandomRoomScatterItem(int depth, RandomXS128 random) {
        if (randomChance(random, 15)) {
            return getRandomSeed(random);
        }

        return getRandomDrop(depth, random, false);
    }

    private Seed getRandomSeed(RandomXS128 random) {
        try {
            ArrayList<Class<? extends Seed>> candidateSeeds = new ArrayList<Class<? extends Seed>>();
            candidateSeeds.add(DreamweedSeed.class);
            candidateSeeds.add(EarthrootSeed.class);
            candidateSeeds.add(FadeleafSeed.class);
            candidateSeeds.add(FirebloomSeed.class);
            candidateSeeds.add(IcecapSeed.class);
            candidateSeeds.add(RotberrySeed.class);
            candidateSeeds.add(SorrowmossSeed.class);
            candidateSeeds.add(SungrassSeed.class);

            Seed seed = candidateSeeds.get(nextInt(random, candidateSeeds.size())).newInstance();
            seed.setQuantity(1 + nextInt(random, 2));
            return seed;
        }
        catch (Exception ignored) {
            return new SungrassSeed();
        }
    }

    public Item getRandomTreasure(int depth){
        try {
            ArrayList<Class<? extends Item>> candidateItems = new ArrayList<>();

            // armor
            candidateItems.add(LeatherArmor.class);
            candidateItems.add(MailArmor.class);
            candidateItems.add(PlateArmor.class);
            candidateItems.add(ScaleArmor.class);

            // melee
            candidateItems.add(Axe.class);
            candidateItems.add(Glaive.class);
            candidateItems.add(Hammer.class);
            candidateItems.add(LongSword.class);
            candidateItems.add(Mace.class);
            candidateItems.add(Rod.class);
            candidateItems.add(Spear.class);
            candidateItems.add(Sword.class);
            candidateItems.add(ArrowItem.class);
            candidateItems.add(Bow.class);
            candidateItems.add(Javelin.class);
            candidateItems.add(Tomahawk.class);
            candidateItems.add(FrostBow.class);
            candidateItems.add(FlameBow.class);
            addWeightedCandidate(candidateItems, FireBoltWand.class, 5);
            addWeightedCandidate(candidateItems, FireBallWand.class, 5);
            addWeightedCandidate(candidateItems, MagicMissileWand.class, 3);
            addWeightedCandidate(candidateItems, WandOfSlowness.class, 2);
            addWeightedCandidate(candidateItems, WandOfBlink.class, 2);
            addWeightedCandidate(candidateItems, WandOfLightning.class, 2);
            addWeightedCandidate(candidateItems, WandOfDisintegration.class, 1);
            candidateItems.add(RingOfDetection.class);
            candidateItems.add(RingOfHaste.class);
            candidateItems.add(RingOfMending.class);
            candidateItems.add(RingOfAccuracy.class);
            candidateItems.add(RingOfEvasion.class);
            candidateItems.add(RingOfPower.class);
            candidateItems.add(RingOfElements.class);
            candidateItems.add(RingOfShadows.class);


            Item item = candidateItems.get(RandomHelper.getInstance().randomInt(candidateItems.size())).newInstance();
            applyDropStylePrefix(item, depth, null);
            return item;
        }
        catch (Exception e){
            return new Gold();
        }
    }

    private void addWeightedCandidate(ArrayList<Class<? extends Item>> candidateItems, Class<? extends Item> itemClass, int weight) {
        for (int i = 0; i < weight; i++) {
            candidateItems.add(itemClass);
        }
    }

    private void applyDropStylePrefix(Item item, int depth, RandomXS128 random) {
        if (item instanceof Seed || item instanceof Potion) {
            return;
        }

        if (item instanceof Armor) {
            applyArmorPrefix((Armor) item, depth, random);
            return;
        }

        if (item instanceof Ring) {
            applyRingPrefix((Ring) item, random);
            return;
        }

        applyDroppedItemUpgrade(item, random);

        if (item instanceof RangedWeapon && !(item instanceof Potion)) {
            applyRangedPrefix((RangedWeapon) item, depth, random);
            return;
        }

        if (item instanceof MeleeWeapon) {
            applyMeleePrefix((MeleeWeapon) item, depth, random);
            return;
        }
    }

    private void applyMeleePrefix(MeleeWeapon weapon, int depth, RandomXS128 random) {
        weapon.setPrefix((Prefix) null);
    }

    private void applyArmorPrefix(Armor armor, int depth, RandomXS128 random) {
        if (randomChance(random, 20)) {
            armor.setPrefix(new com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.armor.Cursed());
            return;
        }

        applyDroppedItemUpgrade(armor, random);
        armor.setPrefix((Prefix) null);
    }

    private void applyRingPrefix(Ring ring, RandomXS128 random) {
        if (randomChance(random, 20)) {
            ring.setPrefix(new com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes.rings.Cursed());
            return;
        }

        applyDroppedItemUpgrade(ring, random);
        ring.setPrefix((Prefix) null);
    }

    private void applyDroppedItemUpgrade(Item item, RandomXS128 random) {
        if (item == null || !item.canUpgrade()) {
            return;
        }

        int upgradeLevels = rollDroppedItemUpgradeLevels(random);
        if (upgradeLevels > 0) {
            item.modifyLevel(upgradeLevels);
        }
    }

    private int rollDroppedItemUpgradeLevels(RandomXS128 random) {
        int roll = nextInt(random, 100);
        if (roll < 5) {
            return 3;
        }

        if (roll < 15) {
            return 2;
        }

        if (roll < 40) {
            return 1;
        }

        return 0;
    }

    private void applyRangedPrefix(RangedWeapon rangedWeapon, int depth, RandomXS128 random) {
        rangedWeapon.setPrefix((Prefix) null);
    }

    private int nextInt(RandomXS128 random, int maxExcluded) {
        if (maxExcluded <= 0) {
            return 0;
        }

        return random != null ? random.nextInt(maxExcluded) : RandomHelper.getInstance().randomInt(maxExcluded);
    }

    private boolean randomChance(RandomXS128 random, int percentageToOccur) {
        return nextInt(random, 100) < percentageToOccur;
    }

    public ArrayList<Item> getLibraryScrolls(int depth, String roomIdentifier, int count) {
        ArrayList<Item> scrolls = new ArrayList<Item>();
        if (count <= 0) {
            return scrolls;
        }

        ArrayList<Class<? extends Scroll>> candidateScrolls = new ArrayList<Class<? extends Scroll>>();
        candidateScrolls.add(ScrollOfIdentify.class);
        candidateScrolls.add(ScrollOfRefuge.class);
        candidateScrolls.add(ScrollOfTeleportation.class);
        candidateScrolls.add(ScrollOfRemoveCurse.class);
        candidateScrolls.add(ScrollOfEnchantment.class);

        if (depth >= 3) {
            candidateScrolls.add(ScrollOfSkill.class);
            candidateScrolls.add(ScrollOfMagicMapping.class);
            candidateScrolls.add(ScrollOfRecharging.class);
            candidateScrolls.add(ScrollOfReadiness.class);
            candidateScrolls.add(ScrollOfUpgrade.class);
            candidateScrolls.add(ScrollOfUpgrade.class);
            candidateScrolls.add(ScrollOfUpgrade.class);
            candidateScrolls.add(ScrollOfUpgrade.class);
            candidateScrolls.add(ScrollOfUpgrade.class);
            candidateScrolls.add(ScrollOfUpgrade.class);
            candidateScrolls.add(ScrollOfLullaby.class);
        }

        if (depth >= 5) {
            candidateScrolls.add(ScrollOfChallenge.class);
            candidateScrolls.add(ScrollOfMirrorImage.class);
            candidateScrolls.add(ScrollOfFrost.class);
            candidateScrolls.add(ScrollOfTerror.class);
            candidateScrolls.add(ScrollOfPsionicBlast.class);
        }

        if (depth >= 7) {
            candidateScrolls.add(ScrollOfSacrifice.class);
            candidateScrolls.add(ScrollOfBloodyRitual.class);
        }

        if (depth >= 9) {
            candidateScrolls.add(ScrollOfWipeOut.class);
        }

        RandomXS128 random = RandomHelper.getInstance().createRoomRandom(depth, roomIdentifier, 0x4C49425241525931L);
        int rolls = Math.min(count, candidateScrolls.size());
        for (int roll = 0; roll < rolls; roll++) {
            try {
                int index = nextInt(random, candidateScrolls.size());
                scrolls.add(candidateScrolls.remove(index).newInstance());
            }
            catch (Exception ignored) {
            }
        }

        while (scrolls.size() < count) {
            scrolls.add(new ScrollOfRefuge());
        }

        return scrolls;
    }

    public ArrayList<Item> getMerchantItems(int depth){
        ArrayList<Item> items = new ArrayList<Item>();
        addMerchantFixedStock(items);

        ArrayList<Class<? extends Item>> candidateItems = new ArrayList<Class<? extends Item>>();
        addMerchantUtilityCandidates(candidateItems, depth);
        addMerchantEquipmentCandidates(candidateItems, depth);

        while (items.size() < MERCHANT_STOCK_SIZE && !candidateItems.isEmpty()) {
            int selectedIndex = RandomHelper.getInstance().randomInt(candidateItems.size());
            items.add(createMerchantItem(candidateItems.remove(selectedIndex)));
        }

        while (items.size() < MERCHANT_STOCK_SIZE) {
            items.add(new ScrollOfRefuge());
        }

        return items;
    }

    private void addMerchantFixedStock(ArrayList<Item> items) {
        items.add(new Rations());
        items.add(new Rations());
        items.add(new HealthPotion());
        items.add(new HealthPotion());
        items.add(new ManaPotion());
        items.add(new ManaPotion());
    }

    private void addMerchantUtilityCandidates(ArrayList<Class<? extends Item>> candidateItems, int depth) {
        candidateItems.add(PotionOfInvisibility.class);
        candidateItems.add(PotionOfLevitation.class);
        candidateItems.add(PotionOfLiquidFlame.class);
        candidateItems.add(PotionOfToxicGas.class);
        candidateItems.add(PotionOfPurity.class);
        candidateItems.add(PotionOfMindVision.class);
        candidateItems.add(ScrollOfIdentify.class);
        addWeightedCandidate(candidateItems, ScrollOfRefuge.class, 3);
        candidateItems.add(ScrollOfTeleportation.class);
        candidateItems.add(ScrollOfRemoveCurse.class);
        candidateItems.add(ScrollOfEnchantment.class);

        if (depth >= 3) {
            candidateItems.add(ScrollOfSkill.class);
            candidateItems.add(PotionOfExperience.class);
            candidateItems.add(PotionOfFrost.class);
            candidateItems.add(PotionOfParalyticGas.class);
            candidateItems.add(PotionOfStrength.class);
            candidateItems.add(ScrollOfMagicMapping.class);
            candidateItems.add(ScrollOfRecharging.class);
            candidateItems.add(ScrollOfReadiness.class);
            addWeightedCandidate(candidateItems, ScrollOfUpgrade.class, 2);
            candidateItems.add(ScrollOfLullaby.class);
        }

        if (depth >= 5) {
            candidateItems.add(PotionOfMight.class);
            candidateItems.add(ScrollOfChallenge.class);
            candidateItems.add(ScrollOfMirrorImage.class);
            candidateItems.add(ScrollOfFrost.class);
            candidateItems.add(ScrollOfTerror.class);
            candidateItems.add(ScrollOfPsionicBlast.class);
        }

        if (depth >= 7) {
            candidateItems.add(ScrollOfSacrifice.class);
            candidateItems.add(ScrollOfBloodyRitual.class);
        }

        if (depth >= 9) {
            candidateItems.add(ScrollOfWipeOut.class);
        }
    }

    private void addMerchantEquipmentCandidates(ArrayList<Class<? extends Item>> candidateItems, int depth) {
        if (depth <= 2) {
            candidateItems.add(ThrowDart.class);
            candidateItems.add(Shuriken.class);
            addWeightedCandidate(candidateItems, ArrowItem.class, 2);
            candidateItems.add(Bow.class);
            candidateItems.add(Dagger.class);
            candidateItems.add(ShortSword.class);
            candidateItems.add(Sword.class);
            candidateItems.add(Knuckles.class);
            candidateItems.add(LeatherArmor.class);
            candidateItems.add(FireBoltWand.class);
            candidateItems.add(RingOfDetection.class);
            return;
        }

        if (depth <= 4) {
            candidateItems.add(Shuriken.class);
            candidateItems.add(CurareDart.class);
            addWeightedCandidate(candidateItems, ArrowItem.class, 2);
            candidateItems.add(Bow.class);
            candidateItems.add(Javelin.class);
            candidateItems.add(Sword.class);
            candidateItems.add(Axe.class);
            candidateItems.add(Spear.class);
            candidateItems.add(Mace.class);
            candidateItems.add(LeatherArmor.class);
            candidateItems.add(MailArmor.class);
            candidateItems.add(FireBoltWand.class);
            candidateItems.add(MagicMissileWand.class);
            candidateItems.add(WandOfBlink.class);
            candidateItems.add(RingOfDetection.class);
            candidateItems.add(RingOfHaste.class);
            candidateItems.add(RingOfPower.class);
            candidateItems.add(RingOfSatiety.class);
            return;
        }

        if (depth <= 6) {
            candidateItems.add(CurareDart.class);
            candidateItems.add(Javelin.class);
            candidateItems.add(Tomahawk.class);
            addWeightedCandidate(candidateItems, ArrowItem.class, 2);
            candidateItems.add(Bow.class);
            candidateItems.add(FrostBow.class);
            candidateItems.add(Axe.class);
            candidateItems.add(Spear.class);
            candidateItems.add(Mace.class);
            candidateItems.add(LongSword.class);
            candidateItems.add(Glaive.class);
            candidateItems.add(MailArmor.class);
            candidateItems.add(ScaleArmor.class);
            candidateItems.add(MagicMissileWand.class);
            candidateItems.add(WandOfPoison.class);
            candidateItems.add(WandOfSlowness.class);
            candidateItems.add(WandOfReach.class);
            candidateItems.add(RingOfHaste.class);
            candidateItems.add(RingOfPower.class);
            candidateItems.add(RingOfSatiety.class);
            candidateItems.add(RingOfAccuracy.class);
            candidateItems.add(RingOfEvasion.class);
            candidateItems.add(RingOfElements.class);
            return;
        }

        if (depth <= 8) {
            candidateItems.add(Javelin.class);
            candidateItems.add(Tomahawk.class);
            addWeightedCandidate(candidateItems, ArrowItem.class, 2);
            candidateItems.add(FrostBow.class);
            candidateItems.add(FlameBow.class);
            candidateItems.add(Spear.class);
            candidateItems.add(LongSword.class);
            candidateItems.add(Glaive.class);
            candidateItems.add(Hammer.class);
            candidateItems.add(ScaleArmor.class);
            candidateItems.add(PlateArmor.class);
            candidateItems.add(WandOfLightning.class);
            candidateItems.add(WandOfAvalanche.class);
            candidateItems.add(WandOfTeleportation.class);
            candidateItems.add(RingOfAccuracy.class);
            candidateItems.add(RingOfEvasion.class);
            candidateItems.add(RingOfElements.class);
            candidateItems.add(RingOfShadows.class);
            candidateItems.add(RingOfThorns.class);
            candidateItems.add(Gemstone.class);
            return;
        }

        candidateItems.add(Tomahawk.class);
        addWeightedCandidate(candidateItems, ArrowItem.class, 2);
        candidateItems.add(FrostBow.class);
        candidateItems.add(FlameBow.class);
        candidateItems.add(LongSword.class);
        candidateItems.add(Glaive.class);
        candidateItems.add(Hammer.class);
        candidateItems.add(ScaleArmor.class);
        candidateItems.add(PlateArmor.class);
        candidateItems.add(WandOfLightning.class);
        candidateItems.add(WandOfAvalanche.class);
        candidateItems.add(WandOfTeleportation.class);
        candidateItems.add(RingOfAccuracy.class);
        candidateItems.add(RingOfEvasion.class);
        candidateItems.add(RingOfElements.class);
        candidateItems.add(RingOfShadows.class);
        candidateItems.add(RingOfThorns.class);
        candidateItems.add(Gemstone.class);
    }

    private Item createMerchantItem(Class<? extends Item> itemClass) {
        try {
            return itemClass.newInstance();
        }
        catch (Exception ignored) {
            return new ScrollOfRefuge();
        }
    }
}
