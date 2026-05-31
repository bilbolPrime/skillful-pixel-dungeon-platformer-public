package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public class Bombvoyage extends BowSkill {

    {
        manaCost = 5;
    }

    public Bombvoyage(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public void use(Unit owner, Unit target) {
        if (!fireBowSkill(owner, 1, 1f, 1f, false, true, false)) {
            return;
        }

        owner.modifyMana(-manaCost);
    }
}