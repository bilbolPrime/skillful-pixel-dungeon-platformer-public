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

        Door door = new Door();
        door.x = 3 * ConstantsHelper.TILE;
        door.y = 3 * ConstantsHelper.TILE;

        return door;
    }

    @Override
    public Room build(){

        ArrayList<Item> items = InventoryHelper.getInstance().getMerchantItems(MapHelper.getInstance().getDepth());

        for(int i = 5; i < width - 3; i++){
            if(items.size() > 0){
                ItemForPurchase itemForPurchase = new ItemForPurchase(items.get(0));
                itemForPurchase.x = i * ConstantsHelper.TILE;
                itemForPurchase.y = 5 * ConstantsHelper.TILE;
                itemForPurchase.floorY = 5 * ConstantsHelper.TILE;
                itemForPurchase.setRoom(identifier);
                itemForPurchase.setGoldCost((1 + MapHelper.getInstance().getDepth() / 10) * items.get(0).getGoldCost());
                UnitHelper.getInstance().addUnit(itemForPurchase);
                items.remove(0);
            }
            platforms.add(UtilsHelper.platformKey(i, 4));
        }

        for(int i = 5; i < width - 3; i++){
            if(items.size() > 0){
                ItemForPurchase itemForPurchase = new ItemForPurchase(items.get(0));
                itemForPurchase.x = i * ConstantsHelper.TILE;
                itemForPurchase.y = 7 * ConstantsHelper.TILE;
                itemForPurchase.floorY = 7 * ConstantsHelper.TILE;
                itemForPurchase.setRoom(identifier);
                itemForPurchase.setGoldCost((1 + MapHelper.getInstance().getDepth() / 10) * items.get(0).getGoldCost());
                UnitHelper.getInstance().addUnit(itemForPurchase);
                items.remove(0);
            }

            platforms.add(UtilsHelper.platformKey(i, 6));
        }

        merchant = createMerchant();
        merchant.x = (width - 3) * ConstantsHelper.TILE;
        merchant.y = 3 * ConstantsHelper.TILE;
        merchant.floorY =  3 * ConstantsHelper.TILE;
        merchant.setRoom(identifier);
        UnitHelper.getInstance().addUnit(merchant);

        return this;
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
            }

            UnitHelper.getInstance().removeUnit(this);
            MapHelper.getInstance().refreshHeroEnvironment();
        }
    }
}

