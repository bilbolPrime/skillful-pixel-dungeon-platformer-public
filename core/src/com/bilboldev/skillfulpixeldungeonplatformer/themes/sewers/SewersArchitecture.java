package com.bilboldev.skillfulpixeldungeonplatformer.themes.sewers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomBackdrop;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomBackdropBudget;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomPlaneSelection;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomSnapshot;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomTransition;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomPiers;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.RoomLighting;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;


final class SewersArchitecture {
    private static final float PIXEL = ConstantsHelper.TILE / 16f;
    private static final int MAX_ALCOVES = 2;
    private static final int MAX_DOOR_SURROUNDS = 4;
    private static final int MAX_FIXTURE_LIGHTS = 2;
    private static final Color RECESS = Color.valueOf("111A1C");
    private static final Color INNER = Color.valueOf("1B282B");
    private static final Color DISTANT_STONE = Color.valueOf("253639");
    private static final Color TRIM = Color.valueOf("394B4E");
    private static final Color EDGE = Color.valueOf("495D5E");
    private static final Color IRON = Color.valueOf("2D3C3E");
    private static final Color GLASS = Color.valueOf("D4A15F");
    private static final Color LIGHT_POOL = new Color(0.83f, 0.63f, 0.37f, 0.20f);
    private static final Color LIGHT_CORE = new Color(1f, 0.78f, 0.42f, 0.30f);
    private static final Color MASONRY_TINT = new Color(0.36f, 0.44f, 0.44f, 1f);
    private static final Color DRAIN_TINT = new Color(0.22f, 0.29f, 0.29f, 1f);
    private static final Color RETURN_FACE = new Color(0.10f, 0.15f, 0.16f, 1f);
    private static final Color RETURN_SHADE = new Color(0.065f, 0.10f, 0.11f, 1f);
    private static final int[] LEFT_STEPS = {14, 8, 3, 0, 1, 4, 8, 15};
    private static final int[] RIGHT_STEPS = {22, 14, 8, 5, 0, 2, 7, 20};
    private final TextureRegion pixel;
    private final TextureRegion pier;
    private final TextureRegion lintel;
    private final TextureRegion drain;
    private final TextureRegion masonry;
    private TextureRegion light;
    private final RoomPiers roomPiers;
    private final RoomLighting roomLighting = new RoomLighting();
    private final float[] alcoveX = new float[MAX_ALCOVES];
    private int alcoveCount;
    private float alcoveY;
    private Room sourceRoom;
    private int sourceDepth;
    private RoomBackdrop backdrop;
    private final RoomBackdropBudget contentBudget = new RoomBackdropBudget();
    private final Rectangle predecessorBounds = new Rectangle(), alcoveBounds = new Rectangle();
    private final Rectangle primaryOpening = new Rectangle();
    private final Rectangle secondaryOpening = new Rectangle(), secondaryBounds = new Rectangle();
    private final float[] primaryReturns = new float[24 * 4];
    private final float[] secondaryReturns = new float[24 * 4];
    private int returnCount, secondaryReturnCount;
    private boolean predecessorVisible, secondaryVisible;

    SewersArchitecture() {
        TextureHelper textures = TextureHelper.GetSingleton();
        Texture masonry = textures.getTexture("images/tiles/sewers/wall.png");
        this.masonry = new TextureRegion(masonry);
        roomPiers = new RoomPiers(this.masonry);
        pixel = new TextureRegion(textures.getSolidPixel());
        pier = new TextureRegion(masonry, 0, 0, 3, 16);
        lintel = new TextureRegion(masonry, 0, 0, 16, 3);
        drain = new TextureRegion(textures.getTexture("images/tiles/sewers/decoration.png"));
    }

    void draw(Batch batch, Room room, int depth) {
        if (sourceRoom != room || sourceDepth != depth) selectAlcoves(room, depth);
        float distantOffset = distantOffset(room);
        RoomSnapshot previous = MapHelper.getInstance().getPredecessorSnapshot();
        predecessorVisible = previous != null;
        secondaryVisible = false;
        if (predecessorVisible) drawPredecessor(batch, previous, room, distantOffset);
        for (int i = 0; i < alcoveCount; i++) {
            if (!coveredByPredecessor(i)) drawAlcove(batch, alcoveX[i], alcoveY, i ^ room.getIdentifier().hashCode(), distantOffset);
        }
        roomPiers.draw(batch, room.getLayout().piers());
        int surrounds = 0;
        for (Door door : room.getDoors()) {
            if (door.x < 3 * PIXEL || door.x + ConstantsHelper.TILE + 3 * PIXEL > room.getWidth() * ConstantsHelper.TILE) continue;
            if (surrounds++ >= MAX_DOOR_SURROUNDS) break;
            drawDoorSurround(batch, door.x, door.y + 7f);
        }
        if (!room.getLayout().hasMountedFixtures()) drawFixtureLights(batch);
        else drawMaintenanceLamps(batch, room);
    }

    private void drawMaintenanceLamps(Batch batch, Room room) {
        for (RoomLayout.Anchor anchor : room.getLayout().fixtureAnchors()) {
            float x = anchor.x, y = anchor.y;
            float lightY = y + RoomLighting.MOUNTED_SOURCE_OFFSET_Y;
            MapHelper.getInstance().getRoomFixtureObservation().beginLight(RoomSnapshot.PropKind.LAMP, pixel, x-16, y-24, 32, 56, x, lightY);

            rect(batch, x, y, -2, -3, 4, 7, IRON);
            rect(batch, x, y, -1, -2, 2, 4, GLASS);
            rect(batch, x, y, -2, 3, 4, 1, EDGE);
            MapHelper.getInstance().getRoomFixtureObservation().endFocal(true);
            roomLighting.draw(batch, room, x, lightY, ConstantsHelper.TILE * 1.5f);
            MapHelper.getInstance().getRoomFixtureObservation().lamp(x, lightY);
        }
    }

    private void drawPredecessor(Batch batch, RoomSnapshot previous, Room room, float distantOffset) {
        float width = primaryOpening.width, height = primaryOpening.height;
        if (width < ConstantsHelper.TILE || height < ConstantsHelper.TILE) {
            predecessorVisible = false;
            return;
        }
        float x = primaryOpening.x, y = primaryOpening.y;
        predecessorBounds.set(x - 3f * PIXEL, y - 2f * PIXEL, width + 6f * PIXEL, height + 5f * PIXEL);
        if (returnCount == 0) selectPrimaryReturns(room);
        if (backdrop == null) backdrop = new RoomBackdrop(TextureHelper.GetSingleton().getBackdropTile(0, 0),
                TextureHelper.GetSingleton().getBackdropTile(0, 4));
        RoomTransition transition = MapHelper.getInstance().getRoomTransition();
        RoomSnapshot fading = transition.getOutgoingBackdrop();
        RoomSnapshot secondary = MapHelper.getInstance().getSecondaryRoomSnapshot();
        RoomPlaneSelection selection = MapHelper.getInstance().getRoomPlaneSelection();
        secondaryVisible = secondary != null && secondaryOpening.width >= 3f * ConstantsHelper.TILE
                && previous.roomIdentifier.equals(selection.primaryIdentifier)
                && !secondary.roomIdentifier.equals(previous.roomIdentifier) && !secondary.roomIdentifier.equals(room.getIdentifier());
        if (fading != null && (fading.roomIdentifier.equals(previous.roomIdentifier) || fading.roomIdentifier.equals(room.getIdentifier())
                || secondaryVisible && fading.roomIdentifier.equals(secondary.roomIdentifier))) fading = null;
        contentBudget.reset();
        boolean reduced = GameSettingsHelper.getInstance().isReducedVisualEffects();
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        float red = parent.r, green = parent.g, blue = parent.b, alpha = parent.a;
        try {
            contentBudget.reservePrimary(previous.enemies.size(), previous.observedPropArtCount);
            contentBudget.allowEffects(reduced ? 1 : 2, reduced ? 0 : 1);
            if (fading != null) {
                batch.setColor(red, green, blue, alpha * transition.getOutgoingOpacity());
                backdrop.drawWithBudget(batch, fading, x + 3f * PIXEL, y + 3f * PIXEL,
                        width - 6f * PIXEL, height - 6f * PIXEL, distantOffset, false, contentBudget);
            }
            contentBudget.reservePrimary(0, 0);
            contentBudget.allowEffects((reduced ? 4 : 16) - (secondaryVisible ? (reduced ? 1 : 4) : 0) - (fading != null ? (reduced ? 1 : 2) : 0),
                    (reduced ? 2 : 4) - (secondaryVisible ? 1 : 0) - (fading != null && !reduced ? 1 : 0));
            batch.setColor(red, green, blue, alpha * transition.getBackdropBlend());
            backdrop.drawWithBudget(batch, previous, x + 3f * PIXEL, y + 3f * PIXEL,
                    width - 6f * PIXEL, height - 6f * PIXEL, distantOffset, false, contentBudget);
        } finally {
            batch.setPackedColor(packed);
        }
        drawPrimaryReturns(batch);
        if (secondaryVisible) {
            if (secondaryReturnCount == 0) secondaryReturnCount = selectReturns(room, secondaryOpening, secondaryReturns);
            secondaryBounds.set(secondaryOpening.x - 3f * PIXEL, secondaryOpening.y - 2f * PIXEL,
                    secondaryOpening.width + 6f * PIXEL, secondaryOpening.height + 5f * PIXEL);

            contentBudget.allowEffects(reduced ? 1 : 4, 1);
            try {
                batch.setColor(red, green, blue, alpha * transition.getSecondaryOpacity(secondary.roomIdentifier));
                backdrop.drawConnectedSecondary(batch, secondary, secondaryOpening.x + 3f * PIXEL, secondaryOpening.y + 3f * PIXEL,
                        secondaryOpening.width - 6f * PIXEL, secondaryOpening.height - 6f * PIXEL, distantOffset * 0.375f, contentBudget,
                        transition.getSecondaryAnchorX(secondary.roomIdentifier, selection.secondaryAnchorX),
                        transition.getSecondaryAnchorY(secondary.roomIdentifier, selection.secondaryAnchorY));
            } finally {
                batch.setPackedColor(packed);
            }
            drawReturns(batch, secondaryOpening, secondaryReturns, secondaryReturnCount);
        }
    }


    private void selectPrimaryReturns(Room room) {
        returnCount = selectReturns(room, primaryOpening, primaryReturns);
    }

    private int selectReturns(Room room, Rectangle opening, float[] returns) {
        int count = 0;
        float x = opening.x + 3f * PIXEL, y = opening.y + 3f * PIXEL;
        float width = opening.width - 6f * PIXEL, height = opening.height - 6f * PIXEL;
        boolean alternate = (room.getIdentifier().hashCode() & 1) != 0;
        float bandHeight = Math.max(PIXEL, (float) Math.floor(height / 8f / PIXEL) * PIXEL);
        for (int band = 0; band < 8; band++) {
            float bottom = y + band * bandHeight;
            float partHeight = band == 7 ? y + height - bottom : Math.min(bandHeight, y + height - bottom);
            if (partHeight <= 0f) break;
            float left = Math.min(width * 0.28f, (2 + (alternate ? RIGHT_STEPS[band] : LEFT_STEPS[band])) * PIXEL);
            float right = Math.min(width * 0.28f, (2 + (alternate ? LEFT_STEPS[band] : RIGHT_STEPS[band])) * PIXEL);
            count = addReturn(returns, count, x, bottom, left, partHeight);
            count = addReturn(returns, count, x + width - right, bottom, right, partHeight);
        }
        count = addReturn(returns, count, x, y, width, 2f * PIXEL);
        count = addReturn(returns, count, x, y + height - 2f * PIXEL, width, 2f * PIXEL);
        int doors = 0;
        for (Door door : room.getDoors()) {
            if (doors++ >= MAX_DOOR_SURROUNDS) break;
            count = protectFixture(opening, returns, count, door.x - 4f * PIXEL, door.y + 7f - 2f * PIXEL,
                    door.getDisplayWidth() + 8f * PIXEL, door.getDisplayHeight() + 4f * PIXEL);
        }
        if (room.getSign() != null) count = protectFixture(opening, returns, count, room.getSign().getX() - 2f * PIXEL,
                room.getSign().getY() - 2f * PIXEL, ConstantsHelper.TILE + 4f * PIXEL, ConstantsHelper.TILE + 4f * PIXEL);
        return count;
    }

    private int protectFixture(Rectangle opening, float[] returns, int count, float x, float y, float width, float height) {
        float left = opening.x + 3f * PIXEL, bottom = opening.y + 3f * PIXEL;
        float right = opening.x + opening.width - 3f * PIXEL;
        float top = opening.y + opening.height - 3f * PIXEL;
        if (x >= right || x + width <= left || y >= top || y + height <= bottom) return count;

        float startY = y - bottom < top - y - height ? bottom : Math.max(bottom, y);
        float endY = startY == bottom ? Math.min(top, y + height) : top;
        return addReturn(returns, count, Math.max(left, x), startY, Math.min(right, x + width) - Math.max(left, x), endY - startY);
    }

    private int addReturn(float[] returns, int count, float x, float y, float width, float height) {
        if (width <= 0f || height <= 0f || count >= returns.length / 4) return count;
        int at = count * 4;
        returns[at] = x; returns[at + 1] = y;
        returns[at + 2] = width; returns[at + 3] = height;
        return count + 1;
    }

    private void drawPrimaryReturns(Batch batch) {
        drawReturns(batch, primaryOpening, primaryReturns, returnCount);
    }

    private void drawReturns(Batch batch, Rectangle opening, float[] returns, int count) {
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        float red = parent.r, green = parent.g, blue = parent.b, alpha = parent.a;
        try {
            float left = opening.x + 3f * PIXEL, bottom = opening.y + 3f * PIXEL;
            float right = opening.x + opening.width - 3f * PIXEL;
            float top = opening.y + opening.height - 3f * PIXEL;

            for (int i = 0; i < count; i++) {
                int at = i * 4;
                float x = returns[at], y = returns[at + 1];
                float width = returns[at + 2], height = returns[at + 3];
                if (y > bottom) piece(batch, pixel, x, y - Math.min(2f * PIXEL, y - bottom),
                        width, Math.min(2f * PIXEL, y - bottom), RETURN_SHADE);
                if (y + height < top) piece(batch, pixel, x, y + height,
                        width, Math.min(PIXEL, top - y - height), RETURN_FACE);
                if (x > left) piece(batch, pixel, x - Math.min(2f * PIXEL, x - left), y,
                        Math.min(2f * PIXEL, x - left), height, RETURN_SHADE);
                if (x + width < right) piece(batch, pixel, x + width, y,
                        Math.min(2f * PIXEL, right - x - width), height, RETURN_FACE);
            }


            batch.setColor(red * 0.21f, green * 0.259f, blue * 0.2695f, alpha);
            for (int i = 0; i < count; i++) drawWallPatch(batch, returns, i * 4);
        } finally { batch.setPackedColor(packed); }
    }

    private void drawWallPatch(Batch batch, float[] returns, int at) {
        float x = returns[at], y = returns[at + 1];
        float right = x + returns[at + 2], top = y + returns[at + 3];
        float tile = ConstantsHelper.TILE, du = masonry.getU2() - masonry.getU(), dv = masonry.getV2() - masonry.getV();
        for (float row = (float) Math.floor(y / tile) * tile; row < top; row += tile) {
            for (float column = (float) Math.floor(x / tile) * tile; column < right; column += tile) {
                float left = Math.max(x, column), bottom = Math.max(y, row);
                float width = Math.min(right, column + tile) - left, height = Math.min(top, row + tile) - bottom;
                batch.draw(masonry.getTexture(), left, bottom, width, height,
                        masonry.getU() + (left - column) / tile * du, masonry.getV2() - (bottom - row) / tile * dv,
                        masonry.getU() + (left + width - column) / tile * du, masonry.getV2() - (bottom + height - row) / tile * dv);
            }
        }
    }

    private boolean coveredByPredecessor(int alcove) {
        alcoveBounds.set(alcoveX[alcove] - 3f * PIXEL, alcoveY - 2f * PIXEL, 30f * PIXEL, 38f * PIXEL);
        return predecessorVisible && predecessorBounds.overlaps(alcoveBounds) || secondaryVisible && secondaryBounds.overlaps(alcoveBounds);
    }

    private void drawFixtureLights(Batch batch) {
        if (alcoveCount == 0) return;
        if (light == null) light = new TextureRegion(TextureHelper.GetSingleton().getSoftLightTexture());
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        float radius = 28 * PIXEL;
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r, parent.g, parent.b, parent.a * GameSettingsHelper.getInstance().getVisualEffectIntensity());
        for (int i = 0; i < alcoveCount && i < MAX_FIXTURE_LIGHTS; i++) {
            if (coveredByPredecessor(i)) continue;

            float x = alcoveX[i] + 25.5f * PIXEL;
            float y = alcoveY + 21 * PIXEL;
            if (camera != null && (Math.abs(x - camera.position.x) > camera.viewportWidth * camera.zoom / 2f + radius ||
                    Math.abs(y - camera.position.y) > camera.viewportHeight * camera.zoom / 2f + radius)) continue;
            piece(batch, light, x - radius, y - radius, radius * 2, radius * 2, LIGHT_POOL);
            float coreRadius = 5 * PIXEL;
            piece(batch, light, x - coreRadius, y - coreRadius, coreRadius * 2, coreRadius * 2, LIGHT_CORE);
            MapHelper.getInstance().getRoomFixtureObservation().lamp(x, y);
        }
        batch.setPackedColor(packed);
    }

    private float distantOffset(Room room) {
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        if (camera == null || GameSettingsHelper.getInstance().isReducedCameraMotion()) return 0f;
        float roomWidth = room.getWidth() * ConstantsHelper.TILE;

        if (roomWidth - camera.viewportWidth * camera.zoom < 2 * ConstantsHelper.TILE) return 0f;


        return MathUtils.clamp((camera.position.x - roomWidth / 2f) * 0.04f, -2 * PIXEL, 2 * PIXEL);
    }

    private void selectAlcoves(Room room, int depth) {
        sourceRoom = room;
        sourceDepth = depth;
        selectPrimaryOpening(room);
        alcoveCount = 0;
        if (com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.SpecialRoomFocalPoint.supports(room)
                || com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.BossArenaArchitecture.supports(room)) return;
        alcoveY = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE + 8 * PIXEL;

        for (RoomLayout.Anchor anchor : room.getLayout().fixtureAnchors()) {
            if (alcoveCount == MAX_ALCOVES) break;
            float x = anchor.x - 25.5f * PIXEL;
            if (clearBay(room, x - 3 * PIXEL, alcoveY - 2 * PIXEL, 30 * PIXEL, 38 * PIXEL)) {
                alcoveX[alcoveCount++] = x;
            }
        }
    }


    private void selectPrimaryOpening(Room room) {
        float width = Math.min(10f * ConstantsHelper.TILE, (room.getWidth() - 4f) * ConstantsHelper.TILE);
        float height = Math.min(5f * ConstantsHelper.TILE,
                (room.getHeight() - ConstantsHelper.MIN_FLOOR - 2f) * ConstantsHelper.TILE);
        float center = Math.round((room.getWidth() * ConstantsHelper.TILE - width) / (2f * PIXEL)) * PIXEL;


        float y = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE + 20f * PIXEL
                - Math.max(0f, height - 4f * ConstantsHelper.TILE);
        float chosen = center, best = openingConflict(room, center, y, width, height);
        for (int side = -1; side <= 1; side += 2) {
            float candidate = MathUtils.clamp(center + side * 2f * ConstantsHelper.TILE,
                    ConstantsHelper.TILE, Math.max(ConstantsHelper.TILE, (room.getWidth() - 1f) * ConstantsHelper.TILE - width));
            float conflict = openingConflict(room, candidate, y, width, height);
            if (conflict < best) { chosen = candidate; best = conflict; }
        }
        primaryOpening.set(chosen, y, width, height);
        returnCount = 0;
        selectSecondaryOpening(room);
    }

    private void selectSecondaryOpening(Room room) {
        float tile = ConstantsHelper.TILE, gap = 8f * PIXEL;
        float leftSpace = primaryOpening.x - gap - tile;
        float rightStart = primaryOpening.x + primaryOpening.width + gap;
        float rightSpace = room.getWidth() * tile - tile - rightStart;
        float y = primaryOpening.y + 12f * PIXEL, height = Math.min(3f * tile, primaryOpening.height);
        float leftWidth = Math.min(6f * tile, leftSpace), rightWidth = Math.min(6f * tile, rightSpace);
        boolean onRight = rightWidth > leftWidth || rightWidth == leftWidth
                && openingConflict(room, rightStart, y, rightWidth, height)
                <= openingConflict(room, primaryOpening.x - gap - leftWidth, y, leftWidth, height);
        float width = onRight ? rightWidth : leftWidth;
        secondaryOpening.set(onRight ? rightStart : primaryOpening.x - gap - width, y, Math.max(0f, width), height);
        secondaryReturnCount = 0;
    }

    private float openingConflict(Room room, float x, float y, float width, float height) {
        float result = 0f, clearance = 4f * PIXEL;
        for (Door door : room.getDoors()) {
            result += overlap(x, y, width, height, door.x - clearance, door.y + 7f - clearance,
                    door.getDisplayWidth() + 2f * clearance, door.getDisplayHeight() + 2f * clearance);
        }
        if (room.getSign() != null) result += overlap(x, y, width, height,
                room.getSign().getX() - clearance, room.getSign().getY() - clearance,
                ConstantsHelper.TILE + 2f * clearance, ConstantsHelper.TILE + 2f * clearance);
        return result;
    }

    private float overlap(float x, float y, float width, float height, float otherX, float otherY, float otherWidth, float otherHeight) {
        return Math.max(0f, Math.min(x + width, otherX + otherWidth) - Math.max(x, otherX))
                * Math.max(0f, Math.min(y + height, otherY + otherHeight) - Math.max(y, otherY));
    }

    private boolean clearBay(Room room, float x, float y, float width, float height) {
        if (x < 0 || x + width > room.getWidth() * ConstantsHelper.TILE ||
                y + height > (room.getHeight() - 1) * ConstantsHelper.TILE) return false;
        for (int tileY = (int) (y / ConstantsHelper.TILE); tileY <= (int) ((y + height) / ConstantsHelper.TILE); tileY++) {
            for (int tileX = (int) (x / ConstantsHelper.TILE); tileX <= (int) ((x + width) / ConstantsHelper.TILE); tileX++) {
                if (room.getPlatforms().contains(UtilsHelper.platformKey(tileX, tileY))) return false;
            }
        }
        for (Door door : room.getDoors()) {
            if (x < door.x + ConstantsHelper.TILE + 4 * PIXEL && x + width > door.x - 4 * PIXEL &&
                    y < door.y + ConstantsHelper.TILE + 8 * PIXEL && y + height > door.y - 2 * PIXEL) return false;
        }
        return true;
    }

    private void drawAlcove(Batch batch, float x, float y, int variant, float distantOffset) {

        rect(batch, x, y, 0, 0, 24, 28, RECESS);
        rect(batch, x, y, 2, 28, 20, 2, RECESS);
        rect(batch, x, y, 4, 30, 16, 2, RECESS);
        rect(batch, x, y, 2, 2, 20, 24, INNER);

        float distantX = x + distantOffset;
        rect(batch, distantX, y, 5, 2, 2, 24, DISTANT_STONE);
        rect(batch, distantX, y, 17, 2, 2, 24, DISTANT_STONE);
        rect(batch, distantX, y, 4, 23, 16, 2, DISTANT_STONE);
        if ((variant & 1) == 0) {
            piece(batch, drain, distantX + 4 * PIXEL, y + 3 * PIXEL, ConstantsHelper.TILE, ConstantsHelper.TILE, DRAIN_TINT);
        } else {
            rect(batch, distantX, y, 5, 5, 14, 17, RECESS);
            for (int bar = 7; bar < 19; bar += 4) rect(batch, distantX, y, bar, 5, 1, 17, IRON);
            rect(batch, distantX, y, 5, 10, 14, 1, IRON);
        }
        rect(batch, x, y, 2, 2, 1, 24, IRON);
        rect(batch, x, y, 20, 2, 2, 24, RECESS);
        for (int row = 0; row < 2; row++) {
            piece(batch, pier, x - 3 * PIXEL, y + row * ConstantsHelper.TILE, 3 * PIXEL, ConstantsHelper.TILE, MASONRY_TINT);
            piece(batch, pier, x + 24 * PIXEL, y + row * ConstantsHelper.TILE, 3 * PIXEL, ConstantsHelper.TILE, MASONRY_TINT);
        }
        rect(batch, x, y, -1, 0, 1, 28, RECESS);
        rect(batch, x, y, 23, 0, 1, 28, RECESS);
        rect(batch, x, y, -3, -2, 30, 2, TRIM);
        rect(batch, x, y, 0, 28, 2, 4, TRIM);
        rect(batch, x, y, 22, 28, 2, 4, TRIM);
        rect(batch, x, y, 2, 30, 2, 4, TRIM);
        rect(batch, x, y, 20, 30, 2, 4, TRIM);
        piece(batch, lintel, x + 4 * PIXEL, y + 32 * PIXEL, ConstantsHelper.TILE, 3 * PIXEL, MASONRY_TINT);

        rect(batch, x, y, 24, 18, 3, 7, RECESS);
        rect(batch, x, y, 25, 19, 1, 4, GLASS);
        rect(batch, x, y, 24, 18, 3, 1, IRON);
        rect(batch, x, y, 24, 24, 3, 1, EDGE);
    }

    private void drawDoorSurround(Batch batch, float x, float y) {
        rect(batch, x, y, -2, 0, 20, 18, RECESS);
        piece(batch, pier, x - 3 * PIXEL, y, 3 * PIXEL, ConstantsHelper.TILE, MASONRY_TINT);
        piece(batch, pier, x + ConstantsHelper.TILE, y, 3 * PIXEL, ConstantsHelper.TILE, MASONRY_TINT);
        piece(batch, lintel, x, y + ConstantsHelper.TILE, ConstantsHelper.TILE, 3 * PIXEL, MASONRY_TINT);
        rect(batch, x, y, -2, 16, 2, 2, TRIM);
        rect(batch, x, y, 16, 16, 2, 2, TRIM);
    }

    private void rect(Batch batch, float x, float y, int px, int py, int width, int height, Color tint) {
        piece(batch, pixel, x + px * PIXEL, y + py * PIXEL, width * PIXEL, height * PIXEL, tint);
    }

    private void piece(Batch batch, TextureRegion region, float x, float y, float width, float height, Color tint) {
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        batch.setColor(parent.r * tint.r, parent.g * tint.g, parent.b * tint.b, parent.a * tint.a);
        try {
            batch.draw(region, x, y, width, height);
            MapHelper.getInstance().getRoomFixtureObservation().focalPiece(region, x, y, width, height, tint);
        }
        finally { batch.setPackedColor(packed); }
    }
}
