package com.bilboldev.skillfulpixeldungeonplatformer.desktop;

import com.badlogic.gdx.Gdx;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.AchievementService;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle;
import com.codedisaster.steamworks.SteamLeaderboardHandle;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Properties;

/**
 * Desktop (Steam) achievement service powered by SteamWorks4J.
 *
 * <p>Steam achievement API names must match the IDs registered in the Steamworks partner
 * dashboard, which by convention are the same strings returned by {@link Achievement#getId()}
 * (e.g. {@code MONSTERS_SLAIN_1}).
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>{@link #init()} is called once before the LibGDX application starts.
 *   <li>{@link #update()} is called every frame so Steam callbacks are processed.
 *   <li>{@link #onDispose()} is called when the application exits.
 * </ol>
 */
public class DesktopAchievementService implements AchievementService {

    private static final String TAG = "DesktopAchievementService";
    private static final String ACHIEVEMENT_ID_PREFIX = "SPD_BADGE_";
    private static final String ACHIEVEMENT_RESOURCE = "steam-achievements.properties";

    private final EnumMap<Achievement, String> achievementIds = new EnumMap<>(Achievement.class);
    private final EnumSet<Achievement> pendingUnlocks = EnumSet.noneOf(Achievement.class);
    private final EnumSet<Achievement> pendingRemoteSyncs = EnumSet.noneOf(Achievement.class);
    private SteamUserStats userStats;
    private boolean initialized;

    public DesktopAchievementService() {
        loadAchievementIds();
    }

    /**
     * Initialises the Steam API and requests the current user's stats/achievements.
     * Must be called before the LibGDX {@link com.badlogic.gdx.ApplicationListener} is created.
     *
     * @return {@code true} if the Steam API was initialised successfully.
     */
    public boolean init() {
        try {
            if (!SteamAPI.init()) {
                System.err.println("[" + TAG + "] Steam API init failed – is Steam running?");
                return false;
            }
            userStats = new SteamUserStats(new SteamUserStatsCallback() {
                @Override
                public void onUserStatsReceived(long gameId, SteamID steamID, SteamResult result) {
                    if (result == SteamResult.OK) {
                        queueRemoteAchievementSync();
                        applyPendingRemoteSync();
                        flushPendingUnlocks();
                        System.out.println("[" + TAG + "] User stats received.");
                    } else {
                        System.err.println("[" + TAG + "] Failed to receive user stats: " + result);
                    }
                }

                @Override
                public void onUserStatsStored(long gameId, SteamResult result) {
                    if (result != SteamResult.OK) {
                        System.err.println("[" + TAG + "] Failed to store user stats: " + result);
                    }
                }

                @Override
                public void onUserStatsUnloaded(SteamID steamIDUser) {}

                @Override
                public void onUserAchievementStored(long gameId, boolean groupAchievement,
                        String achievementName, int curProgress, int maxProgress) {
                    System.out.println("[" + TAG + "] Achievement stored: " + achievementName);
                }

                @Override
                public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard,
                        boolean found) {}

                @Override
                public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard,
                        SteamLeaderboardEntriesHandle entries, int numEntries) {}

                @Override
                public void onLeaderboardScoreUploaded(boolean success,
                        SteamLeaderboardHandle leaderboard, int score, boolean scoreChanged,
                        int globalRankNew, int globalRankPrevious) {}

                @Override
                public void onNumberOfCurrentPlayersReceived(boolean success, int players) {}

                @Override
                public void onGlobalStatsReceived(long gameId, SteamResult result) {}
            });

            initialized = true;
            flushPendingUnlocks();
            System.out.println("[" + TAG + "] Steam API initialised successfully.");
            return true;
        } catch (SteamException e) {
            System.err.println("[" + TAG + "] SteamException during init: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void unlock(Achievement achievement) {
        if (queueAchievement(achievement)) {
            flushPendingUnlocks();
        }
    }

    @Override
    public void synchronize(Iterable<Achievement> achievements) {
        if (achievements == null) {
            return;
        }

        for (Achievement achievement : achievements) {
            queueAchievement(achievement);
        }

        flushPendingUnlocks();
    }

    private void loadAchievementIds() {
        Properties properties = new Properties();
        try (InputStream input = DesktopAchievementService.class.getClassLoader().getResourceAsStream(ACHIEVEMENT_RESOURCE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            System.err.println("[" + TAG + "] Failed to load Steam achievement mappings: " + exception.getMessage());
        }

        for (Achievement achievement : Achievement.values()) {
            String configuredId = properties.getProperty(achievement.getId());
            achievementIds.put(achievement, configuredId == null || configuredId.trim().isEmpty()
                    ? ACHIEVEMENT_ID_PREFIX + achievement.getId()
                    : configuredId.trim());
        }
    }

    private boolean queueAchievement(Achievement achievement) {
        if (achievement == null) {
            return false;
        }

        String achievementId = achievementIds.get(achievement);
        if (achievementId == null || achievementId.isEmpty()) {
            return false;
        }

        pendingUnlocks.add(achievement);
        return true;
    }

    private void queueRemoteAchievementSync() {
        if (userStats == null) {
            return;
        }

        for (Achievement achievement : Achievement.values()) {
            String achievementId = achievementIds.get(achievement);
            if (achievementId == null || achievementId.isEmpty()) {
                continue;
            }

            if (userStats.isAchieved(achievementId, false)) {
                pendingRemoteSyncs.add(achievement);
            }
        }
    }

    private void applyPendingRemoteSync() {
        if (pendingRemoteSyncs.isEmpty() || Gdx.app == null) {
            return;
        }

        final EnumSet<Achievement> syncedAchievements = EnumSet.copyOf(pendingRemoteSyncs);
        pendingRemoteSyncs.clear();
        Gdx.app.postRunnable(() -> AchievementManager.getInstance().applyPlatformSync(syncedAchievements));
    }

    private void flushPendingUnlocks() {
        if (!initialized || userStats == null || pendingUnlocks.isEmpty()) {
            return;
        }

        EnumSet<Achievement> completedUnlocks = EnumSet.noneOf(Achievement.class);
        EnumSet<Achievement> invalidUnlocks = EnumSet.noneOf(Achievement.class);
        boolean storeNeeded = false;
        Iterator<Achievement> iterator = pendingUnlocks.iterator();
        while (iterator.hasNext()) {
            Achievement achievement = iterator.next();
            String achievementId = achievementIds.get(achievement);

            if (achievementId == null || achievementId.isEmpty()) {
                invalidUnlocks.add(achievement);
                continue;
            }

            if (userStats.setAchievement(achievementId)) {
                storeNeeded = true;
                completedUnlocks.add(achievement);
            } else {
                System.err.println("[" + TAG + "] setAchievement returned false for: " + achievementId);
            }
        }

        pendingUnlocks.removeAll(invalidUnlocks);

        if (!storeNeeded) {
            return;
        }

        if (userStats.storeStats()) {
            pendingUnlocks.removeAll(completedUnlocks);
        } else {
            System.err.println("[" + TAG + "] storeStats returned false; pending achievements will retry.");
        }
    }

    /** Pumps the Steam callback queue. Must be called every frame. */
    @Override
    public void update() {
        if (initialized) {
            SteamAPI.runCallbacks();
            applyPendingRemoteSync();
            flushPendingUnlocks();
        }
    }

    /** Releases Steam resources. Must be called before the JVM exits. */
    @Override
    public void onDispose() {
        if (initialized) {
            if (userStats != null) {
                userStats.dispose();
                userStats = null;
            }
            SteamAPI.shutdown();
            initialized = false;
            System.out.println("[" + TAG + "] Steam API shut down.");
        }
    }
}
