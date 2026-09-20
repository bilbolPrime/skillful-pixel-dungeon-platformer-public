package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.ArrowItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.BulletItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.ImpShopkeeper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Merchant;

import java.util.ArrayList;

public class MerchantRoom extends Room {
    {
        canSpawn = false;
        width = 15;
    }

    private Merchant merchant;
    private final boolean useImpShopkeeper;

    public MerchantRoom(String identifier) {
        this(identifier, false);
    }

    public MerchantRoom(String identifier, boolean useImpShopkeeper) {
        super(identifier);
        this.useImpShopkeeper = useImpShopkeeper;
    }

    @Override
    public Door getRandomDoor(){
        requireSupportedPlacement(3, 3, ConstantsHelper.TILE, ConstantsHelper.TILE + 7f);
        Door door = new Door();
        door.x = 3 * ConstantsHelper.TILE;
        door.y = 3 * ConstantsHelper.TILE;

        return door;
    }

    @Override
    public Room build(){
        width = 18 + 2 * com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper.getInstance()
                .createRoomRandom(0, identifier, 0x53484f50L).nextInt(2);
        platforms.clear(); waterPlatforms.clear(); stuff.clear();

        addPlatformSpan(5, 13, 3);
        addPlatformSpan(5, 13, 5);
        height = RoomRoutes.minimumRoomHeightForJump(6);
        getLayout().reserveArrival(3, 3);
        getLayout().describe("merchant-counters", width - 3, 3);
        return this;
    }

    @Override
    protected void placeContents() {
        ArrayList<Item> items = InventoryHelper.getInstance().getMerchantItems(MapHelper.getInstance().getDepth());

        for(int i = 6; i <= 12; i++){
            if(items.size() > 0){
                placeStock(items.get(0), i, 4);
                items.remove(0);
            }
        }

        for(int i = 6; i <= 12; i++){
            if(items.size() > 0){
                placeStock(items.get(0), i, 6);
                items.remove(0);
            }

        }


        if (items.size() == 1 && (items.get(0) instanceof BulletItem || items.get(0) instanceof ArrowItem)) {
            placeStock(items.get(0), 5, 4);
            items.remove(0);
        }
        if (!items.isEmpty()) throw new IllegalStateException("Insufficient stock slots in " + identifier);

        requireContentPlacement(width - 3, 3, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        merchant = createMerchant();
        merchant.x = (width - 3) * ConstantsHelper.TILE;
        merchant.y = 3 * ConstantsHelper.TILE;
        merchant.floorY =  3 * ConstantsHelper.TILE;
        merchant.setRoom(identifier);
        UnitHelper.getInstance().addUnit(merchant);

    }

    private void placeStock(Item item, int tileX, int floorTileY) {
        requireContentPlacement(tileX, floorTileY, ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS);
        ItemForPurchase stock = new ItemForPurchase(item);
        stock.x = tileX * ConstantsHelper.TILE;
        stock.y = floorTileY * ConstantsHelper.TILE;
        stock.floorY = stock.y;
        stock.setRoom(identifier);
        stock.setGoldCost(item instanceof BulletItem ? 10 : (1 + MapHelper.getInstance().getDepth() / 10) * item.getGoldCost());
        UnitHelper.getInstance().addUnit(stock);
        if (item instanceof Gun) InventoryHelper.getInstance().spawnNaturalCompanionItems(item, stock.x, stock.y, stock.floorY, identifier);
    }

    protected Merchant createMerchant() {
        return useImpShopkeeper ? new ImpShopkeeper() : new Merchant();
    }


    public class ItemForPurchase extends ItemOnScreen{

        protected int goldCost;
        public ItemForPurchase(Item item) {
            super(item);
        }

        public int getGoldCost(){
            return InventoryHelper.getInstance().getMerchantBuyPrice(goldCost);
        }

        public int getBaseGoldCost() {
            return goldCost;
        }

        public void setGoldCost(int goldCost){
            this.goldCost = goldCost;
        }

        @Override
        public void pickedUp(){
            if (InventoryHelper.isClassRestricted(item)) {
                InventoryHelper.showClassRestriction();
                return;
            }
            int finalCost = getGoldCost();

            if(InventoryHelper.getInstance().getGold() < finalCost){
                EffectsHelper.getInstance().message( this, "Insufficient gold", Color.GOLD,0f);
                return;
            }

            InventoryHelper.getInstance().modifyGold(-finalCost);
            SoundHelper.GetSingleton().play(Sounds.GOLD, 0f, 1f);
            EffectsHelper.getInstance().message( this, "-" + finalCost + " gold", Color.GOLD,0f);

            if(!InventoryHelper.getInstance().addItem(item)){
                ItemOnScreen itemOnScreen = new ItemOnScreen(item);
                itemOnScreen.setRoom(getRoom());
                itemOnScreen.x = x;
                itemOnScreen.y = y;
                itemOnScreen.floorY = floorY;
                UnitHelper.getInstance().addUnit(itemOnScreen);
            } else {
                com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper.getInstance().showItemReceipt(item);
            }

            UnitHelper.getInstance().removeUnit(this);
            MapHelper.getInstance().refreshHeroEnvironment();
        }
    }
}

