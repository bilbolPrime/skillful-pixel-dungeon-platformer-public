package com.bilboldev.skillfulpixeldungeonplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.InventoryHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ItemIdentityHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.DifficultyHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.NightModeHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.QuestManager;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RatKingHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UIHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomTransition;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.DesktopInputProcessor;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.NecromancerCurse;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Wizard;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.AmbientSound;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Effects;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow;

public class GameScreen extends BaseScreen {
    private static final float WORLD_VIEW_HEIGHT = com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomFraming.ORIGINAL_VIEW_HEIGHT;
    float darknessAlpha;

    {
        maxTop = 20;
        maxRight = 35;
        maxPlants = 1;
        decorationDensity = 0.4f;
        isBattle = false;
        showDamage = true;
        template = "sewers";

    }





Stage stage;

    private DesktopInputProcessor desktopInputProcessor;
    public DesktopInputProcessor desktopInput() { return desktopInputProcessor; }
    private final com.badlogic.gdx.utils.IntSet closeKeysHeld = new com.badlogic.gdx.utils.IntSet();
    private boolean cameraTrackingReady;
    private Hero cameraHero;
    private Room cameraRoom;
    private Room jumpFramingRoom;
    private float jumpFramingTop;
    private int cameraDepth, cameraPlacementVersion;
    private float cameraTrackX, baseCameraX, cameraLookAhead;
    private float cameraLastHeroX, cameraLastHeroY;



    GameSprite stats = new GameSprite("stats.png", 1212, 250);


    Hero hero2 = null;



    UnitHelper unitHelper;
    private final boolean loadSavedRun;
    private final DifficultyHelper.Difficulty startingDifficulty;
    private int runSlot = -1;
    private SaveHelper.RunSaveData slotSave;

    public GameScreen(HeroClass heroClass, DifficultyHelper.Difficulty difficulty, int slot) {
        this(heroClass, false, difficulty);
        if (slot < 0 || slot >= SaveHelper.RUN_SLOT_COUNT || SaveHelper.getInstance().slotOccupied(slot))
            throw new IllegalArgumentException("New runs require an empty slot");
        runSlot = slot;
    }

    public GameScreen(int slot, SaveHelper.RunSaveData saved) {
        this(HeroClass.valueOf(saved.heroClassName), true, DifficultyHelper.Difficulty.fromName(saved.difficultyName));
        if (slot < 0 || slot >= SaveHelper.RUN_SLOT_COUNT) throw new IllegalArgumentException("Invalid run slot");
        runSlot = slot;
        slotSave = saved;
    }

    public GameScreen(HeroClass heroClass){
        this(heroClass, false, DifficultyHelper.Difficulty.NORMAL);
    }

    public GameScreen(HeroClass heroClass, DifficultyHelper.Difficulty startingDifficulty){
        this(heroClass, false, startingDifficulty);
    }

    public GameScreen(HeroClass heroClass, boolean loadSavedRun){
        this(heroClass, loadSavedRun, DifficultyHelper.Difficulty.NORMAL);
    }

    private GameScreen(HeroClass heroClass, boolean loadSavedRun, DifficultyHelper.Difficulty startingDifficulty){
        super();
        hero2 = heroClass.getHero();
        this.loadSavedRun = loadSavedRun;
        this.startingDifficulty = startingDifficulty == null ? DifficultyHelper.Difficulty.NORMAL : startingDifficulty;
    }

    @Override
    public void create() {
        SaveHelper.getInstance().detachRun();
        SaveHelper.RunSaveData saveData = slotSave != null ? slotSave
                : loadSavedRun ? SaveHelper.getInstance().load(hero2.getHeroClass()) : null;
        DifficultyHelper.Difficulty runDifficulty = loadSavedRun
                ? DifficultyHelper.Difficulty.fromName(saveData != null ? saveData.difficultyName : null)
                : startingDifficulty;
        DifficultyHelper.getInstance().setCurrentDifficulty(runDifficulty);
        RandomHelper.getInstance().setRunSeed(saveData != null ? saveData.runSeed : RandomHelper.getInstance().newRunSeed());
        ItemIdentityHelper.getInstance().restore(saveData != null ? saveData.itemIdentity : null, RandomHelper.getInstance().getRunSeed());
        QuestManager.getInstance().restore(saveData != null ? saveData.quests : null);

        boolean touchControlsEnabled = SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled();
        boolean keyboardControlsEnabled = SkillfulPixelDungeonPlatformer.getPlatformProfile().keyboardControlsEnabled();

        float ratio = 1000f / Gdx.graphics.getWidth();
        MapHelper.getInstance().reset();
        SaveHelper.getInstance().prepareRoomRestore(saveData);
        MapHelper.getInstance().setRestoringGeneratedLevels(saveData != null);
        GameHelper.GetSingleton().setCamera(new OrthographicCamera(width, height));

        stageUnits = new Stage(new ExtendViewport(WORLD_VIEW_HEIGHT * 16f / 9f, WORLD_VIEW_HEIGHT,
                GameHelper.GetSingleton().getCamera()));
        unitHelper = UnitHelper.getInstance();
        unitHelper.reset();
        unitHelper.setStage(stageUnits);

        unitHelper.setHero(hero2);
        UIHelper.getInstance().basicButtons();
        MapHelper.getInstance().enterDepth(1);
        unitHelper.addUnit(hero2, true);

        InventoryHelper.getInstance().reset();
        applyStartingGold(runDifficulty, saveData == null);

        stats.setPosition(0, height - 250);

        darknessAlpha = 1f;


        batch = new SpriteBatch();
        batchUI = new SpriteBatch();




        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        cameraPar = new OrthographicCamera();
        cameraPar.setToOrtho(false, 800, 480);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, width, height);
        GameHelper.GetSingleton().setUICamera(uiCamera);
        createUiViewport();


        stage = new Stage(stageUnits.getViewport());

        if (touchControlsEnabled) {
            dpadStage = addTouchPad(unitHelper, width, height);
        }



        stageUnits.addActor(new AmbientSound());


        Effects effects = new Effects();
        stageUnits.addActor(effects);

        EffectsHelper.getInstance().addEffects(effects);

        if (saveData == null) {
            hero2.setRoom(MapHelper.getInstance().getActiveRoomIdentifier());
        }
        else {
            SaveHelper.getInstance().restore(saveData, hero2);
        }
        QuestManager.getInstance().ensureBlacksmithOreVeins();
        MapHelper.getInstance().setRestoringGeneratedLevels(false);

        AchievementManager.getInstance().onGameStarted(!loadSavedRun);

        effects.setZIndex(50);
        RatKingHelper.getInstance().resetRunState();
        RatKingHelper.getInstance().ensureCompanionPresent();
        if (NightModeHelper.isNightModeActive()) {
            EffectsHelper.getInstance().message(hero2, "These dungeons are more dangerous at night", Color.WHITE, 0f);
        }

        stageParalex = new Stage( new StretchViewport(Gdx.graphics.getWidth() * ratio, Gdx.graphics.getHeight() * ratio, cameraPar));
        font = new BitmapFont();
        skin = new Skin();


        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override public boolean keyDown(int key) { return com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.holdsInput(); }
            @Override public boolean keyUp(int key) { return com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.holdsInput(); }
            @Override public boolean touchDown(int x, int y, int pointer, int button) { return com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.holdsInput(); }
            @Override public boolean touchUp(int x, int y, int pointer, int button) { return com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.holdsInput(); }
        });
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keyboardControlsEnabled) com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance().keyboardUsed();
                if (keycode != Input.Keys.BACK && keycode != Input.Keys.ESCAPE) {
                    return false;
                }
                if (!closeKeysHeld.add(keycode)) return true;

                if (unitHelper.getHero() != null && unitHelper.getHero().isDead()) {
                    return true;
                }

                if (WindowHelper.getInstance().windowOpen() && WindowHelper.getInstance().handleKeyDown(keycode)) {
                    return true;
                }

                PauseMenuWindow.openGameplay();
                return true;
            }

            @Override
            public boolean keyUp(int keycode) { return closeKeysHeld.remove(keycode); }
            @Override public boolean touchDown(int x, int y, int pointer, int button) {
                if (keyboardControlsEnabled) com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance().keyboardUsed();
                return false;
            }
        });
        inputMultiplexer.addProcessor(new GestureDetector(WindowHelper.getInstance().inputGestureListener()));
        if (touchControlsEnabled) {
            inputMultiplexer.addProcessor(UIHelper.getInstance().touchInputProcessor());
            inputMultiplexer.addProcessor(dpadStage);
        }
        if (keyboardControlsEnabled) {
            desktopInputProcessor = new DesktopInputProcessor(unitHelper);
            inputMultiplexer.addProcessor(desktopInputProcessor);
        }
        if (!touchControlsEnabled) {
            inputMultiplexer.addProcessor(new GestureDetector(UIHelper.getInstance().inputGestureListener()));
        }
        inputMultiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(inputMultiplexer);
        SaveHelper.getInstance().attachRun(hero2, runSlot, saveData);
        if (runSlot >= 0 && saveData == null && !SaveHelper.getInstance().saveCurrentRun()) {
            WindowHelper.getInstance().addWindow(new com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow(
                    1100, 220, com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages.get("desktop.runs.save_failed")).build());
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void pause() {
        super.pause();
        if (desktopInputProcessor != null) com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance().setFocused(false);
        MapHelper.getInstance().getRoomTransition().stopSound();
        if (unitHelper.getHero() != null) unitHelper.getHero().suspendNewClassActions(true);
        clearPresentationPresses();
    }

    @Override
    public void resume() {
        super.resume();
        if (desktopInputProcessor != null) com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.ControllerInput.getInstance().setFocused(true);
        if (unitHelper.getHero() != null) unitHelper.getHero().suspendNewClassActions(false);
        clearPresentationPresses();
    }

    private void clearPresentationPresses() {
        closeKeysHeld.clear();
        if (unitHelper.getHero() != null) unitHelper.getHero().clearControlIntent();
        if (desktopInputProcessor != null) desktopInputProcessor.clearModalKeyPresses();
        resetCameraTracking();
    }

    private void applyStartingGold(DifficultyHelper.Difficulty difficulty, boolean isNewRun) {
        if (!isNewRun) {
            return;
        }

        int startingGold = getStartingGold(difficulty);
        if (startingGold > 0) {
            InventoryHelper.getInstance().modifyGold(startingGold);
        }
    }

    private int getStartingGold(DifficultyHelper.Difficulty difficulty) {
        if (difficulty == DifficultyHelper.Difficulty.HELL) {
            return 0;
        }
        if (difficulty == DifficultyHelper.Difficulty.NIGHTMARE) {
            return 150;
        }
        return 300;
    }

    @Override
    public void act(float delta) {
        if (com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.holdsGameplay()) {
            clearPresentationPresses();
            return;
        }
        com.bilboldev.skillfulpixeldungeonplatformer.windows.DesktopPauseMenuWindow pause = WindowHelper.getInstance().desktopPause();
        if (pause != null) {
            pause.act(delta);
            if (desktopInputProcessor != null) desktopInputProcessor.refreshMovementState();
            MapHelper.getInstance().getRoomTransition().stopSound();
            return;
        }
        delta = PhysicsHelper.boundGameDelta(delta);
        if(darknessAlpha > 0){
            darknessAlpha -= 0.25f * Math.min(0.1f, delta);

            if(darknessAlpha < 0){
                darknessAlpha = 0;
            }

        }

        MapHelper.getInstance().act(delta);

        if (desktopInputProcessor != null) desktopInputProcessor.refreshMovementState();
        if(!WindowHelper.getInstance().windowOpen()){
            SaveHelper.getInstance().advancePlayTime(delta);
            stageUnits.act(delta);
            unitHelper.act(delta);
            unitHelper.getHero().updateJumpSupport(delta);
            unitHelper.getHero().updateAirbornePose(delta);
            GameHelper.GetSingleton().updateHitImpulse(delta);
            MapHelper.getInstance().getRoomTransition().update(delta);
            com.bilboldev.skillfulpixeldungeonplatformer.cloud.CloudSaves.gameplayAdvanced(delta);
        } else MapHelper.getInstance().getRoomTransition().stopSound();

        welcome();
    }

    @Override
    public void render() {
        act(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        OrthographicCamera worldCamera = GameHelper.GetSingleton().getCamera();
        RoomTransition transition = MapHelper.getInstance().getRoomTransition();
        Hero cameraSubject = unitHelper.getHero();
        transition.validate(MapHelper.getInstance().getActiveRoomIdentifier(), MapHelper.getInstance().getDepth(),
                cameraSubject.getPresentationPlacementVersion(), cameraSubject.isDead());
        if (WindowHelper.getInstance().desktopPause() == null) updateCameraTracking(Gdx.graphics.getDeltaTime());
        worldCamera.zoom = getBaseRoomZoom() * transition.getZoom();
        worldCamera.position.set(getCameraX(), getCameraY(), worldCamera.position.z);
        worldCamera.update();

        if (stageUnits != null) {
            stageUnits.getViewport().apply(false);
        }
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        MapHelper.getInstance().draw(batch);
        batch.end();

        batch.begin();
        stageUnits.draw();
        MapHelper.getInstance().drawVisibilityMask(batch);
        batch.end();

        PhysicsHelper.getInstance().renderDebug(worldCamera);

        if (dpadStage != null) {
            dpadStage.getViewport().apply(false);
        }
        uiViewport.apply(false);
        uiCamera.update();
        batchUI.setProjectionMatrix(uiCamera.combined);
        batchUI.begin();

        if(!WindowHelper.getInstance().windowOpen() || WindowHelper.getInstance().desktopPause() != null){
            UIHelper.getInstance().drawButtons(batchUI);
        }
        if (WindowHelper.getInstance().windowOpen()) {
            WindowHelper.getInstance().draw(batchUI);
        }

        batchUI.end();

        batchUI.begin();

        if(dpadStage != null && !WindowHelper.getInstance().windowOpen()){
            dpadStage.draw();
        }


        batchUI.end();

    }

    @Override
    public void hide() {
        super.hide();
        MapHelper.getInstance().getRoomTransition().stopSound();
    }

    @Override
    public void dispose() {
        MapHelper.getInstance().clearRoomPresentation();
        if (stageUnits != null) {

            for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) if (unit.getStage() == stageUnits) {
                NecromancerCurse.clear(unit);
                if (unit instanceof NecromancerMinion) UnitHelper.getInstance().removeUnit(unit);
            }
        }
                disposeTouchPad();
        batch.dispose();


    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (stageUnits != null) {
            stageUnits.getViewport().update(width, height, true);
        }

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }

        if (dpadStage != null) {
            dpadStage.getViewport().update(width, height, true);
        }
        resizeUiViewport(width, height);
        UIHelper.getInstance().layoutHud();
        resetCameraTracking();

        float ratio = width <= 0 ? 1f : 1000f / width;
        if (stageParalex != null) {
            stageParalex.getViewport().update(
                    Math.max(1, Math.round(width * ratio)),
                    Math.max(1, Math.round(height * ratio)),
                    true);
        }
    }



    boolean welcomed;
    public void welcome(){
        if(welcomed){
            return;
        }

        welcomed = true;

    }


    public void resetCameraTracking() {
        cameraTrackingReady = false;
        GameHelper.GetSingleton().clearHitImpulse();
        MapHelper.getInstance().getRoomTransition().clear();
    }

    private void updateCameraTracking(float delta) {
        Hero hero = unitHelper.getHero();
        Room room = MapHelper.getInstance().getActiveRoom();
        int depth = MapHelper.getInstance().getDepth();
        float focusX = hero.getRenderX() + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float horizontalSpeed = PhysicsHelper.toPixelSpeed(PhysicsHelper.getInstance().getHorizontalSpeed(hero));
        float motion = Math.min(1f, Math.abs(horizontalSpeed) / 300f);
        float lookAhead = (hero.facingRight ? 1f : -1f) * ConstantsHelper.TILE * (0.5f + 0.25f * motion);
        if (GameSettingsHelper.getInstance().isReducedCameraMotion()) lookAhead = 0f;
        boolean reset = !cameraTrackingReady || cameraHero != hero || cameraRoom != room || cameraDepth != depth
                || cameraPlacementVersion != hero.getPresentationPlacementVersion()
                || Math.abs(hero.x - cameraLastHeroX) > 2f * ConstantsHelper.TILE
                || Math.abs(hero.y - cameraLastHeroY) > 2f * ConstantsHelper.TILE;
        float baseVisibleWidth = GameHelper.GetSingleton().getCamera().viewportWidth * getBaseRoomZoom();
        if (reset) {
            GameHelper.GetSingleton().clearHitImpulse();
            cameraLookAhead = lookAhead;
            cameraTrackX = clampCameraX(focusX + lookAhead, baseVisibleWidth);
            baseCameraX = cameraTrackX;
        } else {
            float elapsed = Math.max(0f, Math.min(delta, 0.1f));
            cameraLookAhead += (lookAhead - cameraLookAhead) * (1f - (float) Math.exp(-elapsed / 0.15f));
            float desired = focusX + cameraLookAhead;
            float deadZone = ConstantsHelper.TILE * 0.25f;
            if (desired > cameraTrackX + deadZone) cameraTrackX = desired - deadZone;
            else if (desired < cameraTrackX - deadZone) cameraTrackX = desired + deadZone;
            cameraTrackX = clampCameraX(cameraTrackX, baseVisibleWidth);
            baseCameraX += (cameraTrackX - baseCameraX) * (1f - (float) Math.exp(-elapsed / 0.12f));
            baseCameraX = clampCameraX(baseCameraX, baseVisibleWidth);
        }
        cameraHero = hero;
        cameraRoom = room;
        cameraDepth = depth;
        cameraPlacementVersion = hero.getPresentationPlacementVersion();
        cameraLastHeroX = hero.x;
        cameraLastHeroY = hero.y;
        cameraTrackingReady = true;
    }

    private float clampCameraX(float target, float visibleWidth) {
        float roomWidth = MapHelper.getInstance().getWidth() * ConstantsHelper.TILE;
        return roomWidth <= visibleWidth ? roomWidth / 2f
                : Math.min(Math.max(visibleWidth / 2f, target), roomWidth - visibleWidth / 2f);
    }

    float getCameraX(){
        OrthographicCamera worldCamera = GameHelper.GetSingleton().getCamera();
        return clampCameraX(baseCameraX + GameHelper.GetSingleton().getHitImpulseX(), worldCamera.viewportWidth * worldCamera.zoom);
    }

    private float getBaseRoomZoom() {
        MapHelper map = MapHelper.getInstance();
        float zoom = com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomFraming.baseZoom(
                !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled(),
                map.isBossLevelActive() || map.getDepth() >= 26,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Room room = map.getActiveRoom();
        boolean fitSpecial = room != null
                && com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint.supports(room)
                && !(room instanceof com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.TreasureRoom);
        if (room != null && (room.getClass() == Room.class || fitSpecial) && !map.isBossLevelActive() && map.getDepth() < 26) {
            if (jumpFramingRoom != room) {
                jumpFramingRoom = room;
                jumpFramingTop = com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomRoutes
                        .jumpTopWithClearance(room.getHighestStandingFloor());
            }
            zoom = com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomFraming.fitJump(zoom,
                    jumpFramingTop, GameHelper.GetSingleton().getUICamera().viewportHeight, UIHelper.HUD_HEIGHT + 24f);
        }
        return zoom;
    }

    float getCameraY() {
        OrthographicCamera worldCamera = GameHelper.GetSingleton().getCamera();
        float visibleHeight = worldCamera.viewportHeight * worldCamera.zoom;
        Room activeRoom = MapHelper.getInstance().getActiveRoom();
        if (activeRoom == null) {
            return visibleHeight / 2f;
        }

        float roomHeight = activeRoom.getHeight() * ConstantsHelper.TILE;
        float uiHeight = GameHelper.GetSingleton().getUICamera().viewportHeight;
        return com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomFraming.centerY(
                visibleHeight, roomHeight, uiHeight, UIHelper.HUD_HEIGHT + 24f, getBaseRoomZoom() > 1f);
    }
}
