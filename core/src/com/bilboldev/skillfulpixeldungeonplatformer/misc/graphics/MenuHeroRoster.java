package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;


public final class MenuHeroRoster implements MenuScene {
    public interface SelectionListener { void changed(HeroClass settled); }
    private static final HeroClass[] CLASSES = {HeroClass.WARRIOR, HeroClass.WIZARD, HeroClass.ROGUE,
            HeroClass.ARCHER, HeroClass.NECROMANCER, HeroClass.MERCENARY};
    public static final float HOME_Y = 584f, FIRST_X = 240f, SPACING = 210f;
    public static final float STEP_X = 24f, STEP_Y = 112f, STEP_SECONDS = 0.34f;
    public static final float FOCUS_X = 304f, FOCUS_SECONDS = 0.42f;
    private final MenuHeroPreview[] actors = new MenuHeroPreview[CLASSES.length];
    private final float[] progress = new float[CLASSES.length];
    private final int[] drawOrder = new int[CLASSES.length];
    private int requested = -1, settled = -1;
    private SelectionListener listener;
    private float focusAmount, focusStart, focusTarget, cameraOffset, cameraStart, cameraTarget;
    private float focusTime = FOCUS_SECONDS;
    private boolean reducedFocusMotion;

    public MenuHeroRoster() {
        for (int i = 0; i < actors.length; i++)
            actors[i] = new MenuHeroPreview(CLASSES[i], homeX(i), HOME_Y, i * 0.43f);
    }

    public int size() { return actors.length; }
    public MenuHeroPreview actor(int index) { return actors[index]; }
    public float homeX(int index) { return FIRST_X + index * SPACING; }
    public float progress(int index) { return progress[index]; }
    public HeroClass requestedHero() { return requested < 0 ? null : CLASSES[requested]; }
    public HeroClass settledHero() { return settled < 0 ? null : CLASSES[settled]; }
    public void setSelectionListener(SelectionListener listener) { this.listener = listener; }
    public float cameraOffset() { return cameraOffset; }
    public float focusAmount() { return focusAmount; }
    public boolean focusedView() { return requested >= 0 || focusAmount > 0f; }
    public boolean focusMoving() { return focusTime < FOCUS_SECONDS; }
    public float sceneCoverOpacity() {
        return reducedFocusMotion && focusMoving() ? 1f - Math.abs(2f * focusTime / FOCUS_SECONDS - 1f) : 0f;
    }

    private void focus(boolean selected) {
        focusStart = focusAmount; cameraStart = cameraOffset;
        focusTarget = selected ? 1f : 0f;
        cameraTarget = selected ? homeX(requested) + STEP_X - FOCUS_X : 0f;
        focusTime = 0f;
        reducedFocusMotion = GameSettingsHelper.getInstance().isReducedCameraMotion();
    }


    public boolean select(int index) {
        if (index < 0 || index >= actors.length) throw new IllegalArgumentException("Unknown preview index");
        if (requested == index) return false;
        requested = index; settled = -1;
        focus(true);
        if (listener != null) listener.changed(null);
        return true;
    }

    public void cancelSelection() {
        if (requested < 0 && settled < 0) return;
        requested = -1; settled = -1;
        focus(false);
        if (listener != null) listener.changed(null);
    }

    @Override public void act(float delta) {
        if (Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0) return;
        float step = Math.min(delta, 0.1f);
        focusTime = Math.min(FOCUS_SECONDS, focusTime + step);
        float pan = focusTime / FOCUS_SECONDS;
        float easedPan = pan * pan * (3f - 2f * pan);
        focusAmount = focusStart + (focusTarget - focusStart) * easedPan;
        cameraOffset = reducedFocusMotion ? (pan < 0.5f ? cameraStart : cameraTarget)
                : cameraStart + (cameraTarget - cameraStart) * easedPan;
        boolean arrived = requested >= 0 && !focusMoving();
        for (int i = 0; i < actors.length; i++) {
            float target = i == requested ? 1f : 0f;
            float old = progress[i];
            progress[i] = target > old ? Math.min(target, old + step / STEP_SECONDS)
                    : Math.max(target, old - step / STEP_SECONDS);
            boolean walking = progress[i] != target;
            actors[i].setMovement(walking, target < old);
            float t = progress[i] * progress[i] * (3f - 2f * progress[i]);
            actors[i].setPosition(homeX(i) + STEP_X * t, HOME_Y - STEP_Y * t);
            actors[i].act(step);
            arrived &= progress[i] == target;
        }
        if (arrived && settled != requested) {
            settled = requested;
            if (listener != null) listener.changed(CLASSES[settled]);
        }
    }

    @Override public void draw(Batch batch, OrthographicCamera camera) {

        for (int i = 0; i < actors.length; i++) {
            int j = i;
            while (j > 0 && actors[drawOrder[j - 1]].footY() < actors[i].footY()) {
                drawOrder[j] = drawOrder[j - 1]; j--;
            }
            drawOrder[j] = i;
        }
        for (int index : drawOrder) {


            float alpha = 1f - focusAmount + focusAmount * progress[index];
            if (alpha > 0f) {
                DesktopMenuStyle.heroLight(batch, actors[index].centerX(), actors[index].footY(), focusAmount * progress[index]);
                actors[index].draw(batch, alpha);
            }
        }
    }
}
