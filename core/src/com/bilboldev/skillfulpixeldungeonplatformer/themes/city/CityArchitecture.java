package com.bilboldev.skillfulpixeldungeonplatformer.themes.city;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomBackdropRecess;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;


final class CityArchitecture {
    private static final float TILE = ConstantsHelper.TILE, PIXEL = TILE / 16f;
    private static final Color LIP = Color.valueOf("BAB5A3");
    private static final Color STONE = new Color(0.85f, 0.83f, 0.80f, 1f);
    private static final Color CARVING = Color.valueOf("4A4B48");
    private static final Color EDGE = Color.valueOf("69695F");
    private static final Color RECESS = Color.valueOf("1B2327");
    private static final Color INNER = Color.valueOf("2A3234");
    private static final Color BRASS = Color.valueOf("96815A");
    private static final Color OLD_BRASS = Color.valueOf("615A44");
    private static final Color AMBER = Color.valueOf("D0A35F");
    private static final Color TRIM = new Color(0.43f, 0.45f, 0.45f, 1f);
    private static final Color SHADOW = new Color(0.025f, 0.035f, 0.04f, 0.24f);
    private static final Color FALLOFF = new Color(0.025f, 0.035f, 0.04f, 0.12f);
    private static final Color POOL = new Color(0.87f, 0.64f, 0.33f, 0.16f);
    private static final Color CORE = new Color(1f, 0.77f, 0.43f, 0.23f);
    private final TextureRegion pixel, face, alternate, left, right, pier, lintel;
    private TextureRegion light;
    private RoomBackdropRecess rooms;
    private final com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure structure = new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure("city");
    private final float[] bayX = new float[2], lampX = new float[2], lampY = new float[2];
    private String sourceId;
    private int sourceDepth, bayCount;
    private float bayY;

    CityArchitecture() {
        TextureHelper textures = TextureHelper.GetSingleton();
        Texture sheet = textures.getTexture("images/tiles/city/raised.png");
        pixel = new TextureRegion(textures.getSolidPixel());
        face = new TextureRegion(sheet, 0, 113, 16, 5);
        alternate = new TextureRegion(sheet, 0, 129, 16, 5);
        left = new TextureRegion(sheet, 32, 113, 16, 5);
        right = new TextureRegion(sheet, 16, 113, 16, 5);
        pier = new TextureRegion(sheet, 0, 112, 3, 16);
        lintel = new TextureRegion(sheet, 0, 208, 16, 3);
    }

    void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        boolean leftEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX - 1, tileY));
        boolean rightEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX + 1, tileY));
        float x = tileX * TILE, y = (tileY + 1) * TILE + 4f;
        rect(batch, x, y, 1, -10, 14, 2, SHADOW);
        rect(batch, x, y, 2, -11, 12, 1, FALLOFF);
        if (leftEnd || rightEnd || ((tileX + tileY) & 3) == 0) {
            rect(batch, x, y, 5, -9, 6, 3, CARVING);
            rect(batch, x, y, 6, -11, 4, 2, RECESS);
            rect(batch, x, y, 6, -9, 1, 2, EDGE);
            rect(batch, x, y, 7, -8, 2, 1, BRASS);
        }
        TextureRegion material = leftEnd ? left : rightEnd ? right : ((tileX & 1) == 0 ? face : alternate);
        piece(batch, material, x, y - 6 * PIXEL, TILE, 5 * PIXEL, STONE);
        rect(batch, x, y, 0, -1, 16, 1, LIP);
        rect(batch, x, y, 0, -7, 16, 1, CARVING);
        rect(batch, x, y, 0, -8, 16, 1, RECESS);

        rect(batch, x, y, 4, -5, 8, 1, CARVING);
        rect(batch, x, y, 4, -4, 1, 2, CARVING);
        rect(batch, x, y, 11, -4, 1, 2, CARVING);
        if (leftEnd || rightEnd) rect(batch, x, y, leftEnd ? 1 : 14, -4, 1, 1, BRASS);
    }

    void draw(Batch batch, Room room, int depth) {
        if (sourceDepth != depth || !room.getIdentifier().equals(sourceId)) selectBays(room, depth);
        if (rooms == null && MapHelper.getInstance().getPredecessorSnapshot() != null)
            rooms = new RoomBackdropRecess(RoomBackdropRecess.Kind.CITY,
                    new TextureRegion(TextureHelper.GetSingleton().getTexture("images/tiles/city/wall.png")), pier, lintel);
        if (rooms != null) rooms.draw(batch, room, depth);
        for (int i = 0; i < bayCount; i++) {
            if (rooms == null || !rooms.covers(bayX[i] - 3f * PIXEL, bayY - PIXEL, 32f * PIXEL, 35f * PIXEL))
                drawBay(batch, bayX[i], bayY);
        }
        structure.draw(batch, room);
        int surrounds = 0, lamps = 0;
        for (Door door : room.getDoors()) {
            if (door.x < 4 * PIXEL || door.x + TILE + 4 * PIXEL > room.getWidth() * TILE) continue;
            if (surrounds++ >= 4) break;
            float x = door.x, y = door.y + 7f;
            rect(batch, x, y, -3, 0, 22, 19, RECESS);
            column(batch, x - 4 * PIXEL, y, 16);
            column(batch, x + TILE, y, 16);
            piece(batch, lintel, x, y + TILE, TILE, 3 * PIXEL, TRIM);
            rect(batch, x, y, -2, 17, 2, 1, OLD_BRASS);
            rect(batch, x, y, 16, 17, 2, 1, OLD_BRASS);
            if (!room.getLayout().hasMountedFixtures() && lamps < 2 && x + 23 * PIXEL < room.getWidth() * TILE) {
                rect(batch, x, y, 21, 9, 2, 6, OLD_BRASS);
                rect(batch, x, y, 21, 11, 1, 3, AMBER);
                lampX[lamps] = x + 21.5f * PIXEL;
                lampY[lamps++] = y + 12.5f * PIXEL;
            }
        }
        if (lamps == 0) return;
        if (light == null) light = new TextureRegion(TextureHelper.GetSingleton().getSoftLightTexture());
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * GameSettingsHelper.getInstance().getVisualEffectIntensity());
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        float radius = 20 * PIXEL;
        for (int i = 0; i < lamps; i++) {
            if (camera != null && (Math.abs(lampX[i] - camera.position.x) > camera.viewportWidth * camera.zoom / 2f + radius ||
                    Math.abs(lampY[i] - camera.position.y) > camera.viewportHeight * camera.zoom / 2f + radius)) continue;
            piece(batch, light, lampX[i] - radius, lampY[i] - radius, radius * 2, radius * 2, POOL);
            piece(batch, light, lampX[i] - 3 * PIXEL, lampY[i] - 3 * PIXEL, 6 * PIXEL, 6 * PIXEL, CORE);
            MapHelper.getInstance().getRoomFixtureObservation().cityLamp(lampX[i], lampY[i]);
        }
        batch.setPackedColor(packed);
    }

    private void column(Batch batch, float x, float y, int height) {
        rect(batch, x, y, 0, 0, 4, height, CARVING);
        for (int row = 0; row < height; row += 16)
            piece(batch, pier, x, y + row * PIXEL, 3 * PIXEL, Math.min(16, height - row) * PIXEL, TRIM);
        rect(batch, x, y, 1, 2, 1, height - 4, EDGE);
        rect(batch, x, y, 3, 1, 1, height - 2, RECESS);
        rect(batch, x, y, -1, 0, 6, 2, CARVING);
        rect(batch, x, y, -1, height - 2, 6, 2, CARVING);
        rect(batch, x, y, 0, height - 3, 4, 1, OLD_BRASS);
    }

    private void drawBay(Batch batch, float x, float y) {
        rect(batch, x, y, 0, 0, 26, 32, RECESS);
        rect(batch, x, y, 4, 3, 18, 26, INNER);

        rect(batch, x, y, 7, 6, 12, 20, RECESS);
        rect(batch, x, y, 10, 8, 6, 16, INNER);
        rect(batch, x, y, 12, 12, 2, 8, OLD_BRASS);
        column(batch, x - 2 * PIXEL, y, 32);
        column(batch, x + 24 * PIXEL, y, 32);
        piece(batch, lintel, x + 5 * PIXEL, y + 30 * PIXEL, TILE, 3 * PIXEL, TRIM);
        rect(batch, x, y, 2, 0, 22, 2, CARVING);
    }

    private void selectBays(Room room, int depth) {
        sourceId = room.getIdentifier(); sourceDepth = depth; bayCount = 0;
        if (com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint.supports(room)
                || com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.BossArenaArchitecture.supports(room)) return;
        bayY = ConstantsHelper.MIN_FLOOR * TILE + 10 * PIXEL;
        long hash = RandomHelper.getInstance().levelSeed(depth) ^ sourceId.hashCode() ^ 0x4349545942415953L;
        hash ^= hash >>> 32;
        for (int tile = 2 + (int) (hash & 3); tile < room.getWidth() - 3 && bayCount < bayX.length; tile += 3) {
            float x = tile * TILE;
            if (clearBay(room, x - 3 * PIXEL, bayY - PIXEL, 32 * PIXEL, 35 * PIXEL)) {
                bayX[bayCount++] = x;
                tile += 2;
            }
        }
    }

    private boolean clearBay(Room room, float x, float y, float width, float height) {
        if (x < 0 || x + width > room.getWidth() * TILE || y + height > (room.getHeight() - 1) * TILE) return false;
        for (int row = (int) (y / TILE); row <= (int) ((y + height) / TILE); row++) {
            for (int col = (int) (x / TILE); col <= (int) ((x + width) / TILE); col++) {
                if (room.getPlatforms().contains(UtilsHelper.platformKey(col, row))) return false;
            }
        }
        for (Door door : room.getDoors()) {
            if (x < door.x + TILE + 4 * PIXEL && x + width > door.x - 4 * PIXEL &&
                    y < door.y + TILE + 8 * PIXEL && y + height > door.y - 2 * PIXEL) return false;
        }
        return true;
    }

    private void rect(Batch batch, float x, float y, int px, int py, int width, int height, Color tint) {
        piece(batch, pixel, x + px * PIXEL, y + py *PIXEL, width * PIXEL, height * PIXEL, tint);
    }

    private void piece(Batch batch, TextureRegion region, float x, float y, float width, float height, Color tint) {
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * tint.r, parent.g * tint.g, parent.b * tint.b, parent.a * tint.a);
        batch.draw(region, x, y, width, height);
        batch.setPackedColor(packed);
    }
}
