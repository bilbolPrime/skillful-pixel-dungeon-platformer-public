package com.bilboldev.skillfulpixeldungeonplatformer.items.rings;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public class Gemstone extends Ring {
    private static final float MAX_CHARGE = 40f;
    private static final float ACTIVATE_THRESHOLD = 20f;

    private float charge;

    {
        name = "Gemstone";
        description = "A living gemstone that slowly stores restorative energy while worn.";
        gs = new GameSprite("images/misc/extracted items/GemStone.png", 45, 45);
        goldCost = 150;
    }

    @Override
    public boolean supportsLevel() {
        return false;
    }

    @Override
    protected void whileEquipped(Hero hero, float deltaSeconds) {
        charge = Math.min(MAX_CHARGE, charge + Math.max(0f, deltaSeconds));
    }

    public boolean canActivate() {
        return equipped && charge >= ACTIVATE_THRESHOLD;
    }

    public void activate() {
        Hero hero = owner instanceof Hero ? (Hero) owner : null;
        if (hero == null || !canActivate()) {
            return;
        }

        int missingHealth = hero.getMaxHP() - hero.getHP();
        int restoredHealth = Math.max(1, Math.round(missingHealth * Math.min(1f, charge / MAX_CHARGE)));
        hero.heal(restoredHealth);
        charge = 0f;
        identify();
        EffectsHelper.getInstance().message(hero, "+" + restoredHealth + " HP", Color.GREEN, 0f);
    }

    public int getVisibleCharge() {
        return Math.round(charge);
    }

    public float getCharge() {
        return charge;
    }

    public void restoreCharge(float savedCharge) {
        charge = Math.max(0f, Math.min(MAX_CHARGE, savedCharge));
    }

    @Override
    public String getBigDescription() {
        StringBuilder info = new StringBuilder(super.getBigDescription());
        if (isIdentified()) {
            info.append("\n\nCharge: ").append(getVisibleCharge()).append(" / ").append((int) MAX_CHARGE).append(".");
            info.append(" Once it reaches 20 charge, it can be activated to restore health.");
        }
        return info.toString();
    }
}
