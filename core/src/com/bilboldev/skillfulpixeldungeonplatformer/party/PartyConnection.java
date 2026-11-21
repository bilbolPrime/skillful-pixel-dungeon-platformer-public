package com.bilboldev.skillfulpixeldungeonplatformer.party;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class PartyConnection implements Closeable {
    public static final int PROTOCOL = 1, DISCOVERY_PORT = 47875, MAX_PACKET = 4 * 1024 * 1024;
    private static final int MAX_WIRE = 256 * 1024;
    public static final String FORK_ID = "00000000-0000-0000-0000-000000000000";
    private static final String MAGIC = "PDPL-LAN";
    public interface Listener {
        void connected(int hero);
        void received(byte[] packet);
        void disconnected(String reason);
        default void discovered(String address, int port, int hero) { }
    }
    private final Listener listener;
    private final ArrayBlockingQueue<Runnable> events = new ArrayBlockingQueue<>(64);
    private final ArrayBlockingQueue<byte[]> outgoing = new ArrayBlockingQueue<>(32);
    private volatile boolean closed, connected;
    private volatile Socket socket;
    private volatile ServerSocket server;
    private volatile DatagramSocket discovery;
    private volatile PartySecurity security;
    private String code;
    private int hero, port;

    public PartyConnection(Listener listener) { this.listener = listener; }
    public boolean connected() { return connected && !closed; }
    public String code() { return code; }
    public int port() { return port; }

    public void host(int hero) throws IOException {
        requireIdle(hero);
        this.hero = hero;
        code = PartySecurity.newCode();
        server = new ServerSocket(0, 4);
        server.setSoTimeout(1000);
        port = server.getLocalPort();
        worker("accept", () -> {
            while (!closed && !connected) {
                try {
                    Socket candidate = server.accept();
                    socket = candidate;
                    candidate.setSoTimeout(4000);
                    candidate.setTcpNoDelay(true);
                    handshake(candidate, true);
                    startStreams(candidate);
                } catch (SocketTimeoutException ignored) { }
                catch (Exception invalid) {
                    closeSocket(socket);
                    socket = null;

                    if (!closed) Thread.sleep(150);
                }
            }
        });
        advertise();
    }

    public void join(String address, int port, int hero, String code) {
        requireIdle(hero);
        if (port < 1 || port > 65535 || !validAddress(address)) throw new IllegalArgumentException("address");
        this.hero = hero;
        this.code = PartySecurity.normalize(code);
        worker("join", () -> {
            Socket peer = new Socket();
            socket = peer;
            peer.connect(new InetSocketAddress(address, port), 5000);
            peer.setSoTimeout(4000);
            peer.setTcpNoDelay(true);
            handshake(peer, false);
            startStreams(peer);
        });
    }

    private void handshake(Socket peer, boolean host) throws Exception {
        PartySecurity keys = new PartySecurity(code, host);
        DataInputStream input = new DataInputStream(peer.getInputStream());
        DataOutputStream output = new DataOutputStream(peer.getOutputStream());
        byte[] hello = packet(data -> {
            data.writeUTF(MAGIC); data.writeInt(PROTOCOL); data.writeUTF(FORK_ID);
            data.writeInt(hero); data.writeUTF(keys.publicKey()); data.writeUTF(keys.nonce());
        });
        writeFrame(output, PartySecurity.encryptSetup(hello, false));
        DataInputStream other = stream(PartySecurity.decryptSetup(readFrame(input, 4096), false));
        if (!MAGIC.equals(other.readUTF()) || other.readInt() != PROTOCOL || !FORK_ID.equals(other.readUTF()))
            throw new IOException("version");
        int remoteHero = other.readInt();
        if (remoteHero < 1 || remoteHero > 6) throw new IOException("hero");
        String publicKey = other.readUTF(), nonce = other.readUTF();
        if (other.available() != 0) throw new IOException("handshake");
        keys.agree(publicKey, nonce, Integer.toString(host ? remoteHero : hero), Integer.toString(host ? hero : remoteHero));
        writeFrame(output, PartySecurity.encryptSetup(packet(data -> data.writeUTF(keys.proof())), false));
        DataInputStream proof = stream(PartySecurity.decryptSetup(readFrame(input, 4096), false));
        keys.verify(proof.readUTF());
        if (proof.available() != 0 || closed) throw new IOException("closed");
        security = keys;
        connected = true;
        peer.setSoTimeout(6000);
        post(() -> listener.connected(remoteHero));
        if (server != null) server.close();
        if (discovery != null) discovery.close();
    }

    private void startStreams(Socket peer) {
        worker("write", () -> {
            DataOutputStream output = new DataOutputStream(peer.getOutputStream());
            while (!closed) {
                byte[] value = outgoing.poll(1, TimeUnit.SECONDS);
                if (value == null) value = new byte[0];
                byte[] encrypted = security.encrypt(value);
                writeFrame(output, encrypted);
            }
        });
        worker("read", () -> {
            DataInputStream input = new DataInputStream(peer.getInputStream());
            long window = System.nanoTime();
            int packets = 0;
            while (!closed) {
                byte[] encrypted = readFrame(input, MAX_WIRE + 25);
                long now = System.nanoTime();
                if (now - window > 1_000_000_000L) { window = now; packets = 0; }
                if (++packets > 160) throw new IOException("traffic");
                byte[] value = security.decrypt(encrypted);
                if (value.length != 0) {
                    byte[] decoded = unpack(value);
                    post(() -> listener.received(decoded));
                }
            }
        });
    }

    public boolean send(byte[] value) {
        if (!connected() || value == null || value.length > MAX_PACKET) return false;
        byte[] encoded;
        try { encoded = pack(value); } catch (IOException invalid) { return false; }
        if (encoded.length > MAX_WIRE) return false;
        if (!outgoing.offer(encoded)) { fail("slow_peer"); return false; }
        return true;
    }

    public static byte[] pack(byte[] value) throws IOException {
        if (value == null || value.length < 1 || value.length > MAX_PACKET) throw new IOException("packet_size");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream header = new DataOutputStream(bytes);
        boolean compressed = value.length > 128;
        header.writeBoolean(compressed); header.writeInt(value.length);
        if (compressed) {
            java.util.zip.Deflater deflater = new java.util.zip.Deflater(java.util.zip.Deflater.BEST_SPEED);
            try (java.util.zip.DeflaterOutputStream output = new java.util.zip.DeflaterOutputStream(bytes, deflater)) { output.write(value); }
            finally { deflater.end(); }
        } else header.write(value);
        if (bytes.size() > MAX_WIRE) throw new IOException("wire_size");
        return bytes.toByteArray();
    }
    public static byte[] unpack(byte[] value) throws IOException {
        if (value == null || value.length < 5 || value.length > MAX_WIRE) throw new IOException("wire_size");
        DataInputStream input = stream(value);
        boolean compressed = input.readBoolean(); int length = input.readInt();
        if (length < 1 || length > MAX_PACKET) throw new IOException("unpacked_size");
        byte[] decoded = new byte[length];
        try (InputStream body = compressed ? new java.util.zip.InflaterInputStream(input) : input) {
            new DataInputStream(body).readFully(decoded);
            if (body.read() != -1) throw new IOException("unpacked_tail");
        }
        return decoded;
    }

    public void poll() {
        for (int count = 0; count < 64; count++) {
            Runnable event = events.poll();
            if (event == null) return;
            event.run();
        }
    }

    public void discover() {
        worker("discover", () -> {
            DatagramSocket udp = new DatagramSocket();
            discovery = udp;
            udp.setBroadcast(true); udp.setSoTimeout(250);
            byte[] query = PartySecurity.encryptSetup(MAGIC.getBytes(StandardCharsets.UTF_8), true);
            long next = 0;
            while (!closed) {
                long now = System.nanoTime();
                if (now >= next) {
                    next = now + 2_000_000_000L;
                    Set<InetAddress> targets = new HashSet<>();
                    targets.add(InetAddress.getByName("255.255.255.255"));
                    for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces()))
                        for (InterfaceAddress local : nic.getInterfaceAddresses()) if (local.getBroadcast() != null) targets.add(local.getBroadcast());
                    for (InetAddress target : targets) try { udp.send(new DatagramPacket(query, query.length, target, DISCOVERY_PORT)); } catch (IOException ignored) { }
                }
                try {
                    DatagramPacket response = new DatagramPacket(new byte[2048], 2048);
                    udp.receive(response);
                    DataInputStream data = stream(PartySecurity.decryptSetup(Arrays.copyOf(response.getData(), response.getLength()), true));
                    if (!MAGIC.equals(data.readUTF()) || data.readInt() != PROTOCOL) continue;
                    int remotePort = data.readInt(), remoteHero = data.readInt();
                    if (remotePort < 1 || remotePort > 65535 || remoteHero < 1 || remoteHero > 6 || data.available() != 0) continue;
                    String address = response.getAddress().getHostAddress();
                    post(() -> listener.discovered(address, remotePort, remoteHero));
                } catch (SocketTimeoutException ignored) { }
                catch (GeneralSecurityException | EOFException ignored) { }
            }
        });
    }

    private void advertise() {
        worker("advertise", () -> {
            DatagramSocket udp;
            try { udp = new DatagramSocket(DISCOVERY_PORT); }
            catch (BindException occupied) { return; }
            discovery = udp;
            udp.setSoTimeout(1000);
            long next = 0;
            while (!closed && !connected) {
                try {
                    DatagramPacket request = new DatagramPacket(new byte[2048], 2048);
                    udp.receive(request);
                    long now = System.nanoTime();
                    if (now < next) continue;
                    next = now + 50_000_000L;
                    byte[] query = PartySecurity.decryptSetup(Arrays.copyOf(request.getData(), request.getLength()), true);
                    if (!MAGIC.equals(new String(query, StandardCharsets.UTF_8))) continue;
                    byte[] answer = PartySecurity.encryptSetup(packet(data -> {
                        data.writeUTF(MAGIC); data.writeInt(PROTOCOL); data.writeInt(port); data.writeInt(hero);
                    }), true);
                    udp.send(new DatagramPacket(answer, answer.length, request.getAddress(), request.getPort()));
                } catch (SocketTimeoutException ignored) { }
                catch (GeneralSecurityException ignored) { }
            }
        });
    }

    public static List<String> addresses() {
        List<String> result = new ArrayList<>();
        try {
            for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces()))
                if (nic.isUp() && !nic.isLoopback()) for (InetAddress ip : Collections.list(nic.getInetAddresses()))
                    if (ip instanceof Inet4Address && !ip.isLoopbackAddress()) result.add(ip.getHostAddress());
        } catch (SocketException ignored) { }
        return result;
    }

    public static boolean validAddress(String value) {
        if (value == null) return false;
        String[] parts = value.split("\\.", -1);
        if (parts.length != 4) return false;
        try {
            for (String part : parts) if (!part.matches("[0-9]{1,3}") || Integer.parseInt(part) > 255) return false;
            return Integer.parseInt(parts[0]) > 0 && Integer.parseInt(parts[0]) < 224;
        } catch (NumberFormatException invalid) { return false; }
    }

    private void requireIdle(int hero) {
        if (closed || server != null || socket != null || hero < 1 || hero > 6) throw new IllegalStateException("connection");
    }
    private interface Work { void run() throws Exception; }
    private void worker(String name, Work action) {
        Thread thread = new Thread(() -> { try { action.run(); } catch (Exception failed) { if (!closed && !connectedDiscoveryClose(name)) fail(name + ":" + failed.getClass().getSimpleName()); } }, "Party-" + name);
        thread.setDaemon(true); thread.start();
    }
    private boolean connectedDiscoveryClose(String name) { return connected && name.equals("advertise"); }
    private void post(Runnable event) throws IOException { if (!events.offer(event)) throw new IOException("event_queue"); }
    private synchronized void fail(String reason) {
        if (closed) return;
        close();
        if (!events.offer(() -> listener.disconnected(reason))) { events.clear(); events.offer(() -> listener.disconnected(reason)); }
    }
    @Override public synchronized void close() {
        closed = true; connected = false;
        closeSocket(socket);
        try { if (server != null) server.close(); } catch (IOException ignored) { }
        if (discovery != null) discovery.close();
        outgoing.clear();
    }
    private static void closeSocket(Socket socket) { try { if (socket != null) socket.close(); } catch (IOException ignored) { } }
    static DataInputStream stream(byte[] bytes) { return new DataInputStream(new ByteArrayInputStream(bytes)); }
    public interface Write { void write(DataOutputStream data) throws IOException; }
    public static byte[] packet(Write write) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        write.write(new DataOutputStream(bytes));
        if (bytes.size() > MAX_PACKET) throw new IOException("packet_size");
        return bytes.toByteArray();
    }
    private static void writeFrame(DataOutputStream output, byte[] data) throws IOException {
        output.writeInt(data.length); output.write(data); output.flush();
    }
    private static byte[] readFrame(DataInputStream input, int maximum) throws IOException {
        int count = input.readInt();
        if (count < 1 || count > maximum) throw new IOException("frame_size");
        byte[] data = new byte[count]; input.readFully(data); return data;
    }
}
