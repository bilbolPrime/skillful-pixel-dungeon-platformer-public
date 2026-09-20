package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.TextureHelper;


public final class RoomBackdrop {
    private static final float PRIMARY_SCALE = 0.58f;
    private static final float SECONDARY_SCALE = 0.30f;
    private static final float PRIMARY_PARALLAX_LIMIT = 16f, SECONDARY_PARALLAX_LIMIT = 6f;
    private static final float SECTION_MARGIN = ConstantsHelper.TILE / 4f;
    private static final float ART_PIXEL = ConstantsHelper.TILE / 16f;
    private static final int DOOR_ART = 0, DOOR_SIGN_ART = 1, FOCAL_ART = 2, PIPE_ART = 5, ITEM_ART = 32;
    private final TextureRegion pixel;
    private final TextureRegion masonry;
    private final TextureRegion face, underface, pier;
    private final Rectangle aperture = new Rectangle(), scissors = new Rectangle();
    private final Rectangle sourceView = new Rectangle();
    private final Matrix4 previousTransform = new Matrix4(), displayTransform = new Matrix4();
    private final RoomBackdropBudget singlePlaneBudget = new RoomBackdropBudget();
    private final RoomBackdropAmbience ambience;
    private final RoomPiers roomPiers;
    private final SpritePose[] propPoses = new SpritePose[RoomBackdropBudget.MAX_ITEM_PROPS];
    private final String[] propIdentifiers = new String[RoomBackdropBudget.MAX_ITEM_PROPS];
    private final float[] propAreas = new float[RoomBackdropBudget.MAX_ITEM_PROPS], featherVertices = new float[20];
    private final int[] propKinds = new int[RoomBackdropBudget.MAX_ITEM_PROPS];
    private final boolean[] propPriority = new boolean[RoomBackdropBudget.MAX_ITEM_PROPS];
    private int propCount;

    public RoomBackdrop(TextureRegion masonry) {
        this(masonry, new TextureRegion(TextureHelper.GetSingleton().getSolidPixel()));
    }

    public RoomBackdrop(TextureRegion masonry, TextureRegion pixel) {
        this.pixel = new TextureRegion(pixel);
        ambience = new RoomBackdropAmbience(this.pixel);
        this.masonry = new TextureRegion(masonry);
        roomPiers = new RoomPiers(this.masonry);
        face = new TextureRegion(masonry, 0, 0, 16, 2);
        underface = new TextureRegion(masonry, 0, 4, 16, 2);
        pier = new TextureRegion(masonry, 0, 0, 3, 16);
    }

    public void draw(Batch batch, RoomSnapshot snapshot, float x, float y, float width, float height, float parallax) {
        singlePlaneBudget.reset();
        drawPlane(batch, snapshot, x, y, width, height, parallax, false, singlePlaneBudget, Float.NaN, Float.NaN);
    }

    public void drawSecondary(Batch batch, RoomSnapshot snapshot, float x, float y, float width, float height, float parallax) {
        singlePlaneBudget.reset();
        drawPlane(batch, snapshot, x, y, width, height, parallax, true, singlePlaneBudget, Float.NaN, Float.NaN);
    }

    public void drawWithBudget(Batch batch, RoomSnapshot snapshot, float x, float y, float width, float height,
                               float parallax, boolean secondary, RoomBackdropBudget budget) {
        drawPlane(batch, snapshot, x, y, width, height, parallax, secondary, budget, Float.NaN, Float.NaN);
    }


    public void drawConnectedSecondary(Batch batch, RoomSnapshot snapshot, float x, float y, float width, float height,
                                       float parallax, RoomBackdropBudget budget, float anchorX, float anchorY) {
        drawPlane(batch, snapshot, x, y, width, height, parallax, true, budget, anchorX, anchorY);
    }

    private void drawPlane(Batch batch, RoomSnapshot snapshot, float x, float y, float width, float height,
                           float parallax, boolean secondary, RoomBackdropBudget budget, float anchorX, float anchorY) {
        if (!GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()
                || snapshot == null || snapshot.widthTiles <= 0f || snapshot.heightTiles <= ConstantsHelper.MIN_FLOOR
                || !Float.isFinite(snapshot.widthTiles * ConstantsHelper.TILE) || !Float.isFinite(snapshot.heightTiles * ConstantsHelper.TILE)
                || width <= 0f || height <= 0f || !Float.isFinite(width) || !Float.isFinite(height)
                || !Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(x + width) || !Float.isFinite(y + height)) return;
        float packed = batch.getPackedColor();
        Color color = batch.getColor();
        float red = color.r, green = color.g, blue = color.b, alpha = color.a;
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
        int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
        previousTransform.set(batch.getTransformMatrix());
        aperture.set(x, y, width, height);
        ScissorStack.calculateScissors(GameHelper.GetSingleton().getCamera(), previousTransform, aperture, scissors);
        batch.flush();
        if (!ScissorStack.pushScissors(scissors)) return;
        try {
            float depth = secondary ? com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper.getInstance()
                    .getRoomTransition().getSecondaryDepth(snapshot.roomIdentifier) : 0f;

            batch.setColor(red * atDepth(0.11f, 0.10f, depth), green * atDepth(0.15f, 0.13f, depth),
                    blue * atDepth(0.16f, 0.14f, depth), alpha);
            batch.draw(pixel, x, y, width, height);
            float roomWidth = snapshot.widthTiles * ConstantsHelper.TILE;
            float roomFloor = ConstantsHelper.MIN_FLOOR * ConstantsHelper.TILE;
            float roomHeight = snapshot.heightTiles * ConstantsHelper.TILE - roomFloor;


            float scale = atDepth(PRIMARY_SCALE, SECONDARY_SCALE, depth);
            if (!Float.isFinite(anchorX) || !Float.isFinite(anchorY)) { anchorX = snapshot.departureX; anchorY = snapshot.departureY; }
            float anchorFraction = anchorX > roomWidth / 2f ? 0.60f : 0.40f;
            float parallaxLimit = secondary ? SECONDARY_PARALLAX_LIMIT : PRIMARY_PARALLAX_LIMIT;
            float offset = GameSettingsHelper.getInstance().isReducedCameraMotion() || !Float.isFinite(parallax) ? 0f
                    : Math.max(-parallaxLimit, Math.min(parallax, parallaxLimit));

            float baseOriginX = cropOrigin(x + width * anchorFraction - anchorX * scale,
                    x, width, 0f, roomWidth, scale, SECTION_MARGIN - parallaxLimit);
            if (snapshot.sourceKind.special) baseOriginX = includeObservedFocal(snapshot, baseOriginX,
                    x, width, roomWidth, scale, anchorX, anchorY, parallaxLimit);
            float originX = cropOrigin(baseOriginX + offset, x, width, 0f, roomWidth, scale);
            float originY = cropOrigin(y + height * 0.30f - anchorY * scale,
                    y, height, roomFloor - ConstantsHelper.TILE, roomFloor + roomHeight, scale);
            if (!secondary) originY = includeObservedSupport(snapshot, x, y, width, height, baseOriginX, originY, scale, anchorX, anchorY);
            displayTransform.set(previousTransform).translate(originX, originY, 0f).scale(scale, scale, 1f);
            batch.setTransformMatrix(displayTransform);
            sourceView.set((x - originX) / scale, (y - originY) / scale, width / scale, height / scale);
            drawInterior(batch, snapshot, roomWidth, roomFloor + roomHeight, depth, red, green, blue, alpha);
            batch.setColor(red * atDepth(.75f, .50f, depth), green * atDepth(.75f, .50f, depth),
                    blue * atDepth(.75f, .50f, depth), alpha);
            roomPiers.draw(batch, snapshot.layout.parts, snapshot.layout.family);

            float materialRed = red * atDepth(1f, 0.78f, depth);
            float materialGreen = green * atDepth(1f, 0.70f, depth);
            float materialBlue = blue * atDepth(1f, 0.68f, depth);
            float pixelsPerSourceUnit = scale * com.badlogic.gdx.Gdx.graphics.getHeight()
                    / (GameHelper.GetSingleton().getCamera().viewportHeight * GameHelper.GetSingleton().getCamera().zoom);
            collectProps(snapshot, pixelsPerSourceUnit, secondary, budget);
            drawProps(batch, true, depth, red, green, blue, alpha);

            for (int layer = 0; layer < 3; layer++) {
                drawRoomVolume(batch, layer, roomWidth, roomFloor, roomHeight, materialRed, materialGreen, materialBlue, alpha);
                for (int i = 0; i < snapshot.platforms.size(); i++) {
                    RoomSnapshot.Platform platform = snapshot.platforms.get(i);
                    if (visible(platform.left(), platform.top() - 9f * ART_PIXEL, platform.width(), 9f * ART_PIXEL))
                        drawLedge(batch, layer, platform.left(), platform.top(), platform.width(), platform.wet, snapshot.waterSurfaceThickness,
                                materialRed, materialGreen, materialBlue, alpha);
                }
                for (int i = 0; i < snapshot.doors.size(); i++) {
                    RoomSnapshot.DoorShape door = snapshot.doors.get(i);
                    if (visible(door.x - 3f * ART_PIXEL, door.y, door.width + 6f * ART_PIXEL, door.height + 3f * ART_PIXEL))
                        drawDoorVolume(batch, layer, door, materialRed, materialGreen, materialBlue, alpha);
                }
            }
            batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ZERO, GL20.GL_ONE);
            ambience.draw(batch, snapshot, propPoses, propCount, sourceView, anchorX, anchorY,
                    com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper.getInstance().getBackgroundVisualTime(), secondary, depth,
                    GameSettingsHelper.getInstance().isReducedVisualEffects(), pixelsPerSourceUnit, red, green, blue, alpha, budget);
            drawProps(batch, false, depth, red, green, blue, alpha);


            batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ZERO, GL20.GL_ONE);
            batch.setColor(red * atDepth(0.15f, 0.13f, depth), green * atDepth(0.21f, 0.15f, depth),
                    blue * atDepth(0.23f, 0.16f, depth), alpha * atDepth(0.88f, 0.70f, depth));
            for (int i = 0; i < snapshot.enemies.size(); i++) {
                RoomSnapshot.Occupant occupant = snapshot.enemies.get(i);
                SpritePose pose = occupant.pose;
                if (pose.intersects(sourceView) && Math.abs(pose.height * pose.scaleY) * pixelsPerSourceUnit >= (secondary ? 12f : 18f)
                        && budget.takeOccupant()) {
                    float heightScale = idleHeight(occupant, secondary, budget);
                    pose.drawAnchored(batch, heightScale);
                }
            }
            batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
            batch.setTransformMatrix(previousTransform);
        } finally {
            clearProps();
            try { batch.flush(); }
            finally {
                ScissorStack.popScissors();
                batch.setTransformMatrix(previousTransform);
                batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
                batch.setPackedColor(packed);
            }
        }
    }

    private float idleHeight(RoomSnapshot.Occupant occupant, boolean secondary, RoomBackdropBudget budget) {
        SpritePose pose = occupant.pose;
        if (!occupant.idle || occupant.floating || !Float.isFinite(occupant.supportY) || pose.rotation != 0f
                || pose.scaleY <= 0f || !budget.takeAnimation()) return 1f;
        int seed = occupant.identifier.hashCode();
        double time = com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper.getInstance().getBackgroundVisualTime();
        double period = 3.4d + Math.floorMod(seed, 900) / 1000d;
        double phase = time / period + Math.floorMod(seed, 997) / 997d;
        float amplitude = GameSettingsHelper.getInstance().isReducedVisualEffects() ? 0.006f : 0.018f;
        if (secondary) amplitude *= 0.67f;
        return 1f - amplitude * (0.5f - 0.5f * (float) Math.cos(phase * Math.PI * 2d));
    }

    private float includeObservedFocal(RoomSnapshot snapshot, float origin, float x, float width, float roomWidth,
                                       float scale, float anchorX, float anchorY, float parallaxLimit) {
        SpritePose focal = observedFocal(snapshot);
        if (focal == null) return origin;
        float doorX = anchorX - ConstantsHelper.TILE / 2f, doorWidth = ConstantsHelper.TILE;
        for (int i = 0; i < snapshot.doors.size(); i++) {
            RoomSnapshot.DoorShape door = snapshot.doors.get(i);
            if (door.anchorX() == anchorX && door.y == anchorY) { doorX = door.x; doorWidth = door.width; break; }
        }
        float inset = Math.min(24f * ART_PIXEL, width * 0.22f);
        float focalMinimum = x + inset - focal.x * scale;
        float focalMaximum = x + width - inset - (focal.x + focal.width) * scale;
        float minimum = Math.max(x + SECTION_MARGIN - doorX * scale, focalMinimum);
        float maximum = Math.min(x + width - SECTION_MARGIN - (doorX + doorWidth) * scale, focalMaximum);


        if (minimum > maximum) { minimum = focalMinimum; maximum = focalMaximum; }
        if (minimum > maximum) return origin;
        float preferred = x + width * 0.60f - (focal.x + focal.width / 2f) * scale;
        return cropOrigin(Math.max(minimum, Math.min(preferred, maximum)), x, width, 0f, roomWidth, scale, SECTION_MARGIN - parallaxLimit);
    }

    private SpritePose observedFocal(RoomSnapshot snapshot) {
        SpritePose focal = null;
        for (int i = 0; i < snapshot.props.size(); i++) {
            RoomSnapshot.Prop prop = snapshot.props.get(i);
            if (prop.kind == RoomSnapshot.PropKind.FOCAL && prop.pose != null && prop.pose.layerCount() > 0
                    && (focal == null || prop.pose.displayedArea() > focal.displayedArea())) focal = prop.pose;
        }
        return focal;
    }

    private float includeObservedSupport(RoomSnapshot snapshot, float x, float y, float width, float height,
                                         float originX, float originY, float scale, float anchorX, float anchorY) {
        float left = (x - originX) / scale, right = (x + width - originX) / scale;
        float support = lowestSupport(snapshot.enemies, left, right, Float.POSITIVE_INFINITY);
        support = lowestSupport(snapshot.items, left, right, support);
        if (!Float.isFinite(support)) return originY;
        float doorwayTop = anchorY + ConstantsHelper.TILE;
        for (int i = 0; i < snapshot.doors.size(); i++) {
            RoomSnapshot.DoorShape door = snapshot.doors.get(i);
            if (door.anchorX() == anchorX && door.y == anchorY) { doorwayTop = door.y + door.height; break; }
        }


        float candidate = Math.min(y + height * 0.12f - support * scale, y + height - SECTION_MARGIN - doorwayTop * scale);
        return candidate > originY && candidate + support * scale >= y + 3f * ART_PIXEL ? candidate : originY;
    }

    private float lowestSupport(java.util.List<RoomSnapshot.Occupant> occupants, float left, float right, float support) {
        for (int i = 0; i < occupants.size(); i++) {
            RoomSnapshot.Occupant occupant = occupants.get(i);
            if (!occupant.floating && Float.isFinite(occupant.supportY) && occupant.footX >= left && occupant.footX <= right
                    && occupant.pose.displayedArea() >= ConstantsHelper.UNIT_DIMENSIONS * ConstantsHelper.UNIT_DIMENSIONS / 4f)
                support = Math.min(support, occupant.supportY);
        }
        return support;
    }

    private void collectProps(RoomSnapshot snapshot, float pixelsPerSourceUnit, boolean secondary, RoomBackdropBudget budget) {
        float minimumPixels = secondary ? 12f : 8f;
        for (int i = 0; i < snapshot.doors.size(); i++) {
            RoomSnapshot.DoorShape door = snapshot.doors.get(i);
            considerProp(door.pose, door.identifier, DOOR_ART, door.identifier.equals(snapshot.departureDoorIdentifier), pixelsPerSourceUnit, minimumPixels);
            considerProp(door.signPose, door.identifier, DOOR_SIGN_ART, false, pixelsPerSourceUnit, minimumPixels);
        }
        for (int i = 0; i < snapshot.props.size(); i++) {
            RoomSnapshot.Prop prop = snapshot.props.get(i);
            considerProp(prop.pose, prop.identifier, FOCAL_ART + prop.kind.ordinal(), false, pixelsPerSourceUnit, minimumPixels);
        }
        for (int i = 0; i < snapshot.items.size(); i++) {
            RoomSnapshot.Occupant item = snapshot.items.get(i);
            considerProp(item.pose, item.identifier, ITEM_ART, false, pixelsPerSourceUnit, minimumPixels);
        }
        int admitted = 0;
        while (admitted < propCount && budget.takeItemProp()) admitted++;
        for (int i = admitted; i < propCount; i++) { propPoses[i] = null; propIdentifiers[i] = null; }
        propCount = admitted;
    }

    private void considerProp(SpritePose pose, String identifier, int kind, boolean priority, float pixelsPerUnit, float minimumPixels) {
        if (pose == null || !pose.intersects(sourceView)
                || Math.min(Math.abs(pose.width * pose.scaleX), Math.abs(pose.height * pose.scaleY)) * pixelsPerUnit < minimumPixels) return;
        float area = pose.displayedArea();
        int position = 0;
        for (; position < propCount; position++) {
            if (identifier.equals(propIdentifiers[position]) && kind == propKinds[position]) return;
            if (priority != propPriority[position]) { if (priority) break; }
            else if (area != propAreas[position]) { if (area > propAreas[position]) break; }
            else {
                int order = identifier.compareTo(propIdentifiers[position]);
                if (order < 0 || order == 0 && kind < propKinds[position]) break;
            }
        }
        if (position >= propPoses.length) return;
        for (int i = Math.min(propCount, propPoses.length - 1); i > position; i--) {
            propPoses[i] = propPoses[i - 1]; propIdentifiers[i] = propIdentifiers[i - 1]; propKinds[i] = propKinds[i - 1];
            propAreas[i] = propAreas[i - 1]; propPriority[i] = propPriority[i - 1];
        }
        propPoses[position] = pose; propIdentifiers[position] = identifier; propKinds[position] = kind;
        propAreas[position] = area; propPriority[position] = priority;
        propCount = Math.min(propCount + 1, propPoses.length);
    }

    private void drawProps(Batch batch, boolean behindLedges, float depth, float red, float green, float blue, float alpha) {
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ZERO, GL20.GL_ONE);
        batch.setColor(red * atDepth(0.40f, 0.24f, depth), green * atDepth(0.46f, 0.27f, depth),
                blue * atDepth(0.46f, 0.28f, depth), alpha * atDepth(0.88f, 0.70f, depth));
        for (int i = 0; i < propCount; i++) {
            int kind = propKinds[i];

            boolean fixture = kind >= FOCAL_ART + RoomSnapshot.PropKind.LAMP.ordinal()
                    && kind <= FOCAL_ART + RoomSnapshot.PropKind.HALLS_RED.ordinal();
            if (behindLedges != (kind == FOCAL_ART || kind == PIPE_ART || fixture)) continue;
            if (kind == DOOR_ART || kind == PIPE_ART) propPoses[i].drawFeatheredEdges(batch, ConstantsHelper.TILE * (kind == DOOR_ART ? 0.1875f : 0.25f), featherVertices);
            else propPoses[i].draw(batch);
        }
    }

    private void clearProps() {
        for (int i = 0; i < propCount; i++) { propPoses[i] = null; propIdentifiers[i] = null; }
        propCount = 0;
    }

    private void drawInterior(Batch batch, RoomSnapshot snapshot, float roomWidth, float ceiling, float depth,
                               float red, float green, float blue, float alpha) {


        float tile = ConstantsHelper.TILE;
        float startX = Math.max(0f, (float) Math.floor(sourceView.x / tile) * tile);

        float startY = Math.max(0f, (float) Math.floor(sourceView.y / tile) * tile);
        for (float row = startY; row < Math.min(ceiling, sourceView.y + sourceView.height); row += tile) {
            for (float column = startX; column < Math.min(roomWidth, sourceView.x + sourceView.width); column += tile) {
                int hash = snapshot.roomIdentifier.hashCode() ^ ((int) (column / tile) * 73856093) ^ ((int) (row / tile) * 19349663);
                float variant = 0.96f + ((hash & 0x7fffffff) % 3) * 0.04f;
                batch.setColor(red * atDepth(0.34f, 0.28f, depth) * variant,
                        green * atDepth(0.40f, 0.30f, depth) * variant,
                        blue * atDepth(0.40f, 0.31f, depth) * variant, alpha * atDepth(0.46f, 0.28f, depth));
                batch.draw(masonry, column, row, tile, tile);
            }
        }

        for (int step = 0; step < 3; step++) {
            float inset = (3 - step) * ART_PIXEL * 2f;
            batch.setColor(0f, 0f, 0f, alpha * atDepth(0.08f, 0.05f, depth));
            batch.draw(pixel, 0f, ceiling - inset, roomWidth, inset);
            batch.draw(pixel, roomWidth - inset, ConstantsHelper.MIN_FLOOR * tile, inset,
                    ceiling - ConstantsHelper.MIN_FLOOR * tile);
        }
    }

    private void drawRoomVolume(Batch batch, int layer, float width, float floor, float height,
                                float red, float green, float blue, float alpha) {
        float ceiling = floor + height;

        if (layer == 0) {
            batch.setColor(red * 0.10f, green * 0.15f, blue * 0.16f, alpha);
            batch.draw(pixel, 0f, floor - 6f * ART_PIXEL, width, 6f * ART_PIXEL);
            batch.draw(pixel, 0f, ceiling - 5f * ART_PIXEL, width, 5f * ART_PIXEL);
            batch.draw(pixel, 0f, floor, 5f * ART_PIXEL, height);
            batch.draw(pixel, width - 5f * ART_PIXEL, floor, 5f * ART_PIXEL, height);
            return;
        }
        if (layer != 1) return;
        batch.setColor(red * 0.34f, green * 0.42f, blue * 0.43f, alpha);
        if (sourceView.y < floor && sourceView.y + sourceView.height > floor - 3f * ART_PIXEL)
            band(batch, face, 0f, floor - 3f * ART_PIXEL, width, 3f * ART_PIXEL);
        if (sourceView.y < ceiling && sourceView.y + sourceView.height > ceiling - 3f * ART_PIXEL)
            band(batch, underface, 0f, ceiling - 3f * ART_PIXEL, width, 3f * ART_PIXEL);
        for (float row = Math.max(floor, (float) Math.floor(sourceView.y / ConstantsHelper.TILE) * ConstantsHelper.TILE);
             row < Math.min(ceiling, sourceView.y + sourceView.height); row += ConstantsHelper.TILE) {
            float part = Math.min(ConstantsHelper.TILE, ceiling - row);
            if (sourceView.x < 3f * ART_PIXEL) batch.draw(pier, 0f, row, 3f * ART_PIXEL, part);
            if (sourceView.x + sourceView.width > width - 3f * ART_PIXEL)
                batch.draw(pier, width - 3f * ART_PIXEL, row, 3f * ART_PIXEL, part);
        }
    }

    private void drawLedge(Batch batch, int layer, float left, float top, float width, boolean wet, float waterThickness,
                           float red, float green, float blue, float alpha) {


        if (layer == 0) {
            batch.setColor(red * 0.10f, green * 0.15f, blue * 0.16f, alpha);
            batch.draw(pixel, left + ART_PIXEL, top - 5f * ART_PIXEL, width - ART_PIXEL, 3f * ART_PIXEL);
        } else if (layer == 1) {
            batch.setColor(red * 0.25f, green * 0.32f, blue * 0.33f, alpha);
            band(batch, underface, left + ART_PIXEL, top - 4f * ART_PIXEL, width - ART_PIXEL, ART_PIXEL);
        }
        if (layer < 2) for (float offset = ART_PIXEL * 3f; offset < width - ART_PIXEL * 2f; offset += ConstantsHelper.TILE * 3f) {
            float supportX = left + offset;
            if (supportX + 3f * ART_PIXEL < sourceView.x || supportX > sourceView.x + sourceView.width) continue;
            if (layer == 0) {
                batch.setColor(red * 0.13f, green * 0.19f, blue * 0.20f, alpha);
                batch.draw(pixel, supportX + 2f * ART_PIXEL, top - 6f * ART_PIXEL, ART_PIXEL, 3f * ART_PIXEL);
            } else {
                batch.setColor(red * 0.25f, green * 0.33f, blue * 0.34f, alpha);
                batch.draw(pier, supportX, top - 8f * ART_PIXEL, 2f * ART_PIXEL, 5f * ART_PIXEL);
            }
        }
        if (layer == 0) return;
        if (layer == 1) {
            batch.setColor(red * 0.39f, green * 0.48f, blue * 0.48f, alpha);
            band(batch, face, left, top - 3f * ART_PIXEL, width, 3f * ART_PIXEL);
            return;
        }
        batch.setColor(red * 0.24f, green * (wet ? 0.40f : 0.32f), blue * (wet ? 0.42f : 0.33f), alpha);
        float surface = wet ? waterThickness : ART_PIXEL / 2f;
        batch.draw(pixel, left, top - surface, width, surface);
        if (wet) {
            batch.setColor(red * 0.28f, green * 0.43f, blue * 0.45f, alpha * 0.30f);
            batch.draw(pixel, left + ART_PIXEL, top - 2f * ART_PIXEL, width - 2f * ART_PIXEL, ART_PIXEL / 2f);
        }
        batch.setColor(red * 0.13f, green * 0.19f, blue * 0.20f, alpha);
        batch.draw(pixel, left + width - ART_PIXEL, top - 3f * ART_PIXEL, ART_PIXEL, 2.5f * ART_PIXEL);
    }

    private void drawDoorVolume(Batch batch, int layer, RoomSnapshot.DoorShape door,
                                 float red, float green, float blue, float alpha) {
        if (layer == 0) {
            batch.setColor(red * 0.09f, green * 0.14f, blue * 0.15f, alpha);
            batch.draw(pixel, door.x, door.y, door.width, door.height);
        } else if (layer == 1) {
            batch.setColor(red * 0.35f, green * 0.43f, blue * 0.43f, alpha);
            batch.draw(pier, door.x - 3f * ART_PIXEL, door.y, 3f * ART_PIXEL, door.height);
            batch.draw(pier, door.x + door.width, door.y, 3f * ART_PIXEL, door.height);
            band(batch, face, door.x - 3f * ART_PIXEL, door.y + door.height, door.width + 6f * ART_PIXEL, 3f * ART_PIXEL);
        } else {

            batch.setColor(red * 0.16f, green * 0.23f, blue * 0.24f, alpha);
            batch.draw(pixel, door.x, door.y, ART_PIXEL, door.height);
            batch.draw(pixel, door.x, door.y + door.height - ART_PIXEL, door.width, ART_PIXEL);
        }
    }


    private void band(Batch batch, TextureRegion region, float left, float bottom, float width, float height) {
        float first = Math.max(0f, (float) Math.floor((sourceView.x - left) / ConstantsHelper.TILE)) * ConstantsHelper.TILE;
        float end = Math.min(left + width, sourceView.x + sourceView.width);
        for (float column = left + first; column < end; column += ConstantsHelper.TILE)
            batch.draw(region, column, bottom, Math.min(ConstantsHelper.TILE, left + width - column), height);
    }

    private boolean visible(float x, float y, float width, float height) {
        return x < sourceView.x + sourceView.width && x + width > sourceView.x
                && y < sourceView.y + sourceView.height && y + height > sourceView.y;
    }

    private float cropOrigin(float desired, float start, float extent, float sourceMin, float sourceMax, float scale) {
        return cropOrigin(desired, start, extent, sourceMin, sourceMax, scale, SECTION_MARGIN);
    }

    private static float atDepth(float near, float far, float depth) {
        return depth <= 0f ? near : depth >= 1f ? far : near + (far - near) * depth;
    }

    private float cropOrigin(float desired, float start, float extent, float sourceMin, float sourceMax, float scale, float margin) {
        if ((sourceMax - sourceMin) * scale < extent - 2f * margin)
            return start + (extent - (sourceMax - sourceMin) * scale) / 2f - sourceMin * scale;

        float minimum = start + extent - sourceMax * scale - margin;
        float maximum = start - sourceMin * scale + margin;
        return Math.max(minimum, Math.min(desired, maximum));
    }
}
