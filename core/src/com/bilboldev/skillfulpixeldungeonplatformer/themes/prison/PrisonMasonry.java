package com.bilboldev.skillfulpixeldungeonplatformer.themes.prison;

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


final class PrisonMasonry {
    private static final float TILE = ConstantsHelper.TILE, PIXEL = TILE / 16f;
    private static final Color LIP = Color.valueOf("B4B4A7");
    private static final Color STONE = new Color(0.78f, 0.82f, 0.86f, 1f);
    private static final Color TRIM = new Color(0.43f, 0.48f, 0.51f, 1f);
    private static final Color IRON = Color.valueOf("30393D");
    private static final Color IRON_EDGE = Color.valueOf("596367");
    private static final Color RECESS = Color.valueOf("171E21");
    private static final Color BACK = Color.valueOf("242D30");
    private static final Color SHADOW = new Color(0.025f, 0.04f, 0.045f, 0.24f);
    private static final Color FALLOFF = new Color(0.025f, 0.04f, 0.045f, 0.12f);
    private static final Color FLAME = Color.valueOf("CBA268");
    private static final Color POOL = new Color(0.87f, 0.57f, 0.29f, 0.18f);
    private static final Color CORE = new Color(1f, 0.73f, 0.37f, 0.24f);
    private final TextureRegion pixel, face, alternate, left, right, pier, lintel;
    private TextureRegion light;
    private RoomBackdropRecess rooms;
    private final com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure structure = new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure("prison");
    private final float[] bayX = new float[2];
    private final float[] lampX = new float[2], lampY = new float[2];
    private String sourceId;
    private int sourceDepth, bayCount;
    private float bayY;

    PrisonMasonry() {
        TextureHelper textures = TextureHelper.GetSingleton();
        Texture sheet = textures.getTexture("images/tiles/prison/raised.png");
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
        float x = tileX * TILE;

        float y = (tileY + 1) * TILE + 4f;
        rect(batch, x, y, leftEnd ? 2 : 0, -9, 16 - (leftEnd ? 2 : 0) - (rightEnd ? 2 : 0), 2, SHADOW);
        rect(batch, x, y, 2, -10, 12, 1, FALLOFF);
        if (leftEnd || rightEnd || ((tileX + tileY) & 3) == 0) {

            rect(batch, x, y, 6, -10, 4, 5, IRON);
            rect(batch, x, y, 6, -8, 1, 3, IRON_EDGE);
            rect(batch, x, y, 7, -11, 2, 2, IRON);
            rect(batch, x, y, 8, -7, 1, 1, LIP);
        }
        TextureRegion material = leftEnd ? left : rightEnd ? right : ((tileX & 1) == 0 ? face : alternate);
        piece(batch, material, x, y - 6 * PIXEL, TILE, 5 * PIXEL, STONE);
        rect(batch, x, y, 0, -1, 16, 1, LIP);
        rect(batch, x, y, 0, -7, 16, 1, RECESS);
        if (leftEnd) rect(batch, x, y, 0, -6, 1, 5, IRON_EDGE);
        if (rightEnd) rect(batch, x, y, 15, -7, 1, 6, IRON);
    }

    void drawArchitecture(Batch batch, Room room, int depth) {
        if (sourceDepth != depth || !room.getIdentifier().equals(sourceId)) selectBays(room, depth);
        if (rooms == null && MapHelper.getInstance().getPredecessorSnapshot() != null)
            rooms = new RoomBackdropRecess(new TextureRegion(TextureHelper.GetSingleton().getTexture("images/tiles/prison/wall.png")), pier, lintel);
        if (rooms != null) rooms.draw(batch, room, depth);
        for (int i = 0; i < bayCount; i++)
            if (rooms == null || !rooms.covers(bayX[i] - 2f * PIXEL, bayY, 28f * PIXEL, 34f * PIXEL)) drawBay(batch, bayX[i], bayY, i);
        structure.draw(batch, room);
        int surrounds = 0, lamps = 0;
        for (Door door : room.getDoors()) {
            if (door.x < 3 * PIXEL || door.x + TILE + 3 * PIXEL > room.getWidth() * TILE) continue;
            if (surrounds++ >= 4) break;
            float x = door.x, y = door.y + 7f;
            rect(batch, x, y, -2, 0, 20, 19, RECESS);
            piece(batch, pier, x - 3 * PIXEL, y, 3 * PIXEL, TILE, TRIM);
            piece(batch, pier, x + TILE, y, 3 * PIXEL, TILE, TRIM);
            piece(batch, lintel, x, y + TILE, TILE, 3 * PIXEL, TRIM);
            if (!room.getLayout().hasMountedFixtures() && lamps < 2 && x + 23 * PIXEL < room.getWidth() * TILE) {
                rect(batch, x, y, 20, 9, 2, 5, IRON);
                rect(batch, x, y, 20, 13, 2, 3, FLAME);
                lampX[lamps] = x + 21 * PIXEL;
                lampY[lamps++] = y + 14 * PIXEL;
            }
        }
        if (lamps == 0) return;
        if (light == null) light = new TextureRegion(TextureHelper.GetSingleton().getSoftLightTexture());
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * GameSettingsHelper.getInstance().getVisualEffectIntensity());
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        float radius = 22 * PIXEL;
        for (int i = 0; i < lamps; i++) {
            if (camera != null && (Math.abs(lampX[i] - camera.position.x) > camera.viewportWidth * camera.zoom / 2f + radius ||
                    Math.abs(lampY[i] - camera.position.y) > camera.viewportHeight * camera.zoom / 2f + radius)) continue;
            piece(batch, light, lampX[i] - radius, lampY[i] - radius, radius * 2, radius * 2, POOL);
            piece(batch, light, lampX[i] - 4 * PIXEL, lampY[i] - 4 * PIXEL, 8 * PIXEL, 8 * PIXEL, CORE);
            MapHelper.getInstance().getRoomFixtureObservation().torch(lampX[i], lampY[i]);
        }
        batch.setPackedColor(packed);
    }

    private void selectBays(Room room, int depth) {
        sourceId = room.getIdentifier();
        sourceDepth = depth;
        bayCount = 0;
        if (com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint.supports(room)
                || com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.BossArenaArchitecture.supports(room)) return;
        bayY = ConstantsHelper.MIN_FLOOR * TILE + 10 * PIXEL;
        long hash = RandomHelper.getInstance().levelSeed(depth) ^ sourceId.hashCode() ^ 0x505249534F4E4241L;
        hash ^= hash >>> 32;
        for (int tile = 2 + (int) (hash & 3); tile < room.getWidth() - 3 && bayCount < bayX.length; tile += 3) {
            float x = tile * TILE;
            if (clearBay(room, x - 2 * PIXEL, bayY - 2 * PIXEL, 28 * PIXEL, 34 * PIXEL)) {
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

    private void drawBay(Batch batch, float x, float y, int variant) {
        rect(batch, x, y, 0, 0, 24, 30, RECESS);
        rect(batch, x, y, 2, 2, 20, 26, BACK);

        for (int bar = 4; bar < 22; bar += 4) rect(batch, x, y, bar, 2, 1, 26, IRON);
        rect(batch, x, y, 2, 11, 20, 1, IRON);
        rect(batch, x, y, 2, 22, 20, 1, IRON);
        for (int row = 0; row < 2; row++) {
            piece(batch, pier, x - 2 * PIXEL, y + row * TILE, 3 * PIXEL, TILE, TRIM);
            piece(batch, pier, x + 23 * PIXEL, y + row * TILE, 3 * PIXEL, TILE, TRIM);
        }
        piece(batch, lintel, x + 4 * PIXEL, y + 29 * PIXEL, TILE, 3 * PIXEL, TRIM);
        rect(batch, x, y, 1, 0, 22, 2, IRON);

        int chain = variant == 0 ? 7 : 17;
        for (int link = 17; link < 27; link += 3) {
            rect(batch, x, y, chain, link, 1, 3, IRON_EDGE);
            rect(batch, x, y, chain + 1, link + 1, 1, 1, IRON);
        }
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
