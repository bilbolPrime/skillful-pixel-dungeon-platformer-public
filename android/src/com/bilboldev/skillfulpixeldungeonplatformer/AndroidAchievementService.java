package com.bilboldev.skillfulpixeldungeonplatformer;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.AchievementService;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.PlayGames;

/**
 * Android (Google Play Games Services v2) achievement service.
 *
 * <p>Achievement IDs are looked up at runtime from {@code res/values/game-ids.xml} using the
 * convention {@code achievement_<Achievement.getId().toLowerCase()>}. Replace every
 * {@code REPLACE_WITH_GPGS_ACHIEVEMENT_ID} placeholder in that file with the real ID
 * from the Google Play Console.
 *
 * <p>Sign-in is handled automatically by the Play Games SDK v2; no explicit sign-in call is
 * required after {@link com.google.android.gms.games.PlayGamesSdk#initialize(Activity)} is
 * invoked in {@link AndroidLauncher#onCreate}.
 */
public class AndroidAchievementService implements AchievementService {

    private static final String TAG = "Achievements";
    private static final String RESOURCE_TYPE = "string";
    private static final long AUTH_RETRY_MS = 5_000L;
    private static final long REMOTE_SYNC_RETRY_MS = 10_000L;
    private static final long SIGN_IN_TIMEOUT_MS = 15_000L;
    private static final String DEFAULT_WELCOME_PREFIX = "Welcome ";

    private final Activity activity;
    private final Map<String, Achievement> localAchievementsByGpgsId;
    private final Set<Achievement> pendingAchievementUnlocks;
    private boolean authenticated;
    private boolean authenticationResolved;
    private boolean authenticationCheckInFlight;
    private boolean signInInFlight;
    private boolean remoteSyncCompleted;
    private boolean remoteSyncInFlight;
    private boolean welcomeShown;
    private String pendingWelcomeDisplayName;
    private long signInStartedAtMs;
    private long nextAuthenticationCheckAtMs;
    private long nextRemoteSyncAttemptAtMs;

    public AndroidAchievementService(Activity activity) {
        this.activity = activity;
        this.localAchievementsByGpgsId = buildAchievementLookup();
        this.pendingAchievementUnlocks = new LinkedHashSet<>();
    }

    @Override
    public void unlock(Achievement achievement) {
        if (achievement == null) {
            return;
        }

        if (!authenticated) {
            pendingAchievementUnlocks.add(achievement);
            requestAuthenticationCheck();
            return;
        }

        unlockAuthenticatedAchievement(achievement);
    }

    @Override
    public void synchronize(Iterable<Achievement> achievements) {
        if (achievements == null) {
            return;
        }

        for (Achievement achievement : achievements) {
            if (achievement != null) {
                if (!authenticated) {
                    pendingAchievementUnlocks.add(achievement);
                }
                else {
                    unlockAuthenticatedAchievement(achievement);
                }
            }
        }

        if (!authenticated) {
            requestAuthenticationCheck();
        }
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();

        if (signInInFlight && signInStartedAtMs > 0L && now - signInStartedAtMs >= SIGN_IN_TIMEOUT_MS) {
            signInInFlight = false;
            signInStartedAtMs = 0L;
            authenticated = false;
            authenticationResolved = true;
            nextAuthenticationCheckAtMs = now + AUTH_RETRY_MS;
            Log.w(TAG, "Manual Play Games sign-in timed out without a completion callback.");
        }

        if (!authenticated) {
            if (!authenticationCheckInFlight && now >= nextAuthenticationCheckAtMs) {
                checkAuthentication();
            }
            return;
        }

        showPendingWelcomeMessageIfReady();

        if (!pendingAchievementUnlocks.isEmpty()) {
            flushPendingAchievementUnlocks();
        }

        if (!remoteSyncCompleted && !remoteSyncInFlight && now >= nextRemoteSyncAttemptAtMs) {
            loadRemoteAchievements();
        }
    }

    @Override
    public ConnectionState getConnectionState() {
        if (authenticated) {
            return ConnectionState.CONNECTED;
        }

        if (signInInFlight || !authenticationResolved) {
            return ConnectionState.CONNECTING;
        }

        return ConnectionState.DISCONNECTED;
    }

    @Override
    public void connect() {
        if (authenticated || signInInFlight) {
            return;
        }

        Log.d(TAG, "Starting manual Play Games sign-in.");
        signInInFlight = true;
        signInStartedAtMs = System.currentTimeMillis();
        PlayGames.getGamesSignInClient(activity)
                .signIn()
                .addOnSuccessListener(this::handleAuthenticationResult)
                .addOnFailureListener(error -> {
                    signInInFlight = false;
                    signInStartedAtMs = 0L;
                    authenticated = false;
                    authenticationResolved = true;
                    nextAuthenticationCheckAtMs = System.currentTimeMillis() + AUTH_RETRY_MS;
                    Log.w(TAG, "Manual Play Games sign-in failed.", error);
                });
    }

    public void refreshAuthentication() {
        if (authenticationCheckInFlight) {
            return;
        }

        if (authenticated) {
            showPendingWelcomeMessageIfReady();
            return;
        }

        nextAuthenticationCheckAtMs = 0L;
        checkAuthentication();
    }

    /**
     * Resolves the Google Play Games achievement ID for the given {@link Achievement} by
     * looking up the resource string named {@code achievement_<id_lowercase>}.
     *
     * @return the GPGS achievement ID string, or {@code null} if the resource is missing or
     *         still contains the placeholder value.
     */
    private String resolveGpgsId(Achievement achievement) {
        String resourceName = "achievement_" + achievement.getId().toLowerCase(Locale.ROOT);
        Resources resources = activity.getResources();
        int resId = resources.getIdentifier(resourceName, RESOURCE_TYPE, activity.getPackageName());
        if (resId == 0) {
            Log.e(TAG, "Missing resource: " + resourceName);
            return null;
        }
        String id = resources.getString(resId);
        if (id.startsWith("REPLACE_WITH_")) {
            Log.w(TAG, "GPGS ID not configured for: " + resourceName);
            return null;
        }
        return id;
    }

    private Map<String, Achievement> buildAchievementLookup() {
        Map<String, Achievement> achievementsById = new HashMap<>();
        for (Achievement achievement : Achievement.values()) {
            String gpgsId = resolveGpgsId(achievement);
            if (gpgsId != null) {
                achievementsById.put(gpgsId, achievement);
            }
        }
        return achievementsById;
    }

    private void requestAuthenticationCheck() {
        if (authenticationCheckInFlight) {
            return;
        }

        if (System.currentTimeMillis() >= nextAuthenticationCheckAtMs) {
            checkAuthentication();
        }
    }

    private void checkAuthentication() {
        authenticationCheckInFlight = true;

        PlayGames.getGamesSignInClient(activity)
                .isAuthenticated()
                .addOnSuccessListener(this::handleAuthenticationResult)
                .addOnFailureListener(error -> {
                    authenticationCheckInFlight = false;
                    authenticated = false;
                    authenticationResolved = true;
                    nextAuthenticationCheckAtMs = System.currentTimeMillis() + AUTH_RETRY_MS;
                    Log.w(TAG, "Failed to query Play Games authentication state.", error);
                });
    }

    private void handleAuthenticationResult(com.google.android.gms.games.AuthenticationResult authenticationResult) {
        authenticationCheckInFlight = false;
        signInInFlight = false;
        signInStartedAtMs = 0L;
        authenticationResolved = true;
        authenticated = authenticationResult != null && authenticationResult.isAuthenticated();
        Log.d(TAG, "Play Games authentication resolved. authenticated=" + authenticated);
        if (!authenticated) {
            nextAuthenticationCheckAtMs = System.currentTimeMillis() + AUTH_RETRY_MS;
            Log.d(TAG, "Play Games is not authenticated yet.");
            return;
        }

        nextAuthenticationCheckAtMs = 0L;
        queueWelcomeMessage();
        showPendingWelcomeMessageIfReady();
        flushPendingAchievementUnlocks();
        if (!remoteSyncCompleted && !remoteSyncInFlight) {
            loadRemoteAchievements();
        }
    }

    private void queueWelcomeMessage() {
        if (welcomeShown || pendingWelcomeDisplayName != null) {
            return;
        }

        PlayGames.getPlayersClient(activity)
                .getCurrentPlayer()
                .addOnSuccessListener(player -> {
                    if (welcomeShown || pendingWelcomeDisplayName != null || player == null || Gdx.app == null) {
                        return;
                    }

                    String displayName = player.getDisplayName();
                    if (displayName == null || displayName.trim().isEmpty()) {
                        return;
                    }

                    pendingWelcomeDisplayName = displayName.trim();
                    showPendingWelcomeMessageIfReady();
                })
                .addOnFailureListener(error -> Log.w(TAG, "Unable to load the current Play Games player.", error));
    }

    private void showPendingWelcomeMessageIfReady() {
        if (welcomeShown || pendingWelcomeDisplayName == null || Gdx.app == null) {
            return;
        }

        BaseScreen activeScreen = SkillfulPixelDungeonPlatformer.getActiveScreen();
        if (activeScreen == null || !activeScreen.isInitialized() || WindowHelper.getInstance().windowOpen()) {
            return;
        }

        String welcomeMessage = Messages.getOrNull("custom.platform.play_games.welcome", pendingWelcomeDisplayName);
        if (welcomeMessage == null) {
            welcomeMessage = DEFAULT_WELCOME_PREFIX + pendingWelcomeDisplayName;
        }

        WindowHelper.getInstance().addWindow(1100f, 180f, welcomeMessage);
        pendingWelcomeDisplayName = null;
        welcomeShown = true;
    }

    private void flushPendingAchievementUnlocks() {
        if (!authenticated || pendingAchievementUnlocks.isEmpty()) {
            return;
        }

        List<Achievement> queuedAchievements = new ArrayList<>(pendingAchievementUnlocks);
        pendingAchievementUnlocks.clear();
        for (Achievement achievement : queuedAchievements) {
            unlockAuthenticatedAchievement(achievement);
        }
    }

    private void unlockAuthenticatedAchievement(Achievement achievement) {
        String gpgsId = resolveGpgsId(achievement);
        if (gpgsId == null) {
            Log.w(TAG, "No GPGS ID for achievement: " + achievement.getId());
            return;
        }

        AchievementsClient client = PlayGames.getAchievementsClient(activity);
        client.unlock(gpgsId);
        Log.d(TAG, "Achievement unlocked: " + achievement.getName());
    }

    private void loadRemoteAchievements() {
        if (!authenticated) {
            return;
        }

        remoteSyncInFlight = true;

        AchievementsClient client = PlayGames.getAchievementsClient(activity);
        client.load(false)
                .addOnSuccessListener(annotatedData -> {
                    remoteSyncInFlight = false;
                    nextRemoteSyncAttemptAtMs = 0L;

                    List<Achievement> unlockedAchievements = new ArrayList<>();
                    com.google.android.gms.games.achievement.AchievementBuffer buffer = annotatedData.get();
                    try {
                        for (com.google.android.gms.games.achievement.Achievement remoteAchievement : buffer) {
                            if (remoteAchievement.getState() != com.google.android.gms.games.achievement.Achievement.STATE_UNLOCKED) {
                                continue;
                            }

                            Achievement localAchievement = localAchievementsByGpgsId.get(remoteAchievement.getAchievementId());
                            if (localAchievement != null) {
                                unlockedAchievements.add(localAchievement);
                            }
                        }
                    } finally {
                        buffer.release();
                    }

                    if (!unlockedAchievements.isEmpty() && Gdx.app != null) {
                        Gdx.app.postRunnable(() -> AchievementManager.getInstance().applyPlatformSync(unlockedAchievements));
                    }

                    remoteSyncCompleted = true;
                })
                .addOnFailureListener(error -> {
                    remoteSyncInFlight = false;
                    nextRemoteSyncAttemptAtMs = System.currentTimeMillis() + REMOTE_SYNC_RETRY_MS;
                    Log.w(TAG, "Failed to load Play Games achievements for local sync.", error);
                });
    }
}
