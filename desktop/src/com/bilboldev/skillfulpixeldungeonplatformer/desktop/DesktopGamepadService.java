package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerButton;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.GamepadService;
import org.lwjgl.glfw.GLFWGamepadState;
import java.util.Locale;
import static org.lwjgl.glfw.GLFW.*;


public final class DesktopGamepadService implements GamepadService {
    private final GLFWGamepadState nativeState = GLFWGamepadState.create();
    private static final ControllerButton[] BUTTONS = {ControllerButton.SOUTH, ControllerButton.EAST, ControllerButton.WEST,
            ControllerButton.NORTH, ControllerButton.L1, ControllerButton.R1, ControllerButton.SELECT, ControllerButton.START,
            null, ControllerButton.L3, ControllerButton.R3, ControllerButton.DPAD_UP, ControllerButton.DPAD_RIGHT,
            ControllerButton.DPAD_DOWN, ControllerButton.DPAD_LEFT};
    private int joystick = -1, generation;
    private boolean unavailable, leftTrigger, rightTrigger;
    private String name = "";
    private ControllerButton.Layout layout = ControllerButton.Layout.XBOX;

    @Override public void poll(State result) {
        result.clear();
        if (unavailable) return;
        try {
            if (joystick >= 0 && !glfwJoystickIsGamepad(joystick)) { joystick = -1; generation++; }
            if (joystick < 0) for (int id = GLFW_JOYSTICK_1; id <= GLFW_JOYSTICK_LAST; id++) {
                if (!glfwJoystickIsGamepad(id)) continue;
                joystick = id; generation++; leftTrigger = rightTrigger = false;
                String mappedName = glfwGetGamepadName(id);
                name = mappedName == null ? "Gamepad" : mappedName;
                String lower = name.toLowerCase(Locale.ROOT);
                layout = lower.contains("playstation") || lower.contains("dualshock") || lower.contains("dualsense") || lower.contains("ps4") || lower.contains("ps5")
                        ? ControllerButton.Layout.PLAYSTATION : lower.contains("nintendo") || lower.contains("switch") || lower.contains("joy-con")
                        ? ControllerButton.Layout.NINTENDO : ControllerButton.Layout.XBOX;
                break;
            }
            if (joystick < 0) return;
            if (!glfwGetGamepadState(joystick, nativeState)) { joystick = -1; generation++; return; }
            result.connected = true; result.connection = generation; result.name = name; result.layout = layout;
            for (int i = 0; i < BUTTONS.length; i++)
                if (BUTTONS[i] != null && nativeState.buttons(i) == GLFW_PRESS) result.buttons |= BUTTONS[i].bit();

            leftTrigger = trigger(nativeState.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER), leftTrigger);
            rightTrigger = trigger(nativeState.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER), rightTrigger);
            if (leftTrigger) result.buttons |= ControllerButton.L2.bit();
            if (rightTrigger) result.buttons |= ControllerButton.R2.bit();
            result.leftX = finiteAxis(nativeState.axes(GLFW_GAMEPAD_AXIS_LEFT_X));
            result.leftY = finiteAxis(nativeState.axes(GLFW_GAMEPAD_AXIS_LEFT_Y));
        } catch (RuntimeException | LinkageError failure) {
            unavailable = true; result.clear();
            System.err.println("[Controller] Controller input unavailable; keyboard and mouse remain active: " + failure.getClass().getSimpleName());
        }
    }
    private static float finiteAxis(float value) { return Float.isNaN(value) || Float.isInfinite(value) ? 0 : Math.max(-1, Math.min(1, value)); }
    private static boolean trigger(float value, boolean held) {
        return !Float.isNaN(value) && !Float.isInfinite(value) && value > (held ? -.35f : -.1f);
    }
}
