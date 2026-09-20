package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;


public final class SpecialRoomFocalPoint {
    private static final float TILE = ConstantsHelper.TILE, PIXEL = TILE / 16f;
    private static final Color BACK = Color.valueOf("151F23");
    private static final Color INSET = Color.valueOf("212F32");
    private static final Color LINE = Color.valueOf("405153");
    private static final Color OLD_WOOD = Color.valueOf("48493A");
    private static final Color LEAF = new Color(0.34f, 0.48f, 0.36f, 1f);
    private static final Color WATER = Color.valueOf("3B6468");
    private static final Color OLD_GOLD = Color.valueOf("776B43");
    private static final Color RELIEF = new Color(0.42f, 0.49f, 0.49f, 1f);
    private final Color masonryTint;
    private final TextureRegion pixel, masonry, books, plant, flask, sword;
    private final SpecialRoomArchitecture architecture;
    private String sourceId;
    private int family;
    private float x, y;
    private boolean visible;

    public SpecialRoomFocalPoint(String template) {
        architecture = new SpecialRoomArchitecture(template);
        TextureHelper textures = TextureHelper.GetSingleton();
        pixel = new TextureRegion(textures.getSolidPixel());
        boolean sewers = template.equals("sewers");
        Texture stone = textures.getTexture("images/tiles/" + template + (sewers ? "/wall.png" : "/raised.png"));
        masonry = new TextureRegion(stone, 0, sewers ? 0 : 112, 4, 16);
        masonryTint = template.equals("city") ? new Color(0.47f, 0.44f, 0.39f, 1f)
                : template.equals("halls") ? new Color(0.38f, 0.38f, 0.43f, 1f) : new Color(0.40f, 0.46f, 0.46f, 1f);
        books = asset("images/tiles/" + template + "/library.png");
        plant = new TextureRegion(textures.getTexture("images/misc/plants.png"), 64, 0, 16, 16);
        flask = asset("images/misc/extracted items/POTION_MANA.png");
        sword = asset("images/misc/extracted items/SWORD.png");
    }

    public static boolean supports(Room room) { return family(room) != 0; }

    public static boolean supportsTemplate(String template) {
        return "sewers".equals(template) || "prison".equals(template) || "caves".equals(template)
                || "city".equals(template) || "halls".equals(template);
    }

    private static int family(Room room) {
        if (room == null) return 0;
        switch (room.getClass().getSimpleName()) {
            case "LibraryRoom": return 1;
            case "GardenRoom": return 2;
            case "LaboratoryRoom": return 3;
            case "MagicWellRoom": return 4;
            case "CryptRoom": return 5;
            case "GraveyardRoom": return 14;
            case "PoolRoom": return 6;
            case "TreasuryRoom": case "TreasureRoom": return 7;
            case "ArmoryRoom": return 8;
            case "StorageRoom": return 9;
            case "VaultRoom": return 10;
            case "TrapsRoom": return 11;
            case "MerchantRoom": return 12;
            case "MercenaryRoom": return 13;
            default: return 0;
        }
    }

    public void draw(Batch batch, Room room) {

        if (!(room instanceof com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.TreasureRoom)) {
            architecture.draw(batch, room);
            return;
        }
        if (!room.getIdentifier().equals(sourceId)) select(room);
        if (!visible) return;
        RoomFixtureObservation observation = MapHelper.getInstance().getRoomFixtureObservation();
        observation.beginFocal(pixel, x, y, 40f * PIXEL, 20f * PIXEL);
        boolean completed = false;
        try {
            if (family == 2 || family == 14) {
                rect(batch, 0, 0, 40, 3, OLD_WOOD);
            } else {
                rect(batch, 0, 0, 40, 20, BACK);
                rect(batch, 4, 2, 32, 16, INSET);
                piece(batch, masonry, 0, 2, 4, 16, masonryTint);
                piece(batch, masonry, 36, 2, 4, 16, masonryTint);
                rect(batch, 0, 0, 40, 2, LINE);
                rect(batch, 0, 18, 40, 2, LINE);
            }

            switch (family) {
                case 1:
                    piece(batch, books, 4, 2, 16, 16, RELIEF);
                    piece(batch, books, 20, 2, 16, 16, RELIEF);
                    break;
                case 2:
                    rect(batch, 6, 3, 28, 3, OLD_WOOD);
                    prop(batch, plant, 6, 5, 8, LEAF);
                    prop(batch, plant, 14, 5, 12, LEAF);
                    prop(batch, plant, 26, 5, 8, LEAF);
                    break;
                case 3:
                    rect(batch, 6, 4, 28, 2, OLD_WOOD);
                    rect(batch, 8, 2, 2, 2, LINE); rect(batch, 30, 2, 2, 2, LINE);
                    prop(batch, flask, 7, 6, 8, RELIEF);
                    prop(batch, flask, 17, 6, 10, RELIEF);
                    prop(batch, flask, 28, 6, 6, RELIEF);
                    rect(batch, 11, 15, 19, 1, LINE);
                    break;
                case 4:
                    rect(batch, 16, 5, 8, 11, LINE);
                    rect(batch, 18, 10, 4, 3, BACK);
                    rect(batch, 19, 5, 2, 5, WATER);
                    rect(batch, 9, 3, 22, 4, LINE);
                    rect(batch, 11, 6, 18, 1, WATER);
                    break;
                case 5:

                    rect(batch, 8, 3, 3, 14, LINE); rect(batch, 29, 3, 3, 14, LINE);
                    rect(batch, 10, 15, 20, 2, LINE);
                    rect(batch, 13, 3, 14, 2, LINE);
                    break;
                case 14:
                    rect(batch, 6, 2, 28, 3, OLD_WOOD);
                    for (int marker = 8; marker <= 28; marker += 10) {
                        rect(batch, marker, 5, 3, 7, LINE);
                        rect(batch, marker-2, 10, 7, 2, LINE);
                    }
                    break;
                case 6:
                    rect(batch, 7, 3, 26, 5, LINE);
                    rect(batch, 9, 6, 22, 1, WATER);
                    rect(batch, 12, 4, 7, 1, WATER); rect(batch, 23, 4, 5, 1, WATER);
                    rect(batch, 9, 9, 2, 7, LINE); rect(batch, 29, 9, 2, 7, LINE);
                    rect(batch, 9, 15, 22, 1, LINE);
                    break;
                case 7:

                    rect(batch, 7, 3, 26, 3, LINE);
                    rect(batch, 10, 6, 20, 1, OLD_GOLD);
                    rect(batch, 12, 15, 16, 1, OLD_GOLD);
                    break;
                case 8:
                    rect(batch, 7, 4, 26, 2, OLD_WOOD);
                    rect(batch, 9, 6, 2, 11, OLD_WOOD); rect(batch, 29, 6, 2, 11, OLD_WOOD);
                    for (int hook = 13; hook < 29; hook += 5) rect(batch, hook, 12, 2, 2, LINE);
                    break;
                case 9:
                    crate(batch, 7, 3, 11); crate(batch, 20, 3, 11); crate(batch, 14, 11, 7);
                    break;
                case 10:
                    rect(batch, 6, 2, 2, 16, OLD_GOLD); rect(batch, 32, 2, 2, 16, OLD_GOLD);
                    rect(batch, 8, 15, 24, 2, LINE);
                    rect(batch, 10, 3, 20, 2, LINE);
                    break;
                case 11:
                    rect(batch, 8, 3, 24, 13, LINE);
                    rect(batch, 10, 5, 20, 9, BACK);
                    for (int bar = 12; bar < 29; bar += 4) rect(batch, bar, 5, 1, 9, OLD_WOOD);
                    rect(batch, 10, 9, 20, 1, OLD_WOOD);
                    break;
                case 12:

                    rect(batch, 7, 3, 26, 3, OLD_WOOD);
                    rect(batch, 19, 6, 2, 11, OLD_GOLD);
                    rect(batch, 11, 14, 18, 1, OLD_GOLD);
                    rect(batch, 12, 10, 1, 4, LINE); rect(batch, 27, 10, 1, 4, LINE);
                    rect(batch, 9, 9, 7, 1, OLD_GOLD); rect(batch, 24, 9, 7, 1, OLD_GOLD);
                    break;
                case 13:
                    rect(batch, 9, 15, 22, 1, OLD_WOOD);
                    rect(batch, 14, 5, 12, 10, LINE);
                    rect(batch, 17, 3, 6, 2, LINE);
                    prop(batch, sword, 16, 6, 8, RELIEF);
                    break;
                default: break;
            }
            completed = true;
        } finally { observation.endFocal(completed); }
    }

    private void select(Room room) {
        sourceId = room.getIdentifier(); family = family(room); visible = false;
        if (family == 0) return;
        if (room.getLayout().hasPlannedFeature()) {

            x = room.getLayout().feature().x - 20 * PIXEL;
            y = room.getLayout().feature().y;
            visible = true;
            return;
        }
        float center = (family == 12 || family == 13) ? (room.getWidth() - 3) * TILE + ConstantsHelper.UNIT_DIMENSIONS / 2f
                : room.getWidth() * TILE / 2f;
        y = (family == 12 || family == 13) ? ConstantsHelper.MIN_FLOOR * TILE + 8 * PIXEL : 5 * TILE + 3 * PIXEL;

        for (int attempt = 0; attempt < 7; attempt++) {
            int offset = attempt == 0 ? 0 : ((attempt + 1) / 2) * (attempt % 2 == 1 ? 2 : -2);
            x = Math.round((center - 20 * PIXEL + offset * TILE) / PIXEL) * PIXEL;
            if (x < PIXEL || x + 40 * PIXEL > room.getWidth() * TILE - PIXEL) continue;
            boolean blocked = false;
            for (Door door : room.getDoors()) {
                if (x < door.x + TILE + 4 * PIXEL && x + 40 * PIXEL > door.x - 4 * PIXEL &&
                        y < door.y + TILE + 8 * PIXEL && y + 20 * PIXEL > door.y - 2 * PIXEL) blocked = true;
            }
            if (!blocked) { visible = true; return; }
        }
    }

    private void grille(Batch batch) {
        for (int bar = 8; bar < 35; bar += 6) rect(batch, bar, 2, 1, 16, LINE);
        rect(batch, 5, 11, 30, 1, LINE);
    }

    private void crate(Batch batch, int px, int py, int size) {
        rect(batch, px, py, size, size, OLD_WOOD);
        rect(batch, px + 1, py + 1, size - 2, size - 2, BACK);
        for (int step = 1; step < size - 1; step++) rect(batch, px + step, py + step, 1, 1, LINE);
    }

    private void prop(Batch batch, TextureRegion region, int px, int py, int size, Color tint) {
        float scale = (float) size / Math.max(region.getRegionWidth(), region.getRegionHeight());
        piece(batch, region, px, py, region.getRegionWidth() * scale, region.getRegionHeight() * scale, tint);
    }

    private void rect(Batch batch, int px, int py, int width, int height, Color tint) {
        piece(batch, pixel, px, py, width, height, tint);
    }

    private void piece(Batch batch, TextureRegion region, float px, float py, float width, float height, Color tint) {
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * tint.r, parent.g * tint.g, parent.b * tint.b, parent.a * tint.a);
        batch.draw(region, x + px * PIXEL, y + py * PIXEL, width * PIXEL, height * PIXEL);
        MapHelper.getInstance().getRoomFixtureObservation().focalPiece(region, x + px * PIXEL, y + py * PIXEL,
                width * PIXEL, height * PIXEL, tint);
        batch.setPackedColor(packed);
    }

    private static TextureRegion asset(String path) { return new TextureRegion(TextureHelper.GetSingleton().getTexture(path)); }
}
