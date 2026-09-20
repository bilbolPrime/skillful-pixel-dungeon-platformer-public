package com.bilboldev.skillfulpixeldungeonplatformer;

import com.badlogic.gdx.Game;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmbientMusicHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AssetHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.AchievementService;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.PlatformProfile;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.WindowModeService;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.FreeVersionAboutWindow;

public class SkillfulPixelDungeonPlatformer extends Game {
	private static SkillfulPixelDungeonPlatformer instance;
	private static PlatformProfile platformProfile = PlatformProfile.android();
	private final AchievementService achievementService;
	private final com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudStorage cloudStorage;

	public SkillfulPixelDungeonPlatformer() {
		this(PlatformProfile.android(), null);
	}

	public SkillfulPixelDungeonPlatformer(PlatformProfile platformProfile) {
		this(platformProfile, null);
	}

	public SkillfulPixelDungeonPlatformer(PlatformProfile platformProfile, AchievementService achievementService) {
		this(platformProfile, achievementService, null);
	}

	public SkillfulPixelDungeonPlatformer(PlatformProfile platformProfile, AchievementService achievementService,
			com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudStorage cloudStorage) {
		SkillfulPixelDungeonPlatformer.platformProfile = platformProfile;
		this.achievementService = achievementService;
		this.cloudStorage = cloudStorage;
	}

	@Override
	public void create () {
		instance = this;
		AchievementManager.getInstance().setService(achievementService);
		if (shouldAutoUnlockDesktopSupporter()) {
			AchievementManager.getInstance().applySupporterOwnership(false);
		}
		AssetHelper.getInstance().loadAll();
		FontHelper.reset();
		MapHelper.getInstance().reloadVisualAssets();
		UIHelper.getInstance().reloadVisualAssets();
		Messages.setup(GameSettingsHelper.getInstance().getLanguage());
		com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.initialize(cloudStorage);
		PhysicsHelper.getInstance();
		setScreen(new TitleScreen());
		if (shouldAutoShowFreeVersionAbout()) {
			WindowHelper.getInstance().addWindow(new FreeVersionAboutWindow().build());
		}
	}

	private boolean shouldAutoUnlockDesktopSupporter() {
		return platformProfile.keyboardControlsEnabled()
				&& !platformProfile.touchControlsEnabled()
				&& !platformProfile.isFreeDesktopBuild();
	}

	private boolean shouldAutoShowFreeVersionAbout() {
		return platformProfile.isFreeVersion();
	}

	@Override
	public void render () {
		if (achievementService != null) {
			achievementService.update();
		}
		com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.update();
		com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance()
				.update(com.badlogic.gdx.Gdx.graphics.getDeltaTime());
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		WindowModeService windowModeService = platformProfile.windowModeService();
		if (windowModeService.isSupported()) {
			windowModeService.rememberWindowSize(width, height);
		}

		super.resize(width, height);
	}

	@Override
	public void dispose () {
		com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.shutdown();
		if (achievementService != null) {
			achievementService.onDispose();
		}
		super.dispose();
		AmbientMusicHelper.reset();
		SoundHelper.reset();
		FontHelper.reset();
		PhysicsHelper.reset();
		TextureHelper.GetSingleton().dispose();
		AssetHelper.getInstance().dispose();
	}

	public static void transition(BaseScreen newScreen){
		transition(newScreen, false);
	}

	public static void transition(BaseScreen newScreen, boolean disposeCurrent){
		if (instance == null) {
			return;
		}

		BaseScreen currentScreen = getActiveScreen();
		instance.setScreen(newScreen);
		if (disposeCurrent && currentScreen != null && currentScreen != newScreen) {
			currentScreen.dispose();
		}
	}

	public static PlatformProfile getPlatformProfile() {
		return platformProfile;
	}

	public static boolean isFreeDesktopBuild() {
		return platformProfile != null && platformProfile.isFreeDesktopBuild();
	}

	public static AchievementService getAchievementService() {
		return instance == null ? null : instance.achievementService;
	}

	public static BaseScreen getActiveScreen(){
		return instance == null || !(instance.getScreen() instanceof BaseScreen)
				? null
				: (BaseScreen) instance.getScreen();
	}
}

