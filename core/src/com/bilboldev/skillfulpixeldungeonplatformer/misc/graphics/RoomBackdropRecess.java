package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;


public final class RoomBackdropRecess {
    public enum Kind { PRISON, CAVES, CITY, HALLS }
    private static final float TILE = ConstantsHelper.TILE, PIXEL = TILE / 16f;
    private static final int[] LEFT_STEPS = {10, 10, 3, 3, 3, 3, 7, 12};
    private static final int[] RIGHT_STEPS = {14, 8, 4, 4, 4, 4, 10, 10};
    private static final int[] CAVE_LEFT_STEPS = {26, 12, 5, 18, 3, 12, 10, 30};
    private static final int[] CAVE_RIGHT_STEPS = {12, 22, 5, 4, 18, 5, 18, 26};
    private static final int[] CITY_STEPS = {14, 8, 8, 8, 8, 8, 10, 18};
    private static final int[] HALLS_LEFT_STEPS = {28, 18, 14, 14, 18, 8, 18, 30};
    private static final int[] HALLS_RIGHT_STEPS = {18, 26, 14, 8, 16, 16, 24, 34};
    private final Kind kind;
    private final TextureRegion pixel, returnMasonry, pier, lintel;
    private final RoomBackdrop backdrop;
    private final RoomBackdropBudget contentBudget = new RoomBackdropBudget();
    private final Rectangle primaryOpening = new Rectangle(), secondaryOpening = new Rectangle();
    private final float[] primaryReturns = new float[24 * 4], secondaryReturns = new float[24 * 4];
    private int returnCount, secondaryReturnCount, sourceDepth;
    private String sourceId;
    private boolean predecessorVisible, secondaryVisible, raisedSpecial;

    public RoomBackdropRecess(TextureRegion masonry, TextureRegion pier, TextureRegion lintel) {
        this(Kind.PRISON, masonry, pier, lintel);
    }

    public RoomBackdropRecess(Kind kind, TextureRegion masonry, TextureRegion pier, TextureRegion lintel) {
        this.kind = kind;
        TextureHelper textures = TextureHelper.GetSingleton();
        int biome = kind.ordinal() + 1;
        TextureRegion distantMasonry = textures.getBackdropTile(biome, 0);
        returnMasonry = kind == Kind.CAVES ? textures.getBackdropTile(biome, 3) : distantMasonry;
        this.pier = textures.getBackdropTile(biome, 1);
        this.lintel = textures.getBackdropTile(biome, 2);
        pixel = textures.getBackdropTile(biome, 4);
        backdrop = new RoomBackdrop(distantMasonry, pixel);
    }

    public void draw(Batch batch, Room room, int depth) {
        predecessorVisible = secondaryVisible = false;
        RoomSnapshot previous = MapHelper.getInstance().getPredecessorSnapshot();
        if (previous == null || room == null || room.getClass() != Room.class) return;
        boolean lowFocal = false;
        if (previous.sourceKind.special) for (int i = 0; i < previous.props.size(); i++) {
            RoomSnapshot.Prop prop = previous.props.get(i);
            if (prop.kind == RoomSnapshot.PropKind.FOCAL && prop.pose != null && prop.pose.layerCount() > 0
                    && prop.pose.y < (ConstantsHelper.MIN_FLOOR + 2f) * TILE) { lowFocal = true; break; }
        }
        if (depth != sourceDepth || !room.getIdentifier().equals(sourceId) || raisedSpecial != lowFocal)
            selectOpenings(room, depth, lowFocal);
        if (primaryOpening.width < 4f * TILE || primaryOpening.height < 2f * TILE) return;
        predecessorVisible = true;
        drawPredecessor(batch, previous, room, distantOffset(room));
    }


    public boolean covers(float x, float y, float width, float height) {
        return predecessorVisible && overlaps(primaryOpening, x, y, width, height)
                || secondaryVisible && overlaps(secondaryOpening, x, y, width, height);
    }

    private boolean overlaps(Rectangle opening, float x, float y, float width, float height) {
        return x < opening.x + opening.width + 24f && x + width > opening.x - 24f
                && y < opening.y + opening.height + 24f && y + height > opening.y - 24f;
    }

    private void drawPredecessor(Batch batch, RoomSnapshot previous, Room room, float distantOffset) {
        MapHelper map = MapHelper.getInstance();
        RoomTransition transition = map.getRoomTransition();
        RoomPlaneSelection selection = map.getRoomPlaneSelection();
        RoomSnapshot secondary = map.getSecondaryRoomSnapshot(), fading = transition.getOutgoingBackdrop();
        secondaryVisible = secondary != null && secondaryOpening.width >= 3f * TILE
                && previous.roomIdentifier.equals(selection.primaryIdentifier)
                && !secondary.roomIdentifier.equals(previous.roomIdentifier) && !secondary.roomIdentifier.equals(room.getIdentifier());
        if (fading != null && (fading.roomIdentifier.equals(previous.roomIdentifier) || fading.roomIdentifier.equals(room.getIdentifier())
                || secondaryVisible && fading.roomIdentifier.equals(secondary.roomIdentifier))) fading = null;
        contentBudget.reset();
        boolean reduced = GameSettingsHelper.getInstance().isReducedVisualEffects();
        float packed = batch.getPackedColor();
        Color parent = batch.getColor();
        float red = parent.r, green = parent.g, blue = parent.b, alpha = parent.a;
        float x = primaryOpening.x + 3f * PIXEL, y = primaryOpening.y + 3f * PIXEL;
        float width = primaryOpening.width - 6f * PIXEL, height = primaryOpening.height - 6f * PIXEL;
        try {
            contentBudget.reservePrimary(previous.enemies.size(), previous.observedPropArtCount);
            contentBudget.allowEffects(reduced ? 1 : 2, reduced ? 0 : 1);
            if (fading != null) {
                batch.setColor(red, green, blue, alpha * transition.getOutgoingOpacity());
                backdrop.drawWithBudget(batch, fading, x, y, width, height, distantOffset, false, contentBudget);
            }
            contentBudget.reservePrimary(0, 0);
            contentBudget.allowEffects((reduced ? 4 : 16) - (secondaryVisible ? (reduced ? 1 : 4) : 0) - (fading != null ? (reduced ? 1 : 2) : 0),
                    (reduced ? 2 : 4) - (secondaryVisible ? 1 : 0) - (fading != null && !reduced ? 1 : 0));
            batch.setColor(red, green, blue, alpha * transition.getBackdropBlend());
            backdrop.drawWithBudget(batch, previous, x, y, width, height, distantOffset, false, contentBudget);
            batch.setPackedColor(packed);
            drawReturns(batch, primaryOpening, primaryReturns, returnCount);
            if (secondaryVisible) {
                contentBudget.allowEffects(reduced ? 1 : 4, 1);
                batch.setColor(red, green, blue, alpha * transition.getSecondaryOpacity(secondary.roomIdentifier));
                backdrop.drawConnectedSecondary(batch, secondary, secondaryOpening.x + 3f * PIXEL, secondaryOpening.y + 3f * PIXEL,
                        secondaryOpening.width - 6f * PIXEL, secondaryOpening.height - 6f * PIXEL, distantOffset * 0.375f, contentBudget,
                        transition.getSecondaryAnchorX(secondary.roomIdentifier, selection.secondaryAnchorX),
                        transition.getSecondaryAnchorY(secondary.roomIdentifier, selection.secondaryAnchorY));
                batch.setPackedColor(packed);
                drawReturns(batch, secondaryOpening, secondaryReturns, secondaryReturnCount);
            }
        } finally { batch.setPackedColor(packed); }
    }

    private float distantOffset(Room room) {
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        if (camera == null || GameSettingsHelper.getInstance().isReducedCameraMotion()
                || room.getWidth() * TILE - camera.viewportWidth * camera.zoom < 2f * TILE) return 0f;
        return MathUtils.clamp((camera.position.x - room.getWidth() * TILE / 2f) * 0.04f, -16f, 16f);
    }

    private void selectOpenings(Room room, int depth, boolean lowFocal) {
        sourceId = room.getIdentifier(); sourceDepth = depth; raisedSpecial = lowFocal;
        float width = Math.min((kind == Kind.CITY ? 11f : kind == Kind.CAVES ? 10f : 9f) * TILE, (room.getWidth() - 4f) * TILE);
        float height = Math.min((kind == Kind.CITY ? 6f : 5f) * TILE, (room.getHeight() - ConstantsHelper.MIN_FLOOR - 2f) * TILE);
        float y = ConstantsHelper.MIN_FLOOR * TILE + (kind == Kind.CITY ? 4f * PIXEL
                : 20f * PIXEL - Math.max(0f, height - 4f * TILE));


        if (lowFocal) y += Math.max(0f, Math.min(12f * PIXEL, (room.getHeight() - 1f) * TILE - y - height));
        float center = Math.round((room.getWidth() * TILE - width) / (2f * PIXEL)) * PIXEL;
        float chosen = center, best = conflict(room, center, y, width, height);
        for (int side = -1; side <= 1; side += 2) {
            float candidate = MathUtils.clamp(center + side * 2f * TILE, TILE, Math.max(TILE, (room.getWidth() - 1f) * TILE - width));
            float score = conflict(room, candidate, y, width, height);
            if (score < best) { chosen = candidate; best = score; }
        }
        primaryOpening.set(chosen, y, width, height);
        float gap = 8f * PIXEL, leftWidth = Math.min(6f * TILE, chosen - gap - TILE);
        float rightStart = chosen + width + gap, rightWidth = Math.min(6f * TILE, (room.getWidth() - 1f) * TILE - rightStart);
        boolean onRight = rightWidth > leftWidth || rightWidth == leftWidth
                && conflict(room, rightStart, y, rightWidth, height) <= conflict(room, chosen - gap - leftWidth, y, leftWidth, height);
        float secondaryHeight = Math.min(3f * TILE, height), secondaryY = y + 12f * PIXEL;

        if (kind == Kind.HALLS && y + 28f * PIXEL + secondaryHeight <= (room.getHeight() - 1f) * TILE)
            secondaryY = y + 28f * PIXEL;
        secondaryOpening.set(onRight ? rightStart : chosen - gap - leftWidth, secondaryY,
                Math.max(0f, onRight ? rightWidth : leftWidth), secondaryHeight);
        returnCount = selectReturns(room, primaryOpening, primaryReturns);
        secondaryReturnCount = selectReturns(room, secondaryOpening, secondaryReturns);
    }

    private float conflict(Room room, float x, float y, float width, float height) {
        float result = 0f;
        for (Door door : room.getDoors()) result += overlap(x, y, width, height, door.x - 32f, door.y - 25f,
                door.getDisplayWidth() + 64f, door.getDisplayHeight() + 64f);
        if (room.getSign() != null) result += overlap(x, y, width, height, room.getSign().getX() - 32f,
                room.getSign().getY() - 32f, TILE + 64f, TILE + 64f);
        return result;
    }

    private float overlap(float x, float y, float width, float height, float otherX, float otherY, float otherWidth, float otherHeight) {
        return Math.max(0f, Math.min(x + width, otherX + otherWidth) - Math.max(x, otherX))
                * Math.max(0f, Math.min(y + height, otherY + otherHeight) - Math.max(y, otherY));
    }

    private int selectReturns(Room room, Rectangle opening, float[] returns) {
        int count = 0;
        float x = opening.x + 3f * PIXEL, y = opening.y + 3f * PIXEL;
        float width = opening.width - 6f * PIXEL, height = opening.height - 6f * PIXEL;
        if (width <= 0f || height <= 0f) return 0;
        boolean alternate = (sourceId.hashCode() & 1) != 0;
        int[] leftSteps = kind == Kind.HALLS ? HALLS_LEFT_STEPS : kind == Kind.CITY ? CITY_STEPS : kind == Kind.CAVES ? CAVE_LEFT_STEPS : LEFT_STEPS;
        int[] rightSteps = kind == Kind.HALLS ? HALLS_RIGHT_STEPS : kind == Kind.CITY ? CITY_STEPS : kind == Kind.CAVES ? CAVE_RIGHT_STEPS : RIGHT_STEPS;
        float maximumSide = kind == Kind.CAVES || kind == Kind.HALLS ? 0.28f : 0.24f;
        float bandHeight = Math.max(PIXEL, (float) Math.floor(height / 8f / PIXEL) * PIXEL);
        for (int band = 0; band < 8; band++) {
            float bottom = y + band * bandHeight, part = Math.min(bandHeight, y + height - bottom);
            if (band == 7) part = y + height - bottom;
            if (part <= 0f) break;
            float left = Math.min(width * maximumSide, (2 + (alternate ? rightSteps[band] : leftSteps[band])) * PIXEL);
            float right = Math.min(width * maximumSide, (2 + (alternate ? leftSteps[band] : rightSteps[band])) * PIXEL);
            count = addReturn(returns, count, x, bottom, left, part);
            count = addReturn(returns, count, x + width - right, bottom, right, part);
        }
        count = addReturn(returns, count, x, y, width, 2f * PIXEL);
        count = addReturn(returns, count, x, y + height - 3f * PIXEL, width, 3f * PIXEL);

        if (kind == Kind.PRISON && width > 6f * TILE)
            count = addReturn(returns, count, x + Math.round(width * 0.55f / PIXEL) * PIXEL, y, 5f * PIXEL, height);
        if (kind == Kind.CITY && width > 8f * TILE)
            count = addReturn(returns, count, x + Math.round(width * 0.50f / PIXEL) * PIXEL, y, 5f * PIXEL, height);
        if (kind == Kind.CAVES)
            count = addReturn(returns, count, x + Math.round(width * (alternate ? 0.32f : 0.60f) / PIXEL) * PIXEL,
                    y + height - 8f * PIXEL, Math.min(2f * TILE, width * 0.2f), 8f * PIXEL);
        if (kind == Kind.HALLS)
            count = addReturn(returns, count, x + Math.round(width * (alternate ? 0.38f : 0.62f) / PIXEL) * PIXEL,
                    y + height - 10f * PIXEL, 6f * PIXEL, 10f * PIXEL);
        int doors = 0;
        for (Door door : room.getDoors()) {
            if (doors++ >= 4) break;
            count = protectFixture(opening, returns, count, door.x - 32f, door.y - 9f,
                    door.getDisplayWidth() + 64f, door.getDisplayHeight() + 32f);
        }
        if (room.getSign() != null) count = protectFixture(opening, returns, count, room.getSign().getX() - 16f,
                room.getSign().getY() - 16f, TILE + 32f, TILE + 32f);
        return count;
    }

    private int protectFixture(Rectangle opening, float[] returns, int count, float x, float y, float width, float height) {
        float left = opening.x + 24f, bottom = opening.y + 24f, right = opening.x + opening.width - 24f, top = opening.y + opening.height - 24f;
        if (x >= right || x + width <= left || y >= top || y + height <= bottom) return count;
        float start = y - bottom < top - y - height ? bottom : Math.max(bottom, y);
        float end = start == bottom ? Math.min(top, y + height) : top;
        return addReturn(returns, count, Math.max(left, x), start, Math.min(right, x + width) - Math.max(left, x), end - start);
    }

    private int addReturn(float[] returns, int count, float x, float y, float width, float height) {
        if (width <= 0f || height <= 0f || count >= returns.length / 4) return count;
        int at = count * 4; returns[at] = x; returns[at + 1] = y; returns[at + 2] = width; returns[at + 3] = height;
        return count + 1;
    }

    private void drawReturns(Batch batch, Rectangle opening, float[] returns, int count) {
        float packed = batch.getPackedColor(); Color parent = batch.getColor();
        float red = parent.r, green = parent.g, blue = parent.b, alpha = parent.a;
        float x = opening.x + 24f, y = opening.y + 24f, width = opening.width - 48f, height = opening.height - 48f;
        try {

            if (kind == Kind.PRISON) for (float bar = x + 40f; bar < x + width - 24f; bar += 64f) {
                batch.setColor(red * 0.065f, green * 0.075f, blue * 0.08f, alpha);
                batch.draw(pixel, bar, y, 6f, height);
                batch.setColor(red * 0.16f, green * 0.18f, blue * 0.18f, alpha);
                batch.draw(pixel, bar, y, 2f, height);
            }
            if (kind == Kind.PRISON) {
                batch.setColor(red * 0.085f, green * 0.095f, blue * 0.10f, alpha);
                batch.draw(pixel, x, y + height * 0.65f, width, 5f);
            }

            for (int i = 0; i < count; i++) {
                int at = i * 4; float rx = returns[at], ry = returns[at + 1], rw = returns[at + 2], rh = returns[at + 3];
                batch.setColor(red * 0.095f, green * 0.115f, blue * 0.12f, alpha);
                if (rx + rw < x + width) batch.draw(pixel, rx + rw, ry, Math.min(24f, x + width - rx - rw), rh);
                if (ry + rh < y + height) batch.draw(pixel, rx, ry + rh, rw, Math.min(8f, y + height - ry - rh));
                batch.setColor(red * 0.05f, green * 0.065f, blue * 0.07f, alpha);
                if (rx > x) batch.draw(pixel, rx - Math.min(16f, rx - x), ry, Math.min(16f, rx - x), rh);
                if (ry > y) batch.draw(pixel, rx, ry - Math.min(24f, ry - y), rw, Math.min(24f, ry - y));
            }

            if (kind == Kind.CAVES) batch.setColor(red, green, blue, alpha);
            else if (kind == Kind.CITY) batch.setColor(red * 0.2555f, green * 0.2625f, blue * 0.28f, alpha);
            else if (kind == Kind.HALLS) batch.setColor(red * 0.2625f, green * 0.2765f, blue * 0.294f, alpha);
            else batch.setColor(red * 0.273f, green * 0.287f, blue * 0.3045f, alpha);
            for (int i = 0; i < count; i++) wallPatch(batch, returns, i * 4);
            if (kind == Kind.CITY) {
                drawCityStonework(batch, x, y, width, height, red, green, blue, alpha);
                return;
            }
            if (kind == Kind.HALLS) {
                drawHallsStonework(batch, x, y, width, height, red, green, blue, alpha);
                return;
            }
            if (kind == Kind.CAVES) {

                batch.setColor(red * 0.24f, green * 0.29f, blue * 0.28f, alpha);
                batch.draw(lintel, x + 16f, y + 8f, Math.min(TILE, width * 0.2f), 3f * PIXEL);
                batch.draw(lintel, x + width - Math.min(TILE, width * 0.2f) - 16f, y + height - 4f * PIXEL,
                        Math.min(TILE, width * 0.2f), 3f * PIXEL);
                float intensity = GameSettingsHelper.getInstance().getVisualEffectIntensity();
                batch.setColor(red * 0.22f, green * 0.30f, blue * 0.29f, alpha * intensity);
                batch.draw(pixel, x + 24f, y + 20f, 2f * PIXEL, PIXEL);
                batch.draw(pixel, x + width - 48f, y + height - 24f, PIXEL, PIXEL);
                return;
            }

            batch.setColor(red * 0.30f, green * 0.33f, blue * 0.34f, alpha);
            for (float row = y; row < y + height; row += TILE) {
                float part = Math.min(TILE, y + height - row);
                batch.draw(pier, x - 3f * PIXEL, row, 3f * PIXEL, part);
                batch.draw(pier, x + width, row, 3f * PIXEL, part);
            }
            for (float column = x; column < x + width; column += TILE)
                batch.draw(lintel, column, y + height, Math.min(TILE, x + width - column), 3f * PIXEL);
        } finally { batch.setPackedColor(packed); }
    }

    private void drawCityStonework(Batch batch, float x, float y, float width, float height,
                                   float red, float green, float blue, float alpha) {

        int columns = width > 8f * TILE ? 3 : 2;
        for (int i = 0; i < columns; i++) {
            float column = i == 0 ? x + 6f * PIXEL : i == 1 ? x + width - 9f * PIXEL
                    : x + Math.round(width * 0.50f / PIXEL) * PIXEL + PIXEL;
            batch.setColor(red * 0.33f, green * 0.34f, blue * 0.33f, alpha);
            for (float row = y; row < y + height; row += TILE) {
                float part = Math.min(TILE, y + height - row);
                batch.draw(pier.getTexture(), column, row, 3f * PIXEL, part, pier.getU(), pier.getV2(), pier.getU2(),
                        pier.getV2() - (pier.getV2() - pier.getV()) * part / TILE);
            }
            batch.setColor(red * 0.15f, green * 0.17f, blue * 0.17f, alpha);
            batch.draw(pixel, column + 2f * PIXEL, y + 2f * PIXEL, PIXEL, height - 4f * PIXEL);
            batch.draw(pixel, column - PIXEL, y, 5f * PIXEL, 2f * PIXEL);
            batch.draw(pixel, column - PIXEL, y + height - 3f * PIXEL, 5f * PIXEL, 2f * PIXEL);
            batch.setColor(red * 0.30f, green * 0.27f, blue * 0.19f, alpha);
            batch.draw(pixel, column + PIXEL, y + height - 4f * PIXEL, PIXEL, PIXEL);
        }
        batch.setColor(red * 0.31f, green * 0.32f, blue * 0.32f, alpha);
        for (float column = x; column < x + width; column += TILE) {
            float part = Math.min(TILE, x + width - column);
            batch.draw(lintel.getTexture(), column, y + height - 3f * PIXEL, part, 3f * PIXEL,
                    lintel.getU(), lintel.getV2(), lintel.getU() + (lintel.getU2() - lintel.getU()) * part / TILE, lintel.getV());
        }
        batch.setColor(red * 0.075f, green * 0.09f, blue * 0.095f, alpha);
        batch.draw(pixel, x, y + height - 6f * PIXEL, width, 3f * PIXEL);
    }

    private void drawHallsStonework(Batch batch, float x, float y, float width, float height,
                                    float red, float green, float blue, float alpha) {

        batch.setColor(red * 0.25f, green * 0.26f, blue * 0.29f, alpha);
        for (int side = 0; side < 2; side++) {
            float column = side == 0 ? x + 5f * PIXEL : x + width - 9f * PIXEL;
            for (float row = y; row < y + height; row += TILE) {
                float part = Math.min(TILE, y + height - row);
                batch.draw(pier.getTexture(), column, row, 4f * PIXEL, part, pier.getU(), pier.getV2(), pier.getU2(),
                        pier.getV2() - (pier.getV2() - pier.getV()) * part / TILE);
            }
            batch.draw(lintel, side == 0 ? x + 4f * PIXEL : x + width - TILE - 4f * PIXEL,
                    y + height - 3f * PIXEL, TILE, 3f * PIXEL);
        }
        batch.setColor(red * 0.075f, green * 0.085f, blue * 0.11f, alpha);
        batch.draw(pixel, x + 8f * PIXEL, y + 2f * PIXEL, PIXEL, height - 4f * PIXEL);
        batch.draw(pixel, x + width - 6f * PIXEL, y + 2f * PIXEL, PIXEL, height - 4f * PIXEL);
        float intensity = GameSettingsHelper.getInstance().getVisualEffectIntensity();
        batch.setColor(red * 0.28f, green * 0.27f, blue * 0.29f, alpha * intensity * 0.35f);
        for (int ash = 0; ash < 3; ash++) batch.draw(pixel, x + (5f + ash * 2f) * PIXEL,
                y + (1f + ash % 2) * PIXEL, PIXEL, PIXEL);
    }

    private void wallPatch(Batch batch, float[] returns, int at) {
        float x = returns[at], y = returns[at + 1], right = x + returns[at + 2], top = y + returns[at + 3];
        for (float row = (float) Math.floor(y / TILE) * TILE; row < top; row += TILE)
            for (float column = (float) Math.floor(x / TILE) * TILE; column < right; column += TILE) {
                float u = returnMasonry.getU(), v = returnMasonry.getV(), u2 = returnMasonry.getU2(), v2 = returnMasonry.getV2();
                if (kind == Kind.CAVES) {

                    long hash = RandomHelper.getInstance().levelSeed(sourceDepth) ^ sourceId.hashCode()
                            ^ (int) (column / TILE) * 0x9E3779B97F4A7C15L ^ (int) (row / TILE) * 0xC2B2AE3D27D4EB4FL ^ 0x4341564557414C4CL;
                    hash ^= hash >>> 33;
                    int choice = (int) (hash & 7);
                    if (choice == 6) { float swap = u; u = u2; u2 = swap; }
                    if (choice == 7) { float swap = v; v = v2; v2 = swap; }
                }
                float du = u2 - u, dv = v2 - v;
                float left = Math.max(x, column), bottom = Math.max(y, row);
                float width = Math.min(right, column + TILE) - left, height = Math.min(top, row + TILE) - bottom;
                batch.draw(returnMasonry.getTexture(), left, bottom, width, height,
                        u + (left - column) / TILE * du, v2 - (bottom - row) / TILE * dv,
                        u + (left + width - column) / TILE * du, v2 - (bottom + height - row) / TILE * dv);
            }
    }
}
