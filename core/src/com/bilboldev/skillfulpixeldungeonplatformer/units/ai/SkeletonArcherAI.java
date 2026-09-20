package com.bilboldev.skillfulpixeldungeonplatformer.units.ai;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.NewClassSpellProjectile;


public final class SkeletonArcherAI extends FriendlyAI {
    private final RaisedSkeletonArcher archer;
    public SkeletonArcherAI(RaisedSkeletonArcher archer) {
        super(archer);
        this.archer = archer;
        los = NewClassSpellProjectile.RANGE;
    }
    @Override protected Unit findTarget() {
        Unit best = null;
        float bestDistance = los * los;
        for (Unit candidate : UnitHelper.getInstance().getUnits()) {
            float dx = candidate.x - owner.x, dy = candidate.y - owner.y;
            float distance = dx * dx + dy * dy;
            if (distance <= bestDistance && archer.hasShotLine(candidate)) {
                best = candidate;
                bestDistance = distance;
            }
        }
        return best;
    }
    @Override public void attacked(float delta) {
        if (!archer.hasShotLine(other)) {
            startFollowingAtCurrentPosition();
            return;
        }
        owner.movingLeft = owner.movingRight = false;
        owner.facingRight = other.x >= owner.x;

    }
}
