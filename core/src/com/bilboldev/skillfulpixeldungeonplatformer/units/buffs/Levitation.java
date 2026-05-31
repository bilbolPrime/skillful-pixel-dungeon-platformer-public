package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Levitation extends Buff {
    public Levitation() {
        super("Levitation", "You hover above pressure traps and floor hazards.", "images/buffs/levitation.png");
    }

    @Override
    public Buff setOwner(Unit owner) {
        super.setOwner(owner);
        if (this.owner != null) {
            this.owner.setLevitating(true);
        }

        return this;
    }

    @Override
    public void debuff() {
        if (owner != null) {
            owner.setLevitating(false);
        }
    }
}