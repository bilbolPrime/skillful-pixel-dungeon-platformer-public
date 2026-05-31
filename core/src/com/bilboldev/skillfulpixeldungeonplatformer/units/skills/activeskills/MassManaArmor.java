package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class MassManaArmor extends BuffSkill {

    {
        manaCost = 10;
    }

    public MassManaArmor(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){

        for(Unit unit : UnitHelper.getInstance().getUnits()){
            if(unit.showOnly || unit.getHP() < 1 || unit.isFriendly != owner.isFriendly){
                continue;
            }

            if(unit.getRoom() == null || !unit.getRoom().equals(owner.getRoom())){
                continue;
            }

            new com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaArmor().setOwner(unit);
        }


        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "Heaven protect us", Color.WHITE, 0);
            ((Hero)owner).modifyMp(-manaCost);
        }
    }
}

