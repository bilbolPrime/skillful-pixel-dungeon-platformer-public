package com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing;


public enum ControllerButton {
    SOUTH("A", "Cross", "B"), EAST("B", "Circle", "A"), WEST("X", "Square", "Y"), NORTH("Y", "Triangle", "X"),
    L1("LB", "L1", "L"), R1("RB", "R1", "R"), SELECT("View", "Share", "Minus"), START("Start", "Options", "Plus"),
    L3("L3"), R3("R3"), DPAD_UP("D-Up"), DPAD_RIGHT("D-Right"), DPAD_DOWN("D-Down"), DPAD_LEFT("D-Left"),
    L2("LT", "L2", "ZL"), R2("RT", "R2", "ZR");

    public enum Layout { XBOX, PLAYSTATION, NINTENDO }
    private final String xbox, playstation, nintendo;
    ControllerButton(String label) { this(label, label, label); }
    ControllerButton(String xbox, String playstation, String nintendo) {
        this.xbox = xbox; this.playstation = playstation; this.nintendo = nintendo;
    }
    public long bit() { return 1L << ordinal(); }
    public String label(Layout layout) { return layout == Layout.PLAYSTATION ? playstation : layout == Layout.NINTENDO ? nintendo : xbox; }
    public boolean bindable() { return this != L1 && this != START; }
}
