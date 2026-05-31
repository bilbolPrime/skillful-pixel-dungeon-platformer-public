package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Frenzy extends Buff {
    private static final float DURATION_SECONDS = 20f;

    public Frenzy() {
        super("Frenzy", "+50% att -25% sp", "images/modifiers/frenzy.png");
        permanent = false;
        duration = DURATION_SECONDS;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        this.owner.modifyOutgoingDamageModifier(0.5f);
        this.owner.modifySpeedModifier(-0.25f);

        return this;
    }

    @Override
    public void debuff(){
        this.owner.modifyOutgoingDamageModifier(-0.5f);
        this.owner.modifySpeedModifier(0.25f);
    }
}

