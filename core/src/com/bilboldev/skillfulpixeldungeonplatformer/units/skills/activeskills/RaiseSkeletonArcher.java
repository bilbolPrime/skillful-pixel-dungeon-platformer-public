package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.NewClassBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseTargeting;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class RaiseSkeletonArcher extends CorpseActiveSkill {
    public RaiseSkeletonArcher() {
        super(Skills.RAISE_SKELETON_ARCHER, 4, "Raise Skeleton Archer", "Raise Skeleton Archer",
                "Raise a skeleton archer from a nearby corpse to fight at your side.",
                NewClassAssets.SkillArt.RAISE_SKELETON_ARCHER.key(), 12, 8f, CorpseTargeting.Purpose.SKELETON_ARCHER);
    }
    @Override protected int occupiedMinionSlots(Hero hero) { return NecromancerMinion.occupiedSlots(hero); }
    @Override protected CorpseTargeting.PreparedAction stage(final Hero hero, CorpseTargeting.Target target) {
        return new CorpseTargeting.PreparedAction() {
            @Override public boolean ready() { return hero == UnitHelper.getInstance().getHero() && !hero.isDead(); }
            @Override public void commit(CorpseTargeting.Target selected) {
                RaisedSkeletonArcher archer = new RaisedSkeletonArcher(hero);
                archer.setRoom(selected.roomId);
                archer.appear(selected.actionX, selected.actionY);
                archer.floorY = selected.actionY;
                archer.facingRight = hero.facingRight;
                UnitHelper.getInstance().addUnit(archer);
                EffectsHelper.getInstance().add(new NewClassBurst(hero, selected.x, selected.y, true));
                SoundHelper.GetSingleton().play(Sounds.READ, 0f, .55f);
            }
        };
    }
}
