package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.*;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.supporter.MercenaryAlly;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.*;
import java.io.*;
import java.util.*;

public final class PartySession {
    static final int START = 1, STATE = 2, INPUT = 3, ACTION = 4, HANDOFF = 5, LEAVE = 6, READY = 7, MENU = 8, CHOICE = 9, HANDOFF_ACK = 10, END = 11, FEEDBACK = 12, MERCENARY_MENU = 13;
    public static final int JUMP = 1, RELEASE_JUMP = 2, PRIMARY = 3, RANGED = 4, SKILL = 5, INTERACT = 6,
            DOOR = 7, HEALTH = 8, MANA = 9, FOOD = 10, INVENTORY = 11;
    private static final Hero[] heroes = new Hero[2];
    private static final InventoryHelper[] inventories = new InventoryHelper[2];
    private static final WindowHelper[] windows = new WindowHelper[2];
    private static final HeroClass[] classes = new HeroClass[2];
    private static final com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages[] languages = new com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages[2];
    private static PartyConnection connection;
    private static PartyReplica replica;
    private static PartyMenus menus;
    private static boolean active, running, ready, executing, leaving, launching, jumpIntent, remoteGone, night, surfaceEnding;
    private static int generation;
    private static int local, authority, context = -1, epoch, inputSequence, receivedInput;
    private static long seed, lastStateAt;
    private static DifficultyHelper.Difficulty difficulty;
    private static float sendDelay, inputAge, clock;
    private static String room, status = "";
    private static final LinkedHashMap<String, Integer> hosts = new LinkedHashMap<>();
    private static boolean launchDisconnected;
    private static int lobbyRevision;

    private PartySession() { }
    public static boolean active() { return active; }
    public static boolean running() { return running; }
    public static boolean client() { return running && local != authority; }
    public static boolean remoteContext() { return running && context >= 0 && context != local; }
    public static boolean controls(Hero hero) { return !running || hero == heroes[0] || hero == heroes[1]; }
    public static Hero hero(Hero fallback) { return running && context >= 0 && heroes[context] != null ? heroes[context] : fallback; }
    public static InventoryHelper inventory(InventoryHelper fallback) {
        int player = context >= 0 ? context : local;
        return running && inventories[player] != null ? inventories[player] : fallback;
    }
    public static WindowHelper windows(WindowHelper fallback) {
        int player = context >= 0 ? context : local;
        return running && windows[player] != null ? windows[player] : fallback;
    }
    public static Hero owner(String id) {
        if (!running || id == null) return null;
        for (Hero hero : heroes) if (hero != null && id.equals(hero.getPersistentId())) return hero;
        return null;
    }
    public static Hero corpseHero(Hero fallback) {
        if (running) for (Hero hero : heroes) if (hero != null && !hero.isDead() && hero.getHeroClass() == HeroClass.NECROMANCER) return hero;
        return fallback;
    }
    public static Hero player(int index) { return heroes[index]; }
    public static int index(Hero hero) { return hero == heroes[0] ? 0 : hero == heroes[1] ? 1 : -1; }
    public static Hero viewHero(Hero fallback) {
        return running && fallback != null && fallback.isDead() && heroes[1 - local] != null && !heroes[1 - local].isDead() ? heroes[1 - local] : fallback;
    }
    public static long runSeed(long fallback) { return active ? seed : fallback; }
    public static boolean night(boolean fallback) { return active ? night : fallback; }
    static float clock() { return clock; }
    static void restoreClock(float value) { clock = value; }
    static int localIndex() { return local; }
    public static Languages localLanguage() { return running ? languages[local] : Messages.lang(); }
    public static boolean requestMercenary(MercenaryAlly mercenary) {
        if (!client()) return false;
        if (mercenary != null) send(MERCENARY_MENU, data -> data.writeUTF(mercenary.getPersistentId()));
        return true;
    }
    static int authority() { return authority; }
    static int epoch() { return epoch; }
    static void scoped(int player, Runnable action) {
        int previous = context; context = player;
        try { Messages.withLanguage(player < 0 ? null : languages[player], action); } finally { context = previous; }
    }
    public static void generate(Runnable action) { if (running) scoped(0, action); else action.run(); }
    public static void withOwner(Unit owner, Runnable action) {
        if (!running) { action.run(); return; }
        Hero hero = owner instanceof Hero ? (Hero)owner : owner instanceof NecromancerMinion ? ((NecromancerMinion)owner).getOwnerHero() : null;
        scoped(hero == null ? nearest(owner) : index(hero), action);
    }
    private static int nearest(Unit unit) {
        if (heroes[0] == null || heroes[0].isDead()) return 1;
        if (heroes[1] == null || heroes[1].isDead() || unit == null) return 0;
        return Math.abs(unit.x - heroes[0].x) < Math.abs(unit.x - heroes[1].x) ? 0 : 1;
    }

    public static void choose(HeroClass heroClass) {
        ChoiceDialogWindow dialog = new ChoiceDialogWindow("images/weapons/sword.png", Messages.get("party.mode"), 1300, 420)
                .addChoice(Messages.get("party.solo"), () -> BuildHeroHelper.getSingleton().playSolo(heroClass))
                .addChoice(Messages.get("party.lan"), () -> lobby(heroClass));
        WindowHelper.getInstance().addWindow(dialog.build());
    }
    private static void lobby(HeroClass heroClass) {
        WindowHelper.getInstance().addWindow(new ChoiceDialogWindow("images/weapons/sword.png", Messages.get("party.lan_desc"), 1400, 500)
                .addChoice(Messages.get("party.host"), () -> chooseDifficulty(heroClass))
                .addChoice(Messages.get("party.join"), () -> browse(heroClass)).build());
    }
    private static void chooseDifficulty(HeroClass heroClass) {
        ChoiceDialogWindow dialog = new ChoiceDialogWindow("images/weapons/sword.png", Messages.get("party.difficulty"), 1300, 440);
        for (DifficultyHelper.Difficulty selected : DifficultyHelper.Difficulty.values())
            dialog.addChoice(selected.getDisplayName(), () -> host(heroClass, selected), DifficultyHelper.getInstance().isUnlocked(heroClass, selected));
        WindowHelper.getInstance().addWindow(dialog.build());
    }
    public static void host(HeroClass heroClass, DifficultyHelper.Difficulty selected) {
        reset(); night = NightModeHelper.isNightModeActive(); active = true; local = authority = 0; classes[0] = heroClass; difficulty = selected;
        seed = RandomHelper.getInstance().newRunSeed();
        try {
            connection = new PartyConnection(listener());
            connection.host(heroClass.ordinal());
            StringBuilder addresses = new StringBuilder();
            for (String address : PartyConnection.addresses()) { if (addresses.length() > 0) addresses.append(", "); addresses.append(address).append(":").append(connection.port()); }
            status = Messages.get("party.host_info", addresses.toString(), connection.code());
            WindowHelper.getInstance().addWindow(new PartyLobbyWindow().build());
        } catch (IOException | IllegalStateException failure) { failed("connection_lost"); showLobby(); }
    }
    public static void browse(HeroClass heroClass) {
        reset(); active = true; local = 1; authority = 0; classes[1] = heroClass;
        try { connection = new PartyConnection(listener()); connection.discover();
            status = Messages.get("party.searching");
        } catch (IllegalStateException failure) { failed("connection_lost"); }
        WindowHelper.getInstance().addWindow(new PartyLobbyWindow().build());
    }
    public static void join(HeroClass heroClass, String address, int port, String code) {
        reset();
        active = true; local = 1; authority = 0; classes[1] = heroClass;
        status = Messages.get("party.connecting");
        try {
            connection = new PartyConnection(listener());
            connection.join(address, port, heroClass.ordinal(), code);
            if (!(WindowHelper.getInstance().topWindow() instanceof PartyLobbyWindow)) WindowHelper.getInstance().addWindow(new PartyLobbyWindow().build());
        }
        catch (IllegalArgumentException | IllegalStateException invalid) { failed("connection_lost"); showLobby(); }
    }
    public static String status() { return status; }
    public static Map<String, Integer> hosts() { return new LinkedHashMap<>(hosts); }
    public static int lobbyRevision() { return lobbyRevision; }
    public static void chooseHost(String id) { enterCode(id); }
    public static boolean hosting() { return active && !running && local == 0; }
    public static boolean connected() { return connection != null && connection.connected(); }
    private static void showLobby() { if (active && !running && !(WindowHelper.getInstance().topWindow() instanceof PartyLobbyWindow)) WindowHelper.getInstance().addWindow(new PartyLobbyWindow().build()); }
    public static void enterAddress() {
        final int current = generation;
        requestTextInput(new com.badlogic.gdx.Input.TextInputListener() {
            public void input(String address) { Gdx.app.postRunnable(() -> { if (current == generation && active && !running) enterCode(address); }); }
            public void canceled() { Gdx.app.postRunnable(() -> { if (current == generation) showLobby(); }); }
        }, Messages.get("party.address"), "192.168.1.10:47874");
    }
    public static void enterCode(String address) {
        final int current = generation;
        requestTextInput(new com.badlogic.gdx.Input.TextInputListener() {
            public void input(String code) { Gdx.app.postRunnable(() -> {
                if (current != generation || !active || running) return;
                String[] parts = address.split(":", -1);
                try { if (parts.length != 2) throw new IllegalArgumentException(); join(classes[1], parts[0], Integer.parseInt(parts[1]), code); }
                catch (IllegalArgumentException invalid) { status = Messages.get("party.invalid_address"); showLobby(); }
            }); }
            public void canceled() { Gdx.app.postRunnable(() -> { if (current == generation) showLobby(); }); }
        }, Messages.get("party.code"), "XXXX-XXXX-XXXX");
    }
    private static void requestTextInput(com.badlogic.gdx.Input.TextInputListener listener, String title, String hint) {

        if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.Desktop)
            WindowHelper.getInstance().addWindow(new com.bilboldev.skillfulpixeldungeonplatformer.windows.PartyTextInputWindow(title, hint, listener).build());
        else Gdx.input.getTextInput(listener, title, "", hint);
    }
    public static void start() {
        if (launching || !hosting() || !connected() || classes[1] == null) return;
        send(START, data -> { data.writeLong(seed); data.writeInt(classes[0].ordinal()); data.writeInt(classes[1].ordinal()); data.writeInt(difficulty.ordinal()); data.writeBoolean(night); });
        launch();
    }
    private static void launch() {
        launching = true;
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new LoadingScreen().prepare(new GameScreen(classes[0], difficulty), SkillfulPixelDungeonPlatformer.getActiveScreen()));
    }

    public static void gameCreated() {
        if (!active || running) return;
        languages[local] = Messages.lang();
        heroes[0] = UnitHelper.getInstance().getHero();
        inventories[0] = InventoryHelper.getInstance();
        windows[0] = new WindowHelper(); windows[1] = new WindowHelper();
        running = true; lastStateAt = System.nanoTime();
        scoped(1, () -> {
            heroes[1] = classes[1].getHero();
            heroes[1].setRoom(heroes[0].getRoom()); heroes[1].x = heroes[0].x + 64; heroes[1].y = heroes[0].y;
            UnitHelper.getInstance().addUnit(heroes[1]);
            inventories[1] = new InventoryHelper(false);
            inventories[1].reset();
            inventories[1].modifyGold(difficulty == DifficultyHelper.Difficulty.HELL ? 0 : difficulty == DifficultyHelper.Difficulty.NIGHTMARE ? 150 : 300);
        });
        heroes[0].setPersistentId("player-0"); heroes[1].setPersistentId("player-1");
        UnitHelper.getInstance().setHero(heroes[local]);
        room = MapHelper.getInstance().getActiveRoomIdentifier();
        replica = new PartyReplica(); menus = new PartyMenus();
        PartyFeedback.start();
        UIHelper.getInstance().rebuildHeroHud();
        send(READY, data -> data.writeInt(languages[local].ordinal()));
        SaveHelper.getInstance().detachRun();
        if (launchDisconnected) {
            final int current = generation;
            Gdx.app.postRunnable(() -> { if (current == generation) disconnected(); });
        }
    }

    public static void launched(ThrownProjectile shot) { if (running && !client() && replica != null) replica.launched(shot); }
    public static void retired(ThrownProjectile shot) { if (running && !client() && replica != null) replica.retired(shot); }
    public static void contact(ThrownProjectile shot, Unit target, float damage) { if (running && !client() && replica != null) replica.contact(shot, target, damage); }

    public static void poll() {
        if (connection != null) connection.poll();
    }
    public static void update(Stage stage, float delta) {
        poll();
        if (!running || leaving) return;
        if (client() && System.nanoTime() - lastStateAt > (replica.hasState() ? 3_000_000_000L : 15_000_000_000L)) {
            if (Gdx.app != null) Gdx.app.log("Party", "Host gameplay update timeout");
            if (connection != null) connection.close();
            disconnected();
            if (!running) return;
        }
        if (!ready && connected()) return;
        clock += delta; sendDelay -= delta;
        if (client()) {
            if (sendDelay <= 0) {
                sendDelay = .05f;
                Hero hero = heroes[local];
                boolean blocked = WindowHelper.getInstance().windowOpen() || hero.isDead();
                send(INPUT, data -> { data.writeInt(++inputSequence); data.writeBoolean(!blocked && hero.movingLeft); data.writeBoolean(!blocked && hero.movingRight); });
            }
            replica.animate(delta);
            MapHelper.getInstance().refreshHeroEnvironment();
            for (Actor actor : stage.getActors().toArray(Actor.class)) if (!(actor instanceof Unit)
                    || actor instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Effects
                    || actor instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.misc.AmbientSound) actor.act(delta);
            return;
        }
        inputAge += delta;
        if (inputAge > .5f && heroes[1 - local] != null) {
            Hero remote = heroes[1 - local]; remote.clearControlIntent(); remote.movingLeft = remote.movingRight = false;
        }
        for (int player = 0; player < heroes.length; player++) if (windows[player].windowOpen()) {
            heroes[player].clearControlIntent(); heroes[player].movingLeft = heroes[player].movingRight = false;
        }
        for (Actor actor : stage.getActors().toArray(Actor.class)) {
            if (actor.getStage() != stage) continue;
            Unit owner = actor instanceof ThrownProjectile ? ((ThrownProjectile)actor).getOwner() : actor instanceof Unit ? (Unit)actor : heroes[local];
            withOwner(owner, () -> actor.act(delta));
        }
        PhysicsHelper.getInstance().step(delta);
        for (Hero hero : heroes) if (hero != null) {
            withOwner(hero, () -> { hero.updateJumpSupport(delta); hero.updateAirbornePose(delta); });
        }
        synchronizeRoom();
        if (sendDelay <= 0 && connected() && !remoteGone) {
            sendDelay = .05f;
            transmitState();
            menus.send();
        }
        if (heroes[local].isDead() && heroes[1 - local] != null && !heroes[1 - local].isDead() && connected()) handoff(false);
    }

    private static void synchronizeRoom() {
        String current = MapHelper.getInstance().getActiveRoomIdentifier();
        if (Objects.equals(current, room)) return;
        room = current;
        Hero leader = Objects.equals(heroes[0].getRoom(), room) ? heroes[0] : heroes[1];
        for (int i = 0; i < heroes.length; i++) {
            Hero hero = heroes[i];
            if (hero != leader && !hero.isDead()) {
                hero.setRoom(room); hero.appear(leader.x, leader.y);
                withOwner(hero, () -> { hero.getFriendlies(); NecromancerMinion.transferFor(hero); });
            }
            windows[i].hideAll();
        }
    }
    static void transmitState() {
        try { send(STATE, data -> replica.write(data)); PartyFeedback.flush(); }
        catch (RuntimeException failure) { if (Gdx.app != null) Gdx.app.error("Party", "Unable to capture gameplay metadata", failure); failed("invalid_state"); }
    }
    public static boolean action(int kind, int argument) {
        if (!client() || executing) return false;
        if (kind == JUMP) { if (jumpIntent) return true; jumpIntent = true; }
        if (kind == RELEASE_JUMP) { if (!jumpIntent) return true; jumpIntent = false; }
        send(ACTION, data -> { data.writeInt(kind); data.writeInt(argument); });
        return true;
    }
    static void execute(int player, int kind, int argument) {
        Hero hero = heroes[player];
        if (hero == null || hero.isDead()) return;
        scoped(player, () -> {
            executing = true;
            try {
                switch (kind) {
                    case JUMP: hero.pressJump(); break;
                    case RELEASE_JUMP: hero.releaseJump(); break;
                    case PRIMARY: hero.requestPrimaryAction(); break;
                    case RANGED: hero.requestRangedAction(); break;
                    case SKILL: if (argument >= 0 && argument < 7) hero.requestQuickSkill(argument); break;
                    case INTERACT: MapHelper.getInstance().performContextAction(); break;
                    case DOOR: MapHelper.getInstance().enterDoor(); break;
                    case HEALTH: InventoryHelper.getInstance().consumeHealthPotion(); break;
                    case MANA: InventoryHelper.getInstance().consumeManaPotion(); break;
                    case FOOD: InventoryHelper.getInstance().consumeRations(); break;
                    case INVENTORY: WindowHelper.getInstance().addWindow(new InventoryWindow(hero.getHeroClass(), 2000, 1000).build()); break;
                    default: throw new IllegalArgumentException("action");
                }
            } finally { executing = false; }
        });
    }
    private static void clearPartyMenus() { for (WindowHelper helper : windows) if (helper != null) helper.hideAll(); menus = new PartyMenus(); }
    public static boolean heroDied(Hero hero) { return running && index(hero) >= 0; }
    private static void handoff(boolean exit) {
        if (!running || client() || !connected()) return;
        transmitState();
        int next = 1 - local;
        send(HANDOFF, data -> { data.writeInt(epoch + 1); data.writeInt(next); data.writeBoolean(exit); });
        epoch++; authority = next; lastStateAt = System.nanoTime();
        replica.becomeClient(); clearPartyMenus();
    }
    public static void leave() {
        if (!active || leaving) return;
        leaving = true;
        if (running && connected()) {
            if (!client()) handoff(true);
            else send(LEAVE, data -> { });
        }

        scheduleFinish(1.5f);
    }
    public static void finishParty(boolean surface) {
        if (!running || client() || leaving) return;
        leaving = true; surfaceEnding = surface;
        send(END, data -> data.writeBoolean(surface));
        scheduleFinish(1.5f);
    }
    private static void scheduleFinish(float delay) {
        final int current = generation;
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            public void run() { if (generation == current && leaving) finishLeave(); }
        }, delay);
    }
    private static void finishLeave() {
        BaseScreen next = surfaceEnding ? new SurfaceEndingScreen() : new TitleScreen();
        reset(); WindowHelper.getInstance().hideAll(); SkillfulPixelDungeonPlatformer.transition(next, true);
    }
    public static void cancel() { reset(); WindowHelper.getInstance().hideAll(); }
    private static void disconnected() {
        if (leaving || remoteGone) return;
        if (!running || replica == null) { status = Messages.get("party.disconnected"); launchDisconnected = launching; return; }
        if (client() && !replica.hasState()) { failed("initial_state_missing"); return; }
        if (client()) { authority = local; epoch++; replica.takeAuthority(); }
        clearPartyMenus();
        remoteGone = true;
        Hero departed = heroes[1 - local];
        if (departed != null) { departed.clearControlIntent(); departed.setHP(0); departed.changeState(com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState.DEAD, true); UnitHelper.getInstance().removeUnit(departed); }
        ready = true;
        WindowHelper.getInstance().addWindow(new TextWindow(1200, 260, Messages.get("party.takeover")).build());
    }
    private static void failed(String reason) {
        if (Gdx.app != null) Gdx.app.log("Party", reason);
        if (connection != null) connection.close();
        if (client()) {
            String message = Messages.get("party.disconnected");
            finishLeave();
            WindowHelper.getInstance().addWindow(new TextWindow(1200, 300, message).build());
        } else disconnected();
    }
    static void send(int kind, PartyConnection.Write content) {
        if (connection == null || !connection.connected()) return;
        try {
            byte[] packet = PartyConnection.packet(data -> { data.writeByte(kind); data.writeInt(epoch); content.write(data); });
            if (!connection.send(packet) && connection.connected()) failed("slow_peer");
        } catch (IOException failure) { failed("packet_size"); }
    }
    private static PartyConnection.Listener listener() {
        final int current = generation;
        return new PartyConnection.Listener() {
            public void connected(int hero) { if (current != generation || !active) return; classes[1 - local] = HeroClass.values()[hero]; lobbyRevision++; status = Messages.get("party.connected"); }
            public void disconnected(String reason) { if (current != generation || !active) return; if (Gdx.app != null) Gdx.app.log("Party", "Connection closed: " + reason); PartySession.disconnected(); }
            public void discovered(String address, int port, int hero) { if (current != generation || !active) return; if (!Objects.equals(hosts.put(address + ":" + port, hero), hero)) lobbyRevision++; if (hosts.size() > 32) hosts.remove(hosts.keySet().iterator().next()); }
            public void received(byte[] packet) {
                if (current != generation || !active) return;
                try {
                    DataInputStream data = PartyConnection.stream(packet);
                    int kind = data.readUnsignedByte(), remoteEpoch = data.readInt();
                    if (remoteEpoch != epoch) { if (remoteEpoch < epoch) return; throw new IOException("epoch"); }
                    switch (kind) {
                        case START:
                            if (running || launching || local != 1) throw new IOException("start_role");
                            seed = data.readLong(); int first = data.readInt(), second = data.readInt(), level = data.readInt(); night = data.readBoolean();
                            if (first < 1 || first > 6 || second != classes[1].ordinal() || level < 0 || level >= DifficultyHelper.Difficulty.values().length) throw new IOException("start");
                            classes[0] = HeroClass.values()[first]; difficulty = DifficultyHelper.Difficulty.values()[level]; launch(); break;
                        case READY:
                            int language = data.readInt();
                            if (language < 0 || language >= com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages.values().length) throw new IOException("language");
                            languages[1 - local] = com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages.values()[language]; ready = true; break;
                        case STATE:
                            if (!client()) throw new IOException("state_role");
                            replica.read(data); room = MapHelper.getInstance().getActiveRoomIdentifier(); lastStateAt = System.nanoTime(); break;
                        case INPUT:
                            if (!running || client()) throw new IOException("input_role");
                            int sequence = data.readInt(); boolean left = data.readBoolean(), right = data.readBoolean();
                            if (sequence > receivedInput) { receivedInput = sequence; inputAge = 0; Hero remote = heroes[1 - local];
                                remote.movingLeft = !remote.isDead() && left; remote.movingRight = !remote.isDead() && right;
                                if (left != right && !remote.isAttacking()) { remote.facingRight = right; remote.changeState(com.bilboldev.skillfulpixeldungeonplatformer.units.misc.UnitState.RUNNING); } }
                            break;
                        case ACTION:
                            if (!running || client()) throw new IOException("action_role");
                            execute(1 - local, data.readInt(), data.readInt()); break;
                        case HANDOFF:
                            if (!client()) throw new IOException("handoff_role");
                            int term = data.readInt(), next = data.readInt(); boolean exit = data.readBoolean();
                            if (term != epoch + 1 || next != local) throw new IOException("handoff");
                            epoch = term; authority = next; replica.takeAuthority(); clearPartyMenus();
                            send(HANDOFF_ACK, reply -> { });
                            if (exit) PartySession.disconnected();
                            break;
                        case END:
                            if (!client()) throw new IOException("end_role");
                            surfaceEnding = data.readBoolean(); leaving = true;
                            send(HANDOFF_ACK, reply -> { }); scheduleFinish(.1f); break;
                        case HANDOFF_ACK: if (leaving) finishLeave(); break;
                        case LEAVE: send(HANDOFF_ACK, reply -> { }); PartySession.disconnected(); break;
                        case MENU: if (!client()) throw new IOException("menu_role"); menus.receive(data); break;
                        case CHOICE: if (client()) throw new IOException("choice_role"); menus.choose(data); break;
                        case FEEDBACK: if (!client()) throw new IOException("feedback_role"); PartyFeedback.receive(data); break;
                        case MERCENARY_MENU:
                            if (!running || client()) throw new IOException("mercenary_role");
                            Unit ally = PartyMenuViews.actor(data.readUTF());
                            if (ally instanceof MercenaryAlly && !ally.isDead() && !heroes[1 - local].isDead())
                                scoped(1 - local, () -> WindowHelper.getInstance().addWindow(new MercenaryWindow((MercenaryAlly)ally).build()));
                            break;
                        default: throw new IOException("packet_kind");
                    }
                    if (data.available() != 0) throw new IOException("packet_tail");
                } catch (Exception invalid) { if (Gdx.app != null) Gdx.app.error("Party", "Invalid party data", invalid); failed("invalid_data"); }
            }
        };
    }
    public static void reset() {
        PartyFeedback.reset();
        generation++;
        if (connection != null) connection.close(); connection = null;
        active = running = ready = executing = leaving = launching = jumpIntent = remoteGone = surfaceEnding = false;
        replica = null; menus = null; context = -1; authority = local = epoch = inputSequence = receivedInput = 0;
        clock = inputAge = sendDelay = 0; room = null;
        launchDisconnected = false; lobbyRevision++;
        Arrays.fill(heroes, null); Arrays.fill(inventories, null); Arrays.fill(windows, null); Arrays.fill(classes, null); Arrays.fill(languages, null); hosts.clear();
    }
}
