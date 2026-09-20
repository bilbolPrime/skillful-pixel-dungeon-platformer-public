package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingSupportHelper.TitleThemeOption;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.MenuCameraTransition.Section;


public final class DesktopMenuSession {
    private static final Section[] SECTIONS = Section.values();
    private final MenuCameraTransition navigation = new MenuCameraTransition();
    private final MenuScene[] scenes = new MenuScene[SECTIONS.length];
    private final OrthographicCamera localCamera = new OrthographicCamera();
    private final Rectangle bounds = new Rectangle(), scissors = new Rectangle();
    private final Matrix4 projection = new Matrix4();
    private TitleThemeOption theme;
    private MenuStoneBackdrop foundation;

    private MenuScene heroCast;
    private boolean paused;

    public DesktopMenuSession(TitleThemeOption theme) { setTheme(theme); }

    public void setTheme(TitleThemeOption selected) {
        if (selected == null) selected = TitleThemeOption.SEWERS;
        if (selected == theme) return;
        theme = selected;
        foundation = new MenuStoneBackdrop(theme);
        for (int i = 0; i < scenes.length; i++) scenes[i] = null;
        if (theme == TitleThemeOption.SEWERS) scenes[Section.TITLE.ordinal()] = new SewersTitleScene();
        if (theme == TitleThemeOption.SEWERS) scenes[Section.HEROES.ordinal()] = new SewersHeroScene();
        if (theme == TitleThemeOption.SEWERS) scenes[Section.INFORMATION.ordinal()] = new SewersGalleryScene();
        if (theme == TitleThemeOption.PRISON) scenes[Section.TITLE.ordinal()] = new PrisonTitleScene();
        if (theme == TitleThemeOption.PRISON) scenes[Section.HEROES.ordinal()] = new PrisonHeroScene();
        if (theme == TitleThemeOption.PRISON) scenes[Section.INFORMATION.ordinal()] = new PrisonGalleryScene();
        if (theme == TitleThemeOption.CAVES) scenes[Section.TITLE.ordinal()] = new CavesTitleScene();
        if (theme == TitleThemeOption.CAVES) scenes[Section.HEROES.ordinal()] = new CavesHeroScene();
        if (theme == TitleThemeOption.CAVES) scenes[Section.INFORMATION.ordinal()] = new CavesGalleryScene();
        if (theme == TitleThemeOption.CITY) scenes[Section.TITLE.ordinal()] = new CityTitleScene();
        if (theme == TitleThemeOption.CITY) scenes[Section.HEROES.ordinal()] = new CityHeroScene();
        if (theme == TitleThemeOption.CITY) scenes[Section.INFORMATION.ordinal()] = new CityGalleryScene();
        if (theme == TitleThemeOption.HALL) scenes[Section.TITLE.ordinal()] = new HallsTitleScene();
        if (theme == TitleThemeOption.HALL) scenes[Section.HEROES.ordinal()] = new HallsHeroScene();
        if (theme == TitleThemeOption.HALL) scenes[Section.INFORMATION.ordinal()] = new HallsGalleryScene();
    }

    public void act(float delta) {
        if (paused || Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0f) return;
        float step = Math.min(0.1f, delta);
        navigation.setReducedMotion(GameSettingsHelper.getInstance().isReducedCameraMotion());
        navigation.act(step);
        foundation.act(step);
        for (MenuScene scene : scenes) if (scene != null) scene.act(step);
        if (heroCast != null) heroCast.act(step);
    }

    public void draw(Batch batch, OrthographicCamera camera) {
        float viewLeft = camera.position.x - camera.viewportWidth / 2f;
        if (viewLeft >= 0f && viewLeft + camera.viewportWidth <= 2560f
                && scenes[Section.TITLE.ordinal()] != null) {

            scenes[Section.TITLE.ordinal()].draw(batch, camera);
            return;
        }
        projection.set(batch.getProjectionMatrix());
        float packed = batch.getPackedColor();
        try {


            Section single = viewLeft + camera.viewportWidth <= 0f ? Section.HEROES
                    : viewLeft >= Section.INFORMATION.origin ? Section.INFORMATION : null;
            if (single != null && scenes[single.ordinal()] != null) {
                localCamera.viewportWidth = camera.viewportWidth;
                localCamera.viewportHeight = camera.viewportHeight;
                localCamera.position.set(camera.position.x - single.origin, camera.position.y, 0f);
                localCamera.update();
                batch.setProjectionMatrix(localCamera.combined);
                scenes[single.ordinal()].draw(batch, localCamera);
                if (single == Section.HEROES && heroCast != null) heroCast.draw(batch, localCamera);
                return;
            }
            foundation.draw(batch, camera);
            float left = camera.position.x - camera.viewportWidth / 2f;
            float bottom = camera.position.y - camera.viewportHeight / 2f;
            for (Section section : SECTIONS) {
                MenuScene scene = scenes[section.ordinal()];
                MenuScene cast = section == Section.HEROES ? heroCast : null;
                if (scene == null && cast == null) continue;

                float min = section == Section.HEROES ? left : section.origin;
                float max = section == Section.INFORMATION ? left + camera.viewportWidth : section.origin + 2560f;
                min = Math.max(min, left);
                max = Math.min(max, left + camera.viewportWidth);
                if (max <= min) continue;
                bounds.set(min, bottom, max - min, camera.viewportHeight);
                batch.flush();
                ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), bounds, scissors);
                if (!ScissorStack.pushScissors(scissors)) continue;
                try {
                    localCamera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
                    localCamera.position.set(camera.position.x - section.origin, camera.position.y, camera.position.z);
                    localCamera.update();
                    batch.setProjectionMatrix(localCamera.combined);
                    if (scene != null) scene.draw(batch, localCamera);
                    if (cast != null) cast.draw(batch, localCamera);
                    batch.flush();
                } finally {
                    ScissorStack.popScissors();
                    batch.setProjectionMatrix(projection);
                }
            }
        } finally {
            batch.setProjectionMatrix(projection);
            batch.setPackedColor(packed);
        }
    }


    public void setScene(Section section, MenuScene scene) { scenes[section.ordinal()] = scene; }
    public void setHeroCast(MenuScene cast) { heroCast = cast; }
    public boolean hasTitleScene() { return scenes[Section.TITLE.ordinal()] != null; }
    public boolean hasScene(Section section) { return scenes[section.ordinal()] != null; }
    public TitleThemeOption theme() { return theme; }
    public MenuCameraTransition navigation() { return navigation; }
    public void setPaused(boolean value) {
        paused = value;
        if (value) navigation.stopSound();
    }
}
