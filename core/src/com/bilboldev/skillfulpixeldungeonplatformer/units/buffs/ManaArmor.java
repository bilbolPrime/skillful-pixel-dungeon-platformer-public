package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class ManaArmor extends Buff {
    protected float absorbs = 50f;
    public ManaArmor() {
        super("Mana Armor", "Absorbs damage", "images/buffs/armor.png");
        permanent = true;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }



        return this;
    }

    public float absorbDamage(float damage){
        float absorbUpTo = (float) Math.ceil(0.9f * damage);
        if(absorbs < absorbUpTo){
            float remainingAbsorption = absorbs;
            absorbs = 0f;
            permanent = false;
            EffectsHelper.getInstance().message(owner, "Mana armor broke!", Color.RED, 0f);
            return damage - remainingAbsorption;
        }
        absorbs -= absorbUpTo;
        return damage - absorbUpTo;
    }
}

