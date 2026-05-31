package com.bilboldev.skillfulpixeldungeonplatformer.items;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class EquipableItem extends Item {
    protected Unit owner;
    protected boolean equipped;

    public EquipableItem setOwner(Unit unit){
        this.owner = unit;
        return this;
    }

    public Unit getOwner(){
        return owner;
    }

    public void setEquipped(boolean equipped){
       this.equipped = equipped;
    }

    public void setEquippedState(boolean equipped) {
        this.equipped = equipped;
    }

    public boolean getEquipped(){
        return equipped;
    }
}

