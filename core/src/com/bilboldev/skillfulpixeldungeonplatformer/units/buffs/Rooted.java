package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Rooted extends Buff {

    private static final float ROOT_SLOW = 0.95f;

    private float anchorX;
    private float anchorY;
    private String room;

    public Rooted() {
        super("Rooted", "Unable to leave the rooted tile.", "images/buffs/roots.png");
        permanent = true;
    }

    public Rooted setAnchor(float anchorX, float anchorY, String room) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.room = room;
        return this;
    }

    @Override
    public Buff setOwner(Unit owner) {
        super.setOwner(owner);
        if (this.owner == null) {
            return this;
        }

        this.owner.modifySpeedModifier(-ROOT_SLOW);
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (owner == null || !isOnAnchorTile()) {
            setPermanent(false);
            setDuration(-1f);
        }
    }

    @Override
    public void debuff() {
        if (owner != null) {
            owner.modifySpeedModifier(ROOT_SLOW);
        }
    }

    private boolean isOnAnchorTile() {
        if (owner == null || owner.getRoom() == null || room == null || !room.equals(owner.getRoom())) {
            return false;
        }

        float centerX = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return centerX >= anchorX && centerX <= anchorX + ConstantsHelper.TILE && Math.abs(owner.y - anchorY) <= ConstantsHelper.TILE / 2f;
    }
}