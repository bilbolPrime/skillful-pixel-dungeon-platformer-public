package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;


public final class RoomBackdropBudget {
    public static final int MAX_OCCUPANTS = 18, MAX_ITEM_PROPS = 24;
    public static final int MAX_PARTICLES = 16, MAX_LIGHTS = 4;
    public static final int MAX_ANIMATIONS = 8;
    private int occupants, itemProps, reservedOccupants, reservedItemProps;
    private int particles, lights, particleLimit, lightLimit, particleAllowance, lightAllowance;
    private int animations, animationLimit, reservedAnimations;

    public RoomBackdropBudget() { reset(); }

    public void reset() {
        occupants = itemProps = reservedOccupants = reservedItemProps = particles = lights = 0;
        boolean reduced = com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper.getInstance().isReducedVisualEffects();
        particleLimit = reduced ? 4 : MAX_PARTICLES; lightLimit = reduced ? 2 : MAX_LIGHTS;
        animations = reservedAnimations = 0;
        animationLimit = reduced ? 2 : MAX_ANIMATIONS;
        allowEffects(particleLimit, lightLimit);
    }


    public void allowEffects(int particles, int lights) {
        particleAllowance = Math.min(Math.max(0, particles), particleLimit - this.particles);
        lightAllowance = Math.min(Math.max(0, lights), lightLimit - this.lights);
    }

    public int availableParticles() { return particleAllowance; }
    public int availableLights() { return lightAllowance; }
    public int availableItemProps() { return MAX_ITEM_PROPS - reservedItemProps - itemProps; }

    public boolean takeParticle() {
        if (particleAllowance <= 0 || particles >= particleLimit) return false;
        particleAllowance--; particles++; return true;
    }

    public boolean takeLight() {
        if (lightAllowance <= 0 || lights >= lightLimit) return false;
        lightAllowance--; lights++; return true;
    }


    public void reservePrimary(int occupants, int itemProps) {
        reservedOccupants = Math.min(MAX_OCCUPANTS, Math.max(0, occupants));
        reservedItemProps = Math.min(MAX_ITEM_PROPS, Math.max(0, itemProps));
        reservedAnimations = Math.min(animationLimit, Math.max(0, occupants));
    }

    public boolean takeOccupant() {
        if (occupants >= MAX_OCCUPANTS - reservedOccupants) return false;
        occupants++;
        return true;
    }

    public boolean takeItemProp() {
        if (itemProps >= MAX_ITEM_PROPS - reservedItemProps) return false;
        itemProps++;
        return true;
    }

    public boolean takeAnimation() {
        if (animations >= animationLimit - reservedAnimations) return false;
        animations++;
        return true;
    }

    public int occupants() { return occupants; }
    public int itemProps() { return itemProps; }
    public int particles() { return particles; }
    public int lights() { return lights; }
    public int animations() { return animations; }
}
