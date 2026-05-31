package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Starving extends Buff {
    int healthBonus = 0;
    public Starving() {
        super("Starving", "No health or mana regeneration. Takes 1 damage every 10 seconds.", "images/buffs/starvation.png");
        permanent = true;
    }
}

