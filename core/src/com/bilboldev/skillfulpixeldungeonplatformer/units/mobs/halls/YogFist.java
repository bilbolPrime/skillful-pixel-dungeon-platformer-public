package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

public abstract class YogFist extends Mob {
    private static final float VISUAL_SCALE = 1.25f;
    private static final float AMBIENT_PARTICLE_INTERVAL_SECONDS = 0.14f;

    private float ambientParticleAt;

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isDead() || room == null || !room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            ambientParticleAt = 0f;
            return;
        }

        ambientParticleAt -= delta;
        while (ambientParticleAt <= 0f) {
            emitAmbientParticles();
            ambientParticleAt += AMBIENT_PARTICLE_INTERVAL_SECONDS;
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (gf != null) {
            gf.setOrigin(ConstantsHelper.UNIT_DIMENSIONS / 2f, 0f);
            gf.setScale(VISUAL_SCALE, VISUAL_SCALE);
        }

        super.draw(batch, alpha);
    }

    @Override
    public void drawHP(Batch batch) {
        if (unitState != UnitState.DEAD && hp < mhp) {
            float topOffset = ConstantsHelper.UNIT_DIMENSIONS * VISUAL_SCALE;
            greenGS.setPosition(x, y + topOffset);
            greenGS.draw(batch);
            int width = (int) (ConstantsHelper.UNIT_DIMENSIONS * (mhp - hp) / mhp);
            redGS.setWidth(width);
            redGS.setPosition(x + ConstantsHelper.UNIT_DIMENSIONS - width, y + topOffset);
            redGS.draw(batch);
        }
    }

    @Override
    public void drawBuffs(Batch batch) {
        if (unitState != UnitState.DEAD && buffs.size() > 0) {
            float topOffset = ConstantsHelper.UNIT_DIMENSIONS * VISUAL_SCALE;
            int at = (int) x + (int) ConstantsHelper.UNIT_DIMENSIONS / 2
                    - (buffs.size() % 2 == 0 ? 20 * buffs.size() / 2 : 10 + (20 * (buffs.size() - 1) / 2));
            for (Buff buff : buffs) {
                buff.getGameSprite().setPosition(at, y + topOffset + 5f);
                buff.getGameSprite().draw(batch);
                at += 20;
            }
        }
    }

    protected void emitAmbientBurst(String spritePath, float particleSize, int count, float spread, float rise, float lifeSpan, float gravityScale) {
        float burstX = x - 6f + RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS + 12f);
        float burstY = y + ConstantsHelper.UNIT_DIMENSIONS * 0.2f
            + RandomHelper.getInstance().randomFloat(ConstantsHelper.UNIT_DIMENSIONS * 0.85f);
        EffectsHelper.getInstance().add(new TrapBurst().init(
            burstX,
            burstY,
                spritePath,
                particleSize,
                count,
                spread,
                rise,
                lifeSpan,
                gravityScale));
    }

    @Override
    protected boolean shouldCelebrateBossDeath() {
        return false;
    }

    protected abstract void emitAmbientParticles();
}