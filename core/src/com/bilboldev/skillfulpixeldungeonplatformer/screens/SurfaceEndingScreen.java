package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AmbientMusicHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.AssetHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NightModeHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.RedButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;

import java.util.ArrayList;

public class SurfaceEndingScreen extends MenuScreenBase {

    private static final int FRAME_SOURCE_WIDTH = 88;
    private static final int FRAME_SOURCE_HEIGHT = 125;
    private static final int INNER_SOURCE_WIDTH = 80;
    private static final int INNER_SOURCE_HEIGHT = 112;
    private static final int FRAME_MARGIN_X = 4;
    private static final int FRAME_MARGIN_Y = 9;
    private static final int FRAME_TOP_MARGIN = FRAME_SOURCE_HEIGHT - INNER_SOURCE_HEIGHT - FRAME_MARGIN_Y;
    private static final int FRAME_RIGHT_MARGIN = FRAME_SOURCE_WIDTH - INNER_SOURCE_WIDTH - FRAME_MARGIN_X;
    private static final int GRASS_SOURCE_Y = 60;
    private static final int GRASS_SOURCE_WIDTH = 16;
    private static final int GRASS_SOURCE_HEIGHT = 14;
    private static final int[][] CLOUD_REGIONS = new int[][]{
            {88, 0, 49, 20},
            {88, 20, 49, 22},
            {88, 42, 50, 18}
    };
    private static final float BUTTON_WIDTH = 480f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float BUTTON_BOTTOM_MARGIN = 55f;
    private static final float SCENE_BOTTOM_GAP = 45f;
    private static final float SCENE_TOP_GAP = 60f;
    private static final float HERO_SCALE = 3.8f;
    private static final Color DAY_BACKDROP_COLOR = new Color(0.05f, 0.07f, 0.11f, 1f);
    private static final Color DAY_SKY_COLOR = new Color(0.50f, 0.72f, 0.96f, 1f);
    private static final Color DAY_HORIZON_COLOR = new Color(0.90f, 0.97f, 1f, 1f);
    private static final Color DAY_GROUND_COLOR = new Color(0.40f, 0.63f, 0.24f, 1f);
    private static final Color DAY_CLOUD_COLOR = new Color(0.92f, 0.97f, 1f, 1f);
    private static final Color DAY_FAR_GRASS_COLOR = new Color(0.72f, 0.86f, 0.68f, 1f);
    private static final Color DAY_NEAR_GRASS_COLOR = new Color(0.88f, 0.98f, 0.78f, 1f);
    private static final Color NIGHT_BACKDROP_COLOR = new Color(0.01f, 0.02f, 0.05f, 1f);
    private static final Color NIGHT_SKY_COLOR = new Color(0.08f, 0.11f, 0.20f, 1f);
    private static final Color NIGHT_HORIZON_COLOR = new Color(0.20f, 0.26f, 0.40f, 1f);
    private static final Color NIGHT_GROUND_COLOR = new Color(0.17f, 0.25f, 0.14f, 1f);
    private static final Color NIGHT_CLOUD_COLOR = new Color(0.38f, 0.44f, 0.56f, 1f);
    private static final Color NIGHT_FAR_GRASS_COLOR = new Color(0.36f, 0.46f, 0.32f, 1f);
    private static final Color NIGHT_NEAR_GRASS_COLOR = new Color(0.46f, 0.58f, 0.38f, 1f);
    private static final Color STAR_COLOR = new Color(0.90f, 0.95f, 1f, 1f);

    private final ArrayList<Cloud> clouds = new ArrayList<>();
    private final ArrayList<GrassPatch> farGrass = new ArrayList<>();
    private final ArrayList<GrassPatch> nearGrass = new ArrayList<>();
    private final ArrayList<Star> stars = new ArrayList<>();

    private Music endingMusic;
    private GameSprite backdrop;
    private GameSprite sky;
    private GameSprite horizonGlow;
    private GameSprite ground;
    private GameSprite frame;
    private GameFilm heroSprite;
    private float frameX;
    private float frameY;
    private float frameScale;
    private float innerX;
    private float innerY;
    private float innerWidth;
    private float innerHeight;
    private float sceneBottom;
    private float sceneHeight;
    private float groundHeight;
    private float heroBaseX;
    private float heroBaseY;
    private float heroBobTime;
    private boolean nightEnding;

    @Override
    protected void createMenuContent() {
        ambientSound = null;
        AmbientMusicHelper.getSingleton().stop();
        startEndingMusic();
        nightEnding = NightModeHelper.consumeSurfaceEndingNight();

        float screenWidth = width;
        float screenHeight = height;
        float buttonY = BUTTON_BOTTOM_MARGIN;
        float frameScaleX = screenWidth / FRAME_SOURCE_WIDTH;
        float frameScaleY = screenHeight / FRAME_SOURCE_HEIGHT;

        sceneBottom = 0f;
        sceneHeight = screenHeight;
        frameScale = Math.min(frameScaleX, frameScaleY);
        frameX = 0f;
        frameY = 0f;
        innerX = FRAME_MARGIN_X * frameScaleX;
        innerY = FRAME_MARGIN_Y * frameScaleY;
        innerWidth = screenWidth - innerX - FRAME_RIGHT_MARGIN * frameScaleX;
        innerHeight = screenHeight - innerY - FRAME_TOP_MARGIN * frameScaleY;
        groundHeight = Math.max(innerHeight * 0.16f, sceneHeight * 0.16f);

        Color backdropColor = nightEnding ? NIGHT_BACKDROP_COLOR : DAY_BACKDROP_COLOR;
        Color skyColor = nightEnding ? NIGHT_SKY_COLOR : DAY_SKY_COLOR;
        Color horizonColor = nightEnding ? NIGHT_HORIZON_COLOR : DAY_HORIZON_COLOR;
        Color groundColor = nightEnding ? NIGHT_GROUND_COLOR : DAY_GROUND_COLOR;

        backdrop = createTintedSprite("images/misc/grey.png", screenWidth, screenHeight, backdropColor, 1f);
        backdrop.setPosition(0f, 0f);

        sky = createTintedSprite("images/misc/grey.png", screenWidth, sceneHeight, skyColor, 1f);
        sky.setPosition(0f, sceneBottom);

        horizonGlow = createTintedSprite("images/misc/grey.png", screenWidth, sceneHeight * 0.42f, horizonColor, nightEnding ? 0.12f : 0.18f);
        horizonGlow.setPosition(0f, sceneBottom + sceneHeight * 0.18f);

        ground = createTintedSprite("images/misc/grey.png", screenWidth, groundHeight, groundColor, 1f);
        ground.setPosition(0f, sceneBottom);

        frame = createSurfaceSprite(0, 0, FRAME_SOURCE_WIDTH, FRAME_SOURCE_HEIGHT,
            screenWidth, screenHeight, null, 1f);
        frame.setPosition(frameX, frameY);

        HeroClass heroClass = UnitHelper.getInstance().getHero() != null
                ? UnitHelper.getInstance().getHero().getHeroClass()
                : HeroClass.WARRIOR;
        heroSprite = new GameFilm(heroClass.getFilm(), ConstantsHelper.UNIT_DIMENSIONS, ConstantsHelper.UNIT_DIMENSIONS, 1f);
        heroSprite.clipSizeX = 12;
        heroSprite.clipSizeY = 15;
        heroSprite.yClipOffset = 1;
        heroSprite.tileX = 0;
        heroSprite.tileY = 0;
        float heroScale = HERO_SCALE * (frameScale / 7.2f);
        heroSprite.setScale(heroScale, heroScale);
        heroBaseX = innerX + innerWidth / 2f - ConstantsHelper.UNIT_DIMENSIONS * heroScale / 2f;
        heroBaseY = innerY + groundHeight - 10f;
        heroSprite.setPosition(heroBaseX, heroBaseY);

        rebuildStars();
        rebuildClouds();
        rebuildGrass();

        buttons.add(new RedButton((screenWidth - BUTTON_WIDTH) / 2f, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                SkillfulPixelDungeonPlatformer.transition(new TitleScreen(), true);
            }
        }.setText("Game Over"));
    }

    @Override
    protected void actMenu(float delta) {
        heroBobTime += delta;
        heroSprite.setPosition(heroBaseX, heroBaseY + MathUtils.sin(heroBobTime * 1.6f) * 10f);
        for (Star star : stars) {
            star.act(delta);
        }
        for (Cloud cloud : clouds) {
            cloud.act(delta);
        }
        for (GrassPatch grassPatch : farGrass) {
            grassPatch.act(delta);
        }
        for (GrassPatch grassPatch : nearGrass) {
            grassPatch.act(delta);
        }
    }

    @Override
    protected void drawMenu(Batch batch) {
        backdrop.draw(batch);
        sky.draw(batch);
        horizonGlow.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        for (Cloud cloud : clouds) {
            cloud.draw(batch);
        }
        ground.draw(batch);
        for (GrassPatch grassPatch : farGrass) {
            grassPatch.draw(batch);
        }
        heroSprite.draw(batch);
        for (GrassPatch grassPatch : nearGrass) {
            grassPatch.draw(batch);
        }
        frame.draw(batch);
        drawButtons(batch);
    }

    @Override
    protected boolean handleBackAction() {
        return true;
    }

    @Override
    public void dispose() {
        stopEndingMusic();
        super.dispose();
    }

    private void startEndingMusic() {
        if (!GameSettingsHelper.getInstance().isMusicEnabled()) {
            return;
        }

        endingMusic = AssetHelper.getInstance().getMusic(Sounds.HAPPY);
        endingMusic.stop();
        endingMusic.setLooping(true);
        endingMusic.setVolume(1f);
        endingMusic.play();
    }

    private void stopEndingMusic() {
        if (endingMusic != null) {
            endingMusic.stop();
            endingMusic = null;
        }
    }

    private GameSprite createTintedSprite(String texturePath, float width, float height, Color color, float alpha) {
        Sprite sprite = new Sprite(AssetHelper.getInstance().getTexture(texturePath));
        if (color != null) {
            sprite.setColor(color);
        }

        GameSprite gameSprite = new GameSprite(sprite, width, height);
        gameSprite.setAlpha(alpha);
        return gameSprite;
    }

    private GameSprite createSurfaceSprite(int srcX, int srcY, int srcWidth, int srcHeight,
                                           float drawWidth, float drawHeight,
                                           Color tint, float alpha) {
        Sprite sprite = new Sprite(AssetHelper.getInstance().getTexture("surface.png"), srcX, srcY, srcWidth, srcHeight);
        if (tint != null) {
            sprite.setColor(tint);
        }

        GameSprite gameSprite = new GameSprite(sprite, drawWidth, drawHeight);
        gameSprite.setAlpha(alpha);
        return gameSprite;
    }

    private void rebuildClouds() {
        clouds.clear();
        int cloudCount = Math.max(5, (int) (width / 360f));
        for (int i = 0; i < cloudCount; i++) {
            clouds.add(new Cloud(i));
        }
    }

    private void rebuildStars() {
        stars.clear();
        if (!nightEnding) {
            return;
        }

        int starCount = Math.max(20, (int) (width / 75f));
        for (int i = 0; i < starCount; i++) {
            stars.add(new Star());
        }
    }

    private void rebuildGrass() {
        farGrass.clear();
        nearGrass.clear();

        float farScale = frameScale * 0.72f;
        float nearScale = frameScale * 0.96f;
        float farPatchWidth = GRASS_SOURCE_WIDTH * farScale;
        float nearPatchWidth = GRASS_SOURCE_WIDTH * nearScale;
        int farPatchCount = Math.max(8, (int) Math.ceil(width / farPatchWidth) + 3);
        int nearPatchCount = Math.max(8, (int) Math.ceil(width / nearPatchWidth) + 3);

        float farY = sceneBottom + groundHeight + frameScale * 0.5f;
        float nearY = sceneBottom + groundHeight;
        Color farGrassColor = nightEnding ? NIGHT_FAR_GRASS_COLOR : DAY_FAR_GRASS_COLOR;
        Color nearGrassColor = nightEnding ? NIGHT_NEAR_GRASS_COLOR : DAY_NEAR_GRASS_COLOR;

        for (int i = 0; i < farPatchCount; i++) {
            farGrass.add(new GrassPatch((i - 0.75f) * farPatchWidth, farY, farScale, farGrassColor, 0.85f));
        }

        for (int i = 0; i < nearPatchCount; i++) {
            nearGrass.add(new GrassPatch((i - 0.5f) * nearPatchWidth, nearY, nearScale, nearGrassColor, 1f));
        }
    }

    private final class Star {
        private final GameSprite sprite;
        private final float baseAlpha;
        private final float twinkleSpeed;
        private final float twinkleOffset;
        private float twinkleTime;

        private Star() {
            float size = frameScale * (0.5f + RandomHelper.getInstance().randomFloat(0.75f));
            sprite = createTintedSprite("images/misc/grey.png", size, size, STAR_COLOR, 1f);
            baseAlpha = 0.45f + RandomHelper.getInstance().randomFloat(0.35f);
            twinkleSpeed = 0.7f + RandomHelper.getInstance().randomFloat(1.2f);
            twinkleOffset = RandomHelper.getInstance().randomFloat(MathUtils.PI2);
            sprite.setPosition(
                    innerX + RandomHelper.getInstance().randomFloat(innerWidth),
                    sceneBottom + sceneHeight * (0.48f + RandomHelper.getInstance().randomFloat(0.42f)));
            updateAlpha(0f);
        }

        private void act(float delta) {
            twinkleTime += delta;
            updateAlpha(twinkleTime);
        }

        private void updateAlpha(float time) {
            float twinkle = 0.55f + 0.45f * MathUtils.sin(time * twinkleSpeed + twinkleOffset);
            sprite.setAlpha(baseAlpha * twinkle);
        }

        private void draw(Batch batch) {
            sprite.draw(batch);
        }
    }

    private final class Cloud {
        private final GameSprite sprite;
        private final float baseY;
        private final float speed;
        private final float resetOffset;

        private Cloud(int index) {
            int[] region = CLOUD_REGIONS[RandomHelper.getInstance().randomInt(CLOUD_REGIONS.length)];
            float depthScale = 0.55f + RandomHelper.getInstance().randomFloat(0.55f);
            float cloudWidth = region[2] * frameScale * depthScale;
            float cloudHeight = region[3] * frameScale * depthScale;
            Color cloudColor = nightEnding ? NIGHT_CLOUD_COLOR : DAY_CLOUD_COLOR;
            sprite = createSurfaceSprite(region[0], region[1], region[2], region[3], cloudWidth, cloudHeight, cloudColor, nightEnding ? 0.52f : 0.82f);
            baseY = sceneBottom + sceneHeight * (0.40f + RandomHelper.getInstance().randomFloat(0.44f));
            speed = frameScale * depthScale * (3f + RandomHelper.getInstance().randomFloat(3f));
            resetOffset = frameScale * (20f + RandomHelper.getInstance().randomFloat(40f));
            sprite.setPosition(-sprite.getWidth() - index * resetOffset, baseY);
        }

        private void act(float delta) {
            sprite.translate(speed * delta, 0f);
            if (sprite.getX() > width + resetOffset) {
                sprite.setPosition(-sprite.getWidth() - resetOffset, baseY);
            }
        }

        private void draw(Batch batch) {
            sprite.draw(batch);
        }
    }

    private final class GrassPatch {
        private final GameSprite sprite;
        private final float anchorX;
        private final float anchorY;
        private final float swayAmplitude;
        private final float swaySpeed;
        private final float phaseOffset;
        private float swayTime;

        private GrassPatch(float anchorX, float anchorY, float scale, Color tint, float alpha) {
            int sourceX = 88 + RandomHelper.getInstance().randomInt(4) * GRASS_SOURCE_WIDTH;
            sprite = createSurfaceSprite(sourceX, GRASS_SOURCE_Y, GRASS_SOURCE_WIDTH, GRASS_SOURCE_HEIGHT,
                    GRASS_SOURCE_WIDTH * scale, GRASS_SOURCE_HEIGHT * scale, tint, alpha);
            this.anchorX = anchorX;
            this.anchorY = anchorY;
            swayAmplitude = scale * (0.8f + RandomHelper.getInstance().randomFloat(0.6f));
            swaySpeed = 0.8f + RandomHelper.getInstance().randomFloat(1.1f);
            phaseOffset = RandomHelper.getInstance().randomFloat(MathUtils.PI2);
            updatePose(0f);
        }

        private void act(float delta) {
            swayTime += delta;
            updatePose(swayTime);
        }

        private void updatePose(float time) {
            float sway = MathUtils.sin(time * swaySpeed + phaseOffset);
            float stretch = 0.92f + 0.08f * MathUtils.cos(time * swaySpeed + phaseOffset);
            sprite.setScale(1f, stretch);
            sprite.setPosition(anchorX + sway * swayAmplitude,
                    anchorY - sprite.getHeight() * sprite.getScaleY());
        }

        private void draw(Batch batch) {
            sprite.draw(batch);
        }
    }
}