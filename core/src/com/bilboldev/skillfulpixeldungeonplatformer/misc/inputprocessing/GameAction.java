package com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing;

import com.badlogic.gdx.Input;


public enum GameAction {
    LEFT("windows.wndkeybindings.w", Input.Keys.A, "DPAD_LEFT"),
    RIGHT("windows.wndkeybindings.e", Input.Keys.D, "DPAD_RIGHT"),
    ENTER_DOOR("windows.wndkeybindings.enter_door", Input.Keys.W, "EAST"),
    INTERACT("windows.wndkeybindings.tag_action", Input.Keys.E, "NORTH"),
    EAT_FOOD("items.food.food.name", Input.Keys.NUM_1, "R3"),
    HEALTH_POTION("items.potions.potionofhealing.name", Input.Keys.NUM_2, "DPAD_UP"),
    MANA_POTION("custom.generated.potion_of_mana_7278278513", Input.Keys.NUM_3, "DPAD_DOWN"),
    INVENTORY("windows.wndkeybindings.inventory", Input.Keys.I, "SELECT"),
    ATTACK("scenes.gamescene.attack", 1, Input.Buttons.LEFT, "WEST"),
    RANGED("windows.wndkeybindings.ranged", 1, Input.Buttons.RIGHT, "R2"),
    QUICK_SKILL("windows.wndkeybindings.quickslot_1", Input.Keys.R, "R1"),
    QUICK_SKILL_2("windows.wndkeybindings.quickslot_2", Input.Keys.T, "L1+SOUTH"),
    QUICK_SKILL_3("windows.wndkeybindings.quickslot_3", Input.Keys.F, "L1+WEST"),
    QUICK_SKILL_4("windows.wndkeybindings.quickslot_4", Input.Keys.Z, "L1+NORTH"),
    QUICK_SKILL_5("windows.wndkeybindings.quickslot_5", Input.Keys.X, "L1+EAST"),
    QUICK_SKILL_6("windows.wndkeybindings.quickslot_6", Input.Keys.C, "L1+R2"),
    QUICK_SKILL_7("windows.wndkeybindings.quickslot_7", Input.Keys.V, "L1+R1"),
    JUMP("windows.wndkeybindings.jump", Input.Keys.SPACE, "SOUTH");

    public final String messageKey, controllerDefault;
    public final int keyboardType, keyboardCode;
    GameAction(String key, int code, String controller) { this(key, 0, code, controller); }
    GameAction(String key, int type, int code, String controller) {
        messageKey = key; keyboardType = type; keyboardCode = code; controllerDefault = controller;
    }
}
