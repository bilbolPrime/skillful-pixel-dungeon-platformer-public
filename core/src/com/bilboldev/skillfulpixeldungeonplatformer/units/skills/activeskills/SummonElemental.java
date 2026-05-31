package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Aggression;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Health;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaArmor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Regeneration;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Toughness;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Rat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.FireElemental;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;

public class SummonElemental extends ActiveSkill {

    {
        manaCost = 15;
    }

    public SummonElemental(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){

        for(Unit unit : UnitHelper.getInstance().getUnits()){
            if(unit.isFriendly == owner.isFriendly && unit.isSummoned && !unit.showOnly && (unit instanceof FireElemental)){
                unit.unSummon();
            }
        }

        FireElemental fireElemental = new FireElemental();
        fireElemental.isSummoned = true;
        fireElemental.x = owner.x + (owner.facingRight ? ConstantsHelper.UNIT_DIMENSIONS : -ConstantsHelper.UNIT_DIMENSIONS);
        fireElemental.y = owner.y;
        fireElemental.setRoom(owner.getRoom());

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "Rise", Color.WHITE, 0);
            ((Hero)owner).modifyMp(-manaCost);
            fireElemental.makeFriendly();

            if(((Hero)owner).hasSkill(Skills.SUMMON_FIRE_PLUS)){
                new Health().setOwner(fireElemental);
                new Toughness().setOwner(fireElemental);
            }

            if(((Hero)owner).hasSkill(Skills.SUMMON_FIRE_PLUS_PLUS)){
                new Aggression().setOwner(fireElemental);
                new ManaArmor().setOwner(fireElemental);
            }
        }

        UnitHelper.getInstance().addUnit(fireElemental);
    }
}

