package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.audio.Sound;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

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
        if (!GameSettingsHelper.getInstance().isSoundFxEnabled()) {
            return;
        }
        getSound(sound).play(volume, 0.85f + RandomHelper.getInstance().randomFloat(0.3f) + extraPitch, 0f);
    }

    public void play(Sounds sound, float extraPitch){
        play(sound, extraPitch, 0.25f);
    }

    public void play(Sounds sound){
        if (!GameSettingsHelper.getInstance().isSoundFxEnabled()) {
            return;
        }
        getSound(sound).play(1f,0f,0f);
    }

    public void playUiClick() {
        if (!GameSettingsHelper.getInstance().isSoundFxEnabled()) {
            return;
        }

        getSound(Sounds.CLICK).play(1f, 1f, 0f);
    }

    public void dispose(){
        m_instance = null;
    }

    private Sound getSound(Sounds sound) {
        return AssetHelper.getInstance().getSound(sound);
    }
}

