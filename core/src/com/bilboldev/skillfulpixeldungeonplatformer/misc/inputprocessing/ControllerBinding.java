package com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing;

public final class ControllerBinding {
    public final ControllerButton button;
    public final boolean modified;
    private ControllerBinding(ControllerButton button, boolean modified) { this.button = button; this.modified = modified; }
    public static ControllerBinding of(ControllerButton button, boolean modified) {
        return button != null && button.bindable() ? new ControllerBinding(button, modified) : null;
    }
    public static ControllerBinding parse(String value) {
        if (value == null) return null;
        boolean modified = value.startsWith("L1+");
        try { return of(ControllerButton.valueOf(modified ? value.substring(3) : value), modified); }
        catch (IllegalArgumentException invalid) { return null; }
    }
    public boolean matches(ControllerButton button, boolean modified) { return this.button == button && this.modified == modified; }
    public String saveValue() { return (modified ? "L1+" : "") + button.name(); }
    public String label(ControllerButton.Layout layout) { return (modified ? ControllerButton.L1.label(layout) + "+" : "") + button.label(layout); }
}
