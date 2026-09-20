package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpritePose;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Door extends Unit {
    GameSprite door;
    GameSprite sign;
    private transient String observedRoom;
    private transient int observedState = -1;

    public Door otherDoor;

    protected String leadsTo;

    protected boolean isLocked;
    protected boolean requiresKey;
    protected boolean caged;
    protected boolean opened;


    {
        showOnly = true;
        door = MapHelper.getInstance().getTheme().getClosedDoor().clone();
        opened = false;
    }

    @Override
    public void act(float delta){

    }

    @Override
    public void draw(Batch batch, float alpha){
        float doorX = x;
        float doorY = y + 7;

        door.setPosition(doorX, doorY);
        door.drawFeatheredEdges(batch, ConstantsHelper.TILE * 0.1875f);
        if(sign != null){
            sign.setPosition(x + ConstantsHelper.TILE / 4  , y + ConstantsHelper.TILE + 12);
            sign.draw(batch);
        }
        if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) {
            observedRoom = MapHelper.getInstance().getActiveRoomIdentifier();
            observedState = appearanceState();
        }
    }


    public SpritePose copyObservedDoor(String outgoingRoom) {
        return isVisible() && outgoingRoom != null && outgoingRoom.equals(observedRoom) && observedState == appearanceState()
                ? door.copyPose() : null;
    }

    public SpritePose copyObservedSign(String outgoingRoom) {
        return isVisible() && sign != null && outgoingRoom != null && outgoingRoom.equals(observedRoom)
                ? sign.copyPose() : null;
    }

    private int appearanceState() { return caged ? 3 : isLocked ? 2 : opened ? 1 : 0; }

    public void setLeadsTo(String leadsTo){
        this.leadsTo = leadsTo;
    }

    public String getLeadsTo(){
        return leadsTo;
    }

    public float getDisplayWidth() { return door.getWidth(); }
    public float getDisplayHeight() { return door.getHeight(); }

    public void showOption(){
        UIHelper.getInstance().showDoorButton();
    }

    public void showMessage(){
        if(isLocked){
            WindowHelper.getInstance().addWindow(100, 100,
                    Messages.get("custom.generated.the_door_is_locked_c853bb2b92"));
        }
    }

    public boolean isLocked(){
        return isLocked;
    }

    public boolean requiresKey() {
        return requiresKey;
    }

    public boolean isOpen() {
        return opened;
    }

    protected GameSprite getClosedDoorSprite() {
        return MapHelper.getInstance().getTheme().getClosedDoor().clone();
    }

    protected GameSprite getLockedDoorSprite() {
        return MapHelper.getInstance().getTheme().getLockedDoor().clone();
    }

    protected GameSprite getCagedDoorSprite() {
        return MapHelper.getInstance().getTheme().getCagedDoor().clone();
    }

    protected GameSprite getOpenedDoorSprite() {
        return MapHelper.getInstance().getTheme().getDoor().clone();
    }

    public Door lock(){
        isLocked = true;
        requiresKey = false;
        caged = false;
        opened = false;
        door = getLockedDoorSprite();
        return this;
    }

    public Door lockWithKey() {
        lock();
        requiresKey = true;
        return this;
    }

    public void unlock(){
        isLocked = false;
        requiresKey = false;
        caged = false;
        opened = true;
        door = getOpenedDoorSprite();
    }

    public Door cageUp(){
        isLocked = true;
        requiresKey = false;
        caged = true;
        opened = false;
        door = getCagedDoorSprite();
        return this;
    }

    public void open(){
        isLocked = false;
        requiresKey = false;
        caged = false;
        opened = true;
        door = getOpenedDoorSprite();
    }

    public void close() {
        isLocked = false;
        requiresKey = false;
        caged = false;
        opened = false;
        door = getClosedDoorSprite();
    }

    public boolean isCaged() {
        return caged;
    }

    public LibraryDoor toLibraryDoor(){
        LibraryDoor toReturn = new LibraryDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public TreasureDoor toTreasureDoor(){
        TreasureDoor toReturn = new TreasureDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public MerchantDoor toMerchantDoor(){
        MerchantDoor toReturn = new MerchantDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public ArmoryDoor toArmoryDoor() {
        ArmoryDoor toReturn = new ArmoryDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public GardenDoor toGardenDoor() {
        GardenDoor toReturn = new GardenDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public LaboratoryDoor toLaboratoryDoor() {
        LaboratoryDoor toReturn = new LaboratoryDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public MagicWellDoor toMagicWellDoor() {
        MagicWellDoor toReturn = new MagicWellDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public CryptDoor toCryptDoor() {
        CryptDoor toReturn = new CryptDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public PoolDoor toPoolDoor() {
        PoolDoor toReturn = new PoolDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public TreasuryDoor toTreasuryDoor() {
        TreasuryDoor toReturn = new TreasuryDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public TrapsDoor toTrapsDoor() {
        TrapsDoor toReturn = new TrapsDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public StorageDoor toStorageDoor() {
        StorageDoor toReturn = new StorageDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public VaultDoor toVaultDoor() {
        VaultDoor toReturn = new VaultDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public GraveyardDoor toGraveyardDoor() {
        GraveyardDoor toReturn = new GraveyardDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public MercenaryDoor toMercenaryDoor() {
        MercenaryDoor toReturn = new MercenaryDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }


    public LevelEntryDoor toEntryDoor(){
        LevelEntryDoor toReturn = new LevelEntryDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }

    public LevelExitDoor toExitDoor(){
        LevelExitDoor toReturn = new LevelExitDoor();
        toReturn.leadsTo = leadsTo;
        toReturn.x = x;
        toReturn.y = y;
        toReturn.otherDoor = otherDoor;
        toReturn.isLocked = isLocked;
        toReturn.requiresKey = requiresKey;
        toReturn.caged = caged;
        toReturn.opened = opened;
        return toReturn;
    }
}

