package com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.Level;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.SewersLevel;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.themes.Theme;

public class Sewers extends Theme {
    private static final long WALL_COSMETIC_SALT = 0x534557455257414CL;
    private static final Color PLATFORM_LIP = Color.valueOf("91A09A");
    private static final Color PLATFORM_FACE = Color.valueOf("536466");
    private static final Color PLATFORM_SIDE = Color.valueOf("344245");
    private static final Color PLATFORM_UNDERSIDE = Color.valueOf("161E20");
    private static final Color PLATFORM_WALL_SHADOW = new Color(0.04f, 0.06f, 0.065f, 0.24f);
    private static final Color PLATFORM_WALL_FALLOFF = new Color(0.04f, 0.06f, 0.065f, 0.12f);
    private Texture wallPixel;
    private TextureRegion[] platformFaces;
    private SewersArchitecture architecture;
    {
        int tile = (int) ConstantsHelper.TILE;
        template = "sewers";
        door = new GameSprite("images/tiles/" + template + "/door.png", tile , tile);
        closedDoor = new GameSprite("images/tiles/" + template + "/door-closed.png", tile, tile);
        lockedDoor = new GameSprite("images/tiles/" + template + "/door-locked.png", tile, tile);
        cagedDoor = new GameSprite("images/tiles/" + template + "/door-cage.png", tile, tile);
        uncagedDoor = new GameSprite("images/tiles/" + template + "/door-uncaged.png", tile, tile);
        doorLevelEntrySign = new GameSprite("images/tiles/" + template + "/door-sign-up.png", tile / 2, tile / 2);
        doorLevelExitSign = new GameSprite("images/tiles/" + template + "/door-sign-down.png", tile / 2, tile / 2);
        doorLibrarySign = new GameSprite("images/tiles/" + template + "/door-sign-library.png", tile / 2, tile / 2);
        doorTreasureSign = new GameSprite("images/tiles/" + template + "/door-sign-treasure.png", tile / 2, tile / 2);
        doorMerchantSign = new GameSprite("images/tiles/" + template + "/door-sign-merchant.png", tile / 2, tile / 2);
        doorArmorySign = new GameSprite("images/tiles/" + template + "/door-sign-armory.png", tile / 2, tile / 2);
        doorGardenSign = new GameSprite("images/tiles/" + template + "/door-sign-garden.png", tile / 2, tile / 2);
        doorLaboratorySign = new GameSprite("images/tiles/" + template + "/door-sign-laboratory.png", tile / 2, tile / 2);
        doorMagicWellSign = new GameSprite("images/tiles/" + template + "/door-sign-magic-well.png", tile / 2, tile / 2);
        doorCryptSign = new GameSprite("images/tiles/" + template + "/door-sign-crypt.png", tile / 2, tile / 2);
        doorPoolSign = new GameSprite("images/tiles/" + template + "/door-sign-pool.png", tile / 2, tile / 2);
        doorTreasurySign = new GameSprite("images/tiles/" + template + "/door-sign-treasury.png", tile / 2, tile / 2);
        doorTrapsSign = new GameSprite("images/tiles/" + template + "/door-sign-traps.png", tile / 2, tile / 2);
        doorStorageSign = new GameSprite("images/tiles/" + template + "/door-sign-storage.png", tile / 2, tile / 2);
        doorVaultSign = new GameSprite("images/tiles/" + template + "/door-sign-vault.png", tile / 2, tile / 2);
        doorGraveyardSign = new GameSprite("images/tiles/" + template + "/door-sign-graveyard.png", tile / 2, tile / 2);
        doorMercenarySign = new GameSprite("images/tiles/" + template + "/door-sign-mercenary-warrior.png", tile / 2, tile / 2);
        wall = new GameSprite("images/tiles/" + template + "/wall.png", tile, tile);
        wallFading = new GameSprite("images/tiles/" + template + "/wall-fading.png", tile, tile);
        wall.setColor(new Color(0.60f, 0.74f, 0.77f, 1f));
        wallFading.setColor(new Color(0.60f, 0.74f, 0.77f, 1f));
        floor = new GameSprite("images/tiles/" + template + "/floor.png", tile, tile);
        platform = new GameSprite("images/tiles/" + template + "/platform.png", tile, tile);
        sign = new GameSprite("images/tiles/" + template + "/sign.png", tile, tile);
        fader = new GameSprite("images/tiles/" + template + "/fader.png", tile, tile, 0.65f);
        library = new GameSprite("images/tiles/" + template + "/library.png", tile, tile);
    }

    @Override
    public void drawWall(Batch batch, Room room, int depth, int tileX, int tileY) {
        super.drawWall(batch, room, depth, tileX, tileY);

        long hash = RandomHelper.getInstance().levelSeed(depth) ^ room.getIdentifier().hashCode() ^ WALL_COSMETIC_SALT;
        hash ^= tileX * 0x9E3779B97F4A7C15L ^ tileY * 0xC2B2AE3D27D4EB4FL;
        hash = (hash ^ (hash >>> 33)) * 0xFF51AFD7ED558CCDL;
        hash ^= hash >>> 33;
        int variant = (int) (hash >>> 60);
        if (variant >= 6) return;
        if (wallPixel == null) wallPixel = TextureHelper.GetSingleton().getSolidPixel();
        float packed = batch.getPackedColor();
        float red = batch.getColor().r, green = batch.getColor().g, blue = batch.getColor().b, alpha = batch.getColor().a;
        int markX = 3 + (int) ((hash >>> 4) & 7);
        int markY = 4 + (int) ((hash >>> 8) & 3);
        if (variant < 2) {
            batch.setColor(red * 0.086f, green * 0.118f, blue * 0.125f, alpha * 0.7f);
            wallMark(batch, tileX, tileY, markX, markY, 1, 4);
            wallMark(batch, tileX, tileY, markX + 1, markY - 2, 1, 3);
            wallMark(batch, tileX, tileY, markX + 2, markY - 3, 2, 1);
            wallMark(batch, tileX, tileY, markX - 2, markY + 1, 2, 1);
        } else if (variant < 4) {
            int seamY = markY < 6 ? 4 : 8;
            batch.setColor(red * 0.278f, green * 0.329f, blue * 0.251f, alpha * 0.6f);
            wallMark(batch, tileX, tileY, markX - 1, seamY, 5, 1);
            wallMark(batch, tileX, tileY, markX, seamY + 1, 3, 1);
            wallMark(batch, tileX, tileY, markX + 1, seamY - 1, 1, 1);
            batch.setColor(red * 0.33f, green * 0.38f, blue * 0.29f, alpha * 0.35f);
            wallMark(batch, tileX, tileY, markX + 1, seamY + 1, 1, 1);
        } else {
            batch.setColor(red * 0.086f, green * 0.118f, blue * 0.125f, alpha * 0.45f);
            wallMark(batch, tileX, tileY, markX, 3, 3, 8);
            wallMark(batch, tileX, tileY, markX + 1, 1, 1, 11);
            batch.setColor(red * 0.408f, green * 0.612f, blue * 0.635f, alpha * 0.12f);
            wallMark(batch, tileX, tileY, markX + 2, 6, 1, 3);
        }
        batch.setPackedColor(packed);
    }

    private void wallMark(Batch batch, int tileX, int tileY, int x, int y, int width, int height) {
        float artPixel = ConstantsHelper.TILE / 16f;
        batch.draw(wallPixel, tileX * ConstantsHelper.TILE + x * artPixel,
                tileY * ConstantsHelper.TILE + y * artPixel, width * artPixel, height * artPixel);
    }

    @Override
    public void drawPlatform(Batch batch, Room room, int tileX, int tileY) {
        boolean leftEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX - 1, tileY));
        boolean rightEnd = !room.getPlatforms().contains(UtilsHelper.platformKey(tileX + 1, tileY));
        if (wallPixel == null) wallPixel = TextureHelper.GetSingleton().getSolidPixel();
        if (platformFaces == null) {
            Texture masonry = TextureHelper.GetSingleton().getTexture("images/tiles/sewers/wall.png");
            platformFaces = new TextureRegion[] {
                    new TextureRegion(masonry, 0, 0, 16, 2), new TextureRegion(masonry, 0, 4, 16, 2)
            };
        }


        int shadowInset = leftEnd ? 2 : 0;
        int shadowWidth = 16 - shadowInset - (rightEnd ? 2 : 0);
        platformPart(batch, tileX, tileY, shadowInset, 5, shadowWidth, 2, PLATFORM_WALL_SHADOW);
        platformPart(batch, tileX, tileY, shadowInset, 7, shadowWidth, 1, PLATFORM_WALL_FALLOFF);


        int support = (tileX + tileY) % 4;
        if (leftEnd || rightEnd || support == 0) {
            super.drawPlatform(batch, room, tileX, tileY);
        } else if (support == 2) {
            platformPart(batch, tileX, tileY, 6, 5, 4, 3, PLATFORM_SIDE);
            platformPart(batch, tileX, tileY, 7, 8, 2, 2, PLATFORM_UNDERSIDE);
            platformPart(batch, tileX, tileY, 6, 5, 1, 2, PLATFORM_FACE);
        }


        platformPart(batch, tileX, tileY, 0, 0, 16, 1, PLATFORM_LIP);
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * 0.44f, parent.g * 0.52f, parent.b * 0.53f, parent.a);
        batch.draw(platformFaces[tileX & 1], tileX * ConstantsHelper.TILE,
                (tileY + 1) * ConstantsHelper.TILE + 4f - 3 * ConstantsHelper.TILE / 16f,
                ConstantsHelper.TILE, 2 * ConstantsHelper.TILE / 16f);
        batch.setPackedColor(packed);
        platformPart(batch, tileX, tileY, 0, 3, 16, 1, PLATFORM_SIDE);
        platformPart(batch, tileX, tileY, 0, 4, 16, 1, PLATFORM_UNDERSIDE);
        if (leftEnd) {
            platformPart(batch, tileX, tileY, 0, 1, 1, 3, PLATFORM_LIP);
            platformPart(batch, tileX, tileY, 1, 3, 1, 1, PLATFORM_FACE);
        }
        if (rightEnd) {
            platformPart(batch, tileX, tileY, 14, 1, 2, 3, PLATFORM_SIDE);
            platformPart(batch, tileX, tileY, 15, 2, 1, 3, PLATFORM_UNDERSIDE);
        }
    }

    private void platformPart(Batch batch, int tileX, int tileY, int x, int down, int width, int height, Color tint) {
        float artPixel = ConstantsHelper.TILE / 16f;

        float surfaceY = (tileY + 1) * ConstantsHelper.TILE + 4f;
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * tint.r, parent.g * tint.g, parent.b * tint.b, parent.a * tint.a);
        batch.draw(wallPixel, tileX * ConstantsHelper.TILE + x * artPixel,
                surfaceY - (down + height) * artPixel, width * artPixel, height * artPixel);
        batch.setPackedColor(packed);
    }

    @Override
    public void drawArchitecture(Batch batch, Room room, int depth) {
        if (architecture == null) architecture = new SewersArchitecture();
        architecture.draw(batch, room, depth);
    }

    @Override
    public Level getLevel() {
        return new SewersLevel();
    }

    @Override
    public Integer getChapterIntroDepth() {
        return 1;
    }

    @Override
    public String getChapterIntroStory() {
        return "The Dungeon lies right beneath the City, its upper levels actually constitute the City's sewer system. "
                + "Being nominally a part of the City, these levels are not that dangerous. No one will call it a safe place, "
                + "but at least you won't need to deal with evil magic here.";
    }

    @Override
    public String[] getSignMessages() {
        return new String[] {
            "Don't overestimate your strength, use weapons and armor you can handle.",
            "Some dangers in the sewers are easy to miss at first glance. If a room feels wrong, slow down and look carefully.",
            "Remember that raising your strength is not the only way to access better equipment. You can also lower its strength requirement with Scrolls of Upgrade.",
            "You can spend your gold in shops throughout the dungeon. There is even a merchant on the first floor now.",
            "Beware of Goo!"};
    }
}

