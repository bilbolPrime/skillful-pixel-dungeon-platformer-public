package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class Packrat extends Skill {
    public Packrat() {
        super(Skills.PACKRAT, HeroClass.MERCENARY, 1, "Packrat", "Packrat",
                "Find 50% more bullets in ammunition bundles.",
                NewClassAssets.SkillArt.PACKRAT.key());
    }
}
