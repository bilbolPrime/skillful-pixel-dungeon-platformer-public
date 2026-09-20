package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.CombatText;


public final class DrainLifeProjectile extends NewClassSpellProjectile {
    public DrainLifeProjectile(Hero caster, float damage) {
        super(caster, damage);
        gs.setColor(new Color(.55f, 1f, .65f, 1f));
    }

    @Override protected void resolveHit(Unit target) {
        int previousHp = Math.max(0, target.getHP());
        super.resolveHit(target);
        int removed = Math.max(0, previousHp - Math.max(0, target.getHP()));
        Hero caster = (Hero)getOwner();
        if (caster != UnitHelper.getInstance().getHero() || caster.isDead() || caster.getHP() <= 0) return;
        int healing = Math.min(removed / 2, Math.max(0, caster.getMaxHP() - caster.getHP()));
        if (healing > 0) {
            caster.heal(healing);
            EffectsHelper.getInstance().add(new CombatText().init(caster.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                    caster.y + ConstantsHelper.UNIT_DIMENSIONS + 28f, "+" + healing, new Color(.55f, 1f, .65f, 1f)));
        }
    }
}
