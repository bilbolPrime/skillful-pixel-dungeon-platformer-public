package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public class IronTip extends BowSkill {

    {
        manaCost = 4;
    }

    public IronTip(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public void use(Unit owner, Unit target) {
        if (!fireBowSkill(owner, 1, 1f, 1f, false, false, true)) {
            return;
        }

        owner.modifyMana(-manaCost);
    }
}