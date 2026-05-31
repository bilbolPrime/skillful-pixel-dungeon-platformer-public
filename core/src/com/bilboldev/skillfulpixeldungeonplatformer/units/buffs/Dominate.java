package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.FriendlyAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public class Dominate extends Buff {
    public Dominate() {
        super("Dominate", "Dominate", "images/modifiers/dominate.png");
        duration = 25f;
    }

    @Override
    public Buff setOwner(Unit owner){
        super.setOwner(owner);
        if(this.owner == null){
            return this;
        }

        if(this.owner instanceof Mob){
            ((Mob)this.owner).dominated();
        }

        return this;
    }

    @Override
    public void debuff(){
        if(this.owner instanceof Mob){
            EffectsHelper.getInstance().message(owner, "Free!", Color.WHITE, 0);
            ((Mob)this.owner).unDominate();
        }
    }
}

