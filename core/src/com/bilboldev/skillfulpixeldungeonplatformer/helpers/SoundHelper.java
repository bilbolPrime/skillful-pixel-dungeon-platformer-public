package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.audio.Sound;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.WaterSplash.Contact;

public class SoundHelper {
    private static SoundHelper m_instance;


    public static SoundHelper GetSingleton(){
        if(m_instance == null){
            m_instance = new SoundHelper();
        }

        return m_instance;
    }

    public static  void reset(){
        m_instance = null;
    }

    private SoundHelper()
    {
    }


    public void play(Sounds sound, float extraPitch, float volume){
        play(sound, extraPitch, volume, true);
    }

    private void play(Sounds sound, float extraPitch, float volume, boolean audible) {
        if (!GameSettingsHelper.getInstance().isSoundFxEnabled()) {
            return;
        }
        float pitch = 0.85f + RandomHelper.getInstance().randomFloat(0.3f) + extraPitch;
        if (audible) getSound(sound).play(cueVolume(sound, volume), pitch, 0f);
    }


    public void playLegacyFootstep(float volume, boolean audible) {
        play(Sounds.STEP, 0f, volume, audible);
    }


    public void playFootContact(boolean wet, Contact contact) {
        if (contact == Contact.TAKEOFF || !GameSettingsHelper.getInstance().isSoundFxEnabled()) return;
        boolean step = contact == Contact.STEP;
        float volume = wet ? (step ? 0.22f : 0.32f) : (step ? 0.17f : 0.25f);
        getSound(wet ? Sounds.WATER : Sounds.STEP).play(volume, step ? 1.05f : 0.90f, 0f);
    }

    private float cueVolume(Sounds sound, float requested) {
        float gain;
        switch (sound) {
            case STEP: gain = 0.30f; break;
            case HIT: gain = 2f; break;
            case MISS: gain = 1.4f; break;
            case OPEN_DOOR: case GOLD: gain = 0.60f; break;
            case ITEM: gain = 0.55f; break;
            default: gain = 1f;
        }
        return Math.max(0f, Math.min(1f, requested * gain));
    }

    public void play(Sounds sound, float extraPitch){
        play(sound, extraPitch, 0.25f);
    }

    public void play(Sounds sound){
        if (!GameSettingsHelper.getInstance().isSoundFxEnabled()) {
            return;
        }
        getSound(sound).play(cueVolume(sound, 1f), 1f, 0f);
    }

    public void playUiClick() {
        if (!GameSettingsHelper.getInstance().isSoundFxEnabled()) {
            return;
        }

        getSound(Sounds.CLICK).play(1f, 1f, 0f);
    }


    public void playGunDryFire() {
        if (GameSettingsHelper.getInstance().isSoundFxEnabled()) getSound(Sounds.CLICK).play(.25f, 1f, 0f);
    }


    public void playGunContact(Sounds sound, float volume, boolean audible) {
        play(sound, 0f, volume, audible);
    }

    public void dispose(){
        m_instance = null;
    }

    private Sound getSound(Sounds sound) {
        return AssetHelper.getInstance().getSound(sound);
    }
}

