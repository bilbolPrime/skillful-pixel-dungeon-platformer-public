package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Bow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;

public abstract class BowSkill extends ActiveSkill {

    public BowSkill(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean canUse(Unit owner) {
        if (!super.canUse(owner) || !(owner instanceof Hero)) {
            return false;
        }

        Bow bow = getEquippedBow((Hero) owner);
        return bow != null && bow.getAmmo() >= getRequiredArrows();
    }

    @Override
    public boolean usesRangedAttackAnimation(Unit owner) {
        return owner instanceof Hero && getEquippedBow((Hero) owner) != null;
    }

    protected int getRequiredArrows() {
        return 1;
    }

    protected boolean fireBowSkill(Unit owner,
                                   int arrowsToFire,
                                   float damageMultiplier,
                                   float accuracyMultiplier,
                                   boolean kneeShot,
                                   boolean bombvoyage,
                                   boolean ironTip) {
        if (!(owner instanceof Hero)) {
            return false;
        }

        Bow bow = getEquippedBow((Hero) owner);
        return bow != null && bow.fireSkillShot(arrowsToFire, damageMultiplier, accuracyMultiplier, kneeShot, bombvoyage, ironTip);
    }

    protected Bow getEquippedBow(Hero hero) {
        return hero.getRangedWeapon() instanceof Bow ? (Bow) hero.getRangedWeapon() : null;
    }
}