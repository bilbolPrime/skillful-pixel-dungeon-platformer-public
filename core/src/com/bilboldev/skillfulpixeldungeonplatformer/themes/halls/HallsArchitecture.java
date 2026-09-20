package com.bilboldev.skillfulpixeldungeonplatformer.themes.halls;

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


final class HallsArchitecture {
    private static final float TILE = ConstantsHelper.TILE, PIXEL = TILE / 16f;
    private static final Color LIP = Color.valueOf("A49D96");
    private static final Color FACE = new Color(0.85f, 0.84f, 0.89f, 1f);
    private static final Color TRIM = new Color(0.39f, 0.41f, 0.45f, 1f);
    private static final Color SIDE = Color.valueOf("414047");
    private static final Color EDGE = Color.valueOf("5A555B");
    private static final Color RECESS = Color.valueOf("181B23");
    private static final Color INNER = Color.valueOf("252730");
    private static final Color ASH = new Color(0.47f, 0.46f, 0.48f, 0.32f);
    private static final Color RED = Color.valueOf("986259");
    private static final Color GREEN = Color.valueOf("709879");
    private static final Color RED_POOL = new Color(0.67f, 0.22f, 0.15f, 0.13f);
    private static final Color GREEN_POOL = new Color(0.28f, 0.60f, 0.34f, 0.13f);
    private static final Color SHADOW = new Color(0.025f, 0.03f, 0.045f, 0.24f);
    private static final Color FALLOFF = new Color(0.025f, 0.03f, 0.045f, 0.12f);
    private final TextureRegion pixel, face, alternate, left, right, pillar, lintel;
    private TextureRegion light;
    private RoomBackdropRecess rooms;
    private final com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure structure = new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure("halls");
    private final float[] bayX = new float[2], lampX = new float[2], lampY = new float[2];
    private String sourceId;
    private int sourceDepth, bayCount;
    private float bayY;

    HallsArchitecture() {
        TextureHelper textures = TextureHelper.GetSingleton();
        Texture sheet = textures.getTexture("images/tiles/halls/raised.png");
        pixel = new TextureRegion(textures.getSolidPixel());
        face = new TextureRegion(sheet, 0, 113, 16, 6);
        alternate = new TextureRegion(sheet, 0, 129, 16, 6);
        left = new TextureRegion(sheet, 32, 113, 16, 6);
        right = new TextureRegion(sheet, 16, 113, 16, 6);
        pillar = new TextureRegion(sheet, 0, 112, 4, 16);
        lintel = new TextureRegion(sheet, 0, 208, 16, 3);
    }

    void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        boolean leftEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX - 1, tileY));
        boolean rightEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX + 1, tileY));
        float x = tileX * TILE, y = (tileY + 1) * TILE + 4f;
        rect(batch, x, y, 1, -10, 14, 2, SHADOW);
        rect(batch, x, y, 2, -11, 12, 1, FALLOFF);
        if (leftEnd || rightEnd || ((tileX + tileY) & 3) == 0) {
            rect(batch, x, y, 5, -10, 6, 3, SIDE);
            rect(batch, x, y, 6, -12, 4, 2, RECESS);
            rect(batch, x, y, 6, -10, 1, 3, EDGE);
        }
        TextureRegion material = leftEnd ? left : rightEnd ? right : ((tileX & 1) == 0 ? face : alternate);
        piece(batch, material, x, y - 7 * PIXEL, TILE, 6 * PIXEL, FACE);

        rect(batch, x, y, 0, -1, 16, 1, LIP);
        rect(batch, x, y, 0, -8, 16, 1, SIDE);
        rect(batch, x, y, 0, -9, 16, 1, RECESS);
        if (leftEnd) rect(batch, x, y, 0, -7, 1, 6, EDGE);
        if (rightEnd) rect(batch, x, y, 15, -8, 1, 7, SIDE);
    }

    void draw(Batch batch, Room room, int depth) {
        if (sourceDepth != depth || !room.getIdentifier().equals(sourceId)) selectBays(room, depth);
        if (rooms == null && MapHelper.getInstance().getPredecessorSnapshot() != null)
            rooms = new RoomBackdropRecess(RoomBackdropRecess.Kind.HALLS,
                    new TextureRegion(TextureHelper.GetSingleton().getTexture("images/tiles/halls/wall.png")), pillar, lintel);
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
            if (!room.getLayout().hasMountedFixtures() && lamps < 2 && x + 23 * PIXEL < room.getWidth() * TILE) {
                rect(batch, x, y, 20, 10, 3, 6, RECESS);
                rect(batch, x, y, 21, 11, 1, 4, lamps == 0 ? GREEN : RED);
                lampX[lamps] = x + 21.5f * PIXEL;
                lampY[lamps++] = y + 13 * PIXEL;
            }
        }
        if (lamps == 0) return;
        if (light == null) light = new TextureRegion(TextureHelper.GetSingleton().getSoftLightTexture());
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * GameSettingsHelper.getInstance().getVisualEffectIntensity());
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        float radius = 18 * PIXEL;
        for (int i = 0; i < lamps; i++) {
            if (camera != null && (Math.abs(lampX[i] - camera.position.x) > camera.viewportWidth * camera.zoom / 2f + radius ||
                    Math.abs(lampY[i] - camera.position.y) > camera.viewportHeight * camera.zoom / 2f + radius)) continue;
            piece(batch, light, lampX[i] - radius, lampY[i] - radius, radius * 2, radius * 2, i == 0 ? GREEN_POOL : RED_POOL);
            MapHelper.getInstance().getRoomFixtureObservation().hallsLamp(lampX[i], lampY[i], i == 0);
        }
        batch.setPackedColor(packed);
    }

    private void column(Batch batch, float x, float y, int height) {
        for (int row = 0; row < height; row += 16)
            piece(batch, pillar, x, y + row * PIXEL, 4 * PIXEL, Math.min(16, height - row) * PIXEL, TRIM);
        rect(batch, x, y, 3, 1, 1, height - 2, RECESS);
        rect(batch, x, y, -1, 0, 6, 2, SIDE);
        rect(batch, x, y, -1, height - 2, 6, 2, SIDE);
    }

    private void drawBay(Batch batch, float x, float y) {
        rect(batch, x, y, 0, 0, 26, 32, RECESS);
        rect(batch, x, y, 5, 4, 16, 24, INNER);

        for (int flute = 7; flute < 21; flute += 4) rect(batch, x, y, flute, 4, 2, 24, RECESS);
        column(batch, x - 2 * PIXEL, y, 32);
        column(batch, x + 24 * PIXEL, y, 32);
        piece(batch, lintel, x + 5 * PIXEL, y + 30 * PIXEL, TILE, 3 * PIXEL, TRIM);
        rect(batch, x, y, 2, 0, 22, 2, SIDE);

        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * GameSettingsHelper.getInstance().getVisualEffectIntensity());
        for (int ash = 0; ash < 6; ash++) rect(batch, x, y, 3 + ash * 3, 2 + (ash % 3), 1, 1, ASH);
        batch.setPackedColor(packed);
    }

    private void selectBays(Room room, int depth) {
        sourceId = room.getIdentifier(); sourceDepth = depth; bayCount = 0;
        if (com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint.supports(room)
                || com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.BossArenaArchitecture.supports(room)) return;
        bayY = ConstantsHelper.MIN_FLOOR * TILE + 10 * PIXEL;
        long hash = RandomHelper.getInstance().levelSeed(depth) ^ sourceId.hashCode() ^ 0x48414C4C53424159L;
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
        piece(batch, pixel, x + px * PIXEL, y + py * PIXEL, width * PIXEL, height * PIXEL, tint);
    }

    private void piece(Batch batch, TextureRegion region, float x, float y, float width, float height, Color tint) {
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * tint.r, parent.g * tint.g, parent.b * tint.b, parent.a * tint.a);
        batch.draw(region, x, y, width, height);
        batch.setPackedColor(packed);
    }
}
