package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison;

public class ShieldedBrute extends Brute {
    
    {
        gf.yClipOffset = 1;
        hp = mhp = 40;
        defenseSkill = 20;
        damageReduction = 10;
    }

    @Override
    public String getLibraryName() {
        return "Shielded Brute";
    }

    @Override
    public String getLibraryDescription() {
        return "A rarer brute variant that leans fully into defense, advancing like a shield wall and demanding far more damage to bring down cleanly.";
    }
}