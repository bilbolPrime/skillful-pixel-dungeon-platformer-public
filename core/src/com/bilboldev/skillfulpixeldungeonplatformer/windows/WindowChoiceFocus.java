package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import java.util.List;


public final class WindowChoiceFocus {
    private Button focused;
    private int rememberedIndex;
    private boolean keyboardActive = true;
    private int mouseX = Gdx.input.getX(), mouseY = Gdx.input.getY();

    public Button focused(List<? extends Button> choices) {
        if (focused != null && choices.contains(focused) && focused.canClick()) return focused;
        focused = null;
        for (int offset = 0; offset < choices.size(); offset++) {
            int index = (Math.min(rememberedIndex, choices.size() - 1) + offset) % choices.size();
            if (choices.get(index).canClick()) { focused = choices.get(index); rememberedIndex = index; break; }
        }
        return focused;
    }

    public boolean isKeyboardActive() { return keyboardActive; }

    public void pointerDown(float x, float y, List<? extends Button> choices) {
        keyboardActive = false;
        for (Button button : choices) {
            if (button.isHitProjected(x, y)) { focused = button; rememberedIndex = choices.indexOf(button); break; }
        }
    }

    public boolean keyDown(Window owner, int keycode, List<? extends Button> choices) {
        if (owner != null && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) { owner.hide(); return true; }
        boolean activate = keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER || keycode == Input.Keys.SPACE;
        boolean tab = keycode == Input.Keys.TAB;
        boolean arrow = keycode == Input.Keys.UP || keycode == Input.Keys.DOWN || keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT;
        if (!activate && !tab && !arrow) {
            if (owner != null && GameSettingsHelper.getInstance().getInteractBinding().matchesKey(keycode)) { owner.hide(); return true; }
            return false;
        }
        keyboardActive = true;
        Button current = focused(choices);
        if (current == null) return true;
        if (activate) { current.click(); return true; }
        if (tab) {
            int step = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? -1 : 1;
            int index = choices.indexOf(current);
            for (int count = 0; count < choices.size(); count++) {
                index = (index + step + choices.size()) % choices.size();
                if (choices.get(index).canClick()) { focused = choices.get(index); break; }
            }
        } else {
            float cx = current.x + current.getWidth() / 2f, cy = current.y + current.getHeight() / 2f;
            float bestScore = Float.MAX_VALUE;
            for (Button candidate : choices) {
                if (candidate == current || !candidate.canClick()) continue;
                float dx = candidate.x + candidate.getWidth() / 2f - cx;
                float dy = candidate.y + candidate.getHeight() / 2f - cy;
                float forward = keycode == Input.Keys.RIGHT ? dx : keycode == Input.Keys.LEFT ? -dx : keycode == Input.Keys.UP ? dy : -dy;
                float sideways = keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT ? Math.abs(dy) : Math.abs(dx);
                if (forward <= 1f) continue;
                float score = forward + sideways * 4f;
                if (score < bestScore) { bestScore = score; focused = candidate; }
            }
        }
        rememberedIndex = choices.indexOf(focused);
        return true;
    }

    public void draw(Window owner, Batch batch, List<? extends Button> choices) {
        if (WindowHelper.getInstance().topWindow() != owner) return;
        int nextX = Gdx.input.getX(), nextY = Gdx.input.getY();
        if (mouseX != nextX || mouseY != nextY) keyboardActive = false;
        mouseX = nextX; mouseY = nextY;
        Button button = focused(choices);
        if (button == null || !keyboardActive) return;
        Color previous = new Color(batch.getColor());
        batch.setColor(Color.GOLDENROD);
        float x = button.x - 5f, y = button.y - 5f, width = button.getWidth() + 10f, height = button.getHeight() + 10f;
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y, width, 3f);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y + height - 3f, width, 3f);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y, 3f, height);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x + width - 3f, y, 3f, height);
        batch.setColor(previous);
    }
}
