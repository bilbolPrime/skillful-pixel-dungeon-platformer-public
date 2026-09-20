package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.*;
import java.util.ArrayList;


public class PauseControlsWindow extends Window {
    private static final float BUTTON_WIDTH = 510, BUTTON_HEIGHT = 100, GAP = 14, COLUMN_GAP = 20, PADDING = 80;
    private static final GameAction[] TARGETS = {GameAction.LEFT, GameAction.ATTACK, GameAction.QUICK_SKILL,
            GameAction.RIGHT, GameAction.RANGED, GameAction.QUICK_SKILL_2,
            GameAction.JUMP, GameAction.EAT_FOOD, GameAction.QUICK_SKILL_3,
            GameAction.ENTER_DOOR, GameAction.HEALTH_POTION, GameAction.QUICK_SKILL_4,
            GameAction.INTERACT, GameAction.MANA_POTION, GameAction.QUICK_SKILL_5,
            GameAction.INVENTORY, GameAction.QUICK_SKILL_6, GameAction.QUICK_SKILL_7};
    private final ArrayList<PauseMenuRowButton> buttons = new ArrayList<>();
    private final WindowChoiceFocus keyboardFocus = new WindowChoiceFocus();
    private final boolean desktopTabs = !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
    private GameAction pendingBinding;
    private boolean controllerTab, ignoreNextTap;
    private PauseMenuRowButton pressedButton, keyboardTabButton, controllerTabButton;
    private ControllerButton.Layout lastLayout;
    private FontHelper.FittedTextBlock help;

    public PauseControlsWindow() { this(true); }
    public PauseControlsWindow(boolean showSaveExit) { super(1730, 1052); }

    @Override public Window build() {
        width = BUTTON_WIDTH * 3 + COLUMN_GAP * 2 + PADDING * 2;
        height = desktopTabs ? 1052 : 936;
        super.build(); buttons.clear();
        float firstY = y + height - (desktopTabs ? 164 : 74) - BUTTON_HEIGHT;
        for (int i = 0; i < TARGETS.length; i++) {
            final GameAction action = TARGETS[i];
            buttons.add(new PauseMenuRowButton(x + PADDING + i % 3 * (BUTTON_WIDTH + COLUMN_GAP),
                    firstY - i / 3 * (BUTTON_HEIGHT + GAP), BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override public void clicked() { pendingBinding = action; updateLabels(); }
            });
        }
        buttons.add(new PauseMenuRowButton(x + PADDING, firstY - 6 * (BUTTON_HEIGHT + GAP), width - PADDING * 2, BUTTON_HEIGHT) {
            @Override public void clicked() {
                if (controllerTab) GameSettingsHelper.getInstance().resetControllerDefaults();
                else GameSettingsHelper.getInstance().resetDefaults();
                pendingBinding = null; updateLabels();
            }
        });
        if (desktopTabs) {
            float tabWidth = (width - PADDING * 2 - COLUMN_GAP) / 2;
            keyboardTabButton = new PauseMenuRowButton(x + PADDING, y + height - 108, tabWidth, 64) {
                @Override public void clicked() { selectTab(false); }
            };
            controllerTabButton = new PauseMenuRowButton(x + PADDING + tabWidth + COLUMN_GAP, y + height - 108, tabWidth, 64) {
                @Override public void clicked() { selectTab(true); }
            };
            buttons.add(keyboardTabButton); buttons.add(controllerTabButton);
        }
        updateLabels(); return this;
    }
    private void selectTab(boolean controller) {
        controllerTab = controller; pendingBinding = null; clearPressedButton(); updateLabels();
    }
    public boolean awaitingController() { return controllerTab && pendingBinding != null; }
    public boolean awaitingKeyboard() { return !controllerTab && pendingBinding != null; }
    public void captureController(ControllerButton button, boolean modified) {
        if (!awaitingController() || !button.bindable()) return;
        GameSettingsHelper.getInstance().setControllerBinding(pendingBinding, ControllerBinding.of(button, modified));
        pendingBinding = null; updateLabels();
    }
    @Override public void draw(Batch batch) {
        if (lastLayout != ControllerInput.getInstance().layout()) updateLabels();
        super.draw(batch);
        for (PauseMenuRowButton button : buttons) button.draw(batch);
        if (desktopTabs) {
            PauseMenuRowButton active = controllerTab ? controllerTabButton : keyboardTabButton;
            DesktopMenuStyle.fill(batch, active.x + 12, active.y, active.getWidth() - 24, 3, DesktopMenuStyle.GOLD, 1);
            FontHelper.getSingleton().writeRaw(DesktopMenuStyle.INK, batch, help.size, x + PADDING, y + 65, help.text);
        }
        keyboardFocus.draw(this, batch, buttons);
    }
    @Override public boolean pointerDown(float x, float y, int button) {
        boolean tabHit = desktopTabs && (keyboardTabButton.isHitProjected(x, y) || controllerTabButton.isHitProjected(x, y));
        if (pendingBinding != null && !controllerTab && !tabHit) {
            GameSettingsHelper.getInstance().setBinding(GameSettingsHelper.getInstance().getBinding(pendingBinding),
                    GameSettingsHelper.INPUT_TYPE_MOUSE, button);
            pendingBinding = null; updateLabels(); ignoreNextTap = true; return true;
        }
        keyboardFocus.pointerDown(x, y, buttons); clearPressedButton();
        for (PauseMenuRowButton row : buttons) if (row.isHitProjected(x, y)) { pressedButton = row; row.pressDown(); break; }
        return true;
    }
    @Override public boolean tap(float x, float y) {
        if (ignoreNextTap) { ignoreNextTap = false; return true; }
        if (pressedButton != null) {
            PauseMenuRowButton button = pressedButton; pressedButton = null; button.releasePress();
            if (button.isHitProjected(x, y)) button.click(); else button.cancelPress();
        } else if (x < this.x || x > this.x + width || y < this.y || y > this.y + height) hide();
        return true;
    }
    @Override public boolean keyDown(int keycode) {
        clearPressedButton();
        if (pendingBinding != null) {
            if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) { pendingBinding = null; updateLabels(); }
            else if (!controllerTab) {
                GameSettingsHelper.getInstance().setBinding(GameSettingsHelper.getInstance().getBinding(pendingBinding),
                        GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                pendingBinding = null; updateLabels();
            }
            return true;
        }
        return keyboardFocus.keyDown(this, keycode, buttons);
    }
    @Override public void cancelPointerInput() { clearPressedButton(); ignoreNextTap = false; }
    private void clearPressedButton() { if (pressedButton != null) { pressedButton.cancelPress(); pressedButton = null; } }
    private void updateLabels() {
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        lastLayout = ControllerInput.getInstance().layout();
        for (int i = 0; i < TARGETS.length; i++) {
            GameAction action = TARGETS[i];
            String value = pendingBinding == action ? Messages.get(controllerTab ? "custom.controls.press_controller" : "windows.wndkeybindings.press_or_click")
                    : controllerTab ? settings.getControllerBinding(action).label(lastLayout) : settings.bindingLabel(settings.getBinding(action));
            buttons.get(i).setBindingRow(Messages.get(action.messageKey), value, pendingBinding == action);
        }
        buttons.get(TARGETS.length).setCenteredText(Messages.get("windows.wndkeybindings.default"));
        if (desktopTabs) {
            keyboardTabButton.setCenteredText(Messages.get("custom.controls.keyboard")).setCenteredTextColor(controllerTab ? Color.WHITE : Color.GOLD);
            controllerTabButton.setCenteredText(Messages.get("custom.controls.controller")).setCenteredTextColor(controllerTab ? Color.GOLD : Color.WHITE);
            help = FontHelper.getSingleton().fitLabelToBounds(Messages.get(controllerTab ? "custom.controls.controller_help" : "custom.controls.keyboard_help"),
                    2.2f, width - PADDING * 2, 52);
        }
    }
}
