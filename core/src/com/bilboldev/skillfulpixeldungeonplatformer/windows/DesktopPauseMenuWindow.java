package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;

import java.util.ArrayList;


public final class DesktopPauseMenuWindow extends PauseMenuWindow {
    private static final float PANEL_WIDTH = 820, ROW_HEIGHT = 82;
    private interface ToggleValue { boolean getAsBoolean(); }
    private static final Color PANEL = Color.valueOf("162221");
    private final ArrayList<Row> rows = new ArrayList<>();
    private final WindowChoiceFocus focus = new WindowChoiceFocus();
    private final boolean reducedMotion = GameSettingsHelper.getInstance().isReducedCameraMotion();
    private Row pressed;
    private float progress, clock;
    private boolean closing;

    @Override public Window build() {

        cancelPointerInput();
        rows.clear();
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        add(Messages.get("desktop.pause.resume"), "images/intro/play.png", null, this::hide);
        add(Messages.maybeTranslate("Music"), null, settings::isMusicEnabled,
                () -> settings.setMusicEnabled(!settings.isMusicEnabled()));
        add(Messages.maybeTranslate("Sound FX"), null, settings::isSoundFxEnabled, () -> {
            settings.setSoundFxEnabled(!settings.isSoundFxEnabled());
            if (settings.isSoundFxEnabled()) SoundHelper.GetSingleton().playUiClick();
        });
        add(Messages.get("custom.presentation.reduced_motion"), null, settings::isReducedCameraMotion,
                () -> settings.setReducedCameraMotion(!settings.isReducedCameraMotion()));
        add(Messages.get("custom.presentation.reduced_effects"), null, settings::isReducedVisualEffects,
                () -> settings.setReducedVisualEffects(!settings.isReducedVisualEffects()));
        add(Messages.get("custom.presentation.background_rooms"), null, settings::isBackgroundRoomsEnabled,
                () -> settings.setBackgroundRoomsEnabled(!settings.isBackgroundRoomsEnabled()));
        add(Messages.get("custom.presentation.platform_shadows"), null, settings::isPlatformShadowsEnabled,
                () -> settings.setPlatformShadowsEnabled(!settings.isPlatformShadowsEnabled()));
        add(Messages.get("windows.wndsettings$inputtab.key_bindings"), "images/menu/game-controller.png", null,
                () -> WindowHelper.getInstance().addWindow(new PauseControlsWindow(true).build()));
        add(Messages.get("desktop.pause.save_exit"), "images/menu/exit.png", null, this::saveAndExit);
        layout();
        return this;
    }

    private void add(String label, String icon, ToggleValue checked, Runnable action) {
        rows.add(new Row(label, icon, checked, action));
    }

    public void act(float delta) {
        float step = Float.isFinite(delta) ? Math.min(.1f, Math.max(0, delta)) : 0;
        clock += step;
        progress = Math.max(0, Math.min(1, progress + step / (closing ? -.2f : .6f)));
        DesktopMenuStyle.actHover(step);
        layout();
        if (closing && progress == 0) super.hide();
    }

    private float eased() { return progress * progress * (3 - 2 * progress); }

    private void layout() {
        OrthographicCamera camera = GameHelper.GetSingleton().getUICamera();
        width = PANEL_WIDTH;
        height = camera.viewportHeight;
        y = camera.position.y - height / 2;
        x = camera.position.x - camera.viewportWidth / 2 - (reducedMotion ? 0 : width * (1 - eased()));
        float rowY = y + height - 280;
        float spacing = (height - 280 - 68) / Math.max(1, rows.size() - 1);
        for (Row row : rows) { row.setPosition(x + 58, rowY); rowY -= spacing; }
    }

    private boolean ready() { return !closing && progress >= 1; }

    @Override public void hide() { cancelPointerInput(); closing = true; }

    private void saveAndExit() {
        if (!SaveHelper.getInstance().saveCurrentRun()) {
            WindowHelper.getInstance().addWindow(new TextWindow(1100, 240, Messages.get("desktop.runs.save_failed")).build());
            return;
        }
        SaveHelper.getInstance().detachRun();
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
    }

    @Override public boolean pointerDown(float px, float py, int button) {
        cancelPointerInput(); layout();
        if (!ready()) return true;
        focus.pointerDown(px, py, rows);
        for (Row row : rows) if (row.isHitProjected(px, py)) { pressed = row; row.pressDown(); break; }
        return true;
    }

    @Override public boolean tap(float px, float py) {
        Row target = pressed;
        cancelPointerInput();
        if (target != null && ready() && target.isHitProjected(px, py)) target.click();
        return true;
    }

    @Override public boolean click(float px, float py) { return tap(px, py); }

    @Override public boolean keyDown(int keycode) {
        cancelPointerInput();
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) { hide(); return true; }
        if (ready()) focus.keyDown(null, keycode, rows);
        return true;
    }

    @Override public void cancelPointerInput() {
        if (pressed != null) pressed.cancelPress();
        pressed = null;
    }

    @Override public void draw(Batch batch) {
        layout();
        OrthographicCamera camera = GameHelper.GetSingleton().getUICamera();
        float alpha = eased();
        DesktopMenuStyle.fill(batch, camera.position.x - camera.viewportWidth / 2, y,
                camera.viewportWidth, height, Color.BLACK, .56f * alpha);
        float packed = batch.getPackedColor();
        Color parent = new Color(batch.getColor());
        batch.setColor(parent.r, parent.g, parent.b, parent.a * alpha);

        for (int i = 0; i < 16; i++) DesktopMenuStyle.fill(batch, x + width + i * 6, y, 6, height, PANEL, .94f * (16 - i) / 16f);
        DesktopMenuStyle.fill(batch, x, y, width, height, PANEL, .94f);
        DesktopMenuStyle.fill(batch, x + width - 23, y + 42, 3, height - 84, DesktopMenuStyle.EDGE, .3f);
        Texture logo = TextureHelper.GetSingleton().getTexture("images/intro/pixel-dungeon-platformer.png");
        float logoWidth = 440, logoHeight = logoWidth * logo.getHeight() / logo.getWidth();
        float logoX = x + (width - logoWidth) / 2, logoY = y + height - logoHeight - 36;
        batch.draw(logo, logoX, logoY, logoWidth, logoHeight);
        float runeAlpha = GameSettingsHelper.getInstance().isReducedVisualEffects() ? .35f : (float) (.35 + .3 * Math.sin(clock * 1.7));
        batch.setColor(parent.r, parent.g, parent.b, parent.a * alpha * runeAlpha);
        batch.draw(TextureHelper.GetSingleton().getTexture("images/intro/pixel-dungeon-platformer-over.png"), logoX, logoY, logoWidth, logoHeight);
        batch.setColor(parent.r, parent.g, parent.b, parent.a * alpha);
        for (Row row : rows) row.draw(batch);
        batch.setPackedColor(packed);
    }

    private static void text(Batch batch, String label, float x, float top, float available, float size, Color color) {
        FontHelper.FittedTextBlock fit = FontHelper.getSingleton().fitLabelToBounds(label, size, available, 48);
        FontHelper.getSingleton().writeRaw(new Color(color.r, color.g, color.b, color.a * batch.getColor().a),
                batch, fit.size, x, top, fit.text);
    }

    private final class Row extends ActionButton {
        final String label;
        final Texture icon;
        final ToggleValue checked;
        final Runnable action;
        Row(String label, String icon, ToggleValue checked, Runnable action) {
            super(0, 0, PANEL_WIDTH - 116, ROW_HEIGHT, "images/misc/transparent.png", "images/misc/transparent.png");
            this.label = label; this.icon = icon == null ? null : TextureHelper.GetSingleton().getTexture(icon);
            this.checked = checked; this.action = action;
            enableUiPressFeedback();
        }
        @Override public boolean canClick() { return ready(); }
        @Override public void clicked() { if (ready()) action.run(); }
        @Override public void draw(Batch batch) {
            Button current = focus.focused(rows);
            boolean selected = WindowHelper.getInstance().topWindow() == DesktopPauseMenuWindow.this
                    && (focus.isKeyboardActive() && current == this || DesktopMenuStyle.hovered(batch, this));
            DesktopMenuStyle.fill(batch, x, y, getWidth(), getHeight(), DesktopMenuStyle.GOLD, selected ? .12f : .025f);
            DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
            if (selected) DesktopMenuStyle.fill(batch, x, y + 14, 4, getHeight() - 28, DesktopMenuStyle.GOLD, .9f);
            if (icon != null) batch.draw(icon, x + 16, y + 13, 56, 56);
            else DesktopMenuStyle.fill(batch, x + 35, y + 36, 8, 8, DesktopMenuStyle.EDGE, .8f);
            text(batch, label, x + 88, y + 55, getWidth() - (checked == null ? 104 : 184), 2.8f,
                    selected ? DesktopMenuStyle.GOLD : DesktopMenuStyle.INK);
            if (checked != null) {
                boolean on = checked.getAsBoolean();
                float switchX = x + getWidth() - 80;
                DesktopMenuStyle.fill(batch, switchX, y + 27, 64, 28, DesktopMenuStyle.EDGE, .28f);
                DesktopMenuStyle.fill(batch, switchX + (on ? 34 : 4), y + 30, 26, 22,
                        on ? DesktopMenuStyle.GOLD : DesktopMenuStyle.EDGE, on ? 1f : .5f);
            }
        }
    }
}
