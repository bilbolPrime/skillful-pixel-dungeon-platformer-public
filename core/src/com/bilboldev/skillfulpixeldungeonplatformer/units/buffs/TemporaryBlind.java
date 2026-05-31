package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class TemporaryBlind extends Buff {
    public TemporaryBlind() {
        super("Blind", "Unable to act while the effect lasts.", "images/buffs/blindness.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner) {
        super.setOwner(owner);
        if (this.owner instanceof Mob) {
            ((Mob) this.owner).blinded();
        }

        return this;
    }

    @Override
    public void debuff() {
        if (!(owner instanceof Mob) || owner.isFriendly) {
            return;
        }

        Mob mob = (Mob) owner;
        mob.makeHostile();
        mob.alert(UnitHelper.getInstance().getHero());
    }
}