package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class RatKingAI extends FriendlyAI {
    public RatKingAI(Unit unit) {
        super(unit);
    }

    @Override
    protected Unit findTarget() {
        return null;
    }
}