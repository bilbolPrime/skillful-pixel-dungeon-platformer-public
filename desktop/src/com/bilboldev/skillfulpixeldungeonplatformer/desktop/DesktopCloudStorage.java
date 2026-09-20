package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudFiles;
import com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudStorage;
import com.codedisaster.steamworks.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;


public final class DesktopCloudStorage implements CloudStorage {
    private final SteamRemoteStorage storage;
    private final String account;

    public DesktopCloudStorage() {
        SteamUser user = new SteamUser(new SteamUserCallback() { });
        try { account = Integer.toUnsignedString(user.getSteamID().getAccountID()); }
        finally { user.dispose(); }
        storage = new SteamRemoteStorage(new SteamRemoteStorageCallback() { });
    }
    @Override public String accountId() { return account; }
    @Override public boolean enabled() { return storage.isCloudEnabledForAccount() && storage.isCloudEnabledForApp(); }
    @Override public Map<String, String> files() throws IOException {
        Map<String, String> result = new TreeMap<>();
        int count = storage.getFileCount();
        if (count < 0 || count > 10000) throw new IOException("Invalid cloud file count");
        int[] size = new int[1];
        for (int i = 0; i < count; i++) {
            String name = storage.getFileNameAndSize(i, size);
            if (name == null || name.isEmpty()) throw new IOException("Cloud file enumeration changed");
            result.put(name, storage.getFileTimestamp(name) + ":" + size[0]);
        }
        return result;
    }
    @Override public byte[] read(String name) throws IOException {
        int size = storage.getFileSize(name);
        if (size <= 0 || size > CloudFiles.MAX_BYTES) throw new IOException("Invalid cloud file size: " + name);
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        try {
            if (storage.fileRead(name, buffer) != size) throw new IOException("Incomplete cloud read: " + name);
            byte[] bytes = new byte[size]; buffer.position(0); buffer.get(bytes); return bytes;
        } catch (SteamException e) { throw new IOException("Steam read failed", e); }
    }
    @Override public void write(String name, byte[] bytes) throws IOException {
        long[] total = new long[1], available = new long[1];
        if (!storage.getQuota(total, available)) throw new IOException("Steam quota unavailable");
        if (bytes.length - storage.getFileSize(name) > available[0]) throw new IOException("Steam Cloud quota exceeded");
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length); buffer.put(bytes); buffer.flip();
        try {
            if (!storage.fileWrite(name, buffer)) throw new IOException("Steam write failed: " + name);
            storage.setSyncPlatforms(name, SteamRemoteStorage.RemoteStoragePlatform.All);
        } catch (SteamException e) { throw new IOException("Steam write failed", e); }
    }
    @Override public void delete(String name) throws IOException {
        if (storage.fileExists(name) && !storage.fileDelete(name)) throw new IOException("Steam delete failed: " + name);
    }
    @Override public void dispose() { storage.dispose(); }
}
