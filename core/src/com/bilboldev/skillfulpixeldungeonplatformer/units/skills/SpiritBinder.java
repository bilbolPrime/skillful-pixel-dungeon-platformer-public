package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;

public final class SpiritBinder extends Skill {
    public SpiritBinder() {
        super(Skills.SPIRIT_BINDER, HeroClass.NECROMANCER, 3, "Spirit Binder", "Spirit Binder",
                "Bind loyal spirits and strengthen your minions. Unlocks Summon Ghost and Master of Death.",
                NewClassAssets.SkillArt.SPIRIT_BINDER.key());
    }
    @Override public void affect(Unit owner) {
        if (!(owner instanceof Hero) || owner != UnitHelper.getInstance().getHero()) return;
        Hero hero = (Hero)owner;
        if (hero.isDead() || !hero.hasSkill(Skills.SPIRIT_BINDER)) return;
        for (Unit unit : UnitHelper.getInstance().getUnits())
            if (unit instanceof NecromancerMinion) ((NecromancerMinion)unit).applySpiritBinder(hero);
    }
}
