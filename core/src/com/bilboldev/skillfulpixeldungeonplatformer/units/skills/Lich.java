package com.bilboldev.skillfulpixeldungeonplatformer.units.skills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;

public final class Lich extends Skill {
    public Lich() {
        super(Skills.LICH, HeroClass.NECROMANCER, 3, "Lich", "Lich",
                "Command more minions and strengthen their attacks. Unlocks Raise Skeleton Archer and Corpse Explosion.",
                NewClassAssets.SkillArt.LICH.key());
    }
    @Override public void affect(Unit owner) {
        if (!(owner instanceof Hero) || owner != UnitHelper.getInstance().getHero()) return;
        Hero hero = (Hero)owner;
        if (hero.isDead() || !hero.hasSkill(Skills.LICH)) return;
        for (Unit unit : UnitHelper.getInstance().getUnits())
            if (unit instanceof NecromancerMinion) ((NecromancerMinion)unit).applyLich(hero);
    }
}
