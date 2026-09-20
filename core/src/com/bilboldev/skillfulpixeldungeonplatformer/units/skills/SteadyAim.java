package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class SteadyAim extends Skill {
    public SteadyAim() {
        super(Skills.STEADY_AIM, HeroClass.MERCENARY, 2, "Steady Aim", "Steady Aim",
                "Careful aim improves your accuracy with guns by 20%.",
                NewClassAssets.SkillArt.STEADY_AIM.key());
    }
}
