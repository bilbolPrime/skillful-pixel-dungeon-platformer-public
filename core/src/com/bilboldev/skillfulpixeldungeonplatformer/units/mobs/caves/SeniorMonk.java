package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;

public class SeniorMonk extends Monk {
    {
        weapon = new MeleeAttack().setDamageRange(12f, 20f);
        weapon.setOwner(this);
        gf.yClipOffset = 1;
    }

    @Override
    public String getLibraryName() {
        return "Senior Monk";
    }

    @Override
    public String getLibraryDescription() {
        return "A rarer elder monk whose strikes hit harder than the standard cave disciple and can abruptly swing a close fight.";
    }
}