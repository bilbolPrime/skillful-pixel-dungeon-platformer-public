package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.*;
import java.util.*;
import java.util.function.IntConsumer;

public final class PartyWindow extends Window {
    private final Window view;
    private final List<String> labels;
    private final List<Boolean> enabled;
    private final IntConsumer choose;
    private final WindowChoiceFocus focus = new WindowChoiceFocus();
    private final List<Button> buttons = new ArrayList<>();
    private boolean dismissed;

    PartyWindow(Window view, List<String> labels, List<Boolean> enabled, IntConsumer choose) {
        super(view.partyWidth(), view.partyHeight());
        this.view = view; this.labels = labels; this.enabled = enabled; this.choose = choose;
    }
    @Override public Window build() {
        view.build();
        List<? extends Button> original = view.partyChoices();
        if (original.size() != labels.size()) throw new IllegalArgumentException("menu_choices");
        buttons.clear();
        for (int i = 0; i < original.size(); i++) {
            final int index = i; final Button button = original.get(i);
            buttons.add(new Button(button.x, button.y, button.getWidth(), button.getHeight(), "images/misc/transparent.png", "images/misc/transparent.png") {
                @Override public String partyLabel() { return labels.get(index); }
                @Override public boolean canClick() { return !dismissed && enabled.get(index); }
                @Override public boolean isHitProjected(float x, float y) { return canClick() && button.isHitProjected(x, y); }
                @Override public void click() { if (canClick()) choose.accept(index); }
            });
        }
        return this;
    }
    @Override public boolean contains(float x, float y) { return view.contains(x, y); }
    @Override public void draw(Batch batch) { view.draw(batch); focus.draw(this, batch, buttons); }
    @Override public List<Button> partyChoices() { return buttons; }
    @Override public String partyText() { return view.partyText(); }
    @Override public boolean pointerDown(float x, float y, int button) { focus.pointerDown(x, y, buttons); return true; }
    @Override public boolean click(float x, float y) {
        for (Button button : buttons) if (button.isHitProjected(x, y)) { button.click(); return true; }
        if (!contains(x, y)) hide();
        return true;
    }
    @Override public boolean keyDown(int key) {
        if (view instanceof TextWindow && (key == Input.Keys.ENTER || key == Input.Keys.NUMPAD_ENTER || key == Input.Keys.SPACE)) { hide(); return true; }
        return focus.keyDown(this, key, buttons);
    }

    @Override public void refresh() { }
    @Override public void hide() {
        if (dismissed) return;
        dismissed = true; choose.accept(-1); super.hide();
    }
}
