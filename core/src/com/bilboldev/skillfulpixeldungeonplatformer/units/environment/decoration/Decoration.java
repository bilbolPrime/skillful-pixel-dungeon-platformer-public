package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.decoration;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpritePose;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Decoration extends Unit {
    protected GameSprite gs;
    private transient String observedRoom;

    {
        showOnly = true;
    }

    @Override
    public void act(float delta){

    }

    @Override
    public void draw(Batch batch, float alpha){
        if(gs != null){
            gs.setPosition(x, y);
            gs.draw(batch);
            rememberDisplayedDecoration();
        }
    }

    protected void rememberDisplayedDecoration() {
        if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()
                && room != null && room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) observedRoom = room;
    }

    public SpritePose copyObservedDecoration(String outgoingRoom) {
        return gs != null && outgoingRoom != null && outgoingRoom.equals(room) && outgoingRoom.equals(observedRoom)
                ? gs.copyPose() : null;
    }
}

