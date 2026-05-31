package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.Achievement;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.caves.Caves;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.city.City;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.halls.Halls;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.prison.Prison;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers.Sewers;

public class RatKingSupportHelper {
    private static final String PREFS_NAME = "rat-king-support";
    private static final String KEY_MOBILE_DONATION_UNLOCKED = "mobile_donation_unlocked";
    private static final String KEY_COMPANION_ENABLED = "companion_enabled";
    private static final String KEY_TITLE_THEME = "title_theme";
    private static final String KEY_HIGHEST_DEPTH_REACHED = "highest_depth_reached";
    private static final String KEY_FIREWORKS_ENABLED = "fireworks_enabled";

    private static final RatKingSupportHelper INSTANCE = new RatKingSupportHelper();

    public enum TitleThemeOption {
        SEWERS("Sewers", 1) {
            @Override
            public Theme createTheme() {
                return new Sewers();
            }
        },
        PRISON("Prison", 6) {
            @Override
            public Theme createTheme() {
                return new Prison();
            }
        },
        CAVES("Caves", 11) {
            @Override
            public Theme createTheme() {
                return new Caves();
            }
        },
        CITY("City", 16) {
            @Override
            public Theme createTheme() {
                return new City();
            }
        },
        HALL("Halls", 21) {
            @Override
            public Theme createTheme() {
                return new Halls();
            }
        };

        private final String displayName;
        private final int unlockDepth;

        TitleThemeOption(String displayName, int unlockDepth) {
            this.displayName = displayName;
            this.unlockDepth = unlockDepth;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getUnlockDepth() {
            return unlockDepth;
        }

        public String getUnlockLabel() {
            return Messages.maybeTranslate("REACH %s", Messages.maybeTranslate(displayName));
        }

        public abstract Theme createTheme();
    }

    public static RatKingSupportHelper getInstance() {
        return INSTANCE;
    }

    private Preferences prefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public boolean isRatKingAvailable() {
        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
            return isMobileDonationUnlocked();
        }

        return SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled()
                && !SkillfulPixelDungeonPlatformer.isFreeDesktopBuild();
    }

    public boolean shouldRequireMobileDonation() {
        return SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled() && !isMobileDonationUnlocked();
    }

    public boolean isMobileDonationUnlocked() {
        return prefs().getBoolean(KEY_MOBILE_DONATION_UNLOCKED, false);
    }

    public void setMobileDonationUnlocked(boolean unlocked) {
        Preferences prefs = prefs();
        prefs.putBoolean(KEY_MOBILE_DONATION_UNLOCKED, unlocked);
        prefs.flush();
    }

    public boolean isCompanionEnabled() {
        return prefs().getBoolean(KEY_COMPANION_ENABLED, true);
    }

    public void setCompanionEnabled(boolean enabled) {
        Preferences prefs = prefs();
        prefs.putBoolean(KEY_COMPANION_ENABLED, enabled);
        prefs.flush();
    }

    public int getHighestDepthReached() {
        return Math.max(1, prefs().getInteger(KEY_HIGHEST_DEPTH_REACHED, 1));
    }

    public void recordReachedDepth(int depth) {
        if (depth <= getHighestDepthReached()) {
            return;
        }

        Preferences prefs = prefs();
        prefs.putInteger(KEY_HIGHEST_DEPTH_REACHED, depth);
        prefs.flush();
    }

    public boolean isFireworksUnlocked() {
        return AchievementManager.getInstance().isUnlocked(Achievement.VICTORY);
    }

    public boolean isFireworksEnabled() {
        return isFireworksUnlocked() && prefs().getBoolean(KEY_FIREWORKS_ENABLED, false);
    }

    public void setFireworksEnabled(boolean enabled) {
        Preferences prefs = prefs();
        prefs.putBoolean(KEY_FIREWORKS_ENABLED, isFireworksUnlocked() && enabled);
        prefs.flush();
    }

    public boolean isThemeUnlocked(TitleThemeOption option) {
        return option != null && getHighestDepthReached() >= option.getUnlockDepth();
    }

    public TitleThemeOption getSelectedTitleTheme() {
        String rawValue = prefs().getString(KEY_TITLE_THEME, TitleThemeOption.SEWERS.name());
        TitleThemeOption option;
        try {
            option = TitleThemeOption.valueOf(rawValue);
        }
        catch (IllegalArgumentException ignored) {
            option = TitleThemeOption.SEWERS;
        }

        return isThemeUnlocked(option) ? option : TitleThemeOption.SEWERS;
    }

    public void setSelectedTitleTheme(TitleThemeOption option) {
        TitleThemeOption safeOption = option == null ? TitleThemeOption.SEWERS : option;
        if (!isThemeUnlocked(safeOption)) {
            safeOption = TitleThemeOption.SEWERS;
        }

        Preferences prefs = prefs();
        prefs.putString(KEY_TITLE_THEME, safeOption.name());
        prefs.flush();
    }

    public void applyConfiguredTitleTheme() {
        IntroHelper.getInstance().generateMap(getSelectedTitleTheme().createTheme());
    }
}