package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class MindShot extends NewClassActiveSkill {
    public MindShot() {
        super(Skills.MIND_SHOT, HeroClass.NECROMANCER, 1, "Mind Shot", "Mind Shot",
                "Strike an enemy with a bolt of mental force.",
                NewClassAssets.MIND_SHOT, 4, .8f);
    }
    @Override protected boolean canRelease(Hero hero) {
        return Float.isFinite(hero.x) && Float.isFinite(hero.y) && NewClassSpellProjectile.liveCount() < NewClassSpellProjectile.MAX_LIVE;
    }
    @Override protected void playCastAnimation(Hero hero) {
        hero.startSpellCastAnimation(CAST_ATTACK_DURATION_SECONDS);
    }
    @Override protected boolean release(Hero hero, Unit ignored) {
        if (!canRelease(hero)) return false;
        UnitHelper.getInstance().addUnit(new NewClassSpellProjectile(hero, 6f + 1.5f * (hero.getLevel() - 1)));
        SoundHelper.GetSingleton().play(Sounds.ZAP, 0f, .55f);
        return true;
    }
}
