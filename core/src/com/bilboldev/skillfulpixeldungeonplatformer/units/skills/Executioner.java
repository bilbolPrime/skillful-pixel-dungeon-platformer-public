package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

public final class Executioner extends Skill {
    public Executioner() {
        super(Skills.EXECUTIONER, HeroClass.MERCENARY, 3, "Executioner", "Executioner",
                "Shoot faster and become even more dangerous when badly wounded. Unlocks No Witnesses and Execute.",
                NewClassAssets.SkillArt.EXECUTIONER.key());
    }
}
