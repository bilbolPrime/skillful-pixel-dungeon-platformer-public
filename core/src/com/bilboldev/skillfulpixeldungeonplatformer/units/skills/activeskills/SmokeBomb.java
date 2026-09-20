package com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Invisible;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.TrapBurst;

public class SmokeBomb extends BuffSkill {
    private static final float INVISIBILITY_DURATION_SECONDS = 6f;
    private static final float COOLDOWN_SECONDS = 6f;

    {
        manaCost = 10;
    }

    public SmokeBomb(int id, HeroClass skillClass, int tier, String name, String quickDescription, String description, String sprite) {
        super(id, skillClass, tier, name, quickDescription, description, sprite);
    }

    @Override
    public boolean shouldPlayCastAnimation(Unit owner) {
        return false;
    }

    @Override
    public void use(Unit owner, Unit target) {
        Invisible invisible = (Invisible) owner.getBuff(Invisible.class);
        if (invisible == null) {
            new Invisible().setPermanent(false).setDuration(INVISIBILITY_DURATION_SECONDS).setOwner(owner);
        } else {
            invisible.setPermanent(false).setDuration(INVISIBILITY_DURATION_SECONDS);
            owner.setInvisible(true);
        }

        float centerX = owner.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float centerY = owner.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        EffectsHelper.getInstance().add(new TrapBurst().init(
            centerX,
            centerY,
            "images/misc/black-particle.png",
            10f,
            14,
            34f,
            30f,
            58f,
            -0.01f));
        EffectsHelper.getInstance().add(new TrapBurst().init(
            centerX,
            centerY,
            "images/misc/grey.png",
            8f,
            10,
            26f,
            22f,
            44f,
            -0.02f));

        owner.modifyMana(-manaCost);
        startCooldown(COOLDOWN_SECONDS);
        EffectsHelper.getInstance().message(owner, "Gone", Color.LIGHT_GRAY, 0f);
    }
}
