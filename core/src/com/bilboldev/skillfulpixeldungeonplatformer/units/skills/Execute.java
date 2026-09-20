package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public final class Execute extends Skill {
    public Execute() {
        super(Skills.EXECUTE, HeroClass.MERCENARY, 4, "Execute", "Execute",
                "Finish off severely wounded enemies with a damaging gunshot. Does not affect bosses.",
                NewClassAssets.SkillArt.EXECUTE.key());
    }
}
