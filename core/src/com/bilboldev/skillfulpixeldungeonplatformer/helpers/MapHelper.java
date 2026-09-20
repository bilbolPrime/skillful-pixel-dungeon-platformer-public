package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Key;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomDisplayDepth;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomPlaneSelection;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomFixtureObservation;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomSnapshot;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomTransition;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomAppearanceCache;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Sign;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.PoolRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseRecord;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.CorpseTargeting;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.activeskills.CorpseActiveSkill;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.ContactShadow;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.caves.Caves;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.city.City;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.halls.Halls;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.prison.Prison;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers.Sewers;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelEntryDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.LevelExitDoor;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.DisturbableGraveProp;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Merchant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.traps.PlatformTrap;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.ItemWindow;
import com.bilboldev.skillfulpixeldungeonplatformer.windows.TextWindow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

public class MapHelper {
    private static final String PRISON_WALL_BLOOD_SPRITE = "images/misc/blood.png";
    private static final String SEWER_WALL_DECORATION_SPRITE = "images/tiles/sewers/decoration.png";
    private static final long PRISON_WALL_DECORATION_RANDOM_SALT = 0x5D7A4913B2C4E16L;
    private static final long SEWER_WALL_DECORATION_RANDOM_SALT = 0x0C7A3D55B1A4E29L;
    private static final int DEFAULT_WATER_THICKNESS = 10;
    private static final int DEFAULT_WATER_DENSITY_MULTIPLIER = 1;
    private static final int POOL_ROOM_WATER_DENSITY_MULTIPLIER = 2;
    private static final float SEWERS_DARKNESS_ALPHA = 0.10f;
    private static final float PRISON_DARKNESS_ALPHA = 0.20f;
    private static final float CAVES_DARKNESS_ALPHA = 0.30f;
    private static final float CITY_DARKNESS_ALPHA = 0.10f;
    private static final float HALLS_DARKNESS_ALPHA = 0.40f;
    private static final float CAVES_LINE_OF_SIGHT_EDGE_INSET = 1f;
    private static final float CAVES_HERO_EYE_HEIGHT_RATIO = 0.72f;
    private static final int CAVES_DARKNESS_STRIP_WIDTH = 2;
    private static final float DARKNESS_EDGE_FEATHER = 32f;
    private static final int MAX_WATER_MOTES = 8;
    private static final int MAX_WATER_SHIMMERS = 16;

    public final int MIN_FLOOR = 3;
    protected HashMap<Integer, Array<Integer>> floors;
    protected HashSet<String> platforms;
    protected Theme theme;
    protected int atDepth;
    protected LinkedList<Level> levels;
    protected Level level;
    private transient RoomSnapshot predecessorSnapshot;
    private final transient RoomTransition roomTransition = new RoomTransition();
    private final transient RoomAppearanceCache roomAppearances = new RoomAppearanceCache();
    private transient RoomPlaneSelection roomPlanes = RoomPlaneSelection.EMPTY;
    private final transient RoomFixtureObservation roomFixtureObservation = new RoomFixtureObservation();
    private boolean restoringGeneratedLevels;
    private final HashSet<Integer> shownChapterIntroDepths;
    private final HashMap<Integer, Integer> keyCountsByDepth;
    private boolean deferChapterIntroUntilTransitionBanner;
    private Runnable pendingChapterIntroAction;
    private GameSprite waterSurface;
    private GameSprite waterHighlight;
    private GameSprite waterMote, waterShimmer;
    private Room waterVisualRoom;
    private double waterVisualTime;
    private transient double backgroundVisualTime;
    private int ambientWaterDraws, waterMoteDraws, waterShimmerDraws, waterEmitterDraws, waterPipeDraws;
    private float waterViewLeft, waterViewRight, waterViewBottom, waterViewTop;
    private final float[] darknessVertices = new float[20];
    private GameSprite prisonWallDecoration;
    private GameSprite sewerWallDecoration;
    private final ArrayList<PlatformSpan> cachedVisibilityPlatformSpans;
    private final ArrayList<ShadowInterval> cachedShadowIntervals;
    private Room cachedVisibilityPlatformRoom;

    private static final MapHelper ourInstance = new MapHelper();

    public static MapHelper getInstance() {
        return ourInstance;
    }

    private MapHelper() {
        floors = new HashMap<>();
        platforms = new HashSet<>();
        levels = new LinkedList<>();
        shownChapterIntroDepths = new HashSet<>();
        keyCountsByDepth = new HashMap<>();
        cachedVisibilityPlatformSpans = new ArrayList<PlatformSpan>();
        cachedShadowIntervals = new ArrayList<ShadowInterval>();
        reloadVisualAssets();
    }

    public void reloadVisualAssets() {
        clearRoomPresentation();

        waterSurface = waterSprite(ConstantsHelper.TILE, DEFAULT_WATER_THICKNESS, 0.58f);
        waterHighlight = waterSprite(ConstantsHelper.TILE - 4f, 2f, 0.62f);
        waterMote = waterSprite(2f, 2f, 1f);
        waterShimmer = waterSprite(16f, 2f, 1f);
        waterVisualRoom = null;
        waterVisualTime = 0d;
        prisonWallDecoration = new GameSprite(PRISON_WALL_BLOOD_SPRITE, 24f, 24f, 0.9f);
        sewerWallDecoration = new GameSprite(SEWER_WALL_DECORATION_SPRITE, ConstantsHelper.TILE, ConstantsHelper.TILE, 1f);
    }

    private GameSprite waterSprite(float width, float height, float alpha) {
        GameSprite sprite = new GameSprite(new Sprite(TextureHelper.GetSingleton().getSolidPixel()), width, height);
        sprite.setAlpha(alpha);
        return sprite;
    }

    protected void generateMap(Theme theme){
        this.theme = theme;
        RandomHelper.getInstance().beginLevelGeneration(atDepth);
        try {
            level = this.theme.getLevel().generateLevel(atDepth);
        }
        finally {
            RandomHelper.getInstance().endLevelGeneration();
        }
        levels.add(level);
        calculateFloors();
    }

    public void enterDepth(int depth){
        if(atDepth == depth){
            return;
        }

        clearRoomPresentation();
        boolean goingDown = depth >= atDepth;
        if(levels.size() >= depth){
            level = levels.get(depth - 1);
            atDepth = depth;
            theme = resolveThemeForDepth(depth);
        }
        else{
            depth = levels.size() + 1;
            atDepth = depth;

            theme = resolveThemeForDepth(depth);

            generateMap(theme);
        }

        RatKingSupportHelper.getInstance().recordReachedDepth(atDepth);

        level.getEntryPoint(goingDown).open();
        UnitHelper.getInstance().getHero().setRoom(level.getAtRoom().getIdentifier());
        UnitHelper.getInstance().getHero().appear(level.getEntryPoint(goingDown).x + ConstantsHelper.UNIT_DIMENSIONS / 2, level.getEntryPoint(goingDown).y);
        UnitHelper.getInstance().getHero().floorY = UnitHelper.getInstance().getHero().y;
        UnitHelper.getInstance().getHero().getFriendlies();
        calculateFloors();
        PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
        showChapterIntroIfNeeded(goingDown);
        NecromancerMinion.transferFor(UnitHelper.getInstance().getHero());
    }

    private Theme resolveThemeForDepth(int depth) {
        if (depth < 6) {
            return new Sewers();
        }

        if (depth < 11) {
            return new Prison();
        }

        if (depth < 16) {
            return new Caves();
        }

        if (depth < 21) {
            return new City();
        }

        if (depth < 27) {
            return new Halls();
        }

        return theme != null ? theme : new Halls();
    }

    protected  void  clearFloors(){
        for(Map.Entry<Integer, Array<Integer>> kvp : floors.entrySet()){
            kvp.getValue().clear();
        }

        floors.clear();
    }

    public int getDepth(){
        return atDepth;
    }

    public int getGeneratedDepthCount() {
        return levels.size();
    }

    public void goUp(){
        int previousDepth = atDepth;
        enterDepth(atDepth - 1);
        if (atDepth != previousDepth) {
            UIHelper.getInstance().showDepthTransitionBanner(Messages.get("scenes.interlevelscene$mode.ascend") + "...");
        }
    }

    public void goDown(){
        int previousDepth = atDepth;
        int previousGeneratedDepthCount = levels.size();
        deferChapterIntroUntilTransitionBanner = true;
        try {
            enterDepth(atDepth + 1);
        }
        finally {
            deferChapterIntroUntilTransitionBanner = false;
        }
        if (atDepth != previousDepth) {
            boolean descendedToNewDepth = atDepth > previousGeneratedDepthCount;
            Runnable postTransitionAction = consumePendingChapterIntroAction();
            if (descendedToNewDepth) {
                final Runnable chainedAction = postTransitionAction;
                postTransitionAction = new Runnable() {
                    @Override
                    public void run() {
                        SoundHelper.GetSingleton().play(Sounds.DESCEND, 0f, 1f);
                        if (chainedAction != null) {
                            chainedAction.run();
                        }
                    }
                };
            }
            UIHelper.getInstance().showDepthTransitionBanner(Messages.get("scenes.interlevelscene$mode.descend") + "...", postTransitionAction);
            if (descendedToNewDepth) {
                AchievementManager.getInstance().onLevelCompleted(previousDepth);
            }
        }
        else {
            pendingChapterIntroAction = null;
        }
    }

    public void reset() {
        corpseArt = null;
        clearCorpses();
        clearRoomPresentation();
        clearFloors();
        platforms.clear();
        levels.clear();
        shownChapterIntroDepths.clear();
        keyCountsByDepth.clear();
        deferChapterIntroUntilTransitionBanner = false;
        pendingChapterIntroAction = null;
        theme = null;
        atDepth = 0;
        level = null;
        restoringGeneratedLevels = false;
    }


    public void clearCorpses() {
        for (Level retained : levels) for (Room room : retained.rooms) room.clearCorpses();
    }

    public int getCurrentDepthKeyCount() {
        return getKeyCountForDepth(atDepth);
    }

    public int getKeyCountForDepth(int depth) {
        if (depth < 1) {
            return 0;
        }

        Integer keyCount = keyCountsByDepth.get(depth);
        return keyCount == null ? 0 : Math.max(0, keyCount);
    }

    public boolean consumeKeyForCurrentDepth() {
        return consumeKeyForDepth(atDepth);
    }

    public HashMap<Integer, Integer> getKeyCountsByDepthSnapshot() {
        return new HashMap<>(keyCountsByDepth);
    }

    public void restoreKeyCountsByDepth(Map<Integer, Integer> savedKeyCountsByDepth) {
        keyCountsByDepth.clear();
        if (savedKeyCountsByDepth == null) {
            return;
        }

        for (Map.Entry<Integer, Integer> entry : savedKeyCountsByDepth.entrySet()) {
            Integer depth = entry.getKey();
            Integer keyCount = entry.getValue();
            if (depth != null && depth > 0 && keyCount != null && keyCount > 0) {
                keyCountsByDepth.put(depth, keyCount);
            }
        }
    }

    private void addKeyForCurrentDepth() {
        if (atDepth < 1) {
            return;
        }

        keyCountsByDepth.put(atDepth, getCurrentDepthKeyCount() + 1);
    }

    private boolean consumeKeyForDepth(int depth) {
        int keyCount = getKeyCountForDepth(depth);
        if (keyCount < 1) {
            return false;
        }

        if (keyCount == 1) {
            keyCountsByDepth.remove(depth);
        }
        else {
            keyCountsByDepth.put(depth, keyCount - 1);
        }

        return true;
    }

    private void collectKey(ItemOnScreen itemOnScreen, boolean refreshEnvironment) {
        if (itemOnScreen == null || !(itemOnScreen.getItem() instanceof Key)
                || !UnitHelper.getInstance().getUnits().contains(itemOnScreen)) {
            return;
        }

        addKeyForCurrentDepth();
        UIHelper.getInstance().showPickupNotice(Messages.get("custom.notice.key"), itemOnScreen.getItem().getGameSprite());
        itemOnScreen.pickedUp(refreshEnvironment);
    }

    public HashSet<Integer> getShownChapterIntroDepthsSnapshot() {
        return new HashSet<>(shownChapterIntroDepths);
    }

    public void restoreShownChapterIntroDepths(Iterable<Integer> depths) {
        shownChapterIntroDepths.clear();
        if (depths == null) {
            return;
        }

        for (Integer depth : depths) {
            if (depth != null) {
                shownChapterIntroDepths.add(depth);
            }
        }
    }

    private void showChapterIntroIfNeeded(boolean goingDown) {
        if (!goingDown || restoringGeneratedLevels || theme == null) {
            return;
        }

        Integer introDepth = theme.getChapterIntroDepth();
        if (introDepth == null || introDepth != atDepth || !shownChapterIntroDepths.add(atDepth)) {
            return;
        }

        String introText = theme.getChapterIntroStory();
        if (introText == null || introText.isEmpty()) {
            return;
        }

        Runnable introAction = new Runnable() {
            @Override
            public void run() {
                WindowHelper.getInstance().addWindow(new TextWindow(1500f, 360f, introText).build());
                SoundHelper.GetSingleton().play(Sounds.READ);
            }
        };

        if (deferChapterIntroUntilTransitionBanner) {
            pendingChapterIntroAction = introAction;
        }
        else {
            introAction.run();
        }
    }

    private Runnable consumePendingChapterIntroAction() {
        Runnable introAction = pendingChapterIntroAction;
        pendingChapterIntroAction = null;
        return introAction;
    }

    public boolean isRestoringGeneratedLevels() {
        return restoringGeneratedLevels;
    }

    public void setRestoringGeneratedLevels(boolean restoringGeneratedLevels) {
        this.restoringGeneratedLevels = restoringGeneratedLevels;
    }

    protected void calculateFloors(){

        clearFloors();
        Room floorRoom = level.getAtRoom();
        int floorColumns = floorRoom.isBossArena() ? (int) (5000 / ConstantsHelper.TILE + 1) : (int) floorRoom.getWidth();
        for(int i = 0; i < floorColumns; i++){
            if(!floors.containsKey(i))
            {
                floors.put(i, new Array<Integer>(true, 1000));
            }

            Array<Integer> tmp = floors.get(i);
            tmp.add(3);
            tmp.sort();

            floors.put(i, tmp);
        }

        for(String platform : level.getAtRoom().getPlatforms()){
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]);

            if (!floorRoom.isBossArena() && !com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomGeometry
                    .validPlatform(floorRoom, tileX, tileY)) continue;

            if(!floors.containsKey(tileX))
            {
                floors.put(tileX, new Array<Integer>(true, 1000));
            }

            Array<Integer> tmp = floors.get(tileX);
            tmp.add(tileY + 1);
            tmp.sort();

            floors.put(tileX, tmp);
        }
    }

    public float calculateFloorY(Unit unit){
        return calculateFloorY(unit.x, unit.y);
    }


    public float calculateFloorY(float x, float y){
        int calculatedTile = (int) (x / ConstantsHelper.TILE);
        int calculatedYTile = (int) (y / ConstantsHelper.TILE);



        float candidateFloor = MIN_FLOOR * ConstantsHelper.TILE;
        if(floors.containsKey(calculatedTile))
        {
            for(int a  = floors.get(calculatedTile).size - 1;a >= 0; a--)
            {
                if(floors.get(calculatedTile).get(a) <= calculatedYTile)
                {
                    candidateFloor = Math.max(candidateFloor, floors.get(calculatedTile).get(a) * ConstantsHelper.TILE);
                }
            }
        }



        if(x % ConstantsHelper.TILE > ConstantsHelper.TILE / 2){
            float edgeFloorY = calculateFloorY(x - x % ConstantsHelper.TILE + ConstantsHelper.TILE, y);
            if(edgeFloorY < candidateFloor){
                return edgeFloorY;
            }
        }


        if(x % ConstantsHelper.TILE > ConstantsHelper.TILE / 2){
            float edgeFloorY = calculateFloorY(x - x % ConstantsHelper.TILE + ConstantsHelper.TILE, y);
            if(edgeFloorY > candidateFloor){
                return edgeFloorY;
            }
        }

        return candidateFloor;
    }

    private final com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomBoundaryCoverage roomBoundaryCoverage =
            new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomBoundaryCoverage();

    public void draw(Batch batch){
        Room activeRoom = level.getAtRoom();
        roomBoundaryCoverage.draw(batch, theme, activeRoom, GameHelper.GetSingleton().getCamera());
        roomFixtureObservation.begin(activeRoom.getIdentifier());
        selectWaterVisualRoom(activeRoom);
        prepareWaterView(activeRoom);
        ambientWaterDraws = waterMoteDraws = waterShimmerDraws = waterEmitterDraws = waterPipeDraws = 0;
        HashSet<String> activePlatforms = activeRoom.getPlatforms();
        HashSet<String> waterPlatforms = activeRoom.getWaterPlatforms();
        HashSet<String> renderedWaterTiles = waterPlatforms;
        HashSet<String> prisonDecorationPlatforms = getPrisonDecorationPlatforms(activeRoom);
        HashSet<String> sewerDecorationPlatforms = getSewerDecorationPlatforms(activeRoom, waterPlatforms);

        for(int j = 0; j < activeRoom.getHeight(); j++) {
            for(int i = 0; i < activeRoom.getWidth(); i++) {
                String plat = UtilsHelper.platformKey(i, j);
                if(j > MIN_FLOOR - 1){
                   theme.drawWall(batch, activeRoom, atDepth, i, j);
                   if (prisonDecorationPlatforms.contains(UtilsHelper.platformKey(i, j - 1))) {
                       drawPrisonWallDecoration(batch, i, j);
                   }
                         theme.getFader().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                         theme.getFader().draw(batch);
                }

                if(j == MIN_FLOOR - 1){
                    theme.getWallFading().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getWallFading().draw(batch);

                    theme.getFader().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFader().draw(batch);

                    theme.getFloor().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFloor().draw(batch);

                }

            }
        }

        theme.drawArchitecture(batch, activeRoom, atDepth);
        theme.drawBossArena(batch, activeRoom);
        theme.drawSpecialRoomFocalPoint(batch, activeRoom);



        for (String sewerDecorationPlatform : sewerDecorationPlatforms) {
            String[] tile = sewerDecorationPlatform.split("_");
            int tileX = Integer.parseInt(tile[0]);
            int wallY = Integer.parseInt(tile[1]) + 1;
            if (wallY < MIN_FLOOR || wallY >= activeRoom.getHeight()) continue;
            drawSewerWallDecoration(batch, tileX, wallY);
        }


        for (int tileX = 0; tileX < activeRoom.getWidth(); tileX++) {
            if (renderedWaterTiles.contains(UtilsHelper.platformKey(tileX, MIN_FLOOR - 1)))
                drawWaterPlatform(batch, renderedWaterTiles, sewerDecorationPlatforms, tileX, MIN_FLOOR - 1);
        }

        drawCorpses(batch);


        for (int j = 0; j < activeRoom.getHeight(); j++) {
            for (int i = 0; i < activeRoom.getWidth(); i++) {
                String plat = UtilsHelper.platformKey(i, j);
                if(activePlatforms.contains(plat)){
                    theme.drawPlatform(batch, activeRoom, i, j);

                    if (renderedWaterTiles.contains(plat)) {
                        drawWaterPlatform(batch, renderedWaterTiles, sewerDecorationPlatforms, i, j);
                    }
                }
            }
        }

        for (String sewerDecorationPlatform : sewerDecorationPlatforms) {
            int tileX = Integer.parseInt(sewerDecorationPlatform.split("_")[0]);
            int tileY = Integer.parseInt(sewerDecorationPlatform.split("_")[1]);
            drawWaterDecorationCascade(batch,
                    tileX * ConstantsHelper.TILE,
                    tileY * ConstantsHelper.TILE,
                    tileX,
                    tileY);
        }

        for(Door door : activeRoom.getDoors()){
            door.draw(batch, 1f);
        }

        if(activeRoom.getSign() != null){
            ContactShadow.draw(batch, activeRoom.getSign().getX() + ConstantsHelper.TILE / 2f,
                    activeRoom.getSign().getY(), ConstantsHelper.TILE * 0.45f, activeRoom.getSign().getVisualAlpha(), false);
            theme.getSign().setPosition(activeRoom.getSign().getX(), activeRoom.getSign().getY());
            theme.getSign().setAlpha(activeRoom.getSign().getVisualAlpha());
            theme.getSign().draw(batch);
            activeRoom.getSign().rememberDisplayed(activeRoom.getIdentifier());
            theme.getSign().setAlpha(1f);
        }

        if(activeRoom.getStuff() != null){
            for(Unit something: activeRoom.getStuff()){
                something.draw(batch, 1f);
            }
        }
    }

    private GameSprite corpseArt;

    private void drawCorpses(Batch batch) {
        Hero hero = UnitHelper.getInstance().getHero();
        Room room = getActiveRoom();
        if (hero == null || hero.getHeroClass() != HeroClass.NECROMANCER
                || room == null || !room.getIdentifier().equals(hero.getRoom()) || room.getCorpses().isEmpty()) return;
        if (corpseArt == null) {
            corpseArt = new GameSprite(NewClassAssets.ItemArt.CORPSE.key(), CorpseRecord.ART_SIZE, CorpseRecord.ART_SIZE, 0.88f);
            corpseArt.setColor(new Color(0.84f, 0.86f, 0.82f, 1f));
        }
        for (CorpseRecord corpse : room.getCorpses()) {
            corpseArt.setPosition(corpse.x - CorpseRecord.HALF_WIDTH, corpse.y - CorpseRecord.BOTTOM_PADDING);
            corpseArt.draw(batch);
        }
        CorpseTargeting.Target selected = CorpseActiveSkill.markerTarget(hero);
        if (selected != null) {
            float packed = batch.getPackedColor();
            batch.setColor(.43f, .76f, .68f, batch.getColor().a * .72f);
            Texture pixel = TextureHelper.GetSingleton().getSolidPixel();
            for (int side = -1; side <= 1; side += 2) {
                float x = selected.x + side * 36f;
                batch.draw(pixel, x - 1f, selected.y + 7f, 2f, 11f);
                batch.draw(pixel, side < 0 ? x : x - 9f, selected.y + 7f, 9f, 2f);
            }
            batch.setPackedColor(packed);
        }
    }

    private void drawWaterPlatform(Batch batch, HashSet<String> waterPlatforms, HashSet<String> sewerDecorationPlatforms, int tileX, int tileY) {
        float platformX = tileX * ConstantsHelper.TILE;
        float platformY = tileY * ConstantsHelper.TILE;
        float surfaceY = platformY + ConstantsHelper.TILE + 4f;
        float floor = MIN_FLOOR * ConstantsHelper.TILE + 4f;
        if (!waterInView(platformX - 12f, floor, ConstantsHelper.TILE + 24f, surfaceY - floor + 96f)) return;
        Room activeRoom = getActiveRoom();
        int waterThickness = getWaterThickness(activeRoom);
        int waterDensityMultiplier = getWaterDensityMultiplier(activeRoom);
        Color previous = new Color(batch.getColor());

        batch.setColor(0.16f, 0.43f, 0.46f, previous.a);
        waterSurface.setHeight(waterThickness);
        waterSurface.setPosition(platformX, surfaceY - waterThickness);
        waterSurface.draw(batch);

        batch.setColor(0.55f, 0.78f, 0.77f, previous.a);
        waterHighlight.setPosition(platformX + 2f, surfaceY - 2f);
        waterHighlight.draw(batch);
        drawWaterAccents(batch, platformX, surfaceY, tileX, tileY);

        batch.setColor(0.38f, 0.8f, 1f, previous.a);
        if (tileY >= MIN_FLOOR && !waterPlatforms.contains(UtilsHelper.platformKey(tileX - 1, tileY))) {
            drawWaterSideSpill(batch,
                    platformX + 2f,
                    surfaceY - 2f,
                    tileX * 37 + tileY * 19,
                    waterDensityMultiplier);
        }

        if (tileY >= MIN_FLOOR && !waterPlatforms.contains(UtilsHelper.platformKey(tileX + 1, tileY))) {
            drawWaterSideSpill(batch,
                    platformX + ConstantsHelper.TILE - 5f,
                    surfaceY - 2f,
                    tileX * 53 + tileY * 11,
                    waterDensityMultiplier);
        }

        batch.setColor(0.88f, 0.98f, 1f, previous.a);
        drawWaterEdgeParticles(batch,
                platformX,
                platformY,
                tileX,
                tileY,
                waterDensityMultiplier);

        batch.setColor(previous);
        waterSurface.setHeight(DEFAULT_WATER_THICKNESS);
    }

    private void selectWaterVisualRoom(Room room) {
        if (waterVisualRoom != room) {
            waterVisualRoom = room;
            waterVisualTime = 0d;
        }
    }

    private void prepareWaterView(Room room) {
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        waterViewLeft = camera == null ? 0f : camera.position.x - camera.viewportWidth * camera.zoom / 2f;
        waterViewRight = camera == null ? room.getWidth() * ConstantsHelper.TILE
                : camera.position.x + camera.viewportWidth * camera.zoom / 2f;
        waterViewBottom = camera == null ? 0f : camera.position.y - camera.viewportHeight * camera.zoom / 2f;
        waterViewTop = camera == null ? room.getHeight() * ConstantsHelper.TILE
                : camera.position.y + camera.viewportHeight * camera.zoom / 2f;
    }

    private boolean waterInView(float x, float y, float width, float height) {
        return x < waterViewRight && x + width > waterViewLeft && y < waterViewTop && y + height > waterViewBottom;
    }

    private boolean waterSpriteInView(GameSprite sprite) {
        float width = sprite.getWidth() * sprite.getScaleX();
        float height = sprite.getHeight() * sprite.getScaleY();
        return waterInView(sprite.getX() + (sprite.getWidth() - width) / 2f,
                sprite.getY() + (sprite.getHeight() - height) / 2f, width, height);
    }

    private float waterPhase(long cycleMillis, int seed, float offset) {
        double cycle = waterVisualTime * 1000d / cycleMillis
                + Math.floorMod(seed * 91L, cycleMillis) / (double) cycleMillis + offset;
        return (float) (cycle - Math.floor(cycle));
    }

    private void drawWaterAccents(Batch batch, float x, float surfaceY, int tileX, int tileY) {
        int seed = waterVisualRoom.getIdentifier().hashCode() ^ tileX * 197 ^ tileY * 67;
        boolean reduced = GameSettingsHelper.getInstance().isReducedVisualEffects();
        float intensity = GameSettingsHelper.getInstance().getVisualEffectIntensity();
        float shimmerPhase = waterPhase(2600L, seed, 0f);
        float shimmerFade = (float) Math.sin(Math.PI * shimmerPhase);
        waterShimmer.setPosition(x + 6f + shimmerPhase * 88f, surfaceY - 4f);
        waterShimmer.setAlpha(0.65f * shimmerFade * shimmerFade * intensity);
        if (waterShimmerDraws < (reduced ? 8 : MAX_WATER_SHIMMERS) && waterSpriteInView(waterShimmer)) {
            waterShimmer.draw(batch);
            waterShimmerDraws++;
        }


        if ((seed & 1) != 0 || waterMoteDraws >= (reduced ? 3 : MAX_WATER_MOTES)) return;
        float phase = waterPhase(4800L, seed ^ 0x57415452, 0f);
        float drift = (float) Math.sin(phase * Math.PI * 2d) * 6f;
        waterMote.setPosition(x + 16f + deterministicRandom01(seed ^ 0x4D4F5445L) * 88f + drift,
                surfaceY + 10f + phase * 68f);
        waterMote.setAlpha((float) Math.sin(Math.PI * phase) * 0.26f * intensity);
        if (waterSpriteInView(waterMote)) {
            waterMote.draw(batch);
            waterMoteDraws++;
        }
    }

    private void drawWaterQuad(Batch batch, float x, float y, float width, float height,
                               float red, float green, float blue, float alpha) {
        if (alpha <= 0f || width <= 0f || height <= 0f || !waterInView(x, y, width, height)) return;
        float packed = batch.getPackedColor();
        batch.setColor(red, green, blue, batch.getColor().a * alpha);
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), x, y, width, height);
        batch.setPackedColor(packed);
        ambientWaterDraws++;
    }

    private int getWaterThickness(Room activeRoom) {
        return activeRoom.getWaterSurfaceThickness();
    }

    private int getWaterDensityMultiplier(Room activeRoom) {
        return activeRoom instanceof PoolRoom ? POOL_ROOM_WATER_DENSITY_MULTIPLIER : DEFAULT_WATER_DENSITY_MULTIPLIER;
    }

    private HashSet<String> getPrisonDecorationPlatforms(Room activeRoom) {
        HashSet<String> prisonDecorationPlatforms = new HashSet<String>();
        if (!(theme instanceof Prison) || activeRoom == null) {
            return prisonDecorationPlatforms;
        }

        HashSet<String> supportPlatforms = new HashSet<String>(activeRoom.getPlatforms());
        for (int tileX = 0; tileX < (int) activeRoom.getWidth(); tileX++) {
            supportPlatforms.add(UtilsHelper.platformKey(tileX, MIN_FLOOR - 1));
        }

        HashSet<String> blockedPlatforms = new HashSet<String>();
        for (Door door : activeRoom.getDoors()) {
            int tileX = (int) (door.x / ConstantsHelper.TILE);
            int tileY = Math.max(MIN_FLOOR - 1, (int) (door.y / ConstantsHelper.TILE) - 1);
            blockedPlatforms.add(UtilsHelper.platformKey(tileX, tileY));
        }

        Sign sign = activeRoom.getSign();
        if (sign != null) {
            int tileX = (int) (sign.getX() / ConstantsHelper.TILE);
            int tileY = Math.max(MIN_FLOOR - 1, (int) (sign.getY() / ConstantsHelper.TILE) - 1);
            blockedPlatforms.add(UtilsHelper.platformKey(tileX, tileY));
        }

        ArrayList<String> spanStarts = new ArrayList<String>();
        for (String supportPlatform : supportPlatforms) {
            int tileX = Integer.parseInt(supportPlatform.split("_")[0]);
            int tileY = Integer.parseInt(supportPlatform.split("_")[1]);
            if (!supportPlatforms.contains(UtilsHelper.platformKey(tileX - 1, tileY))) {
                spanStarts.add(supportPlatform);
            }
        }

        Collections.sort(spanStarts);
        RandomXS128 roomRandom = RandomHelper.getInstance().createRoomRandom(atDepth, activeRoom.getIdentifier(), PRISON_WALL_DECORATION_RANDOM_SALT);
        for (String spanStart : spanStarts) {
            int startTileX = Integer.parseInt(spanStart.split("_")[0]);
            int tileY = Integer.parseInt(spanStart.split("_")[1]);
            ArrayList<String> candidates = new ArrayList<String>();

            for (int tileX = startTileX; ; tileX++) {
                String platformKey = UtilsHelper.platformKey(tileX, tileY);
                if (!supportPlatforms.contains(platformKey)) {
                    break;
                }

                if (!blockedPlatforms.contains(platformKey)) {
                    candidates.add(platformKey);
                }
            }

            if (candidates.isEmpty()) {
                continue;
            }

            String firstDecoration = candidates.get(roomRandom.nextInt(candidates.size()));
            prisonDecorationPlatforms.add(firstDecoration);

            if (candidates.size() >= 5) {
                ArrayList<String> secondaryCandidates = new ArrayList<String>(candidates);
                secondaryCandidates.remove(firstDecoration);
                if (!secondaryCandidates.isEmpty()) {
                    prisonDecorationPlatforms.add(secondaryCandidates.get(roomRandom.nextInt(secondaryCandidates.size())));
                }
            }
        }

        return prisonDecorationPlatforms;
    }

    private HashSet<String> getSewerDecorationPlatforms(Room activeRoom, HashSet<String> waterPlatforms) {
        HashSet<String> sewerDecorationPlatforms = new HashSet<String>();
        if (!(theme instanceof Sewers) || activeRoom == null || waterPlatforms == null || waterPlatforms.isEmpty()) {
            return sewerDecorationPlatforms;
        }

        HashSet<String> blockedPlatforms = new HashSet<String>();
        for (Door door : activeRoom.getDoors()) {
            int tileX = (int) (door.x / ConstantsHelper.TILE);
            int tileY = Math.max(0, (int) (door.y / ConstantsHelper.TILE) - 1);
            blockedPlatforms.add(UtilsHelper.platformKey(tileX, tileY));
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof PlatformTrap) || unit.getRoom() == null || !unit.getRoom().equals(activeRoom.getIdentifier())) {
                continue;
            }

            int tileX = (int) (unit.x / ConstantsHelper.TILE);
            int tileY = Math.max(0, (int) (unit.floorY / ConstantsHelper.TILE) - 1);
            blockedPlatforms.add(UtilsHelper.platformKey(tileX, tileY));
        }

        ArrayList<String> spanStarts = new ArrayList<String>();
        for (String waterPlatform : waterPlatforms) {
            int tileX = Integer.parseInt(waterPlatform.split("_")[0]);
            int tileY = Integer.parseInt(waterPlatform.split("_")[1]);
            if (!waterPlatforms.contains(UtilsHelper.platformKey(tileX - 1, tileY))) {
                spanStarts.add(waterPlatform);
            }
        }

        Collections.sort(spanStarts);
        RandomXS128 roomRandom = RandomHelper.getInstance().createRoomRandom(atDepth, activeRoom.getIdentifier(), SEWER_WALL_DECORATION_RANDOM_SALT);
        for (String spanStart : spanStarts) {
            int startTileX = Integer.parseInt(spanStart.split("_")[0]);
            int tileY = Integer.parseInt(spanStart.split("_")[1]);
            ArrayList<String> candidates = new ArrayList<String>();

            for (int tileX = startTileX; ; tileX++) {
                String platformKey = UtilsHelper.platformKey(tileX, tileY);
                if (!waterPlatforms.contains(platformKey)) {
                    break;
                }

                if (!blockedPlatforms.contains(platformKey)) {
                    candidates.add(platformKey);
                }
            }

            if (!candidates.isEmpty()) {
                sewerDecorationPlatforms.add(candidates.get(roomRandom.nextInt(candidates.size())));
            }
        }

        return sewerDecorationPlatforms;
    }

    private void drawSewerWallDecoration(Batch batch, int tileX, int tileY) {
        sewerWallDecoration.setPosition(tileX * ConstantsHelper.TILE, tileY * ConstantsHelper.TILE);
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        float shade = 1f - theme.getFader().getAlpha();

        batch.setColor(parent.r * shade, parent.g * shade, parent.b * shade, parent.a);
        sewerWallDecoration.drawFeatheredEdges(batch, ConstantsHelper.TILE * 0.25f);
        roomFixtureObservation.pipe(sewerWallDecoration, (tileX + 0.5f) * ConstantsHelper.TILE,
                (tileY + 7f / 16f) * ConstantsHelper.TILE, tileY * ConstantsHelper.TILE + 4f);
        batch.setPackedColor(packed);
    }

    private void drawPrisonWallDecoration(Batch batch, int tileX, int tileY) {
        float platformX = tileX * ConstantsHelper.TILE;
        float platformY = tileY * ConstantsHelper.TILE + ConstantsHelper.TILE * 0.5f;
        int depthSeed = atDepth * 131 + tileX * 37 + tileY * 17;
        float rotation = -35f + deterministicRandom01(depthSeed) * 70f;
        float scale = 0.8f + deterministicRandom01(depthSeed + 11L) * 0.55f;
        float offsetX = -4f + deterministicRandom01(depthSeed + 23L) * 8f;
        float offsetY = -8f + deterministicRandom01(depthSeed + 47L) * 10f;

        prisonWallDecoration.setRotation(rotation);
        prisonWallDecoration.setScale(scale, scale);
        prisonWallDecoration.setPosition(platformX + offsetX, platformY + offsetY);
        prisonWallDecoration.draw(batch);
        prisonWallDecoration.setRotation(0f);
        prisonWallDecoration.setScale(1f, 1f);
    }

    private void drawWaterEdgeParticles(Batch batch, float platformX, float platformY, int tileX, int tileY,
                                        int densityMultiplier) {
        if (tileY < MIN_FLOOR) return;
        boolean reduced = GameSettingsHelper.getInstance().isReducedVisualEffects();
        int emitters = reduced ? 1 : densityMultiplier > 1 ? 3 : 2;
        for (int index = 0; index < emitters; index++) {
            float sourceX = platformX + ConstantsHelper.TILE * (index + 0.5f) / emitters;
            float sourceY = platformY + ConstantsHelper.TILE + 2f;
            int seed = tileX * 59 + tileY * 23 + index * 47;
            drawWaterStream(batch, sourceX, sourceY, waterCatchY(sourceX, sourceY), seed,
                    reduced ? 1 : 2, 2.5f, false);
        }
    }

    private void drawWaterSideSpill(Batch batch, float startX, float startY, int seed, int densityMultiplier) {
        boolean reduced = GameSettingsHelper.getInstance().isReducedVisualEffects();
        drawWaterStream(batch, startX, startY, waterCatchY(startX, startY), seed,
                reduced ? 2 : 3, densityMultiplier > 1 ? 4f : 3f, false);
    }

    private void drawWaterDecorationCascade(Batch batch, float platformX, float platformY, int tileX, int tileY) {

        float centerX = platformX + ConstantsHelper.TILE * 0.5f;
        float startY = platformY + ConstantsHelper.TILE * (1f + 7f / 16f);
        float impactY = platformY + ConstantsHelper.TILE + 4f;
        drawWaterStream(batch, centerX, startY, impactY, tileX * 97 + tileY * 61,
                GameSettingsHelper.getInstance().isReducedVisualEffects() ? 2 : 3, 4f, true);
    }


    private float waterCatchY(float x, float startY) {
        int column = (int) Math.floor(x / ConstantsHelper.TILE);
        int topRow = (int) Math.floor((startY - 4.01f) / ConstantsHelper.TILE) - 1;
        for (int row = topRow; row >= MIN_FLOOR; row--) {
            if (waterVisualRoom.getPlatforms().contains(UtilsHelper.platformKey(column, row))) {
                return (row + 1) * ConstantsHelper.TILE + 4f;
            }
        }
        return MIN_FLOOR * ConstantsHelper.TILE + 4f;
    }

    private void drawWaterStream(Batch batch, float x, float startY, float impactY, int seed,
                                 int drops, float width, boolean pipe) {
        float distance = startY - impactY;
        if (distance <= 0f || !waterInView(x - 16f, impactY, 32f, distance + 4f)) return;
        waterEmitterDraws++;
        if (pipe) waterPipeDraws++;
        float intensity = GameSettingsHelper.getInstance().getVisualEffectIntensity();
        long cycle = 700L + (long) (Math.sqrt(distance) * 22f) + Math.floorMod(seed, 170);

        for (int slot = 0; slot < drops; slot++) {
            float phase = waterPhase(cycle, seed, slot / (float) drops);
            if (phase < 0.76f) {
                float fall = phase / 0.76f;
                float headY = startY - distance * (0.12f * fall + 0.88f * fall * fall);
                float length = Math.min(startY - headY + 2f, 5f + 9f * fall);
                drawWaterQuad(batch, x - width / 2f, headY, width, length,
                        0.30f, 0.62f, 0.65f, 0.66f * intensity);
                drawWaterQuad(batch, x - width / 2f, headY, Math.max(1.5f, width * 0.55f), 2.5f,
                        0.67f, 0.86f, 0.83f, 0.78f * intensity);
            } else {
                drawWaterSplash(batch, x, impactY, (phase - 0.76f) / 0.24f, intensity);
            }
        }
    }

    private void drawWaterSplash(Batch batch, float x, float y, float progress, float intensity) {
        float radius = 2f + 10f * progress;
        float alpha = (1f - progress) * 0.55f * intensity;
        drawWaterQuad(batch, x - radius - 3f, y + 1f, 4f, 1.5f, 0.57f, 0.79f, 0.77f, alpha);
        drawWaterQuad(batch, x + radius - 1f, y + 1f, 4f, 1.5f, 0.57f, 0.79f, 0.77f, alpha);
        if (GameSettingsHelper.getInstance().isReducedVisualEffects()) return;
        float arc = 7f * 4f * progress * (1f - progress);
        drawWaterQuad(batch, x - radius, y + arc + 2f, 2f, 2f, 0.39f, 0.70f, 0.71f, alpha);
        drawWaterQuad(batch, x + radius, y + arc + 2f, 2f, 2f, 0.39f, 0.70f, 0.71f, alpha);
    }

    private float deterministicRandom01(long seed) {
        long mixed = seed;
        mixed ^= mixed >>> 33;
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= mixed >>> 33;
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= mixed >>> 33;
        return ((mixed >>> 40) & 0xFFFFFFL) / 16777216f;
    }

    public int getWidth(){
        return (int) level.getAtRoom().getWidth();
    }

    public int getHeight(){
        return (int) level.getAtRoom().getHeight() - 1;
    }

    public Theme getTheme(){
        return theme;
    }

    public boolean isBossLevelActive() {
        return level != null && level.getClass().getSimpleName().endsWith("BossLevel");
    }

    public boolean shouldUsePlatformSightLines() {
        return getVisibilityDarknessAlpha() > 0f;
    }

    public boolean shouldRenderCavesDarkness() {
        return GameSettingsHelper.getInstance().isPlatformShadowsEnabled()
                && getVisibilityDarknessAlpha() > 0f;
    }

    public void drawVisibilityMask(Batch batch) {
        if (!GameSettingsHelper.getInstance().isPlatformShadowsEnabled()) {
            return;
        }

        float darknessAlpha = getVisibilityDarknessAlpha();
        if (darknessAlpha <= 0f) {
            return;
        }

        Room activeRoom = getActiveRoom();
        Unit hero = UnitHelper.getInstance().getHero();
        if (activeRoom == null || hero == null || hero.getRoom() == null || !hero.getRoom().equals(activeRoom.getIdentifier())) {
            drawFullRoomDarkness(batch, activeRoom);
            return;
        }

        Color previousColor = new Color(batch.getColor());
        batch.setColor(0f, 0f, 0f, previousColor.a * darknessAlpha);
        drawLineSampledDarkness(batch, activeRoom, hero);
        batch.setColor(previousColor);
    }

    public boolean heroCanSeeWallPoint(Room room, float x, float y) {
        Unit hero = UnitHelper.getInstance().getHero();
        return hero != null && room != null && room.getIdentifier().equals(hero.getRoom())
                && (!shouldUsePlatformSightLines() || hasPlatformLineOfSight(room,
                hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                hero.y + ConstantsHelper.UNIT_DIMENSIONS * CAVES_HERO_EYE_HEIGHT_RATIO, x, y));
    }

    public boolean hasPlatformLineOfSight(Room room, float startX, float startY, float endX, float endY) {
        if (room == null) {
            return false;
        }

        if (Math.abs(endY - startY) < CAVES_LINE_OF_SIGHT_EDGE_INSET) {
            return true;
        }

        float minY = Math.min(startY, endY) + CAVES_LINE_OF_SIGHT_EDGE_INSET;
        float maxY = Math.max(startY, endY) - CAVES_LINE_OF_SIGHT_EDGE_INSET;
        for (PlatformSpan platformSpan : getVisibilityPlatformSpans(room)) {
            if (platformSpan.topY <= minY || platformSpan.topY >= maxY) {
                continue;
            }

            float intersectionProgress = (platformSpan.topY - startY) / (endY - startY);
            if (intersectionProgress <= 0f || intersectionProgress >= 1f) {
                continue;
            }

            float intersectionX = startX + (endX - startX) * intersectionProgress;
            if (intersectionX >= platformSpan.leftX && intersectionX <= platformSpan.rightX) {
                return false;
            }
        }

        return true;
    }

    public Room getActiveRoom() {
        return level.getAtRoom();
    }


    public RoomDisplayDepth getRoomDisplayDepth() {
        return RoomDisplayDepth.from(level);
    }

    public RoomSnapshot getPredecessorSnapshot() {
        if (!GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) return null;
        if (predecessorSnapshot != null) {
            Room active = level == null ? null : level.getAtRoom(), source = null;
            if (level != null && level.rooms != null) for (int i = 0; i < level.rooms.size(); i++) {
                Room room = level.rooms.get(i);
                if (room == null || room.getIdentifier() == null || !room.getIdentifier().equals(predecessorSnapshot.roomIdentifier)) continue;
                if (source != null) { source = null; break; }
                source = room;
            }
            if (predecessorSnapshot.floor != atDepth || !predecessorSnapshot.isValidFor(source, active)) {

                clearRoomPresentation();
            }
        }
        return predecessorSnapshot;
    }

    public RoomTransition getRoomTransition() { return roomTransition; }
    public RoomFixtureObservation getRoomFixtureObservation() { return roomFixtureObservation; }

    public RoomPlaneSelection getRoomPlaneSelection() {
        return getPredecessorSnapshot() == null ? RoomPlaneSelection.EMPTY : roomPlanes;
    }

    public RoomSnapshot getSecondaryRoomSnapshot() {
        RoomPlaneSelection selection = getRoomPlaneSelection();
        RoomSnapshot secondary = roomAppearances.get(selection.secondaryIdentifier);
        if (secondary == null) return null;
        Room near = uniquePresentationRoom(selection.primaryIdentifier), far = uniquePresentationRoom(selection.secondaryIdentifier);
        return secondary.floor == atDepth && secondary.matchesSource(far)
                && RoomDisplayDepth.reciprocallyConnected(near, far) ? secondary : null;
    }

    private Room uniquePresentationRoom(String identifier) {
        if (identifier == null || level == null) return null;
        Room found = null;
        for (Room room : level.rooms) if (identifier.equals(room.getIdentifier())) {
            if (found != null) return null;
            found = room;
        }
        return found;
    }

    public double getBackgroundVisualTime() { return backgroundVisualTime; }

    public RoomSnapshot getCachedRoomAppearance(String identifier) { return roomAppearances.get(identifier); }

    public RoomSnapshot getCachedRoomAppearanceAt(int slot) { return roomAppearances.at(slot); }


    public int getRetainedRoomAppearanceCount() {
        int count = roomAppearances.size();
        if (predecessorSnapshot != null && !roomAppearances.contains(predecessorSnapshot)) count++;
        RoomSnapshot retiring = roomTransition.getOutgoingBackdrop();
        if (retiring != null && retiring != predecessorSnapshot && !roomAppearances.contains(retiring)) count++;
        return count;
    }


    public void clearRoomAppearances() {
        predecessorSnapshot = null;
        roomTransition.clearAppearances();
        roomAppearances.clear();
        roomPlanes = RoomPlaneSelection.EMPTY;
        roomFixtureObservation.clear();
        backgroundVisualTime = 0d;
    }


    public void clearRoomPresentation() {
        clearRoomAppearances();
        roomTransition.clear();
        cachedVisibilityPlatformRoom = null;
        cachedVisibilityPlatformSpans.clear();
    }

    private RoomSnapshot prepareOutgoingSnapshot(Door door, RoomDisplayDepth displayDepth) {
        if (!GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()
                || door.otherDoor == null || door.getLeadsTo() == null || door.isCaged()
                || (door.isLocked() && (!door.requiresKey() || getCurrentDepthKeyCount() == 0))) return null;
        return RoomSnapshot.capture(level.getAtRoom(), door, atDepth, displayDepth);
    }

    private void drawFullRoomDarkness(Batch batch, Room activeRoom) {
        if (activeRoom == null) {
            return;
        }

        float darknessAlpha = getVisibilityDarknessAlpha();
        if (darknessAlpha <= 0f) {
            return;
        }

        Color previousColor = new Color(batch.getColor());
        batch.setColor(0f, 0f, 0f, previousColor.a * darknessAlpha);
        drawDarknessRect(batch,
                0,
                0,
            (int) (activeRoom.getWidth() * ConstantsHelper.TILE),
            (int) (activeRoom.getHeight() * ConstantsHelper.TILE));
        batch.setColor(previousColor);
    }

    private float getVisibilityDarknessAlpha() {
        if (isBossLevelActive() || theme == null) {
            return 0f;
        }

        if (theme instanceof Sewers) {
            return SEWERS_DARKNESS_ALPHA;
        }

        if (theme instanceof Prison) {
            return PRISON_DARKNESS_ALPHA;
        }

        if (theme instanceof Caves) {
            return CAVES_DARKNESS_ALPHA;
        }

        if (theme instanceof City) {
            return CITY_DARKNESS_ALPHA;
        }

        if (theme instanceof Halls) {
            return HALLS_DARKNESS_ALPHA;
        }

        return 0f;
    }

    private void drawLineSampledDarkness(Batch batch, Room activeRoom, Unit hero) {
        float heroEyeX = hero.x + ConstantsHelper.UNIT_DIMENSIONS * 0.5f;
        float heroEyeY = hero.y + ConstantsHelper.UNIT_DIMENSIONS * CAVES_HERO_EYE_HEIGHT_RATIO;
        int roomPixelWidth = (int) (activeRoom.getWidth() * ConstantsHelper.TILE);
        int roomPixelHeight = (int) (activeRoom.getHeight() * ConstantsHelper.TILE);

        for (int stripStartX = 0; stripStartX < roomPixelWidth; stripStartX += CAVES_DARKNESS_STRIP_WIDTH) {
            int stripWidth = Math.min(CAVES_DARKNESS_STRIP_WIDTH, roomPixelWidth - stripStartX);
            drawLineSampledDarknessColumn(batch,
                    activeRoom,
                    heroEyeX,
                    heroEyeY,
                    stripStartX,
                    stripWidth,
                    roomPixelHeight);
        }
    }

    private void drawLineSampledDarknessColumn(Batch batch, Room activeRoom, float heroEyeX, float heroEyeY,
                                               int stripStartX, int stripWidth, int roomPixelHeight) {
        float sampleX = stripStartX + stripWidth * 0.5f;
        collectShadowIntervals(activeRoom, heroEyeX, heroEyeY, sampleX, roomPixelHeight);
        if (cachedShadowIntervals.isEmpty()) {
            return;
        }

        mergeShadowIntervals();
        for (ShadowInterval shadowInterval : cachedShadowIntervals) {
            float startY = Math.round(shadowInterval.startY);
            float endY = Math.min(roomPixelHeight, startY + Math.round(shadowInterval.endY - shadowInterval.startY));
            float feather = Math.min(DARKNESS_EDGE_FEATHER, (endY - startY) / 2f);
            float lower = startY > 0f ? feather : 0f;
            float upper = endY < roomPixelHeight ? feather : 0f;
            float alpha = batch.getColor().a;

            drawDarknessGradient(batch, stripStartX, startY, stripWidth, lower, 0f, alpha);
            drawDarknessGradient(batch, stripStartX, startY + lower, stripWidth, endY - startY - lower - upper, alpha, alpha);
            drawDarknessGradient(batch, stripStartX, endY - upper, stripWidth, upper, alpha, 0f);
        }
    }

    private void collectShadowIntervals(Room room, float heroEyeX, float heroEyeY, float sampleX, int roomPixelHeight) {
        cachedShadowIntervals.clear();

        for (PlatformSpan platformSpan : getVisibilityPlatformSpans(room)) {
            if (Math.abs(platformSpan.topY - heroEyeY) <= CAVES_LINE_OF_SIGHT_EDGE_INSET) {
                continue;
            }

            ShadowInterval shadowInterval = projectShadowInterval(platformSpan, heroEyeX, heroEyeY, sampleX, roomPixelHeight);
            if (shadowInterval != null) {
                cachedShadowIntervals.add(shadowInterval);
            }
        }
    }

    private ShadowInterval projectShadowInterval(PlatformSpan platformSpan, float heroEyeX, float heroEyeY, float sampleX,
                                                 int roomPixelHeight) {
        float corridorMinX = Math.min(heroEyeX, sampleX);
        float corridorMaxX = Math.max(heroEyeX, sampleX);
        float overlapMinX = Math.max(corridorMinX, platformSpan.leftX);
        float overlapMaxX = Math.min(corridorMaxX, platformSpan.rightX);
        if (overlapMaxX < overlapMinX) {
            return null;
        }

        if (Math.abs(sampleX - heroEyeX) <= CAVES_LINE_OF_SIGHT_EDGE_INSET) {
            if (heroEyeX < platformSpan.leftX || heroEyeX > platformSpan.rightX) {
                return null;
            }

            if (platformSpan.topY > heroEyeY) {
                return new ShadowInterval(platformSpan.topY, roomPixelHeight);
            }

            return new ShadowInterval(0f, platformSpan.topY);
        }

        boolean sampleRightOfHero = sampleX > heroEyeX;
        float nearX = sampleRightOfHero ? overlapMaxX : overlapMinX;
        float farX = sampleRightOfHero ? overlapMinX : overlapMaxX;

        float nearY = Math.abs(nearX - sampleX) <= CAVES_LINE_OF_SIGHT_EDGE_INSET
                ? platformSpan.topY
                : projectShadowY(heroEyeX, heroEyeY, sampleX, platformSpan.topY, nearX);
        float farY = Math.abs(farX - heroEyeX) <= CAVES_LINE_OF_SIGHT_EDGE_INSET
                ? (platformSpan.topY > heroEyeY ? roomPixelHeight : 0f)
                : projectShadowY(heroEyeX, heroEyeY, sampleX, platformSpan.topY, farX);

        float intervalStart = Math.max(0f, Math.min(nearY, farY));
        float intervalEnd = Math.min(roomPixelHeight, Math.max(nearY, farY));
        if (intervalEnd - intervalStart <= CAVES_LINE_OF_SIGHT_EDGE_INSET) {
            return null;
        }

        return new ShadowInterval(intervalStart, intervalEnd);
    }

    private float projectShadowY(float heroEyeX, float heroEyeY, float sampleX, float platformTopY, float platformX) {
        return heroEyeY + (sampleX - heroEyeX) * (platformTopY - heroEyeY) / (platformX - heroEyeX);
    }

    private void mergeShadowIntervals() {
        Collections.sort(cachedShadowIntervals, new Comparator<ShadowInterval>() {
            @Override
            public int compare(ShadowInterval left, ShadowInterval right) {
                return Float.compare(left.startY, right.startY);
            }
        });

        int writeIndex = 0;
        for (int readIndex = 1; readIndex < cachedShadowIntervals.size(); readIndex++) {
            ShadowInterval merged = cachedShadowIntervals.get(writeIndex);
            ShadowInterval candidate = cachedShadowIntervals.get(readIndex);
            if (candidate.startY <= merged.endY + CAVES_LINE_OF_SIGHT_EDGE_INSET) {
                merged.endY = Math.max(merged.endY, candidate.endY);
                continue;
            }

            writeIndex++;
            if (writeIndex != readIndex) {
                cachedShadowIntervals.set(writeIndex, candidate);
            }
        }

        while (cachedShadowIntervals.size() > writeIndex + 1) {
            cachedShadowIntervals.remove(cachedShadowIntervals.size() - 1);
        }
    }

    private ArrayList<PlatformSpan> getVisibilityPlatformSpans(Room room) {
        if (room == null) {
            cachedVisibilityPlatformSpans.clear();
            cachedVisibilityPlatformRoom = null;
            return cachedVisibilityPlatformSpans;
        }

        if (room == cachedVisibilityPlatformRoom) {
            return cachedVisibilityPlatformSpans;
        }

        cachedVisibilityPlatformSpans.clear();
        cachedVisibilityPlatformRoom = room;

        HashMap<Integer, ArrayList<Integer>> rows = new HashMap<Integer, ArrayList<Integer>>();
        for (String platform : room.getPlatforms()) {
            int separatorIndex = platform.indexOf('_');
            int tileX = Integer.parseInt(platform.substring(0, separatorIndex));
            int tileY = Integer.parseInt(platform.substring(separatorIndex + 1));

            if (!rows.containsKey(tileY)) {
                rows.put(tileY, new ArrayList<Integer>());
            }

            rows.get(tileY).add(tileX);
        }

        for (Map.Entry<Integer, ArrayList<Integer>> row : rows.entrySet()) {
            ArrayList<Integer> tileXs = row.getValue();
            Collections.sort(tileXs);
            if (tileXs.isEmpty()) {
                continue;
            }

            int startTileX = tileXs.get(0);
            int previousTileX = startTileX;
            for (int index = 1; index < tileXs.size(); index++) {
                int tileX = tileXs.get(index);
                if (tileX == previousTileX + 1) {
                    previousTileX = tileX;
                    continue;
                }

                cachedVisibilityPlatformSpans.add(new PlatformSpan(startTileX, previousTileX, row.getKey()));
                startTileX = tileX;
                previousTileX = tileX;
            }

            cachedVisibilityPlatformSpans.add(new PlatformSpan(startTileX, previousTileX, row.getKey()));
        }

        return cachedVisibilityPlatformSpans;
    }

    private void drawDarknessRect(Batch batch, int x, int y, int width, int height) {
        drawDarknessGradient(batch, x, y, width, height, batch.getColor().a, batch.getColor().a);
    }

    private void drawDarknessGradient(Batch batch, float x, float y, float width, float height,
                                      float bottomAlpha, float topAlpha) {
        if (width <= 0 || height <= 0) {
            return;
        }

        float bottom = Color.toFloatBits(0f, 0f, 0f, bottomAlpha);
        float top = Color.toFloatBits(0f, 0f, 0f, topAlpha);
        darknessVertices[0] = x; darknessVertices[1] = y; darknessVertices[2] = bottom;
        darknessVertices[3] = 0f; darknessVertices[4] = 1f;
        darknessVertices[5] = x; darknessVertices[6] = y + height; darknessVertices[7] = top;
        darknessVertices[8] = 0f; darknessVertices[9] = 0f;
        darknessVertices[10] = x + width; darknessVertices[11] = y + height; darknessVertices[12] = top;
        darknessVertices[13] = 1f; darknessVertices[14] = 0f;
        darknessVertices[15] = x + width; darknessVertices[16] = y; darknessVertices[17] = bottom;
        darknessVertices[18] = 1f; darknessVertices[19] = 1f;
        batch.draw(TextureHelper.GetSingleton().getSolidPixel(), darknessVertices, 0, darknessVertices.length);
    }

    private static final class PlatformSpan {
        private final float leftX;
        private final float rightX;
        private final float topY;

        private PlatformSpan(int startTileX, int endTileX, int tileY) {
            leftX = startTileX * ConstantsHelper.TILE + CAVES_LINE_OF_SIGHT_EDGE_INSET;
            rightX = (endTileX + 1) * ConstantsHelper.TILE - CAVES_LINE_OF_SIGHT_EDGE_INSET;
            topY = (tileY + 1f) * ConstantsHelper.TILE;
        }
    }

    private static final class ShadowInterval {
        private final float startY;
        private float endY;

        private ShadowInterval(float startY, float endY) {
            this.startY = startY;
            this.endY = endY;
        }
    }

    public void checkEnvironment(int tileX, int tileY){
        boolean showingPickup = checkItems(tileX, tileY);
        checkDoors(tileX, tileY);

        if (showingPickup) {
            UIHelper.getInstance().disableSignButton();
            UIHelper.getInstance().disableInteractButton();
            return;
        }

        if (checkSign(tileX, tileY)) {
            UIHelper.getInstance().disableInteractButton();
            return;
        }

        checkPeople(tileX, tileY);
    }

    public int getHeroTileX() {
        Unit hero = UnitHelper.getInstance().getHero();
        if (hero == null) {
            return 0;
        }

        return (int) ((hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f) / ConstantsHelper.TILE);
    }

    public int getHeroTileY() {
        Unit hero = UnitHelper.getInstance().getHero();
        if (hero == null) {
            return 0;
        }

        return (int) (hero.y / ConstantsHelper.TILE);
    }

    public void refreshHeroEnvironment() {
        if (UnitHelper.getInstance().getHero() == null) {
            return;
        }

        checkEnvironment(getHeroTileX(), getHeroTileY());
    }

    private static final float INTERACTION_MARGIN = ConstantsHelper.TILE * 0.25f;
    private static final float INTERACTION_HYSTERESIS = 8f;
    private transient Object selectedContextTarget;
    private transient Door selectedDoorTarget;
    private transient Unit interactionHero;
    private transient Room interactionRoom;
    private transient int interactionPlacement;

    private boolean isHeroOverItem(ItemOnScreen itemOnScreen, int heroTileY) {
        return isHeroOverItem(itemOnScreen, INTERACTION_MARGIN);
    }

    private boolean isHeroOverItem(ItemOnScreen itemOnScreen, float margin) {
        Unit hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.showOnly() || itemOnScreen == null || !itemOnScreen.isPlaced()
                || itemOnScreen.isDead() || itemOnScreen.getItem() == null) {
            return false;
        }

        float heroLeft = hero.x;
        float heroRight = hero.x + ConstantsHelper.UNIT_DIMENSIONS;
        float itemLeft = itemOnScreen.getInteractionX();
        float itemRight = itemLeft + itemOnScreen.getInteractionWidth();
        return heroRight + margin >= itemLeft && heroLeft - margin <= itemRight
                && Math.abs(itemOnScreen.y - itemOnScreen.getInteractionFloorY()) <= INTERACTION_MARGIN
                && PhysicsHelper.getInstance().canReachInteraction(hero, (itemLeft + itemRight) / 2f,
                itemOnScreen.getInteractionFloorY());
    }

    private boolean canReachInteractionArea(float x, float floorY, float width) {
        return canReachInteractionArea(x, floorY, width, INTERACTION_MARGIN);
    }

    private boolean canReachInteractionArea(float x, float floorY, float width, float margin) {
        Unit hero = UnitHelper.getInstance().getHero();
        if (hero == null || hero.showOnly()) return false;
        float centerX = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        return centerX >= x - margin && centerX <= x + width + margin
                && PhysicsHelper.getInstance().canReachInteraction(hero, x + width / 2f, floorY);
    }

    private float interactionDistance(Unit target) {
        float width = target instanceof ItemOnScreen ? ((ItemOnScreen) target).getInteractionWidth()
                : target instanceof Door ? ConstantsHelper.TILE : ConstantsHelper.UNIT_DIMENSIONS;
        return Math.abs(target.x + width / 2f - UnitHelper.getInstance().getHero().x - ConstantsHelper.UNIT_DIMENSIONS / 2f);
    }

    private boolean nearerInteraction(Unit candidate, Unit current) {
        if (current == null) return true;
        int distance = Float.compare(interactionDistance(candidate), interactionDistance(current));
        if (distance != 0) return distance < 0;
        int xOrder = Float.compare(candidate.x, current.x);
        if (xOrder != 0) return xOrder < 0;
        int yOrder = Float.compare(candidate.y, current.y);
        if (yOrder != 0) return yOrder < 0;
        return String.valueOf(candidate.getPersistentId()).compareTo(String.valueOf(current.getPersistentId())) < 0;
    }

    private ItemOnScreen getItemAt(int tileX, int tileY) {
        ItemOnScreen firstMatchingItem = null;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit.getRoom() == null || !unit.getRoom().equals(getActiveRoomIdentifier())) {
                continue;
            }

            if (unit instanceof ItemOnScreen) {
                ItemOnScreen itemOnScreen = (ItemOnScreen) unit;
                if (isHeroOverItem(itemOnScreen, tileY)) {
                    boolean automatic = shouldAutoPickup(itemOnScreen);
                    boolean previousAutomatic = shouldAutoPickup(firstMatchingItem);
                    if (firstMatchingItem == null || automatic && !previousAutomatic
                            || automatic == previousAutomatic && nearerInteraction(itemOnScreen, firstMatchingItem)) {
                        firstMatchingItem = itemOnScreen;
                    }
                }
            }
        }

        return firstMatchingItem;
    }

    private Interactable getInteractableAt(int tileX, int tileY) {
        Interactable nearest = null;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit.getRoom() == null || !unit.getRoom().equals(getActiveRoomIdentifier())) {
                continue;
            }

            if (unit instanceof Interactable && !unit.isDead()
                    && canReachInteractionArea(unit.x, unit.y, ConstantsHelper.UNIT_DIMENSIONS)) {
                Interactable interactable = (Interactable) unit;
                if (interactable.canInteract() && nearerInteraction(interactable, nearest)) {
                    nearest = interactable;
                }
            }
        }

        return nearest;
    }

    private Door getDoorAt(int tileX, int tileY) {
        Door nearest = null;
        for (Door door : level.getAtRoom().getDoors()) {
            if (!door.isDead() && hasInteractionDestination(door)
                    && canReachInteractionArea(door.x, door.y, ConstantsHelper.TILE) && nearerInteraction(door, nearest)) {
                nearest = door;
            }
        }

        return nearest;
    }

    private boolean hasInteractionDestination(Door door) {

        return door instanceof LevelEntryDoor || door instanceof LevelExitDoor
                || door.otherDoor != null && door.getLeadsTo() != null && getRoom(door.getLeadsTo()) != null;
    }

    private boolean hasSignAt(int tileX, int tileY) {
        return level.getAtRoom().getSign() != null
                && level.getAtRoom().getSign().isReadable()
                && canReachInteractionArea(level.getAtRoom().getSign().getX(), level.getAtRoom().getSign().getY(), ConstantsHelper.TILE);
    }

    private boolean checkItems(int tileX, int tileY){
        ItemOnScreen itemOnScreen = getItemAt(tileX, tileY);
        while (itemOnScreen != null && shouldAutoPickup(itemOnScreen)) {
            if (itemOnScreen.getItem() instanceof Key) {
                collectKey(itemOnScreen, false);
            }
            else {
                itemOnScreen.pickedUp(false);
            }
            itemOnScreen = getItemAt(tileX, tileY);
        }

        Object selected = selectContextTarget();
        if (selected instanceof ItemOnScreen) {
            UIHelper.getInstance().showPickupButton((ItemOnScreen) selected);
            return true;
        }

        UIHelper.getInstance().disablePickupButton();
        return false;
    }

    private boolean shouldAutoPickup(ItemOnScreen itemOnScreen) {
        return itemOnScreen != null
                && (itemOnScreen.getItem() instanceof Gold || itemOnScreen.getItem() instanceof Key);
    }

    private boolean checkPeople(int tileX, int tileY){
        Object selected = selectContextTarget();
        if (selected instanceof Interactable) {
            UIHelper.getInstance().showInteractButton(((Interactable) selected).getInteractGS());
            return true;
        }

        UIHelper.getInstance().disableInteractButton();
        return false;
    }

    private boolean checkDoors(int tileX, int tileY){
        Door door = selectDoorTarget();
        if (door != null) {
            door.showOption();
            return true;
        }

        UIHelper.getInstance().disableDoorButton();
        return false;
    }

    private boolean checkSign(int tileX, int tileY){
        if(level.getAtRoom().getSign() == null){
            UIHelper.getInstance().disableSignButton();
            return false;
        }

        if(selectContextTarget() instanceof Sign){
             UIHelper.getInstance().showSignButton();
            return true;
        }

        UIHelper.getInstance().disableSignButton();
        return false;
    }

    public boolean performContextAction() {
        if (WindowHelper.getInstance().windowOpen()) return false;
        InteractionPrompt resolved = getContextPrompt();
        Object target = resolved == null ? null : resolved.target;
        if (target instanceof ItemOnScreen) {
            openItemWindow((ItemOnScreen) target);
            return true;
        }
        if (target instanceof Sign) {
            ((Sign) target).read();
            return true;
        }
        if (target instanceof Interactable) {
            ((Interactable) target).interact();
            return true;
        }
        return false;
    }


    private boolean prepareInteractionSelection() {
        Unit hero = UnitHelper.getInstance().getHero();
        Room room = level == null ? null : level.getAtRoom();
        if (hero == null || room == null || hero.isDead() || hero.showOnly()) {
            selectedContextTarget = null;
            selectedDoorTarget = null;
            interactionHero = null;
            interactionRoom = null;
            return false;
        }
        if (hero != interactionHero || room != interactionRoom
                || hero.getPresentationPlacementVersion() != interactionPlacement) {
            selectedContextTarget = null;
            selectedDoorTarget = null;
            interactionHero = hero;
            interactionRoom = room;
            interactionPlacement = hero.getPresentationPlacementVersion();
        }
        return true;
    }

    private boolean eligibleInteraction(Object target, float margin) {
        if (target instanceof Sign) {
            Sign sign = (Sign) target;
            return sign == level.getAtRoom().getSign() && sign.isReadable()
                    && canReachInteractionArea(sign.getX(), sign.getY(), ConstantsHelper.TILE, margin);
        }
        if (!(target instanceof Unit)) return false;
        Unit unit = (Unit) target;
        if (unit.isDead()) return false;
        if (unit instanceof Door) {
            Door door = (Door) unit;
            return level.getAtRoom().getDoors().contains(door) && hasInteractionDestination(door)
                    && canReachInteractionArea(door.x, door.y, ConstantsHelper.TILE, margin);
        }
        if (!UnitHelper.getInstance().getUnits().contains(unit) || !getActiveRoomIdentifier().equals(unit.getRoom())) return false;
        if (unit instanceof ItemOnScreen) return isHeroOverItem((ItemOnScreen) unit, margin);
        return unit instanceof Interactable && ((Interactable) unit).canInteract()
                && canReachInteractionArea(unit.x, unit.y, ConstantsHelper.UNIT_DIMENSIONS, margin);
    }


    public boolean canInteractWithItem(ItemOnScreen item) {
        return prepareInteractionSelection() && eligibleInteraction(item, INTERACTION_MARGIN + INTERACTION_HYSTERESIS);
    }

    private int interactionPriority(Object target) {
        if (target instanceof ItemOnScreen) return shouldAutoPickup((ItemOnScreen) target) ? 0 : 1;
        if (target instanceof Sign) return 2;
        return target instanceof Interactable ? 3 : 4;
    }


    private Object selectContextTarget() {
        if (!prepareInteractionSelection()) return null;
        int tileX = getHeroTileX();
        int tileY = getHeroTileY();
        Object candidate = getItemAt(tileX, tileY);
        if (candidate == null && hasSignAt(tileX, tileY)) candidate = level.getAtRoom().getSign();
        if (candidate == null) candidate = getInteractableAt(tileX, tileY);
        if (!eligibleInteraction(selectedContextTarget, INTERACTION_MARGIN + INTERACTION_HYSTERESIS)
                || interactionPriority(candidate) < interactionPriority(selectedContextTarget)) {
            selectedContextTarget = candidate;
        }
        return selectedContextTarget;
    }

    private Door selectDoorTarget() {
        if (!prepareInteractionSelection()) return null;
        if (!eligibleInteraction(selectedDoorTarget, INTERACTION_MARGIN + INTERACTION_HYSTERESIS)) {
            selectedDoorTarget = getDoorAt(getHeroTileX(), getHeroTileY());
        }
        return selectedDoorTarget;
    }


    public static final class InteractionPrompt {
        public final String verbKey;
        public final float x, y;
        private final Object target;
        private InteractionPrompt(Object target, String verbKey, float x, float y) {
            this.target = target;
            this.verbKey = verbKey;
            this.x = x;
            this.y = y;
        }
    }

    public InteractionPrompt getContextPrompt() {
        Object target = selectContextTarget();
        if (target instanceof ItemOnScreen) {
            ItemOnScreen item = (ItemOnScreen) target;
            return new InteractionPrompt(item, "custom.prompt.pickup", item.getInteractionX() + item.getInteractionWidth() / 2f,
                    item.y + ConstantsHelper.TILE + 16f);
        }
        if (target instanceof Sign) {
            Sign sign = (Sign) target;
            return new InteractionPrompt(sign, "custom.prompt.read", sign.getX() + ConstantsHelper.TILE / 2f,
                    sign.getY() + ConstantsHelper.TILE + 16f);
        }
        if (target instanceof Interactable) {
            Interactable person = (Interactable) target;
            String verb = person instanceof DisturbableGraveProp
                    ? "custom.prompt.disturb" : "custom.prompt.talk";
            return new InteractionPrompt(person, verb, person.x + ConstantsHelper.UNIT_DIMENSIONS / 2f,
                    person.y + ConstantsHelper.TILE + 16f);
        }
        return null;
    }

    public InteractionPrompt getDoorPrompt() {
        Door door = selectDoorTarget();
        return door == null ? null : new InteractionPrompt(door, door.isLocked() ? "custom.prompt.enter_locked" : "custom.prompt.enter",
                door.x + door.getDisplayWidth() / 2f, door.y + door.getDisplayHeight() + ConstantsHelper.TILE * 0.75f);
    }

    public void enterDoor(){
        if (WindowHelper.getInstance().windowOpen()) return;
        InteractionPrompt resolved = getDoorPrompt();
        Door doorFound = resolved == null ? null : (Door) resolved.target;

        if(doorFound != null){
            if ((doorFound instanceof LevelEntryDoor || doorFound instanceof LevelExitDoor) && doorFound.isLocked()) {
                WindowHelper.getInstance().addWindow(100, 100, Messages.get("custom.generated.the_door_is_locked_c853bb2b92"));
                return;
            }
            EffectsHelper.getInstance().clear();
            RoomDisplayDepth displayDepth = getRoomDisplayDepth();
            String sourceRoom = getActiveRoomIdentifier(), destinationRoom = doorFound.getLeadsTo();

            RoomDisplayDepth.Direction direction = displayDepth.connected(sourceRoom, destinationRoom)
                    && displayDepth.canShowPrevious(destinationRoom, sourceRoom)
                    ? displayDepth.direction(sourceRoom, destinationRoom) : RoomDisplayDepth.Direction.UNAVAILABLE;
            RoomSnapshot outgoing = prepareOutgoingSnapshot(doorFound, displayDepth);
            RoomSnapshot previousBackdrop = GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()
                    ? getPredecessorSnapshot() : null;
            RoomPlaneSelection previousPlanes = getRoomPlaneSelection();
            if(level.enterDoor(doorFound)){
                UnitHelper.getInstance().getHero().appear(doorFound.otherDoor.x, doorFound.otherDoor.y);
                UnitHelper.getInstance().getHero().setRoom(doorFound.getLeadsTo());
                UnitHelper.getInstance().getHero().getFriendlies();
                calculateFloors();
                PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
                NecromancerMinion.transferFor(UnitHelper.getInstance().getHero());
                SaveHelper.getInstance().saveCurrentRun();
                predecessorSnapshot = outgoing;
                roomTransition.begin(destinationRoom, atDepth, direction, previousBackdrop, previousPlanes,
                        UnitHelper.getInstance().getHero().getPresentationPlacementVersion());
                if (GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) {
                    roomAppearances.recordDeparture(RandomHelper.getInstance().getRunSeed(), atDepth,
                            outgoing, destinationRoom, roomTransition.getOutgoingBackdrop());
                    roomPlanes = RoomPlaneSelection.select(roomAppearances, outgoing, displayDepth);
                }
            }

            doorFound.showMessage();
        }
    }

    public void readSign(){
        if (WindowHelper.getInstance().windowOpen()) return;
        InteractionPrompt resolved = getContextPrompt();
        if (resolved != null && resolved.target instanceof Sign) ((Sign) resolved.target).read();
    }

    public void openItemWindow(){
        if (WindowHelper.getInstance().windowOpen()) return;
        InteractionPrompt resolved = getContextPrompt();
        if (resolved != null && resolved.target instanceof ItemOnScreen) openItemWindow((ItemOnScreen) resolved.target);
    }

    private void openItemWindow(ItemOnScreen itemOnScreen){
        if (itemOnScreen.getItem() instanceof Gold) {
            itemOnScreen.pickedUp();
            return;
        }

        if (itemOnScreen.getItem() instanceof Key) {
            collectKey(itemOnScreen, true);
            return;
        }

        WindowHelper.getInstance().addWindow(new ItemWindow(itemOnScreen.getItem()).addPickUp(itemOnScreen).build());
    }

    public void interact(){
        if (WindowHelper.getInstance().windowOpen()) return;
        InteractionPrompt resolved = getContextPrompt();
        if (resolved != null && resolved.target instanceof Interactable) ((Interactable) resolved.target).interact();
    }

    public String getActiveRoomIdentifier(){
        return level.getAtRoom().getIdentifier();
    }

    public Door getEntryDoor(){
        return level.getEntryDoor();
    }

    public Room getEntryRoom(){
        return level.getEntryRoom();
    }

    public boolean goToRoom(String identifier) {
        if (level == null || !level.goToRoom(identifier)) {
            return false;
        }

        clearRoomPresentation();
        calculateFloors();
        PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
        return true;
    }

    public boolean teleportHeroToRandomRoom() {
        if (level == null || UnitHelper.getInstance().getHero() == null || level.rooms == null || level.rooms.isEmpty()) {
            return false;
        }

        String activeRoomIdentifier = getActiveRoomIdentifier();
        ArrayList<Room> candidates = new ArrayList<Room>();
        for (Room room : level.rooms) {
            if (room == null) {
                continue;
            }

            if (level.rooms.size() > 1 && room.getIdentifier().equals(activeRoomIdentifier)) {
                continue;
            }

            candidates.add(room);
        }

        if (candidates.isEmpty()) {
            return false;
        }

        Room targetRoom = candidates.get(RandomHelper.getInstance().randomInt(candidates.size()));
        if (!goToRoom(targetRoom.getIdentifier())) {
            return false;
        }

        Door spawnDoor = targetRoom.getRandomSpawn();
        if (spawnDoor == null) {
            return false;
        }

        placeHeroAtDoor(spawnDoor, false);
        return true;
    }

    public void resetToEntry(){
        clearRoomPresentation();
        level.goToEntry();
        if (UnitHelper.getInstance().getHero() != null) {
            UnitHelper.getInstance().getHero().setRoom(level.getAtRoom().getIdentifier());
        }
        calculateFloors();
        PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
        placeHeroAtDoor(level.getEntryDoor(), false);
    }

    private void placeHeroAtDoor(Door door, boolean centerHorizontally) {
        if (door == null || UnitHelper.getInstance().getHero() == null) {
            return;
        }


        float centerOffset = level.getAtRoom().isBossArena() ? ConstantsHelper.UNIT_DIMENSIONS / 2f
                : (door.getDisplayWidth() - ConstantsHelper.UNIT_DIMENSIONS) / 2f;
        float spawnX = centerHorizontally ? door.x + centerOffset : door.x;
        UnitHelper.getInstance().getHero().setRoom(level.getAtRoom().getIdentifier());
        UnitHelper.getInstance().getHero().appear(spawnX, door.y);
        UnitHelper.getInstance().getHero().floorY = UnitHelper.getInstance().getHero().y;
        UnitHelper.getInstance().getHero().getFriendlies();
        NecromancerMinion.transferFor(UnitHelper.getInstance().getHero());
    }

    public void act(float delta){
        level.act(delta);

        Room activeRoom = level.getAtRoom();
        selectWaterVisualRoom(activeRoom);

        if (!WindowHelper.getInstance().windowOpen() && delta > 0f) waterVisualTime += delta;
        if (!WindowHelper.getInstance().windowOpen() && Float.isFinite(delta) && delta > 0f
                && GameSettingsHelper.getInstance().isBackgroundRoomsEnabled() && predecessorSnapshot != null) backgroundVisualTime += delta;
        if (activeRoom == null || activeRoom.getSign() == null) {
            return;
        }

        Sign sign = activeRoom.getSign();
        sign.act(delta);
        if (sign.isExpired()) {
            activeRoom.setSign(null);
        }
    }

    public Room getRoom(String identifier){
        for(Room room : level.rooms){
            if(room.getIdentifier().equals(identifier)){
                return room;
            }
        }

        return null;
    }

    public boolean isStandingOnWater(Unit unit) {
        if (unit == null || unit.getRoom() == null) {
            return false;
        }

        Room currentRoom = getRoom(unit.getRoom());
        return currentRoom != null && currentRoom.hasWaterAt(unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f, unit.floorY);
    }
}
