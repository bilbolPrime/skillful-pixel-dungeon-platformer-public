package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SoundHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.platform.AchievementService;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.DescriptionWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.FreeVersionAboutWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.RatKingWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TitleExitWindow;

import java.util.ArrayList;

public class TitleScreen extends MenuScreenBase {
    private static final float FIREWORK_BLAST_COOLDOWN_SECONDS = 0.45f;
    private static final String VERSION_LABEL = "1.0.13 (Build 13)";
    private static final String ABOUT_TEXT = "A realtime platform adaption of Skillful Pixel Dungeon by BilbolDev";
    private static final String DESKTOP_TITLE_PATH = "images/intro/pixel-dungeon.png";
    private static final String DESKTOP_TITLE_OVERLAY_PATH = "images/intro/pixel-dungeon-over.png";
    private static final String SUBTITLE_PATH = "images/intro/subtitle.png";
    private static final String MOBILE_TITLE_PATH = "mobile-tilte.png";
    private static final float DESKTOP_TITLE_WIDTH = 803f;
    private static final float DESKTOP_TITLE_HEIGHT = 518f;
    private static final float SUBTITLE_WIDTH = 660f;
    private static final float SUBTITLE_HEIGHT = 90f;
    private static final float MOBILE_TITLE_SCALE = 0.75f;
    private static final float MOBILE_TITLE_WIDTH = 1100f * MOBILE_TITLE_SCALE;
    private static final float MOBILE_TITLE_HEIGHT = 478f * MOBILE_TITLE_SCALE;
    private static final float DESKTOP_TITLE_Y = 600f;
    private static final float MOBILE_TITLE_CENTER_Y = 620f + (478f / 2f);
    private static final float SUBTITLE_Y = 610f;
    private static final float ABOUT_ICON_RAISE_RATIO = 0.15f;
    private static final float EXIT_BUTTON_SIZE = 100f;
    private static final float EXIT_BUTTON_MARGIN = 40f;
    private static final float GAME_SERVICES_FLASH_RADIANS_PER_MILLI = 0.003926991f;

    private static boolean introFadePending = true;
    private static final String[] FIREWORK_SPRITES = new String[]{
            "images/misc/yellow-dot.png",
            "images/misc/red.png",
            "images/misc/green.png"
    };

    private GameSprite title;
    private GameSprite titleExtra;
    private GameSprite subTitle;
    private GameSprite fireballBackground;
    private GameSprite fireballFront;
    private final ArrayList<FireEffect> leftBalls = new ArrayList<>();
    private final ArrayList<FireEffect> rightBalls = new ArrayList<>();
    private final ArrayList<FireworkEffect> fireworks = new ArrayList<>();
    private float titleOverlay;
    private boolean titleOverlayIncreasing = true;
    private boolean fireworksActive;
    private float fireworkBlastCooldown;

    {
        maxTop = 20;
        maxRight = 35;
        maxPlants = 1;
        decorationDensity = 0.4f;
        isBattle = false;
        showDamage = true;
        template = "kingdom";
    }

    @Override
    protected void createMenuContent() {
        RatKingSupportHelper.getInstance().applyConfiguredTitleTheme();

        if (isMobileTitleLayout()) {
            title = new GameSprite(MOBILE_TITLE_PATH, MOBILE_TITLE_WIDTH, MOBILE_TITLE_HEIGHT);
            title.setPosition(centerX(MOBILE_TITLE_WIDTH), MOBILE_TITLE_CENTER_Y - (MOBILE_TITLE_HEIGHT / 2f));
            titleExtra = null;
            subTitle = null;
        } else {
            title = new GameSprite(DESKTOP_TITLE_PATH, DESKTOP_TITLE_WIDTH, DESKTOP_TITLE_HEIGHT);
            title.setPosition(centerX(DESKTOP_TITLE_WIDTH), DESKTOP_TITLE_Y);

            titleExtra = new GameSprite(DESKTOP_TITLE_OVERLAY_PATH, DESKTOP_TITLE_WIDTH, DESKTOP_TITLE_HEIGHT, 0f);
            titleExtra.setPosition(centerX(DESKTOP_TITLE_WIDTH), DESKTOP_TITLE_Y);

            subTitle = new GameSprite(SUBTITLE_PATH, SUBTITLE_WIDTH, SUBTITLE_HEIGHT, 1f);
            subTitle.setPosition(centerX(SUBTITLE_WIDTH), SUBTITLE_Y);
        }

        fireballBackground = new GameSprite("images/intro/fireball-background.png", 150, 150, 0.7f);
        fireballFront = new GameSprite("images/intro/fireball-front.png", 150, 150, 1f);

        for (int i = 0; i < 10; i++) {
            leftBalls.add(new FireEffect(850, 850));
            rightBalls.add(new FireEffect(1500, 850));
        }

        syncFireworks();

        float buttonWidth = 200f;
        float buttonSpacing = 300f;
        float buttonY = 350f;
        int buttonCount = 7;
        float buttonStartX = (ConstantsHelper.SCREEN_WIDTH - (buttonWidth + buttonSpacing * (buttonCount - 1))) / 2f;

        buttons.add(new IconButton("PLAY", buttonStartX, buttonY, 200, 200, "images/intro/play.png", "images/intro/play.png") {
            @Override
            public void click() {
                SkillfulPixelDungeonPlatformer.transition(new CharacterSelectScreen(), true);
            }
        });

        buttons.add(new IconButton("LIBRARY", buttonStartX + buttonSpacing, buttonY, 200, 200, "images/intro/library.png", "images/intro/library.png") {
            @Override
            public void click() {
                SkillfulPixelDungeonPlatformer.transition(new LibraryScreen(), true);
            }
        });

        buttons.add(new IconButton("SETTINGS", buttonStartX + buttonSpacing * 2f, buttonY, 200, 200, "images/intro/settings.png", "images/intro/settings.png") {
            @Override
            public void click() {
                WindowHelper.getInstance().addWindow(new PauseMenuWindow(false).build());
            }
        });

        buttons.add(new IconButton("RANKINGS", buttonStartX + buttonSpacing * 3f, buttonY, 200, 200, "images/intro/rankings.png", "images/intro/rankings.png") {
            @Override
            public void click() {
                SkillfulPixelDungeonPlatformer.transition(new RankingsScreen(), true);
            }
        });

        buttons.add(new IconButton("BADGES", buttonStartX + buttonSpacing * 4f, buttonY, 200, 200, "images/intro/achievements.png", "images/intro/achievements.png") {
            @Override
            public void click() {
                SkillfulPixelDungeonPlatformer.transition(new AchievementsScreen(), true);
            }
        });

        buttons.add(new IconButton("RAT KING", buttonStartX + buttonSpacing * 5f, buttonY, 200, 200, "images/intro/ratking.png", "images/intro/ratking.png") {
            @Override
            public void click() {
                WindowHelper.getInstance().addWindow(new RatKingWindow().build());
            }
        });

        buttons.add(new IconButton("ABOUT", buttonStartX + buttonSpacing * (buttonCount - 1), buttonY, 200, 200, "images/intro/about.png", "images/intro/about.png") {
            @Override
            public void click() {
                showAboutWindow();
            }
        });

        if (shouldShowGameServicesButton()) {
            buttons.add(new GameServicesButton());
        }

        buttons.add(new ActionButton(
                ConstantsHelper.SCREEN_WIDTH - EXIT_BUTTON_MARGIN - EXIT_BUTTON_SIZE,
                ConstantsHelper.SCREEN_HEIGHT - EXIT_BUTTON_MARGIN - EXIT_BUTTON_SIZE,
                EXIT_BUTTON_SIZE,
                EXIT_BUTTON_SIZE,
                "images/menu/exit.png",
                "images/menu/exit.png") {
            {
                enableUiPressFeedback();
            }

            @Override
            public void click() {
                showExitWindow();
            }
        });
    }

    @Override
    protected boolean handleBackAction() {
        showExitWindow();
        return true;
    }

    @Override
    protected boolean shouldFadeInOnEnter() {
        boolean shouldFade = introFadePending;
        introFadePending = false;
        return shouldFade;
    }

    @Override
    protected void actMenu(float delta) {
        syncFireworks();
        fireworkBlastCooldown = Math.max(0f, fireworkBlastCooldown - delta);

        if (titleOverlayIncreasing) {
            titleOverlay = Math.min(1.5f, titleOverlay + delta);
            if (titleOverlay == 1.5f) {
                titleOverlayIncreasing = false;
            }
        } else {
            titleOverlay = Math.max(0f, titleOverlay - delta);
            if (titleOverlay == 0f) {
                titleOverlayIncreasing = true;
            }
        }

        if (titleExtra != null) {
            titleExtra.setAlpha(titleOverlay);
        }
        fireballBackground.rotate(-delta * 500f);
        fireballFront.rotate(-delta * 500f);

        for (FireEffect fireEffect : leftBalls) {
            fireEffect.act(delta);
        }
        for (FireEffect fireEffect : rightBalls) {
            fireEffect.act(delta);
        }

        for (FireworkEffect firework : fireworks) {
            firework.act(delta);
        }
    }

    @Override
    protected void drawMenu(Batch batch) {
        for (FireworkEffect firework : fireworks) {
            firework.draw(batch);
        }

        title.draw(batch);
        if (titleExtra != null) {
            titleExtra.draw(batch);
        }
        if (subTitle != null) {
            subTitle.draw(batch);
        }

        fireballBackground.setPosition(850, 900);
        fireballFront.setPosition(850, 900);
        fireballBackground.draw(batch);
        fireballFront.draw(batch);

        fireballBackground.setPosition(1500, 900);
        fireballFront.setPosition(1500, 900);
        fireballBackground.draw(batch);
        fireballFront.draw(batch);

        for (FireEffect fireEffect : leftBalls) {
            fireEffect.draw(batch);
        }
        for (FireEffect fireEffect : rightBalls) {
            fireEffect.draw(batch);
        }

        drawButtons(batch);
        FontHelper.getSingleton().writeWhite(batch, 3, 1050, 50, VERSION_LABEL);
    }

    private void syncFireworks() {
        boolean shouldShowFireworks = RatKingSupportHelper.getInstance().isFireworksEnabled();
        if (shouldShowFireworks == fireworksActive) {
            return;
        }

        fireworksActive = shouldShowFireworks;
        fireworks.clear();
        if (!fireworksActive) {
            return;
        }

        for (int i = 0; i < 6; i++) {
            fireworks.add(new FireworkEffect());
        }
    }

    private boolean shouldShowGameServicesButton() {
        return SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
                && SkillfulPixelDungeonPlatformer.getAchievementService() != null;
    }

    private boolean isMobileTitleLayout() {
        return SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
    }

    private float centerX(float width) {
        return (ConstantsHelper.SCREEN_WIDTH - width) / 2f;
    }

    private void showExitWindow() {
        WindowHelper.getInstance().addWindow(new TitleExitWindow().build());
    }

    private void showAboutWindow() {
        if (SkillfulPixelDungeonPlatformer.isFreeDesktopBuild()) {
            WindowHelper.getInstance().addWindow(new FreeVersionAboutWindow().build());
            return;
        }

        WindowHelper.getInstance().addWindow(new DescriptionWindow("images/intro/about.png", ABOUT_TEXT, 1500f, 360f)
                .setDescriptionSpriteYOffsetByRatio(ABOUT_ICON_RAISE_RATIO)
                .build());
    }

    private final class GameServicesButton extends ActionButton {
        private static final float BUTTON_X = EXIT_BUTTON_MARGIN;
        private static final float BUTTON_Y = ConstantsHelper.SCREEN_HEIGHT - EXIT_BUTTON_MARGIN - EXIT_BUTTON_SIZE;
        private static final float BUTTON_SIZE = EXIT_BUTTON_SIZE;
        private static final float STATUS_LABEL_SIZE = 3f;
        private static final float STATUS_LABEL_PADDING = 6f;
        private static final float PULSE_MIN_ALPHA = 0.2f;
        private static final float PULSE_MIN_BRIGHTNESS = 0.65f;
        private static final String DISCONNECTED_LABEL = "X";

        private final GlyphLayout disconnectedLabelLayout;

        private GameServicesButton() {
            super(
                BUTTON_X,
                BUTTON_Y,
                BUTTON_SIZE,
                BUTTON_SIZE,
                    "images/misc/game-services.png",
                    "images/misc/game-services.png");
            enableUiPressFeedback();

            disconnectedLabelLayout = FontHelper.getSingleton().measure(Color.RED, STATUS_LABEL_SIZE, DISCONNECTED_LABEL);
        }

        @Override
        public boolean canClick() {
            return super.canClick() && getConnectionState() == AchievementService.ConnectionState.DISCONNECTED;
        }

        @Override
        public void clicked() {
            AchievementService achievementService = SkillfulPixelDungeonPlatformer.getAchievementService();
            if (achievementService != null) {
                achievementService.connect();
            }
        }

        @Override
        public void draw(Batch batch) {
            AchievementService.ConnectionState connectionState = getConnectionState();
            Color previousColor = new Color(batch.getColor());
            float pulseAlpha = previousColor.a;
            float pulseBrightness = 1f;
            if (connectionState == AchievementService.ConnectionState.CONNECTING) {
                float pulse = (MathUtils.sin(TimeUtils.millis() * GAME_SERVICES_FLASH_RADIANS_PER_MILLI) + 1f) * 0.5f;
                pulseAlpha = PULSE_MIN_ALPHA + (1f - PULSE_MIN_ALPHA) * pulse;
                pulseBrightness = PULSE_MIN_BRIGHTNESS + (1f - PULSE_MIN_BRIGHTNESS) * pulse;
            }

            batch.setColor(
                    previousColor.r * pulseBrightness,
                    previousColor.g * pulseBrightness,
                    previousColor.b * pulseBrightness,
                    pulseAlpha);

            super.draw(batch);
            batch.setColor(previousColor);

            if (connectionState != AchievementService.ConnectionState.CONNECTED) {
                float labelX = BUTTON_X + BUTTON_SIZE - disconnectedLabelLayout.width - STATUS_LABEL_PADDING + 5;
                float labelY = BUTTON_Y + disconnectedLabelLayout.height + STATUS_LABEL_PADDING + disconnectedLabelLayout.height / 2f;
                BitmapFont labelFont = FontHelper.getSingleton().getFont(Color.RED, STATUS_LABEL_SIZE, DISCONNECTED_LABEL);
                labelFont.setColor(1f, 0f, 0f, pulseAlpha);
                labelFont.draw(batch, DISCONNECTED_LABEL, Math.round(labelX), Math.round(labelY));
            }
        }

        private AchievementService.ConnectionState getConnectionState() {
            AchievementService achievementService = SkillfulPixelDungeonPlatformer.getAchievementService();
            return achievementService == null
                    ? AchievementService.ConnectionState.UNAVAILABLE
                    : achievementService.getConnectionState();
        }
    }

    private class FireEffect {

        private final float parentX;
        private final float parentY;
        private final GameSprite fire;
        private float speedX;
        private float speedY;
        private float lifeSpan = 70f;
        private float fadeIn;

        private FireEffect(float parentX, float parentY) {
            this.parentX = parentX;
            this.parentY = parentY;
            fire = new GameSprite("images/intro/fireball-effect-1.png", 38, 54, 0.7f);
            reset();
        }

        private void act(float delta) {
            fire.setPosition(fire.getX() + delta * speedX, fire.getY() + delta * speedY);
            fire.setWidth((int) (38 * (lifeSpan / 100f)));
            fire.setHeight((int) (54 * (lifeSpan / 100f)));
            fire.setAlpha(Math.min(fadeIn / 10f, lifeSpan / 100f));
            lifeSpan -= 40f * delta;
            fadeIn += delta;
            if (lifeSpan < 0f) {
                reset();
            }
        }

        private void draw(Batch batch) {
            fire.draw(batch);
        }

        private void reset() {
            fire.setPosition(parentX + 75, parentY + 100);
            speedX = 100 - RandomHelper.getInstance().randomInt(200);
            speedY = 150 + RandomHelper.getInstance().randomInt(150);
            fire.setWidth(38);
            fire.setHeight(54);
            lifeSpan = 40f + RandomHelper.getInstance().randomInt(30);
            fadeIn = 0f;
        }
    }

    private class FireworkEffect {
        private static final int SPARK_COUNT = 14;
        private static final float MIN_BURST_Y = 820f;
        private static final float BURST_Y_RANGE = 260f;
        private static final float FIREWORK_MIN_X = 300f;
        private static final float FIREWORK_X_RANGE = 1450f;
        private static final float FIREWORK_MIN_START_Y = 240f;
        private static final float FIREWORK_START_Y_RANGE = 120f;
        private static final float FIREWORK_MIN_SPEED_Y = 320f;
        private static final float FIREWORK_SPEED_Y_RANGE = 130f;
        private static final float FIREWORK_DRIFT_RANGE = 50f;
        private static final float SPARK_SPEED_MIN = 110f;
        private static final float SPARK_SPEED_RANGE = 170f;
        private static final float SPARK_GRAVITY = 180f;
        private static final float SPARK_LIFESPAN_MIN = 0.8f;
        private static final float SPARK_LIFESPAN_RANGE = 0.45f;
        private final GameSprite rocket = new GameSprite("images/misc/yellow-dot.png", 10, 10, 0.9f);
        private final ArrayList<FireworkSpark> sparks = new ArrayList<>();
        private float x;
        private float y;
        private float speedX;
        private float speedY;
        private float burstY;
        private boolean exploded;

        private FireworkEffect() {
            reset();
        }

        private void act(float delta) {
            if (!exploded) {
                x += speedX * delta;
                y += speedY * delta;
                rocket.setPosition(x, y);
                if (y >= burstY) {
                    explode();
                }
                return;
            }

            boolean hasActiveSpark = false;
            for (FireworkSpark spark : sparks) {
                spark.act(delta);
                hasActiveSpark |= spark.isActive();
            }

            if (!hasActiveSpark) {
                reset();
            }
        }

        private void draw(Batch batch) {
            if (!exploded) {
                rocket.draw(batch);
                return;
            }

            for (FireworkSpark spark : sparks) {
                spark.draw(batch);
            }
        }

        private void reset() {
            exploded = false;
            sparks.clear();
            x = FIREWORK_MIN_X + RandomHelper.getInstance().randomFloat(FIREWORK_X_RANGE);
            y = FIREWORK_MIN_START_Y + RandomHelper.getInstance().randomFloat(FIREWORK_START_Y_RANGE);
            speedX = RandomHelper.getInstance().randomFloat(FIREWORK_DRIFT_RANGE * 2f) - FIREWORK_DRIFT_RANGE;
            speedY = FIREWORK_MIN_SPEED_Y + RandomHelper.getInstance().randomFloat(FIREWORK_SPEED_Y_RANGE);
            burstY = MIN_BURST_Y + RandomHelper.getInstance().randomFloat(BURST_Y_RANGE);
            rocket.setPosition(x, y);
            rocket.setAlpha(0.95f);
        }

        private void explode() {
            exploded = true;
            if (fireworkBlastCooldown <= 0f) {
                SoundHelper.GetSingleton().play(Sounds.BLAST, 0f, 0.6f);
                fireworkBlastCooldown = FIREWORK_BLAST_COOLDOWN_SECONDS;
            }
            float angleStep = 360f / SPARK_COUNT;
            for (int index = 0; index < SPARK_COUNT; index++) {
                float angle = (angleStep * index + RandomHelper.getInstance().randomFloat(18f)) * 0.017453292f;
                float speed = SPARK_SPEED_MIN + RandomHelper.getInstance().randomFloat(SPARK_SPEED_RANGE);
                String spritePath = FIREWORK_SPRITES[RandomHelper.getInstance().randomInt(FIREWORK_SPRITES.length)];
                sparks.add(new FireworkSpark(
                        spritePath,
                        x,
                        y,
                        (float) Math.cos(angle) * speed,
                        (float) Math.sin(angle) * speed,
                        SPARK_LIFESPAN_MIN + RandomHelper.getInstance().randomFloat(SPARK_LIFESPAN_RANGE)));
            }
        }
    }

    private class FireworkSpark {
        private final GameSprite sprite;
        private float x;
        private float y;
        private float speedX;
        private float speedY;
        private float lifeSpan;
        private final float totalLifeSpan;

        private FireworkSpark(String spritePath, float x, float y, float speedX, float speedY, float lifeSpan) {
            sprite = new GameSprite(spritePath, 12, 12, 0.9f);
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.lifeSpan = lifeSpan;
            this.totalLifeSpan = lifeSpan;
            sprite.setPosition(x, y);
        }

        private void act(float delta) {
            if (lifeSpan <= 0f) {
                return;
            }

            lifeSpan = Math.max(0f, lifeSpan - delta);
            x += speedX * delta;
            y += speedY * delta;
            speedY -= FireworkEffect.SPARK_GRAVITY * delta;
            sprite.setPosition(x, y);
            sprite.setAlpha(totalLifeSpan <= 0f ? 0f : lifeSpan / totalLifeSpan);
        }

        private void draw(Batch batch) {
            if (lifeSpan > 0f) {
                sprite.draw(batch);
            }
        }

        private boolean isActive() {
            return lifeSpan > 0f;
        }
    }
}
