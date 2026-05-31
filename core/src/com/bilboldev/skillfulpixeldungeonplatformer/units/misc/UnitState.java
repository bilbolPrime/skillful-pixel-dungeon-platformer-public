package com.bilboldev.skillfulpixeldungeonplatformer.units.misc;

public enum UnitState {
    SPAWNING, IDLE, RUNNING, ATTACKING, JUMPING, DEAD;

    public boolean canJump(){
        return this == IDLE || this == RUNNING;
    }

    public boolean canAttack() {return this == IDLE || this == RUNNING || this == JUMPING;}

    public boolean showOnly() {return this == DEAD || this == SPAWNING; }
}

