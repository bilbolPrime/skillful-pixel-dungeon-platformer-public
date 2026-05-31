package com.bilboldev.skillfulpixeldungeonplatformer.items.scrolls;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;

public class ScrollOfRefuge extends Scroll {
    {
        name = "Scroll of Sanctuary";
        description = "A scroll of sanctuary warps its reader to a safe room.";
        gs = new GameSprite("images/items/scroll-sanctuary.png", 45, 45);
        quantity = 1;
    }

    @Override
    public void consume(){
        Door door = MapHelper.getInstance().getEntryDoor();
        Room entryRoom = MapHelper.getInstance().getEntryRoom();
        if (door == null || entryRoom == null) {
            EffectsHelper.getInstance().message(getHero(), "No sanctuary found", Color.LIGHT_GRAY, 0f);
            return;
        }

        UnitHelper.getInstance().getHero().x = door.x;
        UnitHelper.getInstance().getHero().y = door.y;
        UnitHelper.getInstance().getHero().setRoom(entryRoom.getIdentifier());
        MapHelper.getInstance().resetToEntry();
        super.consume();
        SaveHelper.getInstance().saveCurrentRun();
    }
}

