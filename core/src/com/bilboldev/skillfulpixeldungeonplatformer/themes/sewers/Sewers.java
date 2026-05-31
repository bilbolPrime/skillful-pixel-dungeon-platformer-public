package com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.SewersLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;

public class Sewers extends Theme {
    {
        int tile = (int) ConstantsHelper.TILE;
        template = "sewers";
        door = new GameSprite("images/tiles/" + template + "/door.png", tile , tile);
        closedDoor = new GameSprite("images/tiles/" + template + "/door-closed.png", tile, tile);
        lockedDoor = new GameSprite("images/tiles/" + template + "/door-locked.png", tile, tile);
        cagedDoor = new GameSprite("images/tiles/" + template + "/door-cage.png", tile, tile);
        uncagedDoor = new GameSprite("images/tiles/" + template + "/door-uncaged.png", tile, tile);
        doorLevelEntrySign = new GameSprite("images/tiles/" + template + "/door-sign-up.png", tile / 2, tile / 2);
        doorLevelExitSign = new GameSprite("images/tiles/" + template + "/door-sign-down.png", tile / 2, tile / 2);
        doorLibrarySign = new GameSprite("images/tiles/" + template + "/door-sign-library.png", tile / 2, tile / 2);
        doorTreasureSign = new GameSprite("images/tiles/" + template + "/door-sign-treasure.png", tile / 2, tile / 2);
        doorMerchantSign = new GameSprite("images/tiles/" + template + "/door-sign-merchant.png", tile / 2, tile / 2);
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
        floor = new GameSprite("images/tiles/" + template + "/floor.png", tile, tile);
        platform = new GameSprite("images/tiles/" + template + "/platform.png", tile, tile);
        sign = new GameSprite("images/tiles/" + template + "/sign.png", tile, tile);
        fader = new GameSprite("images/tiles/" + template + "/fader.png", tile, tile, 0.65f);
        library = new GameSprite("images/tiles/" + template + "/library.png", tile, tile);
    }

    @Override
    public Level getLevel() {
        return new SewersLevel();
    }

    @Override
    public Integer getChapterIntroDepth() {
        return 1;
    }

    @Override
    public String getChapterIntroStory() {
        return "The Dungeon lies right beneath the City, its upper levels actually constitute the City's sewer system. "
                + "Being nominally a part of the City, these levels are not that dangerous. No one will call it a safe place, "
                + "but at least you won't need to deal with evil magic here.";
    }

    @Override
    public String[] getSignMessages() {
        return new String[] {
            "Don't overestimate your strength, use weapons and armor you can handle.",
            "Some dangers in the sewers are easy to miss at first glance. If a room feels wrong, slow down and look carefully.",
            "Remember that raising your strength is not the only way to access better equipment. You can also lower its strength requirement with Scrolls of Upgrade.",
            "You can spend your gold in shops throughout the dungeon. There is even a merchant on the first floor now.",
            "Beware of Goo!"};
    }
}

