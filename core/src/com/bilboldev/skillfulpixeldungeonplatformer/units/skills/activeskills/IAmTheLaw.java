package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class IAmTheLaw extends NewClassActiveSkill {
    public IAmTheLaw() {
        super(Skills.I_AM_THE_LAW, HeroClass.MERCENARY, 4, "I Am the Law", "I Am the Law",
                "Stand your ground, taking less damage for a short time.",
                NewClassAssets.SkillArt.I_AM_THE_LAW.key(), 6, 20f);
    }
    @Override protected boolean canRelease(Hero hero) { return true; }
    @Override protected boolean release(Hero hero, Unit ignored) {
        hero.getNewClassActions().setDuration(Skills.I_AM_THE_LAW, 8f);
        return true;
    }
}
