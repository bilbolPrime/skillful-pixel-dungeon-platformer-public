package com.bilboldev.skillfulpixeldungeonplatformer.themes;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;

public class Kingdom extends Theme {
    {
        int tile = (int) ConstantsHelper.TILE;
        template = "kingdom";
        door = new GameSprite("images/tiles/" + template + "/door.png", tile , tile);
        closedDoor = new GameSprite("images/tiles/" + template + "/door-closed.png", tile, tile);
        doorLevelEntrySign = new GameSprite("images/tiles/" + template + "/door-sign-up.png", tile / 2, tile / 2);
        doorLevelExitSign = new GameSprite("images/tiles/" + template + "/door-sign-down.png", tile / 2, tile / 2);
        wall = new GameSprite("images/tiles/" + template + "/wall.png", tile, tile);
        wallFading = new GameSprite("images/tiles/" + template + "/wall-fading.png", tile, tile);
        floor = new GameSprite("images/tiles/" + template + "/floor.png", tile, tile);
        platform = new GameSprite("images/tiles/" + template + "/platform.png", tile, tile);
        fader = new GameSprite("images/tiles/" + template + "/fader.png", tile, tile, 0.65f);
    }

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public String[] getSignMessages() {
        return new String[0];
    }
}

