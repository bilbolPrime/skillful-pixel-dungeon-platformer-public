package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import java.util.ArrayList;
import java.util.List;


final class MenuContentView {
    static final float LEFT = 544, BOTTOM = 248, SCALE = 0.74f;
    private final MenuScreenBase content;
    private final Runnable changed;
    private final ArrayList<ActionButton> controls = new ArrayList<>();
    private final Matrix4 previous = new Matrix4(), transformed = new Matrix4();

    MenuContentView(MenuScreenBase content, Runnable changed) {
        this.content = content; this.changed = changed;
        content.hostedContent = true;
        content.createMenuContent();
        syncControls();
    }

    List<ActionButton> controls() { return controls; }
    void rebindLanguage() {
        content.buttons.clear();
        content.createMenuContent();
        syncControls();
    }
    void act(float delta) { content.actMenu(delta); }
    void draw(Batch batch, Button selected) {
        content.hostedSelection = selected instanceof Control ? ((Control) selected).source : null;
        previous.set(batch.getTransformMatrix());
        transformed.set(previous).translate(LEFT, BOTTOM, 0).scale(SCALE, SCALE, 1);
        batch.setTransformMatrix(transformed);
        try { content.drawMenu(batch); }
        finally { batch.setTransformMatrix(previous); }
    }

    private void syncControls() {
        boolean same = controls.size() == content.buttons.size();
        for (int i = 0; same && i < controls.size(); i++) same = ((Control) controls.get(i)).source == content.buttons.get(i);
        if (same) return;
        controls.clear();
        for (ActionButton button : content.buttons) controls.add(new Control(button));
        changed.run();
    }

    private final class Control extends ActionButton {
        private final ActionButton source;
        Control(ActionButton source) {
            super(LEFT + source.x * SCALE, BOTTOM + source.y * SCALE, source.getWidth() * SCALE, source.getHeight() * SCALE,
                    "images/misc/transparent.png", "images/misc/transparent.png");
            this.source = source;
        }
        @Override public boolean canClick() { return content.buttons.contains(source) && source.canClick(); }
        @Override public boolean isHitProjected(float x, float y) {
            return canClick() && source.isHitProjected((x - LEFT) / SCALE, (y - BOTTOM) / SCALE);
        }
        @Override public void pressDown() { source.pressDown(); }
        @Override public void releasePress() { source.releasePress(); }
        @Override public void cancelPress() { source.cancelPress(); }
        @Override public void click() { if (canClick()) { source.click(); syncControls(); } }
        @Override public void draw(Batch batch) {                                                         }
    }
}
