package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Invisible extends Buff {
    public Invisible() {
        super("Invisible", "Enemies will lose track of you until you reveal yourself.", "images/buffs/invisible.png");
    }

    @Override
    public Buff setOwner(Unit owner) {
        super.setOwner(owner);
        if (this.owner != null) {
            this.owner.setInvisible(true);
        }

        return this;
    }

    @Override
    public void debuff() {
        if (owner != null) {
            owner.setInvisible(false);
        }
    }
}