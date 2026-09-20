package com.bilboldev.skillfulpixeldungeonplatformer;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.PlatformProfile;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		initialize(new SkillfulPixelDungeonPlatformer(PlatformProfile.android()), config);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideVirtualButtons();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshForegroundLayout();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			refreshForegroundLayout();
		}
	}

	@TargetApi(19)
	private void hideVirtualButtons() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	private void refreshForegroundLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideVirtualButtons();
		}

		if (Gdx.app == null || Gdx.graphics == null) {
			return;
		}

		Gdx.app.postRunnable(() -> {
			BaseScreen activeScreen = SkillfulPixelDungeonPlatformer.getActiveScreen();
			if (activeScreen == null) {
				return;
			}

			FontHelper.reset();
			UIHelper.getInstance().reloadVisualAssets();

			int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();
			if (width > 0 && height > 0) {
				activeScreen.resize(width, height);
			}
		});
	}

}

