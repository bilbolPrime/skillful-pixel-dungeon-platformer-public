package com.bilboldev.skillfulpixeldungeonplatformer.units.buffs;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class Bleeding extends Buff {
    private float tickAt = 0.75f;

    public Bleeding() {
        super("Bleeding", "Losing blood over time", "images/buffs/bleeding.png");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tickAt -= delta;
        if (owner == null || tickAt > 0f || owner.getHP() < 1) {
            return;
        }

        owner.takeDamage(owner, null, 1f);
        EffectsHelper.getInstance().add(new TrapBurst().init(
                owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                "images/misc/blood.png",
                10f,
                4,
                30f,
                20f,
                40f,
                0.08f));
        tickAt = 0.75f;
    }
}