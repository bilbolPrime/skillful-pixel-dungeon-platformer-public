package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class HeadShot extends NewClassActiveSkill {
    public HeadShot() {
        super(Skills.HEAD_SHOT, HeroClass.MERCENARY, 2, "Head Shot", "Head Shot",
                "Fire a powerful gunshot that deals greatly increased damage.",
                NewClassAssets.SkillArt.HEAD_SHOT.key(), 4, 6f);
    }

    @Override protected boolean canRelease(Hero hero) {
        return hero.getRangedWeapon() instanceof Gun && ((Gun)hero.getRangedWeapon()).canFire(hero);
    }

    @Override protected boolean release(Hero hero, Unit ignored) {
        return canRelease(hero) && ((Gun)hero.getRangedWeapon()).tryFire(hero, 1.75f);
    }

    @Override public boolean usesRangedAttackAnimation(Unit owner) { return true; }
}
