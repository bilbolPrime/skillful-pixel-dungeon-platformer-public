package com.bilboldev.skillfulpixeldungeonplatformer.units.traps;

import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Purity;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Slow;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class ParalyticGasCloud extends Unit {
    private static final float DURATION = 5f;
    private static final float EFFECT_INTERVAL = 0.6f;
    private static final float PARTICLE_INTERVAL = 0.18f;
    private static final float MAX_HORIZONTAL_RADIUS_TILES = 3.5f;
    private static final float MAX_VERTICAL_RADIUS_TILES = 2f;

    private float elapsed;
    private float effectAt;
    private float particleAt;
    private Unit owner;

    {
        showOnly = true;
        hp = mhp = 1;
    }

    public ParalyticGasCloud setOwner(Unit owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public void act(float delta) {
        elapsed += delta;
        effectAt += delta;
        particleAt += delta;

        if (elapsed >= DURATION) {
            removeUnit();
            return;
        }

        if (room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            return;
        }

        while (particleAt >= PARTICLE_INTERVAL) {
            emitParticles();
            particleAt -= PARTICLE_INTERVAL;
        }

        while (effectAt >= EFFECT_INTERVAL) {
            affectUnitsInside();
            effectAt -= EFFECT_INTERVAL;
        }
    }

    private void affectUnitsInside() {
        Rectangle area = getCloudArea();
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if (unit == this || unit == owner || unit.showOnly() || unit.getRoom() == null || !unit.getRoom().equals(room) || unit.getHP() < 1 || unit.isDead()) {
                continue;
            }

            if (unit.getBuff(Purity.class) != null) {
                continue;
            }

            if (area.overlaps(unit.getHitBox())) {
                new Slow().setPermanent(false).setDuration(3.5f).setOwner(unit);
            }
        }
    }

    private void emitParticles() {
        Rectangle area = getCloudArea();
        for (int i = 0; i < 10; i++) {
            float particleX = area.x + RandomHelper.getInstance().randomFloat(area.width);
            float particleY = area.y + RandomHelper.getInstance().randomFloat(area.height);
            EffectsHelper.getInstance().add(new TrapBurst().init(
                    particleX,
                    particleY,
                    "images/misc/yellow-dot.png",
                    9f,
                    2,
                    10f,
                    16f,
                    30f,
                    0.01f));
        }
    }


    public Rectangle getCloudArea() {
        float radiusX = Math.min(MAX_HORIZONTAL_RADIUS_TILES * ConstantsHelper.TILE, elapsed * ConstantsHelper.TILE * 1.5f);
        float radiusY = Math.min(MAX_VERTICAL_RADIUS_TILES * ConstantsHelper.TILE, elapsed * ConstantsHelper.TILE * 1.25f);
        float centerX = x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float centerY = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;

        return new Rectangle(
                centerX - ConstantsHelper.UNIT_DIMENSIONS / 2f - radiusX,
                centerY - ConstantsHelper.UNIT_DIMENSIONS / 2f - radiusY,
                ConstantsHelper.UNIT_DIMENSIONS + radiusX * 2f,
                ConstantsHelper.UNIT_DIMENSIONS + radiusY * 2f);
    }
}
