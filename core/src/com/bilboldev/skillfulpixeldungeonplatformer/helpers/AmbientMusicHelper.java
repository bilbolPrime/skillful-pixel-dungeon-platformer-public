package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.audio.Music;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;

public class AmbientMusicHelper {

    private Music intro, background;

    private static AmbientMusicHelper m_instance;

    public static AmbientMusicHelper getSingleton(){
        if(m_instance == null){
            m_instance = new AmbientMusicHelper();
        }

        return m_instance;
    }

    private AmbientMusicHelper(){
        init();
    }

    public static void reset(){
        if (m_instance != null) {
            m_instance.stop();
        }
        m_instance = null;
    }

    void init(){
        background = AssetHelper.getInstance().getMusic(Sounds.DUNGEON);
        intro  = AssetHelper.getInstance().getMusic(Sounds.INTRO);
        background.setLooping(true);
        intro.setLooping(true);
    }

    public void playBackground(float volume){
        if (!GameSettingsHelper.getInstance().isMusicEnabled()) {
            stop();
            return;
        }
        intro.stop();
        if(!background.isPlaying()){
            background.setLooping(true);
            background.setVolume(volume * 0.5f);
            background.play();
        }
    }

    public void playIntro(float volume){
        if (!GameSettingsHelper.getInstance().isMusicEnabled()) {
            stop();
            return;
        }
        background.stop();
        if(!intro.isPlaying()){
            intro.setLooping(true);
            intro.setVolume(volume);
            intro.play();
        }
    }

    public boolean isPlaying(){
        return background.isPlaying() || intro.isPlaying();
    }

    public void setVolume(float volume){
        background.setVolume(volume * 0.5f);
        intro.setVolume(volume);
    }

    public void stop(){
        background.stop();
        intro.stop();
    }
}

