package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

import java.util.Locale;

public final class GameSettingsHelper {

    private static final String PREFERENCES_NAME = "skillful-settings";
    private static final String KEY_MUSIC_ENABLED = "musicEnabled";
    private static final String KEY_SOUND_FX_ENABLED = "soundFxEnabled";
    private static final String KEY_LANGUAGE = "language";

    public static final int INPUT_TYPE_KEY = 0;
    public static final int INPUT_TYPE_MOUSE = 1;

    public static final class InputBinding {
        private int type;
        private int code;

        private InputBinding(int type, int code) {
            this.type = type;
            this.code = code;
        }

        public boolean matchesKey(int keycode) {
            return type == INPUT_TYPE_KEY && code == keycode;
        }

        public boolean matchesMouse(int button) {
            return type == INPUT_TYPE_MOUSE && code == button;
        }

        public void set(int type, int code) {
            this.type = type;
            this.code = code;
        }

        public int getType() {
            return type;
        }

        public int getCode() {
            return code;
        }
    }

    private static final GameSettingsHelper INSTANCE = new GameSettingsHelper();

    private boolean musicEnabled = true;
    private boolean soundFxEnabled = true;
    private Languages language = Languages.matchLocale(Locale.getDefault());
    private boolean preferencesLoaded;

    private final InputBinding moveLeft = new InputBinding(INPUT_TYPE_KEY, Input.Keys.A);
    private final InputBinding moveRight = new InputBinding(INPUT_TYPE_KEY, Input.Keys.D);
    private final InputBinding enterDoor = new InputBinding(INPUT_TYPE_KEY, Input.Keys.W);
    private final InputBinding interact = new InputBinding(INPUT_TYPE_KEY, Input.Keys.E);
    private final InputBinding eatFood = new InputBinding(INPUT_TYPE_KEY, Input.Keys.NUM_1);
    private final InputBinding healthPotion = new InputBinding(INPUT_TYPE_KEY, Input.Keys.NUM_2);
    private final InputBinding manaPotion = new InputBinding(INPUT_TYPE_KEY, Input.Keys.NUM_3);
    private final InputBinding inventory = new InputBinding(INPUT_TYPE_KEY, Input.Keys.I);
    private final InputBinding attack = new InputBinding(INPUT_TYPE_MOUSE, Input.Buttons.LEFT);
    private final InputBinding ranged = new InputBinding(INPUT_TYPE_MOUSE, Input.Buttons.RIGHT);
    private final InputBinding quickSkill = new InputBinding(INPUT_TYPE_KEY, Input.Keys.R);
    private final InputBinding quickSkill2 = new InputBinding(INPUT_TYPE_KEY, Input.Keys.T);
    private final InputBinding quickSkill3 = new InputBinding(INPUT_TYPE_KEY, Input.Keys.F);
    private final InputBinding quickSkill4 = new InputBinding(INPUT_TYPE_KEY, Input.Keys.Z);
    private final InputBinding quickSkill5 = new InputBinding(INPUT_TYPE_KEY, Input.Keys.X);
    private final InputBinding quickSkill6 = new InputBinding(INPUT_TYPE_KEY, Input.Keys.C);
    private final InputBinding quickSkill7 = new InputBinding(INPUT_TYPE_KEY, Input.Keys.V);
    private final InputBinding jump = new InputBinding(INPUT_TYPE_KEY, Input.Keys.SPACE);

    public static GameSettingsHelper getInstance() {
        return INSTANCE;
    }

    private GameSettingsHelper() {

    }

    public boolean isMusicEnabled() {
        ensureLoaded();
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        ensureLoaded();
        this.musicEnabled = musicEnabled;
        if (!musicEnabled) {
            AmbientMusicHelper.getSingleton().stop();
        }
        persistSettings();
    }

    public boolean isSoundFxEnabled() {
        ensureLoaded();
        return soundFxEnabled;
    }

    public void setSoundFxEnabled(boolean soundFxEnabled) {
        ensureLoaded();
        this.soundFxEnabled = soundFxEnabled;
        persistSettings();
    }

    public Languages getLanguage() {
        ensureLoaded();
        return language;
    }

    public Languages cycleLanguage() {
        Languages nextLanguage = getLanguage().next();
        setLanguage(nextLanguage);
        return nextLanguage;
    }

    public void setLanguage(Languages language) {
        ensureLoaded();
        this.language = language == null ? Languages.ENGLISH : language;
        Messages.setup(this.language);
        FontHelper.reset();
        persistSettings();
    }

    public void resetDefaults() {
        moveLeft.set(INPUT_TYPE_KEY, Input.Keys.A);
        moveRight.set(INPUT_TYPE_KEY, Input.Keys.D);
        enterDoor.set(INPUT_TYPE_KEY, Input.Keys.W);
        interact.set(INPUT_TYPE_KEY, Input.Keys.E);
        eatFood.set(INPUT_TYPE_KEY, Input.Keys.NUM_1);
        healthPotion.set(INPUT_TYPE_KEY, Input.Keys.NUM_2);
        manaPotion.set(INPUT_TYPE_KEY, Input.Keys.NUM_3);
        inventory.set(INPUT_TYPE_KEY, Input.Keys.I);
        attack.set(INPUT_TYPE_MOUSE, Input.Buttons.LEFT);
        ranged.set(INPUT_TYPE_MOUSE, Input.Buttons.RIGHT);
        quickSkill.set(INPUT_TYPE_KEY, Input.Keys.R);
        quickSkill2.set(INPUT_TYPE_KEY, Input.Keys.T);
        quickSkill3.set(INPUT_TYPE_KEY, Input.Keys.F);
        quickSkill4.set(INPUT_TYPE_KEY, Input.Keys.Z);
        quickSkill5.set(INPUT_TYPE_KEY, Input.Keys.X);
        quickSkill6.set(INPUT_TYPE_KEY, Input.Keys.C);
        quickSkill7.set(INPUT_TYPE_KEY, Input.Keys.V);
        jump.set(INPUT_TYPE_KEY, Input.Keys.SPACE);
    }

    public InputBinding getMoveLeftBinding() {
        return moveLeft;
    }

    public InputBinding getMoveRightBinding() {
        return moveRight;
    }

    public InputBinding getEnterDoorBinding() {
        return enterDoor;
    }

    public InputBinding getInteractBinding() {
        return interact;
    }

    public InputBinding getHealthPotionBinding() {
        return healthPotion;
    }

    public InputBinding getManaPotionBinding() {
        return manaPotion;
    }

    public InputBinding getEatFoodBinding() {
        return eatFood;
    }

    public InputBinding getInventoryBinding() {
        return inventory;
    }

    public InputBinding getAttackBinding() {
        return attack;
    }

    public InputBinding getRangedBinding() {
        return ranged;
    }

    public InputBinding getQuickSkillBinding() {
        return quickSkill;
    }

    public InputBinding getQuickSkill2Binding() {
        return quickSkill2;
    }

    public InputBinding getQuickSkill3Binding() {
        return quickSkill3;
    }

    public InputBinding getQuickSkill4Binding() {
        return quickSkill4;
    }

    public InputBinding getQuickSkill5Binding() {
        return quickSkill5;
    }

    public InputBinding getQuickSkill6Binding() {
        return quickSkill6;
    }

    public InputBinding getQuickSkill7Binding() {
        return quickSkill7;
    }

    public InputBinding getJumpBinding() {
        return jump;
    }

    public void setBinding(InputBinding binding, int type, int code) {
        binding.set(type, code);
    }

    public String bindingLabel(InputBinding binding) {
        return binding.getType() == INPUT_TYPE_MOUSE ? mouseButtonLabel(binding.getCode()) : keyLabel(binding.getCode());
    }

    public String keyLabel(int keycode) {
        String label = Input.Keys.toString(keycode);
        if (label == null || label.trim().isEmpty()) {
            return "?";
        }

        return label.toUpperCase(Locale.US);
    }

    public String mouseButtonLabel(int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                return "LC";
            case Input.Buttons.RIGHT:
                return "RC";
            case Input.Buttons.MIDDLE:
                return "MC";
            default:
                return "B" + button;
        }
    }

    private void ensureLoaded() {
        if (preferencesLoaded || Gdx.app == null) {
            return;
        }

        Preferences preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
        musicEnabled = preferences.getBoolean(KEY_MUSIC_ENABLED, musicEnabled);
        soundFxEnabled = preferences.getBoolean(KEY_SOUND_FX_ENABLED, soundFxEnabled);
        language = Languages.fromCode(preferences.getString(KEY_LANGUAGE, language.code()));
        preferencesLoaded = true;
    }

    private void persistSettings() {
        if (Gdx.app == null) {
            return;
        }

        Preferences preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
        preferences.putBoolean(KEY_MUSIC_ENABLED, musicEnabled);
        preferences.putBoolean(KEY_SOUND_FX_ENABLED, soundFxEnabled);
        preferences.putString(KEY_LANGUAGE, language.code());
        preferences.flush();
    }
}