package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.NewClassBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseTargeting;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeleton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public final class RaiseSkeleton extends CorpseActiveSkill {
    public RaiseSkeleton() {
        super(Skills.RAISE_SKELETON, 1, "Raise Skeleton", "Raise Skeleton",
                "Raise a melee skeleton from a nearby corpse to fight at your side.",
                NewClassAssets.SkillArt.RAISE_SKELETON.key(), 10, 6f, CorpseTargeting.Purpose.SKELETON);
    }

    @Override protected int occupiedMinionSlots(Hero hero) { return NecromancerMinion.occupiedSlots(hero); }

    @Override protected CorpseTargeting.PreparedAction stage(final Hero hero, CorpseTargeting.Target target) {

        return new CorpseTargeting.PreparedAction() {
            @Override public boolean ready() { return hero == UnitHelper.getInstance().getHero() && !hero.isDead(); }
            @Override public void commit(CorpseTargeting.Target selected) {
                RaisedSkeleton skeleton = new RaisedSkeleton(hero);
                skeleton.setRoom(selected.roomId);
                skeleton.appear(selected.actionX, selected.actionY);
                skeleton.floorY = selected.actionY;
                skeleton.facingRight = hero.facingRight;
                UnitHelper.getInstance().addUnit(skeleton);
                EffectsHelper.getInstance().add(new NewClassBurst(hero, selected.x, selected.y, true));
                SoundHelper.GetSingleton().play(Sounds.READ, 0f, .55f);
            }
        };
    }
}
