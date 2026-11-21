package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public final class PartyFeedback {
    private static final int HIT = 0, MISS = 1, MESSAGE = 2, HEAL = 3, LEVEL_UP = 4, SHIELD = 5, SOUND = 6, NOTICE = 7, BOSS = 8;
    private static final int MAX_EVENTS = 128, MAX_TEXT = 512;
    private static final ArrayList<Event> pending = new ArrayList<>();
    private static long sent, received;
    private static boolean active;

    static void start() { reset(); active = true; }
    static void reset() { pending.clear(); sent = received = 0; active = false; }

    private static Event event(int kind, Unit unit) {
        if (!active || !PartySession.running() || PartySession.client()
                || !PartySession.connected() || unit == null || unit.getRoom() == null) return null;
        if (pending.size() == MAX_EVENTS) pending.remove(0);
        Event event = new Event();
        event.kind = kind; event.unit = unit.getPersistentId(); event.room = unit.getRoom();
        event.x = unit.x; event.y = unit.y;
        pending.add(event);
        return event;
    }

    public static void hit(Unit source, Unit target, int damage, float impulse) {
        Event event = event(HIT, target);
        if (event == null) return;
        event.amount = damage; event.hero = target.isHero; event.impulse = impulse;
        event.direction = source == null || source.x <= target.x ? 1f : -1f;
    }
    public static void miss(Unit unit) { event(MISS, unit); }
    public static void message(Unit unit, String text, Color color, float velocity) {
        if (text == null) return;
        Event event = event(MESSAGE, unit);
        if (event == null) return;
        event.text = text.substring(0, Math.min(MAX_TEXT, text.length()));
        event.color = Color.rgba8888(color); event.velocity = velocity;
    }
    public static void heal(Unit unit) { event(HEAL, unit); }
    public static void notice(String text, GameSprite icon) {
        if (!PartySession.remoteContext() || text == null || text.isEmpty()) return;
        Event event = event(NOTICE, UnitHelper.getInstance().getHero());
        if (event != null) {
            event.text = text.substring(0, Math.min(MAX_TEXT, text.length()));
            event.icon = PartySprite.capture(icon);
        }
    }
    public static void bossSlain() { event(BOSS, UnitHelper.getInstance().getHero()); }
    public static void levelUp(Unit unit) { event(LEVEL_UP, unit); }
    public static void shield(Unit unit, boolean right) {
        Event event = event(SHIELD, unit);
        if (event != null) event.right = right;
    }
    public static void sound(Sounds sound, float pitch, float volume) {

        if (!isWorldSound(sound)) return;
        Event event = event(SOUND, UnitHelper.getInstance().getHero());
        if (event == null) return;
        event.text = sound.name(); event.pitch = pitch; event.volume = volume;
    }

    public static boolean isWorldSound(Sounds sound) {
        return !sound.isMusic() && sound != Sounds.STEP && sound != Sounds.WATER && sound != Sounds.CLICK;
    }

    static void flush() {
        if (pending.isEmpty()) return;
        ArrayList<Event> batch = new ArrayList<>(pending);
        pending.clear();
        long sequence = ++sent;
        PartySession.send(PartySession.FEEDBACK, output -> {
            output.writeLong(sequence); output.writeInt(batch.size());
            for (Event event : batch) event.write(output);
        });
    }

    static void receive(DataInputStream input) throws IOException {
        long sequence = input.readLong();
        int count = input.readInt();
        if (sequence < 1 || count < 0 || count > MAX_EVENTS) throw new IOException("feedback_count");
        ArrayList<Event> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) batch.add(Event.read(input));
        if (input.available() != 0) throw new IOException("feedback_tail");
        if (sequence <= received) return;
        received = sequence;
        for (Event event : batch) event.show();
    }

    private static final class Event {
        int kind, amount, color;
        String unit, room, text;
        float x, y, direction, impulse, velocity, pitch, volume;
        boolean hero, right;
        PartySprite.Data icon;
        GameSprite restoredIcon;

        void write(DataOutputStream output) throws IOException {
            output.writeByte(kind); output.writeUTF(unit); output.writeUTF(room);
            output.writeFloat(x); output.writeFloat(y);
            switch (kind) {
                case HIT:
                    output.writeInt(amount); output.writeBoolean(hero); output.writeFloat(direction); output.writeFloat(impulse); break;
                case MESSAGE:
                    output.writeUTF(text); output.writeInt(color); output.writeFloat(velocity); break;
                case SHIELD: output.writeBoolean(right); break;
                case SOUND:
                    output.writeUTF(text); output.writeFloat(pitch); output.writeFloat(volume); break;
                case NOTICE: output.writeUTF(text); output.writeUTF(PartyCodec.encode(icon)); break;
                default: break;
            }
        }

        static Event read(DataInputStream input) throws IOException {
            Event event = new Event();
            event.kind = input.readUnsignedByte(); event.unit = input.readUTF(); event.room = input.readUTF();
            if (event.kind > BOSS || event.unit.length() > 160 || event.room.length() > 160) throw new IOException("feedback_kind");
            event.x = finite(input.readFloat()); event.y = finite(input.readFloat());
            switch (event.kind) {
                case HIT:
                    event.amount = input.readInt(); event.hero = input.readBoolean();
                    event.direction = finite(input.readFloat()); event.impulse = finite(input.readFloat());
                    if (event.amount < 0 || Math.abs(event.direction) != 1f || event.impulse < 0 || event.impulse > 1) throw new IOException("feedback_hit");
                    break;
                case MESSAGE:
                    event.text = input.readUTF(); event.color = input.readInt(); event.velocity = finite(input.readFloat());
                    if (event.text.length() > MAX_TEXT) throw new IOException("feedback_text");
                    break;
                case SHIELD: event.right = input.readBoolean(); break;
                case NOTICE:
                    event.text = input.readUTF();
                    if (event.text.length() > MAX_TEXT) throw new IOException("feedback_notice");
                    event.restoredIcon = PartySprite.restore(PartyCodec.decode(PartySprite.Data.class, input.readUTF())); break;
                case SOUND:
                    event.text = input.readUTF(); event.pitch = finite(input.readFloat()); event.volume = finite(input.readFloat());
                    try {
                        if (Sounds.valueOf(event.text).isMusic() || event.pitch <= 0 || event.pitch > 4 || event.volume < 0 || event.volume > 1) throw new IOException("feedback_sound");
                    } catch (IllegalArgumentException invalid) { throw new IOException("feedback_sound", invalid); }
                    break;
                default: break;
            }
            return event;
        }

        void show() {
            if (kind == NOTICE) {
                if (unit.equals(PartySession.player(PartySession.localIndex()).getPersistentId()))
                    UIHelper.getInstance().showPickupNotice(text, restoredIcon);
                return;
            }
            if (!Objects.equals(room, MapHelper.getInstance().getActiveRoomIdentifier())) return;
            Unit target = null;
            for (Unit candidate : UnitHelper.getInstance().getUnitsSnapshot())
                if (unit.equals(candidate.getPersistentId()) && room.equals(candidate.getRoom())) { target = candidate; break; }
            EffectsHelper effects = EffectsHelper.getInstance();
            switch (kind) {
                case HIT:
                    effects.showHitResult(target, x, y, amount, hero, impulse, direction);
                    if (amount > 0) effects.add(new Blood().init(x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                            y + ConstantsHelper.UNIT_DIMENSIONS / 2f, 0, amount * direction * 50f, amount * 50f, 200f));
                    break;
                case MISS: effects.showMiss(x, y); break;
                case MESSAGE:
                    Color tint = new Color(); Color.rgba8888ToColor(tint, color);
                    effects.add(new Text().init(x, y + ConstantsHelper.UNIT_DIMENSIONS, velocity, text, tint)); break;
                case HEAL:
                    if (target != null) effects.add(new Heal().init(x, y, 0, 0, 0, 0, target)); break;
                case LEVEL_UP:
                    if (target != null) effects.add(new LevelUp().init(x, y, 0, 0, 0, 0, target)); break;
                case SHIELD: effects.add(new ManaShield().init(x + (right ? 10f : -10f), y, right)); break;
                case SOUND: SoundHelper.GetSingleton().playPartySound(Sounds.valueOf(text), pitch, volume); break;
                case BOSS: UIHelper.getInstance().showBossSlainBanner(); break;
                default: break;
            }
        }
    }

    private static float finite(float value) throws IOException {
        if (Float.isNaN(value) || Float.isInfinite(value) || Math.abs(value) > 10000000f) throw new IOException("feedback_position");
        return value;
    }

    private PartyFeedback() { }
}
