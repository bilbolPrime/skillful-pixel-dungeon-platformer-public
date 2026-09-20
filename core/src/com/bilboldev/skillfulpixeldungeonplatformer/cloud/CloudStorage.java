package com.bilboldev.skillfulpixeldungeonplatformer.cloud;

import java.io.IOException;
import java.util.Map;


public interface CloudStorage {
    String accountId();
    boolean enabled();
    Map<String, String> files() throws IOException;
    byte[] read(String name) throws IOException;
    void write(String name, byte[] data) throws IOException;
    void delete(String name) throws IOException;
    void dispose();
}
