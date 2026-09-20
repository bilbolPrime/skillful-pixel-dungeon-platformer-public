package com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing;

import com.badlogic.gdx.Input;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.GamepadService;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.GameScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseControlsWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.Window;


public final class ControllerInput {
    private static final ControllerInput INSTANCE = new ControllerInput();
    private static final ControllerButton[] BUTTONS = ControllerButton.values();
    private static final GameAction[] ACTIONS = GameAction.values();
    private final GamepadService.State state = new GamepadService.State();
    private BaseScreen owner;
    private Window modal;
    private DesktopInputProcessor gameplay;
    private long previous, blocked;
    private int connection = -1, revision = -1, menuDirection;
    private float repeatTime;
    private boolean focused = true, wasConnected, controllerLabels, stickArmed, analogLeft, analogRight;
    private ControllerButton jumpSource;
    private boolean jumpModified;

    private ControllerInput() { }
    public static ControllerInput getInstance() { return INSTANCE; }
    public boolean usesControllerLabels() { return controllerLabels; }
    public ControllerButton.Layout layout() { return state.layout; }
    public boolean connected() { return state.connected; }
    public String controllerName() { return state.name; }
    public void keyboardUsed() { controllerLabels = false; }
    public void setFocused(boolean value) { focused = value; suppress(); }

    public void update(float delta) {
        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) return;
        SkillfulPixelDungeonPlatformer.getPlatformProfile().gamepadService().poll(state);
        BaseScreen screen = SkillfulPixelDungeonPlatformer.getActiveScreen();
        Window window = WindowHelper.getInstance().topWindow();
        int currentRevision = GameSettingsHelper.getInstance().getBindingRevision();
        if (screen != owner || window != modal || state.connected != wasConnected
                || state.connection != connection || currentRevision != revision) {
            suppress(); owner = screen; modal = window; connection = state.connection; revision = currentRevision;
            wasConnected = state.connected;
            gameplay = screen instanceof GameScreen ? ((GameScreen) screen).desktopInput() : null;
        }
        if (!focused || !state.connected) {
            suppress(); if (!state.connected) controllerLabels = false;
            previous = state.buttons; return;
        }
        blocked &= state.buttons;
        long pressed = state.buttons & ~previous & ~blocked;
        previous = state.buttons;
        boolean modified = state.held(ControllerButton.L1);
        boolean capture = window instanceof PauseControlsWindow && ((PauseControlsWindow) window).awaitingController();
        if (gameplay == null && !capture) { suppress(); return; }
        if (pressed != 0) controllerLabels = true;
        if (window instanceof PauseControlsWindow && ((PauseControlsWindow) window).awaitingKeyboard()) {

            if ((pressed & ControllerButton.START.bit()) != 0) window.keyDown(Input.Keys.ESCAPE);
            releaseGameplay(); return;
        }
        if (capture) {
            for (ControllerButton button : BUTTONS) if ((pressed & button.bit()) != 0) {
                if (button == ControllerButton.START) window.keyDown(Input.Keys.ESCAPE);
                else if (button.bindable()) ((PauseControlsWindow) window).captureController(button, modified);
                if (!((PauseControlsWindow) window).awaitingController()) break;
            }
            releaseGameplay(); return;
        }
        if (window != null) {
            releaseGameplay(); navigateModal(window, pressed, delta); return;
        }
        Hero hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.isDead()) { suppress(); return; }
        if ((pressed & ControllerButton.START.bit()) != 0) {
            PauseMenuWindow.openGameplay(); suppress(); return;
        }
        if (!stickArmed && Math.abs(state.leftX) < .2f && Math.abs(state.leftY) < .2f) stickArmed = true;
        boolean left = stickArmed && state.leftX < (analogLeft ? -.2f : -.35f);
        boolean right = stickArmed && state.leftX > (analogRight ? .2f : .35f);
        if ((left && !analogLeft) || (right && !analogRight)) controllerLabels = true;
        analogLeft = left; analogRight = right;
        gameplay.setControllerMovement(left || held(GameAction.LEFT), right || held(GameAction.RIGHT));
        if (jumpSource != null && (!state.held(jumpSource) || modified != jumpModified)) {
            gameplay.setControllerJump(false); jumpSource = null;
        }
        for (ControllerButton button : BUTTONS) {
            if ((pressed & button.bit()) == 0 || !button.bindable()) continue;
            for (GameAction action : ACTIONS) if (GameSettingsHelper.getInstance().getControllerBinding(action).matches(button, modified)) {
                if (action == GameAction.JUMP) { jumpSource = button; jumpModified = modified; gameplay.setControllerJump(true); }
                else perform(action);
                break;
            }
            if (WindowHelper.getInstance().topWindow() != null || SkillfulPixelDungeonPlatformer.getActiveScreen() != owner) {
                suppress(); break;
            }
        }
    }

    private boolean held(GameAction action) {
        ControllerBinding binding = GameSettingsHelper.getInstance().getControllerBinding(action);
        return (state.buttons & ~blocked & binding.button.bit()) != 0 && binding.modified == state.held(ControllerButton.L1);
    }
    private void releaseGameplay() {
        if (gameplay != null) { gameplay.setControllerMovement(false, false); gameplay.setControllerJump(false); }
        jumpSource = null; analogLeft = analogRight = false;
    }
    private void suppress() {
        releaseGameplay(); blocked = state.buttons; previous = state.buttons;
        stickArmed = false; menuDirection = 0; repeatTime = 0;
    }

    private void navigateModal(Window window, long pressed, float delta) {

        if ((pressed & (ControllerButton.EAST.bit() | ControllerButton.START.bit() | ControllerButton.SELECT.bit())) != 0) {
            WindowHelper.getInstance().handleKeyDown(Input.Keys.ESCAPE); suppress(); return;
        }
        if ((pressed & ControllerButton.SOUTH.bit()) != 0) {
            WindowHelper.getInstance().handleKeyDown(Input.Keys.ENTER); suppress(); return;
        }
        if ((pressed & ControllerButton.R1.bit()) != 0) WindowHelper.getInstance().handleKeyDown(Input.Keys.TAB);
        if (!stickArmed && Math.abs(state.leftX) < .2f && Math.abs(state.leftY) < .2f) stickArmed = true;
        long available = state.buttons & ~blocked;
        int direction = (available & ControllerButton.DPAD_UP.bit()) != 0 ? Input.Keys.UP
                : (available & ControllerButton.DPAD_DOWN.bit()) != 0 ? Input.Keys.DOWN
                : (available & ControllerButton.DPAD_LEFT.bit()) != 0 ? Input.Keys.LEFT
                : (available & ControllerButton.DPAD_RIGHT.bit()) != 0 ? Input.Keys.RIGHT
                : stickArmed && state.leftY < -.5f ? Input.Keys.UP : stickArmed && state.leftY > .5f ? Input.Keys.DOWN
                : stickArmed && state.leftX < -.5f ? Input.Keys.LEFT : stickArmed && state.leftX > .5f ? Input.Keys.RIGHT : 0;
        repeatTime -= Float.isNaN(delta) || Float.isInfinite(delta) ? 0 : Math.max(0, Math.min(.1f, delta));
        if (direction != 0 && (direction != menuDirection || repeatTime <= 0)) {
            controllerLabels = true;
            WindowHelper.getInstance().handleKeyDown(direction);
            repeatTime = direction != menuDirection ? .35f : .12f;
        }
        menuDirection = direction;
        if (WindowHelper.getInstance().topWindow() != window) suppress();
    }

    private void perform(GameAction action) {
        UIHelper ui = UIHelper.getInstance();
        switch (action) {
            case ENTER_DOOR: MapHelper.getInstance().enterDoor(); break;
            case INTERACT: ui.performContextAction(); break;
            case INVENTORY: ui.performInventoryAction(); break;
            case ATTACK: ui.performPrimaryAction(); break;
            case RANGED: ui.performSecondaryAction(); break;
            case EAT_FOOD: ui.performEatFoodAction(); break;
            case HEALTH_POTION: ui.performHealthPotionAction(); break;
            case MANA_POTION: ui.performManaPotionAction(); break;
            case QUICK_SKILL: ui.performQuickSkillAction(); break;
            case QUICK_SKILL_2: ui.performQuickSkill2Action(); break;
            case QUICK_SKILL_3: ui.performQuickSkill3Action(); break;
            case QUICK_SKILL_4: ui.performQuickSkill4Action(); break;
            case QUICK_SKILL_5: ui.performQuickSkill5Action(); break;
            case QUICK_SKILL_6: ui.performQuickSkill6Action(); break;
            case QUICK_SKILL_7: ui.performQuickSkill7Action(); break;
            default: break;
        }
    }
}
