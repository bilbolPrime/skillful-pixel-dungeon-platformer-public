package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.math.Vector2;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NewClassAssets;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.NewClassBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MinionPlacement;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.SummonedGhost;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public final class SummonGhost extends NewClassActiveSkill {
    public SummonGhost() {
        super(Skills.SUMMON_GHOST, HeroClass.NECROMANCER, 4, "Summon Ghost", "Summon Ghost",
                "Summon a ghost to fly at your side and fight for you for a short time.",
                NewClassAssets.SkillArt.SUMMON_GHOST.key(), 12, 12f);
    }
    @Override protected boolean canRelease(Hero hero) {
        return NecromancerMinion.occupiedSlots(hero) < 2 && MinionPlacement.findFlying(hero, SummonedGhost.COLLISION_WIDTH) != null;
    }
    @Override protected boolean release(Hero hero, Unit ignored) {
        if (NecromancerMinion.occupiedSlots(hero) >= 2) return false;
        Vector2 position = MinionPlacement.findFlying(hero, SummonedGhost.COLLISION_WIDTH);
        if (position == null) return false;
        SummonedGhost ghost = new SummonedGhost(hero);
        ghost.setRoom(hero.getRoom()); ghost.floorY = position.y; ghost.facingRight = hero.facingRight;
        ghost.appear(position.x, position.y);
        UnitHelper.getInstance().addUnit(ghost);
        EffectsHelper.getInstance().add(new NewClassBurst(hero, position.x + 48f, position.y, true));
        SoundHelper.GetSingleton().play(Sounds.READ, 0f, .55f);
        return true;
    }
}
