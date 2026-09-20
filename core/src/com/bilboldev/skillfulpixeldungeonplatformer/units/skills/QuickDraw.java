package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class QuickDraw extends Skill {
    public QuickDraw() {
        super(Skills.QUICK_DRAW, HeroClass.MERCENARY, 1, "Quick Draw", "Quick Draw",
                "Draw your gun faster, increasing its attack speed by 10%.",
                NewClassAssets.SkillArt.QUICK_DRAW.key());
    }
}
