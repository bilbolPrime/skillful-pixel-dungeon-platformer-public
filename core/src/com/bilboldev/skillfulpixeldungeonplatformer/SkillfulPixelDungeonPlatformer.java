package com.bilboldev.skillfulpixeldungeonplatformer;

import com.bilboldev.skillfulpixeldungeonplatformer.party.PartySession;

import com.badlogic.gdx.Game;
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
import com.bilboldev.skillfulpixeldungeonplatformer.platform.PlatformProfile;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.WindowModeService;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.TitleScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.FreeVersionAboutWindow;

public class SkillfulPixelDungeonPlatformer extends Game {
	private static SkillfulPixelDungeonPlatformer instance;
	private static PlatformProfile platformProfile = PlatformProfile.android();

	public SkillfulPixelDungeonPlatformer() {
		this(PlatformProfile.android());
	}

	public SkillfulPixelDungeonPlatformer(PlatformProfile platformProfile) {
		SkillfulPixelDungeonPlatformer.platformProfile = platformProfile;
	}

	@Override
	public void create () {
		instance = this;
		AssetHelper.getInstance().loadAll();
		FontHelper.reset();
		MapHelper.getInstance().reloadVisualAssets();
		UIHelper.getInstance().reloadVisualAssets();
		Messages.setup(GameSettingsHelper.getInstance().getLanguage());
		PhysicsHelper.getInstance();
		setScreen(new TitleScreen());
		if (shouldAutoShowFreeVersionAbout()) {
			WindowHelper.getInstance().addWindow(new FreeVersionAboutWindow().build());
		}
	}

	private boolean shouldAutoShowFreeVersionAbout() {
		return platformProfile.isFreeVersion();
	}

	@Override
	public void render () {
        PartySession.poll();
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
        PartySession.reset();
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
        if (PartySession.running() && newScreen instanceof TitleScreen) { PartySession.leave(); return; }
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

	public static BaseScreen getActiveScreen(){
		return instance == null || !(instance.getScreen() instanceof BaseScreen)
				? null
				: (BaseScreen) instance.getScreen();
	}
}
