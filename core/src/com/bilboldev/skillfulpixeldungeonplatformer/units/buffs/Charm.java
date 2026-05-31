package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

public class Charm extends Buff {
    public Charm() {
        super("Charmed", "Unable to attack while charmed.", "images/buffs/heart.png");
        permanent = true;
    }

    @Override
    public boolean preventsAttacks() {
        return true;
    }
}