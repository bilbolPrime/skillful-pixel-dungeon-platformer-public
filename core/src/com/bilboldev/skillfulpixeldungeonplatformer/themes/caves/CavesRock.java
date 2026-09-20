package com.bilboldev.skillfulpixeldungeonplatformer.themes.caves;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomBackdropRecess;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;


final class CavesRock {
    private static final float TILE = ConstantsHelper.TILE, PIXEL = TILE / 16f;
    private static final Color WALL = new Color(0.57f, 0.63f, 0.65f, 1f);
    private static final Color WALL_WASH = new Color(0.23f, 0.26f, 0.27f, 0.55f);
    private static final Color ROCK = new Color(0.70f, 0.75f, 0.74f, 1f);
    private static final Color LIP = Color.valueOf("929C91");
    private static final Color SIDE = Color.valueOf("46514F");
    private static final Color DARK = Color.valueOf("182326");
    private static final Color DISTANT = Color.valueOf("253235");
    private static final Color TIMBER = Color.valueOf("69563E");
    private static final Color TIMBER_EDGE = Color.valueOf("87704F");
    private static final Color OLD_TIMBER = Color.valueOf("3E4034");
    private static final Color ORE = Color.valueOf("87936A");
    private static final Color SHADOW = new Color(0.03f, 0.05f, 0.055f, 0.24f);
    private static final Color FALLOFF = new Color(0.03f, 0.05f, 0.055f, 0.12f);
    private final TextureRegion pixel, face, alternate, left, right, trim;
    private final TextureRegion[] walls = new TextureRegion[3];
    private final float[] shaftX = new float[2];
    private RoomBackdropRecess rooms;
    private final com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure structure = new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomStructure("caves");
    private String sourceId;
    private int sourceDepth, shaftCount;
    private float shaftY;

    CavesRock() {
        TextureHelper textures = TextureHelper.GetSingleton();
        Texture sheet = textures.getTexture("images/tiles/caves/raised.png");
        pixel = new TextureRegion(textures.getSolidPixel());
        face = new TextureRegion(sheet, 0, 112, 16, 6);
        alternate = new TextureRegion(sheet, 0, 128, 16, 6);
        left = new TextureRegion(sheet, 32, 112, 16, 6);
        right = new TextureRegion(sheet, 16, 112, 16, 6);
        trim = new TextureRegion(sheet, 0, 208, 16, 3);
        for (int i = 0; i < walls.length; i++) walls[i] = new TextureRegion(textures.getTexture("images/tiles/caves/wall.png"));
        walls[1].flip(true, false);
        walls[2].flip(false, true);
    }

    void drawWall(Batch batch, Room room, int depth, int tileX, int tileY) {
        long hash = RandomHelper.getInstance().levelSeed(depth) ^ room.getIdentifier().hashCode()
                ^ tileX * 0x9E3779B97F4A7C15L ^ tileY * 0xC2B2AE3D27D4EB4FL ^ 0x4341564557414C4CL;
        hash ^= hash >>> 33;
        int choice = (int) (hash & 7);

        piece(batch, walls[choice < 6 ? 0 : choice - 5], tileX * TILE, tileY * TILE, TILE, TILE, WALL);
        piece(batch, pixel, tileX * TILE, tileY * TILE, TILE, TILE, WALL_WASH);
    }

    void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        boolean leftEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX - 1, tileY));
        boolean rightEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX + 1, tileY));
        float x = tileX * TILE, y = (tileY + 1) * TILE + 4f;
        rect(batch, x, y, 1, -10, 14, 2, SHADOW);
        rect(batch, x, y, 2, -11, 12, 1, FALLOFF);
        if (leftEnd || rightEnd || ((tileX + tileY) & 3) == 0) {
            rect(batch, x, y, 6, -13, 2, 7, TIMBER);
            rect(batch, x, y, 6, -12, 1, 5, TIMBER_EDGE);
            for (int brace = 0; brace < 3; brace++)
                rect(batch, x, y, 8 + brace, -11 + brace, 2, 2, TIMBER);
        }
        rect(batch, x, y, 0, -7, 16, 6, SIDE);
        TextureRegion material = leftEnd ? left : rightEnd ? right : ((tileX & 1) == 0 ? face : alternate);
        piece(batch, material, x, y - 7 * PIXEL, TILE, 6 * PIXEL, ROCK);
        rect(batch, x, y, 0, -1, 16, 1, LIP);

        rect(batch, x, y, 1, -8, 4, 1, SIDE);
        rect(batch, x, y, 7, -9, 3, 2, DARK);
        rect(batch, x, y, 12, -8, 3, 1, SIDE);
        if (leftEnd) rect(batch, x, y, 0, -6, 1, 5, SIDE);
        if (rightEnd) rect(batch, x, y, 15, -7, 1, 6, DARK);
    }

    void drawArchitecture(Batch batch, Room room, int depth) {
        if (sourceDepth != depth || !room.getIdentifier().equals(sourceId)) selectShafts(room, depth);
        if (rooms == null && MapHelper.getInstance().getPredecessorSnapshot() != null)
            rooms = new RoomBackdropRecess(RoomBackdropRecess.Kind.CAVES, walls[0], left, trim);
        if (rooms != null) rooms.draw(batch, room, depth);
        for (int i = 0; i < shaftCount; i++) {
            float x = shaftX[i], y = shaftY;
            if (rooms != null && rooms.covers(x - PIXEL, y - PIXEL, 26f * PIXEL, 35f * PIXEL)) continue;

            rect(batch, x, y, 0, 0, 24, 28, DARK);
            rect(batch, x, y, 2, 28, 20, 3, DARK);
            rect(batch, x, y, 4, 31, 16, 2, DARK);
            rect(batch, x, y, 3, 2, 3, 24, DISTANT);
            rect(batch, x, y, 18, 2, 2, 24, DISTANT);
            rect(batch, x, y, 5, 23, 14, 2, DISTANT);
            rect(batch, x, y, 6, 5, 1, 15, DISTANT);
            rect(batch, x, y, 15, 5, 1, 15, DISTANT);
            rect(batch, x, y, 6, 19, 10, 1, DISTANT);

            rect(batch, x, y, 2, 2, 20, 5, DISTANT);
            rect(batch, x, y, 3, 7, 7, 4, DISTANT);
            rect(batch, x, y, 16, 7, 5, 2, DISTANT);
            for (int brace = 0; brace < 6; brace++)
                rect(batch, x, y, 3 + brace * 3, 8 + brace * 2, 3, 2, OLD_TIMBER);
            rect(batch, x, y, 0, 1, 2, 27, OLD_TIMBER);
            rect(batch, x, y, 22, 1, 2, 27, OLD_TIMBER);
            rect(batch, x, y, 1, 27, 22, 2, OLD_TIMBER);
            piece(batch, trim, x + 4 * PIXEL, y + 30 * PIXEL, TILE, 3 * PIXEL, WALL);

            float packed = batch.getPackedColor();
            Color parent = batch.getColor();
            batch.setColor(parent.r, parent.g, parent.b, parent.a * GameSettingsHelper.getInstance().getVisualEffectIntensity());
            rect(batch, x, y, 2, 25, 1, 1, ORE);
            rect(batch, x, y, 20, 29, 1, 1, ORE);
            batch.setPackedColor(packed);
        }
        structure.draw(batch, room);
    }

    private void selectShafts(Room room, int depth) {
        sourceId = room.getIdentifier();
        sourceDepth = depth;
        shaftCount = 0;
        if (com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint.supports(room)
                || com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.BossArenaArchitecture.supports(room)) return;
        shaftY = ConstantsHelper.MIN_FLOOR * TILE + 9 * PIXEL;
        long hash = RandomHelper.getInstance().levelSeed(depth) ^ sourceId.hashCode() ^ 0x4341564553484146L;
        hash ^= hash >>> 32;
        for (int tile = 2 + (int) (hash & 3); tile < room.getWidth() - 3 && shaftCount < shaftX.length; tile += 3) {
            float x = tile * TILE;
            if (clearShaft(room, x - PIXEL, shaftY - PIXEL, 26 * PIXEL, 35 * PIXEL)) {
                shaftX[shaftCount++] = x;
                tile += 2;
            }
        }
    }

    private boolean clearShaft(Room room, float x, float y, float width, float height) {
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
