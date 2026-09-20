package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.WaterSplash.Contact;


public final class AirbornePose {
    private static final float APEX_SPEED = 120f;
    private static final float LANDING_SECONDS = 0.12f;
    private int placementVersion = Integer.MIN_VALUE;
    private boolean wasGrounded, observedAirborne;
    private boolean contactReady, wasWet;
    private String room;
    private float previousX, previousY;
    private int previousRunPhase = -1;
    private float landingTime;
    private int frame = -1;

    public void update(Hero hero, float delta) {
        boolean grounded = PhysicsHelper.getInstance().hasTerrainFootContact(hero);
        int placement = hero.getPresentationPlacementVersion();
        int runPhase = hero.getRunFootstepPhase();

        boolean wet = grounded && MapHelper.getInstance().getActiveRoom() != null
                && MapHelper.getInstance().getActiveRoom().hasWaterAt(
                        hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f, hero.y);
        if (placementVersion != placement || hero.showOnly() || hero.getRoom() == null
                || !hero.getRoom().equals(room)) {
            placementVersion = placement;
            room = hero.getRoom();
            wasGrounded = grounded;

            contactReady = grounded && !hero.showOnly();
            wasWet = wet;
            previousX = hero.x;
            previousY = hero.y;
            previousRunPhase = runPhase;
            observedAirborne = false;
            landingTime = 0f;
            frame = -1;
            return;
        }

        landingTime = Math.max(0f, landingTime - delta);
        if (!grounded) {
            if (wasGrounded && contactReady && hero.speedY > APEX_SPEED) {
                emit(previousX, previousY, wasWet, Contact.TAKEOFF);
            }
            observedAirborne = true;
            landingTime = 0f;

            frame = hero.speedY > APEX_SPEED ? 6 : hero.speedY < -APEX_SPEED ? 0 : 3;
        } else {
            if (!wasGrounded && observedAirborne) {
                if (contactReady) {
                    landingTime = LANDING_SECONDS;
                    emit(hero.x, hero.y, wet, Contact.LANDING);
                }
                observedAirborne = false;
            } else if (wasGrounded && contactReady && previousRunPhase >= 0 && runPhase >= 0
                    && previousRunPhase != runPhase && hero.movingLeft != hero.movingRight
                    && Math.abs(hero.x - previousX) > 0.25f) {

                emit(hero.x, hero.y, wet, Contact.STEP);
            }
            contactReady = true;

            frame = landingTime > 0f ? 4 : -1;
        }
        wasGrounded = grounded;
        wasWet = wet;
        previousX = hero.x;
        previousY = hero.y;
        previousRunPhase = runPhase;
    }

    private void emit(float x, float y, boolean wet, Contact contact) {
        EffectsHelper.getInstance().footContact(x + ConstantsHelper.UNIT_DIMENSIONS / 2f, y, wet, contact);
    }


    public float compressionFor(Hero hero) {
        if (hero.showOnly() || hero.isAttacking() || placementVersion != hero.getPresentationPlacementVersion()) return 0f;
        float intensity = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.35f : 1f;
        return 0.065f * intensity * landingTime / LANDING_SECONDS;
    }

    public int frameFor(Hero hero, int normalFrame) {
        return frame >= 0 && placementVersion == hero.getPresentationPlacementVersion()
                && !hero.showOnly() && !hero.isAttacking() ? frame : normalFrame;
    }
}
