package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class MasterOfDeath extends Skill {
    public MasterOfDeath() {
        super(Skills.MASTER_OF_DEATH, HeroClass.NECROMANCER, 4, "Master of Death", "Master of Death",
                "Defy death once per game, surviving a fatal blow with restored health and brief protection.",
                NewClassAssets.SkillArt.MASTER_OF_DEATH.key());
    }
}
