package com.bilboldev.skillfulpixeldungeonplatformer.platform;

import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;

public interface AchievementService {
    enum ConnectionState {
        UNAVAILABLE,
        CONNECTING,
        DISCONNECTED,
        CONNECTED
    }

    void unlock(Achievement achievement);

    default void synchronize(Iterable<Achievement> achievements) {
        if (achievements == null) {
            return;
        }

        for (Achievement achievement : achievements) {
            if (achievement != null) {
                unlock(achievement);
            }
        }
    }

    /** Called once per render frame so platform SDKs can process callbacks. */
    default void update() {}

    default ConnectionState getConnectionState() {
        return ConnectionState.UNAVAILABLE;
    }

    default void connect() {}

    /** Called when the application is about to be destroyed. */
    default void onDispose() {}
}
