package com.bilboldev.skillfulpixeldungeonplatformer.cloud;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper.RunSaveData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public final class CloudRunRepository {
    public static final String PREFIX = "pdp2_";
    private final File root;
    public final String device;
    private Journal journal;
    private boolean journalDirty;
    private final Map<String, Head> heads = new TreeMap<>();
    private final Map<String, String> remoteVersions = new HashMap<>(), remoteHashes = new HashMap<>();

    public CloudRunRepository(File root) throws IOException {
        this.root = root;
        root.mkdirs();
        File identity = new File(root, "device.id");
        if (identity.exists()) device = new String(CloudFiles.read(identity), StandardCharsets.UTF_8).trim();
        else {
            device = UUID.randomUUID().toString();
            CloudFiles.write(identity, device.getBytes(StandardCharsets.UTF_8));
        }
        requireId(device);
        File state = new File(root, "journal.bin");
        journal = state.exists() || new File(state + ".bak").exists() ? readLocal(state, Journal.class) : new Journal();
        validateJournal(journal);
        Journal recovered = copyJournal();
        File[] ends = new File(root, "retired").listFiles((dir, name) -> name.endsWith(".txt"));
        if (ends != null) for (File end : ends) {
            String id = end.getName().substring(0, end.getName().length() - 4);
            requireId(id);
            recovered.ended.put(id, new String(CloudFiles.read(end), StandardCharsets.UTF_8));
        }
        validateJournal(recovered); storeJournal(recovered);
        File[] files = new File(root, "heads").listFiles((dir, name) -> name.endsWith(".bin"));
        if (files != null) for (File file : files) {
            Head head = readLocal(file, Head.class);
            validateHead(file.getName(), head);
            heads.put(file.getName(), head);
        }
    }

    private static <T> T readLocal(File file, Class<T> type) throws IOException {
        try { return CloudFiles.decode(CloudFiles.read(file), type); }
        catch (IOException first) {
            try {
                byte[] bytes = CloudFiles.read(new File(file + ".bak"));
                T result = CloudFiles.decode(bytes, type);
                CloudFiles.restoreBackup(file, bytes);
                return result;
            }
            catch (IOException second) { first.addSuppressed(second); throw first; }
        }
    }

    public Set<String> runIds() {
        Set<String> result = new TreeSet<>();
        for (Head head : heads.values()) if (!retired(head.run.runId)) result.add(head.run.runId);
        return result;
    }
    public boolean retired(String id) { return journal.ended.containsKey(id); }
    public String endReason(String id) { return journal.ended.get(id); }
    public Set<String> unlocks() { return new TreeSet<>(journal.unlocks); }

    public void addUnlocks(Set<String> keys) throws IOException {
        Journal next = copyJournal();
        if (next.unlocks.addAll(keys)) storeJournal(next);
    }

    public void retire(String id, String reason) throws IOException {
        requireId(id);
        if (!Arrays.asList("DEAD", "WON", "DELETED").contains(reason)) throw new IOException("Unknown retirement reason");
        Journal next = copyJournal();
        if (!next.ended.containsKey(id)) { next.ended.put(id, reason); storeJournal(next); }
    }

    public List<RunSaveData> candidates(String id) {
        List<RunSaveData> result = new ArrayList<>();
        if (retired(id)) return result;
        for (Head head : heads.values()) if (id.equals(head.run.runId) && !rejected(head.run)) {
            RunSaveData candidate = head.run;
            boolean obsolete = false;
            for (Head other : heads.values()) if (id.equals(other.run.runId) && other != head && !rejected(other.run)
                    && (dominates(other.run, candidate) || other.run.cloudRevision.equals(candidate.cloudRevision)
                    && other.writer.compareTo(head.writer) < 0)) { obsolete = true; break; }
            if (!obsolete) result.add(candidate);
        }
        result.sort((a, b) -> {
            int time = Double.compare(b.playedSeconds, a.playedSeconds);
            return time != 0 ? time : a.cloudRevision.compareTo(b.cloudRevision);
        });
        return result;
    }

    public void reject(String id, String revision) throws IOException {
        requireId(id); requireId(revision);
        CloudFiles.write(new File(root, "quarantine/" + revision + ".txt"), id.getBytes(StandardCharsets.UTF_8));
    }
    private boolean rejected(RunSaveData run) { return new File(root, "quarantine/" + run.cloudRevision + ".txt").exists(); }
    public boolean hasUnresolvedRejectedSave(String id) {
        for (Head head : heads.values()) if (id.equals(head.run.runId) && rejected(head.run)) {
            boolean superseded = false;
            for (RunSaveData valid : candidates(id)) superseded |= dominates(valid, head.run);
            if (!superseded) return true;
        }
        return false;
    }


    public static boolean dominates(RunSaveData newer, RunSaveData older) {
        if (newer.cloudClock == null || older.cloudClock == null || !Objects.equals(newer.runId, older.runId)) return false;
        boolean greater = false;
        Set<String> devices = new HashSet<>(newer.cloudClock.keySet()); devices.addAll(older.cloudClock.keySet());
        for (String key : devices) {
            long n = newer.cloudClock.containsKey(key) ? newer.cloudClock.get(key) : 0;
            long o = older.cloudClock.containsKey(key) ? older.cloudClock.get(key) : 0;
            if (n < o) return false;
            greater |= n > o;
        }
        return greater;
    }

    public void save(RunSaveData run) throws IOException {
        if (retired(run.runId)) throw new IOException("Run has ended");
        if (run.cloudClock == null) run.cloudClock = new HashMap<>();
        else run.cloudClock = new HashMap<>(run.cloudClock);
        long previous = run.cloudClock.containsKey(device) ? run.cloudClock.get(device) : 0;
        for (Head known : heads.values()) if (run.runId.equals(known.run.runId) && known.run.cloudClock.containsKey(device))
            previous = Math.max(previous, known.run.cloudClock.get(device));
        if (previous == Long.MAX_VALUE) throw new IOException("Revision limit exceeded");
        run.cloudClock.put(device, previous + 1);
        run.cloudParent = run.cloudRevision;
        run.cloudRevision = UUID.randomUUID().toString();
        run.cloudDevice = device;
        Head head = new Head(); head.writer = device; head.run = run;
        String name = headName(run.runId, device);
        validateHead(name, head);
        putHead(name, head, CloudFiles.encode(head));
    }


    public void resolve(RunSaveData selected) throws IOException {
        HashMap<String, Long> clock = new HashMap<>();
        for (RunSaveData candidate : candidates(selected.runId)) {
            archive(candidate);
            for (Map.Entry<String, Long> entry : candidate.cloudClock.entrySet())
                clock.put(entry.getKey(), Math.max(clock.containsKey(entry.getKey()) ? clock.get(entry.getKey()) : 0, entry.getValue()));
        }
        selected.cloudClock = clock;
        save(selected);
    }

    public void archive(RunSaveData run) throws IOException {
        if (run.cloudRevision == null) return;
        File file = new File(root, "recovery/" + run.runId + "_" + run.cloudRevision + ".bin");
        if (!file.exists()) CloudFiles.write(file, CloudFiles.encode(run));
    }


    public void pull(CloudStorage storage, boolean force) throws IOException {
        Map<String, String> files = storage.files();
        Map<String, Head> received = new TreeMap<>();
        Map<String, byte[]> bytes = new HashMap<>();
        Journal merged = copyJournal();
        for (Map.Entry<String, String> file : files.entrySet()) {
            String name = file.getKey();
            if (!name.startsWith(PREFIX)) continue;
            if (!force && file.getValue().equals(remoteVersions.get(name))) continue;
            byte[] data = storage.read(name);
            if (name.matches("pdp2_state_[0-9a-f-]{36}\\.bin")) {
                Journal incoming = CloudFiles.decode(data, Journal.class); validateJournal(incoming);
                for (Map.Entry<String, String> end : incoming.ended.entrySet())
                    if (!merged.ended.containsKey(end.getKey())) merged.ended.put(end.getKey(), end.getValue());
                merged.unlocks.addAll(incoming.unlocks);
            } else {
                Head head = CloudFiles.decode(data, Head.class); validateHead(name, head);
                received.put(name, head);
            }
            bytes.put(name, data);
        }
        storeJournal(merged);
        for (Map.Entry<String, Head> incoming : received.entrySet()) {
            Head local = heads.get(incoming.getKey()), remote = incoming.getValue();
            if (local != null && local.run.cloudRevision.equals(remote.run.cloudRevision)) {
                if (!CloudFiles.hash(CloudFiles.read(new File(root, "heads/" + incoming.getKey()))).equals(CloudFiles.hash(bytes.get(incoming.getKey()))))
                    throw new IOException("Same revision has different contents");
            } else if (local == null || dominates(remote.run, local.run)) {
                putHead(incoming.getKey(), remote, bytes.get(incoming.getKey()));
            } else if (!dominates(local.run, remote.run)) {
                archive(remote.run); archive(local.run);
                throw new IOException("Installation identity conflict; both versions retained");
            }
        }
        remoteVersions.clear(); remoteVersions.putAll(files);
        remoteHashes.keySet().retainAll(files.keySet());
        for (Map.Entry<String, byte[]> entry : bytes.entrySet()) remoteHashes.put(entry.getKey(), CloudFiles.hash(entry.getValue()));
    }


    public void push(CloudStorage storage, Set<String> blocked) throws IOException {
        publish(storage, PREFIX + "state_" + device + ".bin", CloudFiles.encode(journal));
        for (Map.Entry<String, Head> entry : heads.entrySet()) {
            Head head = entry.getValue();
            if (!head.writer.equals(device) || blocked.contains(head.run.runId) || retired(head.run.runId)
                    || hasUnresolvedRejectedSave(head.run.runId)) continue;
            publish(storage, entry.getKey(), CloudFiles.read(new File(root, "heads/" + entry.getKey())));
        }

        for (String name : new ArrayList<>(storage.files().keySet())) {
            if (name.matches("pdp2_run_[0-9a-f-]{36}_[0-9a-f-]{36}\\.bin") && retired(name.substring(9, 45))) {
                storage.delete(name); remoteHashes.remove(name); remoteVersions.remove(name);
            }
        }
    }

    private void publish(CloudStorage storage, String name, byte[] data) throws IOException {
        String hash = CloudFiles.hash(data);
        if (!hash.equals(remoteHashes.get(name))) { storage.write(name, data); remoteHashes.put(name, hash); }
    }

    private void putHead(String name, Head head, byte[] data) throws IOException {
        Head previous = heads.get(name);
        if (previous != null && !previous.run.cloudRevision.equals(head.run.cloudRevision)
                && !dominates(head.run, previous.run)) archive(previous.run);
        CloudFiles.write(new File(root, "heads/" + name), data);
        heads.put(name, head);
    }

    private Journal copyJournal() {
        Journal copy = new Journal(); copy.ended.putAll(journal.ended); copy.unlocks.addAll(journal.unlocks); return copy;
    }
    private void storeJournal(Journal next) throws IOException {
        boolean changed = !next.ended.equals(journal.ended) || !next.unlocks.equals(journal.unlocks);
        for (Map.Entry<String, String> end : next.ended.entrySet()) {
            File marker = new File(root, "retired/" + end.getKey() + ".txt");
            if (!marker.exists()) CloudFiles.write(marker, end.getValue().getBytes(StandardCharsets.UTF_8));
        }
        journal = next;
        if (changed || journalDirty || !new File(root, "journal.bin").exists()) {
            journalDirty = true;
            CloudFiles.write(new File(root, "journal.bin"), CloudFiles.encode(next));
            journalDirty = false;
        }
    }
    public static String headName(String id, String writer) { return PREFIX + "run_" + id + "_" + writer + ".bin"; }
    private static void requireId(String id) throws IOException {
        if (id == null || !id.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) throw new IOException("Invalid save identity");
    }
    private static void validateHead(String name, Head head) throws IOException {
        if (head == null || head.schema != 1 || head.run == null) throw new IOException("Unsupported cloud snapshot");
        requireId(head.writer); requireId(head.run.runId); requireId(head.run.cloudRevision);
        if (!name.equals(headName(head.run.runId, head.writer)) || !SaveHelper.isValidRun(head.run)
                || head.run.hero.hp <= 0 || head.run.cloudClock == null || head.run.cloudClock.isEmpty() || head.run.cloudClock.size() > 256
                || !head.run.cloudClock.containsKey(head.writer) || !Double.isFinite(head.run.playedSeconds) || head.run.playedSeconds < 0)
            throw new IOException("Invalid run snapshot");
        for (Map.Entry<String, Long> clock : head.run.cloudClock.entrySet()) {
            requireId(clock.getKey()); if (clock.getValue() == null || clock.getValue() <= 0) throw new IOException("Invalid save revision");
        }
    }
    private static void validateJournal(Journal state) throws IOException {
        if (state == null || state.schema != 1 || state.ended == null || state.unlocks == null) throw new IOException("Unsupported cloud journal");
        for (Map.Entry<String, String> end : state.ended.entrySet()) {
            requireId(end.getKey());
            if (!Arrays.asList("DEAD", "WON", "DELETED").contains(end.getValue())) throw new IOException("Invalid retirement record");
        }
        for (String key : state.unlocks) if (!key.matches("(WARRIOR|WIZARD|ROGUE|ARCHER|NECROMANCER|MERCENARY):(NIGHTMARE|HELL)"))
            throw new IOException("Invalid difficulty unlock");
    }
    public static final class Head implements Serializable {
        private static final long serialVersionUID = 1L;
        public int schema = 1;
        public String writer;
        public RunSaveData run;
    }
    public static final class Journal implements Serializable {
        private static final long serialVersionUID = 1L;
        public int schema = 1;
        public TreeMap<String, String> ended = new TreeMap<>();
        public TreeSet<String> unlocks = new TreeSet<>();
    }
}
