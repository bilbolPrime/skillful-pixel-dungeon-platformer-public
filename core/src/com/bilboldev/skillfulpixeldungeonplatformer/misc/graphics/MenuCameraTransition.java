package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.CameraWhoosh;


public final class MenuCameraTransition {
    public enum Section {
        HEROES(-2560f), TITLE(0f), INFORMATION(2560f);
        public final float origin;
        Section(float origin) { this.origin = origin; }
    }
    public enum Phase { SETTLED, FADE_OUT, PAN, FADE_IN }

    private static final float FADE_OUT_SECONDS = 0.16f, PAN_SECONDS = 0.72f, FADE_IN_SECONDS = 0.18f;
    private Section section = Section.TITLE, destination = Section.TITLE;
    private Phase phase = Phase.SETTLED;
    private float elapsed, cameraX = 1250f, startX = 1250f, opacity = 1f, startOpacity = 1f;
    private Runnable arrival;
    private boolean reducedMotion;
    private final CameraWhoosh whoosh = new CameraWhoosh();


    public boolean request(Section target, Runnable onArrival) {
        if (target == null || phase != Phase.SETTLED) return false;
        if (target == section) {
            if (onArrival != null) onArrival.run();
            return true;
        }
        destination = target;
        arrival = onArrival;
        startOpacity = opacity;
        startX = cameraX;
        elapsed = 0f;
        phase = Phase.FADE_OUT;
        return true;
    }


    public void backToTitle() {
        stopSound();
        arrival = null;
        if (phase == Phase.SETTLED && section == Section.TITLE) return;
        destination = Section.TITLE;
        startX = cameraX;
        startOpacity = opacity;
        elapsed = 0f;
        phase = Phase.FADE_OUT;
    }

    public void setReducedMotion(boolean reduced) {
        reducedMotion = reduced;
        if (reduced) stopSound();
    }

    public void stopSound() { whoosh.stop(); }

    public void act(float delta) {
        if (phase == Phase.SETTLED || Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0f) return;
        elapsed += Math.min(0.1f, delta);
        if (phase == Phase.FADE_OUT) {
            opacity = startOpacity * (1f - Math.min(1f, elapsed / FADE_OUT_SECONDS));
            if (elapsed >= FADE_OUT_SECONDS) {
                elapsed = 0f; phase = Phase.PAN;
                if (!reducedMotion) whoosh.startPan(1250f + destination.origin - startX, PAN_SECONDS);
            }
        } else if (phase == Phase.PAN) {
            float progress = reducedMotion || Math.abs(1250f + destination.origin - startX) < 0.01f
                    ? 1f : Math.min(1f, elapsed / PAN_SECONDS);
            float eased = progress * progress * (3f - 2f * progress);
            cameraX = startX + (1250f + destination.origin - startX) * eased;
            whoosh.update(progress);
            if (progress >= 1f) {
                cameraX = 1250f + destination.origin;
                section = destination;
                phase = Phase.FADE_IN;
                elapsed = 0f;
                Runnable completed = arrival;
                arrival = null;
                if (completed != null) completed.run();
            }
        } else if (phase == Phase.FADE_IN) {
            opacity = Math.min(1f, elapsed / FADE_IN_SECONDS);
            if (opacity >= 1f) { elapsed = 0f; phase = Phase.SETTLED; }
        }
    }

    public float cameraX() { return cameraX; }
    public float opacity() { return opacity; }

    public float sceneCoverOpacity() { return reducedMotion && phase != Phase.SETTLED ? 1f - opacity : 0f; }
    public Section section() { return section; }
    public Section destination() { return destination; }
    public Phase phase() { return phase; }
    public boolean isSettled() { return phase == Phase.SETTLED; }
}
