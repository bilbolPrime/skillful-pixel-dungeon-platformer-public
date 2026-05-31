package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmuletHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EnhancementVisualHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.armor.Armor;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.TomeOfMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.items.quest.Pickaxe;
import com.bilboldev.skillfulpixeldungeonplatformer.items.seeds.Seed;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Gemstone;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.HealthPotion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.potions.Potion;
import com.bilboldev.skillfulpixeldungeonplatformer.items.rings.Ring;
import com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls.Scroll;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MerchantRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;

import java.util.ArrayList;

public class ItemWindow extends DescriptionWindow {
    private static final float MIN_ITEM_WINDOW_HEIGHT = 340f;
    private static final float ITEM_DESCRIPTION_PADDING = 120f;
    private static final float ITEM_ACTION_AREA_HEIGHT = 200f;
    private static final float ITEM_ACTION_BUTTON_HEIGHT = 100f;

    ArrayList<ActionButton> actionButtons;
    Item item;

    public ItemWindow(Item item) {
        super(buildDescriptionSprite(item), item.getName() + "\n" + item.getBigDescription(), 1500, MIN_ITEM_WINDOW_HEIGHT);
        this.item = item;
        actionButtons = new ArrayList<>();
        setReservedBottomHeight(ITEM_DESCRIPTION_PADDING);
        height = calculateBaseHeight(item.getName() + "\n" + item.getBigDescription());
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        x = ConstantsHelper.SCREEN_WIDTH / 2 - width / 2;
    }

    private float calculateBaseHeight(String descriptionSource) {
        float maxOverlayHeight = ConstantsHelper.SCREEN_HEIGHT * 0.9f;
        float maxTextHeight = Math.max(60f, maxOverlayHeight - 200f - ITEM_DESCRIPTION_PADDING);
        String localizedDescription = Messages.maybeTranslate(descriptionSource == null ? "" : descriptionSource);
        FontHelper.FittedTextBlock fittedDescription = FontHelper.getSingleton().fitOverlayText(
                getClass().getName() + ":description",
                descriptionSource,
                localizedDescription,
                3f,
                getDescriptionWrapWidth(width),
                maxTextHeight);
        return Math.max(
                MIN_ITEM_WINDOW_HEIGHT,
                fittedDescription.height + 200f + ITEM_DESCRIPTION_PADDING);
    }

    private static GameSprite buildDescriptionSprite(Item item) {
        GameSprite itemSprite = item != null ? item.getGameSprite() : null;
        GameSprite descriptionSprite = itemSprite != null
                ? itemSprite.clone()
                : new GameSprite("images/misc/transparent.png", 200, 200);
        descriptionSprite.setWidth(200);
        descriptionSprite.setHeight(200);
        descriptionSprite.setRotation(0f);
        EnhancementVisualHelper.applyItemEnhancementPulse(descriptionSprite, item);
        return descriptionSprite;
    }

    @Override
    public Window build(){
        super.build();
        return this;
    }


    public Window addEquipUnequip(){
        boolean equipped = ((EquipableItem) item).getEquipped();
        boolean showActivate = equipped && item instanceof Gemstone && ((Gemstone) item).canActivate();
        boolean showUnequip = equipped && canShowUnequipAction();

        if (equipped && !showActivate && !showUnequip) {
            return this;
        }

        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        if(equipped){
            if (showActivate) {
                float activateX = showUnequip ? x + width - 1000 : x + width - 500;
                actionButtons.add(new RedButton(activateX,y + 100,400,100){
                    @Override
                    public void click(){
                        ((Gemstone) item).activate();
                        hide();
                        WindowHelper.getInstance().refresh();
                    }
                }.setText("ACTIVATE"));
            }

            if (showUnequip) {
                actionButtons.add(new RedButton(x + width - 500,y + 100,400,100){
                    @Override
                    public void click(){
                        hide();
                        ((EquipableItem)item).setEquipped(!((EquipableItem)item).getEquipped());
                        WindowHelper.getInstance().refresh();
                    }
                }.setText("UNEQUIP"));
            }
        }
        else {
            actionButtons.add(new RedButton(x + width - 1000,y + 100,400,100){
                @Override
                public void click(){
                    if(item instanceof Ring && !((Ring)item).canEquip()){
                        WindowHelper.getInstance().addWindow(900f, 120f, ((Ring)item).getEquipFailureMessage());
                        return;
                    }

                    hide();
                    ((EquipableItem)item).setEquipped(!((EquipableItem)item).getEquipped());
                    WindowHelper.getInstance().refresh();

                }
            }.setText(((EquipableItem)item).getEquipped() ? "UNEQUIP" :"EQUIP"));

            actionButtons.add(new RedButton(x + width - 500,y + 100,400,100){
                @Override
                public void click(){
                    InventoryHelper.getInstance().dropItem(item);
                    hide();
                    WindowHelper.getInstance().refresh();
                }
            }.setText("DROP"));
        }
        return this;
    }

    private boolean canShowUnequipAction() {
        if (!(item instanceof EquipableItem) || !((EquipableItem) item).getEquipped()) {
            return false;
        }

        if (item instanceof Armor) {
            return ((Armor) item).canUnequip();
        }

        if (item instanceof Ring) {
            return ((Ring) item).canUnequip();
        }

        return true;
    }

    public Window addAmuletActions() {
        return addAmuletActions(null);
    }

    public Window addAmuletActions(final ItemOnScreen itemOnScreen) {
        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;

        actionButtons.add(new RedButton(x + width - 1000, y + 100, 400, 100) {
            @Override
            public void click() {
                if (itemOnScreen != null) {
                    AmuletHelper.claimVictory(itemOnScreen);
                    return;
                }

                AmuletHelper.claimVictory();
            }
        }.setText("CLAIM VICTORY"));

        actionButtons.add(new RedButton(x + width - 500, y + 100, 400, 100) {
            @Override
            public void click() {
                if (itemOnScreen != null) {
                    if (AmuletHelper.takeToSurface(itemOnScreen)) {
                        hide();
                    }
                    return;
                }

                AmuletHelper.takeToSurface();
                hide();
            }
        }.setText("TAKE TO SURFACE"));

        return this;
    }

    public Window addPickUp(final ItemOnScreen itemOnScreen){
        if(item instanceof AmuletOfYendor && !(itemOnScreen instanceof MerchantRoom.ItemForPurchase)){
            return addAmuletActions(itemOnScreen);
        }

        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        actionButtons.add(new RedButton(x + width - 500 - (!(itemOnScreen instanceof MerchantRoom.ItemForPurchase) ? 500 : 0),y + 100,400,100){
            @Override
            public void click(){
                if(!(itemOnScreen instanceof MerchantRoom.ItemForPurchase)
                && InventoryHelper.getInstance().addItem(item)){
                    itemOnScreen.pickedUp();
                }

                if(itemOnScreen instanceof MerchantRoom.ItemForPurchase){
                    itemOnScreen.pickedUp();
                }

                hide();
            }
        }.setText(!(itemOnScreen instanceof MerchantRoom.ItemForPurchase)
            ? Messages.get("custom.ui.pickup")
            : Messages.get("custom.ui.gold_button",
                new Object[]{((MerchantRoom.ItemForPurchase)itemOnScreen).getGoldCost()})));

        if(!(itemOnScreen instanceof MerchantRoom.ItemForPurchase))
        actionButtons.add(new RedButton(x + width - 500,y + 100,400,100){
            @Override
            public void click(){
                itemOnScreen.pickedUp();
                hide();
            }
        }.setText(Messages.get("custom.ui.destroy")));

        return this;
    }

    public Window addConsume(final ConsumableItem item){
        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        actionButtons.add(new RedButton(x + width - 1000,y + 100,400,100){
            @Override
            public void click(){
                if (item instanceof Scroll) {
                    ((Scroll) item).identify();
                }
                item.consume();
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("USE"));

        actionButtons.add(new RedButton(x + width - 500,y + 100,400,100){
            @Override
            public void click(){
                InventoryHelper.getInstance().dropItem(item);
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("DROP"));

        return this;
    }

    public Window addTomeActions(final TomeOfMastery tomeOfMastery) {
        float bottomExtension = actionButtons.size() == 0
                ? ITEM_ACTION_AREA_HEIGHT + ITEM_ACTION_BUTTON_HEIGHT / 2f
                : ITEM_ACTION_BUTTON_HEIGHT / 2f;
        height += bottomExtension;
        y -= bottomExtension;
        float buttonY = y + ITEM_ACTION_AREA_HEIGHT + ITEM_ACTION_BUTTON_HEIGHT / 2f;

        actionButtons.add(new RedButton(x + width - 1000, buttonY, 400, 100) {
            @Override
            public void click() {
                SoundHelper.GetSingleton().play(Sounds.READ);
                tomeOfMastery.read();
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("READ"));

        actionButtons.add(new RedButton(x + width - 500, buttonY, 400, 100) {
            @Override
            public void click() {
                InventoryHelper.getInstance().dropItem(tomeOfMastery);
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("DROP"));

        return this;
    }

    public Window addPickaxeActions(final Pickaxe pickaxe) {
        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;

        float buttonStartX = x + (width - 900f) / 2f;

        actionButtons.add(new RedButton(buttonStartX, y + 100, 400, 100) {
            @Override
            public void click() {
                pickaxe.setEquipped(!pickaxe.getEquipped());
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText(pickaxe.getEquipped() ? "UNEQUIP" : "EQUIP"));

        actionButtons.add(new RedButton(buttonStartX + 500f, y + 100, 400, 100) {
            @Override
            public void click() {
                if (pickaxe.getEquipped()) {
                    pickaxe.setEquipped(false);
                }
                InventoryHelper.getInstance().dropItem(pickaxe);
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("DROP"));

        return this;
    }

    public Window addPotionActions(final Potion item) {
        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;

        final boolean showMercAction = item instanceof HealthPotion && !getAvailableMercenaries().isEmpty();

        final float[] buttonXs;
        final float buttonWidth;
        if (showMercAction) {
            buttonWidth = 300f;
            float buttonGap = 50f;
            float totalWidth = buttonWidth * 4f + buttonGap * 3f;
            float startX = x + (width - totalWidth) / 2f;
            buttonXs = new float[]{
                    startX,
                    startX + buttonWidth + buttonGap,
                    startX + (buttonWidth + buttonGap) * 2f,
                    startX + (buttonWidth + buttonGap) * 3f
            };
        }
        else {
            buttonWidth = 400f;
            buttonXs = new float[]{x + 50f, x + 550f, x + 1050f};
        }

        actionButtons.add(new RedButton(buttonXs[0], y + 100, buttonWidth, 100) {
            @Override
            public void click() {
                item.consume();
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("USE"));

        if (showMercAction) {
            actionButtons.add(new RedButton(buttonXs[1], y + 100, buttonWidth, 100) {
                @Override
                public void click() {
                    hide();
                    WindowHelper.getInstance().addWindow(buildMercHealthPotionWindow((HealthPotion) item));
                }
            }.setText(Messages.get("custom.ui.mercenary.short")));
        }

        actionButtons.add(new RedButton(buttonXs[showMercAction ? 2 : 1], y + 100, buttonWidth, 100) {
            @Override
            public void click() {
                item.setEquipped(!item.getEquipped());
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText(item.getEquipped() ? "UNEQUIP" : "THROW"));

        actionButtons.add(new RedButton(buttonXs[showMercAction ? 3 : 2], y + 100, buttonWidth, 100) {
            @Override
            public void click() {
                if (item.getEquipped()) {
                    item.setEquipped(false);
                }
                InventoryHelper.getInstance().dropItem(item);
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("DROP"));

        return this;
    }

    private Window buildMercHealthPotionWindow(final HealthPotion potion) {
        ChoiceDialogWindow choiceWindow = new ChoiceDialogWindow(
                "images/items/health-potion.png",
                Messages.get("custom.ui.mercenary.heal_prompt"),
                1500f,
                480f)
                .setButtonBottomPadding(115f);

        for (final MercenaryAlly mercenary : getAvailableMercenaries()) {
            String displayName = MercenaryHelper.getDisplayName(mercenary.getMercenaryType(), mercenary.getMercenaryName());
            choiceWindow.addChoice(
                    Messages.get("custom.ui.mercenary.heal_choice",
                    new Object[]{displayName, mercenary.getHP(), mercenary.getMaxHP()}),
                    new Runnable() {
                        @Override
                        public void run() {
                            potion.consume(mercenary);
                            WindowHelper.getInstance().refresh();
                        }
                    });
        }

        return choiceWindow.build();
    }

    private ArrayList<MercenaryAlly> getAvailableMercenaries() {
        ArrayList<MercenaryAlly> mercenaries = new ArrayList<MercenaryAlly>();
        for (com.bilboldev.skillfulpixeldungeonplatformer.units.Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit instanceof MercenaryAlly && !unit.isDead()) {
                mercenaries.add((MercenaryAlly) unit);
            }
        }

        return mercenaries;
    }

    public Window addSeedActions(final Seed item) {
        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;

        actionButtons.add(new RedButton(x + 50, y + 100, 400, 100) {
            @Override
            public void click() {
                item.plant();
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("PLANT"));

        actionButtons.add(new RedButton(x + 550, y + 100, 400, 100) {
            @Override
            public void click() {
                item.setEquipped(!item.getEquipped());
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText(item.getEquipped() ? "UNEQUIP" : "THROW"));

        actionButtons.add(new RedButton(x + 1050, y + 100, 400, 100) {
            @Override
            public void click() {
                if (item.getEquipped()) {
                    item.setEquipped(false);
                }
                InventoryHelper.getInstance().dropItem(item);
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("DROP"));

        return this;
    }

    public Window addSell(){
        height += actionButtons.size() == 0 ? ITEM_ACTION_AREA_HEIGHT : 0;
        y = ConstantsHelper.SCREEN_HEIGHT / 2 - height / 2;
        final int sellPrice = InventoryHelper.getInstance().getSellPrice(item);
        actionButtons.add(new RedButton(x + width - 500,y + 100,400,100){
            @Override
            public void click(){
                InventoryHelper.getInstance().modifyGold(sellPrice);
                SoundHelper.GetSingleton().play(Sounds.GOLD, 0, 1);
                InventoryHelper.getInstance().removeItem(item);
                hide();
                WindowHelper.getInstance().refresh();
            }
        }.setText("SELL FOR " + sellPrice));

        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        for (ActionButton actionButton : actionButtons){
            actionButton.draw(batch);
        }
    }


    @Override
    public boolean click(float x, float y){
        for(ActionButton actionButton : actionButtons){
            if(actionButton.isHitProjected(x, y)){
                actionButton.click();
                return true;
            }
        }

        if(x < this.x || x > this.x + this.width){
            hide();
        }

        if(y < this.y || y > this.y + this.height){
            hide();
        }

        return true;
    }
}


