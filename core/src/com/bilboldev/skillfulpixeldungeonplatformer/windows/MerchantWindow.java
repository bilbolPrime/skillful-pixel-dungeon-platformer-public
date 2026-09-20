package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.ConsumableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.EquipableItem;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Merchant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;

import java.util.ArrayList;

public class MerchantWindow extends InteractiveWindow {
    private static final String DEFAULT_TITLE_DESCRIPTION = "Welcome to my shop. Show me what you want to sell.. maybe it's worth my time..";
    private static final float TITLE_DESCRIPTION_X = 340f;
    private static final float TITLE_DESCRIPTION_RIGHT_PADDING = 120f;

    private GameSprite merchantSprite;

    private ArrayList<ActionButton> inventoryButtons;

    private HeroClass heroClass;
    private final GameSprite portraitSprite;
    private final String merchantTitleText;

    String titleDescription;

    public MerchantWindow(HeroClass heroClass, float width, float height) {
        this(heroClass, width, height, null, null);
    }

    public MerchantWindow(HeroClass heroClass, float width, float height, GameSprite portraitSprite, String merchantTitleText) {
        super(width, height);
        inventoryButtons = new ArrayList<>();
        this.heroClass = heroClass;
        this.portraitSprite = portraitSprite;
        this.merchantTitleText = merchantTitleText;
    }

    @Override
    public Window build(){
        super.build();

        titleDescription = UtilsHelper.multiLine(
            Messages.maybeTranslate(merchantTitleText == null ? DEFAULT_TITLE_DESCRIPTION : merchantTitleText),
            3,
            getTitleDescriptionWrapWidth());
        merchantSprite = portraitSprite == null ? new Merchant().getInteractGS() : portraitSprite.clone();
        merchantSprite.setWidth(200);
        merchantSprite.setHeight(200);
        merchantSprite.setPosition(x + 100, y + height - 300);



        float offsetY = y + height - 400 - 50 - 30;
        float offsetX = x + 100;

        int counter = 0;
        for(Item item: InventoryHelper.getInstance().getItems()){
            if (InventoryHelper.getInstance().getSellPrice(item) <= 0) {
                continue;
            }
            if((item instanceof EquipableItem) && ((EquipableItem)item).getEquipped()){
                continue;
            }

            counter++;
            inventoryButtons.add(new InventoryButton(offsetX, offsetY, item));
            offsetY -= 125;
            if(counter % 4 == 0){
                offsetX += 450;
                offsetY = y + height - 400 - 50 - 30;
            }
        }

        for(int i = counter + 1; i <= 16; i++){
            inventoryButtons.add(new InventoryButton(offsetX, offsetY, "images/misc/transparent.png", "-"));
            offsetY -= 125;
            if(i % 4 == 0){
                offsetX += 450;
                offsetY = y + height - 400 - 50 - 30;
            }
        }



        return this;
    }

    @Override
    public void draw(Batch batch){
        super.draw(batch);

        merchantSprite.draw(batch);
    FontHelper.getSingleton().writeRaw(Color.WHITE, batch, 3, x + TITLE_DESCRIPTION_X, y + height - 150, titleDescription);

        for(ActionButton actionButton : inventoryButtons){
            actionButton.draw(batch);
        }
    }

    @Override
    public boolean click(float x, float y){
        for(ActionButton actionButton : inventoryButtons){
            if(actionButton.isHitProjected(x, y)){
                actionButton.click();
                return true;
            }
        }

        return super.click(x, y);
    }

    @Override
    protected ArrayList<ActionButton> getKeyboardChoices() {
        ArrayList<ActionButton> choices = new ArrayList<ActionButton>();
        for (ActionButton button : inventoryButtons) if (((InventoryButton) button).item != null) choices.add(button);
        return choices;
    }

    private float getTitleDescriptionWrapWidth() {
        return Math.max(100f, width - TITLE_DESCRIPTION_X - TITLE_DESCRIPTION_RIGHT_PADDING);
    }


    private class InventoryButton extends ActionButton {

        private int nature;
        private String text;
        private boolean selected;
        private Skill skill;
        protected float fontSize = 3;
        private GameSprite gameSprite;
        private boolean equippable;
        private Item item;


        public InventoryButton(float x, float y, String gsString, String text) {
            super(x, y, 400, 100, "images/misc/grey.png", "images/misc/grey.png");

            String localizedText = Messages.maybeTranslate(text);
            fontSize = FontHelper.getSingleton().fitSizeToEnglishFootprint(text, localizedText, 3f, 300f);

            GameSprite gs = new GameSprite(gsString, 72, 72);
            gs.setPosition(14, 14);
            addGameSprite(gs);

            gameSprite = new GameSprite("images/misc/yellow-highlight.png", 400, 100, 0.3f);
            gameSprite.setPosition(x, y);



            this.text = text;
        }

        public InventoryButton(float x, float y, Item item) {
            this(x, y, item.getGameSprite().spriteString, item.getNameWithQuantity());
            fontSize = FontHelper.getSingleton().fitSize(item.getNameWithQuantity(), 3f, 300f, 70f);
            this.item = item;
            gameSprite = new GameSprite("images/misc/yellow-highlight.png", 400, 100, 0.3f);
            gameSprite.setPosition(x, y);
            equippable = item instanceof EquipableItem;
        }


        @Override
        public void draw(Batch batch){
            super.draw(batch);
            FontHelper.getSingleton().writeWhite(batch, fontSize, x + 100, y + 65, Messages.maybeTranslate(text));

            if(selected){
                gameSprite.draw(batch);
            }

            if(equippable && ((EquipableItem)item).getEquipped()){
                gameSprite.draw(batch);
            }
        }




        public InventoryButton setEquipped(boolean equipped){

            return this;
        }


        @Override
        public void click(){
            if(item != null){
                ItemWindow itemWindow = new ItemWindow(item);
                itemWindow.addSell();
                WindowHelper.getInstance().addWindow(itemWindow.build());
            }
        }
    }

    @Override
    public void refresh(){
        inventoryButtons.clear();
        super.refresh();
    }
}


