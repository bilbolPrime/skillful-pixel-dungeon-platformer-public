package com.bilboldev.skillfulpixeldungeonplatformer.platform;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerButton;


public interface GamepadService {
    void poll(State state);
    GamepadService NONE = state -> state.clear();
    final class State {
        public boolean connected;
        public int connection;
        public long buttons;
        public float leftX, leftY;
        public ControllerButton.Layout layout = ControllerButton.Layout.XBOX;
        public String name = "";
        public boolean held(ControllerButton button) { return (buttons & button.bit()) != 0; }
        public void clear() { connected = false; buttons = 0; leftX = leftY = 0; name = ""; }
    }
}
