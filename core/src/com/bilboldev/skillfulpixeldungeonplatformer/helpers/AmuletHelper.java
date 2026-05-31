package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.AmuletOfYendor;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.SurfaceEndingScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;

public final class AmuletHelper {

    private AmuletHelper() {
    }

    public static boolean hasAmulet() {
        return InventoryHelper.getInstance().hasItem(AmuletOfYendor.class);
    }

    public static boolean takeToSurface(ItemOnScreen itemOnScreen) {
        if (itemOnScreen == null || !(itemOnScreen.getItem() instanceof AmuletOfYendor)) {
            return false;
        }

        if (!InventoryHelper.getInstance().addItem(itemOnScreen.getItem())) {
            return false;
        }

        AchievementManager.getInstance().onVictory();
        removeFromFloor(itemOnScreen);
        return true;
    }

    public static void takeToSurface() {
        AchievementManager.getInstance().onVictory();
    }

    public static void claimVictory() {
        claimVictory(null);
    }

    public static void claimVictory(ItemOnScreen itemOnScreen) {
        AchievementManager.getInstance().onVictory();
        removeFromFloor(itemOnScreen);
        finishRun();
    }

    public static void reachSurface() {
        AchievementManager.getInstance().onVictory();
        AchievementManager.getInstance().onHappyEnd();
        NightModeHelper.captureSurfaceEndingState();
        finishRun(new SurfaceEndingScreen());
    }

    private static void finishRun() {
        finishRun(new TitleScreen());
    }

    private static void finishRun(BaseScreen nextScreen) {
        WindowHelper.getInstance().hideAll();

        if (UnitHelper.getInstance().getHero() != null) {
            DifficultyHelper.getInstance().recordVictory(UnitHelper.getInstance().getHero().getHeroClass());
            SaveHelper.getInstance().deleteSave(UnitHelper.getInstance().getHero().getHeroClass());
        }

        RatKingSupportHelper.getInstance().setFireworksEnabled(true);

        SkillfulPixelDungeonPlatformer.transition(nextScreen == null ? new TitleScreen() : nextScreen, true);
    }

    private static void removeFromFloor(ItemOnScreen itemOnScreen) {
        if (itemOnScreen == null) {
            return;
        }

        UnitHelper.getInstance().removeUnit(itemOnScreen);

        if (UnitHelper.getInstance().getHero() == null) {
            return;
        }

        MapHelper.getInstance().refreshHeroEnvironment();
    }
}