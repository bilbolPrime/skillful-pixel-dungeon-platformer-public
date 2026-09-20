package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.*;


final class SpecialRoomArchitecture {
    private static final float T = ConstantsHelper.TILE, P = T / 16f;
    private static final Color SHADE = new Color(.025f, .035f, .035f, .40f);
    private static final Color WOOD = Color.valueOf("464237"), WOOD_EDGE = Color.valueOf("69604A");
    private static final Color IRON = Color.valueOf("333E40"), METAL = Color.valueOf("667274");
    private static final Color GOLD = Color.valueOf("8E7946"), CLOTH = Color.valueOf("4C5651");
    private static final Color GREEN = Color.valueOf("425C3D"), LEAF = Color.valueOf("637750");
    private static final Color WATER = Color.valueOf("416D75"), GLASS = Color.valueOf("6E9892");
    private static final Color BOOKS = new Color(.60f, .64f, .58f, 1f);
    private final TextureRegion pixel, wall, books;
    private final TextureRegion[][] masonry = new TextureRegion[17][17];
    private final Color stone, edge, cloth;
    private Batch batch;
    private RoomFixtureObservation observation;
    private float ox, oy;

    SpecialRoomArchitecture(String theme) {
        TextureHelper textures = TextureHelper.GetSingleton();
        pixel = new TextureRegion(textures.getSolidPixel());
        wall = new TextureRegion(textures.getTexture("images/tiles/" + theme + "/wall.png"), 0, 0, 16, 16);
        books = new TextureRegion(textures.getTexture("images/tiles/" + theme + "/library.png"));
        stone = theme.equals("city") ? Color.valueOf("716A58") : theme.equals("halls") ? Color.valueOf("61586D")
                : theme.equals("caves") ? Color.valueOf("696055") : Color.valueOf("5D706D");
        edge = theme.equals("city") ? Color.valueOf("77725F") : theme.equals("halls") ? Color.valueOf("71647C")
                : Color.valueOf("61736E");
        cloth = theme.equals("halls") ? Color.valueOf("603A4F") : theme.equals("city") ? Color.valueOf("575775")
                : theme.equals("prison") ? Color.valueOf("67433D") : CLOTH;
    }

    void draw(Batch batch, Room room) {
        this.batch = batch;
        observation = MapHelper.getInstance().getRoomFixtureObservation();
        int w = (int)room.getWidth(), c = w / 2;
        try {
            if (room instanceof LibraryRoom) {
                bookcase(6, 3, w - 9);
                bookcase(8, 5, w - 12);
                readingDesk(c - 1, 3);
            } else if (room instanceof LaboratoryRoom) {
                rack(6, 4, 5, false); rack(12, 4, w - 14, false);
                laboratory(9, 6);
            } else if (room instanceof GardenRoom) {
                trellis(6, 4, 4); trellis(w - 7, 4, 4);
                vines(c - 2, 6, 4);
            } else if (room instanceof MagicWellRoom) {
                fountain(c - 2, 3);
                column(c - 5, 4, 3, false); column(c + 5, 4, 3, false);
            } else if (room instanceof CryptRoom) {
                arch(c - 3, 4, 7, 3, false);
                burialSeal(c - 1, 6);
            } else if (room instanceof GraveyardRoom) {
                fence(5, 3, 5); fence(w - 10, 3, 7);
                arch(c - 2, 3, 5, 3, false);
            } else if (room instanceof PoolRoom) {
                sluice(7, 5); sluice(w - 7, 5);
                gauge(c - 1, 3);
            } else if (room instanceof TreasuryRoom) {
                arch(c - 4, 4, 9, 3, true);
                banner(c - 1, 6, true);
            } else if (room instanceof ArmoryRoom) {
                rack(6, 4, 4, true); rack(w - 6, 4, 4, true);
                banner(c - 1, 6, false);
            } else if (room instanceof StorageRoom) {
                stores(6, 4, 4); stores(11, 5, w - 13); stores(w - 8, 7, 4);
            } else if (room instanceof VaultRoom) {
                vault(w - 8, 6);
                column(7, 4, 3, true); column(w - 2, 4, 3, true);
            } else if (room instanceof TrapsRoom) {
                for (int tile : new int[]{7, 8, 9, 11, 12, 13}) mechanism(tile, 3);
                arch(w - 6, 4, 5, 2, false);
            } else if (room instanceof MerchantRoom) {
                shopShelf(5, 4); shopShelf(5, 6);
                canopy(w - 5, 3);
            } else if (room instanceof MercenaryRoom) {
                rack(6, 4, 4, false);
                cot(8, 6);
                banner(w - 5, 5, false);
            }
        } finally {
            observation.endFocal(false);
            this.batch = null;
        }
    }

    private void begin(float tileX, float floor, float width, float height) {
        ox = tileX * T; oy = floor * T;
        observation.beginFocal(pixel, ox, oy, width * T, height * T);
    }
    private void end() { observation.endFocal(true); }

    private void bookcase(int x, int floor, int count) {
        begin(x, floor, count, 1);
        rect(0, 0, count * 16, 16, SHADE);
        for (int i = 0; i < count; i++) piece(books, i * 16 + 1, 1, 14, 14, BOOKS);
        rect(0, 0, count * 16, 1, WOOD_EDGE); rect(0, 15, count * 16, 1, WOOD_EDGE);
        for (int i = 0; i <= count; i += 3) rect(Math.min(count * 16 - 1, i * 16), 0, 1, 16, WOOD);
        end();
    }
    private void readingDesk(int x, int floor) {
        begin(x, floor, 2, 1);
        rect(2, 0, 3, 9, WOOD); rect(26, 0, 3, 9, WOOD);
        rect(0, 8, 32, 2, WOOD_EDGE); rect(1, 10, 30, 1, WOOD);

        rect(10, 11, 12, 2, cloth); rect(11, 13, 10, 1, METAL);
        end();
    }
    private void rack(int x, int floor, int width, boolean hooks) {
        begin(x, floor, width, 1.75f);
        rect(1, 0, 2, 27, WOOD); rect(width * 16 - 3, 0, 2, 27, WOOD);
        rect(0, 24, width * 16, 2, WOOD_EDGE); rect(2, 5, width * 16 - 4, 1, WOOD);
        if (hooks) for (int i = 9; i < width * 16 - 5; i += 13) {
            rect(i, 20, 1, 4, METAL); rect(i - 2, 19, 3, 1, IRON);
        }
        end();
    }
    private void laboratory(int x, int floor) {
        begin(x, floor, 4, 2.5f);
        rect(3, 0, 2, 8, IRON); rect(55, 0, 2, 8, IRON);
        rect(0, 7, 64, 2, WOOD_EDGE);
        rect(10, 9, 12, 12, IRON); rect(9, 11, 14, 8, IRON); rect(11, 10, 10, 9, WATER);
        rect(11, 18, 10, 4, METAL); rect(15, 22, 3, 13, METAL);
        rect(18, 33, 25, 2, METAL); rect(41, 20, 2, 15, METAL);
        rect(36, 10, 16, 10, IRON); rect(35, 12, 18, 6, IRON); rect(37, 11, 14, 7, WATER);
        rect(38, 16, 2, 3, GLASS); rect(12, 13, 2, 5, GLASS);
        rect(27, 11, 3, 15, IRON); rect(26, 26, 5, 2, GOLD);
        end();
    }
    private void trellis(int x, int floor, int width) {
        begin(x, floor, width, 1.75f);
        for (int column = 4; column < width * 16; column += 12) rect(column, 0, 1, 27, WOOD);
        for (int row = 7; row < 28; row += 10) rect(2, row, width * 16 - 4, 1, WOOD);
        leaves(width * 16, 24);
        end();
    }
    private void vines(int x, int floor, int width) {
        begin(x, floor, width, 2);
        rect(1, 28, width * 16 - 2, 2, WOOD);
        leaves(width * 16, 27);
        end();
    }
    private void leaves(int width, int height) {
        for (int i = 7; i < width - 3; i += 13) {
            rect(i, 2, 1, height - 2, GREEN);
            for (int y = 6; y < height; y += 8) {
                rect(i - 3, y + (i % 3), 4, 2, LEAF);
                rect(i + 1, y + 3, 4, 2, GREEN);
            }
        }
    }
    private void fountain(int x, int floor) {
        begin(x, floor, 5, 3.5f);
        stoneStrip(10, 0, 5, 42); stoneStrip(65, 0, 5, 42);
        stoneStrip(15, 40, 50, 5);
        rect(26, 8, 28, 2, edge); stoneStrip(31, 10, 18, 33);
        rect(36, 24, 8, 9, IRON); rect(38, 8, 4, 18, WATER);
        rect(38, 13, 1, 10, GLASS);
        stoneStrip(14, 3, 52, 4); rect(18, 7, 44, 1, GLASS);
        rect(24, 1, 32, 2, edge);
        end();
    }
    private void arch(int x, int floor, int width, int height, boolean gilded) {
        begin(x, floor, width, height);
        int top = height * 16, right = width * 16;
        stoneStrip(0, 0, 6, top - 5); stoneStrip(right - 6, 0, 6, top - 5);
        stoneStrip(5, top - 9, 6, 5); stoneStrip(right - 11, top - 9, 6, 5);
        stoneStrip(10, top - 5, right - 20, 5);
        rect(1, 0, 1, top - 7, gilded ? GOLD : edge);
        rect(right - 7, 0, 1, top - 7, gilded ? GOLD : edge);
        rect(12, top - 6, right - 24, 1, gilded ? GOLD : IRON);
        end();
    }
    private void column(int x, int floor, int height, boolean gilded) {
        begin(x, floor, 1, height);
        stoneStrip(5, 1, 6, height * 16 - 2);
        rect(3, 0, 10, 2, edge); rect(3, height * 16 - 2, 10, 2, edge);
        rect(5, 2, 1, height * 16 - 4, gilded ? GOLD : METAL);
        end();
    }
    private void burialSeal(int x, int floor) {
        begin(x, floor, 3, 1.5f);
        stoneStrip(7, 3, 34, 16);
        rect(21, 6, 6, 10, IRON); rect(17, 11, 14, 3, IRON);
        rect(21, 7, 1, 8, edge);
        end();
    }
    private void fence(int x, int floor, int width) {
        begin(x, floor, width, 1.75f);
        rect(0, 7, width * 16, 2, IRON); rect(0, 20, width * 16, 1, METAL);
        for (int i = 2; i < width * 16; i += 9) {
            rect(i, 0, 2, 25, IRON); rect(i, 25, 1, 2, METAL);
        }
        end();
    }
    private void sluice(int x, int floor) {
        begin(x, floor, 3, 2);
        rect(4, 4, 40, 25, SHADE);
        stoneStrip(1, 0, 5, 32); stoneStrip(42, 0, 5, 32); stoneStrip(5, 27, 37, 5);
        for (int i = 10; i < 41; i += 6) rect(i, 3, 2, 24, IRON);
        rect(6, 15, 36, 2, METAL); rect(8, 3, 32, 1, WATER);
        end();
    }
    private void gauge(int x, int floor) {
        begin(x, floor, 1, 3);
        stoneStrip(3, 0, 7, 48);
        for (int y = 6; y < 44; y += 7) rect(5, y, y % 2 == 0 ? 5 : 3, 1, METAL);
        end();
    }
    private void banner(int x, int floor, boolean gilt) {
        begin(x, floor, 3, 2.5f);
        rect(5, 37, 38, 2, WOOD_EDGE); rect(10, 7, 28, 29, cloth);
        rect(11, 4, 12, 3, cloth); rect(25, 4, 12, 3, cloth);
        rect(12, 7, 1, 28, gilt ? GOLD : IRON); rect(35, 7, 1, 28, gilt ? GOLD : IRON);
        rect(19, 20, 10, 9, gilt ? GOLD : METAL); rect(21, 17, 6, 3, gilt ? GOLD : METAL);
        rect(23, 20, 2, 9, cloth);
        end();
    }
    private void stores(int x, int floor, int width) {
        begin(x, floor, width, 1.75f);
        rect(1, 0, 2, 27, WOOD); rect(width * 16 - 3, 0, 2, 27, WOOD);
        rect(0, 25, width * 16, 2, WOOD_EDGE);
        rect(2, 6, width * 16 - 4, 2, WOOD_EDGE);
        end();
        for (int i = 5; i < width * 16 - 12; i += 18) {
            begin(x + i / 16f, floor + .5f, 1, 1);
            crate(0, 0, 13);
            end();
        }
    }
    private void crate(int x, int y, int size) {
        rect(x, y, size, size, WOOD); rect(x + 1, y + 1, size - 2, size - 2, SHADE);
        rect(x, y, size, 1, WOOD_EDGE); rect(x, y + size - 1, size, 1, WOOD_EDGE);
        for (int i = 1; i < size - 1; i++) rect(x + i, y + i, 1, 1, WOOD_EDGE);
    }
    private void vault(int x, int floor) {
        begin(x, floor, 6, 3);

        stoneStrip(0, 0, 96, 43);
        for (int i = 6; i < 91; i += 21) {
            rect(i, 3, 2, 38, IRON); rect(i, 7, 2, 2, GOLD); rect(i, 34, 2, 2, GOLD);
        }
        rect(2, 21, 92, 2, IRON); rect(2, 40, 92, 2, GOLD);
        end();
    }
    private void mechanism(int x, int floor) {
        begin(x, floor, 1, 2.25f);
        rect(3, 5, 10, 22, IRON); rect(4, 6, 1, 19, METAL);
        rect(6, 27, 4, 8, IRON); rect(5, 34, 6, 1, METAL);
        for (int y = 9; y < 25; y += 5) rect(7, y, 4, 1, GOLD);
        end();
    }
    private void shopShelf(int x, int floor) {
        begin(x, floor, 9, 1.75f);
        rect(1, 0, 142, 27, SHADE);
        rect(0, 0, 2, 28, WOOD_EDGE); rect(142, 0, 2, 28, WOOD_EDGE);
        rect(0, 26, 144, 2, WOOD); rect(2, 24, 140, 1, WOOD_EDGE);
        for (int i = 16; i < 140; i += 16) rect(i, 1, 1, 23, WOOD);
        end();
    }
    private void canopy(int x, int floor) {
        begin(x, floor, 4, 2.5f);
        rect(0, 32, 64, 3, WOOD_EDGE);
        rect(3, 27, 58, 5, cloth);
        for (int i = 5; i < 57; i += 12) rect(i, 25, 6, 3, cloth);
        rect(2, 0, 2, 32, WOOD); rect(60, 0, 2, 32, WOOD);
        end();
    }
    private void cot(int x, int floor) {
        begin(x, floor, 4, 1.5f);
        rect(4, 0, 3, 12, WOOD); rect(57, 0, 3, 12, WOOD);
        rect(4, 10, 56, 3, WOOD_EDGE); rect(8, 13, 48, 4, cloth);
        rect(9, 17, 9, 2, METAL); rect(23, 13, 2, 4, WOOD);
        end();
    }
    private void stoneStrip(float x, float y, float width, float height) {

        for (float yy = 0; yy < height; yy += 16) for (float xx = 0; xx < width; xx += 16)
        {
            int w = (int)Math.min(16, width - xx), h = (int)Math.min(16, height - yy);
            if (masonry[w][h] == null) masonry[w][h] = new TextureRegion(wall, 0, 0, w, h);
            piece(masonry[w][h], x + xx, y + yy, w, h, stone);
        }
    }
    private void rect(float x, float y, float width, float height, Color tint) { piece(pixel, x, y, width, height, tint); }
    private void piece(TextureRegion region, float x, float y, float width, float height, Color tint) {
        float saved = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * tint.r, parent.g * tint.g, parent.b * tint.b, parent.a * tint.a);
        batch.draw(region, ox + x * P, oy + y * P, width * P, height * P);
        observation.focalPiece(region, ox + x * P, oy + y * P, width * P, height * P, tint);
        batch.setPackedColor(saved);
    }
}
