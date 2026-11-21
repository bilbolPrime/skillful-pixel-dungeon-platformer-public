package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PartyTextInputWindow extends DescriptionWindow {
    private static final int MAX_LENGTH = 64;
    private final Input.TextInputListener listener;
    private final String hint;
    private final List<Button> buttons = new ArrayList<>();
    private String text = "";
    private int caret, anchor, focus;
    private boolean completed;

    public PartyTextInputWindow(String title, String hint, Input.TextInputListener listener) {
        super("images/weapons/sword.png", title, 1500, 720);
        this.hint = hint;
        this.listener = listener;
        setReservedBottomHeight(410);
    }

    @Override public Window build() {
        super.build();
        buttons.clear();
        buttons.add(new RedButton(x + 200, y + 115, 530, 110) {
            @Override public void click() { submit(); }
        }.setText(Messages.get("party.accept")));
        buttons.add(new RedButton(x + 770, y + 115, 530, 110) {
            @Override public void click() { hide(); }
        }.setText(Messages.get("party.cancel")));
        return this;
    }

    @Override protected List<? extends Button> getKeyboardChoices() {
        return focus == 0 || buttons.isEmpty() ? Collections.emptyList() : Collections.singletonList(buttons.get(focus - 1));
    }

    @Override public List<? extends Button> partyChoices() { return buttons; }

    @Override public void draw(Batch batch) {
        super.draw(batch);
        for (Button button : buttons) button.draw(batch);
        DesktopMenuStyle.fill(batch, x + 197, y + 277, 1106, 106, focus == 0 ? Color.GOLDENROD : Color.GRAY, 1f);
        DesktopMenuStyle.fill(batch, x + 200, y + 280, 1100, 100, Color.BLACK, 1f);
        FontHelper font = FontHelper.getSingleton();
        String shown = text.isEmpty() ? hint : text;
        float size = font.fitSize(shown, 3f, 1040, 60);
        float baseline = y + 330 + font.measure(Color.WHITE, size, shown).height / 2f;
        float caretX = x + 230 + font.measure(Color.WHITE, size, text.substring(0, caret)).width;
        float anchorX = x + 230 + font.measure(Color.WHITE, size, text.substring(0, anchor)).width;
        if (focus == 0 && caret != anchor)
            DesktopMenuStyle.fill(batch, Math.min(caretX, anchorX), y + 295, Math.abs(caretX - anchorX), 70, Color.ROYAL, 0.7f);
        font.writeRaw(text.isEmpty() ? Color.GRAY : Color.WHITE, batch, size, x + 230, baseline, shown);
        if (focus == 0 && System.currentTimeMillis() % 1000 < 600)
            DesktopMenuStyle.fill(batch, caretX, y + 295, 3, 70, Color.WHITE, 1f);
    }

    @Override public boolean click(float pointX, float pointY) {
        for (Button button : buttons) {
            if (button.isHitProjected(pointX, pointY)) { button.click(); return true; }
        }
        if (pointX >= x + 200 && pointX <= x + 1300 && pointY >= y + 280 && pointY <= y + 380) {
            focus = 0;
            FontHelper font = FontHelper.getSingleton();
            float size = font.fitSize(text.isEmpty() ? hint : text, 3f, 1040, 60);
            caret = 0;
            while (caret < text.length()) {
                float left = font.measure(Color.WHITE, size, text.substring(0, caret)).width;
                float right = font.measure(Color.WHITE, size, text.substring(0, caret + 1)).width;
                if (pointX - x - 230 < (left + right) / 2f) break;
                caret++;
            }
            anchor = caret;
            return true;
        }
        return super.click(pointX, pointY);
    }

    @Override public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) { hide(); return true; }
        if (key == Input.Keys.TAB) { focus = (focus + (shift() ? 2 : 1)) % 3; return true; }
        if (key == Input.Keys.ENTER || key == Input.Keys.NUMPAD_ENTER) {
            if (focus == 2) hide(); else submit();
            return true;
        }
        if (focus != 0) {
            if (key == Input.Keys.SPACE) buttons.get(focus - 1).click();
            return true;
        }
        if (shortcut()) {
            if (key == Input.Keys.A) { anchor = 0; caret = text.length(); }
            else if (key == Input.Keys.V) insert(Gdx.app.getClipboard().getContents());
            else if ((key == Input.Keys.C || key == Input.Keys.X) && anchor != caret) {
                Gdx.app.getClipboard().setContents(text.substring(Math.min(anchor, caret), Math.max(anchor, caret)));
                if (key == Input.Keys.X) insert("");
            }
            return true;
        }
        if (key == Input.Keys.BACKSPACE) {
            if (anchor != caret) insert("");
            else if (caret > 0) { anchor = caret - 1; insert(""); }
        } else if (key == Input.Keys.FORWARD_DEL) {
            if (anchor != caret) insert("");
            else if (caret < text.length()) { anchor = caret + 1; insert(""); }
        } else {
            if (key == Input.Keys.LEFT) caret = !shift() && anchor != caret ? Math.min(anchor, caret) : Math.max(0, caret - 1);
            else if (key == Input.Keys.RIGHT) caret = !shift() && anchor != caret ? Math.max(anchor, caret) : Math.min(text.length(), caret + 1);
            else if (key == Input.Keys.HOME) caret = 0;
            else if (key == Input.Keys.END) caret = text.length();
            else return true;
            if (!shift()) anchor = caret;
        }
        return true;
    }

    @Override public boolean keyTyped(char character) {
        if (focus == 0 && !shortcut() && character >= 32 && character < 127) insert(String.valueOf(character));
        return true;
    }

    private void insert(String value) {
        if (value == null) return;
        int start = Math.min(anchor, caret), end = Math.max(anchor, caret);
        int available = MAX_LENGTH - (text.length() - (end - start));
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < value.length() && input.length() < available; i++) {
            char character = value.charAt(i);
            if (character >= 32 && character < 127) input.append(character);
        }
        text = text.substring(0, start) + input + text.substring(end);
        caret = anchor = start + input.length();
    }

    private static boolean shortcut() {
        return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.SYM);
    }

    private static boolean shift() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }

    private void submit() {
        if (completed || text.trim().isEmpty()) return;
        completed = true;
        super.hide();
        listener.input(text.trim());
    }

    @Override public void hide() {
        if (completed) return;
        completed = true;
        super.hide();
        listener.canceled();
    }
}
