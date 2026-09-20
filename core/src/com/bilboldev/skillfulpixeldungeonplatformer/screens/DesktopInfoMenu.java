package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.DesktopMenuStyle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;


final class DesktopInfoMenu {
    private static final Color ACTIVE = DesktopMenuStyle.GOLD, INK = DesktopMenuStyle.INK;
    enum Destination {
        LIBRARY("LIBRARY", "library"), SETTINGS("SETTINGS", "settings"), RANKINGS("RANKINGS", "rankings"),
        BADGES("BADGES", "achievements"), RAT_KING("RAT KING", "ratking"), ABOUT("ABOUT", "about"),
        BACK("Back", "exit");
        final String label, icon;
        Destination(String label, String icon) { this.label = label; this.icon = icon; }
    }
    private final EnumSet<Destination> supported = EnumSet.allOf(Destination.class);
    private final EnumMap<Destination, MenuContentView> views = new EnumMap<>(Destination.class);
    private final ArrayList<ActionButton> navigation = new ArrayList<>(), controls = new ArrayList<>();
    private final Runnable changed;
    private Destination destination;
    private MenuContentView content;
    private final GameSprite logo, runes;
    private Button selectedControl;
    private ActionButton backButton;

    DesktopInfoMenu(Consumer<Destination> open, Runnable changed) {
        this.changed = changed;
        for (Destination entry : Destination.values()) {
            ActionButton button = new DestinationButton(entry, navigation.size(), open);
            navigation.add(button);
            if (entry == Destination.BACK) backButton = button;
        }
        String path = "images/intro/pixel-dungeon-platformer.png";
        logo = new GameSprite(path, TextureHelper.GetSingleton().getTexture(path).getWidth() * 1.7f,
                TextureHelper.GetSingleton().getTexture(path).getHeight() * 1.7f);
        logo.setPosition(64, 960);
        runes = new GameSprite("images/intro/pixel-dungeon-platformer-over.png", logo.getWidth(), logo.getHeight());
        runes.setPosition(logo.getX(), logo.getY());
        rebuildControls();
    }

    boolean supports(Destination value) { return supported.contains(value); }
    Destination destination() { return destination; }
    boolean hasHostedContent() { return content != null; }
    List<ActionButton> controls() { return controls; }
    ActionButton backButton() { return backButton; }
    ActionButton navigationAt(float x, float y) {
        for (ActionButton button : navigation) if (button.isHitProjected(x, y)) return button;
        return null;
    }
    void open(Destination value) {
        destination = value;
        content = views.get(value);
        if (content == null) {
            MenuScreenBase source = value == Destination.LIBRARY ? new LibraryScreen()
                    : value == Destination.BADGES ? new AchievementsScreen()
                    : value == Destination.RANKINGS ? new RankingsScreen() : null;
            if (source != null) {
                content = new MenuContentView(source, this::rebuildControls);
                views.put(value, content);
            }
        }
        rebuildControls();
    }
    void act(float delta) { if (content != null) content.act(delta); }
    void rebindLanguage() {
        for (ActionButton button : navigation)
            if (button instanceof DestinationButton) ((DestinationButton) button).refreshLabel();
        for (MenuContentView view : views.values()) view.rebindLanguage();
        rebuildControls();
    }
    private void rebuildControls() {
        controls.clear(); controls.addAll(navigation);
        if (content != null) controls.addAll(content.controls());
        changed.run();
    }
    void draw(Batch batch, Button selected, float runeAlpha) {
        selectedControl = selected;
        DesktopMenuStyle.shade(batch, 8, 208, 448, 736, 64, .76f);
        DesktopMenuStyle.rule(batch, 80, 932, 304, ACTIVE);
        logo.draw(batch);
        runes.setAlpha(GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.35f : runeAlpha); runes.draw(batch);
        for (ActionButton button : navigation) button.draw(batch);
        if (content != null) content.draw(batch, selected);
    }

    private final class DestinationButton extends ActionButton {
        private final Destination entry;
        private final Consumer<Destination> open;
        private final GameSprite icon;
        private FontHelper.FittedTextBlock label;
        DestinationButton(Destination entry, int row, Consumer<Destination> open) {
            super(64, 832 - row * 88, 344, 80, "images/misc/transparent.png", "images/misc/transparent.png");
            this.entry = entry; this.open = open;
            icon = new GameSprite(entry == Destination.BACK ? "images/menu/exit.png" : "images/intro/" + entry.icon + ".png", 48, 48);
            if (entry == Destination.BACK) icon.faceRight(false);
            icon.setPosition(x + 8, y + 16);
            refreshLabel();
            enableUiPressFeedback();
        }
        void refreshLabel() {
            label = FontHelper.getSingleton().fitLabelToBounds(entry == Destination.BACK
                    ? Messages.get("custom.desktop_menu.back") : Messages.maybeTranslate(entry.label), 2.5f, 256, 56);
        }
        @Override public boolean canClick() { return supports(entry); }
        @Override public void clicked() { open.accept(entry); }
        @Override public void draw(Batch batch) {
            if (!supports(entry)) return;
            boolean active = destination == entry;
            if (active || selectedControl == this || isShowingPressFeedback())
                DesktopMenuStyle.card(batch, x, y, getWidth(), getHeight(),
                        active ? ACTIVE : DesktopMenuStyle.EDGE, active || isShowingPressFeedback());
            DesktopMenuStyle.hover(batch, this, ACTIVE);
            icon.draw(batch);
            FontHelper.getSingleton().writeRaw(active ? ACTIVE : INK, batch,
                    label.size, x + 72, y + (getHeight() + label.height) / 2, label.text);
        }
    }
}
