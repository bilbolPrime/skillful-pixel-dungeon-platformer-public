package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public final class Marshal extends Skill {
    public Marshal() {
        super(Skills.MARSHAL, HeroClass.MERCENARY, 3, "Marshal", "Marshal",
                "Uphold the law with greater health and resolve. Unlocks I Am the Law.",
                NewClassAssets.SkillArt.MARSHAL.key());
    }
    @Override public void affect(Unit owner) {
        if (owner instanceof Hero && owner == UnitHelper.getInstance().getHero()) ((Hero)owner).applyMarshalHealth();
    }
}
