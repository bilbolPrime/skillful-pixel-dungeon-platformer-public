package com.bilboldev.skillfulpixeldungeonplatformer.units.traps;

import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Purity;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class PoisonCloud extends Unit {
    private static final float DURATION = 10f;
    private static final float DAMAGE_INTERVAL = 1f;
    private static final float PARTICLE_INTERVAL = 0.3f;
    private static final float MAX_HORIZONTAL_RADIUS_TILES = 5f;
    private static final float MAX_VERTICAL_RADIUS_TILES = 3f;
    private static final int PARTICLE_BURSTS_PER_TICK = 5;
    private static final int PARTICLES_PER_BURST = 1;

    private float elapsed;
    private float damageAt;
    private float particleAt;
    private Unit owner;

    {
        showOnly = true;
        hp = mhp = 1;
    }

    public PoisonCloud setOwner(Unit owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public void act(float delta) {
        elapsed += delta;
        damageAt += delta;
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

        while (damageAt >= DAMAGE_INTERVAL) {
            damageUnitsInside();
            damageAt -= DAMAGE_INTERVAL;
        }
    }

    private void damageUnitsInside() {
        Rectangle area = getCloudArea();
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if (unit == this || unit == owner || unit.showOnly() || unit.getRoom() == null || !unit.getRoom().equals(room) || unit.getHP() < 1 || unit.isDead()) {
                continue;
            }

            if (unit.getBuff(Purity.class) != null) {
                continue;
            }

            if (area.overlaps(unit.getHitBox())) {
                unit.takeDamage(this, null, 1f);
            }
        }
    }

    private void emitParticles() {
        Rectangle area = getCloudArea();
        for (int i = 0; i < PARTICLE_BURSTS_PER_TICK; i++) {
            float particleX = area.x + RandomHelper.getInstance().randomFloat(area.width);
            float particleY = area.y + RandomHelper.getInstance().randomFloat(area.height);
            EffectsHelper.getInstance().add(new TrapBurst().init(
                    particleX,
                    particleY,
                    "images/misc/green.png",
                    9f,
                    PARTICLES_PER_BURST,
                    8f,
                    10f,
                    26f,
                    0.01f));
        }
    }

    private Rectangle getCloudArea() {
        float radiusX = Math.min(MAX_HORIZONTAL_RADIUS_TILES * ConstantsHelper.TILE, elapsed * ConstantsHelper.TILE);
        float radiusY = Math.min(MAX_VERTICAL_RADIUS_TILES * ConstantsHelper.TILE, elapsed * ConstantsHelper.TILE);
        float centerX = x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float centerY = y + ConstantsHelper.UNIT_DIMENSIONS / 2f;

        return new Rectangle(
                centerX - ConstantsHelper.UNIT_DIMENSIONS / 2f - radiusX,
                centerY - ConstantsHelper.UNIT_DIMENSIONS / 2f - radiusY,
                ConstantsHelper.UNIT_DIMENSIONS + radiusX * 2f,
                ConstantsHelper.UNIT_DIMENSIONS + radiusY * 2f);
    }
}