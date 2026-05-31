package com.bilboldev.skillfulpixeldungeonplatformer;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.PlatformProfile;
import com.bilboldev.skillfulpixeldungeonplatformer.screens.BaseScreen;
import com.google.android.gms.games.PlayGamesSdk;

public class AndroidLauncher extends AndroidApplication {
	private static final String TAG = "AndroidLauncher";
	private static final String GPGS_APP_ID_RESOURCE = "gpgs_app_id";
	private static final String RESOURCE_TYPE = "string";

	private AndroidBillingStoreService storeService;
	private AndroidAchievementService achievementService;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isConfiguredResource(GPGS_APP_ID_RESOURCE)) {
			achievementService = createAchievementServiceSafely();
		} else {
			Log.i(TAG, "Google Play Games is not configured yet; continuing without Android achievements.");
		}

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		storeService = createStoreServiceSafely();
		initialize(new SkillfulPixelDungeonPlatformer(
				PlatformProfile.android(),
				achievementService,
				storeService), config);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideVirtualButtons();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshAchievementAuthentication();
		refreshForegroundLayout();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			refreshAchievementAuthentication();
			refreshForegroundLayout();
		}
	}

	@Override
	protected void onDestroy() {
		if (storeService != null) {
			storeService.onDispose();
		}
		super.onDestroy();
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

	private void refreshAchievementAuthentication() {
		if (achievementService != null) {
			achievementService.refreshAuthentication();
		}
	}

	private boolean isConfiguredResource(String resourceName) {
		int resId = getResources().getIdentifier(resourceName, RESOURCE_TYPE, getPackageName());
		if (resId == 0) {
			return false;
		}

		String value = getString(resId);
		return value != null && !value.startsWith("REPLACE_WITH_") && !value.trim().isEmpty();
	}

	private AndroidAchievementService createAchievementServiceSafely() {
		try {
			PlayGamesSdk.initialize(this);
			return new AndroidAchievementService(this);
		} catch (Throwable throwable) {
			Log.e(TAG, "Google Play Games initialization failed; continuing without Android achievements.", throwable);
			return null;
		}
	}

	private AndroidBillingStoreService createStoreServiceSafely() {
		try {
			return new AndroidBillingStoreService(this);
		} catch (Throwable throwable) {
			Log.e(TAG, "Google Play Billing initialization failed; continuing without store support.", throwable);
			return null;
		}
	}
}

