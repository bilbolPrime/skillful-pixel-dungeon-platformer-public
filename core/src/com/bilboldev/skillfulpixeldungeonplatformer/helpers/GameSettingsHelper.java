package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

import java.util.Locale;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.GameAction;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerBinding;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput;

public final class GameSettingsHelper {

    private static final String PREFERENCES_NAME = "skillful-settings";
    private static final String KEY_MUSIC_ENABLED = "musicEnabled";
    private static final String KEY_SOUND_FX_ENABLED = "soundFxEnabled";
    private static final String KEY_REDUCED_CAMERA_MOTION = "reducedCameraMotion";
    private static final String KEY_REDUCED_VISUAL_EFFECTS = "reducedVisualEffects";
    private static final String KEY_BACKGROUND_ROOMS = "backgroundRooms";
    private static final String KEY_PLATFORM_SHADOWS = "platformShadows";
    private static final String KEY_LANGUAGE = "language";

    public static final int INPUT_TYPE_KEY = 0;
    public static final int INPUT_TYPE_MOUSE = 1;

    public static final class InputBinding {
        private int type;
        private int code;
        public final GameAction action;

        private InputBinding(GameAction action) {
            this.action = action;
            set(action.keyboardType, action.keyboardCode);
        }

        public boolean matchesKey(int keycode) {
            return type == INPUT_TYPE_KEY && code == keycode;
        }

        public boolean matchesMouse(int button) {
            return type == INPUT_TYPE_MOUSE && code == button;
        }

        private void set(int type, int code) {
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
    private boolean reducedCameraMotion;
    private boolean reducedVisualEffects;
    private boolean backgroundRoomsEnabled = true;
    private boolean platformShadowsEnabled = environmentEffectsDefault();
    private Languages language = Languages.matchLocale(Locale.getDefault());
    private boolean preferencesLoaded;

    private final InputBinding[] keyboardBindings = createKeyboardBindings();
    private final ControllerBinding[] controllerBindings = createControllerBindings();
    private int bindingRevision;

    private static InputBinding[] createKeyboardBindings() {
        GameAction[] actions = GameAction.values();
        InputBinding[] result = new InputBinding[actions.length];
        for (GameAction action : actions) result[action.ordinal()] = new InputBinding(action);
        return result;
    }
    private static ControllerBinding[] createControllerBindings() {
        GameAction[] actions = GameAction.values();
        ControllerBinding[] result = new ControllerBinding[actions.length];
        for (GameAction action : actions) result[action.ordinal()] = ControllerBinding.parse(action.controllerDefault);
        return result;
    }

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

    public boolean isReducedCameraMotion() {
        ensureLoaded();
        return reducedCameraMotion;
    }

    public boolean isBackgroundRoomsEnabled() {
        ensureLoaded();
        return backgroundRoomsEnabled;
    }

    public boolean isPlatformShadowsEnabled() {
        ensureLoaded();
        return platformShadowsEnabled;
    }

    public void setPlatformShadowsEnabled(boolean enabled) {
        ensureLoaded();
        platformShadowsEnabled = enabled;
        persistSettings();
    }

    private static boolean environmentEffectsDefault() {
        return !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
    }

    public void setBackgroundRoomsEnabled(boolean enabled) {
        ensureLoaded();
        if (backgroundRoomsEnabled != enabled) {
            backgroundRoomsEnabled = enabled;

            MapHelper.getInstance().clearRoomAppearances();
        }
        persistSettings();
    }

    public void setReducedCameraMotion(boolean reducedCameraMotion) {
        ensureLoaded();
        this.reducedCameraMotion = reducedCameraMotion;
        persistSettings();
    }

    public boolean isReducedVisualEffects() {
        ensureLoaded();
        return reducedVisualEffects;
    }

    public void setReducedVisualEffects(boolean reducedVisualEffects) {
        ensureLoaded();
        this.reducedVisualEffects = reducedVisualEffects;
        persistSettings();
    }


    public float getVisualEffectIntensity() {
        return isReducedVisualEffects() ? 0.35f : 1f;
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

    public void resetDefaults() { ensureLoaded(); resetKeyboardBindings(); persistSettings(); }
    public void resetControllerDefaults() { ensureLoaded(); resetControllerBindings(); persistSettings(); }
    private void resetKeyboardBindings() {
        for (GameAction action : GameAction.values())
            keyboardBindings[action.ordinal()].set(action.keyboardType, action.keyboardCode);
        bindingRevision++;
    }
    private void resetControllerBindings() {
        for (GameAction action : GameAction.values())
            controllerBindings[action.ordinal()] = ControllerBinding.parse(action.controllerDefault);
        bindingRevision++;
    }
    public InputBinding getBinding(GameAction action) { ensureLoaded(); return keyboardBindings[action.ordinal()]; }
    public ControllerBinding getControllerBinding(GameAction action) { ensureLoaded(); return controllerBindings[action.ordinal()]; }
    public int getBindingRevision() { ensureLoaded(); return bindingRevision; }

    public InputBinding getMoveLeftBinding() {
        return getBinding(GameAction.LEFT);
    }

    public InputBinding getMoveRightBinding() {
        return getBinding(GameAction.RIGHT);
    }

    public InputBinding getEnterDoorBinding() {
        return getBinding(GameAction.ENTER_DOOR);
    }

    public InputBinding getInteractBinding() {
        return getBinding(GameAction.INTERACT);
    }

    public InputBinding getHealthPotionBinding() {
        return getBinding(GameAction.HEALTH_POTION);
    }

    public InputBinding getManaPotionBinding() {
        return getBinding(GameAction.MANA_POTION);
    }

    public InputBinding getEatFoodBinding() {
        return getBinding(GameAction.EAT_FOOD);
    }

    public InputBinding getInventoryBinding() {
        return getBinding(GameAction.INVENTORY);
    }

    public InputBinding getAttackBinding() {
        return getBinding(GameAction.ATTACK);
    }

    public InputBinding getRangedBinding() {
        return getBinding(GameAction.RANGED);
    }

    public InputBinding getQuickSkillBinding() {
        return getBinding(GameAction.QUICK_SKILL);
    }

    public InputBinding getQuickSkill2Binding() {
        return getBinding(GameAction.QUICK_SKILL_2);
    }

    public InputBinding getQuickSkill3Binding() {
        return getBinding(GameAction.QUICK_SKILL_3);
    }

    public InputBinding getQuickSkill4Binding() {
        return getBinding(GameAction.QUICK_SKILL_4);
    }

    public InputBinding getQuickSkill5Binding() {
        return getBinding(GameAction.QUICK_SKILL_5);
    }

    public InputBinding getQuickSkill6Binding() {
        return getBinding(GameAction.QUICK_SKILL_6);
    }

    public InputBinding getQuickSkill7Binding() {
        return getBinding(GameAction.QUICK_SKILL_7);
    }

    public InputBinding getJumpBinding() {
        return getBinding(GameAction.JUMP);
    }

    public void setBinding(InputBinding binding, int type, int code) {
        ensureLoaded();
        if (binding == null || !validKeyboardBinding(type, code)) { resetKeyboardBindings(); persistSettings(); return; }

        for (InputBinding other : keyboardBindings)
            if (other != binding && other.type == type && other.code == code) other.set(binding.type, binding.code);
        binding.set(type, code); bindingRevision++; persistSettings();
    }

    public void setControllerBinding(GameAction action, ControllerBinding binding) {
        ensureLoaded();
        if (action == null || binding == null) { resetControllerBindings(); persistSettings(); return; }
        int index = action.ordinal();
        for (int i = 0; i < controllerBindings.length; i++)
            if (i != index && controllerBindings[i].saveValue().equals(binding.saveValue())) controllerBindings[i] = controllerBindings[index];
        controllerBindings[index] = binding; bindingRevision++; persistSettings();
    }

    public static boolean validKeyboardBinding(int type, int code) {
        if (type == INPUT_TYPE_MOUSE) return code >= Input.Buttons.LEFT && code <= Input.Buttons.FORWARD;
        if (type != INPUT_TYPE_KEY || code <= Input.Keys.UNKNOWN || code > Input.Keys.MAX_KEYCODE
                || code == Input.Keys.ESCAPE || code == Input.Keys.BACK) return false;
        try { return Input.Keys.toString(code) != null; } catch (RuntimeException invalid) { return false; }
    }

    public String hudBindingLabel(InputBinding binding) {
        return ControllerInput.getInstance().usesControllerLabels()
                ? getControllerBinding(binding.action).label(ControllerInput.getInstance().layout()) : bindingLabel(binding);
    }

    public String bindingLabel(InputBinding binding) {
        return binding.getType() == INPUT_TYPE_MOUSE ? mouseButtonLabel(binding.getCode()) : keyLabel(binding.getCode());
    }

    public String keyLabel(int keycode) {
        if (keycode < 0 || keycode > Input.Keys.MAX_KEYCODE) return "?";
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
        reducedCameraMotion = preferences.getBoolean(KEY_REDUCED_CAMERA_MOTION, false);
        reducedVisualEffects = preferences.getBoolean(KEY_REDUCED_VISUAL_EFFECTS, false);
        backgroundRoomsEnabled = preferences.getBoolean(KEY_BACKGROUND_ROOMS, true);
        platformShadowsEnabled = preferences.getBoolean(KEY_PLATFORM_SHADOWS, environmentEffectsDefault());
        language = Languages.fromCode(preferences.getString(KEY_LANGUAGE, language.code()));
        preferencesLoaded = true;
        loadBindings(preferences);
    }

    private void loadBindings(Preferences preferences) {
        boolean badKeyboard = false, badController = false;
        java.util.HashSet<String> keyboardSeen = new java.util.HashSet<>(), controllerSeen = new java.util.HashSet<>();
        for (GameAction action : GameAction.values()) {
            String keyboardKey = "controls.keyboard." + action.name(), controllerKey = "controls.controller." + action.name();
            InputBinding keyboard = keyboardBindings[action.ordinal()];
            try {
                int type = preferences.getInteger(keyboardKey + ".type", action.keyboardType);
                int code = preferences.getInteger(keyboardKey + ".code", action.keyboardCode);
                if (!validKeyboardBinding(type, code) || !keyboardSeen.add(type + ":" + code)) badKeyboard = true;
                else keyboard.set(type, code);
            } catch (RuntimeException invalid) { badKeyboard = true; }
            try {
                ControllerBinding binding = ControllerBinding.parse(preferences.getString(controllerKey, action.controllerDefault));
                if (binding == null || !controllerSeen.add(binding.saveValue())) badController = true;
                else controllerBindings[action.ordinal()] = binding;
            } catch (RuntimeException invalid) { badController = true; }
        }

        if (badKeyboard) resetKeyboardBindings();
        if (badController) resetControllerBindings();
        if (badKeyboard || badController) persistSettings();
    }

    private void persistSettings() {
        if (Gdx.app == null) {
            return;
        }

        Preferences preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
        preferences.putBoolean(KEY_MUSIC_ENABLED, musicEnabled);
        preferences.putBoolean(KEY_SOUND_FX_ENABLED, soundFxEnabled);
        preferences.putBoolean(KEY_REDUCED_CAMERA_MOTION, reducedCameraMotion);
        preferences.putBoolean(KEY_REDUCED_VISUAL_EFFECTS, reducedVisualEffects);
        preferences.putBoolean(KEY_BACKGROUND_ROOMS, backgroundRoomsEnabled);
        preferences.putBoolean(KEY_PLATFORM_SHADOWS, platformShadowsEnabled);
        preferences.putString(KEY_LANGUAGE, language.code());
        for (GameAction action : GameAction.values()) {
            InputBinding binding = keyboardBindings[action.ordinal()];
            String prefix = "controls.keyboard." + action.name();
            preferences.putInteger(prefix + ".type", binding.type).putInteger(prefix + ".code", binding.code);
            preferences.putString("controls.controller." + action.name(), controllerBindings[action.ordinal()].saveValue());
        }
        preferences.flush();
    }
}
