package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class ManaArmor extends BuffSkill {

    {
        manaCost = 10;
    }

    public ManaArmor(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){
        new com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.ManaArmor().setOwner(owner);

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "The heaven protects me", Color.WHITE, 0);
            ((Hero)owner).modifyMp(-manaCost);
        }
    }
}

