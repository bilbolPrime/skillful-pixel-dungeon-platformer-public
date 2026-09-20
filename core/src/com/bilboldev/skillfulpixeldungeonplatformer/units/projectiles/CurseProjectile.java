package com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.CombatText;


public final class CurseProjectile extends NewClassSpellProjectile {
    public CurseProjectile(Hero caster) {
        super(caster, 0f);
        gs.setColor(new Color(.72f, .45f, 1f, 1f));
    }
    @Override protected void resolveHit(Unit target) {
        if (UnitHelper.getInstance().tryHit(getOwner(), target, null, true) && NecromancerCurse.applyTo(target) != null) {
            playSound(Sounds.DEGRADE, .4f);
            EffectsHelper.getInstance().add(new CombatText().init(target.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                    target.y + ConstantsHelper.UNIT_DIMENSIONS + 28f, "Cursed", new Color(.78f, .6f, 1f, 1f)));
        }
    }
}
