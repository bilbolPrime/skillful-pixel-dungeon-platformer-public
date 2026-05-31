package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.FireMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.GrandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.WandMaster;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.BeamEffect;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;

public class IncinerationWand extends Wand {
    private static final float BASE_RANGE_TILES = 6f;
    private static final float BEAM_HEIGHT = 12f;

    {
        manaCost = 6;
        speed = 4f / 7f;
        name = "Incineration Wand";
        description = "A wand that projects a scorching beam through every foe in front of the caster.";

        gs = new GameSprite("images/wands/lazer-wand.png", 45, 45);
    }

    @Override
    public void addProjectile(float variance) {
        if (owner == null || owner.getRoom() == null) {
            return;
        }

        float beamLength = ConstantsHelper.TILE * BASE_RANGE_TILES * getRangeMultiplier();
        float beamX = owner.facingRight ? owner.x + ConstantsHelper.UNIT_DIMENSIONS : owner.x - beamLength;
        float beamY = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f - BEAM_HEIGHT / 2f;
        Rectangle beamHitBox = new Rectangle(beamX, beamY, beamLength, BEAM_HEIGHT);

        EffectsHelper.getInstance().add(new BeamEffect().init(beamX, beamY, beamLength, BEAM_HEIGHT));

        ArrayList<Unit> units = new ArrayList<Unit>(UnitHelper.getInstance().getUnits());
        for (Unit unit : units) {
            if (!(unit instanceof Mob) || unit.showOnly() || unit.isDead()) {
                continue;
            }

            if (unit.getRoom() == null || !unit.getRoom().equals(owner.getRoom())) {
                continue;
            }

            if (!unit.getHitBox().overlaps(beamHitBox)) {
                continue;
            }

            unit.takeDamage(owner, null, rollDamage());
        }
    }

    @Override
    protected Sounds getCastSound() {
        return Sounds.RAY;
    }

    private float getRangeMultiplier() {
        float rangeMultiplier = 1f;

        if (owner.getBuff(WandMaster.class) != null) {
            rangeMultiplier *= 1.25f;
        }

        if (owner.getBuff(GrandMaster.class) != null) {
            rangeMultiplier *= 1.25f;
        }

        return rangeMultiplier;
    }

    private float rollDamage() {
        float damage = 5 + RandomHelper.getInstance().randomInt(6);

        if (owner.getBuff(FireMastery.class) != null) {
            damage *= 1.2f;
        }

        if (owner.getBuff(GrandMaster.class) != null) {
            damage *= 1.25f;
        }

        return scalePower(damage);
    }
}