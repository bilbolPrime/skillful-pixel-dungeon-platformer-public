package com.bilboldev.skillfulpixeldungeonplatformer.themes.halls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.HallsLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;

public class Halls extends Theme {
    private HallsArchitecture architecture;
    {
        int tile = (int) ConstantsHelper.TILE;
        template = "halls";

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
        wall.setColor(new Color(0.75f, 0.79f, 0.84f, 1f));
        wallFading.setColor(new Color(0.75f, 0.79f, 0.84f, 1f));
        floor = new GameSprite("images/tiles/" + template + "/floor.png", tile, tile);
        platform = new GameSprite("images/tiles/" + template + "/platform.png", tile, tile);
        sign = new GameSprite("images/tiles/" + template + "/sign.png", tile, tile);
        fader = new GameSprite("images/tiles/" + template + "/fader.png", tile, tile, 0.65f);
        library = new GameSprite("images/tiles/" + template + "/library.png", tile, tile);
    }

    private HallsArchitecture architecture() {
        if (architecture == null) architecture = new HallsArchitecture();
        return architecture;
    }

    @Override
    public void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        architecture().drawPlatform(batch, room, tileX, tileY);
    }

    @Override
    public void drawArchitecture(Batch batch, Room room, int depth) {
        architecture().draw(batch, room, depth);
    }

    @Override
    public Level getLevel() {
        return new HallsLevel();
    }

    @Override
    public Integer getChapterIntroDepth() {
        return 22;
    }

    @Override
    public String getChapterIntroStory() {
        return "In the past these levels were the outskirts of Metropolis. After the costly victory in the war with the old god "
                + "dwarves were too weakened to clear them of remaining demons. Gradually demons have tightened their grip on this place "
                + "and now it's called Demon Halls.\n\nVery few adventurers have ever descended this far...";
    }

    @Override
    public String[] getSignMessages() {
        return new String[]{
                "As you try to read the sign it bursts into greenish flames.",
                "As you try to read the sign it bursts into greenish flames.",
                "As you try to read the sign it bursts into greenish flames."
        };
    }
}
