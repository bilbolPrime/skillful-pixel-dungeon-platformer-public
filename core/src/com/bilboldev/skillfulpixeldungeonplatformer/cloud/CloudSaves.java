package com.bilboldev.skillfulpixeldungeonplatformer.cloud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper.RunSaveData;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.*;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.ChoiceDialogWindow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public final class CloudSaves {
    private static CloudSaves instance;
    private static boolean requested;
    private static String unavailableStatus;
    private final CloudStorage storage;
    private final CloudRunRepository repository;
    private final Set<String> blocked = new HashSet<>();
    private final Map<String, RunSaveData> pendingLocal = new HashMap<>();
    private final Map<String, String> pendingRetirements = new HashMap<>();
    private boolean reviewRequested = true, failed, syncing, prompting, noticeShown;
    private long lastFrame = System.currentTimeMillis(), lastPoll;
    private float sinceCheckpoint;
    private String status;
    private long lastFailureLog;

    private CloudSaves(CloudStorage storage) throws IOException {
        this.storage = storage;
        SaveHelper saves = SaveHelper.getInstance();
        Set<String> oldUnlocks = DifficultyHelper.getInstance().unlockedKeys();
        saves.useSteamAccount(storage.accountId());
        DifficultyHelper.getInstance().useSteamAccount(storage.accountId());
        repository = new CloudRunRepository(saves.desktopSaveRoot().child("cloud").file());

        File owner = Gdx.files.local("saves/steam-import-owner.txt").file();
        boolean canImport = !owner.exists() || storage.accountId().equals(new String(CloudFiles.read(owner), StandardCharsets.UTF_8).trim());
        File imported = saves.desktopSaveRoot().child("cloud/import.complete").file();
        if (canImport && !imported.exists()) {
            for (RunSaveData run : saves.unscopedDesktopRuns())
                if (!repository.runIds().contains(run.runId) && !repository.retired(run.runId)) repository.save(run);
            DifficultyHelper.getInstance().mergeUnlocks(oldUnlocks);
            repository.addUnlocks(oldUnlocks);
            CloudFiles.write(owner, storage.accountId().getBytes(StandardCharsets.UTF_8));
            CloudFiles.write(imported, new byte[]{1});
        }
        for (int slot = 0; slot < SaveHelper.RUN_SLOT_COUNT; slot++) {
            RunSaveData run = saves.loadSlot(slot);
            if (run == null || repository.retired(run.runId)) continue;
            boolean known = false;
            for (RunSaveData candidate : repository.candidates(run.runId))
                known |= Objects.equals(candidate.cloudRevision, run.cloudRevision) || CloudRunRepository.dominates(candidate, run);
            if (!known || run.cloudRevision == null) repository.save(run);
        }
    }

    public static void initialize(CloudStorage storage) {
        requested = storage != null;
        unavailableStatus = null;
        if (storage == null) return;
        try { instance = new CloudSaves(storage); instance.synchronize(true); }
        catch (IOException | RuntimeException e) {
            Gdx.app.error("CloudSaves", "Cloud initialization failed; scoped local saves remain available", e);
            unavailableStatus = Messages.get("cloud.error");
            storage.dispose();
        }
    }

    public static void requestReview() { if (instance != null) instance.reviewRequested = true; }
    public static boolean holdsGameplay() {
        return instance != null && (instance.prompting || instance.reviewRequested || System.currentTimeMillis() - instance.lastFrame > 2000);
    }
    public static boolean holdsInput() { return holdsGameplay() && !instance.prompting; }
    public static String status() { return instance == null ? unavailableStatus : instance.status; }
    public static void checkpoint() { if (instance != null) SaveHelper.getInstance().saveCurrentRun(); }

    public static void update() {
        if (instance == null) return;
        CloudSaves cloud = instance;
        long now = System.currentTimeMillis();
        boolean wake = now - cloud.lastFrame > 2000 || now < cloud.lastFrame;
        cloud.lastFrame = now;
        if (!cloud.prompting && (wake || cloud.reviewRequested || now - cloud.lastPoll >= 3000))
            cloud.synchronize(wake || cloud.reviewRequested);
    }

    public static void gameplayAdvanced(float delta) {
        if (instance == null) return;
        instance.sinceCheckpoint += delta;
        if (instance.sinceCheckpoint >= 20f) {
            instance.sinceCheckpoint = 0;
            SaveHelper.getInstance().saveCurrentRun();
        }
    }

    public static boolean recordLocalSave(RunSaveData run) {
        if (instance == null) {
            if (requested) { run.cloudParent = run.cloudRevision; run.cloudRevision = null; }
            return true;
        }
        CloudSaves cloud = instance;
        if (cloud.repository.retired(run.runId) || cloud.prompting) return false;
        try { cloud.repository.save(run); return true; }
        catch (IOException | RuntimeException e) {
            cloud.pendingLocal.put(run.runId, run);
            cloud.failure(e);
            return true;
        }
    }

    public static boolean retire(String id, String reason) {
        if (id == null) return true;
        if (instance == null) {
            if (requested) try {
                if (!id.matches("[0-9a-f-]{36}")) return false;
                CloudFiles.write(SaveHelper.getInstance().desktopSaveRoot().child("cloud/retired/" + id + ".txt").file(),
                        reason.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) { Gdx.app.error("CloudSaves", "Could not record ended run", e); return false; }
            return true;
        }
        try { instance.repository.retire(id, reason); return true; }
        catch (IOException | RuntimeException e) {
            instance.pendingRetirements.put(id, reason); instance.failure(e); return false;
        }
    }

    private void synchronize(boolean force) {
        if (syncing) return;
        syncing = true; lastPoll = System.currentTimeMillis();
        try {
            for (Map.Entry<String, String> ended : new HashMap<>(pendingRetirements).entrySet()) {
                repository.retire(ended.getKey(), ended.getValue()); pendingRetirements.remove(ended.getKey());
            }
            for (RunSaveData pending : new ArrayList<>(pendingLocal.values())) {
                if (!repository.retired(pending.runId)) repository.save(pending);
                pendingLocal.remove(pending.runId);
            }
            repository.addUnlocks(DifficultyHelper.getInstance().unlockedKeys());
            if (storage.enabled()) repository.pull(storage, force || failed);
            DifficultyHelper.getInstance().mergeUnlocks(repository.unlocks());
            failed = false; status = storage.enabled() ? null : Messages.get("cloud.offline");
            reviewRequested = false;
            reconcileActive();
            fillSlots();
            for (String id : repository.runIds()) if (repository.hasUnresolvedRejectedSave(id)) status = Messages.get("cloud.error");
            if (storage.enabled() && !failed) repository.push(storage, blocked);
        } catch (IOException | RuntimeException e) { failure(e); }
        finally { reviewRequested = false; syncing = false; }
    }

    private void reconcileActive() throws IOException {
        SaveHelper saves = SaveHelper.getInstance();
        String id = saves.activeRunId();
        if (id == null || !(SkillfulPixelDungeonPlatformer.getActiveScreen() instanceof GameScreen)) return;
        if (repository.retired(id)) {
            blocked.add(id); showEnded(); return;
        }
        RunSaveData base = saves.activeMetadata();
        if (base == null || base.cloudRevision == null) return;
        List<RunSaveData> candidates = repository.candidates(id);
        for (RunSaveData candidate : candidates) if (!candidate.cloudRevision.equals(base.cloudRevision)
                && !CloudRunRepository.dominates(base, candidate)) {
            blocked.add(id);

            repository.archive(base);
            boolean hasUncheckpointedPlay = saves.uncheckpointedSeconds() > .01;
            if (hasUncheckpointedPlay) SaveHelper.getInstance().saveCurrentRun();
            showChoices(saves.activeSlot(), repository.candidates(id), true);
            return;
        }
        blocked.remove(id);
    }


    public static void fillSlots() {
        if (instance == null) return;
        CloudSaves cloud = instance;
        SaveHelper saves = SaveHelper.getInstance();
        Map<String, Integer> occupied = new HashMap<>();
        Set<String> ended = new HashSet<>();
        for (int slot = 0; slot < SaveHelper.RUN_SLOT_COUNT; slot++) {
            RunSaveData run = saves.loadSlot(slot);
            if (run != null) {
                if (cloud.repository.retired(run.runId)) ended.add(run.runId);
                else occupied.put(run.runId, slot);
            }
        }
        saves.removeRetiredSlots(ended);
        for (String id : cloud.repository.runIds()) {
            if (id.equals(saves.activeRunId())) continue;
            List<RunSaveData> candidates = cloud.repository.candidates(id);
            if (candidates.isEmpty()) continue;
            int slot = occupied.containsKey(id) ? occupied.get(id) : saves.firstEmptySlot();
            if (slot < 0) continue;
            RunSaveData existing = saves.loadSlot(slot), selected = candidates.get(0);
            if (candidates.size() > 1) cloud.blocked.add(id); else cloud.blocked.remove(id);
            if (existing == null || !Objects.equals(existing.cloudRevision, selected.cloudRevision)) {
                if (!saves.installCloudSlot(slot, selected)) { cloud.failure(new IOException("Could not install cloud run locally")); return; }
            }
        }
    }

    public static void resumeSlot(int slot, RunSaveData run, Runnable normalResume) {
        if (instance == null) { normalResume.run(); return; }
        CloudSaves cloud = instance;
        cloud.synchronize(true);
        if (cloud.repository.retired(run.runId)) { fillSlots(); return; }
        List<RunSaveData> candidates = cloud.repository.candidates(run.runId);
        if (candidates.size() > 1) cloud.showChoices(slot, candidates, false);
        else {
            RunSaveData latest = SaveHelper.getInstance().loadSlot(slot);
            if (latest != null && latest.runId.equals(run.runId)) cloud.load(slot, latest);
        }
    }

    private void showChoices(int slot, List<RunSaveData> candidates, boolean active) {
        if (prompting || candidates.isEmpty()) return;
        prompting = true;
        if (active) SkillfulPixelDungeonPlatformer.getActiveScreen().pause();
        String message = Messages.get(candidates.size() == 1 ? "cloud.continued" : "cloud.conflict");
        ChoiceDialogWindow dialog = modal(message);
        for (RunSaveData candidate : candidates.subList(0, Math.min(3, candidates.size()))) {
            String label = candidates.size() == 1 ? Messages.get("cloud.continue")
                    : Messages.get("cloud.version", new Object[]{Messages.get(repository.device.equals(candidate.cloudDevice) ? "cloud.this_device" : "cloud.other_device"),
                    candidate.currentDepth, candidate.hero.level,
                    String.format(Locale.ROOT, "%d:%02d", (int)(candidate.playedSeconds / 60), (int)candidate.playedSeconds % 60)});
            dialog.addChoice(label, () -> {
                try {
                    if (storage.enabled()) repository.pull(storage, true);
                    if (repository.retired(candidate.runId)) {
                        WindowHelper.getInstance().closeWindow(dialog); prompting = false;
                        if (active) showEnded(); else fillSlots();
                        return;
                    }
                    List<RunSaveData> current = repository.candidates(candidate.runId);
                    boolean stillAvailable = false;
                    for (RunSaveData run : current) stillAvailable |= run.cloudRevision.equals(candidate.cloudRevision);
                    if (!stillAvailable) {
                        WindowHelper.getInstance().closeWindow(dialog); prompting = false;
                        showChoices(slot, current, active); return;
                    }
                    RunSaveData selected = CloudFiles.decode(CloudFiles.encode(candidate), RunSaveData.class);
                    RunSaveData recovery = active ? SaveHelper.getInstance().activeMetadata() : SaveHelper.getInstance().loadSlotBackup(slot);
                    repository.resolve(selected);
                    if (!SaveHelper.getInstance().replaceActiveCloudSlot(slot, selected)) throw new IOException("Could not install selected save");
                    blocked.remove(selected.runId); prompting = false;
                    load(slot, selected, recovery, candidate.cloudRevision);
                } catch (IOException | RuntimeException e) {
                    WindowHelper.getInstance().closeWindow(dialog); prompting = false; failure(e);
                    if (SkillfulPixelDungeonPlatformer.getActiveScreen() instanceof GameScreen) SkillfulPixelDungeonPlatformer.getActiveScreen().resume();
                }
            });
        }
        if (candidates.size() > 3) dialog.addChoice(Messages.get("cloud.more"), () -> {
            List<RunSaveData> next = new ArrayList<>(candidates); Collections.rotate(next, -3);
            WindowHelper.getInstance().closeWindow(dialog); prompting = false; showChoices(slot, next, active);
        });
        WindowHelper.getInstance().addWindow(dialog.build());
    }

    private void showEnded() {
        if (prompting) return;
        prompting = true;
        SkillfulPixelDungeonPlatformer.getActiveScreen().pause();
        ChoiceDialogWindow dialog = modal(Messages.get("cloud.ended"));
        dialog.addChoice(Messages.get("cloud.return"), () -> {
            SaveHelper.getInstance().detachRun(); prompting = false;
            WindowHelper.getInstance().hideAll(); fillSlots();
            SkillfulPixelDungeonPlatformer.transition(new TitleScreen().openRosterOnEnter(), true);
        });
        WindowHelper.getInstance().addWindow(dialog.build());
    }

    private static ChoiceDialogWindow modal(String text) {
        return new ChoiceDialogWindow("images/intro/play.png", text, 1450, 470) {
            @Override public void hide() {                                                           }
            @Override public boolean click(float x, float y) { return !contains(x, y) || super.click(x, y); }
            @Override public boolean keyDown(int key) {
                if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) return true;
                return super.keyDown(key);
            }
        };
    }

    private void load(int slot, RunSaveData data) {
        load(slot, data, SaveHelper.getInstance().loadSlotBackup(slot), data.cloudRevision);
    }

    private void load(int slot, RunSaveData data, RunSaveData recovery, String sourceRevision) {
        SaveHelper.getInstance().detachRun();
        WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new LoadingScreen().prepare(new GameScreen(slot, data), SkillfulPixelDungeonPlatformer.getActiveScreen())
                .onFailure(error -> {
                    failure(error); SaveHelper.getInstance().detachRun();
                    try {
                        repository.reject(data.runId, data.cloudRevision);
                        repository.reject(data.runId, sourceRevision);
                        if (recovery != null && data.runId.equals(recovery.runId) && !data.cloudRevision.equals(recovery.cloudRevision)) {
                            RunSaveData rollback = CloudFiles.decode(CloudFiles.encode(recovery), RunSaveData.class);
                            repository.save(rollback);
                            if (!SaveHelper.getInstance().installCloudSlot(slot, rollback)) throw new IOException("Could not restore working save");
                            SkillfulPixelDungeonPlatformer.transition(new LoadingScreen().prepare(new GameScreen(slot, rollback), null)
                                    .onFailure(this::loadUnavailable)
                                    .onLoaded(() -> {
                                        com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow.openGameplay();
                                        WindowHelper.getInstance().addWindow(new com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow(
                                                1250, 320, Messages.get("cloud.restored")).build());
                                    }));
                            return;
                        }
                    } catch (IOException | RuntimeException recoveryError) { failure(recoveryError); }
                    loadUnavailable(error);
                }));
    }

    private void loadUnavailable(RuntimeException error) {
        failure(error); SaveHelper.getInstance().detachRun(); WindowHelper.getInstance().hideAll();
        SkillfulPixelDungeonPlatformer.transition(new TitleScreen().openRosterOnEnter());
        WindowHelper.getInstance().addWindow(new com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow(
                1250, 320, Messages.get("cloud.load_failed")).build());
    }

    private void failure(Exception e) {
        failed = true; status = Messages.get("cloud.error");
        long now = System.currentTimeMillis();
        if (now - lastFailureLog > 30000) {
            Gdx.app.error("CloudSaves", "Cloud synchronization paused; recovery copies retained", e);
            lastFailureLog = now;
        }
        if (!noticeShown && SkillfulPixelDungeonPlatformer.getActiveScreen() instanceof GameScreen
                && SkillfulPixelDungeonPlatformer.getActiveScreen().isInitialized() && UnitHelper.getInstance().getHero() != null) {
            EffectsHelper.getInstance().message(UnitHelper.getInstance().getHero(), status, Color.LIGHT_GRAY, 0f);
            noticeShown = true;
        }
    }

    public static void shutdown() {
        if (instance == null) return;
        CloudSaves cloud = instance;
        if (!cloud.prompting) SaveHelper.getInstance().saveCurrentRun();
        cloud.synchronize(true);
        cloud.storage.dispose(); instance = null; requested = false;
    }
}
