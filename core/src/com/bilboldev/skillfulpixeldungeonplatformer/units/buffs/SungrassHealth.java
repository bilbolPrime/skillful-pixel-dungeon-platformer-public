package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;

public class SungrassHealth extends Buff {

    private static final float HEAL_INTERVAL = 5f;

    private float anchorX;
    private float anchorY;
    private String room;
    private float healAt = HEAL_INTERVAL;

    public SungrassHealth() {
        super("Sungrass", "Healing while standing on sungrass.", "images/buffs/healing.png");
        permanent = true;
    }

    public SungrassHealth setAnchor(float anchorX, float anchorY, String room) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.room = room;
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (owner == null || !isOnAnchorTile()) {
            setPermanent(false);
            setDuration(-1f);
            return;
        }

        if (owner.getHP() >= owner.getMaxHP()) {
            healAt = HEAL_INTERVAL;
            return;
        }

        healAt -= delta;
        if (healAt > 0f) {
            return;
        }

        owner.heal(Math.max(1, owner.getMaxHP() / 10));
        EffectsHelper.getInstance().heal(owner);
        healAt = HEAL_INTERVAL;
    }

    private boolean isOnAnchorTile() {
        if (owner == null || owner.getRoom() == null || room == null || !room.equals(owner.getRoom())) {
            return false;
        }

        float centerX = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return centerX >= anchorX && centerX <= anchorX + ConstantsHelper.TILE && Math.abs(owner.y - anchorY) <= ConstantsHelper.TILE / 2f;
    }
}