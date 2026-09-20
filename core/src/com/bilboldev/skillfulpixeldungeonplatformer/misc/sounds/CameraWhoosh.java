package com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AssetHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;


public final class CameraWhoosh {

    private static final float SAMPLE_SECONDS = 0.418f;
    private Sound sound;
    private long voice = -1;
    private float basePitch, peakVolume, panDirection;
    private int zoomDirection;

    public void startPan(float distance, float duration) {
        stop();
        if (Math.abs(distance) < 1f || duration <= 0f) return;
        basePitch = SAMPLE_SECONDS / duration;
        peakVolume = 0.26f * Math.min(1f, Math.abs(distance) / 1250f);
        panDirection = Math.signum(distance);
        zoomDirection = 0;
        start();
    }

    public void startZoom(boolean zoomingIn, float duration) {
        stop();
        if (duration <= 0f) return;
        basePitch = SAMPLE_SECONDS / duration;
        peakVolume = 0.24f;
        panDirection = 0f;
        zoomDirection = zoomingIn ? 1 : -1;
        start();
    }

    private void start() {
        if (!audible()) return;
        sound = AssetHelper.getInstance().getSound(Sounds.MISS);
        voice = sound.play(0f, pitch(0f), panDirection * 0.22f);
    }


    public void update(float progress) {
        if (sound == null || voice < 0) return;
        if (!audible() || !Float.isFinite(progress) || progress >= 1f) { stop(); return; }
        float t = MathUtils.clamp(progress, 0f, 1f);
        float speed = 4f * t * (1f - t);
        sound.setPitch(voice, pitch(t));
        sound.setPan(voice, panDirection * (0.22f - 0.44f * t), peakVolume * speed);
    }

    private float pitch(float t) {
        float value = zoomDirection == 0 ? basePitch * (0.88f + 0.22f * 4f * t * (1f - t))
                : basePitch + zoomDirection * 0.24f * (2f * t - 1f);
        return MathUtils.clamp(value, 0.5f, 2f);
    }

    private boolean audible() {
        return GameSettingsHelper.getInstance().isSoundFxEnabled()
                && !GameSettingsHelper.getInstance().isReducedCameraMotion();
    }


    public void stop() {
        if (sound != null && voice >= 0) sound.stop(voice);
        sound = null;
        voice = -1;
    }
}
