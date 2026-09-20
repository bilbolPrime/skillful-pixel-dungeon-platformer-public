package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;

public class Item {
    protected String name, description;
    protected GameSprite gs;
    protected int quantity;
    protected int goldCost = 1;
    protected ItemIdentityHelper.Family identityFamily = ItemIdentityHelper.Family.NONE;
    protected boolean alwaysIdentified = true;
    protected String unknownDescription;
    private transient GameSprite displaySprite;
    private transient String displaySpritePath;

    public void draw(Batch batch){
        GameSprite gameSprite = resolveGameSprite();
        if(gameSprite != null){
            gameSprite.draw(batch);
        }
    }

    public GameSprite getGameSprite(){
        return resolveGameSprite();
    }

    private GameSprite resolveGameSprite() {
        if(gs == null){
            return null;
        }

        String resolvedPath = ItemIdentityHelper.getInstance().getDisplaySpritePath(this);
        if(resolvedPath == null || resolvedPath.equals(getTrueSpritePath())){
            return gs;
        }

        if(displaySprite == null || !resolvedPath.equals(displaySpritePath)){
            displaySprite = new GameSprite(resolvedPath, gs.getWidth(), gs.getHeight(), gs.getAlpha());
            displaySpritePath = resolvedPath;
            displaySprite.setPosition(gs.getX(), gs.getY());
            displaySprite.setRotation(gs.getRotation());
            displaySprite.setScale(gs.getScaleX(), gs.getScaleY());
            displaySprite.setAlpha(gs.getAlpha());
        }

        return displaySprite;
    }

    public String getName(){
        return ItemIdentityHelper.getInstance().getDisplayName(this);
    }

    public String getNameWithQuantity(){
        if(quantity < 1){
            return getName();
        }

        return Messages.maybeTranslate("%s x %d", getName(), quantity);
    }

    public String getDescription(){
        return ItemIdentityHelper.getInstance().getDisplayDescription(this);
    }

    public int getQuantity() {
        return quantity;
    }

    public Item setQuantity(int quantity){
        this.quantity = quantity;
        return this;
    }

    public String getBigDescription(){
        return getDescription();
    }

    public boolean supportsLevel() {
        return false;
    }

    public boolean canUpgrade() {
        return supportsLevel();
    }

    public int getLevel() {
        return 1;
    }

    public Item setLevel(int level) {
        return this;
    }

    public Item modifyLevel(int amount) {
        return this;
    }

    protected String withLevelSuffix(String baseName) {
        if (!supportsLevel() || !isIdentified() || getLevel() <= 1) {
            return baseName;
        }

        return baseName + " +" + (getLevel() - 1);
    }

    public int getRequiredStrength(){
        return 0;
    }

    public String getTrueName(){
        String localized = Messages.getOrNull(getClass(), "name");
        return localized != null && !localized.isEmpty() ? localized : name;
    }

    public String getTrueDescription(){
        String localized = Messages.getOrNull(getClass(), "desc");
        return localized != null && !localized.isEmpty() ? localized : description;
    }

    public String getTrueSpritePath() {
        return gs != null ? gs.spriteString : null;
    }

    public ItemIdentityHelper.Family getIdentityFamily() {
        return identityFamily;
    }

    public Item setIdentityFamily(ItemIdentityHelper.Family identityFamily) {
        this.identityFamily = identityFamily != null ? identityFamily : ItemIdentityHelper.Family.NONE;
        if (this.identityFamily != ItemIdentityHelper.Family.NONE) {
            alwaysIdentified = false;
        }
        return this;
    }

    public boolean isAlwaysIdentified() {
        return alwaysIdentified || identityFamily == ItemIdentityHelper.Family.NONE;
    }

    public Item setAlwaysIdentified(boolean alwaysIdentified) {
        this.alwaysIdentified = alwaysIdentified;
        return this;
    }

    public String getUnknownDescription() {
        return unknownDescription;
    }

    public Item setUnknownDescription(String unknownDescription) {
        this.unknownDescription = unknownDescription;
        return this;
    }

    public boolean isIdentified() {
        return ItemIdentityHelper.getInstance().isIdentified(this);
    }

    public void identify() {
        ItemIdentityHelper.getInstance().identify(this);
    }

    public void drop(float x, float y){
        drop(x, y, null);
    }

    public void drop(float x, float y, String roomIdentifier){
        ItemOnScreen itemOnScreen = new ItemOnScreen(this);
        itemOnScreen.x = x;
        itemOnScreen.y = y;
        itemOnScreen.floorY = MapHelper.getInstance().calculateFloorY(x, y);
        itemOnScreen.setRoom(roomIdentifier);
        UnitHelper.getInstance().addUnit(itemOnScreen);
    }

    public void spawnNaturally(float x, float y, String roomIdentifier) {
        spawnNaturally(x, y, MapHelper.getInstance().calculateFloorY(x, y), roomIdentifier);
    }

    public void spawnNaturally(float x, float y, float floorY, String roomIdentifier) {

        if (InventoryHelper.isClassRestricted(this)) return;
        ItemOnScreen itemOnScreen = new ItemOnScreen(this);
        itemOnScreen.x = x;
        itemOnScreen.y = y;
        itemOnScreen.floorY = floorY;
        itemOnScreen.setRoom(roomIdentifier);
        UnitHelper.getInstance().addUnit(itemOnScreen);
        InventoryHelper.getInstance().spawnNaturalCompanionItems(this, x, y, floorY, roomIdentifier);
    }

    public int getGoldCost(){
        return goldCost * Math.max(1, quantity);
    }
}

