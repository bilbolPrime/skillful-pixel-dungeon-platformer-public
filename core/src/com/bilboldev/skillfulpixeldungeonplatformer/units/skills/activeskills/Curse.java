package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.CurseProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class Curse extends NewClassActiveSkill {
    public Curse() {
        super(Skills.CURSE, HeroClass.NECROMANCER, 1, "Curse", "Curse",
                "Curse an enemy, weakening its attacks for a short time.",
                NewClassAssets.SkillArt.CURSE.key(), 6, 8f);
    }
    @Override protected boolean canRelease(Hero hero) {
        return Float.isFinite(hero.x) && Float.isFinite(hero.y) && NewClassSpellProjectile.liveCount() < NewClassSpellProjectile.MAX_LIVE;
    }
    @Override protected boolean release(Hero hero, Unit ignored) {
        if (!canRelease(hero)) return false;
        UnitHelper.getInstance().addUnit(new CurseProjectile(hero));
        SoundHelper.GetSingleton().play(Sounds.ZAP, 0f, .55f);
        return true;
    }
}
