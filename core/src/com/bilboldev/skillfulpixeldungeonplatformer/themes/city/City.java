package com.bilboldev.skillfulpixeldungeonplatformer.themes.city;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.CityLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;

public class City extends Theme {
    private CityArchitecture architecture;
    {
        int tile = (int) ConstantsHelper.TILE;
        template = "city";

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
        wall.setColor(new Color(0.73f, 0.75f, 0.80f, 1f));
        wallFading.setColor(new Color(0.73f, 0.75f, 0.80f, 1f));
        floor = new GameSprite("images/tiles/" + template + "/floor.png", tile, tile);
        platform = new GameSprite("images/tiles/" + template + "/platform.png", tile, tile);
        sign = new GameSprite("images/tiles/" + template + "/sign.png", tile, tile);
        fader = new GameSprite("images/tiles/" + template + "/fader.png", tile, tile, 0.65f);
        library = new GameSprite("images/tiles/" + template + "/library.png", tile, tile);
    }

    private CityArchitecture architecture() {
        if (architecture == null) architecture = new CityArchitecture();
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
        return new CityLevel();
    }

    @Override
    public Integer getChapterIntroDepth() {
        return 16;
    }

    @Override
    public String getChapterIntroStory() {
        return "Dwarven Metropolis was once the greatest of dwarven city-states. In its heyday the mechanized army of dwarves "
                + "has successfully repelled the invasion of the old god and his demon army. But it is said, that the returning warriors "
                + "have brought seeds of corruption with them, and that victory was the beginning of the end for the underground kingdom.";
    }

    @Override
    public String[] getSignMessages() {
        return new String[]{
                "Pixel-Mart. A safer life in dungeon.",
                "When you upgrade an enchanted weapon, there is a chance to destroy that enchantment.",
                "Weapons and armors deteriorate faster than wands and rings, but there are more ways to fix them.",
                "The only way to obtain a Scroll of Wipe Out is to receive it as a gift from the dungeon spirits.",
                "No weapons allowed in the presence of His Majesty!"
        };
    }
}
