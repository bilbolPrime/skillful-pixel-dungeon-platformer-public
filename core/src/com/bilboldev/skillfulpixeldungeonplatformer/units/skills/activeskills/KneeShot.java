package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public class KneeShot extends BowSkill {
    private static final float DAMAGE_MULTIPLIER = 1.25f;

    {
        manaCost = 3;
    }

    public KneeShot(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public void use(Unit owner, Unit target) {
        if (!fireBowSkill(owner, 1, DAMAGE_MULTIPLIER, 1f, true, false, false)) {
            return;
        }

        owner.modifyMana(-manaCost);
    }
}