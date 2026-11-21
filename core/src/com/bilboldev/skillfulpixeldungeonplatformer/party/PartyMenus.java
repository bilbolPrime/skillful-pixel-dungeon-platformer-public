package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SkillsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.*;
import java.io.*;
import java.util.*;

final class PartyMenus {
    private static final int GENERIC = 0, JOURNAL = 1, INVENTORY = 2, ITEM = 3, SKILL = 4, NATIVE = 5;
    private Window offered;
    private long token;
    private String signature = "";
    private List<? extends Button> choices = Collections.emptyList();
    private Window displayed;

    void send() {
        if (!PartySession.connected() || PartySession.client()) return;
        PartySession.scoped(1 - PartySession.localIndex(), () -> {
            while (WindowHelper.getInstance().topWindow() != null && !WindowHelper.getInstance().topWindow().partyValid())
                WindowHelper.getInstance().topWindow().hide();
            Window current = WindowHelper.getInstance().topWindow();
            List<? extends Button> buttons = current == null ? Collections.emptyList() : current.partyChoices();
            String description = current == null ? "" : current.partyText();
            int kind = current instanceof JournalWindow ? JOURNAL : current instanceof InventoryWindow ? INVENTORY
                    : current instanceof ItemWindow ? ITEM : current instanceof SkillWindow ? SKILL
                    : PartyMenuViews.supports(current) ? NATIVE : GENERIC;
            String detail = kind == INVENTORY ? Integer.toString(((InventoryWindow)current).partyTab())
                    : kind == ITEM ? PartyCodec.encode(SaveHelper.getInstance().snapshotItem(((ItemWindow)current).partyItem(), UnitHelper.getInstance().getHero()))
                    : kind == SKILL ? Integer.toString(((SkillWindow)current).partySkill().getId())
                    : kind == NATIVE ? PartyMenuViews.capture(current) : "";
            StringBuilder text = new StringBuilder(description).append('\n').append(kind).append(':').append(kind == ITEM ? "" : detail);
            for (Button button : buttons) text.append('\n').append(button.partyLabel()).append(button.canClick());
            String next = text.toString();
            if (current == offered && next.equals(signature)) return;
            offered = current; signature = next; choices = new ArrayList<>(buttons); token++;

            PartySession.scoped(-1, PartySession::transmitState);
            PartySession.send(PartySession.MENU, data -> {
                data.writeLong(token); data.writeBoolean(current != null);
                if (current == null) return;
                data.writeByte(kind); data.writeUTF(detail);
                data.writeUTF(description); data.writeInt(buttons.size());
                for (Button button : buttons) { data.writeUTF(button.partyLabel()); data.writeBoolean(button.canClick()); data.writeFloat(button.getWidth()); }
            });
        });
    }
    void choose(DataInputStream data) throws IOException {
        long requested = data.readLong(); int choice = data.readInt();
        if (requested != token || offered == null) return;
        PartySession.scoped(1 - PartySession.localIndex(), () -> {
            if (WindowHelper.getInstance().topWindow() != offered || PartySession.player(1 - PartySession.localIndex()).isDead()) return;
            if (!offered.partyValid()) { offered.hide(); return; }
            if (choice == -1) offered.hide();
            else if (choice >= 0 && choice < choices.size() && choices.get(choice).canClick()) choices.get(choice).click();
        });
        send();
    }
    void receive(DataInputStream data) throws IOException {
        long version = data.readLong(); boolean open = data.readBoolean();
        int kind = open ? data.readUnsignedByte() : GENERIC;
        String detail = open ? data.readUTF() : "";
        String description = open ? data.readUTF() : "";
        int count = open ? data.readInt() : 0;
        if (kind > NATIVE || detail.length() > 32768 || description.length() > 8192 || count < 0 || count > 64) throw new IOException("menu");
        List<String> labels = new ArrayList<>(); List<Boolean> enabled = new ArrayList<>();
        List<Float> widths = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String label = data.readUTF(); if (label.length() > 2048) throw new IOException("choice");
            labels.add(translate(label)); enabled.add(data.readBoolean());
            float width = data.readFloat();
            if (Float.isNaN(width) || Float.isInfinite(width) || width < 1 || width > 2500) throw new IOException("choice_width");
            widths.add(width);
        }
        if (displayed != null) WindowHelper.getInstance().closeWindow(displayed);
        if (!open) displayed = null;
        else if (kind == JOURNAL) displayed = new JournalWindow().setOnHide(() -> choose(version, -1));
        else if (kind == INVENTORY) {
            int tab = Integer.parseInt(detail);
            if (tab < 0 || tab > 3) throw new IOException("inventory_tab");
            displayed = new PartyInventoryWindow(UnitHelper.getInstance().getHero().getHeroClass(), tab, labels, enabled, index -> choose(version, index));
        } else if (kind == ITEM) {
            Item item = SaveHelper.getInstance().createPreviewItem(PartyCodec.decode(SaveHelper.ItemSaveData.class, detail));
            if (item == null) throw new IOException("menu_item");
            ItemWindow window = new ItemWindow(item, description);
            for (int i = 0; i < count; i++) {
                final int index = i;
                window.addPartyChoice(labels.get(i), widths.get(i), enabled.get(i), () -> choose(version, index));
            }
            displayed = window.setOnHide(() -> choose(version, -1));
        } else if (kind == SKILL) {
            Skill skill = SkillsHelper.getInstance().getSkill(Integer.parseInt(detail));
            if (skill == null) throw new IOException("menu_skill");
            SkillWindow window = new SkillWindow(skill);
            for (int i = 0; i < count; i++) {
                final int index = i;
                window.addPartyChoice(labels.get(i), enabled.get(i), () -> choose(version, index));
            }
            displayed = window.setOnHide(() -> choose(version, -1));
        } else if (kind == NATIVE) {
            displayed = new PartyWindow(PartyMenuViews.restore(detail, description, labels, enabled), labels, enabled, index -> choose(version, index));
        } else displayed = new RemoteWindow(version, description, labels, enabled, 0);
        if (displayed != null) WindowHelper.getInstance().addWindow(displayed.build());
    }
    private static void choose(long version, int index) {
        PartySession.send(PartySession.CHOICE, data -> { data.writeLong(version); data.writeInt(index); });
    }
    private final class RemoteWindow extends ChoiceDialogWindow {
        final long version;
        RemoteWindow(long version, String description, List<String> labels, List<Boolean> enabled, int page) {
            super("images/weapons/sword.png", translate(description), 1500, 400);
            this.version = version;
            int begin = page * 5, end = Math.min(labels.size(), begin + 5);
            for (int i = begin; i < end; i++) {
                final int index = i;
                addChoice(translate(labels.get(i)), () -> PartySession.send(PartySession.CHOICE, data -> { data.writeLong(version); data.writeInt(index); }), enabled.get(i));
            }
            if (end < labels.size()) addChoice(Messages.get("party.next"), () -> showPage(version, description, labels, enabled, page + 1));
            if (page > 0) addChoice(Messages.get("party.previous"), () -> showPage(version, description, labels, enabled, page - 1));
            addChoice(Messages.get("party.close"), () -> PartySession.send(PartySession.CHOICE, data -> { data.writeLong(version); data.writeInt(-1); }));
        }
        private void dismiss() {
            PartySession.send(PartySession.CHOICE, data -> { data.writeLong(version); data.writeInt(-1); });
            hide();
        }
        @Override public boolean click(float x, float y) {
            if (x < this.x || x > this.x + width || y < this.y || y > this.y + height) { dismiss(); return true; }
            return super.click(x, y);
        }
        @Override public boolean keyDown(int key) {
            if (key == com.badlogic.gdx.Input.Keys.ESCAPE || key == com.badlogic.gdx.Input.Keys.BACK || com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper.getInstance().getInteractBinding().matchesKey(key)) {
                dismiss(); return true;
            }
            return super.keyDown(key);
        }
    }
    private void showPage(long version, String description, List<String> labels, List<Boolean> enabled, int page) {
        displayed = new RemoteWindow(version, description, labels, enabled, page);
        WindowHelper.getInstance().addWindow(displayed.build());
    }
    private static String translate(String text) {
        String whole = Messages.maybeTranslate(text);
        if (!whole.equals(text)) return whole;
        StringBuilder result = new StringBuilder();
        for (String line : text.split("\n", -1)) { if (result.length() > 0) result.append('\n'); result.append(Messages.maybeTranslate(line)); }
        return result.toString();
    }
}
