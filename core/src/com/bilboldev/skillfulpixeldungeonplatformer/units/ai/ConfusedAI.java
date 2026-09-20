package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class ConfusedAI extends AgressiveAI{

    {
        los = 500f;
    }

    public ConfusedAI(Unit unit){
        super(unit);
    }

    @Override
    protected boolean canTargetSameSide() { return true; }

    @Override
    public void attacked(float delta){
        if(other != null){
            this.owner.isFriendly = !other.isFriendly;
        }
        super.attacked(delta);
    }

    @Override
    protected Unit findTarget(){
        return UnitHelper.getInstance().findTarget(owner, los, true);
    }
}

