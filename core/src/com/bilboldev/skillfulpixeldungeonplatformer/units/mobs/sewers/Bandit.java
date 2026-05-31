package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers;

public class Bandit extends Thief {
    {
        gf.yClipOffset = 1;
    }

    @Override
    public String getLibraryName() {
        return "Bandit";
    }

    @Override
    public String getLibraryDescription() {
        return "A rarer sewer thief variant that strips valuables in a flash and leaves the victim disoriented after a clean theft.";
    }
}