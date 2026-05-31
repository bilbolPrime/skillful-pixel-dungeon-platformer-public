package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public class DoubleShot extends BowSkill {

    {
        manaCost = 4;
    }

    public DoubleShot(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    protected int getRequiredArrows() {
        return 2;
    }

    @Override
    public void use(Unit owner, Unit target) {
        if (!fireBowSkill(owner, 2, 1f, 1f, false, false, false)) {
            return;
        }

        owner.modifyMana(-manaCost);
    }
}