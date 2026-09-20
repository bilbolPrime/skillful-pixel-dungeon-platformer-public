package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.DrainLifeProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class DrainLife extends NewClassActiveSkill {
    public DrainLife() {
        super(Skills.DRAIN_LIFE, HeroClass.NECROMANCER, 2, "Drain Life", "Drain Life",
                "Steal an enemy's life, healing yourself for half the damage dealt.",
                NewClassAssets.SkillArt.DRAIN_LIFE.key(), 8, 6f);
    }

    @Override protected boolean canRelease(Hero hero) {
        return Float.isFinite(hero.x) && Float.isFinite(hero.y) && NewClassSpellProjectile.liveCount() < NewClassSpellProjectile.MAX_LIVE;
    }

    @Override protected boolean release(Hero hero, Unit ignored) {
        if (!canRelease(hero)) return false;
        UnitHelper.getInstance().addUnit(new DrainLifeProjectile(hero, 10f + 2f * (hero.getLevel() - 1)));
        SoundHelper.GetSingleton().play(Sounds.ZAP, 0f, .55f);
        return true;
    }
}
