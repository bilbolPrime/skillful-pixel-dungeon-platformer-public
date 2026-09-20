package com.bilboldev.skillfulpixeldungeonplatformer.themes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.BossArenaArchitecture;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint;

public abstract class Theme {
    private SpecialRoomFocalPoint specialRoomFocalPoint;
    private BossArenaArchitecture bossArenaArchitecture;
    protected String template;
    protected GameSprite door;
    protected GameSprite closedDoor;
    protected GameSprite lockedDoor;
    protected GameSprite cagedDoor;
    protected GameSprite uncagedDoor;
    protected GameSprite doorLevelEntrySign;
    protected GameSprite doorLevelExitSign;
    protected GameSprite doorLibrarySign;
    protected GameSprite doorMerchantSign;
    protected GameSprite doorTreasureSign;
    protected GameSprite doorArmorySign;
    protected GameSprite doorGardenSign;
    protected GameSprite doorLaboratorySign;
    protected GameSprite doorMagicWellSign;
    protected GameSprite doorCryptSign;
    protected GameSprite doorPoolSign;
    protected GameSprite doorTreasurySign;
    protected GameSprite doorTrapsSign;
    protected GameSprite doorStorageSign;
    protected GameSprite doorVaultSign;
    protected GameSprite doorGraveyardSign;
    protected GameSprite doorMercenarySign;
    protected GameSprite wall;
    protected GameSprite wallFading;
    protected GameSprite floor;
    protected GameSprite platform;
    protected GameSprite fader;
    protected GameSprite sign;
    protected GameSprite library;

    public GameSprite getDoor(){
        return door;
    }

    public GameSprite getDoorLevelEntrySign(){
        return doorLevelEntrySign;
    }

    public GameSprite getDoorLevelExitSign(){
        return doorLevelExitSign;
    }

    public GameSprite getDoorLibrarySign(){
        return doorLibrarySign;
    }

    public GameSprite getDoorMerchantSign(){
        return doorMerchantSign;
    }

    public GameSprite getDoorTreasureSign(){
        return doorTreasureSign;
    }

    public GameSprite getDoorArmorySign() {
        return doorArmorySign;
    }

    public GameSprite getDoorGardenSign() {
        return doorGardenSign;
    }

    public GameSprite getDoorLaboratorySign() {
        return doorLaboratorySign;
    }

    public GameSprite getDoorMagicWellSign() {
        return doorMagicWellSign;
    }

    public GameSprite getDoorCryptSign() {
        return doorCryptSign;
    }

    public GameSprite getDoorPoolSign() {
        return doorPoolSign;
    }

    public GameSprite getDoorTreasurySign() {
        return doorTreasurySign;
    }

    public GameSprite getDoorTrapsSign() {
        return doorTrapsSign;
    }

    public GameSprite getDoorStorageSign() {
        return doorStorageSign;
    }

    public GameSprite getDoorVaultSign() {
        return doorVaultSign;
    }

    public GameSprite getDoorGraveyardSign() {
        return doorGraveyardSign;
    }

    public GameSprite getDoorMercenarySign() {
        return doorMercenarySign;
    }

    public GameSprite getClosedDoor(){
        return closedDoor;
    }

    public GameSprite getLockedDoor(){
        return lockedDoor;
    }

    public GameSprite getCagedDoor(){
        return cagedDoor;
    }

    public GameSprite getUncagedDoor() {
        return uncagedDoor != null ? uncagedDoor : door;
    }

    public GameSprite getWall(){
        return wall;
    }

    public void drawWall(Batch batch, Room room, int depth, int tileX, int tileY) {
        wall.setPosition(tileX * ConstantsHelper.TILE, tileY * ConstantsHelper.TILE);
        wall.draw(batch);
    }

    public GameSprite getWallFading(){
        return wallFading;
    }

    public GameSprite getFloor(){
        return floor;
    }

    public GameSprite getPlatform(){
        return platform;
    }

    public void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        platform.setPosition(tileX * ConstantsHelper.TILE, tileY * ConstantsHelper.TILE);
        platform.draw(batch);
    }


    public void drawArchitecture(Batch batch, Room room, int depth) {
    }

    public void drawBossArena(Batch batch, Room room) {
        if (!BossArenaArchitecture.supports(room) || !SpecialRoomFocalPoint.supportsTemplate(template)) return;
        if (bossArenaArchitecture == null) bossArenaArchitecture = new BossArenaArchitecture(template);
        bossArenaArchitecture.draw(batch, room);
    }


    public void drawSpecialRoomFocalPoint(Batch batch, Room room) {
        if (!SpecialRoomFocalPoint.supports(room) || !SpecialRoomFocalPoint.supportsTemplate(template)) return;
        if (specialRoomFocalPoint == null) specialRoomFocalPoint = new SpecialRoomFocalPoint(template);
        specialRoomFocalPoint.draw(batch, room);
    }

    public GameSprite getFader(){
        return fader;
    }

    public GameSprite getSign(){
        return sign;
    }

    public GameSprite getLibrary(){
        return library;
    }

    public String getTemplate() {
        return template;
    }

    public Integer getChapterIntroDepth() {
        return null;
    }

    public String getChapterIntroStory() {
        return null;
    }

    public abstract Level getLevel();

    public abstract String[] getSignMessages();
}

