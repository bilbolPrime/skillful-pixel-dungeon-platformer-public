package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Rampage extends ActiveSkill {

    {
        manaCost = 15;
    }

    public Rampage(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner){
        return super.canUse(owner) && owner.canAttack();
    }

    @Override
    public void use(Unit owner, Unit target){
        owner.getWeapon().modifyKnockback(100f);
        owner.attack(true);
        owner.facingRight = !owner.facingRight;
        owner.attack(true);
        owner.facingRight = !owner.facingRight;
        owner.attack(true);
        owner.facingRight = !owner.facingRight;
        owner.attack(true);
        owner.facingRight = !owner.facingRight;
        owner.getWeapon().modifyKnockback(-100f);

        if(owner instanceof Hero){
            EffectsHelper.getInstance().message(owner, "...", Color.WHITE, 0);
            ((Hero)owner).modifyMp(-manaCost);
        }
    }
}

