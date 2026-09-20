package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.MercenaryFear;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class NoWitnesses extends NewClassActiveSkill {
    public static final float RADIUS = 4f * ConstantsHelper.TILE;
    public NoWitnesses() {
        super(Skills.NO_WITNESSES, HeroClass.MERCENARY, 4, "No Witnesses", "No Witnesses",
                "Nearby mobs run away.",
                NewClassAssets.SkillArt.NO_WITNESSES.key(), 6, 8f);
    }
    @Override protected boolean canRelease(Hero hero) {
        return hero.getRangedWeapon() instanceof Gun && ((Gun)hero.getRangedWeapon()).canFire(hero);
    }
    @Override protected boolean release(Hero hero, Unit ignored) {

        if (!canRelease(hero) || !((Gun)hero.getRangedWeapon()).tryFire(hero)) return false;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (nearby(hero, unit)) MercenaryFear.applyTo(unit);
        }
        return true;
    }
    private static boolean nearby(Hero hero, Unit unit) {
        float dx = unit.x - hero.x, dy = unit.y - hero.y;
        return MercenaryFear.canAffect(unit) && dx * dx + dy * dy <= RADIUS * RADIUS;
    }
    @Override public boolean usesRangedAttackAnimation(Unit owner) { return true; }
}
