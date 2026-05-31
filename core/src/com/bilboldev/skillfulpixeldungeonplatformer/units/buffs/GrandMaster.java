package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class GrandMaster extends Buff {
    public GrandMaster() {
        super("Grand Master", "Two projectiles", "images/modifiers/sorcery.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        return this;
    }
}

