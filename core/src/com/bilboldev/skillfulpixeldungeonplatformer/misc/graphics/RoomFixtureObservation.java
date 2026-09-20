package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;

import java.util.ArrayList;
import java.util.List;


public final class RoomFixtureObservation {
    private final RoomSnapshot.Prop[] fixtures = new RoomSnapshot.Prop[RoomSnapshot.MAX_PROPS];
    private final SpritePose[] focalParts = new SpritePose[RoomSnapshot.MAX_FOCAL_PARTS];
    private TextureRegion focalBounds;
    private float focalX, focalY, focalWidth, focalHeight, rejectedFocalX = Float.NaN, rejectedFocalY = Float.NaN;
    private int focalPartCount;
    private boolean collectingFocal, focalOverflow;
    private RoomSnapshot.PropKind collectionKind = RoomSnapshot.PropKind.FOCAL;
    private float collectionAnchorX, collectionAnchorY;
    private String roomIdentifier;
    private int count;

    public void begin(String roomIdentifier) {
        if (!GameSettingsHelper.getInstance().isBackgroundRoomsEnabled()) { clear(); return; }
        if (roomIdentifier == null || !roomIdentifier.equals(this.roomIdentifier)) {
            clear();
            this.roomIdentifier = roomIdentifier;
        }
    }

    public void pipe(GameSprite art, float mouthX, float mouthY, float impactY) {
        if (!enabled() || !visible(art.getX(), art.getY(), art.getWidth(), art.getHeight())) return;
        int slot = reserve(RoomSnapshot.PropKind.PIPE, mouthX, mouthY, art.getWidth() * art.getHeight());
        if (slot >= 0) fixtures[slot] = new RoomSnapshot.Prop(identifier(RoomSnapshot.PropKind.PIPE, mouthX, mouthY),
                RoomSnapshot.PropKind.PIPE, art.copyPose(), mouthX, mouthY, impactY);
    }

    public void lamp(float x, float y) {
        light(x, y, RoomSnapshot.PropKind.LAMP);
    }

    public void torch(float x, float y) {
        light(x, y, RoomSnapshot.PropKind.TORCH);
    }

    public void cityLamp(float x, float y) {
        light(x, y, RoomSnapshot.PropKind.CITY_LAMP);
    }

    public void hallsLamp(float x, float y, boolean green) {
        light(x, y, green ? RoomSnapshot.PropKind.HALLS_GREEN : RoomSnapshot.PropKind.HALLS_RED);
    }

    private void light(float x, float y, RoomSnapshot.PropKind kind) {
        if (!enabled()) return;
        boolean torch = kind == RoomSnapshot.PropKind.TORCH;
        boolean city = kind == RoomSnapshot.PropKind.CITY_LAMP;
        boolean halls = kind == RoomSnapshot.PropKind.HALLS_GREEN || kind == RoomSnapshot.PropKind.HALLS_RED;
        float width = halls ? 24f : city || torch ? 16f : 32f, height = city || halls ? 48f : torch ? 56f : 32f;
        if (!enabled() || !visible(x - (halls ? 12f : city ? 4f : torch ? 8f : 16f),
                y - (halls ? 24f : city ? 28f : torch ? 40f : 16f), width, height)) return;
        int slot = reserve(kind, x, y, width * height);
        if (slot >= 0) fixtures[slot] = new RoomSnapshot.Prop(identifier(kind, x, y), kind, null, x, y, Float.NaN);
    }

    public void focal(TextureRegion region, float x, float y, float width, float height, Color tint) {
        if (!enabled() || !visible(x, y, width, height)) return;
        float anchorX = x + width / 2f;
        int slot = reserve(RoomSnapshot.PropKind.FOCAL, anchorX, y, width * height);
        if (slot >= 0) {
            SpritePose pose = new SpritePose(region, x, y, 0f, 0f, width, height, 1f, 1f, 0f,
                    tint.r, tint.g, tint.b, tint.a);
            fixtures[slot] = new RoomSnapshot.Prop(identifier(RoomSnapshot.PropKind.FOCAL, anchorX, y),
                    RoomSnapshot.PropKind.FOCAL, pose, anchorX, y, Float.NaN);
        }
    }


    public void beginFocal(TextureRegion boundsRegion, float x, float y, float width, float height) {
        beginCollection(RoomSnapshot.PropKind.FOCAL, boundsRegion, x, y, width, height, x + width/2f, y);
    }

    public void beginLight(RoomSnapshot.PropKind kind, TextureRegion boundsRegion, float x, float y, float width, float height,
                           float anchorX, float anchorY) {
        endFocal(false);

        if (!enabled()) return;
        beginCollection(kind, boundsRegion, x, y, width, height, anchorX, anchorY);
    }

    private void beginCollection(RoomSnapshot.PropKind kind, TextureRegion boundsRegion, float x, float y, float width, float height,
                                 float anchorX, float anchorY) {
        endFocal(false);
        if (!enabled() || !Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(width) || !Float.isFinite(height)
                || width <= 0f || height <= 0f || x == rejectedFocalX && y == rejectedFocalY) return;
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        if (camera == null || x < camera.position.x - camera.viewportWidth * camera.zoom / 2f
                || x + width > camera.position.x + camera.viewportWidth * camera.zoom / 2f
                || y < camera.position.y - camera.viewportHeight * camera.zoom / 2f
                || y + height > camera.position.y + camera.viewportHeight * camera.zoom / 2f) return;
        for (int i = 0; i < count; i++) if (fixtures[i].kind == kind
                && fixtures[i].anchorX == anchorX && fixtures[i].anchorY == anchorY
                && fixtures[i].pose != null && fixtures[i].pose.layerCount() > 0) return;
        focalBounds = boundsRegion; focalX = x; focalY = y; focalWidth = width; focalHeight = height;
        collectionKind = kind; collectionAnchorX = anchorX; collectionAnchorY = anchorY;
        collectingFocal = true;
    }


    public void focalPiece(TextureRegion region, float x, float y, float width, float height, Color tint) {
        focalPiece(region, x, y, width, height, tint.r, tint.g, tint.b, tint.a);
    }
    public void focalPiece(TextureRegion region, float x, float y, float width, float height, float red, float green, float blue, float alpha) {
        if (!collectingFocal || focalOverflow) return;
        if (focalPartCount == focalParts.length) {
            focalOverflow = true;
            rejectedFocalX = focalX; rejectedFocalY = focalY;
            return;
        }
        focalParts[focalPartCount++] = new SpritePose(region, x, y, 0f, 0f, width, height, 1f, 1f, 0f,
                red, green, blue, alpha);
    }


    public void endFocal(boolean completed) {
        try {
            if (collectingFocal && completed && !focalOverflow && focalPartCount > 0) {
                int slot = -1;
                for (int i = 0; i < count; i++) if (fixtures[i].kind == collectionKind
                        && fixtures[i].anchorX == collectionAnchorX && fixtures[i].anchorY == collectionAnchorY) { slot = i; break; }
                if (slot < 0) slot = reserve(collectionKind, collectionAnchorX, collectionAnchorY, focalWidth * focalHeight);
                if (slot >= 0) fixtures[slot] = new RoomSnapshot.Prop(identifier(collectionKind, collectionAnchorX, collectionAnchorY),
                        collectionKind, SpritePose.composite(focalBounds, focalX, focalY, focalWidth, focalHeight,
                        focalParts, focalPartCount), collectionAnchorX, collectionAnchorY, Float.NaN);
            }
        } finally {
            for (int i = 0; i < focalPartCount; i++) focalParts[i] = null;
            focalBounds = null; focalPartCount = 0; collectingFocal = focalOverflow = false;
        }
    }

    private boolean enabled() { return roomIdentifier != null && GameSettingsHelper.getInstance().isBackgroundRoomsEnabled(); }

    private boolean visible(float x, float y, float width, float height) {
        OrthographicCamera camera = GameHelper.GetSingleton().getCamera();
        return camera != null && x + width >= camera.position.x - camera.viewportWidth * camera.zoom / 2f
                && x <= camera.position.x + camera.viewportWidth * camera.zoom / 2f
                && y + height >= camera.position.y - camera.viewportHeight * camera.zoom / 2f
                && y <= camera.position.y + camera.viewportHeight * camera.zoom / 2f;
    }


    private int reserve(RoomSnapshot.PropKind kind, float x, float y, float area) {
        for (int i = 0; i < count; i++)
            if (fixtures[i].kind == kind && fixtures[i].anchorX == x && fixtures[i].anchorY == y) return -1;
        if (count < fixtures.length) return count++;
        int worst = 0;
        for (int i = 1; i < count; i++) {
            RoomSnapshot.Prop current = fixtures[i], previous = fixtures[worst];
            if (better(previous.kind, previous.anchorX, previous.anchorY, previous.area(), current)) worst = i;
        }
        return better(kind, x, y, area, fixtures[worst]) ? worst : -1;
    }

    private boolean better(RoomSnapshot.PropKind kind, float x, float y, float area, RoomSnapshot.Prop other) {
        if (area != other.area()) return area > other.area();
        if (kind != other.kind) return kind.ordinal() < other.kind.ordinal();
        if (y != other.anchorY) return y < other.anchorY;
        return x < other.anchorX;
    }

    private String identifier(RoomSnapshot.PropKind kind, float x, float y) {
        return roomIdentifier + ":fixture:" + kind + ":" + Float.floatToIntBits(x) + ":" + Float.floatToIntBits(y);
    }

    public List<RoomSnapshot.Prop> copyFor(String outgoingRoom) {
        List<RoomSnapshot.Prop> result = new ArrayList<>();
        if (outgoingRoom != null && outgoingRoom.equals(roomIdentifier))
            for (int i = 0; i < count; i++) result.add(fixtures[i]);
        return result;
    }

    public int size() { return count; }

    public void clear() {
        endFocal(false);
        rejectedFocalX = rejectedFocalY = Float.NaN;
        for (int i = 0; i < count; i++) fixtures[i] = null;
        roomIdentifier = null;
        count = 0;
    }
}
