package com.bilboldev.skillfulpixeldungeonplatformer.cloud;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public final class CloudFiles {
    public static final int MAX_BYTES = 32 * 1024 * 1024;
    private static final int MAGIC = 0x50445032, MAX_EXPANDED = 128 * 1024 * 1024;

    private CloudFiles() { }

    public static byte[] encode(Serializable value) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(compressed))) {
            out.writeObject(value);
        }
        byte[] payload = compressed.toByteArray();
        if (payload.length > MAX_BYTES - 40) throw new IOException("Cloud snapshot exceeds size limit");
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(result);
        out.writeInt(MAGIC); out.writeInt(payload.length); out.write(digest(payload)); out.write(payload);
        return result.toByteArray();
    }

    public static <T> T decode(byte[] bytes, Class<T> type) throws IOException {
        if (bytes == null || bytes.length < 40 || bytes.length > MAX_BYTES) throw new IOException("Invalid snapshot size");
        DataInputStream header = new DataInputStream(new ByteArrayInputStream(bytes));
        if (header.readInt() != MAGIC || header.readInt() != bytes.length - 40) throw new IOException("Invalid snapshot header");
        byte[] hash = new byte[32], payload = new byte[bytes.length - 40];
        header.readFully(hash); header.readFully(payload);
        if (!MessageDigest.isEqual(hash, digest(payload))) throw new IOException("Snapshot checksum mismatch");
        try (ObjectInputStream in = new ObjectInputStream(new LimitedInputStream(new GZIPInputStream(new ByteArrayInputStream(payload)))) {
            @Override protected Class<?> resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException {
                String name = descriptor.getName();

                String component = name.replaceFirst("^\\[+L?", "").replace(";", "");
                if (!(component.startsWith("java.lang.") || component.startsWith("java.util.")
                        || component.startsWith("com.bilboldev.skillfulpixeldungeonplatformer.helpers.")
                        || component.startsWith("com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudRunRepository$")
                        || component.length() == 1)) throw new InvalidClassException("Unexpected save type", name);
                return super.resolveClass(descriptor);
            }
            @Override protected Class<?> resolveProxyClass(String[] interfaces) throws IOException { throw new InvalidClassException("Proxy in save"); }
        }) {
            Object result = in.readObject();
            if (!type.isInstance(result)) throw new IOException("Unexpected snapshot kind");
            return type.cast(result);
        } catch (ClassNotFoundException | RuntimeException e) { throw new IOException("Unreadable snapshot", e); }
    }

    public static void write(File file, byte[] bytes) throws IOException {
        replace(file, bytes, true);
    }

    public static void restoreBackup(File file, byte[] bytes) throws IOException {
        replace(file, bytes, false);
    }

    private static void replace(File file, byte[] bytes, boolean keepPrevious) throws IOException {
        Files.createDirectories(file.toPath().getParent());
        Path temp = file.toPath().resolveSibling(file.getName() + ".tmp");
        try {
            try (FileOutputStream out = new FileOutputStream(temp.toFile())) {
                out.write(bytes); out.getFD().sync();
            }
            if (keepPrevious && file.isFile()) Files.copy(file.toPath(), file.toPath().resolveSibling(file.getName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            try { Files.move(temp, file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temp, file.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }

    public static byte[] read(File file) throws IOException {
        if (file.length() > MAX_BYTES) throw new IOException("Oversized cloud file");
        return Files.readAllBytes(file.toPath());
    }

    public static String hash(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (byte b : digest(data)) result.append(String.format(java.util.Locale.ROOT, "%02x", b & 255));
        return result.toString();
    }

    private static byte[] digest(byte[] bytes) {
        try { return MessageDigest.getInstance("SHA-256").digest(bytes); }
        catch (NoSuchAlgorithmException e) { throw new AssertionError(e); }
    }

    private static final class LimitedInputStream extends FilterInputStream {
        private int remaining = MAX_EXPANDED;
        LimitedInputStream(InputStream stream) { super(stream); }
        @Override public int read() throws IOException {
            if (remaining <= 0) throw new IOException("Expanded snapshot too large");
            int result = super.read(); if (result >= 0) remaining--; return result;
        }
        @Override public int read(byte[] bytes, int offset, int length) throws IOException {
            if (remaining <= 0) throw new IOException("Expanded snapshot too large");
            int result = in.read(bytes, offset, Math.min(length, remaining));
            if (result > 0) remaining -= result; return result;
        }
    }
}
