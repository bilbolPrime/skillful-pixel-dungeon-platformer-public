package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.bilboldev.skillfulpixeldungeonplatformer.SkillfulPixelDungeonPlatformer;
import com.bilboldev.skillfulpixeldungeonplatformer.achievements.AchievementManager;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Gold;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Key;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Sign;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.PoolRoom;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.caves.Caves;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.city.City;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.halls.Halls;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.prison.Prison;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers.Sewers;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;
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
    private static final long DECORATION_SPLASH_DURATION_MS = 500L;
    private static final float DECORATION_SPLASH_MIN_PARTICLE_SCALE = 0.5f;
    private static final float DECORATION_SPLASH_MIN_ANGLE_DEGREES = 20f;
    private static final float DECORATION_SPLASH_MAX_ANGLE_DEGREES = 80f;
    private static final int DEFAULT_WATER_THICKNESS = 16;
    private static final int POOL_ROOM_WATER_THICKNESS = DEFAULT_WATER_THICKNESS * 3;
    private static final float DEFAULT_WATER_EDGE_RANGE_MULTIPLIER = 2f;
    private static final float DEFAULT_WATER_SIDE_RANGE_MULTIPLIER = 4f;
    private static final float POOL_ROOM_WATER_RANGE_MULTIPLIER = 2f;
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

    public final int MIN_FLOOR = 3;
    protected HashMap<Integer, Array<Integer>> floors;
    protected HashSet<String> platforms;
    protected Theme theme;
    protected int atDepth;
    protected LinkedList<Level> levels;
    protected Level level;
    private boolean restoringGeneratedLevels;
    private final HashSet<Integer> shownChapterIntroDepths;
    private final HashMap<Integer, Integer> keyCountsByDepth;
    private boolean deferChapterIntroUntilTransitionBanner;
    private Runnable pendingChapterIntroAction;
    private GameSprite waterSurface;
    private GameSprite waterHighlight;
    private GameSprite waterDrip;
    private GameSprite decorationWaterSplash;
    private GameSprite cavesDarknessTile;
    private GameSprite prisonWallDecoration;
    private GameSprite sewerWallDecoration;
    private final ArrayList<PlatformSpan> cachedVisibilityPlatformSpans;
    private final ArrayList<ShadowInterval> cachedShadowIntervals;
    private String cachedVisibilityPlatformRoomIdentifier;

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
        reloadVisualAssets();
        cachedVisibilityPlatformSpans = new ArrayList<PlatformSpan>();
        cachedShadowIntervals = new ArrayList<ShadowInterval>();
    }

    public void reloadVisualAssets() {
        waterSurface = new GameSprite("images/misc/grey.png", ConstantsHelper.TILE, 16f, 0.82f);
        waterHighlight = new GameSprite("images/misc/grey.png", ConstantsHelper.TILE - 4f, 6f, 0.95f);
        waterDrip = new GameSprite("images/misc/grey.png", 8f, 14f, 0.95f);
        decorationWaterSplash = new GameSprite("images/misc/grey.png", 18f, 6f, 0.9f);
        cavesDarknessTile = new GameSprite("images/misc/grey.png", ConstantsHelper.TILE, ConstantsHelper.TILE, 1f);
        prisonWallDecoration = new GameSprite(PRISON_WALL_BLOOD_SPRITE, 24f, 24f, 0.9f);
        sewerWallDecoration = new GameSprite(SEWER_WALL_DECORATION_SPRITE, ConstantsHelper.TILE, ConstantsHelper.TILE, 1f);
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
        calculateFloors();
        PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
        showChapterIntroIfNeeded(goingDown);
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
        if (itemOnScreen == null || !(itemOnScreen.getItem() instanceof Key)) {
            return;
        }

        addKeyForCurrentDepth();
        EffectsHelper.getInstance().message(itemOnScreen, "Picked up a key", Color.GOLD, 0f);
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
        for(int i = 0; i < 5000 / ConstantsHelper.TILE + 1; i++){
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

    // TODO: fix the edges properly
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


        // Right side is down, shave the edges
        if(x % ConstantsHelper.TILE > ConstantsHelper.TILE / 2){
            float edgeFloorY = calculateFloorY(x - x % ConstantsHelper.TILE + ConstantsHelper.TILE, y);
            if(edgeFloorY < candidateFloor){
                return edgeFloorY;
            }
        }

        // Left side, take rightside into consideration
        if(x % ConstantsHelper.TILE > ConstantsHelper.TILE / 2){
            float edgeFloorY = calculateFloorY(x - x % ConstantsHelper.TILE + ConstantsHelper.TILE, y);
            if(edgeFloorY > candidateFloor){
                return edgeFloorY;
            }
        }

        return candidateFloor;
    }

    public void draw(Batch batch){
        Room activeRoom = level.getAtRoom();
        HashSet<String> activePlatforms = activeRoom.getPlatforms();
        HashSet<String> waterPlatforms = activeRoom.getWaterPlatforms();
        HashSet<String> renderedWaterTiles = getRenderedWaterTiles(activeRoom, waterPlatforms);
        HashSet<String> prisonDecorationPlatforms = getPrisonDecorationPlatforms(activeRoom);
        HashSet<String> sewerDecorationPlatforms = getSewerDecorationPlatforms(activeRoom, waterPlatforms);

        for(int j = 0; j < activeRoom.getHeight(); j++) {
            for(int i = 0; i < activeRoom.getWidth(); i++) {
                String plat = UtilsHelper.platformKey(i, j);
                if(j > MIN_FLOOR - 1){
                   theme.getWall().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                   theme.getWall().draw(batch);
                   if (prisonDecorationPlatforms.contains(UtilsHelper.platformKey(i, j - 1))) {
                       drawPrisonWallDecoration(batch, i, j);
                   }
                   if (sewerDecorationPlatforms.contains(UtilsHelper.platformKey(i, j - 1))) {
                       drawSewerWallDecoration(batch, i, j);
                   }
                         theme.getFader().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                         theme.getFader().draw(batch);
                }

                if(j == activeRoom.getHeight() - 1){
                    theme.getWall().setPosition(i * ConstantsHelper.TILE , j * ConstantsHelper.TILE);
                    theme.getWall().draw(batch);
                }

                if(j == MIN_FLOOR - 1){
                    theme.getWallFading().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getWallFading().draw(batch);

                    theme.getFader().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFader().draw(batch);

                    theme.getFloor().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getFloor().draw(batch);

                    if (renderedWaterTiles.contains(plat)) {
                        drawWaterPlatform(batch, renderedWaterTiles, sewerDecorationPlatforms, i, j);
                    }
                }

                if(activePlatforms.contains(plat)){
                    theme.getPlatform().setPosition(i * ConstantsHelper.TILE, j * ConstantsHelper.TILE);
                    theme.getPlatform().draw(batch);

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
            theme.getSign().setPosition(activeRoom.getSign().getX(), activeRoom.getSign().getY());
            theme.getSign().setAlpha(activeRoom.getSign().getVisualAlpha());
            theme.getSign().draw(batch);
            theme.getSign().setAlpha(1f);
        }

        if(activeRoom.getStuff() != null){
            for(Unit something: activeRoom.getStuff()){
                something.draw(batch, 1f);
            }
        }
    }

    private HashSet<String> getRenderedWaterTiles(Room activeRoom, HashSet<String> waterPlatforms) {
        HashSet<String> renderedWaterTiles = new HashSet<String>(waterPlatforms);
        if (!(activeRoom instanceof PoolRoom)) {
            return renderedWaterTiles;
        }

        for (int tileX = 0; tileX < (int) activeRoom.getWidth(); tileX++) {
            renderedWaterTiles.add(UtilsHelper.platformKey(tileX, MIN_FLOOR - 1));
        }

        return renderedWaterTiles;
    }

    private void drawWaterPlatform(Batch batch, HashSet<String> waterPlatforms, HashSet<String> sewerDecorationPlatforms, int tileX, int tileY) {
        float platformX = tileX * ConstantsHelper.TILE;
        float platformY = tileY * ConstantsHelper.TILE;
        Room activeRoom = getActiveRoom();
        int waterThickness = getWaterThickness(activeRoom);
        float waterRangeMultiplier = getWaterRangeMultiplier(activeRoom);
        int waterDensityMultiplier = getWaterDensityMultiplier(activeRoom);
        Color previous = new Color(batch.getColor());

        batch.setColor(0.24f, 0.67f, 1f, previous.a);
        waterSurface.setHeight(waterThickness);
        waterSurface.setPosition(platformX, platformY + ConstantsHelper.TILE - waterThickness);
        waterSurface.draw(batch);

        batch.setColor(0.9f, 0.97f, 1f, previous.a);
        waterHighlight.setPosition(platformX + 2f, platformY + ConstantsHelper.TILE - 8f);
        waterHighlight.draw(batch);

        batch.setColor(0.38f, 0.8f, 1f, previous.a);
        if (!waterPlatforms.contains(UtilsHelper.platformKey(tileX - 1, tileY))) {
            drawWaterSideSpill(batch,
                    platformX + 2f,
                    platformY + ConstantsHelper.TILE - 1f,
                    tileX * 37 + tileY * 19,
                    DEFAULT_WATER_SIDE_RANGE_MULTIPLIER * waterRangeMultiplier,
                    waterDensityMultiplier);
        }

        if (!waterPlatforms.contains(UtilsHelper.platformKey(tileX + 1, tileY))) {
            drawWaterSideSpill(batch,
                    platformX + ConstantsHelper.TILE - 5f,
                    platformY + ConstantsHelper.TILE - 1f,
                    tileX * 53 + tileY * 11,
                    DEFAULT_WATER_SIDE_RANGE_MULTIPLIER * waterRangeMultiplier,
                    waterDensityMultiplier);
        }

        batch.setColor(0.88f, 0.98f, 1f, previous.a);
        drawWaterEdgeParticles(batch,
                platformX,
                platformY,
                tileX,
                tileY,
                DEFAULT_WATER_EDGE_RANGE_MULTIPLIER * waterRangeMultiplier,
                waterDensityMultiplier);

        batch.setColor(previous);
        waterSurface.setHeight(DEFAULT_WATER_THICKNESS);
    }

    private int getWaterThickness(Room activeRoom) {
        return activeRoom instanceof PoolRoom ? POOL_ROOM_WATER_THICKNESS : DEFAULT_WATER_THICKNESS;
    }

    private float getWaterRangeMultiplier(Room activeRoom) {
        return activeRoom instanceof PoolRoom ? POOL_ROOM_WATER_RANGE_MULTIPLIER : 1f;
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
        sewerWallDecoration.draw(batch);
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
                                        float fallDistanceMultiplier, int densityMultiplier) {
        float[] horizontalOffsets = densityMultiplier > 1
                ? new float[]{0.05f, 0.13f, 0.21f, 0.29f, 0.38f, 0.46f, 0.54f, 0.63f, 0.71f, 0.79f, 0.87f, 0.95f}
                : new float[]{0.1f, 0.26f, 0.42f, 0.58f, 0.74f, 0.9f};
        float[] verticalOffsets = densityMultiplier > 1
                ? new float[]{1f, 2f, 1f, 3f, 2f, 1f, 3f, 2f, 1f, 2f, 1f, 3f}
                : new float[]{1f, 2f, 1f, 3f, 1f, 2f};

        for (int index = 0; index < horizontalOffsets.length; index++) {
            drawWaterDrip(batch,
                    platformX + ConstantsHelper.TILE * horizontalOffsets[index],
                    platformY + ConstantsHelper.TILE - verticalOffsets[index],
                    tileX * (59 + index * 4) + tileY * (23 + index * 3) + 7 + index * 11,
                    fallDistanceMultiplier);
        }
    }

    private void drawWaterSideSpill(Batch batch, float startX, float startY, int seed,
                                    float fallDistanceMultiplier, int densityMultiplier) {
        drawWaterSideSpill(batch, waterDrip, startX, startY, seed, fallDistanceMultiplier, densityMultiplier);
    }

    private void drawWaterSideSpill(Batch batch, GameSprite dripSprite, float startX, float startY, int seed,
                                    float fallDistanceMultiplier, int densityMultiplier) {
        drawWaterDroplet(batch, dripSprite, startX, startY, seed, 0f, fallDistanceMultiplier);
        drawWaterDroplet(batch, dripSprite, startX + 1.5f, startY - 2f, seed, 0.25f, fallDistanceMultiplier);
        drawWaterDroplet(batch, dripSprite, startX - 1.5f, startY - 1f, seed, 0.5f, fallDistanceMultiplier);
        drawWaterDroplet(batch, dripSprite, startX + 0.5f, startY - 3f, seed, 0.75f, fallDistanceMultiplier);

        if (densityMultiplier > 1) {
            drawWaterDroplet(batch, dripSprite, startX + 0.75f, startY - 1f, seed + 17, 0.125f, fallDistanceMultiplier);
            drawWaterDroplet(batch, dripSprite, startX - 0.75f, startY - 2.5f, seed + 29, 0.375f, fallDistanceMultiplier);
            drawWaterDroplet(batch, dripSprite, startX + 2.25f, startY - 1.5f, seed + 43, 0.625f, fallDistanceMultiplier);
            drawWaterDroplet(batch, dripSprite, startX - 2.25f, startY - 2f, seed + 61, 0.875f, fallDistanceMultiplier);
        }
    }

    private void drawWaterDrip(Batch batch, float startX, float startY, int seed, float fallDistanceMultiplier) {
        drawWaterDroplet(batch, waterDrip, startX, startY, seed, 0f, fallDistanceMultiplier);
        drawWaterDroplet(batch, waterDrip, startX + 1.5f, startY - 2f, seed, 0.5f, fallDistanceMultiplier);
        drawWaterDroplet(batch, waterDrip, startX - 1.5f, startY - 1f, seed, 0.25f, fallDistanceMultiplier * 1.1f);
    }

    private void drawWaterDecorationCascade(Batch batch, float platformX, float platformY, int tileX, int tileY) {
        float centerX = platformX + ConstantsHelper.TILE * 0.5f;
        float startY = platformY + ConstantsHelper.TILE * 1.25f;
        int seed = tileX * 97 + tileY * 61;

        Color previous = new Color(batch.getColor());
        batch.setColor(0.88f, 0.98f, 1f, previous.a);
        drawDecorationWaterSideSpill(batch, centerX - 0.5f, startY, seed);
        batch.setColor(previous);
    }

    private void drawDecorationWaterSideSpill(Batch batch, float startX, float startY, int seed) {
        float decorationFallDistanceMultiplier = 1.6f;

        drawDecorationWaterDroplet(batch, startX, startY, seed, 0f, decorationFallDistanceMultiplier);
        drawDecorationWaterDroplet(batch, startX + 1.5f, startY - 2f, seed, 0.25f, decorationFallDistanceMultiplier);
        drawDecorationWaterDroplet(batch, startX - 1.5f, startY - 1f, seed, 0.5f, decorationFallDistanceMultiplier);
        drawDecorationWaterDroplet(batch, startX + 0.5f, startY - 3f, seed, 0.75f, decorationFallDistanceMultiplier);
    }

    private void drawWaterDroplet(Batch batch, GameSprite dripSprite, float startX, float startY, int seed, float phaseOffset, float fallDistanceMultiplier) {
        long cycleDuration = 700L + Math.abs(seed % 250);
        float phase = ((((TimeUtils.millis() + seed * 91L) % cycleDuration) / (float) cycleDuration) + phaseOffset) % 1f;
        float fallDistance = (18f + Math.abs(seed % 10)) * fallDistanceMultiplier;
        float xOffset = ((seed % 5) - 2) * 0.7f;
        float previousAlpha = dripSprite.getAlpha();

        dripSprite.setAlpha(0.18f + (1f - phase) * 0.72f);
        dripSprite.setPosition(startX + xOffset, startY - phase * fallDistance);
        dripSprite.draw(batch);
        dripSprite.setAlpha(previousAlpha);
    }

    private void drawDecorationWaterDroplet(Batch batch, float startX, float startY, int seed, float phaseOffset, float fallDistanceMultiplier) {
        long cycleDuration = 700L + Math.abs(seed % 250);
        float phase = ((((TimeUtils.millis() + seed * 91L) % cycleDuration) / (float) cycleDuration) + phaseOffset) % 1f;
        float fallDistance = (18f + Math.abs(seed % 10)) * fallDistanceMultiplier;
        float xOffset = ((seed % 5) - 2) * 0.7f;
        float dropletX = startX + xOffset;
        float dropletY = startY - phase * fallDistance;
        float previousAlpha = waterDrip.getAlpha();

        waterDrip.setAlpha(0.18f + (1f - phase) * 0.72f);
        waterDrip.setPosition(dropletX, dropletY);
        waterDrip.draw(batch);
        waterDrip.setAlpha(previousAlpha);

        drawDecorationWaterSplash(batch, dropletX, startY - fallDistance, phase, cycleDuration, seed, phaseOffset);
    }

    private void drawDecorationWaterSplash(Batch batch, float dropletX, float splashY, float phase,
                                           long cycleDuration, int seed, float phaseOffset) {
        float splashWindowPhase = Math.min(1f, DECORATION_SPLASH_DURATION_MS / (float) cycleDuration);
        if (phase > splashWindowPhase) {
            return;
        }

        float progress = phase / splashWindowPhase;
        float splashAlpha = (1f - progress) * 0.5f;
        if (splashAlpha <= 0f) {
            return;
        }

        float previousAlpha = decorationWaterSplash.getAlpha();
        float previousScaleX = decorationWaterSplash.getScaleX();
        float previousScaleY = decorationWaterSplash.getScaleY();

        float mainScaleX = 1f + progress * 0.5f;
        float mainScaleY = 0.8f - progress * 0.25f;
        decorationWaterSplash.setAlpha(splashAlpha);
        decorationWaterSplash.setScale(mainScaleX, mainScaleY);
        decorationWaterSplash.setPosition(dropletX - decorationWaterSplash.getWidth() * mainScaleX / 2f,
                splashY - decorationWaterSplash.getHeight() * mainScaleY * 0.35f);
        decorationWaterSplash.draw(batch);

        float sideScale = 0.35f;
        float sideOffset = 2.5f + progress * 1.5f;
        decorationWaterSplash.setAlpha(splashAlpha * 0.8f);
        decorationWaterSplash.setScale(sideScale, sideScale);
        decorationWaterSplash.setPosition(dropletX - sideOffset,
                splashY + 0.5f);
        decorationWaterSplash.draw(batch);
        decorationWaterSplash.setPosition(dropletX + sideOffset,
                splashY + 0.5f);
        decorationWaterSplash.draw(batch);

        decorationWaterSplash.setAlpha(previousAlpha);
        decorationWaterSplash.setScale(previousScaleX, previousScaleY);

        drawDecorationWaterSplashParticles(batch, dropletX, splashY, splashAlpha, progress, seed, phaseOffset);
    }

    private void drawDecorationWaterSplashParticles(Batch batch, float splashX, float splashY, float splashAlpha,
                                                    float progress, int seed, float phaseOffset) {
        float previousAlpha = waterDrip.getAlpha();
        float previousScaleX = waterDrip.getScaleX();
        float previousScaleY = waterDrip.getScaleY();
        long baseSeed = (((long) seed) << 32) ^ Float.floatToRawIntBits(phaseOffset);

        float angleDegrees = DECORATION_SPLASH_MIN_ANGLE_DEGREES
            + deterministicRandom01(baseSeed ^ 0xA0761D6478BD642FL)
            * (DECORATION_SPLASH_MAX_ANGLE_DEGREES - DECORATION_SPLASH_MIN_ANGLE_DEGREES);
        float travelDistance = 4f + deterministicRandom01(baseSeed ^ 0xE7037ED1A0B428DBL) * 5f;
        float particleScaleX = Math.max(
            DECORATION_SPLASH_MIN_PARTICLE_SCALE,
            0.5f + deterministicRandom01(baseSeed ^ 0x8EBC6AF09C88C6E3L) * 0.08f
        );
        float particleScaleY = Math.max(
            DECORATION_SPLASH_MIN_PARTICLE_SCALE,
            0.5f + deterministicRandom01(baseSeed ^ 0x589965CC75374CC3L) * 0.1f
        );
        float particleAlpha = splashAlpha * 0.9f;
        double angleRadians = Math.toRadians(angleDegrees);
        float horizontalDistance = (float) (Math.cos(angleRadians) * travelDistance);
        float peakHeight = (float) (Math.sin(angleRadians) * travelDistance * 1.15f);
        float particleY = splashY + 0.5f + 4f * peakHeight * progress * (1f - progress);

        drawDecorationSplashParticle(batch, splashX - horizontalDistance * progress, particleY,
            particleScaleX, particleScaleY, particleAlpha);
        drawDecorationSplashParticle(batch, splashX + horizontalDistance * progress, particleY,
            particleScaleX, particleScaleY, particleAlpha);

        waterDrip.setAlpha(previousAlpha);
        waterDrip.setScale(previousScaleX, previousScaleY);
    }

        private void drawDecorationSplashParticle(Batch batch, float particleX, float particleY,
                              float particleScaleX, float particleScaleY, float particleAlpha) {
        waterDrip.setAlpha(particleAlpha);
        waterDrip.setScale(particleScaleX, particleScaleY);
        waterDrip.setPosition(
            particleX - waterDrip.getWidth() * particleScaleX * 0.5f,
            particleY - waterDrip.getHeight() * particleScaleY * 0.15f
        );
        waterDrip.draw(batch);
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
        return !SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()
                && getVisibilityDarknessAlpha() > 0f;
    }

    public void drawVisibilityMask(Batch batch) {
        if (SkillfulPixelDungeonPlatformer.getPlatformProfile().touchControlsEnabled()) {
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
            drawDarknessRect(batch,
                    stripStartX,
                    Math.round(shadowInterval.startY),
                    stripWidth,
                    Math.round(shadowInterval.endY - shadowInterval.startY));
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
            cachedVisibilityPlatformRoomIdentifier = null;
            return cachedVisibilityPlatformSpans;
        }

        if (room.getIdentifier().equals(cachedVisibilityPlatformRoomIdentifier)) {
            return cachedVisibilityPlatformSpans;
        }

        cachedVisibilityPlatformSpans.clear();
        cachedVisibilityPlatformRoomIdentifier = room.getIdentifier();

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
        if (width <= 0 || height <= 0) {
            return;
        }

        cavesDarknessTile.setWidth(width);
        cavesDarknessTile.setHeight(height);
        cavesDarknessTile.setPosition(x, y);
        cavesDarknessTile.draw(batch);
        cavesDarknessTile.setWidth((int) ConstantsHelper.TILE);
        cavesDarknessTile.setHeight((int) ConstantsHelper.TILE);
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

    private boolean isHeroOverItem(ItemOnScreen itemOnScreen, int heroTileY) {
        Unit hero = UnitHelper.getInstance().getHero();
        if (hero == null || itemOnScreen == null || !itemOnScreen.isPlaced()) {
            return false;
        }

        if ((int) (itemOnScreen.getInteractionFloorY() / ConstantsHelper.TILE) != heroTileY) {
            return false;
        }

        float heroLeft = hero.x;
        float heroRight = hero.x + ConstantsHelper.UNIT_DIMENSIONS;
        float itemLeft = itemOnScreen.getInteractionX();
        float itemRight = itemLeft + itemOnScreen.getInteractionWidth();
        return heroRight > itemLeft && heroLeft < itemRight;
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
                    if (shouldAutoPickup(itemOnScreen)) {
                        return itemOnScreen;
                    }

                    if (firstMatchingItem == null) {
                        firstMatchingItem = itemOnScreen;
                    }
                }
            }
        }

        return firstMatchingItem;
    }

    private Interactable getInteractableAt(int tileX, int tileY) {
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (unit.getRoom() == null || !unit.getRoom().equals(getActiveRoomIdentifier())) {
                continue;
            }

            if (unit instanceof Interactable
                    && (int) (unit.x / ConstantsHelper.TILE) == tileX
                    && (int) (unit.y / ConstantsHelper.TILE) == tileY) {
                Interactable interactable = (Interactable) unit;
                if (interactable.canInteract()) {
                    return interactable;
                }
            }
        }

        return null;
    }

    private Door getDoorAt(int tileX, int tileY) {
        for (Door door : level.getAtRoom().getDoors()) {
            if ((int) (door.x / ConstantsHelper.TILE) == tileX && (int) (door.y / ConstantsHelper.TILE) == tileY) {
                return door;
            }
        }

        return null;
    }

    private boolean hasSignAt(int tileX, int tileY) {
        return level.getAtRoom().getSign() != null
                && level.getAtRoom().getSign().isReadable()
                && (int) (level.getAtRoom().getSign().getX() / ConstantsHelper.TILE) == tileX
                && (int) (level.getAtRoom().getSign().getY() / ConstantsHelper.TILE) == tileY;
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

        if (itemOnScreen != null) {
            UIHelper.getInstance().showPickupButton(itemOnScreen);
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
        Interactable interactable = getInteractableAt(tileX, tileY);
        if (interactable != null) {
            UIHelper.getInstance().showInteractButton(interactable.getInteractGS());
            return true;
        }

        UIHelper.getInstance().disableInteractButton();
        return false;
    }

    private boolean checkDoors(int tileX, int tileY){
        Door door = getDoorAt(tileX, tileY);
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

        if(hasSignAt(tileX, tileY)){
             UIHelper.getInstance().showSignButton();
            return true;
        }

        UIHelper.getInstance().disableSignButton();
        return false;
    }

    public boolean performContextAction() {
        int tileX = getHeroTileX();
        int tileY = getHeroTileY();

        if (getItemAt(tileX, tileY) != null) {
            openItemWindow();
            return true;
        }

        if (hasSignAt(tileX, tileY)) {
            readSign();
            return true;
        }

        if (getInteractableAt(tileX, tileY) != null) {
            interact();
            return true;
        }

        return false;
    }

    public void enterDoor(){
        Door doorFound = getDoorAt(getHeroTileX(), getHeroTileY());

        if(doorFound != null){
            EffectsHelper.getInstance().clear();
            if(level.enterDoor(doorFound)){
                UnitHelper.getInstance().getHero().appear(doorFound.otherDoor.x, doorFound.otherDoor.y);
                UnitHelper.getInstance().getHero().setRoom(doorFound.getLeadsTo());
                UnitHelper.getInstance().getHero().getFriendlies();
                calculateFloors();
                PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
                SaveHelper.getInstance().saveCurrentRun();
            }

            doorFound.showMessage();
        }
    }

    public void readSign(){
        if(hasSignAt(getHeroTileX(), getHeroTileY())){
            level.getAtRoom().getSign().read();
        }
    }

    public void openItemWindow(){
        ItemOnScreen itemOnScreen = getItemAt(getHeroTileX(), getHeroTileY());
        if (itemOnScreen == null) {
            return;
        }

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
        Interactable interactable = getInteractableAt(getHeroTileX(), getHeroTileY());
        if (interactable != null) {
            interactable.interact();
        }
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
        level.goToEntry();
        if (UnitHelper.getInstance().getHero() != null) {
            UnitHelper.getInstance().getHero().setRoom(level.getAtRoom().getIdentifier());
        }
        calculateFloors();
        PhysicsHelper.getInstance().ensureRoom(level.getAtRoom());
    }

    private void placeHeroAtDoor(Door door, boolean centerHorizontally) {
        if (door == null || UnitHelper.getInstance().getHero() == null) {
            return;
        }

        float spawnX = centerHorizontally ? door.x + ConstantsHelper.UNIT_DIMENSIONS / 2f : door.x;
        UnitHelper.getInstance().getHero().setRoom(level.getAtRoom().getIdentifier());
        UnitHelper.getInstance().getHero().appear(spawnX, door.y);
        UnitHelper.getInstance().getHero().floorY = UnitHelper.getInstance().getHero().y;
        UnitHelper.getInstance().getHero().getFriendlies();
    }

    public void act(float delta){
        level.act(delta);

        Room activeRoom = level.getAtRoom();
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
