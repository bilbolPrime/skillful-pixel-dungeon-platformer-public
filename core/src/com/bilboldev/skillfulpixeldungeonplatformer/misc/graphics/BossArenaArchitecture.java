package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;


public final class BossArenaArchitecture {
    private static final float TILE = ConstantsHelper.TILE, P = TILE / 16f;
    private static final Color RECESS = Color.valueOf("141A1E"), INSET = Color.valueOf("242C30");
    private static final Color EDGE = Color.valueOf("414B4B"), IRON = Color.valueOf("384243");
    private static final Color DAMP = Color.valueOf("344744"), BRASS = Color.valueOf("685E47");
    private static final Color FABRIC = Color.valueOf("3E3541");
    private static final Color STONE = new Color(0.48f, 0.49f, 0.48f, 1f);
    private static final Color AMBER_POOL = new Color(0.65f, 0.45f, 0.23f, 0.10f);
    private static final Color VIOLET_POOL = new Color(0.39f, 0.29f, 0.44f, 0.10f);
    private final TextureRegion pixel, column;
    private final RoomLighting lighting = new RoomLighting();

    public BossArenaArchitecture(String template) {
        TextureHelper textures = TextureHelper.GetSingleton();
        Texture sheet = textures.getTexture("images/tiles/" + template
                + (template.equals("sewers") ? "/wall.png" : "/raised.png"));
        pixel = new TextureRegion(textures.getSolidPixel());
        column = new TextureRegion(sheet, 0, template.equals("sewers") ? 0 : 112, 4, 16);
    }

    public static boolean supports(Room room) {
        if (room == null) return false;
        switch (room.getClass().getSimpleName()) {
            case "GooRoom": case "TenguRoom": case "DM300Room": case "KingRoom": case "YogRoom": return true;
            default: return false;
        }
    }

    public void draw(Batch batch, Room room) {
        switch (room.getClass().getSimpleName()) {
            case "GooRoom":

                for (int tile = 6; tile <= 14; tile += 4) {
                    float x = tile * TILE - 14 * P, y = ConstantsHelper.MIN_FLOOR * TILE + 2 * P;
                    if (!visible(x, y, 28 * P, 28 * P)) continue;
                    frame(batch, x, y, 28, 28);
                    returns(batch, x, y, 28, 28);
                    rect(batch, x, y, 5, 5, 18, 16, RECESS);
                    for (int bar = 8; bar < 23; bar += 4) rect(batch, x, y, bar, 5, 1, 16, IRON);
                    rect(batch, x, y, 6, 4, 16, 1, DAMP);
                    rect(batch, x, y, 3, 21, 22, 2, DAMP);
                    fixture(batch, room, x + 14*P, y + 25*P, AMBER_POOL);
                }
                break;
            case "TenguRoom": {
                float x = room.getWidth() * TILE / 2f - 40 * P, y = 6 * TILE;
                if (!visible(x, y, 80 * P, 56 * P)) break;
                frame(batch, x, y, 80, 56);
                for (int bar = 10; bar < 75; bar += 8) rect(batch, x, y, bar, 4, 1, 48, IRON);
                rect(batch, x, y, 5, 19, 70, 2, EDGE); rect(batch, x, y, 5, 37, 70, 2, EDGE);
                rect(batch, x, y, 34, 22, 12, 12, RECESS);
                rect(batch, x, y, 38, 25, 4, 6, BRASS);
                break;
            }
            case "DM300Room":
                for (int tile = 7; tile <= 21; tile += 14) {
                    float x = tile * TILE - 20 * P, y = 5 * TILE;
                    if (!visible(x, y, 40 * P, 40 * P)) continue;
                    frame(batch, x, y, 40, 40);
                    returns(batch, x, y, 40, 40);
                    rect(batch, x, y, 9, 8, 22, 24, RECESS);
                    for (int rib = 11; rib < 30; rib += 4) rect(batch, x, y, 11, rib, 18, 1, IRON);
                    for (int brace = 0; brace < 7; brace++) {
                        rect(batch, x, y, 5 + brace * 4, 5 + brace * 4, 4, 2, EDGE);
                        rect(batch, x, y, 31 - brace * 4, 5 + brace * 4, 4, 2, EDGE);
                    }
                    fixture(batch, room, x + 20*P, y + 35*P, AMBER_POOL);
                }
                break;
            case "KingRoom":
                for (int tile = 8; tile <= 21; tile += 13) {
                    float x = tile * TILE + ConstantsHelper.UNIT_DIMENSIONS / 2f - 24 * P, y = 6 * TILE;
                    if (!visible(x, y, 48 * P, 64 * P)) continue;
                    frame(batch, x, y, 48, 64);
                    returns(batch, x, y, 48, 64);
                    rect(batch, x, y, 12, 10, 24, 42, FABRIC);
                    rect(batch, x, y, 16, 6, 16, 4, FABRIC);
                    rect(batch, x, y, 10, 52, 28, 2, BRASS);
                    rect(batch, x, y, 17, 32, 14, 3, BRASS);
                    for (int tooth = 17; tooth <= 29; tooth += 6) rect(batch, x, y, tooth, 35, 2, 5, BRASS);
                    fixture(batch, room, x + 24 * P, y + 56 * P, AMBER_POOL);
                }
                break;
            case "YogRoom": {
                float x = 14 * TILE + ConstantsHelper.UNIT_DIMENSIONS / 2f - 40 * P, y = 5 * TILE;
                if (!visible(x, y, 80 * P, 64 * P)) break;
                frame(batch, x, y, 80, 64);
                returns(batch, x, y, 80, 64);

                rect(batch, x, y, 10, 5, 60, 47, RECESS);
                for (int step = 0; step < 4; step++) {
                    rect(batch, x, y, 8 + step * 4, 50 + step * 2, 4, 2, EDGE);
                    rect(batch, x, y, 68 - step * 4, 50 + step * 2, 4, 2, EDGE);
                }
                rect(batch, x, y, 24, 58, 32, 2, EDGE);
                rect(batch, x, y, 8, 12, 2, 34, FABRIC); rect(batch, x, y, 70, 12, 2, 34, FABRIC);
                fixture(batch, room, x + 8 * P, y + 48 * P, VIOLET_POOL);
                fixture(batch, room, x + 72 * P, y + 48 * P, VIOLET_POOL);
                break;
            }
            default: break;
        }
    }

    private void frame(Batch batch, float x, float y, int width, int height) {
        rect(batch, x, y, 0, 0, width, height, RECESS);
        rect(batch, x, y, 4, 3, width - 8, height - 6, INSET);
        for (int row = 0; row < height; row += 16) {
            int remaining = Math.min(16, height - row);
            piece(batch, column, x, y + row * P, 4 * P, remaining * P, STONE);
            piece(batch, column, x + (width - 4) * P, y + row * P, 4 * P, remaining * P, STONE);
        }
        rect(batch, x, y, 0, 0, width, 2, EDGE);
        rect(batch, x, y, 0, height - 2, width, 2, EDGE);
    }


    private void returns(Batch batch, float x, float y, int width, int height) {
        rect(batch, x, y, 0, 2, 1, height-4, EDGE);
        rect(batch, x, y, 3, 2, 1, height-4, RECESS);
        rect(batch, x, y, width-4, 2, 1, height-4, EDGE);
        rect(batch, x, y, width-1, 2, 1, height-4, RECESS);
    }

    private void fixture(Batch batch, Room room, float x, float y, Color color) {
        rect(batch, x, y, -2, -3, 4, 6, IRON);
        rect(batch, x, y, -1, -2, 2, 4, color == VIOLET_POOL ? FABRIC : BRASS);
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * color.a / .24f);

        lighting.draw(batch, room, x, y, 192, color.r, color.g, color.b);
        batch.setPackedColor(packed);
    }

    private static boolean visible(float x, float y, float width, float height) {
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        return camera == null || (x < camera.position.x + camera.viewportWidth * camera.zoom / 2f
                && x + width > camera.position.x - camera.viewportWidth * camera.zoom / 2f
                && y < camera.position.y + camera.viewportHeight * camera.zoom / 2f
                && y + height > camera.position.y - camera.viewportHeight * camera.zoom / 2f);
    }

    private void rect(Batch batch, float x, float y, int px, int py, int width, int height, Color color) {
        piece(batch, pixel, x + px * P, y + py * P, width * P, height * P, color);
    }

    private void piece(Batch batch, TextureRegion region, float x, float y, float width, float height, Color color) {
        float packed = batch.getPackedColor(); Color parent = batch.getColor();
        batch.setColor(parent.r * color.r, parent.g * color.g, parent.b * color.b, parent.a * color.a);
        batch.draw(region, x, y, width, height);
        batch.setPackedColor(packed);
    }
}
