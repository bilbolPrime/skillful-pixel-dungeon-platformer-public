package com.bilboldev.skillfulpixeldungeonplatformer.items.prefixes;

import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

public class Prefix {
    protected String name;
    protected String description;
    protected int levelModifier;
    protected float modifier;

    public String getName() {
        return Messages.capitalizeForDisplay(Messages.maybeTranslate(name));
    }

    public String getDescription() {
        return Messages.maybeTranslate(description);
    }

    public int getLevelModifier() {
        return levelModifier;
    }

    public float getModifier() {
        return modifier;
    }

    public boolean isEnhancement() {
        return false;
    }

    public float modifyAttackDamage(Unit attacker, Unit target, Weapon weapon, float rolledDamage) {
        return rolledDamage;
    }

    public void onAttack(Unit attacker, Unit target, Weapon weapon, int appliedDamage) {

    }

    public void onDefend(Unit defender, Unit attacker, Weapon damagingItem, int appliedDamage) {

    }
}

