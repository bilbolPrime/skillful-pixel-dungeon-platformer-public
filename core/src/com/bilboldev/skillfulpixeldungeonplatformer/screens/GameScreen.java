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
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.TomeOfMastery;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
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
import com.bilboldev.skillfulpixeldungeonplatformer.misc.inputprocessing.DesktopInputProcessor;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Wizard;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.AmbientSound;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.Effects;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.InventoryWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.PauseMenuWindow;

public class GameScreen extends BaseScreen {
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



    GameSprite stats = new GameSprite("stats.png", 1212, 250);


    Hero hero2 = null;



    UnitHelper unitHelper;
    private final boolean loadSavedRun;
    private final DifficultyHelper.Difficulty startingDifficulty;

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
        SaveHelper.RunSaveData saveData = loadSavedRun ? SaveHelper.getInstance().load(hero2.getHeroClass()) : null;
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
        MapHelper.getInstance().setRestoringGeneratedLevels(saveData != null);
        GameHelper.GetSingleton().setCamera(new OrthographicCamera(width, height));
        stageUnits =  new Stage(new StretchViewport(width,  height, GameHelper.GetSingleton().getCamera()));
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


        stage = new Stage(new StretchViewport(width,  height, GameHelper.GetSingleton().getCamera()));

        if (touchControlsEnabled) {
            dpadStage = addTouchPad(unitHelper, width, height);
        }



        stageUnits.addActor(new AmbientSound());


        Effects effects = new Effects();
        stageUnits.addActor(effects);

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
        EffectsHelper.getInstance().addEffects(effects);
        RatKingHelper.getInstance().resetRunState();
        RatKingHelper.getInstance().ensureCompanionPresent();
        if (NightModeHelper.isNightModeActive()) {
            EffectsHelper.getInstance().message(hero2, "These dungeons are more dangerous at night", Color.WHITE, 0f);
        }

        stageParalex = new Stage( new StretchViewport(Gdx.graphics.getWidth() * ratio, Gdx.graphics.getHeight() * ratio, cameraPar));
        font = new BitmapFont();
        skin = new Skin();


        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode != Input.Keys.BACK && keycode != Input.Keys.ESCAPE) {
                    return false;
                }

                if (unitHelper.getHero() != null && unitHelper.getHero().isDead()) {
                    return true;
                }

                if (WindowHelper.getInstance().topWindow() instanceof InventoryWindow) {
                    WindowHelper.getInstance().addWindow(new PauseMenuWindow().build());
                    return true;
                }

                if (WindowHelper.getInstance().windowOpen() && WindowHelper.getInstance().handleKeyDown(keycode)) {
                    return true;
                }

                WindowHelper.getInstance().addWindow(new PauseMenuWindow().build());
                return true;
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
    }

    @Override
    public void init() {

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
        if(darknessAlpha > 0){
            darknessAlpha -= 0.25f * Math.min(0.1f, delta);

            if(darknessAlpha < 0){
                darknessAlpha = 0;
            }
          //  darkness.setAlpha(darknessAlpha);
        }

        MapHelper.getInstance().act(delta);

        if(!WindowHelper.getInstance().windowOpen()){
            if (desktopInputProcessor != null) {
                desktopInputProcessor.refreshMovementState();
            }
            stageUnits.act();
            unitHelper.act(delta);
        }

        welcome();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        OrthographicCamera worldCamera = GameHelper.GetSingleton().getCamera();
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
        uiCamera.update();
        batchUI.setProjectionMatrix(uiCamera.combined);
        batchUI.begin();

        if(!WindowHelper.getInstance().windowOpen()){
            UIHelper.getInstance().drawButtons(batchUI);
        }
        else {
            WindowHelper.getInstance().draw(batchUI);
        }

        batchUI.end();

        batchUI.begin();

        if(dpadStage != null && !WindowHelper.getInstance().windowOpen()){
            dpadStage.draw();
        }


        batchUI.end();

        act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
                disposeTouchPad();
        batch.dispose();
      //  UnitHelper.GetSingleton().getHero().Dispose();
        //TextureHelper.GetSingleton().dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (stageUnits != null) {
            stageUnits.getViewport().update(width, height, true);
        }

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }

        if (dpadStage != null) {
            dpadStage.getViewport().update(width, height, true);
        }

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
      //  EffectsHelper.GetSingleton().AddNews("Dying Forest");
    }

    float getCameraX(){
        if(MapHelper.getInstance().getWidth() < 20){
            return width / 2 - ConstantsHelper.TILE * (20 - MapHelper.getInstance().getWidth()) / 2;
        }
        return Math.min(Math.max(width / 2, unitHelper.getHeroX()), MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - width / 2);
    }

    float getCameraY() {
        Room activeRoom = MapHelper.getInstance().getActiveRoom();
        if (activeRoom == null) {
            return height / 2f;
        }

        float roomHeight = activeRoom.getHeight() * ConstantsHelper.TILE;
        return roomHeight < height ? roomHeight / 2f : height / 2f;
    }
}
