package com.bilboldev.skillfulpixeldungeonplatformer.themes.caves;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.CavesLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;

public class Caves extends Theme {
    private CavesRock rock;
    {
        int tile = (int) ConstantsHelper.TILE;
        template = "caves";

        door = new GameSprite("images/tiles/" + template + "/door.png", tile, tile);
        closedDoor = new GameSprite("images/tiles/" + template + "/door-closed.png", tile, tile);
        lockedDoor = new GameSprite("images/tiles/" + template + "/door-locked.png", tile, tile);
        cagedDoor = new GameSprite("images/tiles/" + template + "/door-cage.png", tile, tile);
        uncagedDoor = new GameSprite("images/tiles/" + template + "/door-uncaged.png", tile, tile);
        doorLevelEntrySign = new GameSprite("images/tiles/common/door-sign-up.png", tile / 2, tile / 2);
        doorLevelExitSign = new GameSprite("images/tiles/common/door-sign-down.png", tile / 2, tile / 2);
        doorLibrarySign = new GameSprite("images/tiles/" + template + "/door-sign-library.png", tile / 2, tile / 2);
        doorMerchantSign = new GameSprite("images/tiles/" + template + "/door-sign-merchant.png", tile / 2, tile / 2);
        doorTreasureSign = new GameSprite("images/tiles/" + template + "/door-sign-treasure.png", tile / 2, tile / 2);
        doorArmorySign = new GameSprite("images/tiles/" + template + "/door-sign-armory.png", tile / 2, tile / 2);
        doorGardenSign = new GameSprite("images/tiles/" + template + "/door-sign-garden.png", tile / 2, tile / 2);
        doorLaboratorySign = new GameSprite("images/tiles/" + template + "/door-sign-laboratory.png", tile / 2, tile / 2);
        doorMagicWellSign = new GameSprite("images/tiles/" + template + "/door-sign-magic-well.png", tile / 2, tile / 2);
        doorCryptSign = new GameSprite("images/tiles/" + template + "/door-sign-crypt.png", tile / 2, tile / 2);
        doorPoolSign = new GameSprite("images/tiles/" + template + "/door-sign-pool.png", tile / 2, tile / 2);
        doorTreasurySign = new GameSprite("images/tiles/" + template + "/door-sign-treasury.png", tile / 2, tile / 2);
        doorTrapsSign = new GameSprite("images/tiles/" + template + "/door-sign-traps.png", tile / 2, tile / 2);
        doorStorageSign = new GameSprite("images/tiles/" + template + "/door-sign-storage.png", tile / 2, tile / 2);
        doorVaultSign = new GameSprite("images/tiles/" + template + "/door-sign-vault.png", tile / 2, tile / 2);
        doorGraveyardSign = new GameSprite("images/tiles/" + template + "/door-sign-graveyard.png", tile / 2, tile / 2);
        doorMercenarySign = new GameSprite("images/tiles/" + template + "/door-sign-mercenary-warrior.png", tile / 2, tile / 2);
        wall = new GameSprite("images/tiles/" + template + "/wall.png", tile, tile);
        wallFading = new GameSprite("images/tiles/" + template + "/wall-fading.png", tile, tile);
        wallFading.setColor(new Color(0.57f, 0.63f, 0.65f, 1f));
        floor = new GameSprite("images/tiles/" + template + "/floor.png", tile, tile);
        platform = new GameSprite("images/tiles/" + template + "/platform.png", tile, tile);
        sign = new GameSprite("images/tiles/" + template + "/sign.png", tile, tile);
        fader = new GameSprite("images/tiles/" + template + "/fader.png", tile, tile, 0.65f);
        library = new GameSprite("images/tiles/" + template + "/library.png", tile, tile);
    }

    private CavesRock rock() {
        if (rock == null) rock = new CavesRock();
        return rock;
    }

    @Override
    public void drawWall(Batch batch, Room room, int depth, int tileX, int tileY) {
        rock().drawWall(batch, room, depth, tileX, tileY);
    }

    @Override
    public void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        rock().drawPlatform(batch, room, tileX, tileY);
    }

    @Override
    public void drawArchitecture(Batch batch, Room room, int depth) {
        rock().drawArchitecture(batch, room, depth);
    }

    @Override
    public Level getLevel() {
        return new CavesLevel();
    }

    @Override
    public Integer getChapterIntroDepth() {
        return 11;
    }

    @Override
    public String getChapterIntroStory() {
        return "The caves, which stretch down under the abandoned prison, are sparcely populated. They lie too deep to be exploited "
                + "by the City and they are too poor in minerals to interest the dwarves. In the past there was a trade outpost "
                + "somewhere here on the route between these two states, but it has perished since the decline of Dwarven Metropolis. "
                + "Only omnipresent gnolls and subterranean animals dwell here now.";
    }

    @Override
    public String[] getSignMessages() {
        return new String[]{
                "Pixel-Mart. Spend money. Live longer.",
                "When you're attacked by several monsters at the same time, try to retreat behind a door.",
                "If you're burning, don't let yourself get boxed in. Vertical movement matters more in the caves.",
                "There is no sense in possessing more than one Ankh at the same time, because you will lose them upon resurrecting.",
                "DANGER! Heavy machinery can cause injury, loss of limbs or death!"
        };
    }
}
