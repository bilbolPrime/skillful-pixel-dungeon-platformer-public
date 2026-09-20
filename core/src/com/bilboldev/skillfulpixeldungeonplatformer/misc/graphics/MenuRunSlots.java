package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.GameScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.LoadingScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.ChoiceDialogWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;

import java.util.ArrayList;


public final class MenuRunSlots {
    private static final float LEFT = 1450, WIDTH = 298, HEIGHT = 230, GAP = 20;
    private final MenuHeroRoster roster;
    private final Runnable changed;
    private final ArrayList<ActionButton> controls = new ArrayList<>();
    private final ArrayList<Slot> slots = new ArrayList<>();
    private float clock;
    private long saveRevision;

    public MenuRunSlots(MenuHeroRoster roster, Runnable changed) {
        this.roster = roster;
        this.changed = changed;
        SaveHelper.getInstance().importClassRunsIntoSlots();
        refresh();
    }

    public ArrayList<ActionButton> controls() { return controls; }

    public void refresh() {
        saveRevision = SaveHelper.getInstance().slotRevision();
        controls.clear(); slots.clear();
        for (int index = 0; index < SaveHelper.RUN_SLOT_COUNT; index++) {
            Slot slot = new Slot(index);
            slots.add(slot); controls.add(slot);
            if (slot.occupied) controls.add(slot.delete);
        }
        if (changed != null) changed.run();
    }

    public void act(float delta) {
        clock = (clock + Math.min(.1f, Math.max(0, delta))) % 3f;
        if (saveRevision != SaveHelper.getInstance().slotRevision()) refresh();
    }

    public void draw(Batch batch, com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button focused) {
        float alpha = 1f - roster.focusAmount();
        if (alpha <= .001f) return;
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * alpha);
        label(batch, Messages.get("desktop.runs.heading"), LEFT, 932, WIDTH * 3 + GAP * 2, 2.8f, DesktopMenuStyle.GOLD);
        label(batch, Messages.get("desktop.runs.new"), 170, 330, 1200, 2.3f, DesktopMenuStyle.INK);
        for (Slot slot : slots) slot.drawSlot(batch, focused);
        String cloudStatus = com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.status();
        if (cloudStatus != null) label(batch, cloudStatus, LEFT, 112, WIDTH * 3 + GAP * 2, 1.8f, DesktopMenuStyle.EDGE);
        batch.setPackedColor(packed);
    }

    private static void label(Batch batch, String text, float x, float top, float width, float size, Color color) {
        FontHelper.FittedTextBlock fit = FontHelper.getSingleton().fitLabelToBounds(text, size, width, 40);
        FontHelper.getSingleton().writeRaw(new Color(color.r, color.g, color.b, color.a * batch.getColor().a),
                batch, fit.size, x + (width - fit.width) / 2, top, fit.text);
    }


    public static int armorTier(SaveHelper.RunSaveData data) {
        String armor = data.inventory.equippedArmorClassName;
        String[] names = {"BirthdaySuit", "Cloth", "LeatherArmor", "MailArmor", "ScaleArmor", "PlateArmor"};
        for (int tier = 0; tier < names.length; tier++)
            if (("items/armor/" + names[tier]).equals(armor)) return tier;
        return 0;
    }

    private final class Slot extends ActionButton {
        final int index, armorRow;
        final SaveHelper.RunSaveData saved;
        final boolean occupied;
        final HeroClass heroClass;
        final Texture sheet;
        final ActionButton delete;

        Slot(int index) {
            super(LEFT + index % 3 * (WIDTH + GAP), 654 - index / 3 * (HEIGHT + GAP), WIDTH, HEIGHT,
                    "images/misc/transparent.png", "images/misc/transparent.png");
            this.index = index;
            saved = SaveHelper.getInstance().loadSlot(index);
            occupied = SaveHelper.getInstance().slotOccupied(index);
            heroClass = saved == null ? null : HeroClass.valueOf(saved.heroClassName);
            sheet = heroClass == null ? null : TextureHelper.GetSingleton().getTexture(heroClass.getFilm());
            armorRow = saved == null ? 0 : armorTier(saved) * 15;
            delete = new ActionButton(x + WIDTH - 78, y + HEIGHT - 44, 74, 40,
                    "images/misc/transparent.png", "images/misc/transparent.png") {
                @Override public boolean canClick() { return occupied && !roster.focusedView(); }
                @Override public void click() { if (canClick()) confirmDelete(); }
            };
        }

        @Override public boolean canClick() { return saved != null && !roster.focusedView(); }
        @Override public boolean isHitProjected(float px, float py) {
            return super.isHitProjected(px, py) && !delete.isHitProjected(px, py);
        }
        @Override public void click() {
            if (!canClick()) return;
            ChoiceDialogWindow prompt = new ChoiceDialogWindow(portrait(),
                    heroClass.getName() + "\n" + details(), 1050, 380);
            prompt.addChoice(Messages.get("desktop.pause.resume"), () -> {
                SaveHelper.RunSaveData latest = SaveHelper.getInstance().loadSlot(index);
                if (latest == null || !saved.runId.equals(latest.runId)) {
                    error("desktop.runs.unavailable"); refresh(); return;
                }
                com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.resumeSlot(index, latest, () -> {
                    WindowHelper.getInstance().hideAll();
                    SkillfulPixelDungeonPlatformer.transition(new LoadingScreen().prepare(
                            new GameScreen(index, latest), SkillfulPixelDungeonPlatformer.getActiveScreen()));
                });
            });
            WindowHelper.getInstance().addWindow(prompt.build());
        }

        private GameSprite portrait() {
            return new GameSprite(new Sprite(new TextureRegion(sheet, 0, armorRow, 12, 15)), 96, 120);
        }

        private String details() {
            return DifficultyHelper.Difficulty.fromName(saved.difficultyName).getDisplayName() + "  |  "
                    + Messages.get("desktop.runs.progress", saved.hero.level, saved.currentDepth);
        }

        private void confirmDelete() {
            String name = heroClass == null ? Messages.get("desktop.runs.unreadable") : heroClass.getName() + " — " + details();
            ChoiceDialogWindow prompt = new ChoiceDialogWindow("images/menu/exit.png",
                    Messages.get("desktop.runs.delete_confirm", index + 1, name), 1200, 430);
            prompt.addChoice(Messages.get("desktop.runs.delete"), () -> {
                if (!SaveHelper.getInstance().deleteSlot(index, saved == null ? null : saved.runId)) error("desktop.runs.delete_failed");
                refresh();
            });
            prompt.addChoice(Messages.get("desktop.runs.cancel"), null);
            WindowHelper.getInstance().addWindow(prompt.build());
        }

        void drawSlot(Batch batch, com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button focus) {
            boolean selected = focus == this || DesktopMenuStyle.hovered(batch, this);
            DesktopMenuStyle.shade(batch, x - 14, y - 12, WIDTH + 28, HEIGHT + 24, 30, occupied ? .85f : .35f);
            DesktopMenuStyle.corners(batch, x, y, WIDTH, HEIGHT, DesktopMenuStyle.GOLD, selected ? .9f : occupied ? .35f : .12f);
            DesktopMenuStyle.fill(batch, x + 22, y + 86, WIDTH - 44, 4, DesktopMenuStyle.EDGE, occupied ? .35f : .1f);
            DesktopMenuStyle.hover(batch, this, DesktopMenuStyle.GOLD);
            label(batch, String.format(java.util.Locale.ROOT, "%02d", index + 1), x + 12, y + HEIGHT - 16, 40, 1.7f, DesktopMenuStyle.EDGE);
            if (sheet != null) {
                int frame = ((int) ((clock + index * .31f) % 3)) == 0 ? 0 : 1;
                batch.draw(sheet, x + (WIDTH - 108) / 2, y + 88, 108, 135, frame * 12, armorRow, 12, 15, false, false);
                label(batch, heroClass.getName(), x + 10, y + 78, WIDTH - 20, 2.3f, DesktopMenuStyle.INK);
                label(batch, DifficultyHelper.Difficulty.fromName(saved.difficultyName).getDisplayName(), x + 10, y + 49, WIDTH - 20, 1.8f, DesktopMenuStyle.GOLD);
                label(batch, Messages.get("desktop.runs.progress", saved.hero.level, saved.currentDepth), x + 10, y + 24, WIDTH - 20, 1.7f, DesktopMenuStyle.INK);
            } else label(batch, Messages.get(occupied ? "desktop.runs.unreadable" : "desktop.runs.empty"),
                    x + 12, y + 128, WIDTH - 24, 2.1f, DesktopMenuStyle.EDGE);
            if (occupied) {
                DesktopMenuStyle.hover(batch, delete, DesktopMenuStyle.GOLD);
                label(batch, Messages.get("desktop.runs.delete"), delete.x, delete.y + 27, delete.getWidth(), 1.6f,
                        focus == delete || DesktopMenuStyle.hovered(batch, delete) ? DesktopMenuStyle.GOLD : DesktopMenuStyle.EDGE);
            }
        }
    }

    private static void error(String key) {
        WindowHelper.getInstance().addWindow(new TextWindow(1100, 240, Messages.get(key)).build());
    }
}
