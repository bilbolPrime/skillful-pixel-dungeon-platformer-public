package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;

public class EarthrootArmor extends Buff {

    private float anchorX;
    private float anchorY;
    private String room;
    private float remainingArmor;

    public EarthrootArmor() {
        super("Earthroot Armor", "Natural armor while standing on the rooted tile.", "images/buffs/armor.png");
        permanent = true;
    }

    public EarthrootArmor setAnchor(float anchorX, float anchorY, String room) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.room = room;
        return this;
    }

    public EarthrootArmor setArmor(float armorValue) {
        this.remainingArmor = Math.max(0f, armorValue);
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (owner == null || !isOnAnchorTile() || remainingArmor <= 0f) {
            setPermanent(false);
            setDuration(-1f);
        }
    }

    public float absorbDamage(float damage) {
        if (damage <= 0f || owner == null || !isOnAnchorTile() || remainingArmor <= 0f) {
            return damage;
        }

        float absorbed = Math.min(damage, remainingArmor);
        remainingArmor -= absorbed;
        if (remainingArmor <= 0f) {
            setPermanent(false);
            setDuration(-1f);
        }

        return damage - absorbed;
    }

    private boolean isOnAnchorTile() {
        if (owner == null || owner.getRoom() == null || room == null || !room.equals(owner.getRoom())) {
            return false;
        }

        float centerX = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return centerX >= anchorX && centerX <= anchorX + ConstantsHelper.TILE && Math.abs(owner.y - anchorY) <= ConstantsHelper.TILE / 2f;
    }
}