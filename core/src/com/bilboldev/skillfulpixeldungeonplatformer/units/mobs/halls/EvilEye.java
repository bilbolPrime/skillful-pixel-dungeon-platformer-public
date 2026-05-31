package com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls;

import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AgressiveAI;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.BeamEffect;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;

import java.util.ArrayList;

public class EvilEye extends Mob {
    private static final float BEAM_RANGE_TILES = 8f;
    private static final float BEAM_HEIGHT = 12f;

    {
        canFly = true;
        hp = mhp = 100;
        experience = 14;
        attackSkill = 30;
        defenseSkill = 20;
        damageReduction = 10;
        gf = new GameFilm("images/units/eye/eye.png", 256, 32, 1f);
        gf.clipSizeX = 16;
        gf.clipSizeY = 18;
        idleFrames = new int[]{0, 1, 2};
        runFrames = new int[]{0, 1, 2};
        attackFrames = new int[]{3, 4, 5};
        dieFrames = new int[]{7, 8, 9 };
        ai = new AgressiveAI(this) {
            private float beamAt = 0.8f;

            @Override
            public void act(float delta) {
                beamAt -= delta;
                super.act(delta);
            }

            @Override
            public void attacked(float delta) {
                Unit target = getOther();
                if (target == null || target.getHP() < 1 || target.getRoom() == null || getOwner().getRoom() == null || !target.getRoom().equals(getOwner().getRoom())) {
                    super.attacked(delta);
                    return;
                }

                float horizontalDistance = Math.abs(target.x - getOwner().x);
                if (horizontalDistance > ConstantsHelper.UNIT_DIMENSIONS * 2f && beamAt <= 0f) {
                    ((EvilEye) getOwner()).fireBeam();
                    getOwner().fakeAttack();
                    getOwner().movingLeft = false;
                    getOwner().movingRight = false;
                    getOwner().facingRight = getOwner().x < target.x;
                    beamAt = 1.9f;
                    return;
                }

                super.attacked(delta);
            }
        };

        speedX = 300;
        attackSpeed = 1.875f;
        weapon = new MeleeAttack().setDamageRange(14f, 20f);
        weapon.setOwner(this);
    }

    private void fireBeam() {
        float beamLength = ConstantsHelper.TILE * BEAM_RANGE_TILES;
        float beamX = facingRight ? x + ConstantsHelper.UNIT_DIMENSIONS : x - beamLength;
        float beamY = y + ConstantsHelper.UNIT_DIMENSIONS / 2f - BEAM_HEIGHT / 2f;
        Rectangle beamHitBox = new Rectangle(beamX, beamY, beamLength, BEAM_HEIGHT);

        EffectsHelper.getInstance().add(new BeamEffect().init(beamX, beamY, beamLength, BEAM_HEIGHT));
        playSound(Sounds.RAY, 0.9f);

        for (Unit unit : new ArrayList<Unit>(UnitHelper.getInstance().getUnits())) {
            if (unit == this || unit.showOnly() || unit.isDead() || unit.getRoom() == null || !unit.getRoom().equals(room)) {
                continue;
            }

            if (unit.isFriendly == isFriendly || !unit.getHitBox().overlaps(beamHitBox)) {
                continue;
            }

            unit.takeDamage(this, null, weapon.getDamage());
        }
    }

    @Override
    public String getLibraryDescription() {
        return "A flying Halls turret that locks a lane and blasts it with punishing eye-beams.";
    }
}