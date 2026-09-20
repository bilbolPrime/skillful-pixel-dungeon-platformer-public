package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class Wanted extends Skill {
    public Wanted() {
        super(Skills.WANTED, HeroClass.MERCENARY, 1, "Wanted", "Wanted",
                "Collect a bounty of 1 extra gold for every enemy you kill.",
                NewClassAssets.SkillArt.WANTED.key());
    }
}
